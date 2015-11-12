package edu.oregonstate.eecs.iis.avatolcv.ui.javafx;

import java.io.IOException;
import java.util.Collections;
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
import edu.oregonstate.eecs.iis.avatolcv.steps.SegmentationConfigurationStep;

public class SegmentationConfigurationStepController implements StepController {
    public RadioButton radioSegSkip = null;
	public ChoiceBox<String> segAlgChoiceBox = null;
	public TextArea segAlgNotes = null;
    private SegmentationConfigurationStep step = null;
    private String fxmlDocName = null;
    public SegmentationConfigurationStepController(SegmentationConfigurationStep step, String fxmlDocName){
        this.step = step;
        this.fxmlDocName = fxmlDocName;
    }
    @Override
    public boolean consumeUIData() {
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
            List<String> segAlgNames = this.step.getSegmentationAlgNames();
            Collections.sort(segAlgNames);
            this.segAlgChoiceBox.getItems().addAll(segAlgNames);
            this.segAlgChoiceBox.setValue(this.segAlgChoiceBox.getItems().get(0));
            this.segAlgChoiceBox.getSelectionModel().selectedIndexProperty().addListener(new AlgChangeListener(this.segAlgChoiceBox, this.segAlgNotes, this.step));
            
            if (radioSegSkip.isSelected()){
                skipSegmentationSelected();
            }
            else {
                useSegmentationSelected();
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
        private SegmentationConfigurationStep step;
        public AlgChangeListener(ChoiceBox<String> cb, TextArea ta, SegmentationConfigurationStep step){
            this.ta = ta;
            this.cb = cb;
            this.step = step;
        }
        @Override
        public void changed(ObservableValue ov, Number value, Number newValue) {
            try {
                String description = step.getSegmentationAlgDescription((String)cb.getItems().get((Integer)newValue));
                ta.setText(description);
            }
            catch(AvatolCVException e){
                AvatolCVExceptionExpresserJavaFX.instance.showException(e, "Problem loading algorithm description ");
            }
        }
    }
    public void skipSegmentationSelected(){
        // disable the algChooser
        segAlgChoiceBox.setDisable(true);
        // clear the algDescription
        this.segAlgNotes.setText("");
        this.step.setIsAglorithmChosen(false);
        this.step.setChosenAlgorithm(null);
    }
    public void useSegmentationSelected(){
        // enable the algChooser
        segAlgChoiceBox.setDisable(false);
        // show the alg description
        try {
            this.step.setChosenAlgorithm(segAlgChoiceBox.getValue());
            String description = this.step.getSegmentationAlgDescription(segAlgChoiceBox.getValue());
            segAlgNotes.setText(description);
        }
        catch(AvatolCVException e){
            AvatolCVExceptionExpresserJavaFX.instance.showException(e, "Problem loading algorithm description ");
        }

        this.step.setIsAglorithmChosen(true);
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
