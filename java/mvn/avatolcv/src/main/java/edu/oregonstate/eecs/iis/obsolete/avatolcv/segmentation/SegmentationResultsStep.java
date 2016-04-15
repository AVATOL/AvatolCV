package edu.oregonstate.eecs.iis.obsolete.avatolcv.segmentation;

import edu.oregonstate.eecs.iis.avatolcv.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.steps.Answerable;
import edu.oregonstate.eecs.iis.avatolcv.steps.Step;

public class SegmentationResultsStep extends Answerable implements Step {

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

    @Override
    public boolean isEnabledByPriorAnswers() {
        return true;
    }
    @Override
	public boolean shouldRenderIfBackingIntoIt() {
		return true;
	}
    
}
