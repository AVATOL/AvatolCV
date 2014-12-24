package edu.oregonstate.eecs.iis.avatolcv.ui;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import edu.oregonstate.eecs.iis.avatolcv.AvatolCVException;

public class TaxonSelectionListener implements MouseListener {
	private ResultMatrixCell cell = null;
    public TaxonSelectionListener(ResultMatrixCell cell){
    	this.cell = cell;
    }
	@Override
	public void mouseClicked(MouseEvent arg0) {
		try {
		    ImageBrowser.hostImageBrowser(cell.getSessionDataForTaxon().getImageBrowser());
		}
		catch(AvatolCVException ace){
			ace.printStackTrace();
			System.out.println(ace.getMessage());
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
