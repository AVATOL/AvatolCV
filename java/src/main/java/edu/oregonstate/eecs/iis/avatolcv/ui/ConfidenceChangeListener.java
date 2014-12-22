package edu.oregonstate.eecs.iis.avatolcv.ui;

import javax.swing.JSlider;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class ConfidenceChangeListener implements ChangeListener, Runnable {
	private ResultMatrixColumn rmc = null;
	private ChangeEvent mostRecentChangeEvent = null;
    public ConfidenceChangeListener(ResultMatrixColumn rmc){
    	this.rmc = rmc;
    }
	@Override
	public void stateChanged(ChangeEvent arg0) {
		mostRecentChangeEvent = arg0;
		SwingUtilities.invokeLater(this);

	}
	@Override
	public void run() {
		JSlider source = (JSlider)this.mostRecentChangeEvent.getSource();
	    //if (!source.getValueIsAdjusting()) {
	    int value = (int)source.getValue();
	    double doubleValue = value;
		this.rmc.adjustToNewThreshold(doubleValue);
	}

}
