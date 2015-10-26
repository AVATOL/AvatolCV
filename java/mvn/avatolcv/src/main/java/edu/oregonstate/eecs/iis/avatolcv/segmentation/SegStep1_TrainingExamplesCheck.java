package edu.oregonstate.eecs.iis.avatolcv.segmentation;

import java.io.File;
import java.util.List;

import edu.oregonstate.eecs.iis.avatolcv.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.core.ImageInfo;
import edu.oregonstate.eecs.iis.avatolcv.core.ImagesForAlgorithmStep;
import edu.oregonstate.eecs.iis.avatolcv.steps.Step;
import edu.oregonstate.eecs.iis.obsolete.avatolcv.View;

public class SegStep1_TrainingExamplesCheck implements Step {

	private boolean segLabelFileAssessmentHasBeenRun = false;
	private SegmentationSessionData ssd = null;
	ImagesForAlgorithmStep ifs = null;
	public SegStep1_TrainingExamplesCheck(SegmentationSessionData ssd) throws AvatolCVException {
		this.ssd = ssd;
        
	}

    @Override
    public void init() throws AvatolCVException {
        String segTrainingImageDirPath = this.ssd.getSegmentationTrainingImageDir();
        File segLabelDir = new File(segTrainingImageDirPath);
        if (!segLabelDir.isDirectory()){
            segLabelDir.mkdirs();
        }
        List<ImageInfo> candidateImages = ssd.getCandidateImages();
        ifs = new ImagesForAlgorithmStep(segTrainingImageDirPath, this.ssd.getSegmentationOutputDir(), candidateImages);
        ifs.reload();
    }
	@Override
	public void consumeProvidedData() throws AvatolCVException {
		this.ssd.setImagesForStage(this.ifs);
		segLabelFileAssessmentHasBeenRun = true;
	}
}
