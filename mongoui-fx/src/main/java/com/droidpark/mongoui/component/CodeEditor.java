package com.droidpark.mongoui.component;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import javafx.scene.layout.AnchorPane;
import javafx.scene.web.WebView;

public class CodeEditor extends AnchorPane {

	WebView webView;
	String code;
	
	public CodeEditor() {
		init();
	}
	
	private void init() {
		webView = new WebView();
		webView.prefHeightProperty().bind(heightProperty());
		webView.prefWidthProperty().bind(widthProperty());
		getChildren().add(webView);
		
	}
	
	public void loadCode(String code) {
		this.code = code;
		readTemplate();
	}
	
	private void readTemplate() {
		try {
			InputStream stream = getClass().getClassLoader().getResourceAsStream("codeeditor/template.html");
			URL css = getClass().getClassLoader().getResource("codeeditor/codemirror.css");
			URL js = getClass().getClassLoader().getResource("codeeditor/codemirror.js");
			URL cl = getClass().getClassLoader().getResource("codeeditor/javascript.js");
			
			BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
			StringBuilder builder = new StringBuilder();
			String line = null;
			while((line = reader.readLine()) != null) {
				builder.append(line);
			}
			String template = builder.toString();
			template = template.replace("${insertCode}", code);
			template = template.replace("${editorJS}", js.toString());
			template = template.replace("${editorType}", cl.toString());
			template = template.replace("${editorCSS}", css.toString());
			webView.getEngine().loadContent(template);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public String getValue() {
		return (String ) webView.getEngine().executeScript("editor.getValue();");
	}
	
	public void destroy() {
		webView.prefHeightProperty().unbind();
		webView.prefWidthProperty().unbind();
	}
}
