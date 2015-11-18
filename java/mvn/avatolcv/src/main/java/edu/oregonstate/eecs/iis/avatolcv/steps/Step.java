package edu.oregonstate.eecs.iis.avatolcv.steps;

import java.util.Hashtable;

import edu.oregonstate.eecs.iis.avatolcv.AvatolCVException;

public interface Step {
    void init() throws AvatolCVException ;
    void consumeProvidedData() throws AvatolCVException;
    boolean hasFollowUpDataLoadPhase();
    boolean isEnabledByPriorAnswers();
}
