package edu.oregonstate.eecs.iis.avatolcv.ui.javafx;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import edu.oregonstate.eecs.iis.avatolcv.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.core.ProgressPresenter;
import edu.oregonstate.eecs.iis.avatolcv.core.StepController;
import edu.oregonstate.eecs.iis.avatolcv.javafxui.AvatolCVExceptionExpresserJavaFX;
import edu.oregonstate.eecs.iis.avatolcv.steps.ImagePullStep;
import edu.oregonstate.eecs.iis.avatolcv.steps.SegmentationRunStep;
import edu.oregonstate.eecs.iis.avatolcv.ui.javafx.ImagePullStepController.ImageDownloadTask;
import edu.oregonstate.eecs.iis.avatolcv.ui.javafx.SegmentationConfigurationStepController.AlgChangeListener;

public class SegmentationRunStepController implements StepController, ProgressPresenter {
    public static final String RUN_SEGMENTATION = "run segmentation";
    private SegmentationRunStep step = null;
    private String fxmlDocName = null;
    public Label segmentationStatus = null;
    public Label segAlgName = null;
    private JavaFXStepSequencer fxSession = null;
    public SegmentationRunStepController(JavaFXStepSequencer fxSession, SegmentationRunStep step, String fxmlDocName){
        this.step = step;
        this.fxmlDocName = fxmlDocName;
        this.fxSession = fxSession;
    }
    @Override
    public boolean consumeUIData() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void clearUIFields() {
        // TODO Auto-generated method stub

    }

    @Override
    public Node getContentNode() throws AvatolCVException {
        try {
            //System.out.println("trying to load" +  this.fxmlDocName);
            FXMLLoader loader = new FXMLLoader(this.getClass().getResource(this.fxmlDocName));
            loader.setController(this);
            Node content = loader.load();
            String algName = this.step.getSelectedSegmentationAlgorithm();
            this.segAlgName.setText(algName);
            this.segmentationStatus.setText("Starting...");
            Task<Boolean> task = new RunSegmentationTask(this, this.step, RUN_SEGMENTATION);
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
    public boolean delayEnableNavButtons() {
        return true;
    }

    @Override
    public void executeFollowUpDataLoadPhase() throws AvatolCVException {
        // TODO Auto-generated method stub

    }

    @Override
    public void configureUIForFollowUpDataLoadPhase() {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean isFollowUpDataLoadPhaseComplete() {
        // TODO Auto-generated method stub
        return false;
    }
    public class NavButtonEnablerRunner implements Runnable{
        @Override
        public void run() {
            fxSession.enableNavButtons();
        }
        
    }
    public class RunSegmentationTask extends Task<Boolean> {
        private String processName;
        private SegmentationRunStep step;
        private SegmentationRunStepController controller;
        private final Logger logger = LogManager.getLogger(RunSegmentationTask.class);
        
        public RunSegmentationTask(SegmentationRunStepController controller, SegmentationRunStep step, String processName){
            this.controller = controller;
            this.step = step;
            this.processName = processName;
        }
        @Override
        protected Boolean call() throws Exception {
            try {
                this.step.runSegmentation(this.controller, processName);
                NavButtonEnablerRunner runner = new NavButtonEnablerRunner();
                Platform.runLater(runner);
                
                return new Boolean(true);
            }
            //catch(AvatolCVException ace){
            catch(Exception ace){    
                logger.error("AvatolCV error downloading images");
                logger.error(ace.getMessage());
                System.out.println("AvatolCV error downloading images");
                ace.printStackTrace();
                AvatolCVExceptionExpresserJavaFX.instance.showException(ace, "problem downloading image");
                return new Boolean(false);
            }
        }
       
    }
    @Override
    public void updateProgress(String processName, double percentDone) {
        // not relevant for this
    }
    @Override
    public void setMessage(String processName, String m) {
        // TODO Auto-generated method stub
        
    }
    /*
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
        }
        */
}
