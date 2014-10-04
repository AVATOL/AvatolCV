package edu.oregonstate.eecs.iis.avatolcv;

import java.util.List;

import org.w3c.dom.Document;

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
	    for (String taxon : this.taxonIds){
	    MatrixRow row = matrixRowForTaxonMap.get(taxon);
	    	List<String> mediaIds = row.getAllMediaIds();
	    	for (String mediaId : mediaIds){
	    		String actualTaxonId = this.taxonIdMapper.getNormalizedTaxonId(taxon);
	    		this.taxonsForMediaId.put(mediaId, actualTaxonId);
	    	}
	    }
	}
}
