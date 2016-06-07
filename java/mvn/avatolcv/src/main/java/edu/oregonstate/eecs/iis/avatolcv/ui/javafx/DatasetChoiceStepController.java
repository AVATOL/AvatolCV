package edu.oregonstate.eecs.iis.avatolcv.ui.javafx;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import edu.oregonstate.eecs.iis.avatolcv.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.datasource.DataSource;
import edu.oregonstate.eecs.iis.avatolcv.datasource.FileSystemDataSource;
import edu.oregonstate.eecs.iis.avatolcv.javafxui.AvatolCVExceptionExpresserJavaFX;
import edu.oregonstate.eecs.iis.avatolcv.session.DatasetInfo;
import edu.oregonstate.eecs.iis.avatolcv.session.ProgressPresenter;
import edu.oregonstate.eecs.iis.avatolcv.session.SessionInfo;
import edu.oregonstate.eecs.iis.avatolcv.session.StepController;
import edu.oregonstate.eecs.iis.avatolcv.steps.DatasetChoiceStep;
import edu.oregonstate.eecs.iis.avatolcv.steps.ScoringConcernStep;
import edu.oregonstate.eecs.iis.avatolcv.steps.ScoringConfigurationStep;
import edu.oregonstate.eecs.iis.avatolcv.ui.javafx.ScoringConcernStepController.RemainingMetadataDownloadTask;
import edu.oregonstate.eecs.iis.avatolcv.ui.javafx.ScoringConfigurationStepController.GroupChangeListener;

public class DatasetChoiceStepController implements StepController {
    public static final String SCORING_INFO_DOWNLOAD = "scoringInfoDownload"; 
    public Label datasetTitleName;
    public VBox datasetChoiceVBox;
    public CheckBox reloadDatasetCheckbox = null;
    public ProgressBar scoringInfoDownloadProgressBar;
    public Label scoringInfoDownloadMessageLabel;
    //public TextField datasetIdTextField;
    public ComboBox<String> selectedDataset;
    private DatasetChoiceStep step;
    private String fxmlDocName;
    private boolean followUpDataDownloadPhaseComplete = false;
    private List<DatasetInfo> datasetInfos = new ArrayList<DatasetInfo>();
    //private List<String> datasetNames = new ArrayList<String>();
    //private List<String> datasetIDs = new ArrayList<String>();
    private int selectedDatasetIndex = 0;
	public DatasetChoiceStepController(DatasetChoiceStep step, String fxmlDocName){
		this.step = step;
		this.fxmlDocName = fxmlDocName;
	}
	@Override
	public boolean consumeUIData() {
		try {
			this.step.setChosenDataset(datasetInfos.get(selectedDatasetIndex));
			this.step.setRefreshFromDatasourceNeeded(this.reloadDatasetCheckbox.selectedProperty().getValue());
			Hashtable<String, String> answerHash = new Hashtable<String, String>();
			answerHash.put("chosenDatasetIndex", "" + this.selectedDatasetIndex);
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
	    selectedDataset.setValue(datasetInfos.get(0).getName());
	}

	private void resetDatasetInfo() throws AvatolCVException {
		this.datasetInfos = this.step.getSessionInfo().getDataSource().getDatasets();
        Collections.sort(this.datasetInfos);
       // datasetNames.clear();
       //// datasetIDs.clear();
       // for (DatasetInfo di : this.datasetInfos){
       // 	datasetNames.add(di.getName());
       // 	datasetIDs.add(di.getProjectID());
       // }
        if (datasetInfos.size() < 1){
        	throw new AvatolCVException("no valid datasets detected.");
        }
	}
	@Override
	public Node getContentNode() throws AvatolCVException {
		try {
        	System.out.println("trying to load" +  this.fxmlDocName);
            FXMLLoader loader = new FXMLLoader(this.getClass().getResource(this.fxmlDocName));
            loader.setController(this);
            Node content = loader.load();
            JavaFXUtils.clearIssues(JavaFXStepSequencer.vBoxDataIssuesSingleton);
            
            DataSource dataSource = this.step.getSessionInfo().getDataSource();
            if (dataSource instanceof FileSystemDataSource){
            	reloadDatasetCheckbox.setDisable(true);
            	datasetChoiceVBox.getChildren().remove(reloadDatasetCheckbox);
            }
            else {
            	reloadDatasetCheckbox.setText(dataSource.getRepullPrompt());
            }
            resetDatasetInfo();
            
            ObservableList<String> list = selectedDataset.getItems();
            for (int i = 0; i < this.datasetInfos.size(); i++){
            	list.add(getTextForDatasetChoice(i));
            }
    		if (this.step.hasPriorAnswers()){
            	followUpDataDownloadPhaseComplete = false;
            	Hashtable<String, String> hash = this.step.getPriorAnswers();
            	String priorSelectedIndexString = hash.get("chosenDatasetIndex");
            	int rememberedIndex = new Integer(priorSelectedIndexString).intValue();
            	selectedDataset.setValue(getTextForDatasetChoice(rememberedIndex));
            }
            else {
        		selectedDataset.setValue(getTextForDatasetChoice(0));
            }
            datasetTitleName.setText(this.step.getDatasetTitleText());
            this.selectedDataset.getSelectionModel().selectedIndexProperty().addListener(new DatasetChangeListener());
            return content;
        }
        catch(IOException ioe){
            throw new AvatolCVException("problem loading ui " + fxmlDocName + " for controller " + this.getClass().getName());
        } 
	}
	private String getTextForDatasetChoice(int i){
		DatasetInfo di = this.datasetInfos.get(i);
		return di.getName() + " ( project ID: " + di.getProjectID() + "  ,  " + di.getDatasetLabel() + " ID: " + di.getID() +" )";
	}
	public String getDatasetIDForDatasetName(String dsName) throws AvatolCVException {
		SessionInfo si = this.step.getSessionInfo();
		DataSource ds = si.getDataSource();
		String result = ds.getDatasetIDforName(dsName);
        return result;
	}
	public class DatasetChangeListener implements ChangeListener<Number> {
        @Override
        public void changed(ObservableValue ov, Number value, Number newValue) {
            try {
            	int index = ((Integer)newValue).intValue();
            	selectedDatasetIndex = index;
            }
            catch(Exception e){
                AvatolCVExceptionExpresserJavaFX.instance.showException(e, "Problem changing grouping of training vs score ");
            }
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
