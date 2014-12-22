package edu.oregonstate.eecs.iis.avatolcv.algata;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class InputFile extends DataIOFile {
	private List<TrainingSample> trainingSamples = new ArrayList<TrainingSample>();
	private List<ToScoreLine> toScoreLines = new ArrayList<ToScoreLine>();
	private String charId;
	private String charName;
    public InputFile(String path, String rootDir){
    	super(path);
    	this.charId = getCharIdFromPath(path);
    	this.charName = getCharNameFromPath(path);
    	try {
        	BufferedReader reader = new BufferedReader(new FileReader(path));
    		String line = null;
    		while (null != (line = reader.readLine())){
    			if (line.startsWith(DataIOFile.TRAINING_DATA_MARKER)){
    				TrainingSample ts = new TrainingSample(line, rootDir, this.charId, this.charName); 
    				trainingSamples.add(ts);
    			}
    			else if (line.startsWith(DataIOFile.IMAGE_TO_SCORE_MARKER)){
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
    
    public List<ResultImage> getTrainingSamples(){
    	List<ResultImage> result = new ArrayList<ResultImage>();
    	result.addAll(this.trainingSamples);
    	return result;
    }
}
