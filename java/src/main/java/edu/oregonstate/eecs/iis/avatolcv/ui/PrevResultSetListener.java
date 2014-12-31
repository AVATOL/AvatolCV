package edu.oregonstate.eecs.iis.avatolcv.ui;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class PrevResultSetListener extends ResultSetListener implements MouseListener {
	private RunSelector runSelector = null;
	private JavaUI javaUI = null;
    public PrevResultSetListener(RunSelector runSelector, JavaUI javaUI){
    	this.runSelector = runSelector;
    	this.javaUI = javaUI;
    }
	@Override
	public void mouseClicked(MouseEvent arg0) {
		if (this.runSelector.backButtonNeeded()){
			this.runSelector.goToPrevSession();
			expressResultSet(this.runSelector, this.javaUI);
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
