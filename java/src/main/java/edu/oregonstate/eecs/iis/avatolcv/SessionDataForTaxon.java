package edu.oregonstate.eecs.iis.avatolcv;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Random;

import edu.oregonstate.eecs.iis.avatolcv.algata.ImageSet;
import edu.oregonstate.eecs.iis.avatolcv.algata.ImageSetSupplier;
import edu.oregonstate.eecs.iis.avatolcv.algata.ResultImage;
import edu.oregonstate.eecs.iis.avatolcv.mb.MorphobankBundle;
import edu.oregonstate.eecs.iis.avatolcv.mb.MorphobankDataException;
import edu.oregonstate.eecs.iis.avatolcv.ui.ImageBrowser;

public class SessionDataForTaxon extends SessionData {
	private String taxonId = null;
	private String taxonName = null;
	private double combinedScore = -1.0;
	private ImageBrowser imageBrowser = null;
	private boolean isRegime2TrainingTaxon = false;
    public SessionDataForTaxon(String taxonId,String taxonName, String charId, String charName,
			List<ResultImage> trainingImages,
			List<ResultImage> scoredImages,
			List<ResultImage> unscoredImages,
			MorphobankBundle mb,
			boolean isRegime2TrainingTaxon) throws AvatolCVException {
    	super(charId, charName, trainingImages, scoredImages, unscoredImages);
        this.taxonId = taxonId;
        this.taxonName = taxonName;
        this.combinedScore = calculateCombinedScore();
        this.imageBrowser = new ImageBrowser(this, this.taxonName, mb);
        this.isRegime2TrainingTaxon = isRegime2TrainingTaxon;
	}
    public boolean isRegime2TrainingTaxon(){
    	return this.isRegime2TrainingTaxon;
    }
    public ImageBrowser getImageBrowser(){
    	return this.imageBrowser;
    }
    public String getTrueScore() throws MorphobankDataException {
    	Hashtable<String, Integer> countsForLabelHash = new Hashtable<String, Integer>();
    	List<String> humanLabelsTraining = getHumanLabelsFromResultImages(this.trainingImages);
    	List<String> humanLabelsScored = getHumanLabelsFromResultImages(this.scoredImages);
    	List<String> humanLabelsUnscored = getHumanLabelsFromResultImages(this.unscoredImages);
    	addToCountsHash(humanLabelsTraining, countsForLabelHash);
    	addToCountsHash(humanLabelsScored, countsForLabelHash);
    	addToCountsHash(humanLabelsUnscored, countsForLabelHash);
    	Enumeration<String> keysEnum = countsForLabelHash.keys();
    	String humanLabelWithMaxCount = "";
    	int maxCount = 0;
    	while (keysEnum.hasMoreElements()){
    		String key = keysEnum.nextElement();
    		int count = countsForLabelHash.get(key).intValue();;
    		if (count > maxCount){
    			maxCount = count;
    			humanLabelWithMaxCount = key;
    		}
    	}
    	return humanLabelWithMaxCount;
    }
    public void addToCountsHash(List<String> labels, Hashtable<String, Integer> hash){
    	for (String label : labels){
    		Integer currentTotalInteger = hash.get(label);
    		if (null == currentTotalInteger){
    			currentTotalInteger = new Integer(0);
    			hash.put(label, currentTotalInteger);
    		}
    		Integer newTotal = new Integer(currentTotalInteger.intValue() + 1);
    		hash.put(label, newTotal);
    	}
    }
    public List<String> getHumanLabelsFromResultImages(List<ResultImage> ris) throws MorphobankDataException {
    	List<String> result = new ArrayList<String>();
    	for (ResultImage ri : ris){
    		String label = ri.getHumanLabel();
    		if (null != label){
        		result.add(label);
    		}
    	}
    	return result;
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
    public String getBelievedStateOld(){
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
    	/*
    	 * 0 means high confident absent , 1 means high confident present
    	 * 0.4 means low conf absent   0.6 means low conf present
    	 * 
    	 * for absent ,( 0.5 - score ) * 2, flag if above 0.5
    	 * for present ( score - 0.5 ) * 2, flag if below 0.5
    	 */
    	double absentConf = 0.0;
    	double presentConf = 0.0;
    	String presentState = "";
    	String absentState = "";
    	for (String stateName : states){
    		List<ResultImage> ris = charStateMap.get(stateName);
    		double averageConfidence = getAverageConfidence(ris);
    		if (averageConfidence < 0.5){
    			absentConf = (0.5 - averageConfidence) * 2;
    			absentState = stateName;
    		}
    		else {
    			presentConf = (averageConfidence - 0.5 ) * 2;
    			presentState = stateName;
    		}
    	}
    	if (absentConf > presentConf){
    		return absentState;
    	}
    	else {
    		return presentState;
    	}
    }
    public double calculateCombinedScore(){
    	double maxConfidence = 0;
    	String maxConfidenceState = "";
    	Hashtable<String, List<ResultImage>> charStateMap = new Hashtable<String, List<ResultImage>>();
    	//if (null == this.scoredImages || this.scoredImages.size() == 0){
    	//	return "NA";
    	//}
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
    	/*
    	 * 0 means high confident absent , 1 means high confident present
    	 * 0.4 means low conf absent   0.6 means low conf present
    	 * 
    	 * for absent ,( 0.5 - score ) * 2, flag if above 0.5
    	 * for present ( score / 2, flag if below 0.5
    	 */
    	double absentConf = 0.0;
    	double presentConf = 0.0;
    	String presentState = "";
    	String absentState = "";
    	for (String stateName : states){
    		List<ResultImage> ris = charStateMap.get(stateName);
    		double averageConfidence = getAverageConfidence(ris);
    		System.out.println("state name: " + stateName + " average confidence initially : " + averageConfidence);
    		if (averageConfidence < 0.5){
    			absentConf = (0.5 - averageConfidence) * 2;
        		System.out.println("state name: " + stateName + " - absent confidence now : " + absentConf);
    			absentState = stateName;
    		}
    		else {
    			presentConf = (averageConfidence - 0.5 ) * 2;
    			presentState = stateName;
        		System.out.println("state name: " + stateName + " - present confidence now : " + presentConf);
    		}
    	}
    	if (absentConf > presentConf){
    		System.out.println("absent conf wins : " + absentConf);
    		return absentConf;
    	}
    	else {
    		System.out.println("present conf wins : " + presentConf);
    		return presentConf;
    	}
    }

    public double calculateCombinedScoreOld(){
    	String maxConfidenceState = getBelievedState();
    	if (this.scoredImages == null){
    		
            double score = getRandomInRange(0.5, 1.0);
            return score;
    	}
    	int count = 0;
    	double total = 0;
    	for (ResultImage ri : this.scoredImages){
    		if (ri.getCharacterStateName().equals(maxConfidenceState)){
    			String confidenceString = ri.getConfidence();
        		Double d = new Double(confidenceString);
        		double val = d.doubleValue();
        		total += val;
        		count += 1;
    		}
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
    @Override
	public String getTrainingTabTitle() {
		return this.taxonName + " - TRAINING images";
	}
	@Override
	public String getScoredTabTitle() {
		return this.taxonName + " - SCORED images";
	}
	@Override
	public String getUnscoredTabTitle() {
		return this.taxonName + " - UNSCORED images";
	}
}