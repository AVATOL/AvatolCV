package edu.oregonstate.eecs.iis.avatolcv.ui.javafx;

import java.io.IOException;
import java.util.Hashtable;
import java.util.List;

import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import edu.oregonstate.eecs.iis.avatolcv.core.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.core.ChoiceItem;
import edu.oregonstate.eecs.iis.avatolcv.core.DataFilter;
import edu.oregonstate.eecs.iis.avatolcv.core.DataFilter.Pair;
import edu.oregonstate.eecs.iis.avatolcv.core.StepController;
import edu.oregonstate.eecs.iis.avatolcv.steps.ScoringConcernStep;
import edu.oregonstate.eecs.iis.avatolcv.steps.SummaryFilterStep;

public class SummaryFilterStepController implements StepController {
    public TextArea summaryTextArea;
    public Tab summaryTab;
    public ScrollPane summaryScrollPane;
    public TabPane summaryFilterTabPane;
    public GridPane filterGridPane;
    private SummaryFilterStep step;
    private String fxmlDocName;

    public SummaryFilterStepController(SummaryFilterStep step, String fxmlDocName){
        this.step = step;
        this.fxmlDocName = fxmlDocName;
    }
    @Override
    public boolean consumeUIData() {
    	Hashtable<String, String> answerHash = new Hashtable<String, String>();
    	// TODO - figure out how to remember filter answers
		this.step.saveAnswers(answerHash);
        return true;
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
            populateFilterGridPane();
            return content;
        }
        catch(IOException ioe){
            throw new AvatolCVException("problem loading ui " + fxmlDocName + " for controller " + this.getClass().getName() + " single character scope");
        }
    }

    private void populateFilterGridPane() throws AvatolCVException {
        DataFilter filter = this.step.getDataFilter();
        List<Pair> pairs = filter.getItems();
        int row = 1;
        for (Pair p : pairs){
            CheckBox cb = new CheckBox();
            cb.setSelected(true);
            filterGridPane.add(cb, 0, row);
            Label nameLabel = new Label(p.getName());
            filterGridPane.add(nameLabel, 1, row);
            Label valueLabel = new Label(p.getValue());
            filterGridPane.add(valueLabel, 2, row);
            if (!p.isEditable()){
                cb.setDisable(true);
                nameLabel.setDisable(true);
                valueLabel.setDisable(true);
            }
            row++;
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
        return false;
    }

}
