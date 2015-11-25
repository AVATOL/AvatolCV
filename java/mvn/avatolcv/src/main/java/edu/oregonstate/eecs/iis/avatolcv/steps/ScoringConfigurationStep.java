package edu.oregonstate.eecs.iis.avatolcv.steps;

import edu.oregonstate.eecs.iis.avatolcv.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.core.SessionInfo;

public class ScoringConfigurationStep extends Answerable implements Step {
	private SessionInfo sessionInfo = null;
	public ScoringConfigurationStep(SessionInfo sessionInfo){
		this.sessionInfo = sessionInfo;
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
	public boolean hasFollowUpDataLoadPhase() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isEnabledByPriorAnswers() {
		// TODO Auto-generated method stub
		return false;
	}

}
