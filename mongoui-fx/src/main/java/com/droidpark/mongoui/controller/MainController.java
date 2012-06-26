package com.droidpark.mongoui.controller;

import com.droidpark.mongoui.component.ResultTab;

import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TabPane;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.MouseEvent;

public class MainController {

	/**
	 * Database TreeView onclick Action
	 * @param treeView
	 * @param tabPane
	 * @param progressBar
	 * @return
	 */
	public EventHandler<MouseEvent> treeViewOnMouseClicked(final TreeView<String> treeView, final TabPane tabPane, 
			final ProgressBar progressBar) {
		return new EventHandler<MouseEvent>() {
			public void handle(MouseEvent event) {
				final TreeItem<String> item = treeView.getSelectionModel().getSelectedItem();
				if(item.getParent().getValue().equalsIgnoreCase("Collections")) {
					new Thread(new Task<Void>() {
						 @Override public Void call() {
							 progressBar.setProgress(-1);
							 ResultTab tab = new ResultTab(item.getValue(), item.getParent().getParent().getValue());
							 tabPane.getTabs().add(tab);
							 tabPane.getSelectionModel().select(tab);
							 progressBar.setProgress(0);
							 return null;
						 }
					}).run();
				}
			}
		};
	}
	
}
