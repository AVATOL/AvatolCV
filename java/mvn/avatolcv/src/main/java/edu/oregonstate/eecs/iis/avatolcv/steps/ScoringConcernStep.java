package edu.oregonstate.eecs.iis.avatolcv.steps;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edu.oregonstate.eecs.iis.avatolcv.bisque.BisqueSessionData;
import edu.oregonstate.eecs.iis.avatolcv.core.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.core.ChoiceItem;
import edu.oregonstate.eecs.iis.avatolcv.core.DataSource;
import edu.oregonstate.eecs.iis.avatolcv.core.ScoringAlgorithms;
import edu.oregonstate.eecs.iis.avatolcv.core.SessionInfo;
import edu.oregonstate.eecs.iis.avatolcv.core.Step;
import edu.oregonstate.eecs.iis.avatolcv.core.View;
import edu.oregonstate.eecs.iis.avatolcv.ws.BisqueWSClient;
import edu.oregonstate.eecs.iis.avatolcv.ws.BisqueWSException;
import edu.oregonstate.eecs.iis.avatolcv.ws.MorphobankWSClient;
import edu.oregonstate.eecs.iis.avatolcv.ws.MorphobankWSException;
import edu.oregonstate.eecs.iis.avatolcv.ws.bisque.BisqueAnnotation;
import edu.oregonstate.eecs.iis.avatolcv.ws.bisque.BisqueDataset;
import edu.oregonstate.eecs.iis.avatolcv.ws.bisque.BisqueImage;
import edu.oregonstate.eecs.iis.avatolcv.ws.morphobank.CharacterInfo.MBCharacter;
import edu.oregonstate.eecs.iis.avatolcv.ws.morphobank.MatrixInfo.MBMatrix;

public class ScoringConcernStep implements Step {
    private SessionInfo sessionInfo = null;
    private ChoiceItem chosenItem = null;
    private List<ChoiceItem> chosenItems = null;
            
            
    public ScoringConcernStep(SessionInfo sessionInfo){
        this.sessionInfo = sessionInfo;
    }
    public List<ChoiceItem> getScoringConcernItems() throws AvatolCVException {
        return this.sessionInfo.getDataSource().getScoringConcernItems(this.sessionInfo.getScoringAlgorithms());
    }
 
    public ScoringAlgorithms getScoringAlgorithms(){
        return this.sessionInfo.getScoringAlgorithms();
    }
   
   
    public void setChosenItems(List<ChoiceItem> choiceItems){
        this.chosenItems = choiceItems;
    }
    public void setChosenChoiceItem(ChoiceItem ci){
        this.chosenItem = ci;
    }
    
    public String getInstructionsForScoringConcernScreen(){
        return this.sessionInfo.getDataSource().getInstructionsForScoringConcernScreen(this.sessionInfo.getScoringAlgorithms());
    }
    @Override
    public void init() throws AvatolCVException {
        // TODO Auto-generated method stub

    }

    @Override
    public void consumeProvidedData() throws AvatolCVException {
        ScoringAlgorithms.ScoringScope scope = getScoringAlgorithms().getScoringScope();
        if (scope == ScoringAlgorithms.ScoringScope.MULTIPLE_ITEM){
            sessionInfo.setScoringConcerns(chosenItems);
        }
        else {
            sessionInfo.setScoringConcern(chosenItem);
        }
        
    }
}