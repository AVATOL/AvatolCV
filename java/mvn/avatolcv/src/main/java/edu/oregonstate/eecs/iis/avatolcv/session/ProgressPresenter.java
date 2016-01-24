package edu.oregonstate.eecs.iis.avatolcv.session;

public interface ProgressPresenter {
    public void updateProgress(String processName, double percentDone);
    public void setMessage(String processName, String m);
}
