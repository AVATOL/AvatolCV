package edu.oregonstate.eecs.iis.avatolcv.segmentation;

import edu.oregonstate.eecs.iis.avatolcv.core.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.core.ProgressPresenter;
import edu.oregonstate.eecs.iis.avatolcv.core.Step;
import edu.oregonstate.eecs.iis.avatolcv.core.View;
import edu.oregonstate.eecs.iis.avatolcv.generic.AlgorithmRunner;

public class SegStep3_Run implements Step {
	private View view = null;	
	private SegmentationSessionData ssd = null;
	private AlgorithmRunner runner = null;
	public SegStep3_Run(View view, SegmentationSessionData ssd, AlgorithmRunner runner){
		this.ssd = ssd;
		this.view = view;
		this.runner = runner;
	}
	public void run(ProgressPresenter pp){
		this.ssd.cleanResults();
		this.runner.run(ssd.getConfigFilePath(), pp);
	}

    @Override
    public void init() throws AvatolCVException {
        // nothing to do
    }
	@Override
	public void consumeProvidedData() throws AvatolCVException {
		// pull the result files into the program's consciousness
		this.ssd.getImagesForStage().reload();	
	}

	@Override
	public boolean needsAnswering() {
		return !runner.isRunComplete();
	}

	@Override
	public View getView() {
		return this.view;
	}

}
