package edu.oregonstate.eecs.iis.avatolcv.session;

import java.util.ArrayList;
import java.util.List;

import edu.oregonstate.eecs.iis.avatolcv.AvatolCVConstants;
import edu.oregonstate.eecs.iis.avatolcv.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.normalized.NormalizedImageInfo;
import edu.oregonstate.eecs.iis.avatolcv.normalized.NormalizedKey;
import edu.oregonstate.eecs.iis.avatolcv.normalized.NormalizedValue;

public class IssueCheckNotifyIfCharacterHasNoUnscoredImages implements
		IssueCheck {

	private List<NormalizedImageInfo> niis = null;
    private List<NormalizedKey> scoringConcernKeys = null;
	public IssueCheckNotifyIfCharacterHasNoUnscoredImages(List<NormalizedImageInfo> niis, List<NormalizedKey> scoringConcernKeys){
		this.niis = niis;
		this.scoringConcernKeys = scoringConcernKeys;
	}
	@Override
	public List<DataIssue> runIssueCheck() throws AvatolCVException {
		List<DataIssue> dataIssues = new ArrayList<DataIssue>();
		List<NormalizedImageInfo> unscoreds = new ArrayList<NormalizedImageInfo>();
		for (NormalizedKey scoringConcernKey : this.scoringConcernKeys){
			int unscoredImageCount = 0;
			for (NormalizedImageInfo nii : niis){
				if (!nii.isExcluded()){
					if (nii.hasKey(scoringConcernKey)){
					    // don't count NPA as unscored here as this is the list we are trying to generate to tell them to score.
                        // but...  NPA is the I Don't Know score, so they can't fix that problem.
                        // see the NPA declaration for more details
					    if (!nii.isExcludedByValueForKey(scoringConcernKey)){
					        if (!nii.hasValueForKey(scoringConcernKey)){
	                            unscoredImageCount++;
	                        }
	                        else {
	                            //System.out.println("yes Scored: " + scoringConcernKey + " " + nii.getImageID() + " " + nii.getValueForKey(scoringConcernKey));
	                        }
					    }
					}
				}
			}
			if (unscoredImageCount == 0){
				DataIssue di = new DataIssue();
				di.setDescription("FYI - Character has no unscored images : " + scoringConcernKey.getName());
				di.addActionOption("No action needed - the training examples from this character may benefit scoring any other characters in the run");
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
