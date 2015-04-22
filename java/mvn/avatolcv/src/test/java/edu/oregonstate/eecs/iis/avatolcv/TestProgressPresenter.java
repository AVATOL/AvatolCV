package edu.oregonstate.eecs.iis.avatolcv;

import edu.oregonstate.eecs.iis.avatolcv.core.ProgressPresenter;

public class TestProgressPresenter implements ProgressPresenter {
    @Override
    public void updateProgress(int percent) {
        System.out.println("percent done " + percent);
    }

    @Override
    public void setMessage(String m) {
        System.out.println(m);  
    }
    
}
