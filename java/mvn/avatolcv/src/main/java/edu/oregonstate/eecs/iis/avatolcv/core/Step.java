package edu.oregonstate.eecs.iis.avatolcv.core;

import java.util.Hashtable;

public interface Step {
    void init() throws AvatolCVException ;
    void consumeProvidedData() throws AvatolCVException;
    boolean hasFollowUpDataLoadPhase();
}
