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
	
	public static void initConnection(){ 
		connect();
	}
	
	public static void initConnection(String host, int port){
		MongoUtil.host = host;
		MongoUtil.port = port;
		connect();
	}
	
	private static void connect(){
		try {
			connection = new Mongo(host, port);
			logger.info("Successfully connected to " + host + ".");
		} catch (UnknownHostException e) {
			logger.error(e.getMessage(),e);
		} catch (MongoException e) {
			logger.error(e.getMessage(),e);
		}
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
