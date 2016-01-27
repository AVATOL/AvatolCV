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
	public NormalizedKey getDefaultTrainTestConcern() throws AvatolCVException {
		return this.sessionInfo.getDefaultTrainTestConcern();
	}
	public void setTrainTestConcern(NormalizedKey trainTestConcern){
		this.trainTestConcern = trainTestConcern;
	}
	public boolean isEvaluationRun() throws AvatolCVException {
	    return this.sessionInfo.isEvaluationRun();
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
	@Override
	public boolean shouldRenderIfBackingIntoIt() {
		return true;
	}
    
}
