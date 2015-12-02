package edu.oregonstate.eecs.iis.avatolcv.steps;

import java.io.File;

import edu.oregonstate.eecs.iis.avatolcv.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.algorithm.AlgorithmLauncher;
import edu.oregonstate.eecs.iis.avatolcv.algorithm.AlgorithmSequence;
import edu.oregonstate.eecs.iis.avatolcv.algorithm.OutputMonitor;
import edu.oregonstate.eecs.iis.avatolcv.algorithm.RunConfigFile;
import edu.oregonstate.eecs.iis.avatolcv.algorithm.ScoringAlgorithm;
import edu.oregonstate.eecs.iis.avatolcv.core.SessionInfo;

public class ScoringRunStep implements Step {
    private SessionInfo sessionInfo = null;
    private AlgorithmLauncher launcher = null;
    public ScoringRunStep(SessionInfo sessionInfo){
        this.sessionInfo = sessionInfo;
    }
    public String getSelectedScoringAlgorithm() throws AvatolCVException {
        return this.sessionInfo.getScoringAlgName();
    }
    public void runScoring(OutputMonitor controller, String processName) throws AvatolCVException {
        ScoringAlgorithm sa  = sessionInfo.getSelectedScoringAlgorithm();
        AlgorithmSequence algSequence = sessionInfo.getAlgorithmSequence();
        algSequence.enableScoring();
        RunConfigFile rcf = new RunConfigFile(sa, algSequence);
        String runConfigPath = rcf.getRunConfigPath();
        File runConfigFile = new File(runConfigPath);
        if (!runConfigFile.exists()){
            throw new AvatolCVException("runConfigFile path does not exist."); 
        }
        this.launcher = new AlgorithmLauncher(sa, runConfigPath);
        this.launcher.launch(controller);
    }
    public void cancelScoring(){
        this.launcher.cancel();
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

    @Override
    public boolean isEnabledByPriorAnswers() {
        return true;
    }

}
