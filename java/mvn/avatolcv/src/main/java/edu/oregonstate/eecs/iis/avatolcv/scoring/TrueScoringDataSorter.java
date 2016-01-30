package edu.oregonstate.eecs.iis.avatolcv.scoring;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import edu.oregonstate.eecs.iis.avatolcv.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.normalized.NormalizedKey;
import edu.oregonstate.eecs.iis.avatolcv.normalized.NormalizedValue;

/*
 * TrueScoringDataSorter organizes by taxon but has a train list and test list for each, supports "make them all test" or "omit testing, just use training ones"
 */
public class TrueScoringDataSorter {
	private List<ScoringSet> sets = null;
	private NormalizedKey nKey = null;
	private List<NormalizedValue> valuesForKey = new ArrayList<NormalizedValue>();
	private List<ModalImageInfo> allModals = new ArrayList<ModalImageInfo>();
	private Hashtable<NormalizedValue, List<ModalImageInfo>> miisForValueHashTraining = new Hashtable<NormalizedValue, List<ModalImageInfo>>();
	private Hashtable<NormalizedValue, List<ModalImageInfo>> miisForValueHashScoring = new Hashtable<NormalizedValue, List<ModalImageInfo>>();

	public TrueScoringDataSorter(List<ScoringSet> sets, NormalizedKey nKey) throws AvatolCVException {
		this.sets = sets;
		this.nKey = nKey;
		for (ScoringSet set : sets){
		    List<ModalImageInfo> scoringModals = set.getImagesToScore();
		    List<ModalImageInfo> trainingModals = set.getImagesToTrainOn();
		    allModals.addAll(scoringModals);
		    allModals.addAll(trainingModals);
		}
		EvaluationSetsKeySorter.loadValuesForKey(this.allModals, this.nKey, this.valuesForKey);
		sortByImageValueOfKeyAndMode();
	}
	/**
	 * Make a special list of images for each value of the key. So if an image has taxon=taxonX, it goes in the taxonX list
	 */
	private void sortByImageValueOfKeyAndMode(){
	    for (NormalizedValue nv : valuesForKey){
	        for (ModalImageInfo mii : allModals ){
	            if (mii.getNormalizedImageInfo().getValueForKey(nKey).equals(nv)){
	            	if (mii.isTraining()){
	            		List<ModalImageInfo> miisForValue = miisForValueHashTraining.get(nv);
		                if (null == miisForValue){
		                    miisForValue = new ArrayList<ModalImageInfo>();
		                    miisForValueHashTraining.put(nv, miisForValue);
		                }
		                miisForValue.add(mii);
	            	}
	            	else {
	            		List<ModalImageInfo> miisForValue = miisForValueHashScoring.get(nv);
		                if (null == miisForValue){
		                    miisForValue = new ArrayList<ModalImageInfo>();
		                    miisForValueHashScoring.put(nv, miisForValue);
		                }
		                miisForValue.add(mii);
	            	}
	                
	            }
	        }
	    }
	}
	public List<ModalImageInfo> getTrainingImagesForKey(NormalizedValue nv){
		List<ModalImageInfo> miisForValue = miisForValueHashTraining.get(nv);
		return miisForValue;
	}

	public List<ModalImageInfo> getScoringImagesForKey(NormalizedValue nv){
		List<ModalImageInfo> miisForValue = miisForValueHashScoring.get(nv);
		return miisForValue;
	}
	public void makeAllForValueTest(NormalizedValue nv){
		List<ModalImageInfo> miisForValue = miisForValueHashTraining.get(nv);
		for (ModalImageInfo mii : miisForValue){
			mii.setAsToScore();
		}
	}
	public void forgetImagesMarkedForTest(NormalizedValue nv) throws AvatolCVException {
		List<ModalImageInfo> miisForValue = miisForValueHashScoring.get(nv);
		for (ModalImageInfo mii : miisForValue){
			mii.getNormalizedImageInfo().excludeForSession("userRemovedDueToTrainingTestConflict");
		}
	}
}
