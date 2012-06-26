package com.droidpark.mongoui.task;

import com.droidpark.mongoui.component.ResultTab;
import com.droidpark.mongoui.util.Util;

import javafx.concurrent.Task;

public class CreateResultTabTask extends Task<ResultTab> {

	private String collection;
	private String database;
	
	public CreateResultTabTask(String collection, String database) {
		this.collection = collection;
		this.database = database;
	}
	
	@Override
	protected ResultTab call() throws Exception {
		try {
			updateProgress(-1, 1);
			ResultTab tab = new ResultTab(collection, database);
			updateProgress(1, 1);
			return tab;
		 }
		 catch (Exception e) {
			e.printStackTrace();
			 return null;
		 }
	}

}
