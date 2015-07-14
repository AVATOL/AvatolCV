package edu.oregonstate.eecs.iis.avatolcv.core;


public interface AlgorithmRunner {
	public boolean isRunComplete();
	public void run(String configFilePath, ProgressPresenter pp);
}
