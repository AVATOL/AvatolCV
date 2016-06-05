package edu.oregonstate.eecs.iis.avatolcv.ui.javafx;

import java.util.List;

import edu.oregonstate.eecs.iis.avatolcv.session.DataIssue;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.VBox;

public class JavaFXUtils {
	public static void dialog(String text){
		Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Error Dialog");
        alert.setHeaderText("AvatolCV error");
        alert.setContentText(text);
        alert.showAndWait();
	}
	
	public static TextArea getIssueText(DataIssue di, int issueNumber){
    	TextArea ta = new TextArea();
    	ta.setCache(false);// blurriness bug
    	ta.setText("" + di.getIssueText(issueNumber));
    	return ta;
    }
	public static void populateIssues(VBox vBox, List<DataIssue> dataIssues){
		vBox.getChildren().clear();
		for (int i = 0; i < dataIssues.size() ; i++){
	    	DataIssue di = dataIssues.get(i);
	    	TextArea ta = JavaFXUtils.getIssueText(di, i+1);
	    	vBox.getChildren().add(ta);
	    }
	}
	public static void clearIssues(VBox vBox){
		vBox.getChildren().clear();
	}
}
