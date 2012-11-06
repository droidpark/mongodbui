package com.droidpark.mongoui.util;

import java.util.Properties;

public class Language {

	private static Properties properties = null;
	
	public static void init() {
		try {
			properties = new Properties();
			properties.load(ClassLoader.getSystemResourceAsStream("english.lang.properties"));
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static String get(String key) {
		return properties.getProperty(key);
	}
}
