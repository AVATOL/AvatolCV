package edu.oregonstate.eecs.iis.avatolcv.steps;

import edu.oregonstate.eecs.iis.avatolcv.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.algorithm.ScoringAlgorithm;
import edu.oregonstate.eecs.iis.avatolcv.algorithm.ScoringAlgorithm.ScoringSessionFocus;
import edu.oregonstate.eecs.iis.avatolcv.session.SessionInfo;

public class SessionFocusStep  extends Answerable implements Step {
    private SessionInfo sessionInfo = null;
    private String scoringAlgName = null;
    public SessionFocusStep(SessionInfo sessionInfo) throws AvatolCVException {
        this.sessionInfo = sessionInfo;
    }
    @Override
    public void init() throws AvatolCVException {
        // TODO Auto-generated method stub

    }
    public void setSelectedScoringAlgName(String algName) throws AvatolCVException {
        this.scoringAlgName = algName;
    }
    @Override
    public void consumeProvidedData() throws AvatolCVException {
        this.sessionInfo.setSelectedScoringAlgName(this.scoringAlgName);
    }
    @Override
    public boolean hasFollowUpDataLoadPhase() {
        // TODO Auto-generated method stub
        return false;
    }
    @Override
    public boolean isEnabledByPriorAnswers() {
        return true;
    }
    @Override
	public boolean shouldRenderIfBackingIntoIt() {
		return true;
	}
    
}
