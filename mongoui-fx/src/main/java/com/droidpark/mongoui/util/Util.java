package com.droidpark.mongoui.util;

public class Util {

	
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
