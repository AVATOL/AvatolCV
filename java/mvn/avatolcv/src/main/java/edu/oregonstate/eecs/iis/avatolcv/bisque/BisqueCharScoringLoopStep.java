package edu.oregonstate.eecs.iis.avatolcv.bisque;

import edu.oregonstate.eecs.iis.avatolcv.core.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.core.Step;
import edu.oregonstate.eecs.iis.avatolcv.core.StepSequence;
import edu.oregonstate.eecs.iis.avatolcv.core.View;
import edu.oregonstate.eecs.iis.avatolcv.segmentation.SegmentationSessionData;

public class BisqueCharScoringLoopStep implements Step {
    private StepSequence ss = new StepSequence();
    private BisqueSessionData bsd = null;
    public void appendStep(Step s){
        ss.appendStep(s);
    }
    public BisqueCharScoringLoopStep(BisqueSessionData bsd){
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
    public boolean needsAnswering() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public View getView() {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public void init() {
        // TODO Auto-generated method stub
        
    }

}
