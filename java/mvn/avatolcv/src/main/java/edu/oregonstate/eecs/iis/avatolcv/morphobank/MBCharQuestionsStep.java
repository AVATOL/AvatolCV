package edu.oregonstate.eecs.iis.avatolcv.morphobank;

import java.util.List;

import edu.oregonstate.eecs.iis.avatolcv.core.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.core.ScoringAlgorithms;
import edu.oregonstate.eecs.iis.avatolcv.core.SessionData;
import edu.oregonstate.eecs.iis.avatolcv.core.Step;

public class MBCharQuestionsStep implements Step {
   
    private SessionData sessionData = null;
    private String view = null;
    private ScoringAlgorithms scoringAlgorithms = null;
    public MBCharQuestionsStep(String view, ScoringAlgorithms scoringAlgorithms, SessionData sessionData) {
        this.sessionData = sessionData;
        this.scoringAlgorithms = scoringAlgorithms;
        this.view = view;
    }
    public ScoringAlgorithms getScoringAlgorithms(){
        return this.scoringAlgorithms;
    }
    public void setChosenAlgorithm(String s){
        sessionData.setChosenAlgorithm(s);
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
    public String getView() {
        // TODO Auto-generated method stub
        return null;
    }

}
