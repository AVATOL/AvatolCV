package edu.oregonstate.eecs.iis.avatolcv;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class InputFile {
	private List<TrainingSample> trainingSamples = new ArrayList<TrainingSample>();
	private List<ToScoreLine> toScoreLines = new ArrayList<ToScoreLine>();
	private String charId;
	private String charName;
    public InputFile(String path, String rootDir){
    	this.charId = getCharIdFromPath(path);
    	this.charName = getCharNameFromPath(path);
    	try {
        	BufferedReader reader = new BufferedReader(new FileReader(path));
    		String line = null;
    		while (null != (line = reader.readLine())){
    			if (line.startsWith(InputFiles.TRAINING_DATA_MARKER)){
    				TrainingSample ts = new TrainingSample(line, rootDir, this.charId, this.charName); 
    				trainingSamples.add(ts);
    			}
    			else if (line.startsWith(InputFiles.IMAGE_TO_SCORE_MARKER)){
    				ToScoreLine tsl = new ToScoreLine(line, rootDir);
    				toScoreLines.add(tsl);
    			}
    			else {
    				//ignore it
    			}
    		}
        	
        	reader.close();
    	}
    	catch(IOException ioe){
    		ioe.printStackTrace();
    		System.out.println(ioe.getMessage());
    	}
    }
    public String getCharId(){
    	return this.charId;
    }
    public String getCharIdFromPath(String path){
    	File f = new File(path);
    	String filename = f.getName();
    	String[] filenameParts = filename.split("\\.");
    	String filenameRoot = filenameParts[0];
    	String filenameRootSansPrefix = filenameRoot.replaceAll(InputFiles.SORTED_INPUT_DATA_PREFIX, "");
    	String[] filenameRootParts = filenameRootSansPrefix.split("_");
    	String charId = filenameRootParts[0];
    	return charId;
    }
    public String getCharNameFromPath(String path){
    	File f = new File(path);
    	String filename = f.getName();
    	String[] filenameParts = filename.split("\\.");
    	String filenameRoot = filenameParts[0];
    	String filenameRootSansPrefix = filenameRoot.replaceAll(InputFiles.SORTED_INPUT_DATA_PREFIX, "");
    	String[] filenameRootParts = filenameRootSansPrefix.split("_");
    	String charName = filenameRootParts[1];
    	return charName;
    }
    public List<TrainingSample> getTrainingSamples(){
    	List<TrainingSample> result = new ArrayList<TrainingSample>();
    	result.addAll(this.trainingSamples);
    	return result;
    }
}
