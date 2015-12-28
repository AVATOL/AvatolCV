package edu.oregonstate.eecs.iis.avatolcv.ui.javafx;

import java.io.IOException;
import java.util.Hashtable;
import java.util.List;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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
import edu.oregonstate.eecs.iis.avatolcv.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.core.DataFilter;
import edu.oregonstate.eecs.iis.avatolcv.core.DataFilter.FilterItem;
import edu.oregonstate.eecs.iis.avatolcv.datasource.ChoiceItem;
import edu.oregonstate.eecs.iis.avatolcv.javafxui.AvatolCVExceptionExpresserJavaFX;
import edu.oregonstate.eecs.iis.avatolcv.session.StepController;
import edu.oregonstate.eecs.iis.avatolcv.steps.ScoringConcernStep;
import edu.oregonstate.eecs.iis.avatolcv.steps.SummaryFilterStep;

public class SummaryFilterStepController implements StepController {
    //public TextArea summaryTextArea;
    //public Tab summaryTab;
    //public ScrollPane summaryScrollPane;
    //public TabPane summaryFilterTabPane;
    public GridPane filterGrid;
    private SummaryFilterStep step;
    private String fxmlDocName;

    public SummaryFilterStepController(SummaryFilterStep step, String fxmlDocName){
        this.step = step;
        this.fxmlDocName = fxmlDocName;
    }
    @Override
    public boolean consumeUIData() {
    	// filter already has everything for reuse, no need to save answers in the usual way
    	try {
    		this.step.consumeProvidedData();
    	}
    	catch(AvatolCVException ace){
   		 	AvatolCVExceptionExpresserJavaFX.instance.showException(ace, "Problem using filter data " +ace.getMessage());
    	}
        return true;
    }

    @Override
    public void clearUIFields() {
        // NA
    }

    @Override
    public Node getContentNode() throws AvatolCVException {
        try {
            FXMLLoader loader = new FXMLLoader(this.getClass().getResource(this.fxmlDocName));
            loader.setController(this);
            Node content = loader.load();
            //summaryTextArea.setText(this.step.getSummaryText());
            populateFilterGridPane();
            return content;
        }
        catch(IOException ioe){
            throw new AvatolCVException("problem loading ui " + fxmlDocName + " for controller " + this.getClass().getName() + " single character scope");
        }
    }

    private void populateFilterGridPane() throws AvatolCVException {
        DataFilter filter = this.step.getDataFilter();
        List<FilterItem> items = filter.getItems();
        int row = 1;
        for (FilterItem fi : items){
            CheckBox cb = new CheckBox();
            cb.getStyleClass().add("columnValue");
            filterGrid.add(cb, 0, row);
            Label nameLabel = new Label(fi.getKey().getName());
            nameLabel.getStyleClass().add("columnValue");
            filterGrid.add(nameLabel, 1, row);
            Label valueLabel = new Label(fi.getValue().getName());
            valueLabel.getStyleClass().add("columnValue");
            
            //Add a listener that disables or enables the labels based on checkbox value
            cb.selectedProperty().addListener(new ChangeListener<Boolean>() {
                public void changed(ObservableValue<? extends Boolean> ov,
                    Boolean old_val, Boolean new_val) {
                		nameLabel.setDisable(new_val.booleanValue());
                		valueLabel.setDisable(new_val.booleanValue());
                		fi.setSelected(new_val);
                		try {
							filter.persist();
						} catch (AvatolCVException ace) {
							AvatolCVExceptionExpresserJavaFX.instance.showException(ace, "Problem persisting filter data " +ace.getMessage());
						}
                }
            });
            
            filterGrid.add(valueLabel, 2, row);
            if (!fi.isEditable()){
                cb.setDisable(true);
                nameLabel.setDisable(true);
                valueLabel.setDisable(true);
            }
            if (fi.isSelected()){
            	cb.setSelected(true);
            }
            else {
            	cb.setSelected(false);
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
