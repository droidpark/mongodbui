package com.droidpark.mongoui.component;

import com.droidpark.mongoui.util.ImageUtil;
import com.droidpark.mongoui.util.Language;
import com.droidpark.mongoui.util.LanguageConstants;
import com.droidpark.mongoui.util.MongoUtil;

import javafx.application.Platform;
import javafx.geometry.Orientation;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

public class JSRunnableTab extends Tab implements UITab {

	AnchorPane tabToolPane;
	AnchorPane footerPane;
	SplitPane splintPane;
	CodeEditor editor;
	CodeEditor result;
	
	public JSRunnableTab(String title) {
		setText(title);
		init();
	}
	
	public void init() {
		Platform.runLater(new Runnable() {
			public void run() {
				initToolBar();
				initContent();
				initFooterBar();
				initLayout();
			}
		});
	}
	
	public void initToolBar() {
		tabToolPane = new AnchorPane();
		tabToolPane.getStyleClass().add("-mongoui-tab-toolpane");
		tabToolPane.setPrefHeight(30);
		HBox toolBox = new HBox();
		toolBox.setStyle("-fx-padding: 4px;");
		tabToolPane.getChildren().add(toolBox);
		initToolPaneButtons(toolBox);
	}
	
	private void initToolPaneButtons(HBox toolBox) {
		Label label = new Label(Language.get(LanguageConstants.LABEL_DATABASE) + ": ");
		label.setStyle("-fx-padding:2px;");
		toolBox.getChildren().add(label);
		
		ComboBox<String> databaseBox = new ComboBox<String>();
		databaseBox.getItems().addAll(MongoUtil.getConnection().getDatabaseNames());
		databaseBox.setPrefWidth(100);
		toolBox.getChildren().add(databaseBox);
		
		Button run = new Button("Run", new ImageView(ImageUtil.TB_DB_RUN_16_16));
		run.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
		toolBox.getChildren().add(run);
		
		Button stop = new Button("Stop", new ImageView(ImageUtil.TB_DB_STOP_16_16));
		stop.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
		toolBox.getChildren().add(stop);
	}
	
	public void initContent() {
		splintPane = new SplitPane();
		splintPane.setOrientation(Orientation.HORIZONTAL);
		editor = new CodeEditor();
		editor.loadCode("");
		result = new CodeEditor();
		result.loadCode("");
		splintPane.getItems().add(editor);
		splintPane.getItems().add(result);
	}
	
	public void initFooterBar() {
		
	}
	
	public void initLayout() {
		BorderPane border = new BorderPane();
		border.setTop(tabToolPane);
		border.setBottom(footerPane);
		border.setCenter(splintPane);
		setContent(border);
	}
	
	public void destroy() {
		
	} 
}
