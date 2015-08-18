package edu.oregonstate.eecs.iis.avatolcv.javafxui;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import edu.oregonstate.eecs.iis.avatolcv.core.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.core.AvatolCVExceptionExpresser;
import edu.oregonstate.eecs.iis.avatolcv.morphobank.javafx.MBImagePullStepController.ProgressUpdater;

public class AvatolCVExceptionExpresserJavaFX implements
        AvatolCVExceptionExpresser {

    @Override
    public void showException(AvatolCVException e, String header) {
        System.out.println(e.getMessage());
        e.printStackTrace();
        Expresser expresser = new Expresser(e, header);
        Platform.runLater(expresser);
    }

    @Override
    public void showException(Exception e, String header) {
        System.out.println(e.getMessage());
        e.printStackTrace();
        Expresser expresser = new Expresser(e, header);
        Platform.runLater(expresser);
    }
    
    public class Expresser implements Runnable {
        private Exception exception;
        private String header;
        public Expresser(Exception e, String header){
            this.exception = e;
            this.header = header;
        }
        @Override
        public void run() {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error Dialog");
            alert.setHeaderText(this.header);
            alert.setContentText(this.exception.getMessage());
            alert.showAndWait();
            
        }
        
    }
}
