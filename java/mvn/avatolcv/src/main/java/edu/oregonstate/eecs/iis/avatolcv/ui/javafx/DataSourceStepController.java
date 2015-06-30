package edu.oregonstate.eecs.iis.avatolcv.ui.javafx;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.RadioButton;
import edu.oregonstate.eecs.iis.avatolcv.core.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.core.SessionInfo;
import edu.oregonstate.eecs.iis.avatolcv.core.StepController;
import edu.oregonstate.eecs.iis.avatolcv.steps.DataSourceStep;

public class DataSourceStepController implements StepController {
    public RadioButton radioMorphobank;
    public RadioButton radioBisque;
    public RadioButton radioFileSystem;
 
    private DataSourceStep dataSourceStep;
    private SessionInfo sessionInfo;
    private String fxmlDocName;
    public DataSourceStepController(DataSourceStep dataSourceStep, SessionInfo sessionInfo, String fxmlDocName){
        this.dataSourceStep = dataSourceStep;
        this.fxmlDocName = fxmlDocName;
        this.sessionInfo = sessionInfo;
    }
    @Override
    public boolean consumeUIData() {
        if (radioMorphobank.isSelected()){
        	DataSource dataSource
        }
        else if (radioBisque.isSelected()){
        	
        }
        else {
        	
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
            //System.out.println("trying to load" +  this.fxmlDocName);
            FXMLLoader loader = new FXMLLoader(this.getClass().getResource(this.fxmlDocName));
            loader.setController(this);
            Node content = loader.load();
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


}
