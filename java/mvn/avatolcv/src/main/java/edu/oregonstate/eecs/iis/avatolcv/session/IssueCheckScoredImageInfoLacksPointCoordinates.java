package edu.oregonstate.eecs.iis.avatolcv.session;

import java.util.ArrayList;
import java.util.List;

import edu.oregonstate.eecs.iis.avatolcv.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.normalized.NormalizedImageInfo;
import edu.oregonstate.eecs.iis.avatolcv.normalized.NormalizedKey;
import edu.oregonstate.eecs.iis.avatolcv.normalized.NormalizedValue;

public class IssueCheckScoredImageInfoLacksPointCoordinates implements
		IssueCheck {
	private static final char TAB = '\t';
	private static final char NL = '\n';
	private List<NormalizedImageInfo> niis = null;
    private List<NormalizedKey> scoringConcernKeys = null;
	public IssueCheckScoredImageInfoLacksPointCoordinates(List<NormalizedImageInfo> niis, List<NormalizedKey> scoringConcernKeys){
		this.niis = niis;
		this.scoringConcernKeys = scoringConcernKeys;
	}
	@Override
	public List<DataIssue> runIssueCheck() throws AvatolCVException {
		List<NormalizedImageInfo> lackingCoords = new ArrayList<NormalizedImageInfo>();
		for (NormalizedKey scoringConcernKey : this.scoringConcernKeys){
			for (NormalizedImageInfo nii : niis){
				if (!nii.isExcluded()){
					if (nii.hasKey(scoringConcernKey)){ // is hasScoringConcern that is relevant
						if (nii.hasValueForKey(scoringConcernKey)){  // is scored.  Also, this appropriately ignores NPA cells - don't want to complain about lack of annotations on NPAs
							if (nii.getAnnotationCoordinates().equals("")){ //but missing coordinatess
								lackingCoords.add(nii);
								System.out.println("NO  coords: " + scoringConcernKey + " " + nii.getImageID() + " " + nii.getAnnotationCoordinates());
							}
							else {
								System.out.println("yes coords: " + scoringConcernKey + " " + nii.getImageID() + " " + nii.getAnnotationCoordinates());
							}
						}
					}
				}
			}
		}
		List<DataIssue> dataIssues = new ArrayList<DataIssue>();
		for (NormalizedImageInfo nii : lackingCoords){
			DataIssue di = new DataIssue();
			StringBuilder sb = new StringBuilder();
			sb.append("need point annotations: " + nii.getImageID());
			List<NormalizedKey> keys = nii.getKeys();
			for (NormalizedKey key : keys){
				NormalizedValue val = nii.getValueForKey(key);
				sb.append("       " + key.getName() + ":" + "  " + val.getName() + ",");
			}
			di.setDescription("" + sb);
			di.addActionOption("Annotate the image, return to dataset choice screen and check the \"pull in changes\" checkbox");
			di.setType(getIssueType());
			dataIssues.add(di);
		}
		return dataIssues;
	}
	@Override
	public String getIssueType() {
		return this.getClass().getName();
	}

}
