package com.droidpark.mongoui.task;

import com.droidpark.mongoui.component.JSRunnableTab;

import javafx.concurrent.Task;

public class CreateJSRunnableTabTask extends Task<JSRunnableTab> {

	@Override
	protected JSRunnableTab call() throws Exception {
		updateProgress(-1, 1);
		JSRunnableTab tab = new JSRunnableTab("JavaScript");
		updateProgress(1, 1);
		return tab;
	}

}
