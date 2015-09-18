package edu.oregonstate.eecs.iis.avatolcv.obsolete.bisque;

import edu.oregonstate.eecs.iis.avatolcv.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.obsolete.View;
import edu.oregonstate.eecs.iis.avatolcv.steps.Step;
import edu.oregonstate.eecs.iis.avatolcv.ws.BisqueWSClient;

public class BisqueExclusionCoachingStep implements Step {
	private BisqueWSClient wsClient = null;
	private String view = null;
	private boolean userHasViewed = false;
	public BisqueExclusionCoachingStep(String view, BisqueWSClient wsClient){
		this.wsClient = wsClient;
		this.view = view;
	}
	@Override
    public void init() {
        // nothing to do
    }
	@Override
	public void consumeProvidedData() throws AvatolCVException {
		// nothing to consume, just showing guidance
	}
	public void userHasViewed(){
		this.userHasViewed = true;
	}
}
