package edu.oregonstate.eecs.iis.avatolcv.ui.javafx;

import edu.oregonstate.eecs.iis.avatolcv.session.DataIssue;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.control.Alert.AlertType;

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
}
