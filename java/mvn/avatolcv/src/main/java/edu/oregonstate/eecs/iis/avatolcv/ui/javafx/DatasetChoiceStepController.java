package edu.oregonstate.eecs.iis.avatolcv.ui.javafx;

import java.io.IOException;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import edu.oregonstate.eecs.iis.avatolcv.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.javafxui.AvatolCVExceptionExpresserJavaFX;
import edu.oregonstate.eecs.iis.avatolcv.session.ProgressPresenter;
import edu.oregonstate.eecs.iis.avatolcv.session.SessionInfo;
import edu.oregonstate.eecs.iis.avatolcv.session.StepController;
import edu.oregonstate.eecs.iis.avatolcv.steps.DatasetChoiceStep;
import edu.oregonstate.eecs.iis.avatolcv.steps.ScoringConcernStep;
import edu.oregonstate.eecs.iis.avatolcv.ui.javafx.ScoringConcernStepController.RemainingMetadataDownloadTask;

public class DatasetChoiceStepController implements StepController {
    public static final String SCORING_INFO_DOWNLOAD = "scoringInfoDownload"; 
    public Label datasetTitleName;
    public VBox datasetChoiceVBox;
    public ProgressBar scoringInfoDownloadProgressBar;
    public Label scoringInfoDownloadMessageLabel;
    public ComboBox<String> selectedDataset;
    private DatasetChoiceStep step;
    private String fxmlDocName;
    private List<String> datasetNames = null;
    private boolean followUpDataDownloadPhaseComplete = false;
	public DatasetChoiceStepController(DatasetChoiceStep step, String fxmlDocName){
		this.step = step;
		this.fxmlDocName = fxmlDocName;
	}
	@Override
	public boolean consumeUIData() {
		try {
			String chosenDataset = (String)this.selectedDataset.getValue();
			this.step.setChosenDataset(chosenDataset);
			Hashtable<String, String> answerHash = new Hashtable<String, String>();
			answerHash.put("chosenDataset", chosenDataset);
			this.step.saveAnswers(answerHash);
			this.step.consumeProvidedData();
			return true;
		}
		catch (Exception e){
            AvatolCVExceptionExpresserJavaFX.instance.showException(e, "problem choosing dataset.");
            return false;
        }
	}

	@Override
	public void clearUIFields() {
	    selectedDataset.setValue(datasetNames.get(0));
	}

	@Override
	public Node getContentNode() throws AvatolCVException {
		try {
        	System.out.println("trying to load" +  this.fxmlDocName);
            FXMLLoader loader = new FXMLLoader(this.getClass().getResource(this.fxmlDocName));
            loader.setController(this);
            Node content = loader.load();
            
            this.datasetNames = this.step.getAvailableDatasets();
            if (datasetNames.size() < 1){
            	throw new AvatolCVException("no valid matrices detected.");
            }
            Collections.sort(datasetNames);
            ObservableList<String> list = selectedDataset.getItems();
    		for (String m : datasetNames){
    			list.add(m);
    		}
            if (this.step.hasPriorAnswers()){
            	followUpDataDownloadPhaseComplete = false;
            	Hashtable<String, String> hash = this.step.getPriorAnswers();
            	String choice = hash.get("chosenDataset");
            	selectedDataset.setValue(choice);
            }
            else {
        		selectedDataset.setValue(datasetNames.get(0));
            }
            datasetTitleName.setText(this.step.getDatasetTitleText());
            return content;
        }
        catch(IOException ioe){
            throw new AvatolCVException("problem loading ui " + fxmlDocName + " for controller " + this.getClass().getName());
        } 
	}
	@Override
	public boolean delayEnableNavButtons() {
		return false;
	}
    @Override
    public void executeFollowUpDataLoadPhase() throws AvatolCVException {
        ProgressPresenterImpl pp = new ProgressPresenterImpl();
        pp.connectProcessNameToLabel(SCORING_INFO_DOWNLOAD, scoringInfoDownloadMessageLabel);
        pp.connectProcessNameToProgressBar(SCORING_INFO_DOWNLOAD,scoringInfoDownloadProgressBar);
        Task<Boolean> task = new ScoringMetadataDownloadTask(pp, this.step, SCORING_INFO_DOWNLOAD);
        new Thread(task).start();
    }
    public class ScoringMetadataDownloadTask extends Task<Boolean> {
        private String processName1;
        private DatasetChoiceStep step;
        private ProgressPresenter pp;
        private final Logger logger = LogManager.getLogger(RemainingMetadataDownloadTask.class);
        
        public ScoringMetadataDownloadTask(ProgressPresenter pp, DatasetChoiceStep step, String processName1){
            this.pp = pp;
            this.step = step;
            this.processName1 = processName1;
        }
        @Override
        protected Boolean call() throws Exception {
            try {

                this.step.loadPrimaryMetadataForChosenDataset(this.pp, processName1);
                followUpDataDownloadPhaseComplete = true;
                return new Boolean(true);
            }
            catch(AvatolCVException ace){
                AvatolCVExceptionExpresserJavaFX.instance.showException(ace, "AvatolCV error downloading scoring data info");
                return new Boolean(false);
            }
        }
    }
    @Override
    public void configureUIForFollowUpDataLoadPhase() {
        datasetChoiceVBox.getChildren().clear();
        datasetChoiceVBox.setSpacing(10);
        Region regionTop = new Region();
        VBox.setVgrow(regionTop, Priority.ALWAYS);
        datasetChoiceVBox.getChildren().add(regionTop);
        // add text about doing best to detect presence/absence
        Label header = new Label("downloading scoring info metadata");
        //header.setPrefWidth(100);
        header.setWrapText(true);
        // add a grid layout
        datasetChoiceVBox.getChildren().add(header);
        
        scoringInfoDownloadProgressBar = new ProgressBar(0.0);
        scoringInfoDownloadProgressBar.setMinWidth(400);
        datasetChoiceVBox.getChildren().add(scoringInfoDownloadProgressBar);
        
        scoringInfoDownloadMessageLabel = new Label("");
        datasetChoiceVBox.getChildren().add(scoringInfoDownloadMessageLabel);
        
        Region regionBottom = new Region();
        VBox.setVgrow(regionBottom, Priority.ALWAYS);
        datasetChoiceVBox.getChildren().add(regionBottom);
    }
    @Override
    public boolean isFollowUpDataLoadPhaseComplete() {
        return followUpDataDownloadPhaseComplete;
    }
}
