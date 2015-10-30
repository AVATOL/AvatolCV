package edu.oregonstate.eecs.iis.avatolcv.steps;

import java.util.List;

import edu.oregonstate.eecs.iis.avatolcv.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.algorithm.AlgorithmModules;
import edu.oregonstate.eecs.iis.avatolcv.core.SessionInfo;

public class SegmentationConfigurationStep extends Answerable implements Step {
    private SessionInfo sessionInfo = null;
    public SegmentationConfigurationStep(SessionInfo sessionInfo){
        this.sessionInfo = sessionInfo;
    }
    //public AlgorithmModules getAlgorithmModules(){
    //	return 
    //}
    public List<String> getSegmentationAlgNames(){
        AlgorithmModules am = this.sessionInfo.getAlgoritmModules();
        return am.getSegmentationAlgNames();
    }
    public String getSegmentationAlgDescription(String segAlgName) throws AvatolCVException {
        AlgorithmModules am = this.sessionInfo.getAlgoritmModules();
        String answer = am.getAlgDescription(segAlgName, AlgorithmModules.AlgType.SEGMENTATION);
        return answer;
    }
    @Override
    public void init() throws AvatolCVException {
    }

    @Override
    public void consumeProvidedData() throws AvatolCVException {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean hasFollowUpDataLoadPhase() {
        return false;
    }

}
