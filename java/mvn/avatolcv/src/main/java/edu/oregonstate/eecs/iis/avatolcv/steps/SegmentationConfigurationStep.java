package edu.oregonstate.eecs.iis.avatolcv.steps;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.oregonstate.eecs.iis.avatolcv.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.algorithm.AlgorithmModules;
import edu.oregonstate.eecs.iis.avatolcv.session.DataIssue;
import edu.oregonstate.eecs.iis.avatolcv.session.SessionInfo;

public class SegmentationConfigurationStep extends Answerable implements Step {
    private SessionInfo sessionInfo = null;
    private boolean algChosen = false;
    private String algName = null;
    private static final Logger logger = LogManager.getLogger(SegmentationConfigurationStep.class);

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
            this.sessionInfo.setChosenSegmentationAlgorithmName(this.algName);
            logger.info("segmentation algorithm chosen : " + this.algName);
        }
        else {
            this.sessionInfo.setChosenSegmentationAlgorithmName(null);
            logger.info("segmentation algorithm skipped");
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
    @Override
	public boolean shouldRenderIfBackingIntoIt() {
		return true;
	}
    @Override
	public List<DataIssue> getDataIssues() {
		return null;
	}
}
