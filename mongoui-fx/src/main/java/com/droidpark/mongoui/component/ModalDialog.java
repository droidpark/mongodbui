package com.droidpark.mongoui.component;


import static com.droidpark.mongoui.util.LanguageConstants.BUTTON_OK;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

import com.droidpark.mongoui.util.ImageUtil;
import com.droidpark.mongoui.util.Language;
import com.droidpark.mongoui.util.Util;

public class ModalDialog {

	public static final int OK = 0;
	public static final int WARN = 1;
	public static final int ERROR = 2;
	public static final int QUESTION = 3;
	
	private AnchorPane shadowPane;
	private AnchorPane main;
	private AnchorPane pane;
	ScrollPane scrollPane;
	
	BorderPane borderPane;
	private HBox header;
	private AnchorPane content;
	private HBox footer;
	
	private Image icon = null;
	private String title;
	private double width;
	private double height;
	
	public ModalDialog(final String title, final double width, final double height) {
		this.title = title;
		this.width = width;
		this.height = height;
		init();
	}
	
	public ModalDialog(final String title, final double width, final double height, Image icon) {
		this.icon = icon;
		this.title = title;
		this.width = width;
		this.height = height;
		this.icon = icon;
		init();
	}
	
	private void init() {
		initDiaglog();
		initBorderPane();
		initHeader();
		initContent();
		initFooter();
	}
	
	private void initDiaglog() {
		pane = new AnchorPane();
		pane.prefWidthProperty().bind(Util.MAIN_FRAME.widthProperty());
		pane.prefHeightProperty().bind(Util.MAIN_FRAME.heightProperty());
		pane.setVisible(false);
		pane.getStyleClass().add("-mongoui-modal");
		shadowPane = new AnchorPane();
		shadowPane.setOpacity(0.6);
		shadowPane.setStyle("-fx-background-color: #000");
		shadowPane.prefWidthProperty().bind(pane.widthProperty());
		shadowPane.prefHeightProperty().bind(pane.heightProperty());
		pane.getChildren().add(shadowPane);
		
		main = new AnchorPane();
		main.getStyleClass().add("-mongoui-modal");
		main.setPrefSize(width, height);
		main.setStyle("-fx-background-color: #f1f1f1");
		pane.getChildren().add(main);
		
		initLocation();
	}
	
	private void initLocation() {
		main.setLayoutX((pane.getPrefWidth() / 2) - (width / 2));
		main.setLayoutY((pane.getPrefHeight() / 2) - (height / 2));
	}

	private void initBorderPane() {
		borderPane = new BorderPane();
		borderPane.prefHeightProperty().bind(main.heightProperty());
		borderPane.prefWidthProperty().bind(main.widthProperty());
		main.getChildren().add(borderPane);
		
	}
	
	private void initHeader() {
		header = new HBox();
		header.setPrefHeight(45);
		header.getStyleClass().add("-mongoui-modal-header");
		borderPane.setTop(header);
		
		if(icon != null) {
			header.getChildren().add(new ImageView(icon));
		}
		
		Label label = new Label(title);
		label.getStyleClass().add("-mongoui-modal-header-label");
		header.getChildren().add(label);
		initHeaderDrag();
	}
	
	private void initFooter() {
		footer = new HBox();
		footer.setPrefHeight(30);
		footer.getStyleClass().add("-mongoui-modal-footer");
		footer.setAlignment(Pos.CENTER_RIGHT);
		footer.setSpacing(5);
		borderPane.setBottom(footer);
	}
	
	private void initContent() {
		AnchorPane anchorPane = new AnchorPane();
		
		content = new AnchorPane();
		content.getStyleClass().add("-mongoui-modal-content");
		
		scrollPane = new ScrollPane();
		scrollPane.getStyleClass().add("-mongoui-modal-scrollpane");
		scrollPane.setContent(content);
		scrollPane.prefHeightProperty().bind(anchorPane.heightProperty());
		scrollPane.prefWidthProperty().bind(anchorPane.widthProperty());
		
		anchorPane.getChildren().add(scrollPane);
		borderPane.setCenter(anchorPane);
	}
	
	private void initHeaderDrag() {
		final Delta delta = new Delta();
		header.setOnMousePressed(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent event) {
				delta.x = main.getLayoutX() - event.getScreenX();
				delta.y = main.getLayoutY() - event.getScreenY();
			}
		});
		
		header.setOnMouseDragged(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent event) {
				main.setLayoutX(event.getScreenX() + delta.x);
				main.setLayoutY(event.getScreenY() + delta.y);
			}
		});
	}
	
	public void addNodeToFooter(Node node) {
		footer.getChildren().add(node);
	}
	
	public void setContent(Node node) {
		content.getChildren().clear();
		content.getChildren().add(node);
	}
	
	public void showModalDialog() {
		Platform.runLater(new Runnable() {
			public void run() {
				Util.MAIN_FRAME.getChildren().add(pane);
				initLocation();
				pane.setVisible(true);
			}
		});
	}

	public void hideModalDialog() {
		Platform.runLater(new Runnable() {
			public void run() {
				Util.MAIN_FRAME.getChildren().remove(pane);
				pane.setVisible(false);
			}
		});
	}
	
	public static ModalDialog createMessageDialog(String title, String message, int severity) {
		final ModalDialog dialog = new ModalDialog(title, 350, 150, getDialogIcon(severity));
		Label label = new Label(message);
		label.setStyle("-fx-padding: 10px;");
		dialog.setContent(label);
		Button okButton = new Button(Language.get(BUTTON_OK));
		dialog.addNodeToFooter(okButton);
		okButton.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent arg0) {
				dialog.hideModalDialog();
				dialog.destroy();
			}
		});
		return dialog;
	}
	
	private static Image getDialogIcon(int severity) {
		switch (severity) {
			case OK: return ImageUtil.MD_DB_OK_24_24;
			case WARN: return ImageUtil.MD_DB_WARN_24_24;
			case ERROR: return ImageUtil.MD_DB_ERROR_24_24;
			case QUESTION: return ImageUtil.MD_DB_QUESTION_24_24;
			default: return ImageUtil.MD_DB_DATABASE_24_24;
		}
	}
	
	public void destroy() {
		pane.prefWidthProperty().unbind();
		pane.prefHeightProperty().unbind();
		shadowPane.prefWidthProperty().unbind();
		shadowPane.prefHeightProperty().unbind();
		borderPane.prefHeightProperty().unbind();
		borderPane.prefWidthProperty().unbind();
		scrollPane.prefHeightProperty().unbind();
		scrollPane.prefWidthProperty().unbind();
	}
	
	class Delta {double x, y;}
}
