package edu.oregonstate.eecs.iis.avatolcv.steps;

import edu.oregonstate.eecs.iis.avatolcv.core.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.core.ScoringAlgorithms;
import edu.oregonstate.eecs.iis.avatolcv.core.ScoringAlgorithms.ScoringSessionFocus;
import edu.oregonstate.eecs.iis.avatolcv.core.SessionInfo;
import edu.oregonstate.eecs.iis.avatolcv.core.Step;

public class SessionFocusStep implements Step {
    private ScoringAlgorithms scoringAlgorithms = null;
    private SessionInfo sessionInfo = null;
    private ScoringSessionFocus scoringFocus = null;
    private String scoringAlgName = null;
    public SessionFocusStep(SessionInfo sessionInfo) throws AvatolCVException {
        this.sessionInfo = sessionInfo;
    }
    public ScoringAlgorithms getScoringAlgorithms(){
        return this.sessionInfo.getScoringAlgorithms();
    }
    @Override
    public void init() throws AvatolCVException {
        // TODO Auto-generated method stub

    }
    public void setScoringAlgInfo(ScoringAlgorithms.ScoringSessionFocus focus, String algName) throws AvatolCVException {
        this.scoringFocus = focus;
        this.scoringAlgName = algName;
    }
    @Override
    public void consumeProvidedData() throws AvatolCVException {
        this.sessionInfo.setScoringAlgInfo(scoringFocus, scoringAlgName);
    }
    @Override
    public boolean hasFollowUpDataLoadPhase() {
        // TODO Auto-generated method stub
        return false;
    }
}
