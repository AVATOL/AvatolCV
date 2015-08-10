package edu.oregonstate.eecs.iis.avatolcv.ui.javafx;

import java.io.IOException;
import java.util.Hashtable;

import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import edu.oregonstate.eecs.iis.avatolcv.core.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.core.ChoiceItem;
import edu.oregonstate.eecs.iis.avatolcv.core.StepController;
import edu.oregonstate.eecs.iis.avatolcv.steps.ScoringConcernStep;
import edu.oregonstate.eecs.iis.avatolcv.steps.SummaryFilterStep;

public class SummaryFilterStepController implements StepController {
    public TextArea summaryTextArea;
    public Tab summaryTab;
    public ScrollPane summaryScrollPane;
    public TabPane summaryFilterTabPane;
    private SummaryFilterStep step;
    private String fxmlDocName;

    public SummaryFilterStepController(SummaryFilterStep step, String fxmlDocName){
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
            FXMLLoader loader = new FXMLLoader(this.getClass().getResource(this.fxmlDocName));
            loader.setController(this);
            Node content = loader.load();
            summaryTextArea.setText(this.step.getSummaryText());
           
            return content;
        }
        catch(IOException ioe){
            throw new AvatolCVException("problem loading ui " + fxmlDocName + " for controller " + this.getClass().getName() + " single character scope");
        }
    }

    @Override
    public boolean delayEnableNavButtons() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void executeDataLoadPhase() throws AvatolCVException {
        // TODO Auto-generated method stub

    }

    @Override
    public void configureUIForDataLoadPhase() {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean isDataLoadPhaseComplete() {
        // TODO Auto-generated method stub
        return false;
    }

}
