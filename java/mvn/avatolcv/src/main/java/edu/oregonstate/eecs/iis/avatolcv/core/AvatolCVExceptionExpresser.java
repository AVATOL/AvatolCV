package edu.oregonstate.eecs.iis.avatolcv.core;

public interface AvatolCVExceptionExpresser {
    void showException(AvatolCVException e, String header);
    void showException(Exception e, String header);
}
