package edu.oregonstate.eecs.iis.avatolcv;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class OutputFile extends DataIOFile {
	private List<TrainingSample> trainingSamples = new ArrayList<TrainingSample>();
	private List<ScoredImage> scoredImages = new ArrayList<ScoredImage>();
	private List<UnscoredImage> unscoredImages = new ArrayList<UnscoredImage>();
	private String charId;
	private String charName;
	
    public OutputFile(String path, String rootDir) throws AvatolCVException {
    	super(path);
    	this.charId = getCharIdFromPath(path);
    	this.charName = getCharNameFromPath(path);
    	try {
        	BufferedReader reader = new BufferedReader(new FileReader(path));
    		String line = null;
    		while (null != (line = reader.readLine())){
    			if (line.startsWith(TRAINING_DATA_MARKER)){
    				//System.out.println("loading TRAINING line " + line);
    				TrainingSample ts = new TrainingSample(line, rootDir, this.charId, this.charName); 
    				trainingSamples.add(ts);
    			}
    			else if (line.startsWith(IMAGE_SCORED_MARKER)){
    				//System.out.println("loading SCORED line " + line);
    				ScoredImage isl = new ScoredImage(line, rootDir, this.charId, this.charName);
    				scoredImages.add(isl);
    			}
    			else if (line.startsWith(IMAGE_NOT_SCORED_MARKER)){
    				//System.out.println("loading NOT_SCORED line " + line);
    				UnscoredImage isl = new UnscoredImage(line, rootDir, this.charId, this.charName);
    				unscoredImages.add(isl);
    			}
    			else {
    				//ignore it
    			}
    		}
        	
        	reader.close();
    	}
    	catch(IOException ioe){
    		ioe.printStackTrace();
    		throw new AvatolCVException(ioe.getMessage());
    	}
    }
    public String getCharName(){
    	return this.charName;
    }
    public String getCharId(){
    	return this.charId;
    }
    
    public List<ResultImage> getScoredImages(){
    	List<ResultImage> result = new ArrayList<ResultImage>();
    	result.addAll(this.scoredImages);
    	return result;
    }
    public List<ResultImage> getUnscoredImages(){
    	List<ResultImage> result = new ArrayList<ResultImage>();
    	result.addAll(this.unscoredImages);
    	return result;
    }
    public List<ResultImage> getTrainingSamples(){
    	List<ResultImage> result = new ArrayList<ResultImage>();
    	result.addAll(this.trainingSamples);
    	return result;
    }
}
