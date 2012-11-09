package com.droidpark.mongoui.component;

import com.droidpark.mongoui.util.ImageUtil;

import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Tab;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

public class JSEditorTab extends Tab implements UITab {

	String script;
	String title;
	
	AnchorPane tabToolPane;
	TextArea textArea;
	
	public JSEditorTab(String title, String script) {
		this.title = title;
		this.script = script;
		init();
	}
	
	public void init() {
		initToolPane();
		initTextArea();
		BorderPane pane = new BorderPane();
		pane.setTop(tabToolPane);
		pane.setCenter(textArea);
		setContent(pane);
	}
	
	private void initTextArea() {
		textArea = new TextArea(script);
		setText(title);
	}
	
	private void initToolPane() {
		tabToolPane = new AnchorPane();
		tabToolPane = new AnchorPane();
		tabToolPane.getStyleClass().add("-mongoui-tab-toolpane");
		tabToolPane.setPrefHeight(30);
		HBox toolBox = new HBox();
		toolBox.setStyle("-fx-padding: 4px;");
		tabToolPane.getChildren().add(toolBox);
		initToolPaneButtons(toolBox);
	}
	
	private void initToolPaneButtons(HBox toolBox) {
		Button refresh = new Button("Refresh", new ImageView(ImageUtil.TB_DB_REFRESH_16_16));
		refresh.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
		toolBox.getChildren().add(refresh);
		
		Button save = new Button("Save", new ImageView(ImageUtil.TB_DB_SAVE_16_16));
		save.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
		toolBox.getChildren().add(save);
		
		Button remove = new Button("Remove", new ImageView(ImageUtil.TB_DB_REMOVE_16_16));
		remove.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
		toolBox.getChildren().add(remove);
	}

	public void destroy() {
		
	}
}
