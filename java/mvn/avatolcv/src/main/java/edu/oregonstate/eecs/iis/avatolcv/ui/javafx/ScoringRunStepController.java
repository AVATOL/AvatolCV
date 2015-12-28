package edu.oregonstate.eecs.iis.avatolcv.ui.javafx;

import java.io.File;
import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.Alert.AlertType;
import edu.oregonstate.eecs.iis.avatolcv.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.AvatolCVFileSystem;
import edu.oregonstate.eecs.iis.avatolcv.algorithm.OutputMonitor;
import edu.oregonstate.eecs.iis.avatolcv.javafxui.AvatolCVExceptionExpresserJavaFX;
import edu.oregonstate.eecs.iis.avatolcv.session.StepController;
import edu.oregonstate.eecs.iis.avatolcv.steps.ScoringRunStep;

public class ScoringRunStepController implements StepController, OutputMonitor{
    public static final String RUN_SCORING = "run scoring";
    public static final String FILESEP = System.getProperty("file.separator");
    public static final String NL = System.getProperty("line.separator");
    private ScoringRunStep step = null;
    private String fxmlDocName = null;
    public TextArea outputText = null;
    public Label algName = null;
    public Button cancelAlgorithmButton = null;
    private JavaFXStepSequencer fxSession = null;
    public ScoringRunStepController(JavaFXStepSequencer fxSession, ScoringRunStep step, String fxmlDocName){
        this.step = step;
        this.fxmlDocName = fxmlDocName;
        this.fxSession = fxSession;
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

    @Override
    public boolean consumeUIData() {
        try {
            this.step.generateRunSummaries();
        }
        catch(AvatolCVException ace){
            AvatolCVExceptionExpresserJavaFX.instance.showException(ace, "problem generating runSummary: " + ace.getMessage());
        }
        return true;
    }

    @Override
    public void clearUIFields() {
        // TODO Auto-generated method stub
        
    }
    public boolean useRunConfig() throws AvatolCVException {
        String path = AvatolCVFileSystem.getDatasetDir() + FILESEP + "skipRunConfigForScoringON.txt";
        File f = new File(path);
        if (f.exists()){
            return false;
        }
        return true;
    }
    @Override
    public Node getContentNode() throws AvatolCVException {
        try {
            //System.out.println("trying to load" +  this.fxmlDocName);
            FXMLLoader loader = new FXMLLoader(this.getClass().getResource(this.fxmlDocName));
            loader.setController(this);
            Node content = loader.load();
            String algName = this.step.getSelectedScoringAlgorithm();
            this.algName.setText(algName);
            this.outputText.setText("Starting...");
            boolean useRunConfig = useRunConfig();
            if (!useRunConfig){
                Alert alert = new Alert(AlertType.ERROR);
                alert.setTitle("Error Dialog");
                alert.setHeaderText("DEBUGGING MODE ENGAGED");
                alert.setContentText("Scoring will run without the runConfig file being processed.  To disable, rename " + AvatolCVFileSystem.getDatasetDir() + "skipRunConfigForScoringON.txt to OFF.txt");
                alert.showAndWait();
                
            }
            Task<Boolean> task = new RunScoringTask(this, this.step, RUN_SCORING, useRunConfig);
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
    public class PostScoringUIAdjustments implements Runnable{
        @Override
        public void run() {
            fxSession.enableNavButtons();
        }
        
    }
    public class RunScoringTask extends Task<Boolean> {
        private String processName;
        private ScoringRunStep step;
        private ScoringRunStepController controller;
        private final Logger logger = LogManager.getLogger(RunScoringTask.class);
        private boolean useRunConfig = true;

        public RunScoringTask(ScoringRunStepController controller, ScoringRunStep step, String processName, boolean useRunConfig){
            this.controller = controller;
            this.step = step;
            this.processName = processName;
            this.useRunConfig = useRunConfig;

        }
        @Override
        protected Boolean call() throws Exception {
            try {
                if (this.useRunConfig){
                    this.step.runScoring(this.controller, processName, true);
                    PostScoringUIAdjustments runner = new PostScoringUIAdjustments();
                    Platform.runLater(runner);
                    return new Boolean(true);
                }
                else {
                    this.step.runScoring(this.controller, processName, false);
                    PostScoringUIAdjustments runner = new PostScoringUIAdjustments();
                    Platform.runLater(runner);
                    return new Boolean(true);
                }
                
            }
            //catch(AvatolCVException ace){
            catch(Exception e){    
                logger.error("AvatolCV error running algorithm");
                logger.error(e.getMessage());
                System.out.println("AvatolCV error running algorithm");
                e.printStackTrace();
                AvatolCVExceptionExpresserJavaFX.instance.showException(e, "problem running algorithm");
                return new Boolean(false);
            }
        }
       
    }
    @Override
    public boolean delayEnableNavButtons() {
        // NA
        return false;
    }

    @Override
    public void executeFollowUpDataLoadPhase() throws AvatolCVException {
        // NA
        
    }

    @Override
    public void configureUIForFollowUpDataLoadPhase() {
        // NA
        
    }

    @Override
    public boolean isFollowUpDataLoadPhaseComplete() {
        // NA
        return false;
    }
    public void cancelAlgorithm(){
        System.out.println("heard cancel");
        this.step.cancelScoring();
    }
}
