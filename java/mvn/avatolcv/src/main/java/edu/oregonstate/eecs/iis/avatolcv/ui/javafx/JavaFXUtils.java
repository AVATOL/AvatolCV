package edu.oregonstate.eecs.iis.avatolcv.ui.javafx;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class JavaFXUtils {
	public static void dialog(String text){
		Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Error Dialog");
        alert.setHeaderText("AvatolCV error");
        alert.setContentText(text);
        alert.showAndWait();
	}
}
