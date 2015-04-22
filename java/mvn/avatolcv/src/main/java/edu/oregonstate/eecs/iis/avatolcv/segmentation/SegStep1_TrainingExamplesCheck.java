package edu.oregonstate.eecs.iis.avatolcv.segmentation;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import edu.oregonstate.eecs.iis.avatolcv.core.ImageInfo;
import edu.oregonstate.eecs.iis.avatolcv.core.Step;
import edu.oregonstate.eecs.iis.avatolcv.core.View;
import edu.oregonstate.eecs.iis.avatolcv.core.AvatolCVException;

public class SegStep1_TrainingExamplesCheck implements Step {

	private View view = null;	
	private boolean segLabelFileAssessmentHasBeenRun = false;
	private SegmentationSessionData ssd = null;
	SegmentationImages si = null;
	public SegStep1_TrainingExamplesCheck(View view, SegmentationSessionData ssd){
		this.ssd = ssd;
		this.view = view;
	}
	
	@Override
	public void consumeProvidedData() throws AvatolCVException {
		this.ssd.setSegmentationImages(this.si);
	}
	
	public void assess()  throws SegmentationException {
		segLabelFileAssessmentHasBeenRun = true;
		String segTrainingImageDirPath = this.ssd.getSegmentationLabelDir();
		File segLabelDir = new File(segTrainingImageDirPath);
		if (!segLabelDir.isDirectory()){
			throw new SegmentationException("given segmentationLabelDir does not exist " + segTrainingImageDirPath);
		}
		List<ImageInfo> candidateImages = ssd.getCandidateImages();
		si = new SegmentationImages(segTrainingImageDirPath, candidateImages);
		si.reload();
	}
	
	@Override
	public boolean needsAnswering() {
		if (segLabelFileAssessmentHasBeenRun){
			return false;
		}
		return true;
	}

	@Override
	public View getView() {
		return this.view;
	}

}
