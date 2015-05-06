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
    private AlgorithmRunner runner = null;
    public OrientStep3_Run(View view, OrientationSessionData osd, AlgorithmRunner runner){
        this.osd = osd;
        this.view = view;
        this.runner = runner;
    }
    public void run(ProgressPresenter pp){
        this.osd.cleanResults();
        this.runner.run(osd.getConfigFilePath(), pp);
    }
    @Override
    public void init() throws AvatolCVException {
        // nothing to do
    }
    @Override
    public void consumeProvidedData() throws AvatolCVException {
        // pull the result files into the program's consciousness
        this.osd.getImagesForStage().reload();  
    }


    @Override
    public View getView() {
        return this.view;
    }
}
