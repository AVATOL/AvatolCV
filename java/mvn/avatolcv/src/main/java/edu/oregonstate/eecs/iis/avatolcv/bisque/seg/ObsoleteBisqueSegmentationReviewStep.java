package edu.oregonstate.eecs.iis.avatolcv.bisque.seg;

import edu.oregonstate.eecs.iis.avatolcv.bisque.BisqueSessionData;
import edu.oregonstate.eecs.iis.avatolcv.core.Step;
import edu.oregonstate.eecs.iis.avatolcv.core.View;
import edu.oregonstate.eecs.iis.avatolcv.core.AvatolCVException;

public class ObsoleteBisqueSegmentationReviewStep implements Step {
	private BisqueSessionData sessionData = null;
	private View view = null;
	public ObsoleteBisqueSegmentationReviewStep(View view, BisqueSessionData sessionData){
		this.sessionData = sessionData;
		this.view = view;
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

}
