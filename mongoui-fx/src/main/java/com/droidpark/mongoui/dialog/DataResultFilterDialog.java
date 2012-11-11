package com.droidpark.mongoui.dialog;

import org.apache.log4j.Logger;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

import com.droidpark.mongoui.component.ModalDialog;
import com.droidpark.mongoui.component.ResultTab;
import com.droidpark.mongoui.util.ImageUtil;
import com.droidpark.mongoui.util.Language;
import com.mongodb.BasicDBObject;
import com.mongodb.util.JSON;

import static com.droidpark.mongoui.util.LanguageConstants.*;

public class DataResultFilterDialog extends ModalDialog {

	final Logger logger = Logger.getLogger(DataResultFilterDialog.class);
	
	ResultTab resultTab;
	final TextField queryText = new TextField();
	final TextField sortText = new TextField();
	final TextField skipText = new TextField();
	final TextField limitText = new TextField();
	
	public DataResultFilterDialog(ResultTab resultTab) {
		super(Language.get(DIALOG_TITLE_FILTER), 400, 150, ImageUtil.MD_DB_FILTER_24_24);
		this.resultTab = resultTab;
		init();
	}
	
	private void init() {
		final GridPane grid = new GridPane();
		grid.setStyle("-fx-padding: 10px;");
		//query
		Label queryLabel = new Label(Language.get(LABEL_QUERY) + ": ");
		queryLabel.setStyle("-fx-padding: 0px 10px 0px 0px;");
		grid.add(queryLabel, 0,0);
		grid.add(queryText, 1,0,5,1);
		
		//Sort
		Label sortLabel = new Label(Language.get(LABEL_SORT) + ": ");
		sortLabel.setStyle("-fx-padding: 0px 10px 0px 0px;");
		grid.add(sortLabel,0,1);
		sortText.setPrefWidth(100);
		grid.add(sortText, 1,1);
		
		//Skip
		Label skipLabel = new Label(Language.get(LABEL_SKIP) + ": ");
		skipLabel.setStyle("-fx-padding: 0px 10px 0px 20px;");
		grid.add(skipLabel, 2, 1);
		skipText.setText(resultTab.getDataSkipValue().toString());
		skipText.setPrefWidth(50);
		grid.add(skipText, 3, 1);
		
		//Limit
		Label limitLabel = new Label(Language.get(LABEL_LIMIT) + ": ");
		limitLabel.setStyle("-fx-padding: 0px 10px 0px 20px;");
		grid.add(limitLabel, 4, 1);
		limitText.setText(resultTab.getDataLimitValue().toString());
		limitText.setPrefWidth(50);
		grid.add(limitText, 5, 1);
		
		setContent(grid);
		
		Button cancelButton = new Button(Language.get(BUTTON_CANCEL));
		addNodeToFooter(cancelButton);
		cancelButton.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent arg0) {
				hideModalDialog();
			}
		});
		
		Button filterButton = new Button(Language.get(BUTTON_FILTER));
		addNodeToFooter(filterButton);
		filterButton.setOnAction(new FilterButton_OnClick());
	}
	
	//Filter Button OnClick Action
	private class FilterButton_OnClick implements EventHandler<ActionEvent> {
		public void handle(ActionEvent arg0) {
			try {
				resultTab.setQuery((BasicDBObject) JSON.parse(queryText.getText()));
				resultTab.setDataLimitValue(Integer.valueOf(limitText.getText()));
				resultTab.setDataSkipValue(Integer.valueOf(skipText.getText()));
				resultTab.refreshData();
				hideModalDialog();
			}
			catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
		}
	}
	
}
