package edu.oregonstate.eecs.iis.avatolcv;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Random;

import edu.oregonstate.eecs.iis.avatolcv.algata.ResultImage;

public class SessionDataForTaxon extends SessionData {
	private String taxonId = null;
	private String taxonName = null;
	private double combinedScore = -1.0;
	
    public SessionDataForTaxon(String taxonId,String taxonName, String charId, String charName,
			List<ResultImage> trainingImages,
			List<ResultImage> scoredImages,
			List<ResultImage> unscoredImages){
    	super(charId, charName, trainingImages, scoredImages, unscoredImages);
        this.taxonId = taxonId;
        this.taxonName = taxonName;
        this.combinedScore = calculateCombinedScore();
	}
    public double getRandomInRange(double min, double max){
    	double curVal = max + 1;
    	while(curVal < min || curVal > max){
    		Random r = new Random();
            curVal = r.nextDouble() % 1.0;
    	}
    	return curVal;
    }
    public double getAverageConfidence(List<ResultImage> ris){
    	double total = 0;
    	for (ResultImage ri : ris){
    	    String confString = ri.getConfidence();
    	    double confVal = new Double(confString).doubleValue();
    	    
    		total = total + confVal;
    	}
    	double average = total / ris.size();
    	return average;
    }
    public String getConfidenceForState(String state){
    	List<ResultImage> imagesForState = new ArrayList<ResultImage>();
    	for (ResultImage ri : this.scoredImages){
    		String charStateName = ri.getCharacterStateName();
    		if (state.equals(charStateName)){
    			imagesForState.add(ri);
    		}
    	}
    	double averageConfidence = getAverageConfidence(imagesForState);
    	return "" + averageConfidence;
    }
    public String getBelievedState(){
    	double maxConfidence = 0;
    	String maxConfidenceState = "";
    	Hashtable<String, List<ResultImage>> charStateMap = new Hashtable<String, List<ResultImage>>();
    	if (null == this.scoredImages || this.scoredImages.size() == 0){
    		return "NA";
    	}
    	List<String> states = new ArrayList<String>();
    	for (ResultImage ri : this.scoredImages){
    		String charStateName = ri.getCharacterStateName();
    		if (!states.contains(charStateName)){
    			states.add(charStateName);
    		}
    		List<ResultImage> ris = charStateMap.get(charStateName);
    		if (null == ris){
    			ris = new ArrayList<ResultImage>();
    			charStateMap.put(charStateName, ris);
    		}
    		ris.add(ri);
    	}
    	for (String stateName : states){
    		List<ResultImage> ris = charStateMap.get(stateName);
    		double averageConfidence = getAverageConfidence(ris);
    		if (averageConfidence > maxConfidence){
    			maxConfidence = averageConfidence;
    			maxConfidenceState = stateName;
    		}
    	}
    	return maxConfidenceState;
    }
    public double calculateCombinedScore(){
    	if (this.scoredImages == null){
    		
            double score = getRandomInRange(0.5, 1.0);
            return score;
    	}
    	int count = this.scoredImages.size();
    	double total = 0;
    	for (ResultImage ri : this.scoredImages){
    		String confidenceString = ri.getConfidence();
    		Double d = new Double(confidenceString);
    		double val = d.doubleValue();
    		total += val;
    	}
    	double average = total / count;
    	return average;
    }
    public String getTaxonName(){
    	return this.taxonName;
    }
    public String getTaxonId(){
    	return this.taxonId;
    }
    public double getCombinedScore(){
    	return this.combinedScore;
    }
    public String getCombinedScoreString(){
    	String scoreString = "" + this.combinedScore;
        int indexOfPoint = scoreString.indexOf('.');
        if (scoreString.length() > indexOfPoint + 3){
        	scoreString = scoreString.substring(0,indexOfPoint + 3);
        }
        return scoreString;
    }
}