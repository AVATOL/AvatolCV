package edu.oregonstate.eecs.iis.avatolcv.segmentation;

import edu.oregonstate.eecs.iis.avatolcv.core.AlgorithmRunner;
import edu.oregonstate.eecs.iis.avatolcv.core.ProgressPresenter;

public class BogusAlgorithmRunner implements AlgorithmRunner {
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
