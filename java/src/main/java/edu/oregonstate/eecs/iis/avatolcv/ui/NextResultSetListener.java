package edu.oregonstate.eecs.iis.avatolcv.ui;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class NextResultSetListener implements MouseListener {
	private RunSelector runSelector = null;
    public NextResultSetListener(RunSelector runSelector){
    	this.runSelector = runSelector;
    }
	@Override
	public void mouseClicked(MouseEvent arg0) {
		if (this.runSelector.backButtonNeeded()){
			this.runSelector.goToNextSession();
			this.runSelector.expressDataForCurrentMetadata();
		}
	}
	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub

	}

}
