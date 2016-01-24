package edu.oregonstate.eecs.iis.avatolcv.steps;

import edu.oregonstate.eecs.iis.avatolcv.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.datasource.BisqueDataSource;
import edu.oregonstate.eecs.iis.avatolcv.datasource.FileSystemDataSource;
import edu.oregonstate.eecs.iis.avatolcv.datasource.MorphobankDataSource;
import edu.oregonstate.eecs.iis.avatolcv.session.SessionInfo;

public class ScoringModeStep  extends Answerable implements Step {
   
    private SessionInfo sessionInfo = null;
    private boolean isEvaluation = true;
    public ScoringModeStep(SessionInfo sessionInfo){
        this.sessionInfo = sessionInfo;
    }
    @Override
    public void init() throws AvatolCVException {
        // TODO Auto-generated method stub

    }
    public String getChosenScoringAlgName() throws AvatolCVException {
    	return this.sessionInfo.getScoringAlgName();
    }
    public boolean isAllImagesLabeled() throws AvatolCVException {
        return this.sessionInfo.isAllImagesLabeled();
    }
    public void setModeToEvaluation(){
        this.isEvaluation = true;
    }
    public void setModeToScoringImages(){
        this.isEvaluation = false;
    }
    @Override
    public void consumeProvidedData() throws AvatolCVException {
       this.sessionInfo.setScoringModeToEvaluation(this.isEvaluation);
    }
    @Override
    public boolean hasFollowUpDataLoadPhase() {
        // TODO Auto-generated method stub
        return false;
    }
    @Override
    public boolean isEnabledByPriorAnswers() {
    	try {
    		if (isAllImagesLabeled()){
            	return false;
            }
            return true;
    	}
    	catch(AvatolCVException ace){
    		return true;
    	}
    }
    @Override
	public boolean shouldRenderIfBackingIntoIt() {
		return isEnabledByPriorAnswers();
	}
    
}
