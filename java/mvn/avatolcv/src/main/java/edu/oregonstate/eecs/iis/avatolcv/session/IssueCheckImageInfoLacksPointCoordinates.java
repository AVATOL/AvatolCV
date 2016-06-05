package edu.oregonstate.eecs.iis.avatolcv.session;

import java.util.ArrayList;
import java.util.List;

import edu.oregonstate.eecs.iis.avatolcv.AvatolCVConstants;
import edu.oregonstate.eecs.iis.avatolcv.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.normalized.NormalizedImageInfo;
import edu.oregonstate.eecs.iis.avatolcv.normalized.NormalizedKey;
import edu.oregonstate.eecs.iis.avatolcv.normalized.NormalizedValue;

public class IssueCheckImageInfoLacksPointCoordinates implements IssueCheck {
	private static final char TAB = '\t';
	private static final char NL = '\n';
	private List<NormalizedImageInfo> niis = null;
    private List<NormalizedKey> scoringConcernKeys = null;
	public IssueCheckImageInfoLacksPointCoordinates(List<NormalizedImageInfo> niis, List<NormalizedKey> scoringConcernKeys){
		this.niis = niis;
		this.scoringConcernKeys = scoringConcernKeys;
	}
	@Override
	public List<DataIssue> runIssueCheck() throws AvatolCVException {
		List<NormalizedImageInfo> lackingCoords = new ArrayList<NormalizedImageInfo>();
		for (NormalizedKey scoringConcernKey : this.scoringConcernKeys){
			for (NormalizedImageInfo nii : niis){
				if (!nii.isExcluded()){
					if (nii.hasKey(scoringConcernKey)){
						if (AvatolCVConstants.NPA.equals(nii.getValueForKey(scoringConcernKey).getName())){
							System.out.println("coords NA for NPA cells: " + scoringConcernKey + " " + nii.getImageID());
						}
						else {
							// ! NPA, so need to check if coords missing
							if (nii.getAnnotationCoordinates().equals("")){
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
			dataIssues.add(di);
		}
		return dataIssues;
	}

}
