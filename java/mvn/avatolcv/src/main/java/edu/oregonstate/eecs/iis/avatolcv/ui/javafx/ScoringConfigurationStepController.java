package edu.oregonstate.eecs.iis.avatolcv.ui.javafx;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Accordion;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TitledPane;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import edu.oregonstate.eecs.iis.avatolcv.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.AvatolCVFileSystem;
import edu.oregonstate.eecs.iis.avatolcv.algorithm.ScoringAlgorithm;
import edu.oregonstate.eecs.iis.avatolcv.core.ImageInfo;
import edu.oregonstate.eecs.iis.avatolcv.core.ImageWithInfo;
import edu.oregonstate.eecs.iis.avatolcv.javafxui.AvatolCVExceptionExpresserJavaFX;
import edu.oregonstate.eecs.iis.avatolcv.normalized.NormalizedKey;
import edu.oregonstate.eecs.iis.avatolcv.normalized.NormalizedValue;
import edu.oregonstate.eecs.iis.avatolcv.scoring.EvaluationSet;
import edu.oregonstate.eecs.iis.avatolcv.scoring.ModalImageInfo;
import edu.oregonstate.eecs.iis.avatolcv.scoring.ScoringSet;
import edu.oregonstate.eecs.iis.avatolcv.scoring.KeySorterEvaluation;
import edu.oregonstate.eecs.iis.avatolcv.scoring.TrainScoreIgnoreBreakdown;
import edu.oregonstate.eecs.iis.avatolcv.scoring.TrueScoringSet;
import edu.oregonstate.eecs.iis.avatolcv.session.DataIssue;
import edu.oregonstate.eecs.iis.avatolcv.session.ImagesForStep;
import edu.oregonstate.eecs.iis.avatolcv.session.SessionInfo;
import edu.oregonstate.eecs.iis.avatolcv.session.StepController;
import edu.oregonstate.eecs.iis.avatolcv.steps.ScoringConfigurationStep;

public class ScoringConfigurationStepController implements StepController {
	private ScoringConfigurationStep step = null;
    private String fxmlDocName = null;
    public HBox divideByGroupHBox = null;
    public VBox trainingVsScoringVbox = null;
    public RadioButton radioViewByImage = null;
    public RadioButton radioViewByGroup = null;
    public ChoiceBox<String> choiceBoxGroupProperty = null;
    public TextArea groupDescriptionTextArea = null;
    public AnchorPane trainTestSettingsAnchorPane = null;
    //public HBox percentageExpressionHbox = null;
    private List<EvaluationSet> evaluationSets = null;
    private List<TrueScoringSet> trueScoringSets = null;
    private boolean sortByImage = true;
    private NormalizedKey trainTestKey = null;
    private Hashtable<String, NormalizedKey> normalizedKeyHash = new Hashtable<String, NormalizedKey>();
    private TrainScoreIgnoreBreakdown trainScoreIgnore = null;
    
    public ScoringConfigurationStepController(ScoringConfigurationStep step, String fxmlDocName) throws AvatolCVException {
        this.step = step;
        this.fxmlDocName = fxmlDocName;
    }
    public List<ScoringSet> getActiveScoringSets(){
    	List<ScoringSet> sets = new ArrayList<ScoringSet>();
    	if (null != evaluationSets){
	    	for (EvaluationSet es : this.evaluationSets){
	    		sets.add(es);
	    	}
	    }
	    else {
	    	for (TrueScoringSet tss : this.trueScoringSets){
	    		sets.add(tss);
	    	}
	    }
    	return sets;
    }
    private boolean hasTrainingExamplesSelected() throws AvatolCVException {
    	List<ScoringSet> sets = getActiveScoringSets();
    	for (ScoringSet set : sets){
			if (set.getImagesToTrainOn().size() != 0){
				return true;
			}
		}
		return false;
    }
	@Override
	public boolean consumeUIData() {
		try {
			if (!hasTrainingExamplesSelected()){
				Platform.runLater(new NoTrainingDataAlert());
				return false;
			}
			else {
				if (this.step.getSessionInfo().isEvaluationRun()){
				    trainScoreIgnore.persist();
					consumeEvaluationSets();
			    }
			    else {
			    	consumeTrueScoringSets();
			    }
				if (this.sortByImage){
					this.step.setTrainTestConcern(null);
				}
				else {
					this.step.setTrainTestConcern(normalizedKeyHash.get(this.choiceBoxGroupProperty.getValue()));
				}
				
		        this.step.consumeProvidedData();
		        return true;
			}
		}
		catch(Exception e){
            AvatolCVExceptionExpresserJavaFX.instance.showException(e, "problem trying to consume data at scoring configuration");
            return false;
        }
	    
	}

	public class NoTrainingDataAlert implements Runnable {
		public void run(){
			Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error Dialog");
            alert.setHeaderText("Scoring configuration problem");
            alert.setContentText("training examples need to be selected.");
            alert.showAndWait();
		}
	}
	private void consumeEvaluationSets(){
		List<ScoringSet> sSets = new ArrayList<ScoringSet>();
        sSets.addAll(this.evaluationSets);
        this.step.setScoringSets(sSets);
	}
	private void consumeTrueScoringSets(){
		List<ScoringSet> sSets = new ArrayList<ScoringSet>();
        sSets.addAll(this.trueScoringSets);
        this.step.setScoringSets(sSets);
	}
	@Override
	public void clearUIFields() {
		// TODO Auto-generated method stub
	}

	@Override
	public Node getContentNode() throws AvatolCVException {
        try {
            System.out.println("trying to load" +  this.fxmlDocName);
            FXMLLoader loader = new FXMLLoader(this.getClass().getResource(this.fxmlDocName));
            loader.setController(this);
            Node content = loader.load();
            List<DataIssue> dataIssues =  this.step.getSessionInfo().checkDataIssues();
            JavaFXUtils.populateIssues(dataIssues);
            JavaFXUtils.populateDataInPlay(this.step.getSessionInfo());
            //trainTestSettingsPane.setStyle("-fx-border-color: black;");
            SessionInfo sessionInfo = this.step.getSessionInfo();
            sessionInfo.reAssessImagesInPlay();
            
            NormalizedKey mandatoryTrainTestKey = sessionInfo.getMandatoryTrainTestConcern();
            trainTestKey = mandatoryTrainTestKey;
            if (null == trainTestKey){
            	trainTestKey = sessionInfo.getDefaultTrainTestConcern();
            } 
            this.step.setTrainTestConcern(trainTestKey);
            
            sessionInfo.alignLabelsAcrossCharacters();
            if (sessionInfo.isEvaluationRun()){
                this.evaluationSets = sessionInfo.getEvaluationSets();
                this.trueScoringSets = null;
            }
            else {
                this.evaluationSets = null;
                this.trueScoringSets = sessionInfo.getTrueScoringSets();
            }
            populateSortingChoiceBox();
            ScoringAlgorithm sa = sessionInfo.getSelectedScoringAlgorithm();
            if (sa.isTrainTestConcernRequired()){
            	radioViewByGroup.setSelected(true);
            	radioViewByImage.setDisable(true);
            	configureAsGroupByProperty();
            	if (mandatoryTrainTestKey != null){
            		this.trainingVsScoringVbox.getChildren().remove(this.radioViewByImage);
            		this.trainingVsScoringVbox.getChildren().remove(this.divideByGroupHBox);
            	}
            }
            else {
            	configureAsSortByImage();
            }
            return content;
        }
        catch(IOException ioe){
            throw new AvatolCVException("problem loading ui " + fxmlDocName + " for controller " + this.getClass().getName());
        } 
    }
	private void populateSortingChoiceBox() throws AvatolCVException {
		List<NormalizedKey> sortCandidates = this.step.getSessionInfo().getScoringSortingCandidates();
		List<String> sortCandidateStrings = new ArrayList<String>();
		for (NormalizedKey nk : sortCandidates){
			String keyName = nk.getName();
			normalizedKeyHash.put(keyName, nk);
			sortCandidateStrings.add(keyName);
		}
		sortCandidateStrings = this.step.getSessionInfo().getDataSource().filterBadSortCandidates(sortCandidateStrings);
		Collections.sort(sortCandidateStrings);
        this.choiceBoxGroupProperty.getItems().addAll(sortCandidateStrings);
        if (null == trainTestKey){
        	this.choiceBoxGroupProperty.setValue(this.choiceBoxGroupProperty.getItems().get(0));
        }
        else {
        	System.out.println("Setting choiceBox to " + trainTestKey.getName());
        	this.choiceBoxGroupProperty.setValue(trainTestKey.getName());
        }
        this.choiceBoxGroupProperty.getSelectionModel().selectedIndexProperty().addListener(new GroupChangeListener(this.choiceBoxGroupProperty, this.groupDescriptionTextArea, this.step));
	}
	public class GroupChangeListener implements ChangeListener<Number> {
        private ChoiceBox<String> cb;
        private TextArea ta;
        private ScoringConfigurationStep step;
        public GroupChangeListener(ChoiceBox<String> cb, TextArea ta, ScoringConfigurationStep step){
            this.ta = ta;
            this.cb = cb;
            this.step = step;
        }
        @Override
        public void changed(ObservableValue ov, Number value, Number newValue) {
            try {
                //System.out.println("new Value " + newValue);
                radioViewByGroup.setSelected(true);
                configureAsGroupByProperty();
            }
            catch(Exception e){
                AvatolCVExceptionExpresserJavaFX.instance.showException(e, "Problem changing grouping of training vs score ");
            }
        }
    }
	
	
	
	public void configureAsSortByImage(){
		try {
			this.sortByImage = true;
			choiceBoxGroupProperty.setDisable(true);
			radioViewByImage.setSelected(true);
			List<ScoringSet> sets = getActiveScoringSets();
			if (sets.get(0) instanceof EvaluationSet){
			    renderByImage(sets, true);
			}
			else {
			    renderByImage(sets, false);
			}
			//configureAsSortByImage(sets);
		}
		catch(AvatolCVException ace){
			AvatolCVExceptionExpresserJavaFX.instance.showException(ace, "Problem setting 'sort by image' ");
		}
	}
	public void configureAsGroupByProperty() throws AvatolCVException {
		this.sortByImage = false;
		choiceBoxGroupProperty.setDisable(false);
		radioViewByGroup.setSelected(true);
		List<ScoringSet> sets = getActiveScoringSets();
		this.step.setTrainTestConcern(new NormalizedKey(choiceBoxGroupProperty.getValue()));
        renderByGroup(sets);
	}
	

	public void renderByImage(List<ScoringSet> sets, boolean allowUserChanges) throws AvatolCVException {
	    trainTestSettingsAnchorPane.getChildren().clear();
        Accordion accordian = new SetsByImageAccordion(sets, allowUserChanges);
        AnchorPane.setTopAnchor(accordian, 0.0);
        AnchorPane.setLeftAnchor(accordian, 0.0);
        AnchorPane.setRightAnchor(accordian, 0.0);
        AnchorPane.setBottomAnchor(accordian, 0.0);
        trainTestSettingsAnchorPane.getChildren().add(accordian);
	}
	//public boolean isEvaluation(){
	//	if (){
	//		return true;
	//	}
	//	return false;
	//}
	public void renderByGroup(List<ScoringSet> sets) throws AvatolCVException {
	    trainTestSettingsAnchorPane.getChildren().clear();
	    String selectedTrainTestConcern = choiceBoxGroupProperty.getValue();
        NormalizedKey selectedTrainTestConcernKey = normalizedKeyHash.get(selectedTrainTestConcern);
        List<NormalizedValue> trainTestValues = this.step.getSessionInfo().getValuesForTrainTestConcern(selectedTrainTestConcernKey);
        
        if (this.step.getSessionInfo().isEvaluationRun()){
            trainScoreIgnore = new TrainScoreIgnoreBreakdown();
        	GroupedPanelEvaluation panel = new GroupedPanelEvaluation(sets, selectedTrainTestConcern, selectedTrainTestConcernKey, trainTestValues, trainScoreIgnore);
    	    AnchorPane.setTopAnchor(panel, 0.0);
            AnchorPane.setLeftAnchor(panel, 0.0);
            AnchorPane.setRightAnchor(panel, 0.0);
            AnchorPane.setBottomAnchor(panel, 0.0);
            trainTestSettingsAnchorPane.getChildren().add(panel);
        }
        else {
        	boolean trainTestSplitRequiredByAlg = this.step.getSessionInfo().getSelectedScoringAlgorithm().isTrainTestConcernRequired();
        	GroupedPanelTrueScoring panel = new GroupedPanelTrueScoring(sets, selectedTrainTestConcern, selectedTrainTestConcernKey, trainTestValues, trainTestSplitRequiredByAlg);
    	    AnchorPane.setTopAnchor(panel, 0.0);
            AnchorPane.setLeftAnchor(panel, 0.0);
            AnchorPane.setRightAnchor(panel, 0.0);
            AnchorPane.setBottomAnchor(panel, 0.0);
            trainTestSettingsAnchorPane.getChildren().add(panel);
        }
	    
        //inspect(trainTestSettingsAnchorPane);
	}
	public void inspect(Region region){
		System.out.println(region.getClass().getName() + " " + region.getMaxHeight());
		ObservableList<Node> list = region.getChildrenUnmodifiable();
		for (Node n : list){
			if (n instanceof Region){
				inspect((Region)n);
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
	
}

