package edu.oregonstate.eecs.iis.avatolcv.steps;

import java.util.List;

import edu.oregonstate.eecs.iis.avatolcv.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.algorithm.AlgorithmModules;
import edu.oregonstate.eecs.iis.avatolcv.core.SessionInfo;

public class OrientationConfigurationStep extends Answerable implements Step {
    private SessionInfo sessionInfo = null;
    private boolean algChosen = false;
    private String algName = null;
    public OrientationConfigurationStep(SessionInfo sessionInfo){
        this.sessionInfo = sessionInfo;
    }
    //public AlgorithmModules getAlgorithmModules(){
    //  return 
    //}
    public List<String> getOrientationAlgNames() throws AvatolCVException {
        AlgorithmModules am = this.sessionInfo.getAlgoritmModules();
        return am.getOrientationAlgNames();
    }
    public String getOrientationAlgDescription(String orientAlgName) throws AvatolCVException {
        AlgorithmModules am = this.sessionInfo.getAlgoritmModules();
        String answer = am.getAlgDescription(orientAlgName, AlgorithmModules.AlgType.ORIENTATION);
        return answer;
    }
    public void setIsAlgorithmChosen(boolean isChosen){
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
        if (this.algChosen){
            this.sessionInfo.setChosenOrientationAlgorithmName(this.algName);
        }
        else {
            this.sessionInfo.setChosenOrientationAlgorithmName(null);
        }
    }

    @Override
    public boolean hasFollowUpDataLoadPhase() {
        return false;
    }
    @Override
    public boolean isEnabledByPriorAnswers() {
        return true;
    }

}