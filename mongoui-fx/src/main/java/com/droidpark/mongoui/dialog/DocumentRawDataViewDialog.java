package com.droidpark.mongoui.dialog;

import static com.droidpark.mongoui.util.LanguageConstants.BUTTON_OK;
import static com.droidpark.mongoui.util.LanguageConstants.DIALOG_TITLE_RAW_DATA;

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
import com.mongodb.DBObject;

public class DocumentRawDataViewDialog extends ModalDialog {

	Logger logger = Logger.getLogger(DocumentRawDataViewDialog.class);
	
	DBObject document;
	ResultTab resultTab;
	AnchorPane pane = new AnchorPane();
	CodeEditor editor = new CodeEditor();
	
	public DocumentRawDataViewDialog(DBObject document, ResultTab resultTab) {
		super(Language.get(DIALOG_TITLE_RAW_DATA), 400, 200, ImageUtil.MD_DB_DOCUMENT_24_24);
		this.resultTab = resultTab;
		this.document = document;
		codeEditorInit();
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
		
		Button okButton = new Button(Language.get(BUTTON_OK));
		addNodeToFooter(okButton);
		okButton.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent arg0) {
				hideModalDialog();
				destroy();
			}
		});
		
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
		editor.prefHeightProperty().unbind();
		editor.prefWidthProperty().unbind();
		editor.destroy();
		super.destroy();
	}
	
}
