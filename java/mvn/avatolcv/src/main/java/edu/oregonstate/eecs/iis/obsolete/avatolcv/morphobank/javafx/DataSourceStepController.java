package edu.oregonstate.eecs.iis.obsolete.avatolcv.morphobank.javafx;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import edu.oregonstate.eecs.iis.avatolcv.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.core.StepController;
import edu.oregonstate.eecs.iis.obsolete.avatolcv.morphobank.DataSourceStep;

public class DataSourceStepController implements StepController {

    private DataSourceStep dataSourceStep;
    private String fxmlDocName;
    public DataSourceStepController(DataSourceStep dataSourceStep, String fxmlDocName){
        this.dataSourceStep = dataSourceStep;
        this.fxmlDocName = fxmlDocName;
    }
    @Override
    public boolean consumeUIData() {
        // for now, everything is hardcoded to Morphobank
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
