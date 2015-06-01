package edu.oregonstate.eecs.iis.avatolcv.morphobank.javafx;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import edu.oregonstate.eecs.iis.avatolcv.core.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.morphobank.MBLoginStep;
import edu.oregonstate.eecs.iis.avatolcv.morphobank.MBMatrixChoiceStep;

public class MBMatrixChoiceStepController implements StepController {
    public ComboBox<String> selectedDataset;
    private MBMatrixChoiceStep step;
    private String fxmlDocName;
    private List<String> matrices = null;
	public MBMatrixChoiceStepController(MBMatrixChoiceStep step, String fxmlDocName){
		this.step = step;
		this.fxmlDocName = fxmlDocName;
	}
	@Override
	public boolean consumeUIData() {
		try {
			this.step.setChosenMatrix((String)this.selectedDataset.getValue());
			this.step.consumeProvidedData();
			return true;
		}
		catch (AvatolCVException ace){
			return false;
		}
	}

	@Override
	public void clearUIFields() {
	    selectedDataset.setValue(matrices.get(0));
	}

	@Override
	public Node getContentNode() throws AvatolCVException {
		try {
        	System.out.println("trying to load" +  this.fxmlDocName);
            FXMLLoader loader = new FXMLLoader(this.getClass().getResource(this.fxmlDocName));
            loader.setController(this);
            Node content = loader.load();
            this.matrices = this.step.getAvailableMatrices();
            if (matrices.size() < 1){
            	throw new AvatolCVException("no valid matrices detected.");
            }
            Collections.sort(matrices);
    		for (String m : matrices){
    			ObservableList<String> list = selectedDataset.getItems();
    			list.add(m);
    		}
    		selectedDataset.setValue(matrices.get(0));
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
