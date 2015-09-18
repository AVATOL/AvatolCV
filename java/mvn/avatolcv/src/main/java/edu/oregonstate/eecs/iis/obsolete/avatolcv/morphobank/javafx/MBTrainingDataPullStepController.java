package edu.oregonstate.eecs.iis.obsolete.avatolcv.morphobank.javafx;

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
import edu.oregonstate.eecs.iis.obsolete.avatolcv.morphobank.MBImagePullStep;
import edu.oregonstate.eecs.iis.obsolete.avatolcv.morphobank.MBTrainingDataPullStep;
import edu.oregonstate.eecs.iis.obsolete.avatolcv.morphobank.MorphobankSessionJavaFX;

public class MBTrainingDataPullStepController implements StepController, ProgressPresenter  {
    public static final String TRAINING_DATA_DOWNLOAD = "trainingDataDownload"; 
    public static final String ANNOTATION_DATA_DOWNLOAD = "annotatoinDataDownload"; 
    public ProgressBar trainingDataDownloadProgress;
    public Label trainingDataDownloadMessage;
    public ProgressBar annotationDataDownloadProgress;
    public Label annotationDataDownloadMessage;
    private MBTrainingDataPullStep step;
    private String fxmlDocName;
    private MorphobankSessionJavaFX fxSession;
    
    public MBTrainingDataPullStepController(MorphobankSessionJavaFX fxSession, MBTrainingDataPullStep step, String fxmlDocName){
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
        trainingDataDownloadProgress.setProgress(0.0);
        trainingDataDownloadMessage.setText("");
        
        annotationDataDownloadProgress.setProgress(0.0);
        annotationDataDownloadMessage.setText("");
    }

    @Override
    public Node getContentNode() throws AvatolCVException {
        try {
            System.out.println("trying to load" +  this.fxmlDocName);
            FXMLLoader loader = new FXMLLoader(this.getClass().getResource(this.fxmlDocName));
            loader.setController(this);
            Node content = loader.load();
            
            
            
            trainingDataDownloadProgress.setProgress(0.0);
            trainingDataDownloadMessage.setText("");
            
            annotationDataDownloadProgress.setProgress(0.0);
            annotationDataDownloadMessage.setText("");
            
            Task<Boolean> task = new TrainingDataDownloadTask(this, this.step, TRAINING_DATA_DOWNLOAD, ANNOTATION_DATA_DOWNLOAD);
            /*
             * NOTE - wanted to use javafx properties and binding here but couldn't dovetail it in.  I could not put
             * the loops that do work in the call method of the Task (which manages updating on the JavaFX APp Thread), 
             * I had to manage this myself the old school way, to update the progress bar, by using Platform.runLater
             * 
             */
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
            if (ANNOTATION_DATA_DOWNLOAD.equals(processName)){
                annotationDataDownloadMessage.setText(message);
            }
            else if (TRAINING_DATA_DOWNLOAD.equals(processName)){
                trainingDataDownloadMessage.setText(message);
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
        	if (ANNOTATION_DATA_DOWNLOAD.equals(processName)){
                //System.out.println("should have setr progress to " + percent);
                annotationDataDownloadProgress.setProgress((double)percent);
            }
            else if (TRAINING_DATA_DOWNLOAD.equals(processName)){
                //System.out.println("should have setr progress to " + percent);
                trainingDataDownloadProgress.setProgress((double)percent);
            }
        }
    }
   
    public class TrainingDataDownloadTask extends Task<Boolean> {
        private String processName1;
        private String processName2;
        private MBTrainingDataPullStep step;
        private MBTrainingDataPullStepController controller;
        private final Logger logger = LogManager.getLogger(TrainingDataDownloadTask.class);
    	
        public TrainingDataDownloadTask(MBTrainingDataPullStepController controller, MBTrainingDataPullStep step, String processName1, String processName2){
            this.controller = controller;
            this.step = step;
            this.processName1 = processName1;
            this.processName2 = processName2;
            
        }
        @Override
        protected Boolean call() throws Exception {
        	try {
        		this.step.downloadTrainingData(this.controller, processName1);
        		this.step.downloadAnnotationData(this.controller, processName2);
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
