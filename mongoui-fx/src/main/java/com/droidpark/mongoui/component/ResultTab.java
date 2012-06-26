package com.droidpark.mongoui.component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bson.types.ObjectId;

import com.droidpark.mongoui.util.ConsoleUtil;
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
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TitledPane;
import javafx.scene.control.Tooltip;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.util.Callback;

public class ResultTab extends Tab {

	private String collectionName;
	private String databaseName;
	
	List<String> columns;
	TableView<DBObject> tableView = new TableView<DBObject>();
	ObservableList<DBObject> dataList;
	
	AnchorPane tabToolPane;
	AnchorPane tabFooterPane;
	SplitPane horizontalPane;
	
	int resultSize = 0;
	private Gson gson = null;
	
	public ResultTab(String collection, String database) {
		super(database + "." + collection);
		this.collectionName = collection;
		this.databaseName = database;
		initComponent();
	}
	
	private void initComponent() {
		initLayout();
		initDataListAndColumnList();
		initResultTable();
		initColumnViewPane();
		initFooterPane();
		
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
			Mongo mongo = new Mongo("192.168.56.101", 27017);
			DB database = mongo.getDB(databaseName);
			DBCollection collection = database.getCollection(collectionName);
			resultSize = collection.find(new BasicDBObject()).size();
			ConsoleUtil.echo("Query: db." + collectionName + ".find().size()");
			DBCursor cursor = collection.find(new BasicDBObject()).limit(20);
			ConsoleUtil.echo("Query: db." + collectionName + ".find().limit(20)");
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
		AnchorPane tableAncPane = new AnchorPane();
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
					table.getColumns().get(index).setVisible(check.getValue().isSelected());
				}
			});
			rootField.getChildren().add(check);
		}
		
		AnchorPane columnAncWrapPane = new AnchorPane();
		AnchorPane columnAncPane = new AnchorPane();
		columnAncPane.setMinSize(0, 0);
		columnAncPane.setMaxSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);
		columnAncPane.prefHeightProperty().bind(columnAncWrapPane.heightProperty());
		columnAncPane.prefWidthProperty().bind(columnAncWrapPane.widthProperty());
		columnAncWrapPane.getChildren().add(columnAncPane);
		
		TreeView<CheckBox> columnTreePane = new TreeView<CheckBox>(rootField);
		AnchorPane columnTreeWrapPane = new AnchorPane();
		columnTreeWrapPane.setPrefWidth(150);
		columnTreeWrapPane.setMinSize(0, 0);
		columnTreeWrapPane.setMaxSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);
		columnTreeWrapPane.getChildren().add(columnTreePane);
		
		columnTreePane.prefHeightProperty().bind(columnTreeWrapPane.heightProperty());
		columnTreePane.prefWidthProperty().bind(columnTreeWrapPane.widthProperty());
		
		TitledPane columnTitledPane = new TitledPane("Collection Fields", columnTreeWrapPane);
		columnTitledPane.prefHeightProperty().bind(columnAncPane.heightProperty());
		columnTitledPane.prefWidthProperty().bind(columnAncPane.widthProperty());
		columnTitledPane.setCollapsible(false);
		columnAncPane.getChildren().add(columnTitledPane);
		horizontalPane.getItems().add(columnAncWrapPane);
	}
	
	private void initFooterPane() {
		HBox footerBox = new HBox();
		footerBox.getChildren().add(new Label("Result: "));
		footerBox.getChildren().add(new Label(resultSize + " items."));
		footerBox.setStyle("-fx-padding: 4px;");
		tabFooterPane.getChildren().add(footerBox);
	}
	
}
