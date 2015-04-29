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
        File orientLabelDir = new File(orientTrainingImageDirPath);
        if (!orientLabelDir.isDirectory()){
            orientLabelDir.mkdirs();
        }
        List<ImageInfo> candidateImages = osd.getCandidateImages();
        ifs = new ImagesForStage(orientTrainingImageDirPath, this.osd.getOrientationOutputDir(), candidateImages);
        ifs.reload();
        orientLabelFileAssessmentHasBeenRun = true;
        
    }
    @Override
    public void consumeProvidedData() throws AvatolCVException {
        this.osd.setImagesForStage(this.ifs);
    }
    
    
    @Override
    public boolean needsAnswering() {
        if (orientLabelFileAssessmentHasBeenRun){
            return false;
        }
        return true;
    }

    @Override
    public View getView() {
        return null;
    }

}
