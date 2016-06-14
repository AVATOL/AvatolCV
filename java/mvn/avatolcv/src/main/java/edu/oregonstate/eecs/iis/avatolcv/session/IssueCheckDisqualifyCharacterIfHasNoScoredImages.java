package edu.oregonstate.eecs.iis.avatolcv.session;

import java.util.ArrayList;
import java.util.List;

import edu.oregonstate.eecs.iis.avatolcv.AvatolCVConstants;
import edu.oregonstate.eecs.iis.avatolcv.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.normalized.NormalizedImageInfo;
import edu.oregonstate.eecs.iis.avatolcv.normalized.NormalizedKey;
import edu.oregonstate.eecs.iis.avatolcv.normalized.NormalizedValue;

public class IssueCheckDisqualifyCharacterIfHasNoScoredImages implements
		IssueCheck {

	private static final char NL = '\n';
	private List<NormalizedImageInfo> niis = null;
    private List<NormalizedKey> scoringConcernKeys = null;
    private SessionInfo sessionInfo = null;
	public IssueCheckDisqualifyCharacterIfHasNoScoredImages(List<NormalizedImageInfo> niis, List<NormalizedKey> scoringConcernKeys, SessionInfo sessionInfo){
		this.niis = niis;
		this.scoringConcernKeys = scoringConcernKeys;
		this.sessionInfo = sessionInfo;
	}
	@Override
	public List<DataIssue> runIssueCheck() throws AvatolCVException {
		List<DataIssue> dataIssues = new ArrayList<DataIssue>();
		List<NormalizedImageInfo> unscoreds = new ArrayList<NormalizedImageInfo>();
		for (NormalizedKey scoringConcernKey : this.scoringConcernKeys){
			int scoreCount = 0;
			for (NormalizedImageInfo nii : niis){
				if (!nii.isExcluded()){
					if (nii.hasKey(scoringConcernKey)){
					    if (!nii.isExcludedByValueForKey(scoringConcernKey)){
					        if (nii.hasValueForKey(scoringConcernKey)){
	                            scoreCount++;
	                        }
					    }
					}
				}
			}
			if (scoreCount == 0){
				sessionInfo.disqualifyScoringConcern(scoringConcernKey);
				DataIssue di = new DataIssue();
				di.setDescription("FYI - Disqualified character : " + scoringConcernKey.getName() + " due to lack of scored images that survived exclusion");
				di.addActionOption("No action required - other characters can be scored.");
				di.setType(getIssueType());
				dataIssues.add(di);
			}
		}
		return dataIssues;
	}
	@Override
	public String getIssueType() {
		return this.getClass().getName();
	}

}
