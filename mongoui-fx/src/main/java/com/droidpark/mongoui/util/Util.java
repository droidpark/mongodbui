package com.droidpark.mongoui.util;

import javafx.scene.layout.AnchorPane;

public class Util {

	public static String DEFAULT_STYLE = "/style/dynamic/";
	public static AnchorPane MAIN_FRAME;
	
	//Sleep Thread
	public static void sleepThread(long millis) {
		try {
			Thread.sleep(millis);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
