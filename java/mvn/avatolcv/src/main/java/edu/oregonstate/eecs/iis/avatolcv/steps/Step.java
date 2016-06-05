package edu.oregonstate.eecs.iis.avatolcv.steps;

import java.util.Hashtable;
import java.util.List;

import edu.oregonstate.eecs.iis.avatolcv.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.session.DataIssue;

public interface Step {
    void init() throws AvatolCVException ;
    void consumeProvidedData() throws AvatolCVException;
    boolean hasFollowUpDataLoadPhase();
    boolean isEnabledByPriorAnswers();
    boolean shouldRenderIfBackingIntoIt();
    List<DataIssue> getDataIssues();
}
