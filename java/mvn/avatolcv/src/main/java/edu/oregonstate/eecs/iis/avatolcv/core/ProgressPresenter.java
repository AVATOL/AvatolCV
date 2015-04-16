package edu.oregonstate.eecs.iis.avatolcv.core;

public interface ProgressPresenter {
    public void updateProgress(int percent);
    public void setMessage(String m);
}
