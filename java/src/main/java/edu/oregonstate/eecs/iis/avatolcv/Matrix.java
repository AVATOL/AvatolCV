package edu.oregonstate.eecs.iis.avatolcv;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Matrix {
	protected List<String> rowIds = new ArrayList<String>();
	protected List<String> charIds = new ArrayList<String>();
	protected Hashtable<String,MatrixRow> matrixRowForTaxonMap = new Hashtable<String, MatrixRow>();
	protected Hashtable<String, String> taxonsForMediaId = new Hashtable<String, String>();
	
    public Matrix(Document doc) throws MorphobankDataException {
    	NodeList nodes = doc.getElementsByTagName("CodedDescription");
    	for (int i = 0; i < nodes.getLength(); i++){
    		Node codedDescription = nodes.item(i);
    		MatrixRow matrixRow = new MatrixRow(codedDescription);
    		String taxonId = matrixRow.getTaxonId();
    		rowIds.add(taxonId);
    		matrixRowForTaxonMap.put(taxonId, matrixRow);
    	}
    	this.charIds = loadCharIdsFromAllRows();
    	for (String rowId : rowIds){
    		MatrixRow row = matrixRowForTaxonMap.get(rowId);
    		//System.out.println("column count for row " + taxonId + " is " + row.getColumnCount());
    	}
    }
    public String getTaxonIdForMediaId(String mediaId) throws MorphobankDataException {
    	String taxonId = taxonsForMediaId.get(mediaId);
    	if (null == taxonId){
    		throw new MorphobankDataException("no taxonId available for mediaId " + mediaId);
    	}
    	return taxonId;
    }
    /*
     * loadTaxonsForMediaOld which looked through specimen nodes, was found to not always work.  New version uses matrix data instead.
     */
    public void loadTaxonsForMedia()throws MorphobankDataException{
    	for (String rowId : this.rowIds){
    		MatrixRow row = matrixRowForTaxonMap.get(rowId);
    		List<String> mediaIds = row.getAllMediaIds();
    		for (String mediaId : mediaIds){
    			this.taxonsForMediaId.put(mediaId, rowId);
    		}
    	}
    }
    public List<String> getImageNamesForSpecialCase(){
    	ArrayList<String> result = new ArrayList<String>();
    	//Thyroptera tricolor 268577
    	//<CategoricalCharacter id='c427749'><Representation><Label>Upper I1 presence</Label><
    	MatrixRow row = matrixRowForTaxonMap.get("t281047");
    	MatrixCell cell = row.getCellForCharacter("c427753");
    	result.addAll(cell.getMediaIds());
    	row = matrixRowForTaxonMap.get("t281048");
    	cell = row.getCellForCharacter("c427753");
    	result.addAll(cell.getMediaIds());
    	
    	row = matrixRowForTaxonMap.get("t281049");
    	cell = row.getCellForCharacter("c427753");
    	result.addAll(cell.getMediaIds());
    	
    	row = matrixRowForTaxonMap.get("t281050");
    	cell = row.getCellForCharacter("c427753");
    	result.addAll(cell.getMediaIds());
    	
        row = matrixRowForTaxonMap.get("t281051");
    	cell = row.getCellForCharacter("c427753");
    	result.addAll(cell.getMediaIds());
    	
    	row = matrixRowForTaxonMap.get("t281052");
    	cell = row.getCellForCharacter("c427753");
    	result.addAll(cell.getMediaIds());
    	
    	row = matrixRowForTaxonMap.get("t281053");
    	cell = row.getCellForCharacter("c427753");
    	result.addAll(cell.getMediaIds());
    	
    	row = matrixRowForTaxonMap.get("t281054");
    	cell = row.getCellForCharacter("c427753");
    	result.addAll(cell.getMediaIds());
    	
    	row = matrixRowForTaxonMap.get("t281055");
    	cell = row.getCellForCharacter("c427753");
    	result.addAll(cell.getMediaIds());
    	
        return result;
    }
    public List<String> loadCharIdsFromAllRows(){
    	List<String> allCharIds = new ArrayList<String>();
    	for (String taxonId : rowIds){
    		MatrixRow row = matrixRowForTaxonMap.get(taxonId);
    		List<String> charIds = row.getCharacterIds();

        	for (String charId : charIds){
        		if (!allCharIds.contains(charId)){
        			allCharIds.add(charId);
        		}
        	}
    	}
    	Collections.sort(allCharIds);
    	//System.out.println("charId count is " + allCharIds.size());
    	return allCharIds;
    }
    public List<MatrixCell> getCellsForCharacter(String charId) {
    	List<MatrixCell> cells = new ArrayList<MatrixCell>();
    	for (String rowId : rowIds){
    		MatrixRow matrixRow = matrixRowForTaxonMap.get(rowId);
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

