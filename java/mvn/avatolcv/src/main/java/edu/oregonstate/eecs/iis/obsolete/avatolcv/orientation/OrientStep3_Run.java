package edu.oregonstate.eecs.iis.obsolete.avatolcv.orientation;

import edu.oregonstate.eecs.iis.avatolcv.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.algorithm.AlgorithmRunner;
import edu.oregonstate.eecs.iis.avatolcv.session.ProgressPresenter;
import edu.oregonstate.eecs.iis.avatolcv.steps.Step;
import edu.oregonstate.eecs.iis.obsolete.avatolcv.View;
import edu.oregonstate.eecs.iis.obsolete.avatolcv.segmentation.SegmentationSessionData;

public class OrientStep3_Run implements Step {
    private String view = null;   
    private OrientationSessionData osd = null;
    private AlgorithmRunner runner = null;
    public OrientStep3_Run(String view, OrientationSessionData osd, AlgorithmRunner runner){
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
}
