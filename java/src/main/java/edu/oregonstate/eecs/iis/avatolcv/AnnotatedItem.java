package edu.oregonstate.eecs.iis.avatolcv;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import edu.oregonstate.eecs.iis.avatolcv.mb.AnnotationCoordinates;

public class AnnotatedItem {

	private AnnotationCoordinates annotationCoordinates;
    public void parseAnnotationLine(String line){
    	// 4588,1822:c427749:Upper I1 presence:s946108:I1 present
    	String[] parts = line.split(":");
    	String coordsString = parts[0];
    	// already have other info
    	this.annotationCoordinates = new AnnotationCoordinates(coordsString);
    }
    public void loadAnnotationCoordinates(String path, String lineNumber){
        try{
        	int lineNumberInt = new Integer(lineNumber).intValue();
        	BufferedReader reader = new BufferedReader(new FileReader(path));
        	String line = null;
        	int curLineNumber = 1;
        	while (null != (line = reader.readLine())){
        		if (lineNumberInt == curLineNumber){
        			parseAnnotationLine(line);
        			return;
        		}
        		else {
        			curLineNumber += 1;
        		}
        	}
        	reader.close();
        }
        catch(IOException ioe){
        	ioe.printStackTrace();
        	System.out.println(ioe.getMessage());
        }
    }
    public AnnotationCoordinates getAnnotationCoordinates(){
    	return this.annotationCoordinates;
    }
}
