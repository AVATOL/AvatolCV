package edu.oregonstate.eecs.iis.avatolcv.morphobank.javafx;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import edu.oregonstate.eecs.iis.avatolcv.core.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.core.ScoringAlgorithms;
import edu.oregonstate.eecs.iis.avatolcv.morphobank.charscore.MBTrainingExampleCheckStep;

public class MBTrainingExampleCheckStepController implements StepController  {
    public GridPane trainingTestingGridPane;
    private MBTrainingExampleCheckStep step = null;
    private String fxmlDocName;
    public MBTrainingExampleCheckStepController(MBTrainingExampleCheckStep step, String fxmlDocName){
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
            String trainingTestingDescriminatorName = this.step.getTrainingTestingDescriminatorName();
            LEFT OFF HERE
            - get all the taxa
            - for each taxon, geth the count of training images and test images
            - add a row to the gridpane
            - add a radio button for each, with counts

            return content;
        }
        catch(IOException ioe){
            throw new AvatolCVException("problem loading ui " + fxmlDocName + " for controller " + this.getClass().getName());
        }
    }

}
