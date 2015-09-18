package edu.oregonstate.eecs.iis.avatolcv.obsolete.bisque;

import edu.oregonstate.eecs.iis.avatolcv.core.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.core.Step;
import edu.oregonstate.eecs.iis.avatolcv.core.StepSequence;
import edu.oregonstate.eecs.iis.avatolcv.core.View;
import edu.oregonstate.eecs.iis.avatolcv.segmentation.SegmentationSessionData;

public class BisqueCharScoringContainerStep implements Step {
    private StepSequence ss = new StepSequence();
    private BisqueSessionData bsd = null;
    public void appendStep(Step s){
        ss.appendStep(s);
    }
    public BisqueCharScoringContainerStep(BisqueSessionData bsd){
        this.bsd = bsd;
    }
    public StepSequence getStepSequence(){
        return this.ss;
    }
    @Override
    public void consumeProvidedData() throws AvatolCVException {
        // TODO Auto-generated method stub

    }


    @Override
    public void init() {
        // TODO Auto-generated method stub
        
    }

}
