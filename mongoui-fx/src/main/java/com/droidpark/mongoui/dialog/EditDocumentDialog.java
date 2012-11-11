package com.droidpark.mongoui.dialog;

import java.util.Map;

import org.apache.log4j.Logger;
import org.json.JSONObject;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;

import com.droidpark.mongoui.component.ModalDialog;
import com.droidpark.mongoui.component.ResultTab;
import com.droidpark.mongoui.util.ImageUtil;
import com.droidpark.mongoui.util.Language;
import com.mongodb.DBObject;

import static com.droidpark.mongoui.util.LanguageConstants.*;

public class EditDocumentDialog extends ModalDialog {
	
	Logger logger = Logger.getLogger(EditDocumentDialog.class);
	
	boolean managed;
	DBObject document = null;
	ResultTab resultTab;
	AnchorPane pane = new AnchorPane();
	TextArea textArea = new TextArea();
	
	public EditDocumentDialog(ResultTab resultTab) {
		super(Language.get(DIALOG_TITLE_ADD_DOCUMENT), 600, 300, ImageUtil.MD_DB_ADD_24_24);
		this.resultTab = resultTab;
		init();
	}
	
	public EditDocumentDialog(DBObject object, ResultTab resultTab) {
		super(Language.get(DIALOG_TITLE_EDIT_DOCUMENT), 600, 300, ImageUtil.MD_DB_EDIT_24_24);
		this.managed = true;
		document = object;
		this.resultTab = resultTab;
		init();
	}

	private void init() {
		initButtons();
		initEditor();
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
			logger.info("Updating document");
			resultTab.refreshData();
			hideModalDialog();
			destroy();
		}
	}
	
	//Save button onclick action
	private class SaveButton_OnClick implements EventHandler<ActionEvent> {
		public void handle(ActionEvent arg0) {
			logger.info("Saving document");
			resultTab.refreshData();
			hideModalDialog();
			destroy();
		}
	}
	
	private void initEditor() {
		String code = "";
		if(document != null) {
			try {
				JSONObject json = new JSONObject(document.toString());
				code = json.toString(2);
			}
			catch (Exception e) {
				logger.error(e.getMessage(),e);
			}
		}
		
		pane.prefHeightProperty().bind(getContent().heightProperty());
		pane.prefWidthProperty().bind(getContent().widthProperty());
		textArea.prefHeightProperty().bind(pane.heightProperty());
		textArea.prefWidthProperty().bind(pane.widthProperty());
		textArea.setWrapText(true);
		textArea.setText(code);
		pane.getChildren().add(textArea);
		setContent(pane);
		
	}
	
	@Override
	public void destroy() {
		pane.prefHeightProperty().unbind();
		pane.prefWidthProperty().unbind();
		textArea.prefHeightProperty().unbind();
		textArea.prefWidthProperty().unbind();
		super.destroy();
	}
}
