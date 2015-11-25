package edu.oregonstate.eecs.iis.avatolcv.ui.javafx;

import java.io.IOException;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import edu.oregonstate.eecs.iis.avatolcv.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.core.StepController;
import edu.oregonstate.eecs.iis.avatolcv.javafxui.AvatolCVExceptionExpresserJavaFX;
import edu.oregonstate.eecs.iis.avatolcv.steps.OrientationConfigurationStep;

public class OrientationConfigurationStepController implements StepController {
    private static final String KEY_ORIENT_ALG_CHOICE = "orientAlgChoice";
    private static final String SEG_SKIP = "skipSegmentation";
    public RadioButton radioOrientSkip = null;
    public ChoiceBox<String> orientAlgChoiceBox = null;
    public TextArea orientAlgNotes = null;
    private OrientationConfigurationStep step = null;
    private String fxmlDocName = null;
    public OrientationConfigurationStepController(OrientationConfigurationStep step, String fxmlDocName){
        this.step = step;
        this.fxmlDocName = fxmlDocName;
    }
    @Override
    public boolean consumeUIData() {
        Hashtable<String, String> answerHash = new Hashtable<String, String>();
        if (radioOrientSkip.isSelected()){
            answerHash.put(KEY_ORIENT_ALG_CHOICE, SEG_SKIP);
            this.step.setIsAlgorithmChosen(false);
            this.step.setChosenAlgorithm(null);
        }
        else {
            String orientAlgName = orientAlgChoiceBox.getValue();
            answerHash.put(KEY_ORIENT_ALG_CHOICE, orientAlgName);
            this.step.setIsAlgorithmChosen(true);
            this.step.setChosenAlgorithm(orientAlgName);
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

    @SuppressWarnings("unchecked")
    @Override
    public Node getContentNode() throws AvatolCVException {
        try {
            System.out.println("trying to load" +  this.fxmlDocName);
            FXMLLoader loader = new FXMLLoader(this.getClass().getResource(this.fxmlDocName));
            loader.setController(this);
            Node content = loader.load();
            List<String> orientAlgNames = this.step.getOrientationAlgNames();
            Collections.sort(orientAlgNames);
            this.orientAlgChoiceBox.getItems().addAll(orientAlgNames);
            this.orientAlgChoiceBox.setValue(this.orientAlgChoiceBox.getItems().get(0));
            this.orientAlgChoiceBox.getSelectionModel().selectedIndexProperty().addListener(new AlgChangeListener(this.orientAlgChoiceBox, this.orientAlgNotes, this.step));
            
            if (radioOrientSkip.isSelected()){
                skipOrientationSelected();
            }
            else {
                useOrientationSelected();
            }
            
            return content;
        }
        catch(IOException ioe){
            throw new AvatolCVException("problem loading ui " + fxmlDocName + " for controller " + this.getClass().getName());
        } 
    }
    public class AlgChangeListener implements ChangeListener<Number> {
        private ChoiceBox<String> cb;
        private TextArea ta;
        private OrientationConfigurationStep step;
        public AlgChangeListener(ChoiceBox<String> cb, TextArea ta, OrientationConfigurationStep step){
            this.ta = ta;
            this.cb = cb;
            this.step = step;
        }
        @Override
        public void changed(ObservableValue ov, Number value, Number newValue) {
            try {
                String description = step.getOrientationAlgDescription((String)cb.getItems().get((Integer)newValue));
                ta.setText(description);
            }
            catch(Exception e){
                AvatolCVExceptionExpresserJavaFX.instance.showException(e, "Problem loading algorithm description ");
            }
        }
    }
    public void skipOrientationSelected(){
        // disable the algChooser
        orientAlgChoiceBox.setDisable(true);
        // clear the algDescription
        this.orientAlgNotes.setText("");
    }
    public void useOrientationSelected(){
        // enable the algChooser
        orientAlgChoiceBox.setDisable(false);
        // show the alg description
        try {
            String description = this.step.getOrientationAlgDescription(orientAlgChoiceBox.getValue());
            orientAlgNotes.setText(description);
        }
        catch(Exception e){
            AvatolCVExceptionExpresserJavaFX.instance.showException(e, "Problem loading algorithm description ");
        }
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

