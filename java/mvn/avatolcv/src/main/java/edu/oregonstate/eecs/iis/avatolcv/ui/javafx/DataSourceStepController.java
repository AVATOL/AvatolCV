package edu.oregonstate.eecs.iis.avatolcv.ui.javafx;

import java.io.IOException;
import java.util.Hashtable;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.RadioButton;
import edu.oregonstate.eecs.iis.avatolcv.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.javafxui.AvatolCVExceptionExpresserJavaFX;
import edu.oregonstate.eecs.iis.avatolcv.javafxui.AvatolCVJavaFX;
import edu.oregonstate.eecs.iis.avatolcv.session.SessionInfo;
import edu.oregonstate.eecs.iis.avatolcv.session.StepController;
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
    	Hashtable<String, String> answerHash = new Hashtable<String, String>();
        if (radioMorphobank.isSelected()){
			answerHash.put("chosenDataSource", "Morphobank");
        	this.dataSourceStep.setDataSourceToMorphobank();
        }
        else if (radioBisque.isSelected()){
			answerHash.put("chosenDataSource", "Bisque");
        	this.dataSourceStep.activateBisqueDataSource();
        }
        else {
			answerHash.put("chosenDataSource", "FileSystem");
        	this.dataSourceStep.activateFileSystemDataSource();
        }
        this.dataSourceStep.saveAnswers(answerHash);
        try {
            this.dataSourceStep.consumeProvidedData();
            return true;
        }
        catch(Exception e){
            AvatolCVExceptionExpresserJavaFX.instance.showException(e, "Problem activating data source");
            return false;
        }
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
            if (this.dataSourceStep.hasPriorAnswers()){
            	Hashtable<String, String> priorAnswers = this.dataSourceStep.getPriorAnswers();
            	String chosenDataSource = priorAnswers.get("chosenDataSource");
            	if (chosenDataSource.equals("FileSystem")){
            		radioFileSystem.setSelected(true);
            	}
            	else if (chosenDataSource.equals("Bisque")){
            		radioBisque.setSelected(true);
            	}
            	else {
            		radioMorphobank.setSelected(true);
            	}
            }
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
    public void executeFollowUpDataLoadPhase() throws AvatolCVException {
     // nothing to be done
    }
    @Override
    public void configureUIForFollowUpDataLoadPhase() {
     // nothing to be done
    }
    @Override
    public boolean isFollowUpDataLoadPhaseComplete() {
     // not relevant
        return true;
    }


}
