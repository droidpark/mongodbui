package com.droidpark.mongoui.dialog;

import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.bson.BSONObject;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

import com.droidpark.mongoui.component.ModalDialog;
import com.droidpark.mongoui.form.MainForm;
import com.droidpark.mongoui.util.ImageUtil;
import com.droidpark.mongoui.util.Language;
import com.droidpark.mongoui.util.MongoUtil;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBObject;
import com.mongodb.Mongo;

import static com.droidpark.mongoui.util.LanguageConstants.*;

public class ManageCollectionDialog extends ModalDialog {

	Logger logger = Logger.getLogger(ManageCollectionDialog.class);
	
	ComboBox<String> databaseCombo;
	TextField collectionText;
	MainForm mainForm;
	
	public ManageCollectionDialog(MainForm form) {
		super(Language.get(DIALOG_TITLE_MANAGE_COLLECTION), 300, 150, ImageUtil.COLLECTION_24_24);
		this.mainForm = form;
		init();
	}
	
	private void init() {
		initContent();
		initButtons();
	}
	
	private void initContent() {
		GridPane grid = new GridPane();
		grid.setStyle("-fx-padding: 10px;");
		
		Label databaseLabel = new Label(Language.get(LABEL_DATABASE));
		databaseLabel.setStyle("-fx-padding: 0px 50px 10px 0px;");
		grid.add(databaseLabel, 0, 0);
		databaseCombo = new ComboBox<String>();
		databaseCombo.getItems().addAll(MongoUtil.getConnection().getDatabaseNames());
		grid.add(databaseCombo, 1, 0);
		
		Label collectionLabel = new Label(Language.get(LABEL_COLLECTION));
		collectionLabel.setStyle("-fx-padding: 0px 50px 0px 0px;");
		grid.add(collectionLabel, 0, 1);
		
		collectionText = new TextField();
		grid.add(collectionText, 1, 1);
		
		setContent(grid);
	}
	
	private void initButtons() {
		Button cancelButton = new Button(Language.get(BUTTON_CANCEL));
		addNodeToFooter(cancelButton);
		cancelButton.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent arg0) {
				hideModalDialog();
				destroy();
			}
		});
		
		Button createButton = new Button(Language.get(BUTTON_CREATE));
		createButton.setOnAction(new CreateButton_OnClick());
		addNodeToFooter(createButton);
		
	}
	
	//Create button on click action
	private class CreateButton_OnClick implements EventHandler<ActionEvent> {
		public void handle(ActionEvent arg0) {
			try {
				String dbname = databaseCombo.getValue();
				String collectionName = collectionText.getText();
				DB database = MongoUtil.getConnection().getDB(dbname);
				if(!database.collectionExists(collectionName)) {
					database.createCollection(collectionName, new BasicDBObject());
					mainForm.refreshDatabaseTreeView();
					logger.info("Collection created.");
				}
				else {
					logger.info("Collection already exist!.");
				}
			}
			catch (Exception e) {
				logger.error(e.getMessage(),e);
			}
			finally {
				hideModalDialog();
				destroy();
			}
		}
	}

	@Override
	public void destroy() {
		super.destroy();
	}
	
}
