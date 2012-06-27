package com.droidpark.mongoui.util;

public enum DBTreeEnum {
	
	NULL("NULL"),
	COLLECTION("Collections"),
	JAVASCRIPT("Stored JScripts"); 
	
	private String name;
	
	DBTreeEnum(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public static DBTreeEnum find(String name) {
		DBTreeEnum item = NULL;
		for(DBTreeEnum field : DBTreeEnum.values()) {
			if(field.getName().equalsIgnoreCase(name)) {
				item = field; break;
			}
		}
		return item;
	}
}
