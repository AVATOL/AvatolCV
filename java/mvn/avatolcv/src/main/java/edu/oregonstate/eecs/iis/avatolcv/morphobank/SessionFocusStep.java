package edu.oregonstate.eecs.iis.avatolcv.morphobank;

import edu.oregonstate.eecs.iis.avatolcv.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.algorithm.ScoringAlgorithms;
import edu.oregonstate.eecs.iis.avatolcv.obsolete.SessionDataInterface;
import edu.oregonstate.eecs.iis.avatolcv.steps.Step;

public class SessionFocusStep implements Step {
    private ScoringAlgorithms scoringAlgorithms = null;
    private SessionDataInterface sessionData = null;
    public SessionFocusStep(SessionDataInterface sessionData) throws AvatolCVException {
        this.sessionData = sessionData;
    }
    public ScoringAlgorithms getScoringAlgorithms(){
        return this.sessionData.getScoringAlgorithms();
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
