package edu.oregonstate.eecs.iis.avatolcv.steps;

import java.util.List;

import edu.oregonstate.eecs.iis.avatolcv.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.algorithm.AlgorithmModules;
import edu.oregonstate.eecs.iis.avatolcv.core.SessionInfo;

public class SegmentationConfigurationStep extends Answerable implements Step {
    private SessionInfo sessionInfo = null;
    private boolean algChosen = false;
    private String algName = null;
    public SegmentationConfigurationStep(SessionInfo sessionInfo){
        this.sessionInfo = sessionInfo;
    }
    //public AlgorithmModules getAlgorithmModules(){
    //	return 
    //}
    public List<String> getSegmentationAlgNames() throws AvatolCVException {
        AlgorithmModules am = this.sessionInfo.getAlgoritmModules();
        return am.getSegmentationAlgNames();
    }
    public String getSegmentationAlgDescription(String segAlgName) throws AvatolCVException {
        AlgorithmModules am = this.sessionInfo.getAlgoritmModules();
        String answer = am.getAlgDescription(segAlgName, AlgorithmModules.AlgType.SEGMENTATION);
        return answer;
    }
    public void setIsAglorithmChosen(boolean isChosen){
        this.algChosen = isChosen;
    }
    public void setChosenAlgorithm(String algName){
        this.algName = algName;
    }
    @Override
    public void init() throws AvatolCVException {
    }

    @Override
    public void consumeProvidedData() throws AvatolCVException {
        this.sessionInfo.setChosenSegmentationAlgorithm(this.algName);
    }

    @Override
    public boolean hasFollowUpDataLoadPhase() {
        return false;
    }

}
