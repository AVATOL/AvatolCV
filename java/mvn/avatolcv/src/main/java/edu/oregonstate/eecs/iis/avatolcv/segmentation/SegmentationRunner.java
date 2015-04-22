package edu.oregonstate.eecs.iis.avatolcv.segmentation;

import edu.oregonstate.eecs.iis.avatolcv.core.ProgressPresenter;

public interface SegmentationRunner {
	public boolean isRunComplete();
	public void run(String configFilePath, ProgressPresenter pp);
}
