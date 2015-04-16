package edu.oregonstate.eecs.iis.avatolcv.core;

import edu.oregonstate.eecs.iis.avatolcv.ws.BisqueSessionException;

public interface Step {
    public void consumeProvidedData() throws BisqueSessionException;
    public boolean needsAnswering();
    public View getView();
}
