package com.droidpark.mongoui.util;

public enum ConsoleLabelEnum {
	MONGO_UI("MongoUI", "blue"),
	QUERY("Query", "green"),
	ERROR("Error","red");
	
	private String name;
	private String color;
	ConsoleLabelEnum(String name, String color) {
		this.name = name;
		this.color = color;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getColor() {
		return color;
	}
	public void setColor(String color) {
		this.color = color;
	}
}
