package com.droidpark.mongoui.component;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.droidpark.mongoui.util.Language;
import com.droidpark.mongoui.util.LanguageConstants;

import javafx.scene.control.Tab;
import javafx.scene.layout.AnchorPane;
import javafx.scene.web.WebView;

public class MongoUITab extends Tab implements UITab {

	AnchorPane pane;
	WebView webView;
	
	public MongoUITab() {
		init();
	}
	
	private void init() {
		pane = new AnchorPane();
		webView = new WebView();
		webView.prefHeightProperty().bind(pane.heightProperty());
		webView.prefWidthProperty().bind(pane.widthProperty());
		pane.getChildren().add(webView);
		setContent(pane);
		setText(Language.get(LanguageConstants.TAB_LABEL_WELCOME));
		loadContent();
	}
	
	private void loadContent() {
		String page = getStream("mongoui/welcome.html");
		webView.getEngine().loadContent(page);
	}
	
	private String getStream(String file) {
		try {
			InputStream stream = getClass().getClassLoader().getResourceAsStream(file);
			BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
			StringBuilder builder = new StringBuilder();
			String line = null;
			while((line = reader.readLine()) != null) {
				builder.append(line);
			}
			return builder.toString();
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public void destroy() {
		webView.prefHeightProperty().unbind();
		webView.prefWidthProperty().unbind();
	}

}
