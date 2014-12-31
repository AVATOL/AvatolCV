package edu.oregonstate.eecs.iis.avatolcv;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import edu.oregonstate.eecs.iis.avatolcv.algata.InputFile;
import edu.oregonstate.eecs.iis.avatolcv.algata.OutputFile;
import edu.oregonstate.eecs.iis.avatolcv.algata.ResultImage;
import edu.oregonstate.eecs.iis.avatolcv.mb.MorphobankBundle;

public class ScoredSetMetadatas {
	
	private Hashtable<String,String> allData = new Hashtable<String,String>();
	private List<String> keyList = new ArrayList<String>();
	private int currentKeyIndex = 0;
	private String metadataDir = null;
	private String SEP = System.getProperty("file.separator");
	private String NL = System.getProperty("line.separator");
	private Hashtable<String,ScoredSetMetadata> ssmHash = new Hashtable<String,ScoredSetMetadata>();
	public ScoredSetMetadatas(String rootDir){
		this.metadataDir = rootDir + SEP + "scoredSetMetadata";
		File f = new File(this.metadataDir);
		f.mkdirs();
	}
	public int getSetCount(){
		return keyList.size();
	}
	public void goToNextSession(){
		if (this.currentKeyIndex < this.keyList.size() - 1){
			this.currentKeyIndex += 1;
		}
	}
	public void goToPrevSession(){
		if (this.currentKeyIndex > 0){
			this.currentKeyIndex -= 1;
		}
	}
	public boolean backButtonNeeded(){
		return (this.currentKeyIndex > 0);
	}
	public boolean nextButtonNeeded(){
		return (this.currentKeyIndex < this.keyList.size() - 1);
	}
	public String getPositionInList(){
		String runPositionInList = (this.currentKeyIndex + 1) + "/" + this.keyList.size();
		return runPositionInList;
	}
	public void persistForDPM(String matrixName, String characterName, String charId, String viewName, List<String> charactersTrained,
			String input_folder, String output_folder, String detection_results_folder, AvatolCVProperties properties) throws AvatolCVException {
    	String path = getPath(matrixName,"DPM");
    	try {
    		BufferedWriter writer = new BufferedWriter(new FileWriter(path));
    		writer.write("matrix=" + matrixName + NL);
    		writer.write("character=" + characterName + NL);
    		writer.write("view=" + viewName + NL);
    		writer.write("algorithm=DPM" + NL);
    		writer.write(NL);
    		for (String trainedChar : charactersTrained){
    			writer.write("character_trained=" + trainedChar + NL);
    		}
    		writer.write(ScoredSetMetadata.INPUT_FOLDER_KEY + "=" + input_folder + NL);
    		writer.write(ScoredSetMetadata.OUTPUT_FOLDER_KEY + "=" + output_folder + NL);
    		writer.write(ScoredSetMetadata.DETECTION_RESULTS_FOLDER_KEY + "=" + detection_results_folder + NL);
    		writer.write(ScoredSetMetadata.FOCUS_CHARID_KEY + "=" + charId + NL);
    		writer.write(properties.getMetadataLines());
    		writer.close();
    	}
    	catch(IOException ioe){
    		ioe.printStackTrace();
    		throw new AvatolCVException(ioe.getMessage());
    	}
    	
    }
    public void persistForCRF(String matrixName, String character) throws AvatolCVException {
    	String path = getPath(matrixName,"CRF");
    	try {
    		BufferedWriter writer = new BufferedWriter(new FileWriter(path));
    		writer.write("matrix      : " + matrixName + NL);
    		writer.write("character   : " + character + NL);
    		writer.write("algorithm   : CRF" + NL);
    		writer.close();
    	}
    	catch(IOException ioe){
    		ioe.printStackTrace();
    		throw new AvatolCVException(ioe.getMessage());
    	}
    	
    }
    public String getPath(String matrixName, String alg){
    	String filename = (System.currentTimeMillis() / 1000L) + "_" + alg + "_" + matrixName +".txt";
    	String path = this.metadataDir + SEP + filename;
    	return path;
    }
    public String getMatrixNameFromKey(String key){
    	String[] keyParts = key.split("_");
		String matrixName = keyParts[2];
		return matrixName;
    }

    public String getAlgNameFromKey(String key){
    	String[] keyParts = key.split("_");
		String algName = keyParts[1];
		return algName;
    }
    public boolean hasSessionData(){
    	File f = new File(this.metadataDir);
    	File[] files = f.listFiles();
    	for (File file : files){
        	System.out.println("FILE : " + file.getAbsolutePath());
    		if (file.getName().endsWith(".txt")){
    			return true;
    		}
    	}
    	return false;
    }
    public void loadAll() throws AvatolCVException {
    	File f = new File(this.metadataDir);
    	File[] files = f.listFiles();
    	for (File mdFile : files){
    		System.out.println("session name " + mdFile.getName());
    		if (mdFile.getName().endsWith(".txt")){
    			String[] parts =  mdFile.getName().split("\\.");
        		String fileRoot = parts[0];
        		String path = mdFile.getAbsolutePath();
        		StringBuilder sb = new StringBuilder();
        		try {
        			BufferedReader reader = new BufferedReader(new FileReader(path));
        			String line = null;
        			while (null != (line = reader.readLine())){
        				sb.append(line + NL);
        			}
        			reader.close();
        			this.ssmHash.put(fileRoot, new ScoredSetMetadata(fileRoot,sb.toString()));
        			this.allData.put(fileRoot, sb.toString());
        		}
        		catch(IOException ioe){
        			ioe.printStackTrace();
        			throw new AvatolCVException(ioe.getMessage());
        		}
    		}
    	}
    	this.keyList = getKeys();
    	selectMostRecentRun(); 
    }
    public void selectMostRecentRun(){
    	if (this.keyList.size() > 0){
    	    this.currentKeyIndex = this.keyList.size() - 1;
    	}
    	else {
    		this.currentKeyIndex = -1;
    	}
    }
    public List<String> getKeys(){
    	List<String> keysList = new ArrayList<String>();
    	Enumeration<String> keysEnum = this.allData.keys();
    	while (keysEnum.hasMoreElements()){
    		String key = keysEnum.nextElement();
    		keysList.add(key);
    	}
    	Collections.sort(keysList);
    	//Collections.reverse(keysList);
    	for (String key : keysList){
    		System.out.println("key found is " + key);
    	}
    	return keysList;
    }
    public ScoredSetMetadata getScoredSetMetadataForKey(String key){
    	return this.ssmHash.get(key);
    }
    public String getDataForKey(String key){
    	return this.allData.get(key);
    }
    public String getInputFolderForKey(String key){
    	return getValueFromDataForKey(key,ScoredSetMetadata.INPUT_FOLDER_KEY);
    }
    public String getOutputFolderForKey(String key){
    	return getValueFromDataForKey(key,ScoredSetMetadata.OUTPUT_FOLDER_KEY);
    }
    public String getDetectionResultsFolderForKey(String key){
    	return getValueFromDataForKey(key,ScoredSetMetadata.DETECTION_RESULTS_FOLDER_KEY);
    }
    public String getFocusCharIdForKey(String key){
    	return getValueFromDataForKey(key,ScoredSetMetadata.FOCUS_CHARID_KEY);
    }
    public String getValueFromDataForKey(String key, String lineStart){
    	String result = "";
    	String info =  this.allData.get(key);
    	try {
    		BufferedReader reader = new BufferedReader(new StringReader(info));
        	String line = null;
        	while (null != (line = reader.readLine())){
        		if (line.startsWith(lineStart)){
        			String[] parts = line.split("=");
        			result = parts[1];
        		} 
        		else 
        		{
        			//do nothing
        		}
        	}
        	
    	}
    	catch(IOException ioe){
    		ioe.printStackTrace();
    		System.out.println(ioe.getMessage());
    	}
    	return result;
    }
    public String getDisplayableData(){
    	String key = this.keyList.get(this.currentKeyIndex);
    	String result = "";
    	String info =  this.allData.get(key);
    	try {
    		BufferedReader reader = new BufferedReader(new StringReader(info));
        	String line = null;
        	while (null != (line = reader.readLine())){
        		if (line.startsWith(ScoredSetMetadata.INPUT_FOLDER_KEY)){
        			// do nothing
        		} 
        		else if (line.startsWith(ScoredSetMetadata.OUTPUT_FOLDER_KEY)){
        			// do nothing
        		}
        		else if (line.startsWith(ScoredSetMetadata.DETECTION_RESULTS_FOLDER_KEY)){
        			//do nothing
        		}
        		else if (line.startsWith(ScoredSetMetadata.FOCUS_CHARID_KEY)){
        			//do nothing
        		}
        		else if (line.startsWith("SYSTEM_PROPERTY:")){
        			line = line.replaceFirst("SYSTEM_PROPERTY:", "");
        			result = result + line + NL;
        		}
        		else {
        			result = result + line + NL;
        		}
        	}
        	
    	}
    	catch(IOException ioe){
    		ioe.printStackTrace();
    		System.out.println(ioe.getMessage());
    	}
    	return result;
    }
    public String getCurrentKey(){
    	if (this.currentKeyIndex >=0){
    		String currentKey = this.keyList.get(this.currentKeyIndex);
    		return currentKey;
    	}
    	else {
    		return null;
    	}
    }
    public SessionDataForTaxa getSessionDataForTaxa(MorphobankBundle mb) throws AvatolCVException {
    //public SessionData getSessionResultsData(MorphobankBundle mb) throws AvatolCVException {
    	String key = getCurrentKey();
    	if (null == key){
    		return null;
    	}
    	String input_folder = getInputFolderForKey(key);
    	System.out.println("input folder : " + input_folder);
    	String output_folder = getOutputFolderForKey(key);
    	System.out.println("output folder : " + output_folder);

    	Hashtable<String,InputFile> inputFilesForCharacter = mb.getInputFilesForCharacter(input_folder);
    	Hashtable<String,OutputFile> outputFilesForCharacter = mb.getOutputFilesForCharacter(output_folder);
    	
    	
    	String currentCharId = getFocusCharIdForKey(key);
        String currentCharName = mb.getCharacterNameForId(currentCharId);
      
        System.out.println("....char name for Id : " + currentCharName);
        InputFile inputFile = inputFilesForCharacter.get(currentCharId);
        System.out.println("....inputFile : " + inputFile);
        
        List<ResultImage> trainingSamples = (List<ResultImage>)inputFile.getTrainingSamples();

        
        OutputFile outputFile = outputFilesForCharacter.get(currentCharId);
        List<ResultImage> scoredImages = outputFile.getScoredImages();
        List<ResultImage> unscoredImages = outputFile.getUnscoredImages();
        if (null == scoredImages){
        	System.out.println("ScoredImages null");
        }
        else {
        	System.out.println("ScoredImages length : " +  scoredImages.size());
        }
    	//SessionData srd = new SessionData(currentCharId, currentCharName,trainingSamples,scoredImages, unscoredImages);
        SessionDataForTaxa sdt = new SessionDataForTaxa(currentCharId, currentCharName,trainingSamples,scoredImages, unscoredImages, mb);
		return sdt;
		//return srd;
    }

    //public SessionDataForTaxa getSessionResultsData(MorphobankBundle mb) throws AvatolCVException {
    public SessionData getSessionResultsData(MorphobankBundle mb) throws AvatolCVException {
    	String key = getCurrentKey();
    	if (null == key){
    		return null;
    	}
    	String input_folder = getInputFolderForKey(key);
    	System.out.println("input folder : " + input_folder);
    	String output_folder = getOutputFolderForKey(key);
    	System.out.println("output folder : " + output_folder);

    	Hashtable<String,InputFile> inputFilesForCharacter = mb.getInputFilesForCharacter(input_folder);
    	Hashtable<String,OutputFile> outputFilesForCharacter = mb.getOutputFilesForCharacter(output_folder);
    	
    	
    	String currentCharId = getFocusCharIdForKey(key);
        String currentCharName = mb.getCharacterNameForId(currentCharId);
      
        System.out.println("....char name for Id : " + currentCharName);
        InputFile inputFile = inputFilesForCharacter.get(currentCharId);
        System.out.println("....inputFile : " + inputFile);
        
        List<ResultImage> trainingSamples = (List<ResultImage>)inputFile.getTrainingSamples();

        
        OutputFile outputFile = outputFilesForCharacter.get(currentCharId);
        List<ResultImage> scoredImages = outputFile.getScoredImages();
        List<ResultImage> unscoredImages = outputFile.getUnscoredImages();
        
        
    	SessionData srd = new SessionData(currentCharId, currentCharName,trainingSamples,scoredImages, unscoredImages);
        //SessionDataForTaxa sdt = new SessionDataForTaxa(currentCharId, currentCharName,trainingSamples,scoredImages, unscoredImages);
		//return sdt;
		return srd;
    }
}

