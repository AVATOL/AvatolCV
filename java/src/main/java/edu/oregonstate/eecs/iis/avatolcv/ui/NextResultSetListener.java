package edu.oregonstate.eecs.iis.avatolcv.ui;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import edu.oregonstate.eecs.iis.avatolcv.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.SessionDataForTaxa;
import edu.oregonstate.eecs.iis.avatolcv.mb.MorphobankBundle;
import edu.oregonstate.eecs.iis.avatolcv.mb.MorphobankDataException;

public class NextResultSetListener extends ResultSetListener implements MouseListener {
	private RunSelector runSelector = null;
	private JavaUI javaUI = null;
    public NextResultSetListener(RunSelector runSelector, JavaUI javaUI){
    	this.runSelector = runSelector;
    	this.javaUI = javaUI;
    }
	@Override
	public void mouseClicked(MouseEvent arg0) {
		if (this.runSelector.nextButtonNeeded()){
			this.runSelector.goToNextSession();
			expressResultSet(this.runSelector, this.javaUI);
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
