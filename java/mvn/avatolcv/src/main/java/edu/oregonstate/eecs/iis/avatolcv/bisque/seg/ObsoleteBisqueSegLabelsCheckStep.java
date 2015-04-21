package edu.oregonstate.eecs.iis.avatolcv.bisque.seg;

import java.io.File;
import java.util.List;

import edu.oregonstate.eecs.iis.avatolcv.bisque.BisqueSessionData;
import edu.oregonstate.eecs.iis.avatolcv.core.Step;
import edu.oregonstate.eecs.iis.avatolcv.core.View;
import edu.oregonstate.eecs.iis.avatolcv.core.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.segmentation.SegmentationSessionData;
import edu.oregonstate.eecs.iis.avatolcv.ws.bisque.BisqueImage;

/*
 * This step will
 * - check to see if segmentation label files exist already
 * - if so, show a screen that says that labels already exist and will be shown if you want to amend.   
 * - if not, show a screen that coaches the user in how to label
 */
public class ObsoleteBisqueSegLabelsCheckStep implements Step {
	private static final String FILESEP = System.getProperty("file.separator");
	public static final String GROUND_TRUTH_SUFFIX = "_groundtruth";
	private BisqueSessionData sessionData = null;
	private SegmentationSessionData segSessionData = null;
	private View view = null;
	private boolean segLabelDetectionHasBeenRun = false;
	// - go through heroic actions to segment, save with filenames consistent with originals (suffix groundtruth)

	public ObsoleteBisqueSegLabelsCheckStep(View view, BisqueSessionData sessionData){
		this.sessionData = sessionData;
		this.segSessionData = sessionData.getSegmentationSessionData();
		this.view = view;
	}
	@Override
	public void consumeProvidedData() throws AvatolCVException {
		//nothing to do
	}
	
	@Override
	public boolean needsAnswering() {
		if (segLabelDetectionHasBeenRun){
			return false;
		}
		return true;
	}

	@Override
	public View getView() {
		return this.view;
	}

}
