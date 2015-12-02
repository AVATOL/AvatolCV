package edu.oregonstate.eecs.iis.avatolcv.ui.javafx;

import java.io.IOException;
import java.util.List;

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
import edu.oregonstate.eecs.iis.avatolcv.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.algorithm.ScoringAlgorithm;
import edu.oregonstate.eecs.iis.avatolcv.core.EvaluationSet;
import edu.oregonstate.eecs.iis.avatolcv.core.ModalImageInfo;
import edu.oregonstate.eecs.iis.avatolcv.core.ScoringSet;
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
    public ChoiceBox choiceBoxGroupProerty = null;
    public ScrollPane trainTestSettingsScrollPane = null;
    private EvaluationSet evaluationSet = null;
    private TrueScoringSet trueScoringSet = null;
    public ScoringConfigurationStepController(ScoringConfigurationStep step, String fxmlDocName){
        this.step = step;
        this.fxmlDocName = fxmlDocName;
    }
	@Override
	public boolean consumeUIData() {
	    this.step.setScoringSet(this.trueScoringSet);
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
            this.evaluationSet = this.step.getEvaluationSet();
            radioEvaluateAlgorithm.setSelected(true);
            try {
            	this.trueScoringSet = this.step.getTrueScoringSet();
            	List<String> sortCandidateValues = this.step.getScoreConfigurationSortingValueOptions(this.trueScoringSet);
            	setSortSelectorValues(sortCandidateValues);
            }
            catch(AvatolCVException ace){
            	// disable the radio if true scoring set cannot be constructed (i.e. there are no unlabeled images)
            	radioScoreImages.setDisable(true);
            }
            radioViewByImage.setSelected(true);
            configureViewByImage(this.evaluationSet);
            return content;
        }
        catch(IOException ioe){
            throw new AvatolCVException("problem loading ui " + fxmlDocName + " for controller " + this.getClass().getName());
        } 
    }
	private void setSortSelectorValues(List<String> sortCandidateValues){
	    
        ObservableList<String> sortCandidateList        = FXCollections.observableList(sortCandidateValues);
        
       // setSortSelector(presenceAbsenceAlgChoice, radioPresenceAbsence, paList,      KEY_PRESENCE_ABSENCE);
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
	public void configureAsEvaluteAlgorithm(){
	    System.out.println("configureAsEvaluteAlgorithm");
	}
	public void configureAsScoreImages(){
	    System.out.println("configureAsScoreImages");
	}
	public void configureAsSortByImage(){
	    System.out.println("configureAsSortByImage");
	}
	public void configureAsGroupByProperty(){
	    System.out.println("configureAsGroupByProperty");
	}
	public void configureViewByProperty(ScoringSet ss){
		
	}
	public void configureViewByImage(ScoringSet ss){
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
	    /*
		Label labelTrain =  new Label("Train");
		Label labelTest =  new Label("Test");
		Label labelImage =  new Label("Image");
		
		gp.add(labelTrain, 0, 0);
		gp.add(labelTest, 1, 0);
		gp.add(labelImage, 2, 0);
		*/
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
		trainTestSettingsScrollPane.setContent(gp);
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
