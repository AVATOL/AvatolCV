package edu.oregonstate.eecs.iis.avatolcv.ui.javafx;

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
import edu.oregonstate.eecs.iis.avatolcv.steps.Z_Obsolete_ScoringConcernDataPullStep;

public class Z_Obsolete_ScoringConcernDataPullStepController implements StepController  {
    public static final String SCORING_INFO_DOWNLOAD = "scoringInfoDownload"; 
   // public ProgressBar imageFileDownloadProgress;
    //public Label imageFileDownloadMessage;
    public ProgressBar scoringInfoDownloadProgress;
    public Label scoringInfoDownloadMessage;
    private Z_Obsolete_ScoringConcernDataPullStep step;
    private String fxmlDocName;
    private JavaFXStepSequencer fxStepSequencer;
    
    public Z_Obsolete_ScoringConcernDataPullStepController(JavaFXStepSequencer fxStepSequencer, Z_Obsolete_ScoringConcernDataPullStep step, String fxmlDocName){
        this.step = step;
        this.fxmlDocName = fxmlDocName;
        this.fxStepSequencer = fxStepSequencer;
    }
    @Override
    public boolean consumeUIData() {
        // nothing to consume - just reporting progress
        return true;
    }

    @Override
    public void clearUIFields() {
        scoringInfoDownloadProgress.setProgress(0.0);
        scoringInfoDownloadMessage.setText("");
        
    }

    @Override
    public Node getContentNode() throws AvatolCVException {
        try {
            System.out.println("trying to load" +  this.fxmlDocName);
            FXMLLoader loader = new FXMLLoader(this.getClass().getResource(this.fxmlDocName));
            loader.setController(this);
            Node content = loader.load();
            scoringInfoDownloadProgress.setProgress(0.0);
            scoringInfoDownloadMessage.setText("");
            ProgressPresenterImpl pp = new ProgressPresenterImpl();
            pp.connectProcessNameToLabel(SCORING_INFO_DOWNLOAD, scoringInfoDownloadMessage);
            pp.connectProcessNameToProgressBar(SCORING_INFO_DOWNLOAD,scoringInfoDownloadProgress);
            Task<Boolean> task = new ScoringInfoDownloadTask(pp, this.step, SCORING_INFO_DOWNLOAD);
            
            new Thread(task).start();
            return content;
        }
        catch(IOException ioe){
            throw new AvatolCVException("problem loading ui " + fxmlDocName + " for controller " + this.getClass().getName());
        } 
    }
   
    public class ScoringInfoDownloadTask extends Task<Boolean> {
        private String processName1;
        private Z_Obsolete_ScoringConcernDataPullStep step;
        private ProgressPresenter pp;
        private final Logger logger = LogManager.getLogger(ScoringInfoDownloadTask.class);
    	
        public ScoringInfoDownloadTask(ProgressPresenter pp, Z_Obsolete_ScoringConcernDataPullStep step, String processName1){
            this.pp = pp;
            this.step = step;
            this.processName1 = processName1;
            
        }
        @Override
        protected Boolean call() throws Exception {
        	try {
        		this.step.loadPrimaryMetadataForChosenDataset(this.pp, processName1);
        		NavButtonEnablerRunner runner = new NavButtonEnablerRunner();
        		Platform.runLater(runner);
        		return new Boolean(true);
        	}
        	catch(AvatolCVException ace){
        		logger.error("AvatolCV error downloading scoring data info");
        		logger.error(ace.getMessage());
        		System.out.println("AvatolCV error downloading scoring data info");
        		ace.printStackTrace();
        		return new Boolean(false);
        	}
        }
    }
    public class NavButtonEnablerRunner implements Runnable{
		@Override
		public void run() {
			fxStepSequencer.enableNavButtons();
		}
    	
    }
	@Override
	public boolean delayEnableNavButtons() {
		return true;
	}
    @Override
    public void executeFollowUpDataLoadPhase() throws AvatolCVException {
     // nothing to be done
    }
    @Override
    public void configureUIForFollowUpDataLoadPhase() {
        //nothing to be done
    }
    @Override
    public boolean isFollowUpDataLoadPhaseComplete() {
        // not relevant
        return true;
    }
   
}
