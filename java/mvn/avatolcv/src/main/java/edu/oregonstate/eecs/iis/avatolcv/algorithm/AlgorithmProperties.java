package edu.oregonstate.eecs.iis.avatolcv.algorithm;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Hashtable;

import edu.oregonstate.eecs.iis.avatolcv.AvatolCVException;

/**
 * 
 * @author admin-jed
 *
 *  Each algortihm in the modules are contains a file for each platform it is supported under, which tells 
 *  avatolCV about itself.  This way, avatolCV knows which dropdown menu to include it in 
 *  (the one for present/absence, shape or exture), and how to invoke it.
 */
public class AlgorithmProperties {
	public static final String PROPERTY_LAUNCH_FILE = "launchWith";  // can be a matlab function name, a script or executable.
	public static final String PROPERTY_SCORING_FOCUS = "scoringFocus";
	public static final String PROPERTY_SCORING_SCOPE = "scoringScope";

	//public static final String PROPERTY_LAUNCH_FILE_LANGUAGE = "launchFileLanguage";
	public static final String PROPERTY_LAUNCH_FILE_LANGUAGE_MATLAB = "matlab";
	public static final String PROPERTY_LAUNCH_FILE_LANGUAGE_OTHER = "other";
	public static final String PROPERTY_PARENT_DIR = "parentDir";
	public static final String PROPERTY_ALG_NAME = "algName";
	public static final String PROPERTY_ALG_TYPE = "algType";
	
	public static final String PROPERTY_ALG_TYPE_VALUE_SEGMENTATION = "segmentation";
	public static final String PROPERTY_ALG_TYPE_VALUE_ORIENTATION = "orientation";
	public static final String PROPERTY_ALG_TYPE_VALUE_SCORING = "scoring";
	
	
	private Hashtable<String, String> propsHash = new Hashtable<String,String>();
	private String path = null;
	public AlgorithmProperties(String path) throws AvatolCVException {
		this.path = path;
		File f = new File(path);
		if (!f.exists()){
			throw new AvatolCVException("AlgorithmProperties given nonexistent path : " + path);
		}
		String parentPath = f.getParent();
		propsHash.put(PROPERTY_PARENT_DIR, parentPath);
		try {
			BufferedReader reader = new BufferedReader(new FileReader(path));
			String line = null;
			while (null != (line = reader.readLine())){
				if (line.startsWith("#")){
					// ignore comments
				}
				else {
				    String[] propPair = line.split("=");
				    propsHash.put(propPair[0], propPair[1]);
				}
			}
			reader.close();
			// try to access the key properties to make sure they are present.
			String launchFile = getLaunchFile();
		}
		catch(IOException ioe){
			throw new AvatolCVException("Could not read AlgorithmProperties file " + path);
		}
	}
	public String getAlgType() throws AvatolCVException  {
		String value = propsHash.get(PROPERTY_ALG_TYPE);
		if (null == value){
			throw new AvatolCVException(PROPERTY_ALG_TYPE + " not set in Algorithmproperties for " + this.path);
		}
		return value;
	}
	public void setAlgName(String algName) throws AvatolCVException {
		String value = propsHash.get(PROPERTY_ALG_NAME);
		if (!(null == value)){
			throw new AvatolCVException(PROPERTY_ALG_NAME + " already set in Algorithmproperties for " + this.path);
		}
		propsHash.put(PROPERTY_ALG_NAME, algName);
	}
	public String getAlgName() throws AvatolCVException{
		String value = propsHash.get(PROPERTY_ALG_NAME);
		if (null == value){
			throw new AvatolCVException("no value for property " + PROPERTY_ALG_NAME + " in algorithm properties file " + this.path);
		}
		return value; 	}
	public String getParentDir(){
		return propsHash.get(PROPERTY_PARENT_DIR);
	}
	public String getProperty(String key) throws AvatolCVException {
		String value = propsHash.get(key);
		if (null == value){
			throw new AvatolCVException("no value for property " + key + " in algorithm properties file " + this.path);
		}
		return value;
	}
	
	public String getLaunchFile() throws AvatolCVException {
		String value = propsHash.get(PROPERTY_LAUNCH_FILE);
		if (null == value){
			throw new AvatolCVException("no value for property " + PROPERTY_LAUNCH_FILE + " in algorithm properties file " + this.path);
		}
		return value;
	}
}
