package edu.oregonstate.eecs.iis.avatolcv;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import edu.oregonstate.eecs.iis.avatolcv.mb.MorphobankBundle;

public class ResultMatrixColumn {
	
	private SessionDataForTaxa sdft = null;
	private List<ResultMatrixCell> cells = new ArrayList<ResultMatrixCell>();
	private ResultMatrixCell focusCell = null;
	private Hashtable<String, ResultMatrixCell> cellsForTaxonName = new Hashtable<String, ResultMatrixCell>();
    public ResultMatrixColumn(MorphobankBundle mb, SessionDataForTaxa sdft) throws AvatolCVException {
    	this.sdft = sdft;
    	int count = sdft.getTaxonCount();
    	for (int i = 0; i < count; i++){
    		SessionDataForTaxon sd = sdft.getSessionDataForTaxonAtIndex(i);
    		String taxonId = sd.getTaxonId();
    		String taxonName = mb.getTaxonNameForId(taxonId);
    		ResultMatrixCell cell = new ResultMatrixCell(taxonId, taxonName, sd);
    		this.cellsForTaxonName.put(taxonName, cell);
    	}
    }
    public void adjustToNewThreshold(double threshold){
    	for (ResultMatrixCell cell : this.cells){
    		cell.adjustToNewThreshold(threshold);
    	}
    }
    public List<String> getTaxonNames(){
    	List<String> names = new ArrayList<String>();
    	for (ResultMatrixCell cell: this.cells){
    		names.add(cell.getName());
    	}
    	return names;
    }
    public int getRowCount(){
    	return this.sdft.getTaxonCount();
    }
    public ResultMatrixCell getCellAtIndex(int index){
    	return this.cells.get(index);
    }
    public ResultMatrixCell getCellForTaxonName(String taxonName){
    	return this.cellsForTaxonName.get(taxonName);
    }
    public void focusOnCell(String taxonName){
    	if (null != this.focusCell){
    		this.focusCell.setFocus(false);
    	}
    	this.focusCell = getCellForTaxonName(taxonName);
    	this.focusCell.setFocus(true);
    }
}
