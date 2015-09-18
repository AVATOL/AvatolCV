package edu.oregonstate.eecs.iis.avatolcv.steps;


import java.util.List;

import edu.oregonstate.eecs.iis.avatolcv.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.algorithm.ScoringAlgorithms;
import edu.oregonstate.eecs.iis.avatolcv.algorithm.ScoringAlgorithms.ScoringScope;
import edu.oregonstate.eecs.iis.avatolcv.core.ProgressPresenter;
import edu.oregonstate.eecs.iis.avatolcv.core.SessionInfo;
import edu.oregonstate.eecs.iis.avatolcv.datasource.ChoiceItem;

public class ScoringConcernStep  extends Answerable implements Step {
    private SessionInfo sessionInfo = null;
    private ChoiceItem chosenItem = null;
    private List<ChoiceItem> chosenItems = null;
            
            
    public ScoringConcernStep(SessionInfo sessionInfo){
        this.sessionInfo = sessionInfo;
    }
    public List<ChoiceItem> getScoringConcernItems() throws AvatolCVException {
        return this.sessionInfo.getDataSource().getScoringConcernOptions(this.sessionInfo.getScoringScope(), this.sessionInfo.getScoringFocus());
    }
 
    public ScoringAlgorithms getScoringAlgorithms(){
        return this.sessionInfo.getScoringAlgorithms();
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
        ScoringAlgorithms.ScoringScope scope = this.sessionInfo.getScoringScope();
        if (scope == ScoringAlgorithms.ScoringScope.MULTIPLE_ITEM){
            sessionInfo.setScoringConcerns(chosenItems);
        }
        else {
            sessionInfo.setScoringConcern(chosenItem);
        }
        
    }
    @Override
    public boolean hasFollowUpDataLoadPhase() {
        return true;
    }
    
    public void loadRemainingMetadataForChosenDataset(ProgressPresenter pp, String processName) throws AvatolCVException {
        this.sessionInfo.getDataSource().loadRemainingMetadataForChosenDataset(pp, processName);
    }
}