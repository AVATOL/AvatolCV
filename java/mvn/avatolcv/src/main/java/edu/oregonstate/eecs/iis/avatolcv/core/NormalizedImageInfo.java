package edu.oregonstate.eecs.iis.avatolcv.core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import edu.oregonstate.eecs.iis.avatolcv.AvatolCVFileSystem;

public class NormalizedImageInfo {
    //avcv_annotation=point:21.2571225071225,55.3632478632479+point:21.84729344729345,40.810256410256414
    //character:1824350|Diastema between I2 and C=characterState:4884329|Diastema present
    //taxon=773126|Artibeus jamaicensis
    //view=8905|Skull - ventral annotated teeth
    Hashtable<String, Object> keyValueHash = new Hashtable<String, Object>();
    Hashtable<String, Object> scoreHash = new Hashtable<String, Object>();
    private static final String PREFIX = AvatolCVFileSystem.RESERVED_PREFIX;
    public static final String KEY_ANNOTATION         = PREFIX + "annotation";
    private static final String KEY_SCORING_CONFIDENCE = PREFIX + "scoringConfidence";
    public static final String KEY_IMAGE_NAME         = PREFIX + "imageName";
    public static final String KEY_TIMESTAMP          = PREFIX + "timestamp";
    private static final String KEY_TRAINING_VS_TEST_CONCERN_VALUE  = PREFIX + "trainingVsTestConcernValue";
    private ScoreIndex scoreIndex = null;
      //private ScoreIndex scoreIndexForBaseFile = new ScoreIndex();
    //private ScoreIndex scoreIndexForScoreFile = new ScoreIndex();
    private String imageName = "?";
    private String imageID = null;
    public NormalizedImageInfo(String path, ScoreIndex scoreIndex) throws AvatolCVException {
        this.imageID = getImageIDFromPath(path);
        this.scoreIndex = scoreIndex;
        loadNormalizedInfoFromPath(path, "Problem loading Normalized Image Info file: ", keyValueHash, scoreIndex);
    }
    
    public static String getImageIDFromPath(String path){
        File f = new File(path);
        String filename = f.getName();
        String[] parts = filename.split("\\.");
        String root = parts[0];
        String[] rootParts = root.split("_");
        String id = rootParts[0];
        return id;
    }
    public String getImageID(){
        return this.imageID;
    }
    private void loadNormalizedInfoFromPath(String path, String errorMessage, Hashtable<String, Object> hash, ScoreIndex scoreIndex)throws AvatolCVException {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(path));
            String line = null;
            while(null != (line = reader.readLine())){
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
                        if (key.contains(":")){
                            // skip for now
                        }
                        else {
                            if (value.contains("|")){
                                String[] valueParts = value.split("|");
                                String id = valueParts[0];
                                String name = valueParts[1];
                                ValueIDandName inv = new ValueIDandName(id, name);
                                hash.put(key, inv);
                            }
                            else {
                                hash.put(key,value);
                            }
                        }
                    }
                }
            }
            reader.close();
        }
        catch(IOException ioe){
            throw new AvatolCVException(errorMessage + path);
        }
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
            loadNormalizedInfoFromPath(scoreFile.getAbsolutePath(), "Problem loading score info file: ", scoreHash, scoreIndex);
            //if (!scoreIndexForScoreFile.equals(scoreIndexForBaseFile)){
            //    throw new AvatolCVException("The base file and the score file should have the same scoreIndex values: " + path);
            //}
        }
    }
    public boolean isScored(){
        return !scoreHash.isEmpty();
    }
    
    public String getScoringConfidence(){
        return (String)keyValueHash.get(KEY_SCORING_CONFIDENCE);
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
    public String getValue(Hashtable<String, Object> hash, ScoreIndex scoreIndex)  {
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
    public String getImageName(){
        return this.imageName;
    }
    public String getTrainingVsTestName(){
        return (String)keyValueHash.get(KEY_TRAINING_VS_TEST_CONCERN_VALUE);
    }
    private void loadAvatolCVKeyedLine(String line) throws AvatolCVException {
        String[] parts = line.split("=");
        String key = parts[0];
        String value = parts[1];
        if (key.equals(KEY_ANNOTATION)){
            loadAnnotationLine(key, value);
        }
        else if (key.equals(KEY_IMAGE_NAME)){
        	imageName = value;
        }
        else {
            //ignore;
        }
    }
    private void loadAnnotationLine(String key, String value){
      //avcv_annotation=point:21.2571225071225,55.3632478632479+point:21.84729344729345,40.810256410256414
        // ...from MorphobankDataSource.java
        // avcv_annotation=rectangle:25,45;35,87+point:98,92
        // + delimits the annotations in the series
        // ; delimits the points in the annotation
        // , delimits x and y coordinates
        // : delimits type from points
        String[] annotationValueParts = value.split("+");
        for (String annotation : annotationValueParts){
            String[] annotationParts = annotation.split(":");
            String annotationType = annotationParts[0];
            String annotationPointSequence = annotationParts[1];
            String[] annotationPointPairs = annotationPointSequence.split(";");
            for (String pair : annotationPointPairs){
                String[] pairParts = pair.split(",");
                String x = pairParts[0];
                String y = pairParts[1];
                // FIXME - finish this liogic
            }
        }
    }
    public class ValueIDandName{
        private String id = null;
        private String name = null;
        public ValueIDandName(String id, String name){
            this.id = id;
            this.name = name;
        }
        public String getID(){
            return this.id;
        }
        public String getName(){
            return this.name;
        }
    }
}
