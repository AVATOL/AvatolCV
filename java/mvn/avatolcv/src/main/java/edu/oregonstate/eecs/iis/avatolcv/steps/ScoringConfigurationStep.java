package edu.oregonstate.eecs.iis.avatolcv.steps;

import java.util.List;

import edu.oregonstate.eecs.iis.avatolcv.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.core.EvaluationSet;
import edu.oregonstate.eecs.iis.avatolcv.core.ScoringSet;
import edu.oregonstate.eecs.iis.avatolcv.core.SessionInfo;
import edu.oregonstate.eecs.iis.avatolcv.core.TrueScoringSet;

public class ScoringConfigurationStep extends Answerable implements Step {
	private SessionInfo sessionInfo = null;
	public ScoringConfigurationStep(SessionInfo sessionInfo){
		this.sessionInfo = sessionInfo;
	}
	public List<String> getScoreConfigurationSortingValueOptions(ScoringSet ss){
		return this.sessionInfo.getScoreConfigurationSortingValueOptions(ss);
	}
	public EvaluationSet getEvaluationSet() throws AvatolCVException {
		return this.sessionInfo.getEvaluationSet();
	}
	public TrueScoringSet getTrueScoringSet() throws AvatolCVException {
		return this.sessionInfo.getTrueScoringSet();
	}
	@Override
	public void init() throws AvatolCVException {
		// TODO Auto-generated method stub
		
	}
	public void reAssessImagesInPlay() throws AvatolCVException {
		this.sessionInfo.reAssessImagesInPlay();
	}
	@Override
	public void consumeProvidedData() throws AvatolCVException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean hasFollowUpDataLoadPhase() {
		return false;
	}

	@Override
	public boolean isEnabledByPriorAnswers() {
		return true;
	}

}
