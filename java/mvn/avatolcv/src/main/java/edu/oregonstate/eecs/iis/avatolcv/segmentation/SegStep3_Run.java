package edu.oregonstate.eecs.iis.avatolcv.segmentation;

import edu.oregonstate.eecs.iis.avatolcv.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.algorithm.AlgorithmRunner;
import edu.oregonstate.eecs.iis.avatolcv.session.ProgressPresenter;
import edu.oregonstate.eecs.iis.avatolcv.steps.Step;
import edu.oregonstate.eecs.iis.obsolete.avatolcv.View;

public class SegStep3_Run implements Step {
	private String view = null;	
	private SegmentationSessionData ssd = null;
	private AlgorithmRunner runner = null;
	public SegStep3_Run(String view, SegmentationSessionData ssd, AlgorithmRunner runner){
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
}
