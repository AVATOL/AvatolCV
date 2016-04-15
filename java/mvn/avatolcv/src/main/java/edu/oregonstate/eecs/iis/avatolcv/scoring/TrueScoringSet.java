package edu.oregonstate.eecs.iis.avatolcv.scoring;

import java.util.ArrayList;
import java.util.List;

import edu.oregonstate.eecs.iis.avatolcv.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.normalized.NormalizedImageInfo;
import edu.oregonstate.eecs.iis.avatolcv.normalized.NormalizedKey;
import edu.oregonstate.eecs.iis.avatolcv.normalized.NormalizedValue;

public class TrueScoringSet implements ScoringSet {	
	public static final String NL = System.getProperty("line.separator");
	private List<NormalizedImageInfo> niis = null;
	private List<NormalizedImageInfo> niisWithKeyToScore = new ArrayList<NormalizedImageInfo>();
	private List<ModalImageInfo> modals = new ArrayList<ModalImageInfo>();
	private NormalizedKey keyToScore = null;
	public TrueScoringSet(List<NormalizedImageInfo> niis, NormalizedKey keyToScore) throws AvatolCVException {
		this.niis = niis;
		this.keyToScore = keyToScore;
		System.out.println("keyToScore " + keyToScore);
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
        for (NormalizedImageInfo nii : niisWithKeyToScore){
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

	@Override
	public double getTrainingPercentage() {
		int trainCount = getImagesToTrainOn().size();
		int testCount = getImagesToScore().size();
		double total = trainCount + testCount;
		double percent = (double)trainCount / total;
		return percent;
	}
	@Override
	public String getSummaryString() {
		StringBuilder sb = new StringBuilder();
		sb.append(NL);
		sb.append("    TRUE SCORING SET" + NL);
		sb.append("key to score       : " + this.keyToScore + NL);
		sb.append(NL + "training images    : " + NL);
		for (ModalImageInfo mii : modals){
			if (mii.isTraining()){
				NormalizedImageInfo nii = mii.getNormalizedImageInfo();
				sb.append("    ID: " + nii.getImageID() + "   NAME: " + nii.getImageName() + "    COORDS: " + nii.getAnnotationString());
			}
		}
		sb.append(NL + "images to score    : " + NL);
		for (ModalImageInfo mii : modals){
			if (mii.isScoring()){
				NormalizedImageInfo nii = mii.getNormalizedImageInfo();
				sb.append("    ID: " + nii.getImageID() + "   NAME: " + nii.getImageName() + "    COORDS: " + nii.getAnnotationString());
			}
		}
		return "" + sb;
	}
}
