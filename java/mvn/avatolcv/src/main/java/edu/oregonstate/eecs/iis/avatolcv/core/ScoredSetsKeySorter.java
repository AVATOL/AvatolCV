package edu.oregonstate.eecs.iis.avatolcv.core;

import java.util.List;

/*
 * Evaluation Sets are handed in, and logic here adjusts the train vs test settings in the ModalImageInfos that belong to the EvaluationSet.
 * Then, just as before, the EvaluationSets can be passed along to feed the training_ file generation process.
 */
public class ScoredSetsKeySorter {
	private List<EvaluationSet> sets = null;
	private String key = null;
	public ScoredSetsKeySorter(List<EvaluationSet> sets, String key){
		this.sets = sets;
		this.key = key;
	}
	
	// returns the names of each taxon, for example
	public List<String> getValuesPresentForKey(){
		return null;
	}
	
	public boolean isValueToTrain(String value){
		return false;
	}
	public List<String> getScoringConcernNames(){
		return null;
	}
	public int getTotalTrainingCountForItem(){
		return 0;
	}

	public int getTotalScoringCountForItem(){
		return 0;
	}
	public int getTotalCountForItemAndScoringConcern(){
		return 0;
	}
	public List<NormalizedImageInfo> getImagesInBothTrainingAndScoring(){
		return null;
	}
	public void setValueToTrain(String value){
		
	}
	public void setValueToScore(String value){
		
	}
	
}
