package edu.oregonstate.eecs.iis.avatolcv.javafxui;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import edu.oregonstate.eecs.iis.avatolcv.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.AvatolCVExceptionExpresser;

public class AvatolCVExceptionExpresserJavaFX implements
        AvatolCVExceptionExpresser {
    private static final Logger logger = LogManager.getLogger(AvatolCVExceptionExpresserJavaFX.class);
    private static final String NL = System.getProperty("line.separator");
    public static AvatolCVExceptionExpresser instance = new AvatolCVExceptionExpresserJavaFX();
    @Override
    public void showException(AvatolCVException e, String header) {
        logException(e);
        System.out.println(e.getMessage());
        e.printStackTrace();
        Expresser expresser = new Expresser(e, header);
        Platform.runLater(expresser);
    }
    @Override
    public void showException(Exception e, String header) {
        System.out.println(e.getMessage());
        logException(e);
        e.printStackTrace();
        Expresser expresser = new Expresser(e, header);
        Platform.runLater(expresser);
    }
    private void logException(Exception e){
        logger.error(e.getMessage());
        StringBuilder sb = new StringBuilder();
        sb.append("Exception int thread " + Thread.currentThread().getName() + " " + e.getClass().getName() + NL);
        StackTraceElement[] st = e.getStackTrace();
        for (StackTraceElement element : st){
            String className = element.getClassName();
            String methodName = element.getMethodName();
            int lineNumber = element.getLineNumber();
            String filename = element.getFileName();
            String s = "       at " + className + "." + methodName + "(" + filename + "." + lineNumber + ")" + NL;
            sb.append(s);
        }
        logger.error("" + sb);
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
