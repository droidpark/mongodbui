package com.droidpark.mongoui.component;


import static com.droidpark.mongoui.util.LanguageConstants.*;
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
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;

import com.droidpark.mongoui.util.ImageUtil;
import com.droidpark.mongoui.util.Language;
import com.droidpark.mongoui.util.Util;

public class ModalDialog {

	public static final int OK = 0;
	public static final int WARN = 1;
	public static final int ERROR = 2;
	public static final int QUESTION = 3;
	
	AnchorPane shadowPane;
	AnchorPane main;
	AnchorPane pane;
	
	BorderPane borderPane;
	AnchorPane headerWrap;
	HBox header;
	AnchorPane content;
	AnchorPane footerWrap;
	HBox footer;
	
	Image icon = null;
	String title;
	double width;
	double height;
	boolean fullScreen = false;
	
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
		headerWrap = new AnchorPane();
		headerWrap.getStyleClass().add("-mongoui-modal-header");
		AnchorPane pane = new AnchorPane();
		headerWrap.getChildren().add(pane);
		header = new HBox();
		header.setStyle("-fx-padding: 10px;");
		pane.setPrefHeight(45);
		
		pane.getChildren().add(header);
		borderPane.setTop(headerWrap);
		
		if(icon != null) {
			header.getChildren().add(new ImageView(icon));
		}
		
		Label label = new Label(title);
		label.getStyleClass().add("-mongoui-modal-header-label");
		header.getChildren().add(label);
		initHeaderDrag();
		initFullScreenSupport();
	}
	
	private void initFooter() {
		footerWrap = new AnchorPane();
		footerWrap.getStyleClass().add("-mongoui-modal-footer");
		AnchorPane pane = new AnchorPane();
		footerWrap.getChildren().add(pane);
		pane.setPrefHeight(35);
		footer = new HBox();
		footer.setStyle("-fx-padding: 6px;");
		footer.setAlignment(Pos.CENTER_RIGHT);
		footer.setSpacing(5);
		footer.prefWidthProperty().bind(footerWrap.widthProperty());
		pane.getChildren().add(footer);
		borderPane.setBottom(footerWrap);
		initResizeEvent();
	}
	
	private void initContent() {
		AnchorPane anchorPane = new AnchorPane();
		anchorPane.setMaxSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);
		content = new AnchorPane();
		content.getStyleClass().add("-mongoui-modal-content");
		
		content.prefHeightProperty().bind(anchorPane.heightProperty());
		content.prefWidthProperty().bind(anchorPane.widthProperty());
		content.setMaxSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);
		anchorPane.getChildren().add(content);
		borderPane.setCenter(anchorPane);
	}
	
	private void initHeaderDrag() {
		final Delta delta = new Delta();
		headerWrap.setOnMousePressed(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent event) {
				delta.x = main.getLayoutX() - event.getScreenX();
				delta.y = main.getLayoutY() - event.getScreenY();
			}
		});
		
		headerWrap.setOnMouseDragged(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent event) {
				main.setLayoutX(event.getScreenX() + delta.x);
				main.setLayoutY(event.getScreenY() + delta.y);
			}
		});
	}
	
	private void initResizeEvent() {
		final Delta delta = new Delta();
		final Delta size = new Delta();
		footerWrap.setOnMousePressed(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent event) {
				delta.x = event.getScreenX();
				delta.y = event.getScreenY();
				size.x = main.getWidth();
				size.y = main.getHeight();
			}
		});
		footerWrap.setOnMouseDragged(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent event) {
				double w = size.x + (event.getScreenX() - delta.x);
				double h = size.y + (event.getScreenY() - delta.y);
				if(h < height) h = height;
				if(w < width) w = width;
				main.setPrefSize(w, h);
			}
		});
	}
	
	private void initFullScreenSupport() {
		headerWrap.setOnMouseClicked(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent event) {
				if(event.getButton().equals(MouseButton.PRIMARY) && event.getClickCount() >= 2) {
					if(!fullScreen) {
						main.setLayoutX(25);
						main.setLayoutY(25);
						main.setPrefWidth(Util.MAIN_FRAME.getWidth() - 50);
						main.setPrefHeight(Util.MAIN_FRAME.getHeight() - 50);
						fullScreen = true;
					}
					else {
						fullScreen = false;
						main.setPrefSize(width, height);
						initLocation();
					}
				}
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
	
	public static ModalDialog createYesNoQuestionDialog(String title, String message) {
		final ModalDialog dialog = new ModalDialog(title, 350, 150, getDialogIcon(QUESTION));
		Label label = new Label(message);
		label.setStyle("-fx-padding: 10px;");
		dialog.setContent(label);
		Button noButton = new Button(Language.get(BUTTON_NO));
		dialog.addNodeToFooter(noButton);
		noButton.setOnAction(new EventHandler<ActionEvent>() {
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
		content.prefHeightProperty().unbind();
		content.prefWidthProperty().unbind();
		footer.prefWidthProperty().unbind();
	}
	
	public AnchorPane getContent() {
		return this.content;
	}
	
	class Delta {double x, y;}
}
