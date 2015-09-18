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
import java.util.List;
import java.util.Properties;

/**
 * 
 * @author admin-jed
 *
 * encapsulates generation and loading of {@link SystemDesignDocumentation#whatIsANormalizedFile() normalizedFiles} .
 */
public class AvatolCVDataFiles {
    protected static final String FILESEP = System.getProperty("file.separator");
    protected static final String NL = System.getProperty("line.separator");
    
    public void persistNormalizedImageFile(String path, Properties p) throws AvatolCVException {
    	try {
    		List<String> orderedKeys = new ArrayList<String>();
    		BufferedWriter writer = new BufferedWriter(new FileWriter(path));
    		Enumeration<Object> keys = p.keys();
    		while (keys.hasMoreElements()){
    			String key = (String)keys.nextElement();
    			orderedKeys.add(key);
    		}
    		Collections.sort(orderedKeys);
    		for (String key: orderedKeys){
    			String value = p.getProperty(key);
    			writer.write(key + "=" + value + NL);
    		}
    		writer.close();
    	}
    	catch(IOException ioe){
    		throw new AvatolCVException("could not persist normalized image file to path " + path);
    	}
    }
    public void clearNormalizedImageFiles() throws AvatolCVException {
    	String path = AvatolCVFileSystem.getNormalizedImageInfoDir();
    	File f = new File(path);
    	File[] files = f.listFiles();
    	for (File file : files){
    		if (!file.isDirectory()){
    			file.delete();
    		}
    	}
    }
    public Properties loadNormalizedImageFile(String path) throws AvatolCVException {
    	File f = new File(path);
    	if (!f.exists()){
    		throw new AvatolCVException("could not load normalized image file from path " + path);
    	}
    	try {
    		Properties p = new Properties();
    		BufferedReader reader = new BufferedReader(new FileReader(path));
    		String line = null;
    		while (null != (line = reader.readLine())){
    			String[] parts = line.split("=");
    			String key = parts[0];
    			String val = "";
    			if (parts.length > 1){
    				val = parts[1];
    			}
    			p.setProperty(key,  val);
    		}
    		reader.close();
    		return p;
    	}
    	catch(IOException ioe){
    		throw new AvatolCVException("could not load normalized image file from path " + path);
    	}
    }
}
