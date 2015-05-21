package edu.oregonstate.eecs.iis.avatolcv.core;

public interface Step {
    public void init() throws AvatolCVException ;
    public void consumeProvidedData() throws AvatolCVException;
    public String getView();
}
