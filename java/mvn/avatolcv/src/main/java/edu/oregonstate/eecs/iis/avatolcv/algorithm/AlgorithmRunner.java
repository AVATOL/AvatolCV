package edu.oregonstate.eecs.iis.avatolcv.algorithm;

import edu.oregonstate.eecs.iis.avatolcv.core.ProgressPresenter;


public interface AlgorithmRunner {
	public boolean isRunComplete();
	public void run(String configFilePath, ProgressPresenter pp);
}
