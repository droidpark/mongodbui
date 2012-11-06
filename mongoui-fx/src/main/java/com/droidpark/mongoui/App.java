package com.droidpark.mongoui;

import com.droidpark.mongoui.form.MainForm;
import com.droidpark.mongoui.util.Language;

import javafx.application.Application;

/**
 * MongoUI Launcher
 * @author Kamil Ors
 *
 */

public class App {
    public static void main( String[] args ) {
    	Language.init();
    	Application.launch(MainForm.class, args);
    }
}
