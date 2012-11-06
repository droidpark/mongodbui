package com.droidpark.mongoui.task;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

import com.droidpark.mongoui.component.UITab;
import com.droidpark.mongoui.util.Util;

public class AddTabTask extends Task<Void> {

	private Task<?> resultTask;
	private TabPane tabPane;
	
	public AddTabTask(Task<?> resultTask, TabPane tabPane) {
		this.resultTask = resultTask;
		this.tabPane = tabPane;
	}
	
	@Override
	protected Void call() throws Exception {
		while(true) {
			if(resultTask.isDone()) {
				Platform.runLater(new Runnable() { 
				public void run() {
					try {
						final Tab tab = (Tab) (Tab)resultTask.get();
						tab.setOnClosed(new EventHandler<Event>() {
							public void handle(Event arg0) {
								tabPane.getTabs().remove(tab);
								((UITab)tab).destroy();
							}
						});
						tabPane.getTabs().add(tab);
						tabPane.getSelectionModel().select(tab);
					}
					catch (Exception e) {
						e.printStackTrace();
					}
				}});
				break;
			}
			Util.sleepThread(100);
		}
		return null;
	}
}
