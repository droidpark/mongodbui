package com.droidpark.mongoui.util;

import java.net.UnknownHostException;

import org.apache.log4j.Logger;

import com.mongodb.Mongo;
import com.mongodb.MongoException;

public class MongoUtil {

	static Logger logger = Logger.getLogger(MongoUtil.class);
	
	private static Mongo connection = null;
	private static String host;
	private static int port;
	
	public static void initConnection() throws Exception{ 
		connect();
	}
	
	public static void initConnection(String host, int port) throws Exception{
		MongoUtil.host = host;
		MongoUtil.port = port;
		connect();
	}
	
	private static void connect() throws Exception {
		connection = new Mongo(host, port);
	}
	
	public static Mongo getConnection() {
		return connection;
	}

	public static String getHost() {
		return host;
	}

	public static void setHost(String host) {
		MongoUtil.host = host;
	}

	public static int getPort() {
		return port;
	}

	public static void setPort(int port) {
		MongoUtil.port = port;
	}
}
