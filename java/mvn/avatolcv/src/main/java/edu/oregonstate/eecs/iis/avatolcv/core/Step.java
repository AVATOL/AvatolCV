package edu.oregonstate.eecs.iis.avatolcv.core;

public interface Step {
    public void consumeProvidedData() throws AvatolCVException;
    public boolean needsAnswering();
    public View getView();
}
