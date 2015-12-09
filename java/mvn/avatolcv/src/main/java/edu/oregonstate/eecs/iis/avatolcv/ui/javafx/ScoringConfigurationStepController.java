package edu.oregonstate.eecs.iis.avatolcv.ui.javafx;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import edu.oregonstate.eecs.iis.avatolcv.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.core.EvaluationSet;
import edu.oregonstate.eecs.iis.avatolcv.core.ModalImageInfo;
import edu.oregonstate.eecs.iis.avatolcv.core.NormalizedValue;
import edu.oregonstate.eecs.iis.avatolcv.core.NormalizedKey;
import edu.oregonstate.eecs.iis.avatolcv.core.ScoringSet;
import edu.oregonstate.eecs.iis.avatolcv.core.SessionInfo;
import edu.oregonstate.eecs.iis.avatolcv.core.StepController;
import edu.oregonstate.eecs.iis.avatolcv.core.TrueScoringSet;
import edu.oregonstate.eecs.iis.avatolcv.javafxui.AvatolCVExceptionExpresserJavaFX;
import edu.oregonstate.eecs.iis.avatolcv.steps.ScoringConfigurationStep;

public class ScoringConfigurationStepController implements StepController {
	private ScoringConfigurationStep step = null;
    private String fxmlDocName = null;
    public RadioButton radioScoreImages = null;
    public RadioButton radioEvaluateAlgorithm = null;
    public RadioButton radioViewByImage = null;
    public RadioButton radioViewByGroup = null;
    public ChoiceBox<String> choiceBoxGroupProperty = null;
    public TextArea groupDescriptionTextArea = null;
    public ScrollPane trainTestSettingsScrollPane = null;
    private List<EvaluationSet> evaluationSets = null;
    private List<TrueScoringSet> trueScoringSets = null;
    private boolean activeSetIsEvaluation = true;
    private boolean sortByImage = true;
    private Hashtable<String, NormalizedKey> normalizedKeyHash = new Hashtable<String, NormalizedKey>();
    public ScoringConfigurationStepController(ScoringConfigurationStep step, String fxmlDocName){
        this.step = step;
        this.fxmlDocName = fxmlDocName;
    }
    public List<ScoringSet> getActiveScoringSets(){
    	List<ScoringSet> sets = new ArrayList<ScoringSet>();
    	if (this.activeSetIsEvaluation){
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
	@Override
	public boolean consumeUIData() {
	    if (SessionInfo.isBisqueSession()){
	        System.out.println("true scoring set for bisque");
	        this.step.setScoringSet(this.trueScoringSets.get(0));
	    }
	    else {
	        System.out.println("evaluation set for morphobank");
	        this.step.setScoringSet(this.evaluationSets.get(0));
	    }
	    
	    try {
            this.step.consumeProvidedData();
        }
        catch(Exception e){
            AvatolCVExceptionExpresserJavaFX.instance.showException(e, "problem trying to consume data at segmentation configuration");
        }
        return true;
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
            trainTestSettingsScrollPane.setStyle("-fx-border-color: black;");
            this.step.reAssessImagesInPlay();
            this.evaluationSets = this.step.getEvaluationSets();
            
            try {
            	this.trueScoringSets = this.step.getTrueScoringSets();
            	
            }
            catch(AvatolCVException ace){
            	// disable the radio if true scoring set cannot be constructed (i.e. there are no unlabeled images)
            	radioScoreImages.setDisable(true);
            }
            configureAsEvaluateAlgorithm();
            populateSortingChoiceBox();
            configureAsSortByImage();
            NormalizedKey trainTestKey = this.step.getDefaultTrainTestConcern();
            this.step.setTrainTestConcern(trainTestKey);
            return content;
        }
        catch(IOException ioe){
            throw new AvatolCVException("problem loading ui " + fxmlDocName + " for controller " + this.getClass().getName());
        } 
    }
	private void populateSortingChoiceBox() throws AvatolCVException {
		List<NormalizedKey> sortCandidates = this.step.getScoringSortingCandidates();
		List<String> sortCandidateStrings = new ArrayList<String>();
		for (NormalizedKey nk : sortCandidates){
			String keyName = nk.getName();
			normalizedKeyHash.put(keyName, nk);
			sortCandidateStrings.add(keyName);
		}
		Collections.sort(sortCandidateStrings);
        this.choiceBoxGroupProperty.getItems().addAll(sortCandidateStrings);
        this.choiceBoxGroupProperty.setValue(this.choiceBoxGroupProperty.getItems().get(0));
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
                configureAsGroupByProperty();
            }
            catch(Exception e){
                AvatolCVExceptionExpresserJavaFX.instance.showException(e, "Problem changing grouping of training vs score ");
            }
        }
    }
	
	private void setSortSelector(ComboBox<String> choiceBox, RadioButton radioButton, ObservableList<String> oList, String key){
        choiceBox.setItems(oList);
        if (oList.size() > 0){
            if (this.step.hasPriorAnswers()){
                String paAlg = this.step.getPriorAnswers().get(key);
                if(null == paAlg){
                    // alg was not selected prior
                    choiceBox.setValue(oList.get(0));
                }
                else {
                    // alg was selected prior, use that value
                    choiceBox.setValue(paAlg);
                    radioButton.setSelected(true);
                }
            }
            else {
                choiceBox.setValue(oList.get(0));
            }
            choiceBox.requestLayout();
        }
    }
	public void configureAsEvaluateAlgorithm(){
		try {
			radioEvaluateAlgorithm.setSelected(true);
			this.activeSetIsEvaluation = true;
			List<ScoringSet> sets = getActiveScoringSets();
			if (this.sortByImage){
				configureAsSortByImage(sets);
			}
			else {
				configureAsSortByProperty(sets);
			}
		}
		catch(AvatolCVException ace){
			AvatolCVExceptionExpresserJavaFX.instance.showException(ace, "Problem configuring evaluation run ");
		}
		
	}

	public void configureAsScoreImages() {
		try {
			radioScoreImages.setSelected(true);
			this.activeSetIsEvaluation = false;
			List<ScoringSet> sets = getActiveScoringSets();
			if (this.sortByImage){
				configureAsSortByImage(sets);
			}
			else {
				configureAsSortByProperty(sets);
			}
		}
		catch(AvatolCVException ace){
			AvatolCVExceptionExpresserJavaFX.instance.showException(ace, "Problem configuring scoring run ");
		}
		
	}

	public void configureAsSortByImage(){
		try {
			this.sortByImage = true;
			//choiceBoxGroupProperty.setDisable(true);
			radioViewByImage.setSelected(true);
			List<ScoringSet> sets = getActiveScoringSets();
			configureAsSortByImage(sets);
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
		configureAsSortByProperty(sets);
		this.step.setTrainTestConcern(new NormalizedKey(choiceBoxGroupProperty.getValue()));
	}
	public void configureAsSortByProperty(List<ScoringSet> scoringSets) throws AvatolCVException {
		trainTestSettingsScrollPane.setContent(null);
		if (scoringSets.size() == 1){
			GridPane gp = loadGridPaneWithSetByGrouping(scoringSets.get(0));
			trainTestSettingsScrollPane.setContent(gp);
		}
		else {
			VBox vbox = new VBox();
			for (ScoringSet ss : scoringSets){
				Label label = new Label(ss.getKeyToScore().getName());
				vbox.getChildren().add(label);
				GridPane gp = loadGridPaneWithSetByGrouping(ss);
				vbox.getChildren().add(gp);
			}
			trainTestSettingsScrollPane.setContent(vbox);
		}
	}

	public void configureAsSortByImage(List<ScoringSet> scoringSets) throws AvatolCVException {
		trainTestSettingsScrollPane.setContent(null);
		if (scoringSets.size() == 1){
			GridPane gp = loadGridPaneWithSetByImage(scoringSets.get(0));
			trainTestSettingsScrollPane.setContent(gp);
		}
		else {
			VBox vbox = new VBox();
			for (ScoringSet ss : scoringSets){
				Label label = new Label(ss.getKeyToScore().getName());
				vbox.getChildren().add(label);
				GridPane gp = loadGridPaneWithSetByImage(ss);
				vbox.getChildren().add(gp);
			}
			trainTestSettingsScrollPane.setContent(vbox);
		}
	}
	public GridPane loadGridPaneWithSetByImage(ScoringSet ss) throws AvatolCVException { 
		GridPane gp = new GridPane();
		ColumnConstraints column1 = new ColumnConstraints();
	    //column1.setPercentWidth(15);
	    ColumnConstraints column2 = new ColumnConstraints();
	    //column2.setPercentWidth(15);
	    ColumnConstraints column3 = new ColumnConstraints();
	   // column3.setPercentWidth(80);
	    gp.getColumnConstraints().addAll(column1, column2, column3); 
	    gp.setHgap(20);
	    gp.setVgap(6);
	    
		List<ModalImageInfo> trainingImages = ss.getImagesToTrainOn();
		List<ModalImageInfo> scoringImages = ss.getImagesToScore();
		int row = 0;
		for (ModalImageInfo mii : trainingImages){
			RadioButton radioTrain = new RadioButton("train");
			RadioButton radioScore = new RadioButton("score");
			ToggleGroup tg = new ToggleGroup();
			radioTrain.setToggleGroup(tg);
			radioScore.setToggleGroup(tg);
			radioTrain.setSelected(true);
			Label imageLabel = new Label(mii.getNormalizedImageInfo().getImageName());
			gp.add(radioTrain, 0, row);
			gp.add(radioScore, 1, row);
			gp.add(imageLabel, 2, row);
			row++;
		}
		for (ModalImageInfo mii : scoringImages){
			RadioButton radioTrain = new RadioButton("train");
			RadioButton radioScore = new RadioButton("score");
			ToggleGroup tg = new ToggleGroup();
			radioTrain.setToggleGroup(tg);
			radioScore.setToggleGroup(tg);
			radioScore.setSelected(true);
			Label imageLabel = new Label(mii.getNormalizedImageInfo().getImageName());
			gp.add(radioTrain, 0, row);
			gp.add(radioScore, 1, row);
			gp.add(imageLabel, 2, row);
			row++;
		}
		return gp;
	}
	public GridPane loadGridPaneWithSetByGrouping(ScoringSet ss) throws AvatolCVException { 
		String trainTestConcern = choiceBoxGroupProperty.getValue();
		NormalizedKey trainTestConcernKey = normalizedKeyHash.get(trainTestConcern);
		List<NormalizedValue> trainTestValues = this.step.getValuesForTrainTestConcern(trainTestConcernKey);
		for (NormalizedValue ttValue : trainTestValues){
			//ss.g
		}
		GridPane gp = new GridPane();
		ColumnConstraints column1 = new ColumnConstraints();
	    //column1.setPercentWidth(15);
	    ColumnConstraints column2 = new ColumnConstraints();
	    //column2.setPercentWidth(15);
	    ColumnConstraints column3 = new ColumnConstraints();
	   // column3.setPercentWidth(80);
	    gp.getColumnConstraints().addAll(column1, column2, column3); 
	    gp.setHgap(20);
	    gp.setVgap(6);
	    
		List<ModalImageInfo> trainingImages = ss.getImagesToTrainOn();
		List<ModalImageInfo> scoringImages = ss.getImagesToScore();
		int row = 0;
		for (ModalImageInfo mii : trainingImages){
			RadioButton radioTrain = new RadioButton("train");
			RadioButton radioScore = new RadioButton("score");
			ToggleGroup tg = new ToggleGroup();
			radioTrain.setToggleGroup(tg);
			radioScore.setToggleGroup(tg);
			radioTrain.setSelected(true);
			Label imageLabel = new Label(mii.getNormalizedImageInfo().getImageName());
			gp.add(radioTrain, 0, row);
			gp.add(radioScore, 1, row);
			gp.add(imageLabel, 2, row);
			row++;
		}
		for (ModalImageInfo mii : scoringImages){
			RadioButton radioTrain = new RadioButton("train");
			RadioButton radioScore = new RadioButton("score");
			ToggleGroup tg = new ToggleGroup();
			radioTrain.setToggleGroup(tg);
			radioScore.setToggleGroup(tg);
			radioScore.setSelected(true);
			Label imageLabel = new Label(mii.getNormalizedImageInfo().getImageName());
			gp.add(radioTrain, 0, row);
			gp.add(radioScore, 1, row);
			gp.add(imageLabel, 2, row);
			row++;
		}
		return gp;
	}
	@Override
	public boolean delayEnableNavButtons() {
		// TODO Auto-generated method stub
		return false;
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

}
