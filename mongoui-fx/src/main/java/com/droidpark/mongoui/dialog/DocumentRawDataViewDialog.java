package com.droidpark.mongoui.dialog;

import static com.droidpark.mongoui.util.LanguageConstants.BUTTON_OK;
import static com.droidpark.mongoui.util.LanguageConstants.DIALOG_TITLE_RAW_DATA;
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

public class DocumentRawDataViewDialog extends ModalDialog {

	DBObject document;
	ResultTab resultTab;
	AnchorPane pane = new AnchorPane();
	TextArea textArea = new TextArea();
	
	public DocumentRawDataViewDialog(DBObject document, ResultTab resultTab) {
		super(Language.get(DIALOG_TITLE_RAW_DATA), 400, 200, ImageUtil.MD_DB_DOCUMENT_24_24);
		this.resultTab = resultTab;
		this.document = document;
		init();
	}

	private void init() {
		
		pane.prefHeightProperty().bind(getContent().heightProperty());
		pane.prefWidthProperty().bind(getContent().widthProperty());
		textArea.prefHeightProperty().bind(pane.heightProperty());
		textArea.prefWidthProperty().bind(pane.widthProperty());
		textArea.setWrapText(true);
		pane.getChildren().add(textArea);
		setContent(pane);
		
		if(document != null) { 
			textArea.setText(document.toString());
		}
		
		Button okButton = new Button(Language.get(BUTTON_OK));
		addNodeToFooter(okButton);
		okButton.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent arg0) {
				hideModalDialog();
				destroy();
			}
		});
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
