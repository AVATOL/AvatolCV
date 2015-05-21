package edu.oregonstate.eecs.iis.avatolcv.orientation;

import java.io.File;
import java.util.List;

import edu.oregonstate.eecs.iis.avatolcv.core.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.core.ImageInfo;
import edu.oregonstate.eecs.iis.avatolcv.core.ImagesForStage;
import edu.oregonstate.eecs.iis.avatolcv.core.Step;
import edu.oregonstate.eecs.iis.avatolcv.core.View;
import edu.oregonstate.eecs.iis.avatolcv.segmentation.SegmentationSessionData;

public class OrientStep1_TrainingExamplesCheck implements Step {

    private boolean orientLabelFileAssessmentHasBeenRun = false;
    private OrientationSessionData osd = null;
    ImagesForStage ifs = null;
    public OrientStep1_TrainingExamplesCheck(OrientationSessionData osd) throws AvatolCVException {
        this.osd = osd;
        
    }

    @Override
    public void init() throws AvatolCVException {
        String orientTrainingImageDirPath = this.osd.getTrainingImageDir();
        File orientTrainingImageDir = new File(orientTrainingImageDirPath);
        if (!orientTrainingImageDir.isDirectory()){
        	orientTrainingImageDir.mkdirs();
        }
        List<ImageInfo> candidateImages = osd.getCandidateImages();
        ifs = new ImagesForStage(orientTrainingImageDirPath, this.osd.getOrientationOutputDir(), candidateImages);
        ifs.reload();
    }
    @Override
    public void consumeProvidedData() throws AvatolCVException {
        this.osd.setImagesForStage(this.ifs);
        orientLabelFileAssessmentHasBeenRun = true;
    }
    
    
    @Override
    public String getView() {
        return null;
    }

}
