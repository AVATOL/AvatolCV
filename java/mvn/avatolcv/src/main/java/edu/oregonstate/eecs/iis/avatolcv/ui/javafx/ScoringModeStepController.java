package edu.oregonstate.eecs.iis.avatolcv.ui.javafx;

import java.io.IOException;
import java.util.Hashtable;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import edu.oregonstate.eecs.iis.avatolcv.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.core.SessionInfo;
import edu.oregonstate.eecs.iis.avatolcv.core.StepController;
import edu.oregonstate.eecs.iis.avatolcv.javafxui.AvatolCVExceptionExpresserJavaFX;
import edu.oregonstate.eecs.iis.avatolcv.javafxui.AvatolCVJavaFX;
import edu.oregonstate.eecs.iis.avatolcv.steps.DataSourceStep;
import edu.oregonstate.eecs.iis.avatolcv.steps.ScoringModeStep;

public class ScoringModeStepController implements StepController {
    public RadioButton radioEvaluateAlgorithm;
    public RadioButton radioScoreImages;
    public Label scoringAlgLabel;
    public TextArea scoringModeNotesTextArea;
    private ScoringModeStep step;
    private String fxmlDocName;
    public ScoringModeStepController(ScoringModeStep step, String fxmlDocName){
        this.step = step;
        this.fxmlDocName = fxmlDocName;
    }
    @Override
    public boolean consumeUIData() {
    	Hashtable<String, String> answerHash = new Hashtable<String, String>();
        if (radioEvaluateAlgorithm.isSelected()){
			answerHash.put("chosenScoringMode", "EvaluationAlgorithm");
        	this.step.setModeToEvaluation();
        }
        else {
			answerHash.put("chosenScoringMode", "ScoreImages");
        	this.step.setModeToScoringImages();
        }
        this.step.saveAnswers(answerHash);
        try {
            this.step.consumeProvidedData();
            return true;
        }
        catch(Exception e){
            AvatolCVExceptionExpresserJavaFX.instance.showException(e, "Problem activating data source");
            return false;
        }
    }

    @Override
    public void clearUIFields() {
        // NA

    }

    @Override
    public Node getContentNode() throws AvatolCVException {
        try {
            //System.out.println("trying to load" +  this.fxmlDocName);
            FXMLLoader loader = new FXMLLoader(this.getClass().getResource(this.fxmlDocName));
            loader.setController(this);
            Node content = loader.load();
            radioEvaluateAlgorithm.setSelected(true);
            if (this.step.hasPriorAnswers()){
            	Hashtable<String, String> priorAnswers = this.step.getPriorAnswers();
            	String chosenScoringMode = priorAnswers.get("chosenScoringMode");
            	if (chosenScoringMode.equals("EvaluationAlgorithm")){
            		radioEvaluateAlgorithm.setSelected(true);
            	}
            	
            	else {
            	    radioScoreImages.setSelected(true);
            	}
            }
            scoringAlgLabel.setText("Scoring Algorithm:  " + this.step.getChosenScoringAlgName());
            if (this.step.isAllImagesLabeled()){
                radioScoreImages.setDisable(true);
                radioEvaluateAlgorithm.setSelected(true);
                String noImagesToScoreExplanation = "All images in play for this session are already scored";
                scoringModeNotesTextArea.setText(noImagesToScoreExplanation);
            }
            else {
                scoringModeNotesTextArea.setText("");
            }
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
     // NA
    }
    @Override
    public void configureUIForFollowUpDataLoadPhase() {
     // NA
    }
    @Override
    public boolean isFollowUpDataLoadPhaseComplete() {
     // NA
        return true;
    }


}
