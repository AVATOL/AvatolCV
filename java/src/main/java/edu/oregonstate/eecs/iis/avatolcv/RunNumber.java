package edu.oregonstate.eecs.iis.avatolcv;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class RunNumber {
	private String FILESEP = System.getProperty("file.separator");
	private String NL = System.getProperty("line.separator");
	private String dirName = null;
	private String currentRunNumber = null;
    public RunNumber(String dirName){
	    this.dirName = dirName;
	    this.currentRunNumber = incrementRunNumber();
    }
    public String incrementRunNumber(){
    	String runNumberFilePath = this.dirName + FILESEP + "nextRunNumber.txt";
    	File f = new File(runNumberFilePath);
    	String result = "?";
    	try {
    		if (!f.exists()){
        		// create new file and return 1
        		persist(runNumberFilePath, "2");
        		result = "1";
        	}
        	else {
        		String nextValue = loadNextRunNumber(runNumberFilePath);
        		Integer runNumberInteger = new Integer(nextValue);
        		int futureValue = runNumberInteger.intValue() + 1;
        		persist(runNumberFilePath,"" + futureValue);
        		result = nextValue;
        	}
    	}
    	catch(Exception e){
    		e.printStackTrace();
    		System.out.println(e.getMessage());
    	}
    	return result;
    }
    public String getCurrentRunNumber(){
    	return this.currentRunNumber;
    }
    public String loadNextRunNumber(String path) throws FileNotFoundException, IOException  {
    	BufferedReader reader = new BufferedReader(new FileReader(path));
    	String value = reader.readLine();
    	reader.close();
    	return value;
    }
    public void persist(String path, String newValue) throws IOException {
    	File f = new File(path);
    	if (f.exists()){
    		f.delete();
    	}
    	BufferedWriter writer = new BufferedWriter(new FileWriter(path));
    	writer.write(newValue + NL);
    	writer.close();
    }
}
