package edu.oregonstate.eecs.iis.avatolcv.core;

public interface ProgressPresenter {
    public void updateProgress(String processName, int percent);
    public void setMessage(String processName, String m);
}
