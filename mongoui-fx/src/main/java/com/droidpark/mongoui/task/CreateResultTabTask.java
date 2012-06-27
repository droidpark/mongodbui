package com.droidpark.mongoui.task;

import com.droidpark.mongoui.component.ResultTab;
import com.droidpark.mongoui.util.Util;
import com.mongodb.Mongo;

import javafx.concurrent.Task;

public class CreateResultTabTask extends Task<ResultTab> {

	private Mongo mongo;
	private String collection;
	private String database;
	
	public CreateResultTabTask(String collection, String database, Mongo mongo) {
		this.collection = collection;
		this.database = database;
		this.mongo = mongo;
	}
	
	@Override
	protected ResultTab call() throws Exception {
		try {
			updateProgress(-1, 1);
			ResultTab tab = new ResultTab(collection, database, mongo);
			updateProgress(1, 1);
			return tab;
		 }
		 catch (Exception e) {
			e.printStackTrace();
			 return null;
		 }
	}

}
