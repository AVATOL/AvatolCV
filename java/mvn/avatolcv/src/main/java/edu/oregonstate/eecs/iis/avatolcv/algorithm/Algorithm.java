package edu.oregonstate.eecs.iis.avatolcv.algorithm;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import edu.oregonstate.eecs.iis.avatolcv.AvatolCVException;

/**
 * 
 * @author admin-jed
 *
 *  Each algortihm in the modules are contains a file for each platform it is supported under, which tells 
 *  avatolCV about itself.  This way, avatolCV knows which dropdown menu to include it in 
 *  (the one for present/absence, shape or exture), and how to invoke it.
 */
public class Algorithm {
	public static final String PROPERTY_LAUNCH_FILE = "launchWith";  // can be a matlab function name, a script or executable.

	public static final String PROPERTY_PARENT_DIR = "parentDir";
	public static final String PROPERTY_ALG_NAME = "algName";
	public static final String PROPERTY_ALG_TYPE = "algType";
	
	public static final String PROPERTY_ALG_TYPE_VALUE_SEGMENTATION = "segmentation";
	public static final String PROPERTY_ALG_TYPE_VALUE_ORIENTATION = "orientation";
	public static final String PROPERTY_ALG_TYPE_VALUE_SCORING = "scoring";
	
	public static final String PROPERTY_ALG_DESCRIPTION = "description";
	
    public static final String DECLARATION_DEPENDENCY = "dependency:";
    public static final String DECLARATION_INPUT_REQUIRED = "inputRequired:";
    public static final String DECLARATION_INPUT_OPTIONAL = "inputOptional:";
    public static final String DECLARATION_OUTPUT_GENERATED = "outputGenerated:";
	
	
	private List<AlgorithmDependency> dependencies = new ArrayList<AlgorithmDependency>();
	protected Hashtable<String, String> propsHash = new Hashtable<String,String>();
	protected List<String> algPropsEntriesNotYetConsumed = new ArrayList<String>();
	private String path = null;
	public Algorithm(List<String> lines, String path) throws AvatolCVException {
	    this.path = path;
	    File f = new File(path);
        if (!f.exists()){
            throw new AvatolCVException("AlgorithmProperties file does not exist : " + path);
        }
        String parentPath = f.getParent();
        propsHash.put(PROPERTY_PARENT_DIR, parentPath);
        for (String line : lines){
            if (line.startsWith("#") || line.equals("")){
                // ignore comments
            }
            else if (line.startsWith(DECLARATION_DEPENDENCY)){
                AlgorithmDependency ad = new AlgorithmDependency(line);
                this.dependencies.add(ad);
            } 
            else if (line.startsWith(DECLARATION_INPUT_REQUIRED)){
                
            }
            else if (line.startsWith(DECLARATION_INPUT_OPTIONAL)){
                
            }
            else if (line.startsWith(DECLARATION_OUTPUT_GENERATED)){
     
            }
            else if (line.startsWith(PROPERTY_LAUNCH_FILE)){
                acceptProperty(line);
            }
            else if (line.startsWith(PROPERTY_ALG_NAME)){
                acceptProperty(line);
            }
            else if (line.startsWith(PROPERTY_ALG_TYPE)){
                acceptProperty(line);
            }
            else if (line.startsWith(PROPERTY_ALG_DESCRIPTION)){
                acceptProperty(line);
            }
            else {
                algPropsEntriesNotYetConsumed.add(line);
            }
        }
        // try to access the key properties to make sure they are present.
        String launchFile = getLaunchFile();
        String algName = getAlgName();
        String algDescription = getAlgDescription();
        String algType = getAlgType();
	}
	private void acceptProperty(String line) throws AvatolCVException {
	    if (!line.contains("=")){
	        expressPropertyDeclarationError(line);
	    }
	    String[] propPair = line.split("=");
	    if (propPair.length < 2){
	        expressPropertyDeclarationError(line);
	    }
        propsHash.put(propPair[0], propPair[1]);
	}
	private void expressPropertyDeclarationError(String line) throws AvatolCVException{
	    throw new AvatolCVException("Algorithm property must be of the form key=value : " + line);
	}
	/*
	 * 

inputRequired:testImagesMaskFile refsFilesWithSuffix _croppedMask ofType mask_SpecimenGreen_BackgroundBlue_ClutterRed
inputRequired:testImagesFile refsFilesWithSuffix _croppedOrig ofType isolatedSpecimenImage

outputGenerated:ofType mask_SpecimenGreen_BackgroundBlue_ClutterRed withSuffix _orientedMask
outputGenerated:ofType isolatedSpecimenImage withSuffix _orientedOrig

	 */

	public String getAlgDescription() throws AvatolCVException {
	    return getProperty(PROPERTY_ALG_DESCRIPTION);
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
