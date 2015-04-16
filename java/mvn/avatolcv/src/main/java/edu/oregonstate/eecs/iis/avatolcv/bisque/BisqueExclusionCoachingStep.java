package edu.oregonstate.eecs.iis.avatolcv.bisque;

import edu.oregonstate.eecs.iis.avatolcv.core.Step;
import edu.oregonstate.eecs.iis.avatolcv.core.View;
import edu.oregonstate.eecs.iis.avatolcv.ws.BisqueSessionException;
import edu.oregonstate.eecs.iis.avatolcv.ws.BisqueWSClient;

public class BisqueExclusionCoachingStep implements Step {
	private BisqueWSClient wsClient = null;
	private View view = null;
	public BisqueExclusionCoachingStep(View view, BisqueWSClient wsClient){
		this.wsClient = wsClient;
		this.view = view;
	}
	@Override
	public void consumeProvidedData() throws BisqueSessionException {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean needsAnswering() {
		// TODO Auto-generated method stub
		return false;
	}

}
