package edu.oregonstate.eecs.iis.avatolcv.steps;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.oregonstate.eecs.iis.avatolcv.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.session.DataIssue;
import edu.oregonstate.eecs.iis.avatolcv.session.SessionInfo;

public class SessionFocusStep  extends Answerable implements Step {
    private SessionInfo sessionInfo = null;
    private String scoringAlgName = null;
    private static final Logger logger = LogManager.getLogger(SessionFocusStep.class);

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
    public void setScoringGoalTrueScoring(boolean val){
    	this.sessionInfo.setScoringGoalTrueScoring(val);
    }
    @Override
    public void consumeProvidedData() throws AvatolCVException {
        this.sessionInfo.setSelectedScoringAlgName(this.scoringAlgName);
        logger.info("scoring algorithm chosen " + this.scoringAlgName);
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
    @Override
	public List<DataIssue> getDataIssues() {
		return null;
	}
}
