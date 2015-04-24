package edu.oregonstate.eecs.iis.avatolcv.segmentation;

import edu.oregonstate.eecs.iis.avatolcv.core.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.core.ProgressPresenter;
import edu.oregonstate.eecs.iis.avatolcv.core.Step;
import edu.oregonstate.eecs.iis.avatolcv.core.View;

public class SegStep3_Run implements Step {
	private View view = null;	
	private SegmentationSessionData ssd = null;
	private SegmentationRunner segRunner = null;
	public SegStep3_Run(View view, SegmentationSessionData ssd, SegmentationRunner segRunner){
		this.ssd = ssd;
		this.view = view;
		this.segRunner = segRunner;
	}
	public void run(ProgressPresenter pp){
		this.ssd.cleanResults();
		this.segRunner.run(ssd.getConfigFilePath(), pp);
	}
	@Override
	public void consumeProvidedData() throws AvatolCVException {
		// pull the result files into the program's consciousness
		this.ssd.getImagesForStage().reload();	
	}

	@Override
	public boolean needsAnswering() {
		return !segRunner.isRunComplete();
	}

	@Override
	public View getView() {
		return this.view;
	}

}
