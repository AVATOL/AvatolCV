package edu.oregonstate.eecs.iis.avatolcv.ui.javafx;

import java.util.List;

import edu.oregonstate.eecs.iis.avatolcv.session.DataIssue;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

public class JavaFXUtils {
	public static void dialog(String text){
		Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Error Dialog");
        alert.setHeaderText("AvatolCV error");
        alert.setContentText(text);
        alert.showAndWait();
	}
	public static Label getIssueText(DataIssue di, int issueNumber){
		Label label = new Label();
    	label.setText("" + di.getIssueText(issueNumber));
    	label.getStyleClass().add("issueText");
    	label.setPadding(new Insets(0,0,0,10));
    	//ta.setWrapText(true);
    	//ta.setPrefRowCount(3);
    	return label;
    }
	/*
	public static AnchorPane getIssueText(DataIssue di, int issueNumber){
		AnchorPane ap = new AnchorPane();
    	TextArea ta = new TextArea();
    	ap.getChildren().add(label);
    	AnchorPane.setBottomAnchor(ta, 0.0);
    	AnchorPane.setTopAnchor(ta, 0.0);
    	AnchorPane.setRightAnchor(ta, 0.0);
    	AnchorPane.setLeftAnchor(ta, 0.0);
    	ta.setCache(false);// blurriness bug
    	ta.setText("" + di.getIssueText(issueNumber));
    	ta.setStyle("-fx-border-color: black;");
    	ta.setWrapText(true);
    	ta.setPrefRowCount(3);
    	return ap;
    }
    */
	public static void populateIssues(List<DataIssue> dataIssues){
		VBox vBox = JavaFXStepSequencer.vBoxDataIssuesSingleton;
		vBox.getChildren().clear();
		for (int i = 0; i < dataIssues.size() ; i++){
	    	DataIssue di = dataIssues.get(i);
	    	Label label = JavaFXUtils.getIssueText(di, i+1);
	    	vBox.getChildren().add(label);
	    }
		String s = dataIssues.size() + " issues detected in the data - see Issues pane";
		if (dataIssues.size() == 0){
			if (JavaFXStepSequencer.issueCountLabelSingleton.getStyleClass().contains("issueAlert")){
				JavaFXStepSequencer.issueCountLabelSingleton.getStyleClass().remove("issueAlert");
			}
		}
		else {
			if (!JavaFXStepSequencer.issueCountLabelSingleton.getStyleClass().contains("issueAlert")){
				JavaFXStepSequencer.issueCountLabelSingleton.getStyleClass().add("issueAlert");
			}
		}
		JavaFXStepSequencer.issueCountLabelSingleton.setText(s);
	}
	public static void clearIssues(VBox vBox){
		vBox.getChildren().clear();
		JavaFXStepSequencer.issueCountLabelSingleton.setText("");
		if (JavaFXStepSequencer.issueCountLabelSingleton.getStyleClass().contains("issueAlert")){
			JavaFXStepSequencer.issueCountLabelSingleton.getStyleClass().remove("issueAlert");
		}
	}
}
