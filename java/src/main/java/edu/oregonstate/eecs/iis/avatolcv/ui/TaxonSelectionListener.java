package edu.oregonstate.eecs.iis.avatolcv.ui;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import edu.oregonstate.eecs.iis.avatolcv.AvatolCVException;

public class TaxonSelectionListener implements MouseListener {
	private ResultMatrixCell cell = null;
	private ResultMatrixColumn rmc = null;
    public TaxonSelectionListener(ResultMatrixCell cell, ResultMatrixColumn rmc){
    	this.rmc = rmc;
    	this.cell = cell;
    }
	@Override
	public void mouseClicked(MouseEvent arg0) {
		//try {
		    //ImageBrowser.hostImageBrowser(cell.getSessionDataForTaxon().getImageBrowser());
		    this.rmc.focusOnCell(this.cell);
		//}
		//catch(AvatolCVException ace){
		//	ace.printStackTrace();
		//	System.out.println(ace.getMessage());
		//}
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		cell.highlightCellForHover(true);

	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		cell.highlightCellForHover(false);

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
