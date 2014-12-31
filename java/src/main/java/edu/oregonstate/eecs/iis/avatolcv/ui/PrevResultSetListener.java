package edu.oregonstate.eecs.iis.avatolcv.ui;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class PrevResultSetListener implements MouseListener {
	private RunSelector runSelector = null;
    public PrevResultSetListener(RunSelector runSelector){
    	this.runSelector = runSelector;
    }
	@Override
	public void mouseClicked(MouseEvent arg0) {
		if (this.runSelector.backButtonNeeded()){
			this.runSelector.goToPrevSession();
			this.runSelector.expressDataForCurrentMetadata();
		}
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

}
