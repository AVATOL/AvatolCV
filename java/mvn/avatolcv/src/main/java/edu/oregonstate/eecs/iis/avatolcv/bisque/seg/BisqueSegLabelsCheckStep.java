package edu.oregonstate.eecs.iis.avatolcv.bisque.seg;

import java.io.File;
import java.util.List;

import edu.oregonstate.eecs.iis.avatolcv.bisque.BisqueSessionData;
import edu.oregonstate.eecs.iis.avatolcv.core.Step;
import edu.oregonstate.eecs.iis.avatolcv.core.View;
import edu.oregonstate.eecs.iis.avatolcv.ws.BisqueSessionException;
import edu.oregonstate.eecs.iis.avatolcv.ws.bisque.BisqueImage;

/*
 * This step will
 * - check to see if segmentation label files exist already
 * - if so, show a screen that says that labels already exist and will be shown if you want to amend.   
 * - if not, show a screen that coaches the user in how to label
 */
public class BisqueSegLabelsCheckStep implements Step {
	private static final String FILESEP = System.getProperty("file.separator");
	public static final String GROUND_TRUTH_SUFFIX = "_groundTruth";
	private BisqueSessionData sessionData = null;
	private SegmentationSessionData segSessionData = null;
	private View view = null;
	private boolean segLabelDetectionHasBeenRun = false;
	// - go through heroic actions to segment, save with filenames consistent with originals (suffix groundtruth)

	public BisqueSegLabelsCheckStep(View view, BisqueSessionData sessionData){
		this.sessionData = sessionData;
		this.segSessionData = sessionData.getSegmentationSessionData();
		this.view = view;
	}
	@Override
	public void consumeProvidedData() throws BisqueSessionException {
		//nothing to do
	}
	public boolean segmentationLabelsExist(){
		segLabelDetectionHasBeenRun = true;
		String segLabelDir = this.segSessionData.getSegmentationLabelDir();
		List<BisqueImage> imagesToLookFor = this.sessionData.getCurrentImages();
		for (BisqueImage image : imagesToLookFor){
			List<Integer> imageWidths = this.sessionData.getImageWidths();
			for (Integer imageWidth : imageWidths){
				String pathOfSegLabelFile = segLabelDir + FILESEP + image.getImageFileRootname(imageWidth) + GROUND_TRUTH_SUFFIX;
				File f = new File(pathOfSegLabelFile);
				if (f.exists()){
					return true;
				}
			}
		}
		return false;
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
