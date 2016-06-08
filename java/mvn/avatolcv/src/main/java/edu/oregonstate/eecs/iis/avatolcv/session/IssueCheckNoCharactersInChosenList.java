package edu.oregonstate.eecs.iis.avatolcv.session;

import java.util.ArrayList;
import java.util.List;

import edu.oregonstate.eecs.iis.avatolcv.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.datasource.ChoiceItem;
import edu.oregonstate.eecs.iis.avatolcv.normalized.NormalizedImageInfo;
import edu.oregonstate.eecs.iis.avatolcv.normalized.NormalizedKey;

public class IssueCheckNoCharactersInChosenList implements IssueCheck {
    private SessionInfo sessionInfo = null;
	public IssueCheckNoCharactersInChosenList(SessionInfo sessionInfo){
		this.sessionInfo = sessionInfo;
	}
	@Override
	public List<DataIssue> runIssueCheck() throws AvatolCVException {
		List<DataIssue> dataIssues = new ArrayList<DataIssue>();
		List<ChoiceItem> scoringConcerns = sessionInfo.getChosenScoringConcerns();
		if (scoringConcerns.size() == 0){
			DataIssue di = new DataIssue();
			di.setDescription("All characters have been disqualified for this scoring goal.");
			di.addActionOption("Try going back and changing the scoring goal, or address the other issues that might be causing exclusions that lead to this.");
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
