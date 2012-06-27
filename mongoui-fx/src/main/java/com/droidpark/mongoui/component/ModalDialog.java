package com.droidpark.mongoui.component;


import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

public class ModalDialog {

	public static AnchorPane MAIN_FRAME;
	private AnchorPane shadowPane;
	private AnchorPane main;
	private AnchorPane pane;
	
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
		pane.prefWidthProperty().bind(MAIN_FRAME.widthProperty());
		pane.prefHeightProperty().bind(MAIN_FRAME.heightProperty());
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
		main.setLayoutX((pane.getPrefWidth() / 2) - (width / 2));
		main.setLayoutY(100);
		main.setStyle("-fx-background-color: #f1f1f1");
		pane.getChildren().add(main);
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
		borderPane.setBottom(footer);
	}
	
	private void initContent() {
		content = new AnchorPane();
		content.getStyleClass().add("-mongoui-modal-content");
		AnchorPane contentWrap = new AnchorPane();
		contentWrap.getChildren().add(content);
		borderPane.setCenter(contentWrap);
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
		MAIN_FRAME.getChildren().add(pane);
		pane.setVisible(true);
	}

	public void hideModalDialog() {
		MAIN_FRAME.getChildren().remove(pane);
		pane.setVisible(false);
	}
	
	class Delta {double x, y;}
}
