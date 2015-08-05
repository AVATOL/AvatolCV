package edu.oregonstate.eecs.iis.avatolcv.javafxui;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import edu.oregonstate.eecs.iis.avatolcv.core.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.core.AvatolCVExceptionExpresser;

public class AvatolCVExceptionExpresserJavaFX implements
        AvatolCVExceptionExpresser {

    @Override
    public void showException(AvatolCVException e, String header) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Error Dialog");
        alert.setHeaderText(header);
        alert.setContentText(e.getMessage());
        alert.showAndWait();

    }

    @Override
    public void showException(Exception e, String header) {
        System.out.println(e.getMessage());
        e.printStackTrace();
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Error Dialog");
        alert.setHeaderText(header);
        alert.setContentText(e.getMessage());
        alert.showAndWait();

        
    }
    
}
