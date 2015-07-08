package edu.oregonstate.eecs.iis.avatolcv.ui.javafx;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import edu.oregonstate.eecs.iis.avatolcv.core.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.core.StepController;
import edu.oregonstate.eecs.iis.avatolcv.steps.DatasetChoiceStep;

public class DatasetChoiceStepController implements StepController {
    public ComboBox<String> selectedDataset;
    private DatasetChoiceStep step;
    private String fxmlDocName;
    private List<String> datasetNames = null;
	public DatasetChoiceStepController(DatasetChoiceStep step, String fxmlDocName){
		this.step = step;
		this.fxmlDocName = fxmlDocName;
	}
	@Override
	public boolean consumeUIData() {
		try {
			this.step.setChosenDataset((String)this.selectedDataset.getValue());
			this.step.consumeProvidedData();
			return true;
		}
		catch (AvatolCVException ace){
			return false;
		}
	}

	@Override
	public void clearUIFields() {
	    selectedDataset.setValue(datasetNames.get(0));
	}

	@Override
	public Node getContentNode() throws AvatolCVException {
		try {
        	System.out.println("trying to load" +  this.fxmlDocName);
            FXMLLoader loader = new FXMLLoader(this.getClass().getResource(this.fxmlDocName));
            loader.setController(this);
            Node content = loader.load();
            this.datasetNames = this.step.getAvailableDatasets();
            if (datasetNames.size() < 1){
            	throw new AvatolCVException("no valid matrices detected.");
            }
            Collections.sort(datasetNames);
    		for (String m : datasetNames){
    			ObservableList<String> list = selectedDataset.getItems();
    			list.add(m);
    		}
    		selectedDataset.setValue(datasetNames.get(0));
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
        // not relavent
        return true;
    }
}
