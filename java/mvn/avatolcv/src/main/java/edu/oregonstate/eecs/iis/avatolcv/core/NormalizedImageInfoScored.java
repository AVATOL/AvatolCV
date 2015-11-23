package edu.oregonstate.eecs.iis.avatolcv.core;

import java.io.File;
import java.util.Hashtable;

import edu.oregonstate.eecs.iis.avatolcv.AvatolCVException;

public class NormalizedImageInfoScored extends NormalizedImageInfo {
    private static final String KEY_SCORING_CONFIDENCE = PREFIX + "scoringConfidence";
    protected Hashtable<String, Object> scoreHash = new Hashtable<String, Object>();

	ScoreIndex scoreIndex = null;
    //private ScoreIndex scoreIndexForBaseFile = new ScoreIndex();
	//private ScoreIndex scoreIndexForScoreFile = new ScoreIndex();
	public NormalizedImageInfoScored(String path, ScoreIndex scoreIndex) throws AvatolCVException {
		super(path);
		this.scoreIndex = scoreIndex;
	}

    public boolean isScored(){
        return !scoreHash.isEmpty();
    }
    
    public String getScoringConfidence(){
        return (String)keyValueHash.get(KEY_SCORING_CONFIDENCE);
    }
    
   
	public boolean hasScoringConcern(String scoringConcern, ScoreIndex scoreIndex){
    	// as reminder, this is what feeds the scoring index object
    	//avcv_scoringConcernLocation=leaf apex angle:key
    	//avcv_scoreValueLocation=leaf apex angle:value
    	String scoringConcernKey = scoreIndex.getkeyForScoringConcernName();
    	String keyOrValue = scoreIndex.isScoringConcernNameTheKeyOrValue();
    	if (null == keyOrValue){
    		return false;
    	}
    	if (keyOrValue.equals("key")){
    		// compare the key
    		if (scoringConcernKey.equals(scoringConcern)){
    			return true;
    		}
    		else{
    			return false;
    		}
    	}
    	else {
    		// compare value to given scoringConcern string
    		Object valueObject = scoreHash.get(scoringConcernKey);
    		String value = "?";
    		if (valueObject instanceof ValueIDandName){
        		ValueIDandName vin = (ValueIDandName)valueObject;
        		value = vin.getName();
        	}
    		else {
    			value = (String)valueObject;
    		}
    		if (value.equals(scoringConcern)){
    			return true;
    		}
    		else {
    			return false;
    		}
    	}
    	
    	
    }
	 /*
     * 
     */
    public void loadAVCVScoreFile(String path, ScoreIndex scoreIndex) throws AvatolCVException {
        File scoreFile = new File(path);
        if (scoreFile.exists()){
            loadNormalizedInfoFromPath(scoreFile.getAbsolutePath(), "Problem loading score info file: ", scoreHash);
            //if (!scoreIndexForScoreFile.equals(scoreIndexForBaseFile)){
            //    throw new AvatolCVException("The base file and the score file should have the same scoreIndex values: " + path);
            //}
        }
    }
    public String getScoredItemName(ScoreIndex scoreIndex) throws AvatolCVException {
        if (scoreHash.isEmpty()){
            throw new AvatolCVException("Tried to get score information from a non-scored item");
        }
        String key = scoreIndex.getkeyForScoringConcernName();
        Object valueObject = scoreHash.get(key);
        String keyOrValue = scoreIndex.isScoringConcernNameTheKeyOrValue();
        if (keyOrValue.equals("key")){
            return key;
        }
        if (valueObject instanceof ValueIDandName){
            ValueIDandName vid = (ValueIDandName)valueObject;
            return vid.getName();
        }
        String result = (String) valueObject;
        return result;
    }

    public String getScoreValue(ScoreIndex scoreIndex) throws AvatolCVException {
        return getValue(scoreHash, scoreIndex);
    }
    public static String getValue(Hashtable<String, Object> hash, ScoreIndex scoreIndex)  {
        if (hash.isEmpty()){
            return null;
        }
        String key = scoreIndex.getKeyForScoringConcernValue();
        Object valueObject = hash.get(key);
        String keyOrValue = scoreIndex.isScoringConcernValueTheKeyOrValue();
        if (keyOrValue.equals("key")){
            return key;
        }
        if (valueObject instanceof ValueIDandName){
            ValueIDandName vid = (ValueIDandName)valueObject;
            return vid.getName();
        }
        String result = (String) valueObject;
        return result;
    }
    public String getTruthValue(ScoreIndex scoreIndex) throws AvatolCVException {
        return getValue(keyValueHash, scoreIndex);
    }
}
