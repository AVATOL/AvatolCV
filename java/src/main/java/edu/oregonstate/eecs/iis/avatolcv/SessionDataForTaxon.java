package edu.oregonstate.eecs.iis.avatolcv;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SessionDataForTaxon extends SessionData {
	private String taxonId = null;
	private double combinedScore = -1.0;
    public SessionDataForTaxon(String taxonId,String charId, String charName,
			List<ResultImage> trainingImages,
			List<ResultImage> scoredImages,
			List<ResultImage> unscoredImages){
    	super(charId, charName, trainingImages, scoredImages, unscoredImages);
        this.taxonId = taxonId;
        this.combinedScore = calculateCombinedScore();
	}
    public double calculateCombinedScore(){
    	Random r = new Random();
        double score = r.nextDouble() % 1.0;
        return score;
    }
    public String getTaxonId(){
    	return this.taxonId;
    }
    public double getCombinedScore(){
    	return this.combinedScore;
    }
}
