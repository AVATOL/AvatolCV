package edu.oregonstate.eecs.iis.avatolcv.steps;

import java.io.File;

import edu.oregonstate.eecs.iis.avatolcv.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.algorithm.AlgorithmLauncher;
import edu.oregonstate.eecs.iis.avatolcv.algorithm.AlgorithmModules;
import edu.oregonstate.eecs.iis.avatolcv.algorithm.AlgorithmSequence;
import edu.oregonstate.eecs.iis.avatolcv.algorithm.CommandLineInvoker;
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
        AlgorithmSequence algSequence = sessionInfo.getAlgorithmSequence();
        algSequence.enableSegmentation();
        RunConfigFile rcf = new RunConfigFile(sa, algSequence);
        String runConfigPath = rcf.getRunConfigPath();
        File runConfigFile = new File(runConfigPath);
        if (!runConfigFile.exists()){
            throw new AvatolCVException("runConfigFile path does not exist."); 
        }
        AlgorithmLauncher launcher = new AlgorithmLauncher(sa, runConfigPath);
        String statusPath = rcf.getAlgorithmStatusPath();
        launcher.launch();
    }
}
