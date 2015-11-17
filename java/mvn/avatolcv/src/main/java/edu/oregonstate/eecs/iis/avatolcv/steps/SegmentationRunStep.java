package edu.oregonstate.eecs.iis.avatolcv.steps;

import edu.oregonstate.eecs.iis.avatolcv.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.algorithm.AlgorithmLauncher;
import edu.oregonstate.eecs.iis.avatolcv.algorithm.AlgorithmModules;
import edu.oregonstate.eecs.iis.avatolcv.algorithm.AlgorithmSequence;
import edu.oregonstate.eecs.iis.avatolcv.algorithm.RunConfigFile;
import edu.oregonstate.eecs.iis.avatolcv.algorithm.SegmentationAlgorithm;
import edu.oregonstate.eecs.iis.avatolcv.core.SessionInfo;
import edu.oregonstate.eecs.iis.avatolcv.ui.javafx.SegmentationRunStepController;

public class SegmentationRunStep implements Step {
    private SessionInfo sessionInfo = null;
    public SegmentationRunStep(SessionInfo sessionInfo){
        this.sessionInfo = sessionInfo;
    }
    public String getSelectedSegmentationAlgorithm(){
        return this.sessionInfo.getSegmentationAlgName();
    }
    @Override
    public void init() throws AvatolCVException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void consumeProvidedData() throws AvatolCVException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public boolean hasFollowUpDataLoadPhase() {
        // TODO Auto-generated method stub
        return false;
    }

    public void runSegmentation(SegmentationRunStepController controller, String processName) throws AvatolCVException {
        SegmentationAlgorithm sa  = sessionInfo.getSelectedSegmentationAlgorithm();
        AlgorithmSequence algorithmSequence = sessionInfo.getAlgorithmSequence();
        RunConfigFile rcf = new RunConfigFile(sa, algorithmSequence);
        String runConfigPath = rcf.getRunConfigPath();
        
        AlgorithmLauncher launcher = new AlgorithmLauncher(sa, runConfigPath);
        
    }
}
