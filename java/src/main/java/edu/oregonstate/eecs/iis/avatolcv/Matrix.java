package edu.oregonstate.eecs.iis.avatolcv;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Matrix {
	private List<String> taxonIds = new ArrayList<String>();
	private List<String> charIds = new ArrayList<String>();
	private Hashtable<String,MatrixRow> matrixRowForTaxonMap = new Hashtable<String, MatrixRow>();
	
    public Matrix(Document doc) throws MorphobankDataException {
    	NodeList nodes = doc.getElementsByTagName("CodedDescription");
    	for (int i = 0; i < nodes.getLength(); i++){
    		Node codedDescription = nodes.item(i);
    		MatrixRow matrixRow = new MatrixRow(codedDescription);
    		String taxonId = matrixRow.getTaxonId();
    		taxonIds.add(taxonId);
    		matrixRowForTaxonMap.put(taxonId, matrixRow);
    	}
    	this.charIds = loadCharIdsFromAllRows();
    	for (String taxonId : taxonIds){
    		MatrixRow row = matrixRowForTaxonMap.get(taxonId);
    		System.out.println("column count for row " + taxonId + " is " + row.getColumnCount());
    	}
    	
    }
    public List<String> loadCharIdsFromAllRows(){
    	List<String> allCharIds = new ArrayList<String>();
    	for (String taxonId : taxonIds){
    		MatrixRow row = matrixRowForTaxonMap.get(taxonId);
    		List<String> charIds = row.getCharacterIds();

        	for (String charId : charIds){
        		if (!allCharIds.contains(charId)){
        			allCharIds.add(charId);
        		}
        	}
    	}
    	Collections.sort(allCharIds);
    	System.out.println("charId count is " + allCharIds.size());
    	return allCharIds;
    }
    public List<MatrixCell> getCellsForCharacter(String charId) {
    	List<MatrixCell> cells = new ArrayList<MatrixCell>();
    	for (String taxonId : taxonIds){
    		MatrixRow matrixRow = matrixRowForTaxonMap.get(taxonId);
    		MatrixCell cell = matrixRow.getCellForCharacter(charId);
    		if (null != cell){
    			cells.add(cell);
    		}
    	}
    	return cells;
    }
    public List<MatrixCell> getCellsWithMedia(String charId){
    	List<MatrixCell> cellsWithMedia = new ArrayList<MatrixCell>();
    	List<MatrixCell> cells = getCellsForCharacter(charId);
    	for (MatrixCell cell : cells){
    		if (!cell.hasMedia()){
    			cellsWithMedia.add(cell);
    		}
    	}
    	return cellsWithMedia;
    }
    public List<MatrixCell> getUnscoredCells(String charId) {
    	List<MatrixCell> unscoredCells = new ArrayList<MatrixCell>();
    	List<MatrixCell> cells = getCellsForCharacter(charId);
    	for (MatrixCell cell : cells){
    		if (!cell.isScored()){
    			unscoredCells.add(cell);
    		}
    	}
    	return unscoredCells;
    }
    
}

