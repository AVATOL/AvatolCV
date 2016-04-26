package edu.oregonstate.eecs.iis.avatolcv.results;

import java.util.ArrayList;
import java.util.List;

import edu.oregonstate.eecs.iis.avatolcv.normalized.NormalizedKey;
import edu.oregonstate.eecs.iis.avatolcv.normalized.NormalizedValue;

public class ScoreItem{
	public enum ScoringFate {
		ABSTAIN_FROM_CHANGING_SCORE_VOTE_TIE,
		ABSTAIN_FROM_CHANGING_SCORE_NEW_VALUE_SAME,
		SET_NEW_VALUE,
		REVISE_VALUE
	}
    private String imageID = null;
    private NormalizedKey trainTestConcern = null;
    private NormalizedValue trainTestConcernValue = null;
    private NormalizedValue newValue = null;
    private NormalizedValue existingValueForKey = null;
    private List<String> imageIDsRepresentedByWinner = null;
    private ScoringFate fate = null;
    public ScoreItem(String imageID, NormalizedKey trainTestConcern, NormalizedValue trainTestConcernValue, NormalizedValue newValue, NormalizedValue existingValueForKey){
        this.imageID = imageID;
        this.trainTestConcern = trainTestConcern;
        this.trainTestConcernValue = trainTestConcernValue;
        this.newValue = newValue;
        this.existingValueForKey = existingValueForKey;
    }
    public void deduceScoringFate(){
    	if (newValue.equals(existingValueForKey)){
    		this.fate = ScoringFate.ABSTAIN_FROM_CHANGING_SCORE_NEW_VALUE_SAME;
    	}
    	else if (null == existingValueForKey){
    		this.fate = ScoringFate.SET_NEW_VALUE;
    	}
    	else if (existingValueForKey.getName().equals("")){
    		this.fate = ScoringFate.SET_NEW_VALUE;
    	}
    	this.fate = ScoringFate.REVISE_VALUE;
    }
    public void setImageIDsRepresentedByWinner(List<String> ids){
    	this.imageIDsRepresentedByWinner = ids;
    }
    public List<String> getImageIDsRepresentedByWinner(){
    	return this.imageIDsRepresentedByWinner;
    }
    public String getImageID() {
        return imageID;
    }
    public NormalizedKey getTrainTestConcern() {
        return trainTestConcern;
    }
    public NormalizedValue getTrainTestConcernValue() {
        return trainTestConcernValue;
    }
    public NormalizedValue getNewValue() {
        return newValue;
    }
    public void setNewValue(NormalizedValue value){
    	this.newValue = value;
    }
    public NormalizedValue getExistingValueForKey() {
        return existingValueForKey;
    }
    public void noteTieVote(){
    	this.fate = ScoringFate.ABSTAIN_FROM_CHANGING_SCORE_VOTE_TIE;
    }
    public ScoringFate getScoringFate(){
    	return this.fate;
    }
}