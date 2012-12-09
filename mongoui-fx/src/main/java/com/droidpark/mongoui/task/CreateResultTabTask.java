package com.droidpark.mongoui.task;

import org.apache.log4j.Logger;

import com.droidpark.mongoui.component.ResultTab;
import com.droidpark.mongoui.dialog.ConnectionDialog;
import com.droidpark.mongoui.util.Util;
import com.mongodb.Mongo;
import com.mongodb.MongoException;

import javafx.concurrent.Task;

public class CreateResultTabTask extends Task<ResultTab> {

	Logger logger = Logger.getLogger(CreateResultTabTask.class);
	
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
			return tab;
		}
		catch (MongoException e) {
			logger.error("MongoException: " + e.getMessage());
			throw e;
		}				
		catch (Exception e) {
			logger.error("Exception: " + e.getMessage(),e);
			throw e;
		}
		finally {
			updateProgress(1, 1);
		}
	}

}
