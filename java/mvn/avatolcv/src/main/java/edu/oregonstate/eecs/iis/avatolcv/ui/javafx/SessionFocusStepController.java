package edu.oregonstate.eecs.iis.avatolcv.ui.javafx;

import java.io.IOException;
import java.util.Hashtable;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.RadioButton;
import edu.oregonstate.eecs.iis.avatolcv.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.algorithm.ScoringAlgorithms;
import edu.oregonstate.eecs.iis.avatolcv.core.SessionInfo;
import edu.oregonstate.eecs.iis.avatolcv.core.StepController;
import edu.oregonstate.eecs.iis.avatolcv.steps.SessionFocusStep;

public class SessionFocusStepController implements StepController {
	private static final String KEY_PRESENCE_ABSENCE = "presenceAbsence";
	private static final String KEY_SHAPE_ASPECT = "shapeAspect";
	private static final String KEY_TEXTURE_ASPECT = "textureAspect";
    public RadioButton radioPresenceAbsence;
    public RadioButton radioShape;
    public RadioButton radioTexture;
    public ComboBox<String> presenceAbsenceAlgChoice;
    public ComboBox<String> shapeAlgChoice;
    public ComboBox<String> textureAlgChoice;
    private SessionFocusStep focusStep;
    private String fxmlDocName;
    public SessionFocusStepController(SessionFocusStep focusStep, String fxmlDocName){
        this.focusStep = focusStep;
        this.fxmlDocName = fxmlDocName;
    }
    @Override
    public boolean consumeUIData() {
        try {
        	Hashtable<String, String> answerHash = new Hashtable<String, String>();
            if (radioPresenceAbsence.isSelected()){
            	String scoringAlg = presenceAbsenceAlgChoice.getValue();
    			answerHash.put(KEY_PRESENCE_ABSENCE, scoringAlg);
                this.focusStep.setScoringAlgInfo(ScoringAlgorithms.ScoringSessionFocus.SPECIMEN_PART_PRESENCE_ABSENCE, scoringAlg);
            }
            else if (radioShape.isSelected()){
            	String scoringAlg = shapeAlgChoice.getValue();
    			answerHash.put(KEY_SHAPE_ASPECT, scoringAlg);
                this.focusStep.setScoringAlgInfo(ScoringAlgorithms.ScoringSessionFocus.SPECIMEN_SHAPE_ASPECT, scoringAlg);
            }
            else {
                // must be texture
            	String scoringAlg = textureAlgChoice.getValue();
    			answerHash.put(KEY_TEXTURE_ASPECT, scoringAlg);
                this.focusStep.setScoringAlgInfo(ScoringAlgorithms.ScoringSessionFocus.SPECIMEN_TEXTURE_ASPECT, scoringAlg);
            }
			this.focusStep.saveAnswers(answerHash);
            this.focusStep.consumeProvidedData();
            return true;
        }
        catch(AvatolCVException e){
            SessionInfo.exceptionExpresser.showException(e, "Problem consuming info from Session Focus screen");
            return false;
        }
        
    }

    @Override
    public void clearUIFields() {
        //NA
    }

    @Override
    public Node getContentNode() throws AvatolCVException {
        try {
            //System.out.println("trying to load" +  this.fxmlDocName);
            FXMLLoader loader = new FXMLLoader(this.getClass().getResource(this.fxmlDocName));
            loader.setController(this);
            Node content = loader.load();
            ScoringAlgorithms sa = this.focusStep.getScoringAlgorithms();
            
            radioPresenceAbsence.setText(sa.getRadioButtonTextForScoringFocus(ScoringAlgorithms.ScoringSessionFocus.SPECIMEN_PART_PRESENCE_ABSENCE));
            radioShape.setText(          sa.getRadioButtonTextForScoringFocus(ScoringAlgorithms.ScoringSessionFocus.SPECIMEN_SHAPE_ASPECT));
            radioTexture.setText(        sa.getRadioButtonTextForScoringFocus(ScoringAlgorithms.ScoringSessionFocus.SPECIMEN_TEXTURE_ASPECT));

            List<String> presenceAbsenceAlgNames = sa.getNamesForScoringFocus(ScoringAlgorithms.ScoringSessionFocus.SPECIMEN_PART_PRESENCE_ABSENCE);
            List<String> shapeAlgNames           = sa.getNamesForScoringFocus(ScoringAlgorithms.ScoringSessionFocus.SPECIMEN_SHAPE_ASPECT);
            List<String> textureAlgNames         = sa.getNamesForScoringFocus(ScoringAlgorithms.ScoringSessionFocus.SPECIMEN_TEXTURE_ASPECT);
            ObservableList<String> paList        = FXCollections.observableList(presenceAbsenceAlgNames);
            ObservableList<String> shapeList     = FXCollections.observableList(shapeAlgNames);
            ObservableList<String> textureList   = FXCollections.observableList(textureAlgNames);
            
            setAlgSelector(presenceAbsenceAlgChoice, radioPresenceAbsence, paList,      KEY_PRESENCE_ABSENCE);
            setAlgSelector(shapeAlgChoice,           radioShape,           shapeList,   KEY_SHAPE_ASPECT);
            setAlgSelector(textureAlgChoice,         radioTexture, 		   textureList, KEY_TEXTURE_ASPECT);
            return content;
        }
        catch(IOException ioe){
            throw new AvatolCVException("problem loading ui " + fxmlDocName + " for controller " + this.getClass().getName());
        }
    }
    private void setAlgSelector(ComboBox<String> choiceBox, RadioButton radioButton, ObservableList<String> oList, String key){
    	choiceBox.setItems(oList);
        if (oList.size() > 0){
        	if (this.focusStep.hasPriorAnswers()){
        		String paAlg = this.focusStep.getPriorAnswers().get(key);
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
    
   
	@Override
	public boolean delayEnableNavButtons() {
		return false;
	}
    @Override
    public void executeFollowUpDataLoadPhase() throws AvatolCVException {
        // nothing to be done
    }
    @Override
    public void configureUIForFollowUpDataLoadPhase() {
        // nothing to be done
    }
    @Override
    public boolean isFollowUpDataLoadPhaseComplete() {
        // not relevant
        return true;
    }


}
