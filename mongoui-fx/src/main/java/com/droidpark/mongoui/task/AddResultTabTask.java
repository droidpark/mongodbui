package com.droidpark.mongoui.task;

import com.droidpark.mongoui.component.ResultTab;
import com.droidpark.mongoui.util.Util;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.TabPane;

public class AddResultTabTask extends Task<Void> {

	private Task<ResultTab> resultTask;
	private TabPane tabPane;
	
	public AddResultTabTask(Task<ResultTab> resultTask, TabPane tabPane) {
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
						tabPane.getTabs().add(resultTask.get());
						tabPane.getSelectionModel().select(resultTask.get());
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
