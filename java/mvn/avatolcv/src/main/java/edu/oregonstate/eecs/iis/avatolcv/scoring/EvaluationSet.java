package edu.oregonstate.eecs.iis.avatolcv.scoring;

import java.util.ArrayList;
import java.util.List;

import edu.oregonstate.eecs.iis.avatolcv.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.core.ModalImageInfo;
import edu.oregonstate.eecs.iis.avatolcv.normalized.NormalizedImageInfo;
import edu.oregonstate.eecs.iis.avatolcv.normalized.NormalizedKey;
import edu.oregonstate.eecs.iis.avatolcv.normalized.NormalizedValue;

public class EvaluationSet implements ScoringSet {
	public static double DEFAULT_EVALUATION_SPLIT = 0.7;
	private List<NormalizedImageInfo> niis = null;
	private List<NormalizedImageInfo> niisWithValueForKey = new ArrayList<NormalizedImageInfo>();
	//private List<NormalizedImageInfo> niisForEvaluationTrain = new ArrayList<NormalizedImageInfo>();
	//private List<NormalizedImageInfo> niisForEvaluationScore = new ArrayList<NormalizedImageInfo>();
	private List<ModalImageInfo> modals = new ArrayList<ModalImageInfo>();
	private double percentToTrainOn = 0;
	private NormalizedKey keyToScore = null;
	public EvaluationSet(List<NormalizedImageInfo> niis, NormalizedKey keyToScore, double percentToTrainOn){
		this.niis = niis;
		this.percentToTrainOn = percentToTrainOn;
		this.keyToScore = keyToScore;
		// isolate the ones that have values for the scoring key
		System.out.println("EVALUATION SET " + keyToScore + " GIVEN this many niis " + niis.size());
		for (NormalizedImageInfo nii : this.niis){
		    //LEFT OFF HERE
			if (nii.hasKey(keyToScore)){
				//LEFT OFF HERE - WHY MB CASE NO MATCH?
				if (nii.hasValueForKey(keyToScore)){
					niisWithValueForKey.add(nii);
				}
			}
		}
		System.out.println("EVALUATION SET for " + keyToScore + " has this many niisWithValueForKey " + niisWithValueForKey.size());
		// get integer number to hold out
		int holdoutCount = getHoldoutCount(niisWithValueForKey.size(), this.percentToTrainOn);
		NormalizedImageInfo nii = null;
		for (int i = 0 ; i < holdoutCount ; i++){
			 nii = niisWithValueForKey.get(i);
			 ModalImageInfo mii = new ModalImageInfo(nii);
			 mii.setAsToScore();
			 modals.add(mii);
		}
		for (int i = holdoutCount; i < niisWithValueForKey.size(); i++){
			nii = niisWithValueForKey.get(i);
			 ModalImageInfo mii = new ModalImageInfo(nii);
			 mii.setAsTraining();
			 modals.add(mii);
		}
	}
	public static int getHoldoutCount(int totalCount, double percentToTrainOn){
		double trainSize = totalCount * percentToTrainOn;
		int countToTrain = (int)trainSize;
		return totalCount - countToTrain;
	}
	@Override
	public List<ModalImageInfo> getImagesToTrainOn() {
		List<ModalImageInfo> result = new ArrayList<ModalImageInfo>();
		for (ModalImageInfo mii : modals){
			if (mii.isTraining()){
				result.add(mii);
			}
		}
		return result;
	}
	@Override
	public List<ModalImageInfo> getImagesToScore() {
		List<ModalImageInfo> result = new ArrayList<ModalImageInfo>();
		for (ModalImageInfo mii : modals){
			if (mii.isScoring()){
				result.add(mii);
			}
		}
		return result;
	}
	@Override
	public List<ModalImageInfo> getImagesToTrainOnForKeyValue(NormalizedKey key,
			NormalizedValue value) throws AvatolCVException {
		List<ModalImageInfo> result = new ArrayList<ModalImageInfo>();
		for (ModalImageInfo mii : this.modals){
			if (mii.isTraining()){
				NormalizedImageInfo nii = mii.getNormalizedImageInfo();
				if (nii.hasKey(key)){
					if (nii.getValueForKey(key).equals(value)){
						result.add(mii);
					}
				}
			}
			
		}
		return result;
	}
    @Override
    public List<NormalizedKey> getAllKeys() {
        List<NormalizedKey> result = new ArrayList<NormalizedKey>();
        for (NormalizedImageInfo nii : niisWithValueForKey){
            List<NormalizedKey> keys = nii.getKeys();
            for (NormalizedKey key : keys){
                if (!result.contains(key)){
                    result.add(key);
                }
            }
        }
        return result;
    }
	@Override
	public NormalizedKey getKeyToScore() {
		return this.keyToScore;
	}
	@Override
	public String getScoringConcernName() {
		return this.keyToScore.getName();
	}
	public double getPercentToTrainOn(){
	    return this.percentToTrainOn;
	}
}
