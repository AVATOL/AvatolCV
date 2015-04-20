package edu.oregonstate.eecs.iis.avatolcv.bisque.seg;

import edu.oregonstate.eecs.iis.avatolcv.bisque.BisqueSessionData;
import edu.oregonstate.eecs.iis.avatolcv.core.Step;
import edu.oregonstate.eecs.iis.avatolcv.core.View;
import edu.oregonstate.eecs.iis.avatolcv.ws.BisqueSessionException;

/*
 * This step supports the UI that allows for segmentation labeling or reviewing of segmentation labels
 */
public class SegmentationLabelStep implements Step {
	private View view = null;
	private BisqueSessionData sessionData = null;
	private SegmentationSessionData segSessionData = null;
	public SegmentationLabelStep(View view, BisqueSessionData sessionData){
		this.view = view;
		this.sessionData = sessionData;
		this.segSessionData = sessionData.getSegmentationSessionData();
	}
	public List<BisqueImage>
	@Override
	public void consumeProvidedData() throws BisqueSessionException {
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
