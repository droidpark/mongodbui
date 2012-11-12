package com.droidpark.mongoui.component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.bson.types.ObjectId;

import com.droidpark.mongoui.dialog.DataResultFilterDialog;
import com.droidpark.mongoui.dialog.DocumentRawDataViewDialog;
import com.droidpark.mongoui.dialog.EditDocumentDialog;
import com.droidpark.mongoui.util.ImageUtil;
import com.droidpark.mongoui.util.Language;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.util.JSON;

import static com.droidpark.mongoui.util.LanguageConstants.*;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.util.Callback;

public class ResultTab extends Tab implements UITab {

	static Logger logger = Logger.getLogger(ResultTab.class);
	Integer dataLimitValue = 100;
	Integer dataSkipValue = 0;
	
	Mongo mongo;
	String collectionName;
	String databaseName;
	
	DB database = null; 
	DBCollection collection = null;
	Map<String, Column> columns;
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
	
	DataResultFilterDialog filterDialog = null;
	
	Label resultSizeLabel = new Label();
	Label navInfoLabel = new Label();
	
	int resultSize = 0;
	private Gson gson = null;
	
	BasicDBObject query = null;
	
	final ResultTab instance = this;
	
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
		initFilterDialog();
		initLabels();
		
		BorderPane borderPane = new BorderPane();
		borderPane.setTop(tabToolPane);
		borderPane.setCenter(horizontalPane);
		borderPane.setBottom(tabFooterPane);
		setContent(borderPane);
	}
	
	private void initLabels() {
		navInfoLabel.setStyle("-fx-padding: 2px 5px;");
	}
	
	private void initDataListAndColumnList() {
		try {
			dataList = FXCollections.observableArrayList();
			columns = new HashMap<String, ResultTab.Column>();
			query = query == null ? new BasicDBObject() : query;
			
			GsonBuilder gsonBuilder = new GsonBuilder();
			gson = gsonBuilder.serializeNulls().create();
			database = mongo.getDB(databaseName);
			collection = database.getCollection(collectionName);
			
			resultSize = collection.find(query).skip(dataSkipValue).size();
			resultSizeLabel.setText(resultSize + " Row(s)");
			int navInfoSize = resultSize < dataLimitValue ? resultSize : dataLimitValue;
			navInfoLabel.setText(dataSkipValue + " - " + navInfoSize + " of " + resultSize);
			logger.info("db." + collectionName + ".find("+query.toString()+").skip("+dataSkipValue+").size()");
			
			DBCursor cursor = collection.find(query).skip(dataSkipValue).limit(dataLimitValue);
			logger.info("db." + collectionName + ".find("+query.toString()+").skip("+dataSkipValue+").limit("+dataLimitValue+")");
			
			//init column list
			Map<String, Column> columnsMap = new HashMap<String, ResultTab.Column>();
			while(cursor.hasNext()) {
				DBObject object = cursor.next();
				object.isPartialObject();
				dataList.add(object);
				for(String columnStr : object.keySet()) {
					Object cobject = (Object) object.get(columnStr);
					Class clazz = cobject != null ? cobject.getClass() : null;
					Column column = new Column(columnStr, clazz);
					columnsMap.put(columnStr, column);
				}
				
			}
			columns.putAll(columnsMap);
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
		refreshTableView();
		
	}
	
	private void refreshTableView() {
		clearTableViewData();
		for(final String columnName : columns.keySet()) {
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
							value = value.length() > 100 ? value.substring(0, 100) + "..." : value;
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
			tableView.getSelectionModel().setCellSelectionEnabled(false);
			tableView.setItems(dataList);
		}
	}
	
	private void clearTableViewData() {
		tableView.getColumns().clear();
		tableView.getItems().clear();
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
		for(final String columnName : columns.keySet()) {
			final Column column = columns.get(columnName);
			final TreeItem<CheckBox> check = new TreeItem<CheckBox>();
			check.setValue(new CheckBox());
			String type = column.clazz != null ? column.clazz.getSimpleName() : "Unkown";
			check.getValue().setText(column.name + " [" + type +"]");
			check.getValue().setSelected(true);
			check.getValue().setOnAction(new EventHandler<ActionEvent>() {
				public void handle(ActionEvent arg0) {
					List<String> tempcolumn = new ArrayList<String>();
					for(TableColumn column : table.getColumns()) {
						tempcolumn.add(column.getText());
					}
					int index = tempcolumn.indexOf(column.name);
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
		resultInfoBox.getChildren().add(resultSizeLabel);
		resultInfoBox.setStyle("-fx-padding: 4px;");
		
		//initFooterNavButtons();
	}
	
	//init foolter button
	private void initFooterNavButtons() {
		HBox resultNavBox = new HBox();
		resultNavBox.setStyle("-fx-padding: 4px;");
		footerBorder.setRight(resultNavBox);
		Button prev = new Button("", new ImageView(ImageUtil.PREV_16_16));
		prev.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
		prev.setOnAction(new FooterPrevButton_onClick());
		resultNavBox.getChildren().add(prev);
		resultNavBox.getChildren().add(navInfoLabel);
		
		Button next = new Button("", new ImageView(ImageUtil.NEXT_16_16));
		next.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
		next.setOnAction(new FooterNextButton_onClick());
		resultNavBox.getChildren().add(next);
	}
	
	//init toolbar
	private void initToolPane() {
		HBox toolBox = new HBox();
		toolBox.setStyle("-fx-padding: 4px;");
		tabToolPane.getChildren().add(toolBox);
		initToolButtons(toolBox);
	}
	
	//init toolbar buttons
	private void initToolButtons(HBox toolBox) {
		//toolbar refresh button
		Button refresh = new Button("Refresh", new ImageView(ImageUtil.TB_DB_REFRESH_16_16));
		refresh.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
		toolBox.getChildren().add(refresh);
		refresh.setOnAction(new ToolbarRefreshButton_onClick());
		
		//toolbar add button
		Button add = new Button("Add", new ImageView(ImageUtil.TB_DB_ADD_16_16));
		add.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
		add.setOnAction(new ToolbarAddButtn_onClick());
		toolBox.getChildren().add(add);
		
		//toolbar edit button
		Button edit = new Button("Edit", new ImageView(ImageUtil.TB_DB_EDIT_16_16));
		edit.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
		edit.setOnAction(new ToolbarEditButton_onClick());
		toolBox.getChildren().add(edit);
		
		//toolbar remove button
		Button remove = new Button("Remove", new ImageView(ImageUtil.TB_DB_REMOVE_16_16));
		remove.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
		toolBox.getChildren().add(remove);
		remove.setOnAction(new ToolbarRemoveButton_onClick());
		
		//toolbar filter button
		Button filter = new Button("Filter", new ImageView(ImageUtil.TB_DB_FILTER_16_16));
		filter.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
		toolBox.getChildren().add(filter);
		filter.setOnAction(new ToolbarFilterButton_onClick());
		
		//toolber document button
		Button document = new Button("Document", new ImageView(ImageUtil.TB_DB_DOC_16_16));
		document.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
		toolBox.getChildren().add(document);
		document.setOnAction(new ToolbarDocumentButton_onClick());
	}
	
	//init filter dialog
	private void initFilterDialog() {
		filterDialog = new DataResultFilterDialog(this); 
	}
	
	//Refresh datalist
	public void refreshData() {
		initDataListAndColumnList();
		refreshTableView();
	}
	
	//FooterPane Prev Button onlick
	private class FooterPrevButton_onClick implements EventHandler<ActionEvent> {
		public void handle(ActionEvent arg0) {
			if((dataSkipValue - dataLimitValue) >= 0) {
				dataSkipValue = dataSkipValue - dataLimitValue;
			}
			else {
				dataSkipValue = 0;
			}
			refreshData();
		}
	}
	
	//FooterPane Next Button on click
	private class FooterNextButton_onClick implements EventHandler<ActionEvent> {
		public void handle(ActionEvent arg0) {
			if((dataSkipValue + dataLimitValue) <= resultSize) {
				dataSkipValue = dataSkipValue + dataLimitValue;
			}
			else {
				dataSkipValue = resultSize;
			}
			refreshData();
		}
	}
	
	//Toolbar Add Document On Click Action
	private class ToolbarAddButtn_onClick implements EventHandler<ActionEvent> {
		public void handle(ActionEvent arg0) {
			EditDocumentDialog dialog = new EditDocumentDialog(instance);
			dialog.showModalDialog();
		}
	}
	
	//Toolbar Edit Document On Click Action
	private class ToolbarEditButton_onClick implements EventHandler<ActionEvent> {
		public void handle(ActionEvent arg0) {
			DBObject object = tableView.getSelectionModel().getSelectedItem();
			if(object != null) {
				EditDocumentDialog dialog = new EditDocumentDialog(object, instance);
				dialog.showModalDialog();
			}
		}
	}
	
	//Toolbar Remove Button On Click Action
	private class ToolbarRemoveButton_onClick implements EventHandler<ActionEvent> {
		public void handle(ActionEvent arg0) {
			final ModalDialog dialog = ModalDialog.createYesNoQuestionDialog(Language.get(DIALOG_TITTLE_REMOVE), 
					Language.get(MESSAGE_ARE_YOU_SURE));
			Button yesButton = new Button(Language.get(BUTTON_YES));
			dialog.addNodeToFooter(yesButton);
			yesButton.setOnAction(new EventHandler<ActionEvent>() {
				public void handle(ActionEvent arg0) {
					DBObject document = tableView.getSelectionModel().getSelectedItem(); 
					collection.remove(document);
					refreshData();
					logger.info("Removed: " + document.get("_id"));
					dialog.hideModalDialog();
					dialog.destroy();
				}
			});
			dialog.showModalDialog();
		}
	}
	
	//Toolbar refresh button onclick action
	private class ToolbarRefreshButton_onClick implements EventHandler<ActionEvent> {
		public void handle(ActionEvent arg0) {
			refreshData();
		}
	}
	
	//Toolber filter button onclick action
	private class ToolbarFilterButton_onClick implements EventHandler<ActionEvent> {
		public void handle(ActionEvent arg0) {
			filterDialog.showModalDialog();
		}
	}
	
	//Toolbar document button onclick action
	private class ToolbarDocumentButton_onClick implements EventHandler<ActionEvent> {
		public void handle(ActionEvent arg0) {
			DBObject document = tableView.getSelectionModel().getSelectedItem();
			if(document != null) {
				DocumentRawDataViewDialog dialog = new DocumentRawDataViewDialog(document, instance);
				dialog.showModalDialog();
			}
		}
	}
	
	public class Column {
		private String name;
		private Class clazz;
		Column(String name, Class clazz) {
			this.name = name;
			this.clazz = clazz;
		}
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
		filterDialog.destroy();
	}

	public Integer getDataLimitValue() {
		return dataLimitValue;
	}

	public void setDataLimitValue(Integer dataLimitValue) {
		this.dataLimitValue = dataLimitValue;
	}

	public Integer getDataSkipValue() {
		return dataSkipValue;
	}

	public void setDataSkipValue(Integer dataSkipValue) {
		this.dataSkipValue = dataSkipValue;
	}

	public BasicDBObject getQuery() {
		return query;
	}

	public void setQuery(BasicDBObject query) {
		this.query = query;
	}

	public Map<String, Column> getColumns() {
		return columns;
	}

	public AnchorPane getColumnAncWrapPane() {
		return columnAncWrapPane;
	}

	public DBCollection getCollection() {
		return collection;
	}
	
}
