package com.droidpark.mongoui.form;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Accordion;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.control.ToolBar;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import static com.droidpark.mongoui.util.LanguageConstants.*;

import com.droidpark.mongoui.component.ModalDialog;
import com.droidpark.mongoui.task.AddTabTask;
import com.droidpark.mongoui.task.CreateJSEditorTab;
import com.droidpark.mongoui.task.CreateResultTabTask;
import com.droidpark.mongoui.util.DBTreeEnum;
import com.droidpark.mongoui.util.ImageUtil;
import com.droidpark.mongoui.util.Language;
import com.droidpark.mongoui.util.LanguageConstants;
import com.droidpark.mongoui.util.Log4jTextAreaAppender;
import com.droidpark.mongoui.util.Util;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;

public class MainForm extends Application {

	private static Logger logger = Logger.getLogger(MainForm.class);
	
	private Stage stage;
	private Scene scene;
	private AnchorPane pane;
	private BorderPane borderLayout;
	private ToolBar toolBar;
	private HBox statusBar;
	private Accordion leftAccordionPane;
	private AnchorPane databasePane;
	private BorderPane consolePane;
	private AnchorPane centerPane;
	private TabPane tabPane;
	private TextArea consoleTextArea = new TextArea();
	
	private final ProgressBar progressBar = new ProgressBar();
	
	private Mongo mongo;
	
	@Override
	public void start(Stage primary) throws Exception {
		Log4jTextAreaAppender.initTextArea(consoleTextArea);
		stage = primary;
		initComponent();
		stage.show();
	}

	
	/**
	 * Init All UI Components
	 */
	private void initComponent() {
		initStage();
		initBorderLayout();
		initToolBar();
		initToolBarButtons();
		initStatusBar();
		initCenterPane();
		initTabPane();
		initConsole();
		connectToDatabaseModalDialog();
	}
	
	
	/**
	 * Init Main Stage
	 */
	private void initStage() {
		stage.setTitle("MongoUI FX");
		pane = new AnchorPane();
		scene = new Scene(pane, 850, 500);
		scene.getStylesheets().addAll(getClass().getResource(Util.DEFAULT_STYLE + "style.css").toExternalForm());
		pane.prefWidthProperty().bind(scene.widthProperty());
		pane.prefHeightProperty().bind(scene.heightProperty());
		stage.setScene(scene);
		stage.centerOnScreen();
		progressBar.setProgress(0);
		Util.MAIN_FRAME = pane;
	}
	
	/**
	 * Init Border Layout
	 */
	private void initBorderLayout() {
		borderLayout = new BorderPane();
		borderLayout.prefHeightProperty().bind(pane.heightProperty());
		borderLayout.prefWidthProperty().bind(pane.widthProperty());
		pane.getChildren().add(borderLayout);
	}
	
	/**
	 * Init Top ToolBar
	 */
	private void initToolBar() {
		toolBar = new ToolBar();
		toolBar.getStyleClass().add("main-toolbar");
		toolBar.setPrefHeight(70);
		borderLayout.setTop(toolBar);
	}
	
	
	/**
	 * Init Bottom Status Bar
	 */
	private void initStatusBar() {
		statusBar = new HBox();
		statusBar.getStyleClass().add("status-bar");
		statusBar.setPrefHeight(25);
		statusBar.setAlignment(Pos.CENTER_RIGHT);
		statusBar.getChildren().add(progressBar);
		borderLayout.setBottom(statusBar);
	}
	
	
	/*
	 * Init Center Panel
	 */
	private void initCenterPane() {
		//Horizontal Split Pane
		SplitPane horizontalSplitPane = new SplitPane();
		horizontalSplitPane.setOrientation(Orientation.HORIZONTAL);
		horizontalSplitPane.setDividerPositions(0.3);
		borderLayout.setCenter(horizontalSplitPane);
		
		//Left Position
		AnchorPane leftPane = new AnchorPane();
		leftPane.setPrefWidth(150);
		leftPane.setMinSize(0, 0);
		leftPane.setMaxSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);
		horizontalSplitPane.getItems().add(leftPane);
		
		//Left Accordion Pane
		leftAccordionPane = new Accordion();
		leftAccordionPane.prefHeightProperty().bind(leftPane.heightProperty());
		leftAccordionPane.prefWidthProperty().bind(leftPane.widthProperty());
		leftPane.getChildren().add(leftAccordionPane);
		
		//Accordion Database Pane
		databasePane = new AnchorPane();
		databasePane.setPrefWidth(150);
		databasePane.setMinSize(0, 0);
		databasePane.setMaxSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);
		TitledPane databaseTitledPane = new TitledPane("Database Explorer", databasePane);
		databaseTitledPane.setCollapsible(false);
		leftAccordionPane.getPanes().add(databaseTitledPane);
		leftAccordionPane.setExpandedPane(databaseTitledPane);
		
		//Right Position
		AnchorPane rightPane = new AnchorPane();
		horizontalSplitPane.getItems().add(rightPane);
		
		//Right Vertical Split Pane
		SplitPane verticalSplitPane = new SplitPane();
		verticalSplitPane.setOrientation(Orientation.VERTICAL);
		verticalSplitPane.setDividerPositions(0.7);
		verticalSplitPane.prefHeightProperty().bind(rightPane.heightProperty());
		verticalSplitPane.prefWidthProperty().bind(rightPane.widthProperty());
		rightPane.getChildren().add(verticalSplitPane);
		
		//Top Pane
		centerPane = new AnchorPane();
		centerPane.setStyle("-fx-background-color: #F1F1F1");
		verticalSplitPane.getItems().add(centerPane);
		
		//Bottom Pane
		consolePane = new BorderPane();
		consolePane.setMinSize(0, 0);
		consolePane.setMaxSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);
		consolePane.prefHeight(150);
		TitledPane consoleTitledPane = new TitledPane("Console", consolePane);
		consoleTitledPane.setCollapsible(false);
		
		AnchorPane consoleTitledPaneWrap = new AnchorPane();
		consoleTitledPaneWrap.setMinHeight(100);
		consoleTitledPaneWrap.setMinSize(0, 0);
		consoleTitledPaneWrap.setMaxSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);
		consoleTitledPaneWrap.getChildren().add(consoleTitledPane);
		consoleTitledPane.prefWidthProperty().bind(consoleTitledPaneWrap.widthProperty());
		consoleTitledPane.prefHeightProperty().bind(consoleTitledPaneWrap.heightProperty());
		verticalSplitPane.getItems().add(consoleTitledPaneWrap);
	}
	
	//Init ResultTabPane
	private void initTabPane() {
		tabPane = new TabPane();
		tabPane.prefWidthProperty().bind(centerPane.widthProperty());
		tabPane.prefHeightProperty().bind(centerPane.heightProperty());
		centerPane.getChildren().add(tabPane);
	}
	
	private void initToolBarButtons() {
		
		//Connection Database
		Button createConnectionButton = new Button(Language.get(MAIN_MENU_DATABASE), new ImageView(ImageUtil.DATABASE_24_24));
		createConnectionButton.setContentDisplay(ContentDisplay.TOP);
		createConnectionButton.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent arg0) {
				connectToDatabaseModalDialog();
			}
		});
		toolBar.getItems().add(createConnectionButton);
		
		//Add Collection
		Button addCollectionButton = new Button(Language.get(MAIN_MENU_COLLECTION), new ImageView(ImageUtil.COLLECTION_24_24));
		addCollectionButton.setContentDisplay(ContentDisplay.TOP);
		toolBar.getItems().add(addCollectionButton);
		
		//Add Index
		Button addIndexButton = new Button(Language.get(MAIN_MENU_INDEX), new ImageView(ImageUtil.INDEX2_24_24));
		addIndexButton.setContentDisplay(ContentDisplay.TOP);
		toolBar.getItems().add(addIndexButton); 
		
		//JavaScript
		Button javaScriptButton = new Button(Language.get(MAIN_MENU_JAVASCRIPT), new ImageView(ImageUtil.JAVASCRIPT_24_24));
		javaScriptButton.setContentDisplay(ContentDisplay.TOP);
		toolBar.getItems().add(javaScriptButton);
		
		//Server Status
		Button statusButton = new Button(Language.get(MAIN_MENU_SERVER_STATUS), new ImageView(ImageUtil.SYSTEM_MONITOR_24_24));
		statusButton.setContentDisplay(ContentDisplay.TOP);
		toolBar.getItems().add(statusButton);
		
		
		//Sharding
		Button toolsButton = new Button(Language.get(MAIN_MENU_SHARDING), new ImageView(ImageUtil.TOOLS_24_24));
		toolsButton.setContentDisplay(ContentDisplay.TOP);
		toolBar.getItems().add(toolsButton);
		
		//Settigns
		Button settingsButton = new Button(Language.get(MAIN_MENU_SETTINGS), new ImageView(ImageUtil.SETTINGS_24_24));
		settingsButton.setContentDisplay(ContentDisplay.TOP);
		toolBar.getItems().add(settingsButton);
	}
	
	
	/**
	 * Database Connection after Database Tree Init.
	 */
	private void initDatabaseTree(String host, int port) {
		try {
			mongo = new Mongo(host, port);
			List<String> databaseList = new ArrayList<String>(mongo.getDatabaseNames());
			//Root Node
			TreeItem<String> root = new TreeItem<String>(host, new ImageView(ImageUtil.NEXT_NODE_16_16));
			root.setExpanded(true);
			//Database Nodes
			for(String dbname : databaseList) {
				TreeItem<String> main = new TreeItem<String>(dbname, new ImageView(ImageUtil.DATABASE2_16_16));
				
				//Collection Nodes
				TreeItem<String> collectionItem = new TreeItem<String>("Collections", new ImageView(ImageUtil.COLLECTION_16_16));
				main.getChildren().add(collectionItem);
				
				//Add Collections
				DB database = mongo.getDB(dbname);
				Set<String> collectionList = new HashSet<String>(database.getCollectionNames());
				for(String collectionName: collectionList) {
					
					if(collectionName.contains("system.")) { continue;}
					
					TreeItem<String> item = new TreeItem<String>(collectionName, new ImageView(ImageUtil.COLLECTION3_16_16));
					collectionItem.getChildren().add(item);
					
					//Index Nodes
					TreeItem<String> index = new TreeItem<String>("Indexes", new ImageView(ImageUtil.INDEX2_16_16));
					item.getChildren().add(index);
					
					//Add Indexes
					DBCollection collection = database.getCollection(collectionName);
					List<DBObject> indexInfoList = collection.getIndexInfo();
					for(DBObject obj : indexInfoList) {
						TreeItem<String> indItem = new TreeItem<String>(obj.get("name").toString(), new ImageView(ImageUtil.INDEX_16_16));
						index.getChildren().add(indItem);
					}
				}
				root.getChildren().add(main);
				
				//JavaScripts
				TreeItem<String> javascriptItem = new TreeItem<String>("Stored JScripts", new ImageView(ImageUtil.JAVASCRIPT_16_16));
				main.getChildren().add(javascriptItem);
				
				if(database.collectionExists("system.js")) {
					DBCollection javascripts = database.getCollection("system.js");
					DBCursor cursor =  javascripts.find();
					
					while(cursor.hasNext()) {
						DBObject obj = cursor.next();
						TreeItem<String> item = new TreeItem<String>(obj.get("_id").toString(),  new ImageView(ImageUtil.JAVASCRIPT2_16_16));
						javascriptItem.getChildren().add(item);
					}
				}
			}
			
			//Database TreeView
			databasePane.getChildren().clear();
			final TreeView<String> treeView = new TreeView<String>(root);
			treeView.prefHeightProperty().bind(databasePane.heightProperty());
			treeView.prefWidthProperty().bind(databasePane.widthProperty());
			databasePane.getChildren().add(treeView);
			
			
			// Add New Result Tab For onClick Collection Item
			treeView.setOnMouseClicked(new EventHandler<MouseEvent>() {
				public void handle(MouseEvent event) {
					TreeItem<String> item = treeView.getSelectionModel().getSelectedItem();
					if(item == null) {return;}
					if(item.getParent() != null) {
						DBTreeEnum treeEnum = DBTreeEnum.find(item.getParent().getValue());
						switch (treeEnum) {
							case COLLECTION: onClickCollectionItem(item); break;
							case JAVASCRIPT: onClickJavaScriptItem(item); break;
						}
					}
				}
			});
			
			logger.info("Successfully connected to " + host + ".");
		}
		catch (UnknownHostException e) {
			databasePane.getChildren().clear();
			logger.warn("Unknown Host: " + e.getMessage());
			logger.debug(e.getMessage(),e);
		}
		catch (Exception e) {
			databasePane.getChildren().clear();
			logger.debug(e.getMessage(), e);
		}
	}
	
	// Database Tree onClick Collection Items
	private void onClickCollectionItem(TreeItem<String> item) {
		String database = item.getParent().getParent().getValue();
		String collection = item.getValue();
		//Create new result Tab task
		CreateResultTabTask task = new CreateResultTabTask(collection, database, mongo);
		progressBar.progressProperty().bind(task.progressProperty());
		Thread taskThread = new Thread(task);
		taskThread.start();
		
		//Add Result tab to TabPane Task
		AddTabTask addTabTask = new AddTabTask(task, tabPane);
		Thread tabThread = new Thread(addTabTask);
		tabThread.start();
	}
	
	private void onClickJavaScriptItem(TreeItem<String> item) {
		String database = item.getParent().getParent().getValue();
		String stored = item.getValue();
		
		CreateJSEditorTab task = new CreateJSEditorTab(stored, database, mongo);
		progressBar.progressProperty().bind(task.progressProperty());
		Thread taskThread = new Thread(task);
		taskThread.start();
		
		AddTabTask addTabTask = new AddTabTask(task, tabPane);
		Thread tabThread = new Thread(addTabTask);
		tabThread.start();
	}
	
	//Init Console
	private void initConsole() {
		AnchorPane consoleTextWrap = new AnchorPane();
		consoleTextWrap.setMinSize(0, 0);
		consoleTextWrap.setMaxSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);
		consoleTextWrap.prefHeightProperty().bind(consolePane.heightProperty());
		consoleTextWrap.prefWidthProperty().bind(consolePane.widthProperty());
		
		consoleTextArea.prefWidthProperty().bind(consoleTextWrap.prefWidthProperty());
		consoleTextArea.prefHeightProperty().bind(consoleTextWrap.prefHeightProperty());
		consoleTextWrap.getChildren().add(consoleTextArea);
		
		consolePane.getChildren().add(consoleTextWrap);
	}

	private void connectToDatabaseModalDialog() {
		
		final ModalDialog dialog = new ModalDialog("Connect to Database", 250, 150, ImageUtil.DATABASE_24_24);
		GridPane grid = new GridPane();
		
		Label hostLabel = new Label("Host: ");
		hostLabel.setStyle("-fx-padding: 0px 50px 0px 0px;");
		final TextField hostField = new TextField("localhost");
		grid.addRow(0, hostLabel, hostField);
		grid.setStyle("-fx-padding: 10px;");
		
		Label portLabel = new Label("Port: ");
		portLabel.setStyle("-fx-padding: 0px 50px 0px 0px;");
		final TextField portField = new TextField("27017");
		grid.addRow(1, portLabel, portField);
		dialog.setContent(grid);
		Button cancelButton = new Button("Cancel");
		cancelButton.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent arg0) {
				dialog.hideModalDialog();
				dialog.destroy();
			}
		});
		dialog.addNodeToFooter(cancelButton);
		
		Button connectButton = new Button("Connect");
		connectButton.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent arg0) {
				dialog.hideModalDialog();
				tabPane.getTabs().clear();
				initDatabaseTree(hostField.getText(), Integer.valueOf(portField.getText()));
			}
		});
		dialog.addNodeToFooter(connectButton);
		dialog.showModalDialog();
	}
}
