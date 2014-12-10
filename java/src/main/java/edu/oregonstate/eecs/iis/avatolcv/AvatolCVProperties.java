package edu.oregonstate.eecs.iis.avatolcv;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;


public class AvatolCVProperties {
	public static final String NL = System.getProperty("line.separator");
	public static final String FILESEP = System.getProperty("file.separator");
	public static final String METADATA_FLAG = "SYSTEM_PROPERTY";
	public static final String TRAINING_SPLIT_THRESHOLD_KEY = "training_data_split_threshold";
	public static final String MATRIX_ROW_TYPE_KEY = "matrix_row_type";
	public static final String MATRIX_ROW_TYPE_SPECIMEN = "specimen";
	public static final String MATRIX_ROW_TYPE_TAXON = "taxon";
	private double trainingDataSplitThreshold = -1.0;
	private String row_type = "unknown";
    public AvatolCVProperties(String bundleDir) throws AvatolCVException {
    	String path = bundleDir + FILESEP + "avatolcv_properties.txt";
    	try {
        	BufferedReader reader = new BufferedReader(new FileReader(path));
        	String line = null;
    		System.out.println("loading properties file:");
        	while (null != (line = reader.readLine())){
        		System.out.println(line);
        		if (line.startsWith(TRAINING_SPLIT_THRESHOLD_KEY)){
        			String[] parts = line.split("=");
        			Double threshold = new Double(parts[1]);
        			this.trainingDataSplitThreshold = threshold.doubleValue();
        		}
        		else if (line.startsWith(MATRIX_ROW_TYPE_KEY)){
        			String[] parts = line.split("=");
        			this.row_type = parts[1];
        		}
        	}
        	reader.close();
    	}
    	catch(FileNotFoundException fnfe){
    		throw new AvatolCVException("could not find properties file " + path + usage());
    	}
    	catch(IOException ioe){
    		throw new AvatolCVException("problem reading properties file " + path + usage());
    	}
    }
    public String usage(){
    	String usage = NL + NL + "file should be of form:"+ NL +
    			"training_data_split_threshold=0.7" + NL +
    			"matrix_row_type=specimen   or    taxon" + NL;
    	return usage;

    }
    public double getTrainingDataSplitThreshold(){
    	return this.trainingDataSplitThreshold;
    }
    public boolean isSpecimenPerRowBundle(){
    	if (this.row_type.equals(MATRIX_ROW_TYPE_SPECIMEN)){
    		return true;
    	}
    	return false;
    }
    public boolean isPartitioningNeeded(){
    	if (this.trainingDataSplitThreshold == -1.0){
    		return false;
    	}
    	return true;
    }
    public String getMetadataLines(){
    	return METADATA_FLAG + ":" + TRAINING_SPLIT_THRESHOLD_KEY + "=" + this.trainingDataSplitThreshold + NL +
    		   METADATA_FLAG + ":" + MATRIX_ROW_TYPE_KEY + "=" + this.row_type + NL;
    }
}
