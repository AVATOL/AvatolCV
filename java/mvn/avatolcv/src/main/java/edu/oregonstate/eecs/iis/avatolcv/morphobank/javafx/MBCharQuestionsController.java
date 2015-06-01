package edu.oregonstate.eecs.iis.avatolcv.morphobank.javafx;

import java.util.ArrayList;
import java.util.List;
import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import edu.oregonstate.eecs.iis.avatolcv.core.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.core.ScoringAlgorithms;
import edu.oregonstate.eecs.iis.avatolcv.morphobank.MBCharQuestionsStep;

public class MBCharQuestionsController implements StepController {
    public VBox radioButtonVbox;
    private MBCharQuestionsStep step;
    private String fxmlDocName;
    private List<RadioButton> radioButtons = null;
    public MBCharQuestionsController(MBCharQuestionsStep step, String fxmlDocName){
        this.step = step;
        this.fxmlDocName = fxmlDocName;
    }
    
    @Override
    public boolean consumeUIData() {
        ScoringAlgorithms algs = this.step.getScoringAlgorithms();
        try {
            for (RadioButton rb : this.radioButtons){
                if (rb.isSelected()){
                    String algFocus = rb.getText();
                    String algName = algs.getNameForScoringFocus(algFocus);
                    this.step.setChosenAlgorithm(algName);
                }
            }
            return true;
        }
        catch(AvatolCVException ace){
            ace.printStackTrace();
            return false;
        }
        
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
            radioButtonVbox.getChildren().clear();
            radioButtons = new ArrayList<RadioButton>();
            ScoringAlgorithms algs = this.step.getScoringAlgorithms();
            List<String> algNames = algs.getAlgNames();
            ToggleGroup toggleGroup = new ToggleGroup();
            for (String name : algNames){
                HBox hbox = new HBox();
                String algFocus = algs.getScoringFocusForAlgName(name);
                RadioButton rb = new RadioButton(algFocus);
                rb.setToggleGroup(toggleGroup);
                rb.setDisable(!algs.isAlgorithmEnabled(name));
                radioButtons.add(rb);
                Label algNameLabel = new Label(" (" + name + ") ");
                algNameLabel.setDisable(!algs.isAlgorithmEnabled(name));
                hbox.getChildren().addAll(rb, algNameLabel);
                radioButtonVbox.getChildren().add(hbox);
            }
            return content;
        }
        catch(IOException ioe){
            throw new AvatolCVException("problem loading ui " + fxmlDocName + " for controller " + this.getClass().getName());
        }
    }

    @Override
    public boolean hasActionToAutoStart() {
        return false;
    }

    @Override
    public void startAction() throws AvatolCVException {
    }

}
