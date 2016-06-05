package edu.oregonstate.eecs.iis.avatolcv.session;

import java.util.ArrayList;
import java.util.List;

import edu.oregonstate.eecs.iis.avatolcv.AvatolCVConstants;
import edu.oregonstate.eecs.iis.avatolcv.normalized.NormalizedImageInfo;
import edu.oregonstate.eecs.iis.avatolcv.normalized.NormalizedKey;
import edu.oregonstate.eecs.iis.avatolcv.normalized.NormalizedValue;

public class IssueCheckUnscoredImageInfo implements IssueCheck {
	private static final char TAB = '\t';
	private static final char NL = '\n';
	private List<NormalizedImageInfo> niis = null;
    private List<NormalizedKey> scoringConcernKeys = null;
	public IssueCheckUnscoredImageInfo(List<NormalizedImageInfo> niis, List<NormalizedKey> scoringConcernKeys){
		this.niis = niis;
		this.scoringConcernKeys = scoringConcernKeys;
	}
	@Override
	public List<DataIssue> runIssueCheck() {
		List<NormalizedImageInfo> unscoreds = new ArrayList<NormalizedImageInfo>();
		for (NormalizedKey scoringConcernKey : this.scoringConcernKeys){
			for (NormalizedImageInfo nii : niis){
				if (nii.hasKey(scoringConcernKey)){
					if (!nii.hasValueForKey(scoringConcernKey)){
						// don't count NPA as unscored here as this is the list we are trying to generate to tell them to score.
						// but...  NPA is the I Don't Know score, so they can't fix that problem.
						// see the NPA declaration for more details
						if (!AvatolCVConstants.NPA.equals(nii.getValueForKey(scoringConcernKey))){
							unscoreds.add(nii);
						}
						
						//System.out.println("NOT Scored: " + scoringConcernKey + " " + nii.getImageID() + " " + nii.getValueForKey(scoringConcernKey));
					}
					else {
						//System.out.println("yes Scored: " + scoringConcernKey + " " + nii.getImageID() + " " + nii.getValueForKey(scoringConcernKey));
					}
				}
			}
		}
		List<DataIssue> dataIssues = new ArrayList<DataIssue>();
		for (NormalizedImageInfo nii : unscoreds){
			DataIssue di = new DataIssue();
			StringBuilder sb = new StringBuilder();
			sb.append("unscored image : " + nii.getImageID());
			List<NormalizedKey> keys = nii.getKeys();
			for (NormalizedKey key : keys){
				NormalizedValue val = nii.getValueForKey(key);
				sb.append("       " + key.getName() + ":" + "  " + val.getName() + ",");
			}
			di.setDescription("" + sb);
			di.addActionOption("Score by hand, return to dataset choice screen and check the \"pull in changes\" checkbox");
			dataIssues.add(di);
		}
		return dataIssues;
	}

}
