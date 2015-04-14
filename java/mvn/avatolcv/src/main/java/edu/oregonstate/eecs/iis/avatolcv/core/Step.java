package edu.oregonstate.eecs.iis.avatolcv.core;

import edu.oregonstate.eecs.iis.avatolcv.ws.BisqueSessionException;

public interface Step {
    public void activate();
    public void consumeProvidedData() throws BisqueSessionException;
}
