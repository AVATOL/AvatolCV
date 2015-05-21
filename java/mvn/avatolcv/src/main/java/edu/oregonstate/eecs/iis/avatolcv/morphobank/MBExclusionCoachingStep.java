package edu.oregonstate.eecs.iis.avatolcv.morphobank;

import edu.oregonstate.eecs.iis.avatolcv.core.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.core.Step;
import edu.oregonstate.eecs.iis.avatolcv.core.View;
import edu.oregonstate.eecs.iis.avatolcv.ws.BisqueWSClient;
import edu.oregonstate.eecs.iis.avatolcv.ws.MorphobankWSClient;

public class MBExclusionCoachingStep implements Step {
    private MorphobankWSClient wsClient = null;
    private String view = null;
    private boolean userHasViewed = false;
    public MBExclusionCoachingStep(String view, MorphobankWSClient wsClient){
        this.wsClient = wsClient;
        this.view = view;
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
