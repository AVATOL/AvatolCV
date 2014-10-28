package edu.oregonstate.eecs.iis.avatolcv;

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
	        String taxonName = row.getTaxonName();
	        String pureTaxonName = taxonIdMapper.getPureTaxonNameForName(taxonName);
	        this.taxonNameForId.put(rowId, pureTaxonName);
	        System.out.println("mapped rowId " + rowId + " to pureTaxonName " + pureTaxonName);
	    	List<String> mediaIds = row.getAllMediaIds();
	    	for (String mediaId : mediaIds){
	    		String actualTaxonId = taxonIdMapper.getNormalizedTaxonId(rowId);
	    		this.taxonsForMediaId.put(mediaId, actualTaxonId);
	    	}
	    }
	}
}
