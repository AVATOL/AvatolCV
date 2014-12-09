package edu.oregonstate.eecs.iis.avatolcv;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;


public class AvatolCVProperties {
	public static final String NL = System.getProperty("line.separator");
	public static final String FILESEP = System.getProperty("file.separator");
	public static final String METADATA_FLAG = "SYSTEM_PROPERTY";
	public static final String TRAINING_SPLIT_THRESHOLD = "trainingSplitThreshold";
	private double trainingSplitThreshold = -1.0;
    public AvatolCVProperties(String bundleDir) throws AvatolCVException {
    	String path = bundleDir + FILESEP + "avatolcv_properties.txt";
    	try {
        	BufferedReader reader = new BufferedReader(new FileReader(path));
        	String line = null;
        	while (null != (line = reader.readLine())){
        		if (line.startsWith("annotation_split_threshold")){
        			String[] parts = line.split("=");
        			Double threshold = new Double(parts[1]);
        			trainingSplitThreshold = threshold.doubleValue();
        		}
        	}
        	reader.close();
    	}
    	catch(FileNotFoundException fnfe){
    		throw new AvatolCVException("could not find properties file " + path);
    	}
    	catch(IOException ioe){
    		throw new AvatolCVException("problem reading properties file " + path);
    	}
    }
    public double getTrainingSplitThreshold(){
    	return this.trainingSplitThreshold;
    }
    public String getMetadataLines(){
    	return METADATA_FLAG + ":" + TRAINING_SPLIT_THRESHOLD + "=" + this.trainingSplitThreshold + NL;
    }
}
