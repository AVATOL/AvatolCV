package edu.oregonstate.eecs.iis.avatolcv.steps;

import java.util.List;

import edu.oregonstate.eecs.iis.avatolcv.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.normalized.NormalizedKey;
import edu.oregonstate.eecs.iis.avatolcv.normalized.NormalizedValue;
import edu.oregonstate.eecs.iis.avatolcv.scoring.EvaluationSet;
import edu.oregonstate.eecs.iis.avatolcv.scoring.ScoringSet;
import edu.oregonstate.eecs.iis.avatolcv.scoring.TrueScoringSet;
import edu.oregonstate.eecs.iis.avatolcv.session.SessionInfo;

public class ScoringConfigurationStep extends Answerable implements Step {
	private SessionInfo sessionInfo = null;
	private List<ScoringSet> scoringSets = null;
	private NormalizedKey trainTestConcern = null;
	public ScoringConfigurationStep(SessionInfo sessionInfo){
		this.sessionInfo = sessionInfo;
	}
	public SessionInfo getSessionInfo(){
		return this.sessionInfo;
	}
	public void setScoringSets(List<ScoringSet> scoringSets){
	    this.scoringSets = scoringSets;
	}
	public void setTrainTestConcern(NormalizedKey trainTestConcern){
		this.trainTestConcern = trainTestConcern;
	}
	
	@Override
	public void init() throws AvatolCVException {
		// TODO Auto-generated method stub
		
	}
	
	
	@Override
	public void consumeProvidedData() throws AvatolCVException {
		for (ScoringSet ss : this.scoringSets){
			String scoringConcern = ss.getScoringConcernName();
			this.sessionInfo.setScoringSetForConcernName(scoringConcern, ss);
		}
		this.sessionInfo.setTrainTestConcern(this.trainTestConcern);
	}

	@Override
	public boolean hasFollowUpDataLoadPhase() {
		return false;
	}

	@Override
	public boolean isEnabledByPriorAnswers() {
		return true;
	}
	@Override
	public boolean shouldRenderIfBackingIntoIt() {
		return true;
	}
    
}
