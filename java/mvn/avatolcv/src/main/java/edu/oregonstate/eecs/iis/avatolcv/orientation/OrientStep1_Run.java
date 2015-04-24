package edu.oregonstate.eecs.iis.avatolcv.orientation;

import edu.oregonstate.eecs.iis.avatolcv.core.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.core.ImagesForStage;
import edu.oregonstate.eecs.iis.avatolcv.core.Step;
import edu.oregonstate.eecs.iis.avatolcv.core.View;
import edu.oregonstate.eecs.iis.avatolcv.segmentation.SegmentationSessionData;

public class OrientStep1_Run implements Step {
	private View view = null;
	private OrientationSessionData osd = null;
	ImagesForStage ifs = null;
	boolean needsAnswering = true;
	public OrientStep1_Run(View view, OrientationSessionData osd){
		this.view = view;
		this.osd = osd;
		this.ifs = osd.getImagesForStage();
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
