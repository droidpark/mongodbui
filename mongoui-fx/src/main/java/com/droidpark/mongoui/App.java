package com.droidpark.mongoui;

import org.apache.log4j.Logger;

import com.droidpark.mongoui.form.MainForm;
import com.droidpark.mongoui.util.Language;

import javafx.application.Application;

/**
 * MongoUI Launcher
 * @author Kamil Ors
 *
 */

public class App {
	
	private static Logger logger = Logger.getLogger(App.class);
	
    public static void main( String[] args ) {
    	logger.info("MongoUI initializing...");
    	Language.init();
    	logger.info("MongoUI initilazed.");
    	Application.launch(MainForm.class, args);
    }
}
