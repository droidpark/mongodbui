package com.droidpark.mongoui.dialog;

import org.apache.log4j.Logger;
import org.json.JSONObject;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;

import com.droidpark.mongoui.component.CodeEditor;
import com.droidpark.mongoui.component.ModalDialog;
import com.droidpark.mongoui.component.ResultTab;
import com.droidpark.mongoui.util.ImageUtil;
import com.droidpark.mongoui.util.Language;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.WriteResult;
import com.mongodb.util.JSON;

import static com.droidpark.mongoui.util.LanguageConstants.*;

public class EditDocumentDialog extends ModalDialog {
	
	Logger logger = Logger.getLogger(EditDocumentDialog.class);
	
	boolean managed;
	DBObject document = null;
	ResultTab resultTab;
	AnchorPane pane = new AnchorPane();
	CodeEditor editor = new CodeEditor();
	
	public EditDocumentDialog(ResultTab resultTab) {
		super(Language.get(DIALOG_TITLE_ADD_DOCUMENT), 400, 200, ImageUtil.MD_DB_ADD_24_24);
		this.resultTab = resultTab;
		init();
	}
	
	public EditDocumentDialog(DBObject object, ResultTab resultTab) {
		super(Language.get(DIALOG_TITLE_EDIT_DOCUMENT), 400, 200, ImageUtil.MD_DB_EDIT_24_24);
		this.managed = true;
		document = object;
		this.resultTab = resultTab;
		init();
	}

	private void init() {
		initButtons();
		codeEditorInit();
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
		
		if(managed) {
			Button updateButton = new Button(Language.get(BUTTON_UPDATE));
			addNodeToFooter(updateButton);
			updateButton.setOnAction(new UpdateButton_OnClick());
		}
		else {
			Button saveButton = new Button(Language.get(BUTTON_SAVE));
			addNodeToFooter(saveButton);
			saveButton.setOnAction(new SaveButton_OnClick());
		}
	}

	//Update button onclick action
	private class UpdateButton_OnClick implements EventHandler<ActionEvent> {
		public void handle(ActionEvent arg0) {
			try {
				DBCollection collection = resultTab.getCollection();
				DBObject update = (DBObject)JSON.parse(editor.getValue());
				WriteResult reulst = collection.update(document, update);
				logger.info("Document Updated: " + document + " to " + update);
				resultTab.refreshData();
				hideModalDialog();
				destroy();
			}
			catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
		}
	}
	
	//Save button onclick action
	private class SaveButton_OnClick implements EventHandler<ActionEvent> {
		public void handle(ActionEvent arg0) {
			try {
				DBCollection collection = resultTab.getCollection();
				DBObject object = (DBObject) JSON.parse(editor.getValue());
				WriteResult result = collection.save(object);
				logger.info("Document saved: " + object.toString());
				resultTab.refreshData();
				hideModalDialog();
				destroy();
			}
			catch (Exception e) {
				logger.error(e.getMessage(),e);
			}
		}
	}
	
	private void codeEditorInit() {
		pane.prefHeightProperty().bind(getContent().heightProperty());
		pane.prefWidthProperty().bind(getContent().widthProperty());
		editor.prefHeightProperty().bind(pane.heightProperty());
		editor.prefWidthProperty().bind(pane.widthProperty());
		pane.getChildren().add(editor);
		setContent(pane);
		String code = getDocumentJson();
		editor.loadCode(code);
	}
	
	private String getDocumentJson() {
		String code = "";
		if(document != null) {
			try {
				code = new JSONObject(document.toString()).toString(2);
			}
			catch (Exception e) {
				logger.error(e.getMessage(),e);
			}
		}
		return code;
	}
	
	
	@Override
	public void destroy() {
		pane.prefHeightProperty().unbind();
		pane.prefWidthProperty().unbind();
		editor.prefHeightProperty().bind(pane.heightProperty());
		editor.prefWidthProperty().bind(pane.widthProperty());
		editor.destroy();
		super.destroy();
	}
}
