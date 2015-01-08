package edu.oregonstate.eecs.iis.avatolcv.ui;

import javax.swing.JProgressBar;

public class ProgressBarUpdater implements Runnable {
	private JProgressBar progressBar = null;
	private int  value = -1;
    public ProgressBarUpdater(JProgressBar progressBar, int value){
    	this.progressBar = progressBar;
    	this.value = value;
    }
	@Override
	public void run() {
		this.progressBar.setValue(this.value);
	}

}
