package edu.oregonstate.eecs.iis.avatolcv.segmentation;

import java.io.File;
import java.util.List;

import edu.oregonstate.eecs.iis.avatolcv.core.ImageInfo;
import edu.oregonstate.eecs.iis.avatolcv.core.ImagesForStage;
import edu.oregonstate.eecs.iis.avatolcv.core.Step;
import edu.oregonstate.eecs.iis.avatolcv.core.View;
import edu.oregonstate.eecs.iis.avatolcv.core.AvatolCVException;

public class SegStep1_TrainingExamplesCheck implements Step {

	private View view = null;	
	private boolean segLabelFileAssessmentHasBeenRun = false;
	private SegmentationSessionData ssd = null;
	ImagesForStage ifs = null;
	public SegStep1_TrainingExamplesCheck(View view, SegmentationSessionData ssd){
		this.ssd = ssd;
		this.view = view;
	}
	
	@Override
	public void consumeProvidedData() throws AvatolCVException {
		this.ssd.setImagesForStage(this.ifs);
	}
	
	public void assess()  throws AvatolCVException {
		segLabelFileAssessmentHasBeenRun = true;
		String segTrainingImageDirPath = this.ssd.getSegmentationTrainingImageDir();
		File segLabelDir = new File(segTrainingImageDirPath);
		if (!segLabelDir.isDirectory()){
			segLabelDir.mkdirs();
		}
		List<ImageInfo> candidateImages = ssd.getCandidateImages();
		ifs = new ImagesForStage(segTrainingImageDirPath, this.ssd.getSegmentationOutputDir(), candidateImages);
		ifs.reload();
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
