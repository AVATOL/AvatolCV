package edu.oregonstate.eecs.iis.avatolcv.ui.javafx;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import edu.oregonstate.eecs.iis.avatolcv.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.core.StepController;
import edu.oregonstate.eecs.iis.avatolcv.steps.ScoringConfigurationStep;
import edu.oregonstate.eecs.iis.avatolcv.steps.SegmentationConfigurationStep;
import edu.oregonstate.eecs.iis.avatolcv.ui.javafx.SegmentationConfigurationStepController.AlgChangeListener;

public class ScoringConfigurationStepController implements StepController {
	private ScoringConfigurationStep step = null;
    private String fxmlDocName = null;
    public ScoringConfigurationStepController(ScoringConfigurationStep step, String fxmlDocName){
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


            return content;
        }
        catch(IOException ioe){
            throw new AvatolCVException("problem loading ui " + fxmlDocName + " for controller " + this.getClass().getName());
        } 
    }

	@Override
	public boolean delayEnableNavButtons() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void executeFollowUpDataLoadPhase() throws AvatolCVException {
		// TODO Auto-generated method stub

	}

	@Override
	public void configureUIForFollowUpDataLoadPhase() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isFollowUpDataLoadPhaseComplete() {
		// TODO Auto-generated method stub
		return false;
	}

}
