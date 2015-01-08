package edu.oregonstate.eecs.iis.avatolcv.mb;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import edu.oregonstate.eecs.iis.avatolcv.AvatolCVException;

public class Matrix {
	protected List<String> rowIds = new ArrayList<String>();
	protected List<String> charIds = new ArrayList<String>();
	protected Hashtable<String,MatrixRow> matrixRowForTaxonMap = new Hashtable<String, MatrixRow>();
	protected Hashtable<String, String> taxonsForMediaId = new Hashtable<String, String>();
	protected Hashtable<String, String> taxonNameForId = new Hashtable<String, String>();
	protected Hashtable<String, String> taxonIdForName = new Hashtable<String, String>();
	
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
    public List<Taxon> getAllTaxa() throws MorphobankDataException { // have to throw this due to method overriding this one in SPRMatrix
    	List<Taxon> result = new ArrayList<Taxon>();
    	for (String id : rowIds){
    		String taxonName = taxonNameForId.get(id);
    		Taxon t = new Taxon(id, taxonName);
    		result.add(t);
    	}
    	return result;
    }
    public List<MatrixRow> getRows(){
    	List<MatrixRow> rows = new ArrayList<MatrixRow>();
    	for (String taxonId : rowIds){
    		rows.add(matrixRowForTaxonMap.get(taxonId));
    	}
    	return rows;
    }
    public List<String> getScoredCharacterIds(){
    	List<String> scoredCharacters = new ArrayList<String>();
    	for (String taxonId : rowIds){
    		MatrixRow row = matrixRowForTaxonMap.get(taxonId);
    		for (String charId : charIds){

        		MatrixCell cell = row.getCellForCharacter(charId);
        		if (cell.hasWorkableScore()){
        			if (!scoredCharacters.contains(charId)){
        				scoredCharacters.add(charId);
        			}
        		}
    		}
    	}
    	return scoredCharacters;
    }
    public List<String> getScoredTaxonNames() throws AvatolCVException {
    	List<String> taxonNames = new ArrayList<String>();
    	List<String> charIds = getScoredCharacterIds();
    	for (String charId : charIds){
        	List<MatrixCell> cells = this.getCellsForCharacter(charId);
        	for (MatrixCell cell : cells){
        		List<String> mediaIds = cell.getMediaIds();
        		if (mediaIds.size() > 0){
        			String mediaId = cell.getMediaIds().get(0);
            		String taxonId = getTaxonIdForMediaId(mediaId);
            		String taxonName = getTaxonNameForId(taxonId);
            		if (!(taxonNames.contains(taxonName))){
            			taxonNames.add(taxonName);
            		}
        		}
        	}
    	}
    	return taxonNames;
    }
    public String getTaxonNameForId(String taxonId) throws AvatolCVException {
    	String taxonName = taxonNameForId.get(taxonId);
    	if (null == taxonName){
    		throw new AvatolCVException("no taxonName available for taxonId " + taxonId);
    	}
    	return taxonName;
    }
    public String getTaxonIdForName(String taxonName) throws AvatolCVException {
    	String taxonId = taxonIdForName.get(taxonName);
    	if (null == taxonId){
    		throw new AvatolCVException("no taxonId available for taxonName " + taxonName);
    	}
    	return taxonId;
    }
    public String getTaxonIdForMediaId(String mediaId) throws AvatolCVException {
    	String taxonId = taxonsForMediaId.get(mediaId);
    	if (null == taxonId){
    		throw new AvatolCVException("no taxonId available for mediaId " + mediaId);
    	}
    	return taxonId;
    }
    /*
     * loadTaxonsForMediaOld which looked through specimen nodes, was found to not always work.  New version uses matrix data instead.
     */
    public void loadTaxonsForMedia()throws MorphobankDataException{
    	for (String rowId : this.rowIds){
    		MatrixRow row = matrixRowForTaxonMap.get(rowId);
    		String taxonName = row.getTaxonName();
    		this.taxonNameForId.put(rowId, taxonName);
    		//System.out.println("loadTaxonsForMedia name " + taxonName + " rowId " + rowId);
    		this.taxonIdForName.put(taxonName, rowId);
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
    public List<MatrixCell> getCellsForCharacterAndTaxon(String charId, String taxonId){
    	List<MatrixCell> result = new ArrayList<MatrixCell>();
    	List<MatrixCell> cellsForChar = getCellsForCharacter(charId);
    	for (MatrixCell mc : cellsForChar){
    		if (mc.getTaxonId().equals(taxonId)){
    			result.add(mc);
    		}
    	}
    	return result;
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
    		if (!cell.hasWorkableScore()){
    			unscoredCells.add(cell);
    		}
    	}
    	return unscoredCells;
    }
    
}

