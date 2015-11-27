package edu.oregonstate.eecs.iis.avatolcv.core;

import java.util.ArrayList;
import java.util.List;

import edu.oregonstate.eecs.iis.avatolcv.AvatolCVException;

public class TrueScoringSet implements ScoringSet {
	private List<NormalizedImageInfo> niis = null;
	private List<NormalizedImageInfo> niisWithKeyToScore = new ArrayList<NormalizedImageInfo>();
	private List<ModalImageInfo> modals = new ArrayList<ModalImageInfo>();
	public TrueScoringSet(List<NormalizedImageInfo> niis, String keyToScore) throws AvatolCVException {
		this.niis = niis;
		// isolate the ones that have values for the scoring key
		for (NormalizedImageInfo nii : this.niis){
			if (nii.hasKey(keyToScore)){
				niisWithKeyToScore.add(nii);
			}
		}
		for (NormalizedImageInfo nii : this.niisWithKeyToScore){
			ModalImageInfo mii = new ModalImageInfo(nii);
			if (nii.hasValueForKey(keyToScore)){
				mii.setAsTraining();
				modals.add(mii);
			}
			else {
				mii.setAsToScore();
				modals.add(mii);
			}
		}
		if (getUnscoredCount() == 0){
			throw new AvatolCVException("TrueScoringSet needs at least one image to be not yet scored.");
		}
	}
	
	public int getUnscoredCount(){
		int count = 0;
		for (ModalImageInfo mii : modals){
			if (mii.isScoring()){
				count++;
			}
		}
		return count;
	}
	@Override
	public List<ModalImageInfo> getImagesToTrainOn() {
		List<ModalImageInfo> modalsToScore = new ArrayList<ModalImageInfo>();
		for (ModalImageInfo mii : modals){
			if (mii.isTraining()){
				modalsToScore.add(mii);
			}
		}
		return modalsToScore;
	}

	@Override
	public List<ModalImageInfo> getImagesToScore() {
		List<ModalImageInfo> modalsToScore = new ArrayList<ModalImageInfo>();
		for (ModalImageInfo mii : modals){
			if (mii.isScoring()){
				modalsToScore.add(mii);
			}
		}
		return modalsToScore;
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
