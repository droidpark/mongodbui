package com.droidpark.mongoui.component;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import javafx.scene.layout.AnchorPane;
import javafx.scene.web.WebView;

public class CodeEditor extends AnchorPane {

	URL editorCss, editorJs, colorJs, 
		simpleJs, hintJs, hintCss;
	
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
			initResources();
			String template = getStream("codeeditor/template.html");
			template = template.replace("${insertCode}", code);
			template = template.replace("${editorJS}", editorJs.toString());
			template = template.replace("${editorType}", colorJs.toString());
			template = template.replace("${editorCSS}", editorCss.toString());
			template = template.replace("${hintCSS}", hintCss.toString());
			template = template.replace("${simpleHint}", simpleJs.toString());
			template = template.replace("${jsHint}", hintJs.toString());
			webView.getEngine().loadContent(template);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
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
	
	private void initResources() {
		editorJs = getResourceFile("codeeditor/codemirror.js");
		editorCss = getResourceFile("codeeditor/codemirror.css");
		colorJs = getResourceFile("codeeditor/javascript.js");
		simpleJs = getResourceFile("codeeditor/simple-hint.js");
		hintJs = getResourceFile("codeeditor/javascript-hint.js");
		hintCss = getResourceFile("codeeditor/simple-hint.css");
	}
	
	private URL getResourceFile(String path) {
		try {
			return ClassLoader.getSystemClassLoader().getResource(path);
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
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
