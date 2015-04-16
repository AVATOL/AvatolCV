package edu.oregonstate.eecs.iis.avatolcv.bisque;

import edu.oregonstate.eecs.iis.avatolcv.core.Step;
import edu.oregonstate.eecs.iis.avatolcv.core.View;
import edu.oregonstate.eecs.iis.avatolcv.ws.BisqueSessionException;
import edu.oregonstate.eecs.iis.avatolcv.ws.BisqueWSClient;

public class BisqueExclusionCoachingStep implements Step {
	private BisqueWSClient wsClient = null;
	private View view = null;
	private boolean userHasViewed = false;
	public BisqueExclusionCoachingStep(View view, BisqueWSClient wsClient){
		this.wsClient = wsClient;
		this.view = view;
	}
	@Override
	public void consumeProvidedData() throws BisqueSessionException {
		// nothing to consume, just showing guidance
	}

	@Override
	public boolean needsAnswering() {
		if (userHasViewed){
			return false;
		}
		return true;
	}
	@Override
	public View getView() {
		return this.view;
	}
	public void userHasViewed(){
		this.userHasViewed = true;
	}
}
