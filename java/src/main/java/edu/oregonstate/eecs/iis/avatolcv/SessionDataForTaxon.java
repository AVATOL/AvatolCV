package edu.oregonstate.eecs.iis.avatolcv;

import java.util.ArrayList;
import java.util.List;

public class SessionDataForTaxon extends SessionData {
	private String taxonId = null;
    public SessionDataForTaxon(String taxonId,String charId, String charName,
			List<ResultImage> trainingImages,
			List<ResultImage> scoredImages,
			List<ResultImage> unscoredImages){
    	super(charId, charName, trainingImages, scoredImages, unscoredImages);
        this.taxonId = taxonId;
	}
    public String getTaxonId(){
    	return this.taxonId;
    }
    
}
