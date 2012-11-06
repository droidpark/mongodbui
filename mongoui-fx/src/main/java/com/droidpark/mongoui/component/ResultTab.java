package com.droidpark.mongoui.component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.bson.types.ObjectId;

import com.droidpark.mongoui.util.ImageUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.control.Tooltip;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.util.Callback;

public class ResultTab extends Tab implements UITab {

	private static Logger logger = Logger.getLogger(ResultTab.class);
	
	private Mongo mongo;
	private String collectionName;
	private String databaseName;
	
	List<String> columns;
	TableView<DBObject> tableView = new TableView<DBObject>();
	ObservableList<DBObject> dataList;
	
	
	AnchorPane tabToolPane;
	AnchorPane tabFooterPane;
	SplitPane horizontalPane;
	AnchorPane tableAncPane;
	AnchorPane columnAncWrapPane;
	AnchorPane columnAncPane;
	TreeView<CheckBox> columnTreePane;
	AnchorPane columnTreeWrapPane;
	TitledPane columnTitledPane;
	BorderPane footerBorder;
	
	int resultSize = 0;
	private Gson gson = null;
	
	public ResultTab(String collection, String database, Mongo mongo) {
		super(database + "." + collection);
		this.collectionName = collection;
		this.databaseName = database;
		this.mongo = mongo;
		initComponent();
	}
	
	private void initComponent() {
		initLayout();
		initDataListAndColumnList();
		initResultTable();
		initColumnViewPane();
		initFooterPane();
		initToolPane();
		
		BorderPane borderPane = new BorderPane();
		borderPane.setTop(tabToolPane);
		borderPane.setCenter(horizontalPane);
		borderPane.setBottom(tabFooterPane);
		setContent(borderPane);
	}
	
	private void initDataListAndColumnList() {
		try {
			dataList = FXCollections.observableArrayList();
			columns = new ArrayList<String>();
			
			GsonBuilder gsonBuilder = new GsonBuilder();
			gson = gsonBuilder.serializeNulls().create();
			DB database = mongo.getDB(databaseName);
			DBCollection collection = database.getCollection(collectionName);
			resultSize = collection.find(new BasicDBObject()).size();
			logger.info("db." + collectionName + ".find().size()");
			DBCursor cursor = collection.find(new BasicDBObject()).limit(20);
			logger.info("db." + collectionName + ".find().limit(20)");
			Set<String> columnsSet = new HashSet<String>();
			while(cursor.hasNext()) {
				DBObject object = cursor.next();
				dataList.add(object);
				for(String column : object.keySet()) {
					columnsSet.add(column);
				}
				
			}
			columns.addAll(columnsSet);
			Collections.sort(columns);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void initResultTable() {
		tableAncPane = new AnchorPane();
		tableAncPane.getChildren().add(tableView);
		
		tableView.setMinSize(0, 0);
		tableView.prefHeightProperty().bind(tableAncPane.heightProperty());
		tableView.prefWidthProperty().bind(tableAncPane.widthProperty());
		
		horizontalPane.getItems().add(tableAncPane);
		
		for(final String columnName : columns) {
			TableColumn<DBObject, String> column = new TableColumn<DBObject, String>();
			column.setText(columnName);
			column.setMinWidth(100);
			
			column.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<DBObject,String>, ObservableValue<String>>() {
				public ObservableValue<String> call(CellDataFeatures<DBObject, String> cell) {
					if(cell.getValue() != null) {
						DBObject obj = cell.getValue();
						JsonElement entity = gson.toJsonTree(obj.get(columnName));
						String value = "";
						if(obj.get(columnName) instanceof ObjectId) {
							value = obj.get(columnName).toString();
						}
						else if(entity.isJsonPrimitive()) {
							value = entity.getAsString();
						}
						else if(entity.isJsonObject()) {
							value = "{" + entity.getAsJsonObject().entrySet().size() + " fields}";
						}
						else if(entity.isJsonArray()) {
							value = "{" + entity.getAsJsonArray().size() + " fields}";
						}
						return new SimpleStringProperty(value);
					}
					return null;
				}
			});
			
			tableView.getColumns().add(column);
			tableView.getSelectionModel().setCellSelectionEnabled(true);
			tableView.setItems(dataList);
		}
	}
	
	private void initLayout() {
		tabToolPane = new AnchorPane();
		tabToolPane.getStyleClass().add("-mongoui-tab-toolpane");
		tabToolPane.setPrefHeight(30);
		
		tabFooterPane = new AnchorPane();
		tabFooterPane.getStyleClass().add("-mongoui-tab-footerpane");
		tabFooterPane.setPrefHeight(30);
		
		horizontalPane = new SplitPane();
		horizontalPane.setOrientation(Orientation.HORIZONTAL);
		horizontalPane.setDividerPosition(0,0.8);
	}
	
	private void initColumnViewPane() {
		TreeItem<CheckBox> rootField = new TreeItem<CheckBox>(new CheckBox(collectionName));
		rootField.setExpanded(true);
		rootField.getValue().setDisable(true);
		rootField.getValue().setSelected(true);
		final TableView<DBObject> table = tableView;
		for(final String column : columns) {
			final TreeItem<CheckBox> check = new TreeItem<CheckBox>();
			check.setValue(new CheckBox());
			check.getValue().setText(column);
			check.getValue().setSelected(true);
			check.getValue().setOnAction(new EventHandler<ActionEvent>() {
				public void handle(ActionEvent arg0) {
					List<String> tempcolumn = new ArrayList<String>();
					for(TableColumn column : table.getColumns()) {
						tempcolumn.add(column.getText());
					}
					int index = tempcolumn.indexOf(column);
					tempcolumn.clear();
					table.getColumns().get(index).setVisible(check.getValue().isSelected());
				}
			});
			rootField.getChildren().add(check);
		}
		
		columnAncWrapPane = new AnchorPane();
		columnAncPane = new AnchorPane();
		columnAncPane.setMinSize(0, 0);
		columnAncPane.setMaxSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);
		columnAncPane.prefHeightProperty().bind(columnAncWrapPane.heightProperty());
		columnAncPane.prefWidthProperty().bind(columnAncWrapPane.widthProperty());
		columnAncWrapPane.getChildren().add(columnAncPane);
		
		columnTreePane = new TreeView<CheckBox>(rootField);
		columnTreeWrapPane = new AnchorPane();
		columnTreeWrapPane.setPrefWidth(150);
		columnTreeWrapPane.setMinSize(0, 0);
		columnTreeWrapPane.setMaxSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);
		columnTreeWrapPane.getChildren().add(columnTreePane);
		
		columnTreePane.prefHeightProperty().bind(columnTreeWrapPane.heightProperty());
		columnTreePane.prefWidthProperty().bind(columnTreeWrapPane.widthProperty());
		
		columnTitledPane = new TitledPane("Collection Fields", columnTreeWrapPane);
		columnTitledPane.prefHeightProperty().bind(columnAncPane.heightProperty());
		columnTitledPane.prefWidthProperty().bind(columnAncPane.widthProperty());
		columnTitledPane.setCollapsible(false);
		columnAncPane.getChildren().add(columnTitledPane);
		horizontalPane.getItems().add(columnAncWrapPane);
	}
	
	private void initFooterPane() {
		footerBorder = new BorderPane();
		footerBorder.prefWidthProperty().bind(tabFooterPane.widthProperty());
		footerBorder.prefHeightProperty().bind(tabFooterPane.heightProperty());
		tabFooterPane.getChildren().add(footerBorder);
		
		HBox resultInfoBox = new HBox();
		footerBorder.setLeft(resultInfoBox);
		resultInfoBox.getChildren().add(new Label("Result: "));
		resultInfoBox.getChildren().add(new Label(resultSize + " items."));
		resultInfoBox.setStyle("-fx-padding: 4px;");
		
		HBox resultNavBox = new HBox();
		footerBorder.setRight(resultNavBox);
		Button prev = new Button("", new ImageView(ImageUtil.PREV_16_16));
		prev.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
		resultNavBox.getChildren().add(prev);
		
		Label limit = new Label("20 of " + resultSize);
		resultNavBox.getChildren().add(limit);
		
		Button next = new Button("", new ImageView(ImageUtil.NEXT_16_16));
		next.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
		resultNavBox.getChildren().add(next);
	}
	
	private void initToolPane() {
		HBox toolBox = new HBox();
		toolBox.setStyle("-fx-padding: 4px;");
		tabToolPane.getChildren().add(toolBox);
		initToolButtons(toolBox);
	}
	
	private void initToolButtons(HBox toolBox) {
		Button refresh = new Button("Refresh", new ImageView(ImageUtil.DB_REFRESH_16_16));
		refresh.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
		toolBox.getChildren().add(refresh);
		
		Button add = new Button("Add", new ImageView(ImageUtil.DB_ADD_16_16));
		add.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
		toolBox.getChildren().add(add);
		
		Button remove = new Button("Remove", new ImageView(ImageUtil.DB_REMOVE_16_16));
		remove.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
		toolBox.getChildren().add(remove);
		
		Button filter = new Button("Filter", new ImageView(ImageUtil.DB_FILTER_16_16));
		filter.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
		toolBox.getChildren().add(filter);
	}
	
	public void destroy() {
		tableView.prefHeightProperty().unbind();
		tableView.prefWidthProperty().unbind();
		columnAncPane.prefHeightProperty().unbind();
		columnAncPane.prefWidthProperty().unbind();
		columnTreePane.prefHeightProperty().unbind();
		columnTreePane.prefWidthProperty().unbind();
		columnTitledPane.prefHeightProperty().unbind();
		columnTitledPane.prefWidthProperty().unbind();
		footerBorder.prefWidthProperty().unbind();
		footerBorder.prefHeightProperty().unbind();
		tableView.getColumns().clear();
		tableView.setItems(null);
		tableView = null;
		dataList.clear();
		dataList = null;
		columnTreePane.getRoot().getChildren().clear();
		columnTreePane.setRoot(new TreeItem<CheckBox>());
	}
	
}
