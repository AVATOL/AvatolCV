package edu.oregonstate.eecs.iis.avatolcv;

public class ScoredImage {
	private static final String SEP = System.getProperty("file.separator");
	private String mediaPath;
	private String stateId;
	private String stateName;
	private String annotationFilePath;
	private String confidence;
    public ScoredImage(String line, String rootDir){
    	//image_scored|media/M328520_Thyroptera tricolor AMNH268577Mvent.jpg|<stateid>|<statename>|<annotation path>|confidence
    	String[] parts = line.split(Annotation.ANNOTATION_DELIM_ESCAPED_FOR_USE_WITH_SPLIT);
    	String relativeMediaPath = parts[1];
    	this.mediaPath = rootDir + SEP + relativeMediaPath;
    	this.stateId = parts[2];
    	this.stateName=parts[3];
    	this.annotationFilePath = rootDir + SEP + parts[4];
    	this.confidence = parts[5];
    }
    public String getMediaPath(){
    	return this.mediaPath;
    }
    public String getStateId(){
    	return this.stateId;
    }
    public String getStateName(){
    	return this.stateName;
    }
    public String getAnnotationPathname(){
    	return this.annotationFilePath;
    }
    public String getConfidence(){
    	return this.confidence;
    }
}
