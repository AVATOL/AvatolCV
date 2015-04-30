package edu.oregonstate.eecs.iis.avatolcv.orientation;

import edu.oregonstate.eecs.iis.avatolcv.core.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.core.ImagesForStage;
import edu.oregonstate.eecs.iis.avatolcv.core.Step;
import edu.oregonstate.eecs.iis.avatolcv.core.View;
import edu.oregonstate.eecs.iis.avatolcv.segmentation.SegmentationSessionData;

public class OrientStep2_LabelTrainingExamples implements Step {
    
    private View view = null;
    private OrientationSessionData osd = null;
    ImagesForStage ifs = null;
    boolean needsAnswering = true;
    public OrientStep2_LabelTrainingExamples(View view, OrientationSessionData osd){
        this.view = view;
        this.osd = osd;
        
    }
    @Override
    public void init() throws AvatolCVException {
        this.ifs = osd.getImagesForStage();
    }
    @Override
    public void consumeProvidedData() throws AvatolCVException {
        this.ifs.reload();
        this.osd.createTrainingImageListFile();
        this.osd.createTestImageListFile();
        this.osd.createOrientationConfigFile();
        this.needsAnswering = false;
    }

    @Override
    public boolean needsAnswering() {
        return false;
    }

    @Override
    public View getView() {
        return null;
    }
   

}
