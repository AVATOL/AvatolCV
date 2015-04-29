package edu.oregonstate.eecs.iis.avatolcv.segmentation;

import edu.oregonstate.eecs.iis.avatolcv.core.ProgressPresenter;
import edu.oregonstate.eecs.iis.avatolcv.generic.AlgorithmRunner;

public class BogusSegmentationRunner implements AlgorithmRunner {
	private boolean runComplete = false;
	@Override
	public boolean isRunComplete() {
		return runComplete;
	}

	@Override
	public void run(String configFilePath, ProgressPresenter pp) {
		this.runComplete = true;
	}

}
