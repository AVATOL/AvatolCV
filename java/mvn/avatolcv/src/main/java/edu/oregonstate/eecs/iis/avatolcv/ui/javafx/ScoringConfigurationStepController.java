package edu.oregonstate.eecs.iis.avatolcv.ui.javafx;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import edu.oregonstate.eecs.iis.avatolcv.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.core.EvaluationSet;
import edu.oregonstate.eecs.iis.avatolcv.core.ModalImageInfo;
import edu.oregonstate.eecs.iis.avatolcv.core.StepController;
import edu.oregonstate.eecs.iis.avatolcv.core.TrueScoringSet;
import edu.oregonstate.eecs.iis.avatolcv.steps.ScoringConfigurationStep;
import edu.oregonstate.eecs.iis.avatolcv.steps.SegmentationConfigurationStep;
import edu.oregonstate.eecs.iis.avatolcv.ui.javafx.SegmentationConfigurationStepController.AlgChangeListener;

public class ScoringConfigurationStepController implements StepController {
	private ScoringConfigurationStep step = null;
    private String fxmlDocName = null;
    public RadioButton radioScoreImages = null;
    public RadioButton radioEvaluateAlgorithm = null;
    public RadioButton radioViewByImage = null;
    public RadioButton radioViewByGroup = null;
    public ScrollPane trainTestSettingsScrollPane = null;
    public ScoringConfigurationStepController(ScoringConfigurationStep step, String fxmlDocName){
        this.step = step;
        this.fxmlDocName = fxmlDocName;
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
            System.out.println("trying to load" +  this.fxmlDocName);
            FXMLLoader loader = new FXMLLoader(this.getClass().getResource(this.fxmlDocName));
            loader.setController(this);
            Node content = loader.load();
            this.step.reAssessImagesInPlay();
            EvaluationSet es = this.step.getEvaluationSet();
            radioEvaluateAlgorithm.setSelected(true);
            try {
            	TrueScoringSet tss = this.step.getTrueScoringSet();
            }
            catch(AvatolCVException ace){
            	// disable the radio if true scoring set cannot be constructed (i.e. there are no unlabeled images)
            	radioScoreImages.setDisable(true);
            }
            radioViewByImage.setSelected(true);
            configureViewEvalByImage(es);
            return content;
        }
        catch(IOException ioe){
            throw new AvatolCVException("problem loading ui " + fxmlDocName + " for controller " + this.getClass().getName());
        } 
    }
	public void configureViewEvalByImage(EvaluationSet es){
		GridPane gp = new GridPane();
		ColumnConstraints column1 = new ColumnConstraints();
	    column1.setPercentWidth(10);
	    ColumnConstraints column2 = new ColumnConstraints();
	    column2.setPercentWidth(10);
	    ColumnConstraints column3 = new ColumnConstraints();
	    column3.setPercentWidth(80);
	    gp.getColumnConstraints().addAll(column1, column2, column3); 
	    gp.setHgap(6);
	    gp.setVgap(6);
		Label labelTrain =  new Label("Train");
		Label labelTest =  new Label("Test");
		Label labelImage =  new Label("Image");
		
		gp.add(labelTrain, 0, 0);
		gp.add(labelTest, 1, 0);
		gp.add(labelImage, 2, 0);
		List<ModalImageInfo> trainingImages = es.getImagesToTrainOn();
		List<ModalImageInfo> scoringImages = es.getImagesToScore();
		int row = 1;
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
			RadioButton radioTrain = new RadioButton();
			RadioButton radioScore = new RadioButton();
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
