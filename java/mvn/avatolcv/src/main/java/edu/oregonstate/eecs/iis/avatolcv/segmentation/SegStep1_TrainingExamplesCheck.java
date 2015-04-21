package edu.oregonstate.eecs.iis.avatolcv.segmentation;

import java.io.File;
import java.util.List;

import edu.oregonstate.eecs.iis.avatolcv.core.ImageInfo;
import edu.oregonstate.eecs.iis.avatolcv.core.Step;
import edu.oregonstate.eecs.iis.avatolcv.core.View;
import edu.oregonstate.eecs.iis.avatolcv.core.AvatolCVException;

public class SegStep1_TrainingExamplesCheck implements Step {

	private View view = null;	
	private boolean segLabelFileDetectionHasBeenRun = false;
    //private SegmentationToolHarness segToolHarness= null;
	private SegmentationSessionData ssd = null;
	public SegStep1_TrainingExamplesCheck(View view, SegmentationSessionData ssd){
		this.ssd = ssd;
		this.view = view;
	}
	/*public SegLabelsCheckStep(View view, SegmentationToolHarness segToolHarness){
		this.segToolHarness = segToolHarness;
		
		this.view = view;
	}*/
	@Override
	public void consumeProvidedData() throws AvatolCVException {
		// TODO Auto-generated method stub

	}
	
	public int percentSegTrainingFileExist() throws SegmentationException {
		segLabelFileDetectionHasBeenRun = true;
		String segLabelDirPath = this.ssd.getSegmentationLabelDir();
		File segLabelDir = new File(segLabelDirPath);
		if (!segLabelDir.isDirectory()){
			throw new SegmentationException("given segmentationLabelDir does not exist " + segLabelDirPath);
		}
		File[] labelFiles = segLabelDir.listFiles();
		
		/*
		 * to avoid having to know about potential suffixes on the root names, search just by root names
		 */
		List<ImageInfo> candidateImages = ssd.getCandidateImages();
		int count = 0;
		int targetCount = candidateImages.size();
		for (ImageInfo ii : candidateImages){
			
			String fileRootToLookFor  = ii.getFilename_IdName();
			for (File f : labelFiles){
				if (f.getName().startsWith(fileRootToLookFor)){
					count++;
				}
			}
		}
		int percent = (int)(100 * ((double)count / (double)targetCount));
		return percent;
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
