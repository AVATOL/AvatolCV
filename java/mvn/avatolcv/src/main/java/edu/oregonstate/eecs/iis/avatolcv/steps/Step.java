package edu.oregonstate.eecs.iis.avatolcv.steps;

import edu.oregonstate.eecs.iis.avatolcv.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.session.SessionInfo;

public interface Step {
    void init() throws AvatolCVException ;
    void consumeProvidedData() throws AvatolCVException;
    boolean hasFollowUpDataLoadPhase();
    boolean isEnabledByPriorAnswers();
    boolean shouldRenderIfBackingIntoIt();
    SessionInfo getSessionInfo();
}
