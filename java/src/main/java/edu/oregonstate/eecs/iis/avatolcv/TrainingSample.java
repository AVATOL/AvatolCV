package edu.oregonstate.eecs.iis.avatolcv;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class TrainingSample {
	private static final String SEP = System.getProperty("file.separator");
	private String mediaPath;
	private String characterStateId;
	private String characterStateName;
	private String annotationFilePath;
	private String taxonId;
	private String annotationLineNumber;
	private AnnotationCoordinates annotationCoordinates;
	private String characterId;
	private String characterName;
    public TrainingSample(String line, String rootDir, String charId, String charName){
        this.characterId = charId;
        this.characterName = charName;
    	//training_data|media/M328543_Thyroptera tricolor AMNH239080Fvent.jpg|s946108|I1 present|annotations\c427749_m328543.txt|t281048|1
    	String[] parts = line.split(Annotation.ANNOTATION_DELIM_ESCAPED_FOR_USE_WITH_SPLIT);
    	String relativeMediaPath = parts[1];
    	this.mediaPath = rootDir + SEP + relativeMediaPath;
    	this.characterStateId = parts[2];
    	this.characterStateName = parts[3];
    	String relativeAnnotationPath = parts[4];
    	this.annotationFilePath = rootDir + SEP + relativeAnnotationPath;
    	this.taxonId = parts[5];
    	this.annotationLineNumber = parts[6];
    	if (!relativeAnnotationPath.equals("NA")){
    		//annotation line has coords, character and character name : 4588,1822:c427749:Upper I1 presence:s946108:I1 present
    		loadAnnotationEntry(this.annotationFilePath, this.annotationLineNumber);
    	}
    	
    	
    }
    public String getMediaPath(){
    	return this.mediaPath;
    }
    public String getCharacterStateId(){
    	return this.characterStateId;
    }
    public String getCharacterStateName(){
    	return this.characterStateName;
    }
    public String getTaxonId(){
    	return this.taxonId;
    }
    public AnnotationCoordinates getAnnotationCoordinates(){
    	return this.annotationCoordinates;
    }
    public String getCharacterId(){
    	return this.characterId;
    }
    public String getCharacterName(){
    	return this.characterName;
    }
    public void parseAnnotationLine(String line){
    	// 4588,1822:c427749:Upper I1 presence:s946108:I1 present
    	String[] parts = line.split(":");
    	String coordsString = parts[0];
    	// already have other info
    	this.annotationCoordinates = new AnnotationCoordinates(coordsString);
    }
    public void loadAnnotationEntry(String path, String lineNumber){
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
        }
        catch(IOException ioe){
        	ioe.printStackTrace();
        	System.out.println(ioe.getMessage());
        }
    }
}
