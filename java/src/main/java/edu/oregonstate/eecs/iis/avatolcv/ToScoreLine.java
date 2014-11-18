package edu.oregonstate.eecs.iis.avatolcv;

import edu.oregonstate.eecs.iis.avatolcv.mb.Annotation;

public class ToScoreLine {
	private static final String SEP = System.getProperty("file.separator");
	private String mediaPath;
	private String taxonId;
    public ToScoreLine(String line, String rootDir){
    	//image_to_score|media/M328520_Thyroptera tricolor AMNH268577Mvent.jpg|t281048
    	String[] parts = line.split(Annotation.ANNOTATION_DELIM_ESCAPED_FOR_USE_WITH_SPLIT);
    	String relativeMediaPath = parts[1];
    	this.mediaPath = rootDir + SEP + relativeMediaPath;
    	this.taxonId = parts[2];
    }
    public String getMediaPath(){
    	return this.mediaPath;
    }
    public String getTaxonId(){
    	return this.taxonId;
    }
}
