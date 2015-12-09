package edu.oregonstate.eecs.iis.avatolcv.core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import edu.oregonstate.eecs.iis.avatolcv.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.AvatolCVFileSystem;

public class NormalizedImageInfoScored extends NormalizedImageInfo  {
    private static final String KEY_SCORING_CONFIDENCE = PREFIX + "scoringConfidence";
    protected Hashtable<String, String> scoreHash = new Hashtable<String, String>();

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
    
    public String getScoringConfidence() throws AvatolCVException {
        NormalizedValue nv = keyValueHash.get(new NormalizedKey(KEY_SCORING_CONFIDENCE));
        return nv.getName();
    }
    
   
	public boolean hasScoringConcern(String scoringConcern, ScoreIndex scoreIndex) throws AvatolCVException {
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
    	    NormalizedTypeIDName tin = new NormalizedTypeIDName(scoringConcernKey);
    		if (tin.getName().equals(scoringConcern)){
    			return true;
    		}
    		else{
    			return false;
    		}
    	}
    	else {
    		// compare value to given scoringConcern string
    		String value = scoreHash.get(scoringConcernKey);
    		NormalizedTypeIDName tin = new NormalizedTypeIDName(value);
    		String valueName = tin.getName();
    	
    		if (valueName.equals(scoringConcern)){
    			return true;
    		}
    		else {
    			return false;
    		}
    	}
    	
    	
    }
	// copied this temporarily until figure out how whether need normalizedKey and Value in results
	private void loadAvatolCVKeyedLine(String line) throws AvatolCVException {
        String[] parts = line.split("=");
        String key = parts[0];
        String value = "";
        if (parts.length > 1){
            value = parts[1];
        }
        if (key.equals(KEY_IMAGE_NAME)){
        	imageName = value;
        	keyValueHash.put(new NormalizedKey(key),new NormalizedValue(value));
        }
        else {
        	keyValueHash.put(new NormalizedKey(key),new NormalizedValue(value));
        }
    }
	protected void loadNormalizedInfoFromLines(List<String> lines, String errorMessage, Hashtable<String, String> hash, boolean dummyVal) throws AvatolCVException {
    	setNiiStringFromLines(lines);
    	for (String line : lines){
    		if (line.startsWith("#")){
                // ignore
            }
            else {
                if (line.startsWith(AvatolCVFileSystem.RESERVED_PREFIX)){
                    loadAvatolCVKeyedLine(line);
                }
                else {
                    String[] parts = line.split("=");
                    String key = parts[0];
                    String value = "";
                    if (parts.length > 1){
                        value = parts[1];
                    }
                    hash.put(key,value);
                }
            }
    	}
    }
	protected void loadNormalizedInfoFromPath(String path, String errorMessage, Hashtable<String, String> hash, boolean value)throws AvatolCVException {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(path));
            List<String> lines = new ArrayList<String>();
            String line = null;
            while(null != (line = reader.readLine())){
                lines.add(line);
            }
            reader.close();
            loadNormalizedInfoFromLines(lines, errorMessage, hash, false);
        }
        catch(IOException ioe){
            throw new AvatolCVException(errorMessage + path + " : " + ioe.getMessage());
        }
    }
    // to support tes
	 /*
     * 
     */
    public void loadAVCVScoreFile(String path, ScoreIndex scoreIndex) throws AvatolCVException {
        File scoreFile = new File(path);
        if (scoreFile.exists()){
            loadNormalizedInfoFromPath(scoreFile.getAbsolutePath(), "Problem loading score info file: ", scoreHash, false);
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
        return getValue(scoreHash, scoreIndex, false);
    }
    public static String getValue(Hashtable<String, String> hash, ScoreIndex scoreIndex, boolean dummyVal)throws AvatolCVException  {
        if (hash.isEmpty()){
            return null;
        }
        String key = scoreIndex.getKeyForScoringConcernValue();
        String value = hash.get(key);
        String keyOrValue = scoreIndex.isScoringConcernValueTheKeyOrValue();
        if (keyOrValue.equals("key")){
            NormalizedTypeIDName tin = new NormalizedTypeIDName(key);
            return tin.getName();
        }
        NormalizedTypeIDName tin = new NormalizedTypeIDName(value);
        String result = tin.getName();
        return result;
    }
    public String getTruthValue(ScoreIndex scoreIndex) throws AvatolCVException {
    	return "";
        // punt on this until revisit results review...... return getValue(keyValueHash, scoreIndex);
    }
}
