package edu.oregonstate.eecs.iis.obsolete.avatolcv.morphobank;

import java.util.List;

import edu.oregonstate.eecs.iis.avatolcv.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.algorithm.ScoringAlgorithms;
import edu.oregonstate.eecs.iis.avatolcv.steps.Step;
import edu.oregonstate.eecs.iis.obsolete.avatolcv.SessionDataInterface;

public class MBCharQuestionsStep implements Step {
   
    private SessionDataInterface sessionData = null;
    private String view = null;
    private ScoringAlgorithms scoringAlgorithms = null;
    public MBCharQuestionsStep(String view, ScoringAlgorithms scoringAlgorithms, SessionDataInterface sessionData) {
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

}
