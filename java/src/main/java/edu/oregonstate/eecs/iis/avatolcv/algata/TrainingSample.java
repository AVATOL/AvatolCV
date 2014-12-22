package edu.oregonstate.eecs.iis.avatolcv.algata;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import edu.oregonstate.eecs.iis.avatolcv.mb.Annotation;
import edu.oregonstate.eecs.iis.avatolcv.mb.AnnotationCoordinates;
import edu.oregonstate.eecs.iis.avatolcv.mb.Media;

public class TrainingSample extends AnnotatedItem implements ResultImage  {
	private static final String SEP = System.getProperty("file.separator");
	private String mediaPath;
	private String characterStateId;
	private String characterStateName;
	private String annotationFilePath;
	private String taxonId;
	private String annotationLineNumber;
	private String characterId;
	private String characterName;
	private String mediaId;
    public TrainingSample(String line, String rootDir, String charId, String charName){
        this.characterId = charId;
        this.characterName = charName;
    	//training_data|media/M328543_Thyroptera tricolor AMNH239080Fvent.jpg|s946108|I1 present|annotations\c427749_m328543.txt|t281048|1
    	String[] parts = line.split(Annotation.ANNOTATION_DELIM_ESCAPED_FOR_USE_WITH_SPLIT);
    	String relativeMediaPath = parts[1];
    	this.mediaId = getMediaIdFromRelativePath(relativeMediaPath);
    	this.mediaPath = rootDir + SEP + relativeMediaPath;
    	this.characterStateId = parts[2];
    	this.characterStateName = parts[3];
    	String relativeAnnotationPath = parts[4];
    	this.annotationFilePath = rootDir + SEP + relativeAnnotationPath;
    	this.taxonId = parts[5];
    	this.annotationLineNumber = parts[6];
    	if (!relativeAnnotationPath.equals("NA")){
    		//annotation line has coords, character and character name : 4588,1822:c427749:Upper I1 presence:s946108:I1 present
    		loadAnnotationCoordinates(this.annotationFilePath, this.annotationLineNumber);
    	}
    	
    	
    }
    public String getMediaId(){
    	return this.mediaId;
    }
    public static String getMediaIdFromRelativePath(String rp){
    	String prefix = Media.MEDIA_DIRNAME + SEP;
    	String mediaFilename = rp.replace(prefix,"");
    	String[] parts = mediaFilename.split("_");
    	String mediaIdCapM = parts[0];
    	String mediaId = mediaIdCapM.replace("M","m");
    	return mediaId;
    }
    @Override
    public boolean hasAnnotationCoordinates(){
    	if (this.getAnnotationCoordinates() != null){
    		return true;
    	}
    	return false;
    }
    @Override
    public String getMediaPath(){
    	return this.mediaPath;
    }
    @Override
    public String getCharacterStateId(){
    	return this.characterStateId;
    }
    @Override
    public String getCharacterStateName(){
    	return this.characterStateName;
    }
    @Override
    public String getTaxonId(){
    	return this.taxonId;
    }
    @Override
    public AnnotationCoordinates getAnnotationCoordinates(){
    	return super.getAnnotationCoordinates();
    }
    @Override
    public String getCharacterId(){
    	return this.characterId;
    }
    @Override
    public String getCharacterName(){
    	return this.characterName;
    }
	@Override
	public boolean hasConfidence() {
		return false;
	}
	@Override
	public String getConfidence() {
		return "NA";
	}
	@Override
	public boolean hasCharacterState() {
		return true;
	}
}
