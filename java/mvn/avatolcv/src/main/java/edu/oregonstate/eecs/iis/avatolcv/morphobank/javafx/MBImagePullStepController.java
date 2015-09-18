package edu.oregonstate.eecs.iis.avatolcv.morphobank.javafx;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Alert.AlertType;
import edu.oregonstate.eecs.iis.avatolcv.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.core.ProgressPresenter;
import edu.oregonstate.eecs.iis.avatolcv.core.StepController;
import edu.oregonstate.eecs.iis.avatolcv.javafxui.AvatolCVJavaFXMB;
import edu.oregonstate.eecs.iis.avatolcv.morphobank.MBImagePullStep;
import edu.oregonstate.eecs.iis.avatolcv.morphobank.MorphobankSessionJavaFX;

public class MBImagePullStepController implements StepController, ProgressPresenter  {
    public static final String IMAGE_INFO_DOWNLOAD = "imageInfoDownload"; 
    public static final String IMAGE_FILE_DOWNLOAD = "imageFileDownload"; 
    public ProgressBar imageFileDownloadProgress;
    public Label imageFileDownloadMessage;
    public ProgressBar imageInfoDownloadProgress;
    public Label imageInfoDownloadMessage;
    private MBImagePullStep step;
    private String fxmlDocName;
    private MorphobankSessionJavaFX fxSession;
    
    public MBImagePullStepController(MorphobankSessionJavaFX fxSession, MBImagePullStep step, String fxmlDocName){
        this.step = step;
        this.fxmlDocName = fxmlDocName;
        this.fxSession = fxSession;
    }
    @Override
    public boolean consumeUIData() {
        // nothing to consume - just reporting progress
        return true;
    }

    @Override
    public void clearUIFields() {
        imageInfoDownloadProgress.setProgress(0.0);
        imageInfoDownloadMessage.setText("");
        
        imageFileDownloadProgress.setProgress(0.0);
        imageFileDownloadMessage.setText("");
    }

    @Override
    public Node getContentNode() throws AvatolCVException {
        try {
            System.out.println("trying to load" +  this.fxmlDocName);
            FXMLLoader loader = new FXMLLoader(this.getClass().getResource(this.fxmlDocName));
            loader.setController(this);
            Node content = loader.load();
            
            
            
            imageInfoDownloadProgress.setProgress(0.0);
            imageInfoDownloadMessage.setText("");
            
            imageFileDownloadProgress.setProgress(0.0);
            imageFileDownloadMessage.setText("");
            
            Task<Boolean> task = new ImageDownloadTask(this, this.step, IMAGE_INFO_DOWNLOAD, IMAGE_FILE_DOWNLOAD);
            /*
             * NOTE - wanted to use javafx properties and binding here but couldn't dovetail it in.  I could not put
             * the loops that do work in the call method of the Task (which manages updating on the JavaFX APp Thread), 
             * I had to manage this myself the old school way, to update the progress bar, by using Platform.runLater
             * 
             */
            //imageFileDownloadProgress.progressProperty().bind(fileDownloadPercentProperty);
            //imageInfoDownloadProgress.progressProperty().bind(infoDownloadPercentProperty);
            //imageFileDownloadMessage.textProperty().bind(fileMessageProperty);
            //imageInfoDownloadMessage.textProperty().bind(infoMessageProperty);
            new Thread(task).start();
            return content;
        }
        catch(IOException ioe){
            throw new AvatolCVException("problem loading ui " + fxmlDocName + " for controller " + this.getClass().getName());
        } 
    }
    
    public Node getContentNodeOld() throws AvatolCVException {
        try {
            System.out.println("trying to load" +  this.fxmlDocName);
            FXMLLoader loader = new FXMLLoader(this.getClass().getResource(this.fxmlDocName));
            loader.setController(this);
            Node content = loader.load();
            
            imageInfoDownloadProgress.setProgress(0.0);
            imageInfoDownloadMessage.setText("");
            
            imageFileDownloadProgress.setProgress(0.0);
            imageFileDownloadMessage.setText("");
            
            Task<Boolean> task = new ImageDownloadTask(this, this.step, IMAGE_INFO_DOWNLOAD, IMAGE_FILE_DOWNLOAD);
            /*
             * NOTE - wanted to use javafx properties and binding here but couldn't dovetail it in.  I could not put
             * the loops that do work in the call method of the Task (which manages updating on the JavaFX APp Thread), 
             * I had to manage this myself the old school way, to update the progress bar, by using Platform.runLater
             * 
             */
            //imageFileDownloadProgress.progressProperty().bind(fileDownloadPercentProperty);
            //imageInfoDownloadProgress.progressProperty().bind(infoDownloadPercentProperty);
            //imageFileDownloadMessage.textProperty().bind(fileMessageProperty);
            //imageInfoDownloadMessage.textProperty().bind(infoMessageProperty);
            new Thread(task).start();
            return content;
        }
        catch(IOException ioe){
            throw new AvatolCVException("problem loading ui " + fxmlDocName + " for controller " + this.getClass().getName());
        } 
    }
    @Override
    public void updateProgress(String processName, double percent) {
        ProgressUpdater pu = new ProgressUpdater(processName, percent);
        Platform.runLater(pu);
    }
    @Override
    public void setMessage(String processName, String m) {
        MessageUpdater mu = new MessageUpdater(processName,m);
        Platform.runLater(mu);
    }
    public class MessageUpdater implements Runnable {
        private String processName;
        private String message;
        public MessageUpdater(String processName, String message){
            this.processName = processName;
            this.message = message;
        }
        @Override
        public void run() {
            if (IMAGE_FILE_DOWNLOAD.equals(processName)){
                imageFileDownloadMessage.setText(message);
            }
            else if (IMAGE_INFO_DOWNLOAD.equals(processName)){
                imageInfoDownloadMessage.setText(message);
            }
        }
    }
    public class ProgressUpdater implements Runnable {
        private String processName;
        private double percent;
        public ProgressUpdater(String processName, double percent){
            this.processName = processName;
            this.percent = percent;
        }
        @Override
        public void run() {
        	if (IMAGE_FILE_DOWNLOAD.equals(processName)){
                System.out.println("should have setr progress to " + percent);
                imageFileDownloadProgress.setProgress((double)percent);
            }
            else if (IMAGE_INFO_DOWNLOAD.equals(processName)){
                System.out.println("should have setr progress to " + percent);
                imageInfoDownloadProgress.setProgress((double)percent);
            }
        }
    }
   
    public class ImageDownloadTask extends Task<Boolean> {
        private String processName1;
        private String processName2;
        private MBImagePullStep step;
        private MBImagePullStepController controller;
        private final Logger logger = LogManager.getLogger(ImageDownloadTask.class);
    	
        public ImageDownloadTask(MBImagePullStepController controller, MBImagePullStep step, String processName1, String processName2){
            this.controller = controller;
            this.step = step;
            this.processName1 = processName1;
            this.processName2 = processName2;
            
        }
        @Override
        protected Boolean call() throws Exception {
        	try {
        		this.step.downloadImageInfoForChosenCharactersAndView(this.controller, processName1);
        		this.step.downloadImagesForChosenCharactersAndView(this.controller, processName2);
        		NavButtonEnablerRunner runner = new NavButtonEnablerRunner();
        		Platform.runLater(runner);
        		return new Boolean(true);
        	}
        	catch(AvatolCVException ace){
        		logger.error("AvatolCV error downloading images");
        		logger.error(ace.getMessage());
        		System.out.println("AvatolCV error downloading images");
        		ace.printStackTrace();
        		return new Boolean(false);
        	}
        }
       
    }
    public class NavButtonEnablerRunner implements Runnable{
		@Override
		public void run() {
			fxSession.enableNavButtons();
		}
    	
    }
	@Override
	public boolean delayEnableNavButtons() {
		return true;
	}
   
}
