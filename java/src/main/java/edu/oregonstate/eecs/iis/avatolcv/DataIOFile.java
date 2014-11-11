package edu.oregonstate.eecs.iis.avatolcv;

import java.io.File;

public class DataIOFile {
	public static final String SORTED_INPUT_DATA_PREFIX = "sorted_input_data_";
	public static final String SORTED_OUTPUT_DATA_PREFIX = "sorted_output_data_";
	public static final String SORTED_DETECTION_RESULTS_DATA_PREFIX = "sorted_detection_results_data_";

    public static final String INPUT_DIRNAME = "input";
    public static final String OUTPUT_DIRNAME = "output";
    public static final String DETECTION_RESULTS_DIRNAME = "detection_results";
    public static final String IMAGE_TO_SCORE_MARKER = "image_to_score";
    public static final String IMAGE_SCORED_MARKER = "image_scored";
    public static final String IMAGE_NOT_SCORED_MARKER = "image_not_scored";
    public static final String TRAINING_DATA_MARKER = "training_data";
    
	protected String charId;
	protected String charName;
	
	public DataIOFile(String path){
		this.charId = getCharIdFromPath(path);
    	this.charName = getCharNameFromPath(path);
	}
	public String getCharIdFromPath(String path){
    	File f = new File(path);
    	String filename = f.getName();
    	String[] filenameParts = filename.split("\\.");
    	String filenameRoot = filenameParts[0];
    	String filenameRootSansPrefix = removeAnyFilenamePrefix(filenameRoot);
    	String[] filenameRootParts = filenameRootSansPrefix.split("_");
    	String charId = filenameRootParts[0];
    	return charId;
    }
    public String getCharNameFromPath(String path){
    	File f = new File(path);
    	String filename = f.getName();
    	String[] filenameParts = filename.split("\\.");
    	String filenameRoot = filenameParts[0];
    	String filenameRootSansPrefix = removeAnyFilenamePrefix(filenameRoot);
    	String[] filenameRootParts = filenameRootSansPrefix.split("_");
    	String charName = filenameRootParts[1];
    	return charName;
    }
    public String removeAnyFilenamePrefix(String s){
    	String result = s.replaceAll(SORTED_INPUT_DATA_PREFIX, "");
    	result = result.replaceAll(SORTED_OUTPUT_DATA_PREFIX, "");
    	result = result.replaceAll(SORTED_DETECTION_RESULTS_DATA_PREFIX, "");
    	return result;
    }

    public String getCharId(){
    	return this.charId;
    }
}
