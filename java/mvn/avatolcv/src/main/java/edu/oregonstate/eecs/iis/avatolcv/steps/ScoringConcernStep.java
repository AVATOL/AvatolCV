package edu.oregonstate.eecs.iis.avatolcv.steps;


import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.oregonstate.eecs.iis.avatolcv.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.algorithm.ScoringAlgorithm;
import edu.oregonstate.eecs.iis.avatolcv.algorithm.ScoringAlgorithm.ScoringScope;
import edu.oregonstate.eecs.iis.avatolcv.datasource.ChoiceItem;
import edu.oregonstate.eecs.iis.avatolcv.session.DataIssue;
import edu.oregonstate.eecs.iis.avatolcv.session.ProgressPresenter;
import edu.oregonstate.eecs.iis.avatolcv.session.SessionInfo;

public class ScoringConcernStep  extends Answerable implements Step {
    private SessionInfo sessionInfo = null;
    private ChoiceItem chosenItem = null;
    private List<ChoiceItem> chosenItems = null;
    private static final Logger logger = LogManager.getLogger(ScoringConcernStep.class);
           
            
    public ScoringConcernStep(SessionInfo sessionInfo){
        this.sessionInfo = sessionInfo;
    }
    public List<ChoiceItem> getScoringConcernItems() throws AvatolCVException {
        return this.sessionInfo.getDataSource().getScoringConcernOptions(this.sessionInfo.getScoringScope(), this.sessionInfo.getScoringFocus());
    }
 
    public ScoringScope getScoringScope(){
        return this.sessionInfo.getScoringScope();
    }
   
    public void setChosenItems(List<ChoiceItem> choiceItems){
        this.chosenItems = choiceItems;
    }
    public void setChosenChoiceItem(ChoiceItem ci){
        this.chosenItem = ci;
    }
    
    public String getInstructionsForScoringConcernScreen(){
        return this.sessionInfo.getDataSource().getInstructionsForScoringConcernScreen(this.sessionInfo.getScoringScope(), this.sessionInfo.getScoringFocus());
    }
    @Override
    public void init() throws AvatolCVException {
        // TODO Auto-generated method stub

    }

    @Override
    public void consumeProvidedData() throws AvatolCVException {
        ScoringAlgorithm.ScoringScope scope = this.sessionInfo.getScoringScope();
        logger.info("scoring scope set to " + scope);
        if (scope == ScoringAlgorithm.ScoringScope.MULTIPLE_ITEM){
            sessionInfo.setScoringConcerns(chosenItems);
            for (ChoiceItem item : chosenItems){
            	logger.info("selected scoring concern : " + item.getNormalizedKey());
            }
        }
        else {
            sessionInfo.setScoringConcern(chosenItem);
            logger.info("selected scoring concern : " + chosenItem.getNormalizedKey());
        }
        
    }
    @Override
    public boolean hasFollowUpDataLoadPhase() {
        return true;
    }
    
    public void loadRemainingMetadataForChosenDataset(ProgressPresenter pp, String processName) throws AvatolCVException {
        this.sessionInfo.getDataSource().loadRemainingMetadataForChosenDataset(pp, processName);
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
	public SessionInfo getSessionInfo() {
		return this.sessionInfo;
	}
}