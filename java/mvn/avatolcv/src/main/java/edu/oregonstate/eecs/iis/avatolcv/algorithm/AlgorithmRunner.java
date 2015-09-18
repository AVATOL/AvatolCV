package edu.oregonstate.eecs.iis.avatolcv.algorithm;

import edu.oregonstate.eecs.iis.avatolcv.core.ProgressPresenter;

/**
 * 
 * @author admin-jed
 * 
 * Intended to support running of algorithms, but not sure if it will wind up being dead code or not.(9/18/2015)
 *
 */

public interface AlgorithmRunner {
	public boolean isRunComplete();
	public void run(String configFilePath, ProgressPresenter pp);
}
