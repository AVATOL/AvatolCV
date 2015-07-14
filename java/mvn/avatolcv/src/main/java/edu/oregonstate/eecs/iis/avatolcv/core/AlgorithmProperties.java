package edu.oregonstate.eecs.iis.avatolcv.core;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Hashtable;

public class AlgorithmProperties {
	public static final String PROPERTY_LAUNCH_FILE = "launchFile";  // can be a matlab file, a script or executable. If it ends with .m, we'll handle it through our matlab invoker
	public static final String PROPERTY_SCORING_FOCUS = "scoringFocus";
	private Hashtable<String, String> propsHash = new Hashtable<String,String>();
	private String path = null;
	public AlgorithmProperties(String path) throws AvatolCVException {
		this.path = path;
		try {
			BufferedReader reader = new BufferedReader(new FileReader(path));
			String line = null;
			while (null != (line = reader.readLine())){
				if (line.startsWith("#")){
					// ignore comments
				}
				String[] propPair = line.split("=");
				propsHash.put(propPair[0], propPair[1]);
			}
			reader.close();
			// try to access the launchFile to make sure it's there correctly.
			String launchFile = getLaunchFile();
		}
		catch(IOException ioe){
			throw new AvatolCVException("Could not read AlgorithmProperties file " + path);
		}
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
