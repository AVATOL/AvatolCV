package edu.oregonstate.eecs.iis.avatolcv.segmentation;

import edu.oregonstate.eecs.iis.avatolcv.core.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.core.Step;
import edu.oregonstate.eecs.iis.avatolcv.core.View;

public class SegStep4_Review implements Step {
	private View view = null;	
	private SegmentationSessionData ssd = null;
	public SegStep4_Review(View view, SegmentationSessionData ssd){
		this.ssd = ssd;
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
