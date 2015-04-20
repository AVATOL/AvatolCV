package edu.oregonstate.eecs.iis.avatolcv.segmentation;

import java.io.File;
import java.util.List;

import edu.oregonstate.eecs.iis.avatolcv.bisque.BisqueSessionData;
import edu.oregonstate.eecs.iis.avatolcv.core.ImageInfo;
import edu.oregonstate.eecs.iis.avatolcv.core.Step;
import edu.oregonstate.eecs.iis.avatolcv.core.View;
import edu.oregonstate.eecs.iis.avatolcv.ws.BisqueSessionException;
import edu.oregonstate.eecs.iis.avatolcv.ws.bisque.BisqueImage;

public class SegLabelsCheckStep implements Step {
	private static final String FILESEP = System.getProperty("file.separator");
	private View view = null;	
	private boolean segLabelFileDetectionHasBeenRun = false;
    private SegmentationToolHarness segToolHarness= null;
	public SegLabelsCheckStep(View view, SegmentationToolHarness segToolHarness){
		this.segToolHarness = segToolHarness;
		
		this.view = view;
	}
	@Override
	public void consumeProvidedData() throws BisqueSessionException {
		// TODO Auto-generated method stub

	}
	public boolean segLabelFileExist() throws SegmentationException {
		segLabelFileDetectionHasBeenRun = true;
		String segLabelDirPath = this.segToolHarness.getSegmentationLabelDir();
		File segLabelDir = new File(segLabelDirPath);
		if (!segLabelDir.isDirectory()){
			throw new SegmentationException("given segmentationLabelDir does not exist " + segLabelDirPath);
		}
		File[] labelFiles = segLabelDir.listFiles();
//		List<Image> imagesToLookFor = this.segToolHarness.getAllImages();
		/*
		 * to avoid having to know about potential suffixes on the root names, search just by root names
		 */
//		for (Image image : imagesToLookFor){
			
//			image.getImageFileRootname() + GROUND_TRUTH_SUFFIX;
//				File f = new File(pathOfSegLabelFile);
//				if (f.exists()){
//				}
		
//		}
		return false;
	}
	@Override
	public boolean needsAnswering() {
		if (segLabelFileDetectionHasBeenRun){
			return false;
		}
		return true;
	}

	@Override
	public View getView() {
		return this.view;
	}

}
