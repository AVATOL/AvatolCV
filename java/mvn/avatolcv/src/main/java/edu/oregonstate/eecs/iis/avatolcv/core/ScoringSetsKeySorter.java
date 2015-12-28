package edu.oregonstate.eecs.iis.avatolcv.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;

import edu.oregonstate.eecs.iis.avatolcv.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.normalized.NormalizedImageInfo;
import edu.oregonstate.eecs.iis.avatolcv.normalized.NormalizedKey;
import edu.oregonstate.eecs.iis.avatolcv.normalized.NormalizedValue;

/*
 * Evaluation Sets are handed in, and logic here adjusts the train vs test settings in the ModalImageInfos that belong to the EvaluationSet.
 * Then, just as before, the EvaluationSets can be passed along to feed the training_ file generation process.
 * 
 * The process here is to group the images that have the same value for the key, then split on those
 */
public class ScoringSetsKeySorter {
	private List<EvaluationSet> sets = null;
	private NormalizedKey nKey = null;
	private List<NormalizedValue> valuesForKey = new ArrayList<NormalizedValue>();
	private List<ModalImageInfo> allModals = new ArrayList<ModalImageInfo>();
	//private List<ModalImageInfo> training = new ArrayList<ModalImageInfo>();
    //private List<ModalImageInfo> scoring = new ArrayList<ModalImageInfo>();
	private Hashtable<NormalizedValue, List<ModalImageInfo>> miisForValueHash = new Hashtable<NormalizedValue, List<ModalImageInfo>>();
	private double percentageToTrainWith = 0.0;
	
	public ScoringSetsKeySorter(List<EvaluationSet> sets, NormalizedKey nKey){
		this.sets = sets;
		this.nKey = nKey;
		this.percentageToTrainWith = sets.get(0).getPercentToTrainOn();
		for (EvaluationSet set : sets){
		    List<ModalImageInfo> scoringModals = set.getImagesToScore();
		    List<ModalImageInfo> trainingModals = set.getImagesToTrainOn();
		    allModals.addAll(scoringModals);
		    allModals.addAll(trainingModals);
		}
		loadValuesForKey();
		sortByImageValueOfKey();
		splitByPercentageToTrainWith();
	}
	/**
	 * Go through all the images and look for ones that have values for the key we are worried about, and note those values.
	 * So for example, if taxon is the key , then taxonX, taxonY, etc are the values.
	 */
	private void loadValuesForKey(){
	    for (ModalImageInfo mii : allModals){
            NormalizedImageInfo nii = mii.getNormalizedImageInfo();
            if (nii.hasKey(nKey)){
                NormalizedValue nv = nii.getValueForKey(nKey);
                if (!valuesForKey.contains(nv)){
                    valuesForKey.add(nv);
                }
            }
	    }
	}
	/**
	 * Make a special list of images for each value of the key. So if an image has taxon=taxonX, it goes in the taxonX list
	 */
	private void sortByImageValueOfKey(){
	    for (NormalizedValue nv : valuesForKey){
	        for (ModalImageInfo mii : allModals ){
	            if (mii.getNormalizedImageInfo().getValueForKey(nKey).equals(nv)){
	                List<ModalImageInfo> miisForValue = miisForValueHash.get(nv);
	                if (null == miisForValue){
	                    miisForValue = new ArrayList<ModalImageInfo>();
	                    miisForValueHash.put(nv, miisForValue);
	                }
	                miisForValue.add(mii);
	            }
	        }
	    }
	}
	/**
	 * Figure out how many images meet the percentageToTrainWith value.  Then gather that many images in the training list
	 * and put the others in scoring.
	 */
	private void splitByPercentageToTrainWith(){
	    int totalValueCount = valuesForKey.size();
	    double numberToTrain = totalValueCount * this.percentageToTrainWith;
	    int numberToTrainAsInt = (int) numberToTrain;
	    for (int i = 0; i < numberToTrainAsInt; i++){
	        NormalizedValue nv = valuesForKey.get(i);
	        List<ModalImageInfo> miis = miisForValueHash.get(nv);
	        for (ModalImageInfo mii : miis){
	            mii.setAsTraining();
	        }
	    }
	    for (int i = numberToTrainAsInt; i < totalValueCount; i++){
	        NormalizedValue nv = valuesForKey.get(i);
            List<ModalImageInfo> miis = miisForValueHash.get(nv);
            for (ModalImageInfo mii : miis){
                mii.setAsToScore();
            }
	    }
	}
	// returns the names of each taxon, for example
	public List<String> getValuesPresentForKey(){
	    List<String> values = new ArrayList<String>();
	    for (NormalizedValue nv : valuesForKey){
	        values.add(nv.getName());
	    }
	    Collections.sort(values);
		return values;
	}
	
	public boolean isValueToTrain(NormalizedValue value) throws AvatolCVException {
	    List<ModalImageInfo> miis = miisForValueHash.get(value);
	    return miis.get(0).isTraining();
	}
	
	public List<String> getScoringConcernNames(){
		List<String> result = new ArrayList<String>();
	    for (EvaluationSet set : sets){
		    result.add(set.getScoringConcernName());
		}
	    return result;
	}
	public int getTotalTrainingCount(){
		int count = 0;
		for (ModalImageInfo mii : allModals){
		    if (mii.isTraining()){
		        count++;
		    }
		}
		return count;
	}

	public int getTotalScoringCount(){
	    int count = 0;
        for (ModalImageInfo mii : allModals){
            if (mii.isScoring()){
                count++;
            }
        }
        return count;
	}
	
	public List<NormalizedImageInfo> getImagesInBothTrainingAndScoring(){
	    //TODO - deferred
	    return null;
	}
	public NormalizedValue getNormalizedValueForString(String name) throws AvatolCVException {
	    for (NormalizedValue nv : valuesForKey){
	        if (nv.getName().equals(name)){
	            return nv;
	        }
	    }
	    throw new AvatolCVException("no NormalizedValue matching name " + name  + " in ScoringSetsKeySorter.");
	}
	public void setValueToTrain(NormalizedValue nv) throws AvatolCVException {
	    System.out.println("setting value to train " + nv.getName());
		List<ModalImageInfo> miis = miisForValueHash.get(nv);
		for (ModalImageInfo mii: miis){
		    mii.setAsTraining();
		}
	}
	public void setValueToScore(NormalizedValue nv) throws AvatolCVException {
	    System.out.println("setting value to score " + nv.getName());
	    List<ModalImageInfo> miis = miisForValueHash.get(nv);
        for (ModalImageInfo mii: miis){
            mii.setAsToScore();
        }
	}
	public int getCountForValue(NormalizedValue nv){
	    List<ModalImageInfo> miis = miisForValueHash.get(nv);
	    if (null == miis){
	        return 0;
	    }
	    return miis.size();
	}
}
