package com.droidpark.mongoui.util;

import javafx.application.Platform;
import javafx.scene.control.TextArea;

import org.apache.log4j.WriterAppender;
import org.apache.log4j.spi.LoggingEvent;

public class Log4jTextAreaAppender extends WriterAppender {

	static TextArea textArea = null;
	
	public static void initTextArea(TextArea area) {
		Log4jTextAreaAppender.textArea = area;
	}
	
	public void append(LoggingEvent loggingEvent) {
		final String message = this.layout.format(loggingEvent);
		if(textArea != null) {
			Platform.runLater(new Runnable() {
				public void run() {
					textArea.appendText(message);
				}
			});
		}
	}
	
}
