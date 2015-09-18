package edu.oregonstate.eecs.iis.avatolcv;

/**
 * 
 * @author admin-jed
 * 
 * Different UI layers can implement this to decide how to render error info.  As of 9/18/15, I notice that the 
 * only classes using this are javaFX-aware classes, so this might have been an unnecessary feature.
 *
 */
public interface AvatolCVExceptionExpresser {
    void showException(AvatolCVException e, String header);
    void showException(Exception e, String header);
}
