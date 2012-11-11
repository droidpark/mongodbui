package com.droidpark.mongoui.dialog;

import org.apache.log4j.Logger;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;

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
	
	public EditDocumentDialog(ResultTab resultTab) {
		super(Language.get(DIALOG_TITLE_ADD_DOCUMENT), 400, 250, ImageUtil.MD_DB_ADD_24_24);
		this.resultTab = resultTab;
		init();
	}
	
	public EditDocumentDialog(DBObject object, ResultTab resultTab) {
		super(Language.get(DIALOG_TITLE_EDIT_DOCUMENT), 400, 250, ImageUtil.MD_DB_EDIT_24_24);
		this.managed = true;
		document = object;
		this.resultTab = resultTab;
		init();
	}

	private void init() {
		initButtons();
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
			destroy();
		}
	}
	
	//Save button onclick action
	private class SaveButton_OnClick implements EventHandler<ActionEvent> {
		public void handle(ActionEvent arg0) {
			logger.info("Saving document");
			resultTab.refreshData();
			destroy();
		}
	}
	
	@Override
	public void destroy() {
		super.destroy();
	}
}
