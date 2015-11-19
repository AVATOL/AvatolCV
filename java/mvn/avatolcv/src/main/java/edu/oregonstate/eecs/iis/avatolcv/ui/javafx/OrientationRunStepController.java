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
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import edu.oregonstate.eecs.iis.avatolcv.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.algorithm.OutputMonitor;
import edu.oregonstate.eecs.iis.avatolcv.core.StepController;
import edu.oregonstate.eecs.iis.avatolcv.javafxui.AvatolCVExceptionExpresserJavaFX;
import edu.oregonstate.eecs.iis.avatolcv.steps.OrientationRunStep;

public class OrientationRunStepController implements StepController, OutputMonitor {
    public static final String RUN_ORIENTATION = "run orientation";
    public static final String NL = System.getProperty("line.separator");
    private OrientationRunStep step = null;
    private String fxmlDocName = null;
    public TextArea outputText = null;
    public Label algName = null;
    public Button cancelAlgorithmButton = null;
    private JavaFXStepSequencer fxSession = null;
    public OrientationRunStepController(JavaFXStepSequencer fxSession, OrientationRunStep step, String fxmlDocName){
        this.step = step;
        this.fxmlDocName = fxmlDocName;
        this.fxSession = fxSession;
    }
    @Override
    public boolean consumeUIData() {
        return true;
    }

    @Override
    public void clearUIFields() {
        // TODO Auto-generated method stub

    }
    @SuppressWarnings("unchecked")
    @Override
    public Node getContentNode() throws AvatolCVException {
        try {
            //System.out.println("trying to load" +  this.fxmlDocName);
            FXMLLoader loader = new FXMLLoader(this.getClass().getResource(this.fxmlDocName));
            loader.setController(this);
            Node content = loader.load();
            String algName = this.step.getSelectedOrientationAlgorithm();
            this.algName.setText(algName);
            this.outputText.setText("Starting...");
            Task<Boolean> task = new RunOrientationTask(this, this.step, RUN_ORIENTATION);
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
            throw new AvatolCVException("problem loading ui " + fxmlDocName + " for controller " + this.getClass().getName() + " : " + ioe.getMessage());
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
    public class PostOrientationUIAdjustments implements Runnable{
        @Override
        public void run() {
            fxSession.enableNavButtons();
        }
        
    }
    public class RunOrientationTask extends Task<Boolean> {
        private String processName;
        private OrientationRunStep step;
        private OrientationRunStepController controller;
        private final Logger logger = LogManager.getLogger(RunOrientationTask.class);
        
        public RunOrientationTask(OrientationRunStepController controller, OrientationRunStep step, String processName){
            this.controller = controller;
            this.step = step;
            this.processName = processName;
        }
        @Override
        protected Boolean call() throws Exception {
            try {
                this.step.runOrientation(this.controller, processName);
                PostOrientationUIAdjustments runner = new PostOrientationUIAdjustments();
                Platform.runLater(runner);
                
                return new Boolean(true);
            }
            //catch(AvatolCVException ace){
            catch(Exception ace){    
                logger.error("AvatolCV error running algorithm");
                logger.error(ace.getMessage());
                System.out.println("AvatolCV error running algorithm");
                ace.printStackTrace();
                AvatolCVExceptionExpresserJavaFX.instance.showException(ace, "problem running algorithm");
                return new Boolean(false);
            }
        }
       
    }
   
    @Override
    public void acceptOutput(String s) {
        System.out.println("OUTPUT MONITOR : " + s);
        MessageUpdater mu = new MessageUpdater(s);
        Platform.runLater(mu);
    }
    
    public class MessageUpdater implements Runnable {
        private String message;
        public MessageUpdater(String message){
            this.message = message;
        }
        @Override
        public void run() {
            outputText.appendText(message + NL);
        }
    }
    public void cancelAlgorithm(){
        System.out.println("heard cancel");
        this.step.cancelOrientation();
    }
}
