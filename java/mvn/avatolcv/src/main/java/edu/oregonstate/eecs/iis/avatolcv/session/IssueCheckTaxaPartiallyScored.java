package edu.oregonstate.eecs.iis.avatolcv.session;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;

import edu.oregonstate.eecs.iis.avatolcv.AvatolCVConstants;
import edu.oregonstate.eecs.iis.avatolcv.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.normalized.NormalizedImageInfo;
import edu.oregonstate.eecs.iis.avatolcv.normalized.NormalizedKey;
import edu.oregonstate.eecs.iis.avatolcv.normalized.NormalizedValue;

public class IssueCheckTaxaPartiallyScored implements IssueCheck {
	private static final char NL = '\n';
	private List<NormalizedImageInfo> niis = null;
    private List<NormalizedKey> scoringConcernKeys = null;
    private NormalizedKey trainTestConcern = null;
    private Hashtable<NormalizedValue, List<NormalizedImageInfo>> trainTestHash = new Hashtable<NormalizedValue, List<NormalizedImageInfo>>();
	public IssueCheckTaxaPartiallyScored(List<NormalizedImageInfo> niis, List<NormalizedKey> scoringConcernKeys, NormalizedKey trainTestConcern){
		this.niis = niis;
		this.scoringConcernKeys = scoringConcernKeys;
		this.trainTestConcern = trainTestConcern;
	}
	@Override
	public List<DataIssue> runIssueCheck() throws AvatolCVException {
		List<DataIssue> dataIssues = new ArrayList<DataIssue>();
		List<NormalizedValue> ttVals = new ArrayList<NormalizedValue>();
		// divide the niis up into lists hashed by the trainTestConcernValue
		for (NormalizedImageInfo nii : niis){
			if (!nii.isExcluded()){
				// ignore those that don't have the trainTestConcern - those would need to be excluded at ScoringConfiguration - only an issue for Bisque and likely never happen 
				if (nii.hasKey(trainTestConcern)){
					NormalizedValue ttVal = nii.getValueForKey(trainTestConcern);
					if (!ttVals.contains(ttVal)){
						ttVals.add(ttVal);
					}
					List<NormalizedImageInfo> niisForVal = trainTestHash.get(ttVal);
					if (null == niisForVal){
						niisForVal = new ArrayList<NormalizedImageInfo>();
						trainTestHash.put(ttVal,  niisForVal);
					}
					niisForVal.add(nii);
				}
			}
		}
		// now everything is hashed.  For each ttVal, look at all the Niis and see if they are either all scored or all not scored.
		for (NormalizedValue ttVal : ttVals){
			List<NormalizedImageInfo> niisForVal = trainTestHash.get(ttVal);
			List<NormalizedImageInfo> scoredNiisForVal   = new ArrayList<NormalizedImageInfo>();
			List<NormalizedImageInfo> unscoredNiisForVal = new ArrayList<NormalizedImageInfo>();
			for (NormalizedImageInfo nii : niisForVal){ // look through all the niis for this taxon
			    if (!nii.isExcluded()){
			        for (NormalizedKey scoringConcernKey : scoringConcernKeys){
	                    if (nii.hasKey(scoringConcernKey)){
	                        // ignore NPA cells in this discovery, see the NPA declaration for details
	                        if (!nii.isExcludedByValueForKey(scoringConcernKey)){
	                            if (nii.hasValueForKey(scoringConcernKey)){ // is it scored?
	                                scoredNiisForVal.add(nii);
	                            }
	                            else {
	                                unscoredNiisForVal.add(nii);
	                            }
	                        }
	                    }
	                } 
			    }
				
			}
			if ((scoredNiisForVal.size() != 0) && (unscoredNiisForVal.size() != 0)){
				DataIssue di = createDataIssue(ttVal,scoredNiisForVal, unscoredNiisForVal);
				dataIssues.add(di);
			}
		}
		return dataIssues;
	}
	private DataIssue createDataIssue(NormalizedValue trainTestValue, List<NormalizedImageInfo> scoredNiis, List<NormalizedImageInfo> unscoredNiis){
		DataIssue di = new DataIssue();
		di.setDescription(trainTestValue.getName() + " has both scored and unscored images.  The scoring algorithm needs them to be all one way or the other." + NL);

		StringBuilder sb = new StringBuilder();
		sb.append("score the following images, return to dataset choice screen and check the \"pull in changes\" checkbox" + NL );
		for (NormalizedImageInfo nii : unscoredNiis){
			List<NormalizedKey> keys = nii.getKeys();
			Collections.sort(keys);
			for (NormalizedKey key : keys){
				NormalizedValue val = nii.getValueForKey(key);
				sb.append("      " + key.getName() + ": " + val.getName());
			}
			sb.append(NL);
		}
		di.addActionOption("" + sb);
		di.setType(getIssueType());
		return di;
	}
	@Override
	public String getIssueType() {
		return this.getClass().getName();
	}

}
