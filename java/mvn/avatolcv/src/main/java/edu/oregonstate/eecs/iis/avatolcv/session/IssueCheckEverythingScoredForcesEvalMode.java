package edu.oregonstate.eecs.iis.avatolcv.session;

import java.util.ArrayList;
import java.util.List;

import edu.oregonstate.eecs.iis.avatolcv.AvatolCVConstants;
import edu.oregonstate.eecs.iis.avatolcv.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.normalized.NormalizedImageInfo;
import edu.oregonstate.eecs.iis.avatolcv.normalized.NormalizedKey;
import edu.oregonstate.eecs.iis.avatolcv.normalized.NormalizedValue;

public class IssueCheckEverythingScoredForcesEvalMode implements IssueCheck {

	private static final char NL = '\n';
	private List<NormalizedImageInfo> niis = null;
    private List<NormalizedKey> scoringConcernKeys = null;
	public IssueCheckEverythingScoredForcesEvalMode(List<NormalizedImageInfo> niis, List<NormalizedKey> scoringConcernKeys){
		this.niis = niis;
		this.scoringConcernKeys = scoringConcernKeys;
	}
	@Override
	public List<DataIssue> runIssueCheck() throws AvatolCVException {
		List<NormalizedImageInfo> unscoreds = new ArrayList<NormalizedImageInfo>();
		for (NormalizedKey scoringConcernKey : this.scoringConcernKeys){
			for (NormalizedImageInfo nii : niis){
				if (!nii.isExcluded()){
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
		}
		List<DataIssue> dataIssues = new ArrayList<DataIssue>();
		if (unscoreds.size() == 0){
			
			DataIssue di = new DataIssue();
			di.setDescription("FYI - You chose 'score unscored images' mode but all images are already scored.  This automatically puts the run into evaluation (compare scores) mode.  No need to change anything.  You will be able to select which itms to score at the Scoring Configuration Screen.");
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
