package edu.oregonstate.eecs.iis.avatolcv;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

public class ScoredSetMetadata {
	private Hashtable<String,String> allData = new Hashtable<String,String>();
	private String metadataDir = null;
	private String SEP = System.getProperty("file.separator");
	private String NL = System.getProperty("line.separator");
	public ScoredSetMetadata(String rootDir){
		this.metadataDir = rootDir + SEP + "scoredSetMetadata";
		File f = new File(this.metadataDir);
		f.mkdirs();
	}
	public void persistForDPM(String matrixName, String taxon, String character, String view, List<String> charactersTrained) throws AvatolCVException {
    	String path = getPath(matrixName,"DPM");
    	try {
    		BufferedWriter writer = new BufferedWriter(new FileWriter(path));
    		writer.write("matrix      : " + matrixName + NL);
    		writer.write("taxon       : " + taxon + NL);
    		writer.write("character   : " + character + NL);
    		writer.write("view        : " + view + NL);
    		writer.write("algorithm   : DPM" + NL);
    		writer.write(NL);
    		for (String trainedChar : charactersTrained){
    			writer.write("character trained : " + trainedChar + NL);
    		}
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
    	String filename = alg + "_" + (System.currentTimeMillis() / 1000L) + ".txt";
    	String path = this.metadataDir + SEP + filename;
    	return path;
    }
    public void loadAll() throws AvatolCVException {
    	File f = new File(this.metadataDir);
    	File[] files = f.listFiles();
    	for (File mdFile : files){
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
    			this.allData.put(fileRoot, sb.toString());
    		}
    		catch(IOException ioe){
    			ioe.printStackTrace();
    			throw new AvatolCVException(ioe.getMessage());
    		}
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
    		System.out.println(key);
    	}
    	return keysList;
    }
    public String getDataForKey(String key){
    	return this.allData.get(key);
    }
}
