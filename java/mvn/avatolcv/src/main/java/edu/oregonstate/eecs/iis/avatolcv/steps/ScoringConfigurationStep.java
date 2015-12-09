package edu.oregonstate.eecs.iis.avatolcv.steps;

import java.util.List;

import edu.oregonstate.eecs.iis.avatolcv.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.core.EvaluationSet;
import edu.oregonstate.eecs.iis.avatolcv.core.NormalizedKey;
import edu.oregonstate.eecs.iis.avatolcv.core.NormalizedValue;
import edu.oregonstate.eecs.iis.avatolcv.core.ScoringSet;
import edu.oregonstate.eecs.iis.avatolcv.core.SessionInfo;
import edu.oregonstate.eecs.iis.avatolcv.core.TrueScoringSet;

public class ScoringConfigurationStep extends Answerable implements Step {
	private SessionInfo sessionInfo = null;
	private List<ScoringSet> scoringSets = null;
	private NormalizedKey trainTestConcern = null;
	public ScoringConfigurationStep(SessionInfo sessionInfo){
		this.sessionInfo = sessionInfo;
	}
	public void setScoringSets(List<ScoringSet> scoringSets){
	    this.scoringSets = scoringSets;
	}
	public NormalizedKey getDefaultTrainTestConcern() throws AvatolCVException {
		return this.sessionInfo.getDefaultTrainTestConcern();
	}
	public void setTrainTestConcern(NormalizedKey trainTestConcern){
		this.trainTestConcern = trainTestConcern;
	}
	public List<NormalizedKey> getScoreConfigurationSortingValueOptions(ScoringSet ss){
		return this.sessionInfo.getScoreConfigurationSortingValueOptions(ss);
	}
	public List<EvaluationSet> getEvaluationSets() throws AvatolCVException {
		return this.sessionInfo.getEvaluationSets();
	}
	public List<TrueScoringSet> getTrueScoringSets() throws AvatolCVException {
		return this.sessionInfo.getTrueScoringSets();
	}
	public List<NormalizedKey> getScoringSortingCandidates() throws AvatolCVException {
		return this.sessionInfo.getScoringSortingCandidates();
	}
	@Override
	public void init() throws AvatolCVException {
		// TODO Auto-generated method stub
		
	}
	public List<NormalizedValue> getValuesForTrainTestConcern(NormalizedKey trainTestConcern) throws AvatolCVException {
		return this.sessionInfo.getValuesForTrainTestConcern(trainTestConcern);
	}
	public void reAssessImagesInPlay() throws AvatolCVException {
		this.sessionInfo.reAssessImagesInPlay();
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

}
