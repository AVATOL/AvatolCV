package edu.oregonstate.eecs.iis.avatolcv.orientation;

import edu.oregonstate.eecs.iis.avatolcv.core.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.core.ImagesForStage;
import edu.oregonstate.eecs.iis.avatolcv.core.ProgressPresenter;
import edu.oregonstate.eecs.iis.avatolcv.core.Step;
import edu.oregonstate.eecs.iis.avatolcv.core.View;
import edu.oregonstate.eecs.iis.avatolcv.generic.AlgorithmRunner;
import edu.oregonstate.eecs.iis.avatolcv.segmentation.SegmentationSessionData;

public class OrientStep3_Run implements Step {
    private View view = null;   
    private OrientationSessionData osd = null;
    private AlgorithmRunner segRunner = null;
    public OrientStep3_Run(View view, OrientationSessionData osd, OrientationRunner orientRunner){
        this.osd = osd;
        this.view = view;
        this.segRunner = segRunner;
    }
    public void run(ProgressPresenter pp){
        this.ssd.cleanResults();
        this.segRunner.run(ssd.getConfigFilePath(), pp);
    }
    @Override
    public void consumeProvidedData() throws AvatolCVException {
        // pull the result files into the program's consciousness
        this.ssd.getImagesForStage().reload();  
    }

    @Override
    public boolean needsAnswering() {
        return !segRunner.isRunComplete();
    }

    @Override
    public View getView() {
        return this.view;
    }
}
