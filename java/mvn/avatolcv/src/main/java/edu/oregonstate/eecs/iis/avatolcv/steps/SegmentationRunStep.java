package edu.oregonstate.eecs.iis.avatolcv.steps;

import edu.oregonstate.eecs.iis.avatolcv.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.core.SessionInfo;

public class SegmentationRunStep implements Step {
    private SessionInfo sessionInfo = null;
    public SegmentationRunStep(SessionInfo sessionInfo){
        this.sessionInfo = sessionInfo;
    }
    public String getSelectedSegmentationAlgorithm(){
        return this.sessionInfo.getSegmentationAlg();
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
    public boolean hasFollowUpDataLoadPhase() {
        // TODO Auto-generated method stub
        return false;
    }

}
