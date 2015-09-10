package edu.oregonstate.eecs.iis.avatolcv.steps;

import edu.oregonstate.eecs.iis.avatolcv.core.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.core.SessionInfo;
import edu.oregonstate.eecs.iis.avatolcv.core.Step;
import edu.oregonstate.eecs.iis.avatolcv.ui.javafx.ImagePullStepController;

public class ImagePullStep implements Step {
    private SessionInfo sessionInfo;
    public ImagePullStep(SessionInfo sessionInfo){
        this.sessionInfo = sessionInfo;
    }
    @Override
    public void init() throws AvatolCVException {
        // TODO Auto-generated method stub

    }

    @Override
    public void consumeProvidedData() throws AvatolCVException {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean hasDataLoadPhase() {
        // TODO Auto-generated method stub
        return false;
    }

    public void downloadImages(ImagePullStepController controller, String processName) throws AvatolCVException {
        
    }
}
