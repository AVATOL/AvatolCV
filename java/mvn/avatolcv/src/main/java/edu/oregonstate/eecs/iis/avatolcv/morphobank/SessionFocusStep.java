package edu.oregonstate.eecs.iis.avatolcv.morphobank;

import edu.oregonstate.eecs.iis.avatolcv.core.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.core.ScoringAlgorithms;
import edu.oregonstate.eecs.iis.avatolcv.core.SessionData;
import edu.oregonstate.eecs.iis.avatolcv.core.Step;

public class SessionFocusStep implements Step {
    private ScoringAlgorithms scoringAlgorithms = null;
    private SessionData sessionData = null;
    public SessionFocusStep(SessionData sessionData) throws AvatolCVException {
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

    @Override
    public String getView() {
        // TODO Auto-generated method stub
        return null;
    }

}
