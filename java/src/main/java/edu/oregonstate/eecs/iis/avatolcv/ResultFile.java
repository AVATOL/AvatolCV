package edu.oregonstate.eecs.iis.avatolcv;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ResultFile extends DataIOFile {
	private List<TrainingSample> trainingSamples = new ArrayList<TrainingSample>();
	private List<ScoredImage> scoredImages = new ArrayList<ScoredImage>();
	private List<UnscoredImage> unscoredImages = new ArrayList<UnscoredImage>();
	
    public ResultFile(String path, String rootDir){
    	super(path);
    	try {
        	BufferedReader reader = new BufferedReader(new FileReader(path));
    		String line = null;
    		while (null != (line = reader.readLine())){
    			if (line.startsWith(TRAINING_DATA_MARKER)){
    				TrainingSample ts = new TrainingSample(line, rootDir, this.charId, this.charName); 
    				trainingSamples.add(ts);
    			}
    			else if (line.startsWith(IMAGE_SCORED_MARKER)){
    				ScoredImage isl = new ScoredImage(line, rootDir);
    				scoredImages.add(isl);
    			}
    			else if (line.startsWith(IMAGE_NOT_SCORED_MARKER)){
    				UnscoredImage isl = new UnscoredImage(line, rootDir);
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
    		System.out.println(ioe.getMessage());
    	}
    }

    public List<ScoredImage> getScoredImages(){
    	List<ScoredImage> result = new ArrayList<ScoredImage>();
    	result.addAll(this.scoredImages);
    	return result;
    }
    public List<UnscoredImage> getUnscoredImages(){
    	List<UnscoredImage> result = new ArrayList<UnscoredImage>();
    	result.addAll(this.unscoredImages);
    	return result;
    }
    public List<TrainingSample> getTrainingSamples(){
    	List<TrainingSample> result = new ArrayList<TrainingSample>();
    	result.addAll(this.trainingSamples);
    	return result;
    }
}
