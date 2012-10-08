package com.droidpark.mongoui.task;

import com.droidpark.mongoui.component.JSEditorTab;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;

import javafx.concurrent.Task;

public class CreateJSEditorTab extends Task<JSEditorTab> {

	private Mongo mongo;
	private String database;
	private String stored;
	
	public CreateJSEditorTab(String stored,String database, Mongo mongo) {
		this.mongo = mongo;
		this.database = database;
		this.stored = stored;
	}
	
	@Override
	protected JSEditorTab call() throws Exception {
		try {
			updateProgress(-1, 1);
			JSEditorTab tab = null;
			DB db = mongo.getDB(database);
			DBCollection collection = db.getCollection("system.js");
			DBCursor cursor =  collection.find(new BasicDBObject("_id", stored));
			if(cursor.hasNext()) {
				DBObject obj = cursor.next();
				tab = new JSEditorTab(database + "."+stored, obj.get("value").toString());
			}
			return tab;
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		finally {
			updateProgress(1, 1);
		}
	}

}
