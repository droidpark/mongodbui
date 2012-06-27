package com.droidpark.mongoui.util;

import java.text.SimpleDateFormat;
import java.util.Date;

import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.control.TextArea;
import javafx.scene.web.HTMLEditor;
import javafx.scene.web.WebEvent;
import javafx.scene.web.WebView;

public class ConsoleUtil {

	private static TextArea textArea = null;
	private static SimpleDateFormat formatter = null;
	
	private static StringBuffer consoleBuffer = new StringBuffer();
	private static String htmlBodyBegin = "<html><body style=\"padding:1px; margin:0px;\" onload=\"window.location.hash='consoleBottom'\">";
	private static String htmlBodyEnd = "</body></html>";
	private static WebView webView = null;
	
	
	static {
		formatter = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");;
		
	}
	
	public static void init(WebView view) {
		webView = view;
		webView.setStyle("-fx-padding:0px;");
	}
	
	public static void init(TextArea console) {
		textArea = console;
	}
	
	public static void echo(String text, ConsoleLabelEnum label) {
		String writeHtml = "<div style=\"background-color: #f1f1f1; padding: 2px; margin:2px; font-size: 9pt;\"><span style=\"font-weight:bold;\">[" 
				+ formatter.format(new Date())+ "]</span><span style=\"font-weight:bold;color:"+label.getColor()
				+";\">["+label.getName()+"]: </span> " + text + "</div>";
		consoleBuffer.append(writeHtml);
		Platform.runLater(new Runnable() {
			public void run() {
				String html = htmlBodyBegin + consoleBuffer.toString() + "<a name=\"consoleBottom\"></a>" + htmlBodyEnd;
				webView.getEngine().loadContent(html);
			}
		});
	}
	
}
