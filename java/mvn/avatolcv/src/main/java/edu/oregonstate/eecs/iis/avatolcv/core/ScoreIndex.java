package edu.oregonstate.eecs.iis.avatolcv.core;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import edu.oregonstate.eecs.iis.avatolcv.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.AvatolCVFileSystem;

/**
 * 
 * @author admin-jed
 *
 *  ScoreIndex is used to allow for the idea that scoring concern name and value might be contained inside an 
 *  expression like type:id|name (i.e. the NormalizedTyopeIDName class).  It is the name field that is of interest, but in various contexts 
 *  it might be the name in the key or the name in the value that is either the Scoring concern or the value.
 *  
 */
public class ScoreIndex {
	private static final String PREFIX = AvatolCVFileSystem.RESERVED_PREFIX;
	public static final String KEY_SCORING_CONCERN_LOCATION = PREFIX + "scoringConcernLocation";
	public static final String KEY_SCORING_VALUE_LOCATION = PREFIX + "scoreValueLocation";
	 
	private String lineWithScoringConcernHasKey = null;
    private String scoringConcernNameIsKeyOrValue = null;
    private String lineWithScoringConcernValueHasKey = null;
    private String scoringConcernValueIsKeyOrValue = null;
    
    public ScoreIndex(String path) throws AvatolCVException {
    	try {
    		BufferedReader reader = new BufferedReader(new FileReader(path));
    		String line = null;
    		while(null != (line = reader.readLine())){
    			if (line.startsWith(KEY_SCORING_CONCERN_LOCATION)){
                    setScoringConcernLocationInfo(line);
                }
                else if (line.startsWith(KEY_SCORING_VALUE_LOCATION)){
                    setScoringConcernValueInfo(line);
                }
    		}
    		reader.close();
    	}
    	catch(IOException ioe){
    		throw new AvatolCVException("Problem trying to load scoreIndex file from path:" + path);
    	}
    }
    public void setScoringConcernLocationInfo(String line) throws AvatolCVException {
        String[] parts = line.split("=");
        if (parts.length != 2){
            throw new AvatolCVException("malformed scoringConcernLocation line " + line);
        }
        String value = parts[1];
        String[] scoringConcernLocationParts = value.split(":");
        if (scoringConcernLocationParts.length != 2){
            throw new AvatolCVException("malformed scoringConcernLocation info " + value + " should be <name>:<key|value>");
        }
        lineWithScoringConcernHasKey = scoringConcernLocationParts[0];
        scoringConcernNameIsKeyOrValue = scoringConcernLocationParts[1];
        int foo = 3;
        int bar = foo;
    }
    public void setScoringConcernValueInfo(String line)  throws AvatolCVException {
        String[] parts = line.split("=");
        if (parts.length != 2){
            throw new AvatolCVException("malformed scoringConcernValue line " + line);
        }
        String value = parts[1];
        String[] scoringConcernValueParts = value.split(":");
        if (scoringConcernValueParts.length != 2){
            throw new AvatolCVException("malformed scoringConcernValue info " + value + " should be <name>:<key|value>");
        }
        lineWithScoringConcernValueHasKey = scoringConcernValueParts[0];
        scoringConcernValueIsKeyOrValue = scoringConcernValueParts[1]; 
    }
    
    public String getkeyForScoringConcernName(){
        return this.lineWithScoringConcernHasKey;
    }
    public String isScoringConcernNameTheKeyOrValue(){
        return this.scoringConcernNameIsKeyOrValue;
    }
    public String getKeyForScoringConcernValue(){
        return this.lineWithScoringConcernValueHasKey;
    }
    public String isScoringConcernValueTheKeyOrValue(){
        return this.scoringConcernValueIsKeyOrValue;
    }
    public boolean equals(ScoreIndex other){
        if (!other.getkeyForScoringConcernName().equals(this.lineWithScoringConcernHasKey)){
            return false;
        }
        if (!other.isScoringConcernNameTheKeyOrValue().equals(this.scoringConcernNameIsKeyOrValue)){
            return false;
        }
        if (!other.getKeyForScoringConcernValue().equals(this.lineWithScoringConcernValueHasKey)){
            return false;
        }
        if (!other.isScoringConcernValueTheKeyOrValue().equals(this.scoringConcernValueIsKeyOrValue)){
            return false;
        }
        return true;
    }
}
