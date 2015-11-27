package edu.oregonstate.eecs.iis.avatolcv.core;

import java.util.ArrayList;
import java.util.List;

public class EvaluationSet implements ScoringSet {
	public static double DEFAULT_EVALUATION_SPLIT = 0.7;
	private List<NormalizedImageInfo> niis = null;
	private List<NormalizedImageInfo> niisWithValueForKey = new ArrayList<NormalizedImageInfo>();
	//private List<NormalizedImageInfo> niisForEvaluationTrain = new ArrayList<NormalizedImageInfo>();
	//private List<NormalizedImageInfo> niisForEvaluationScore = new ArrayList<NormalizedImageInfo>();
	private List<ModalImageInfo> modals = new ArrayList<ModalImageInfo>();
	private double percentToTrainOn = 0;
	public EvaluationSet(List<NormalizedImageInfo> niis, String keyToScore, double percentToTrainOn){
		this.niis = niis;
		this.percentToTrainOn = percentToTrainOn;
		// isolate the ones that have values for the scoring key
		for (NormalizedImageInfo nii : this.niis){
			if (nii.hasKey(keyToScore)){
				if (nii.hasValueForKey(keyToScore)){
					niisWithValueForKey.add(nii);
				}
			}
		}
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
	public List<ModalImageInfo> getImagesToTrainOnForKeyValue(String key,
			String value) {
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
}
