package edu.oregonstate.eecs.iis.avatolcv;

public class UnscoredImage {
	private static final String SEP = System.getProperty("file.separator");
	private String mediaPath;
    public UnscoredImage(String line, String rootDir){
    	//image_scored|media/M328520_Thyroptera tricolor AMNH268577Mvent.jpg|<stateid>|<statename>|<annotation path>|confidence
    	String[] parts = line.split(Annotation.ANNOTATION_DELIM_ESCAPED_FOR_USE_WITH_SPLIT);
    	String relativeMediaPath = parts[1];
    	this.mediaPath = rootDir + SEP + relativeMediaPath;
    }
    public String getMediaPath(){
    	return this.mediaPath;
    }
}
