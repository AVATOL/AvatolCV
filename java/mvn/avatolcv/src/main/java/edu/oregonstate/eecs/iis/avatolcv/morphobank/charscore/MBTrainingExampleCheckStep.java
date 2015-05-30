package edu.oregonstate.eecs.iis.avatolcv.morphobank.charscore;

import edu.oregonstate.eecs.iis.avatolcv.core.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.core.SessionData;
import edu.oregonstate.eecs.iis.avatolcv.core.Step;
import edu.oregonstate.eecs.iis.avatolcv.core.View;

public class MBTrainingExampleCheckStep implements Step {
    private String view = null;
    private SessionData sessionData = null;
    public MBTrainingExampleCheckStep(String view, SessionData sessionData){
        this.view = view;
        this.sessionData = sessionData;
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
