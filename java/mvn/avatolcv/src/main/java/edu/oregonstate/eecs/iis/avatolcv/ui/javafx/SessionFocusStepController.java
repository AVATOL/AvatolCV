package edu.oregonstate.eecs.iis.avatolcv.ui.javafx;

import java.io.IOException;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.RadioButton;
import edu.oregonstate.eecs.iis.avatolcv.core.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.core.ScoringAlgorithms;
import edu.oregonstate.eecs.iis.avatolcv.core.SessionInfo;
import edu.oregonstate.eecs.iis.avatolcv.core.StepController;
import edu.oregonstate.eecs.iis.avatolcv.steps.SessionFocusStep;

public class SessionFocusStepController implements StepController {
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
            if (radioPresenceAbsence.isSelected()){
                this.focusStep.setScoringAlgInfo(ScoringAlgorithms.ScoringSessionFocus.SPECIMEN_PART_PRESENCE_ABSENCE, presenceAbsenceAlgChoice.getValue());
            }
            else if (radioShape.isSelected()){
                this.focusStep.setScoringAlgInfo(ScoringAlgorithms.ScoringSessionFocus.SPECIMEN_SHAPE_ASPECT, shapeAlgChoice.getValue());
            }
            else {
                // must be texture
                this.focusStep.setScoringAlgInfo(ScoringAlgorithms.ScoringSessionFocus.SPECIMEN_TEXTURE_ASPECT, textureAlgChoice.getValue());
            }
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
            presenceAbsenceAlgChoice.setItems(paList);
            if (paList.size() > 0){
                presenceAbsenceAlgChoice.setValue(paList.get(0));
                presenceAbsenceAlgChoice.requestLayout();
            }
            shapeAlgChoice.setItems(shapeList);
            if (shapeList.size() > 0){
                shapeAlgChoice.setValue(shapeList.get(0));
            }
            textureAlgChoice.setItems(textureList);
            if (textureList.size() > 0){
                textureAlgChoice.setValue(textureList.get(0));
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
    public void executeDataLoadPhase() throws AvatolCVException {
        // nothing to be done
    }
    @Override
    public void configureUIForDataLoadPhase() {
        // nothing to be done
    }
    @Override
    public boolean isDataLoadPhaseComplete() {
        // not relevant
        return true;
    }


}
