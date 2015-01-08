package edu.oregonstate.eecs.iis.avatolcv.mb;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;

/*
 * Specimen-per-row matrix - i.e. the BAT skull project
 */
public class SPRMatrix extends Matrix {
	private static SPRTaxonIdMapper taxonIdMapper;
	public SPRMatrix(Document doc, SPRTaxonIdMapper mapper) throws MorphobankDataException {
	    super(doc);
	    taxonIdMapper = mapper;
	}
	 
	/*
	 * loadTaxonsForMediaOld which looked through specimen nodes, was found to not always work.  New version uses matrix data instead.
	 */
	public void loadTaxonsForMedia()throws MorphobankDataException{
	    for (String rowId : this.rowIds){
	    	MatrixRow row = matrixRowForTaxonMap.get(rowId);
	    	String actualTaxonId = taxonIdMapper.getNormalizedTaxonId(rowId);
	    	row.overRideCellsWithActualTaxonId(actualTaxonId);
	        String taxonName = row.getTaxonName();
    		//System.out.println("loadTaxonsForMedia name " + taxonName + " rowId " + rowId);
	        String pureTaxonName = taxonIdMapper.getPureTaxonNameForName(taxonName);
	        this.taxonNameForId.put(actualTaxonId, pureTaxonName);
	        this.taxonIdForName.put(pureTaxonName, actualTaxonId);
	        //System.out.println("mapped rowId " + actualTaxonId + " to pureTaxonName " + pureTaxonName);
	    	List<String> mediaIds = row.getAllMediaIds();
	    	for (String mediaId : mediaIds){
	    		
	    		this.taxonsForMediaId.put(mediaId, actualTaxonId);
	    	}
	    }
	}
    public List<Taxon> getAllTaxa() throws MorphobankDataException {
    	List<Taxon> result = new ArrayList<Taxon>();
    	List<String> taxonIdsSeen = new ArrayList<String>();
    	for (String rowId : rowIds){
    		
    		MatrixRow row = matrixRowForTaxonMap.get(rowId);
	    	String actualTaxonId = taxonIdMapper.getNormalizedTaxonId(rowId);
	    	if (!taxonIdsSeen.contains(actualTaxonId)){
	    		taxonIdsSeen.add(actualTaxonId);
	    		String taxonName = row.getTaxonName();
	    		String pureTaxonName = taxonIdMapper.getPureTaxonNameForName(taxonName);
	    		//System.out.println("loadTaxonsForMedia purename " + pureTaxonName + " rowId " + actualTaxonId);
	    		Taxon t = new Taxon(actualTaxonId, pureTaxonName);
	    		result.add(t);
	    	}
    	}
    	return result;
    }
}
