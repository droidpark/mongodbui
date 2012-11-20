package com.droidpark.mongoui.dialog;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

import com.droidpark.mongoui.component.ModalDialog;
import com.droidpark.mongoui.form.MainForm;
import com.droidpark.mongoui.util.ImageUtil;
import com.droidpark.mongoui.util.Language;
import com.droidpark.mongoui.util.MongoUIDBConnection;
import com.droidpark.mongoui.util.MongoUtil;

import static com.droidpark.mongoui.util.LanguageConstants.*;



public class ConnectionDialog extends ModalDialog {

	static String CONN_FILE = "mongoui.fx";
	
	Logger logger = Logger.getLogger(ConnectionDialog.class);
	
	TextField nameField = new TextField("localhost");
	TextField hostField = new TextField("127.0.0.1");
	TextField portField = new TextField("27017");
	TextField userField = new TextField();
	TextField passField = new TextField();
	
	SplitPane splitPane = new SplitPane();
	BorderPane border = new BorderPane();
	AnchorPane formPane = new AnchorPane();
	BorderPane databasePane = new BorderPane();
	ListView<MongoUIDBConnection> databaseListView = new ListView<MongoUIDBConnection>();
	MainForm mainForm;
	
	public ConnectionDialog(MainForm mainForm) {
		super(Language.get(DIALOG_TITLE_MANAGE_CONNECTIONS), 400, 210, ImageUtil.DATABASE_24_24);
		this.mainForm = mainForm;
		init();
	}

	private void init() {
		initContents();
		initConnectionList();
		initConnectionFields();
		initButtons();
		readConnectionList();
	}
	
	private void initConnectionList() {
		databaseListView.getStyleClass().add("-mongoui-listview");
		databasePane.setCenter(databaseListView);
		databaseListView.setOnMouseClicked(new ConnectionList_onClick());
		
		HBox hBox = new HBox();
		hBox.setAlignment(Pos.CENTER_RIGHT);
		hBox.setPrefHeight(25);
		hBox.getStyleClass().add("-mongoui-footerpane2");
		databasePane.setBottom(hBox);
		
		Button save = new Button("Save", new ImageView(ImageUtil.TB_DB_SAVE_16_16));
		save.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
		save.setOnAction(new ConnectionListAddButton_onClick());
		hBox.getChildren().add(save);
		
		Button remove = new Button("Remove", new ImageView(ImageUtil.TB_DB_REMOVE_16_16));
		remove.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
		remove.setOnAction(new ConnectionListRemoveButton_onClick());
		hBox.getChildren().add(remove);
	}
	
	private class ConnectionListAddButton_onClick implements EventHandler<ActionEvent> {
		public void handle(ActionEvent arg0) {
			MongoUIDBConnection connection = new MongoUIDBConnection();
			connection.setName(nameField.getText());
			connection.setHost(hostField.getText());
			connection.setPort(portField.getText());
			connection.setUsername(userField.getText());
			connection.setPassword(passField.getText());
			databaseListView.getItems().add(connection);
			writeConnectionList();
		}
	}
	
	private class ConnectionListRemoveButton_onClick implements EventHandler<ActionEvent> {
		public void handle(ActionEvent arg0) {
			databaseListView.getItems().remove(databaseListView.getSelectionModel().getSelectedIndex());
			writeConnectionList();
		}
	}
	
	public class ConnectionList_onClick implements EventHandler<Event> {
		public void handle(Event arg0) {
			MongoUIDBConnection conn = databaseListView.getSelectionModel().getSelectedItem();
			nameField.setText(conn.getName());
			hostField.setText(conn.getHost());
			portField.setText(conn.getPort());
			userField.setText(conn.getUsername());
			passField.setText(conn.getPassword());
		}
	}
	
	private void initButtons() {
		Button cancelButton = new Button("Cancel");
		addNodeToFooter(cancelButton);
		cancelButton.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent arg0) {
				hideModalDialog();
				destroy();
			}
		});
		
		Button connectButton = new Button("Connect");
		connectButton.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent arg0) {
				try {
					MongoUtil.setHost(hostField.getText());
					MongoUtil.setPort(Integer.valueOf(portField.getText()));
					MongoUtil.setUsername(userField.getText());
					MongoUtil.setPassword(passField.getText());
					MongoUtil.initConnection();
					logger.info("Successfully connectted to " + MongoUtil.getHost() + ".");
					mainForm.refreshDatabaseTreeView();
				}
				catch (Exception e) {
					logger.error(e.getMessage(),e);
				}
				finally {
					hideModalDialog();
					destroy();
				}
			}
		});
		addNodeToFooter(connectButton);
	}
	
	private void initConnectionFields() {
		GridPane grid = new GridPane();
		grid.setStyle("-fx-padding: 5px;");
		int filedWidth = 125;
		Label nameLabel = new Label(Language.get(LABEL_CONNECTION_NAME));
		nameLabel.setStyle("-fx-padding: 0px 20px 0px 0px;");
		nameField.setPrefWidth(filedWidth);
		grid.addRow(0, nameLabel, nameField);
		
		Label hostLabel = new Label(Language.get(LABEL_HOST));
		nameLabel.setStyle("-fx-padding: 0px 20px 0px 0px;");
		hostField.setPrefWidth(filedWidth);
		grid.addRow(1, hostLabel, hostField);
		
		Label portLabel = new Label(Language.get(LABEL_PORT));
		portLabel.setStyle("-fx-padding: 0px 20px 0px 0px;");
		portField.setPrefWidth(filedWidth);
		grid.addRow(2, portLabel, portField);
		
		Label userLabel = new Label(Language.get(LABEL_USERNAME));
		userLabel.setStyle("-fx-padding: 0px 20px 0px 0px;");
		userField.setPrefWidth(filedWidth);
		grid.addRow(3, userLabel, userField);
		
		Label passLabel = new Label(Language.get(LABEL_PASSWORD));
		passLabel.setStyle("-fx-padding: 0px 20px 0px 0px;");
		passField.setPrefWidth(filedWidth);
		grid.addRow(4, passLabel, passField);
		formPane.getChildren().add(grid);
	}
	
	private void initContents() {
		border.prefWidthProperty().bind(getContent().widthProperty());
		border.prefHeightProperty().bind(getContent().heightProperty());
		border.setCenter(splitPane);
		splitPane.getItems().add(databasePane);
		splitPane.getItems().add(formPane);
		splitPane.setDividerPosition(0,0.45);
		splitPane.setOrientation(Orientation.HORIZONTAL);
		setContent(border);
	}
	
	@Override
	public void destroy() {
		border.prefWidthProperty().unbind();
		border.prefHeightProperty().unbind();
		mainForm = null;
		super.destroy();
	}
	
	private void readConnectionList() {
		ObjectInput input = null;
		try {
			InputStream file = new FileInputStream(CONN_FILE);
			InputStream buffer = new BufferedInputStream(file);
			input = new ObjectInputStream(buffer);
			List<MongoUIDBConnection> conns = (List<MongoUIDBConnection>) input.readObject();
			databaseListView.getItems().clear();
			databaseListView.getItems().addAll(conns);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			if(input != null) {try {input.close();}catch (Exception e) {e.printStackTrace();}}
		}
	}
	
	private void writeConnectionList() {
		ObjectOutput output = null;
		try {
			OutputStream file = new FileOutputStream(CONN_FILE);
			OutputStream buffer = new BufferedOutputStream(file);
			output = new ObjectOutputStream(buffer);
			List<MongoUIDBConnection> conns = new ArrayList<MongoUIDBConnection>();
			conns.addAll(databaseListView.getItems());
			output.writeObject(conns);
			databaseListView.getItems().clear();
			databaseListView.getItems().addAll(conns);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			if(output != null) {try {output.close();}catch (Exception e) {e.printStackTrace();}}
		}
	}
}
