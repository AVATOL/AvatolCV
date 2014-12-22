package edu.oregonstate.eecs.iis.avatolcv.algata;

import edu.oregonstate.eecs.iis.avatolcv.mb.Annotation;
import edu.oregonstate.eecs.iis.avatolcv.mb.AnnotationCoordinates;

public class UnscoredImage implements ResultImage {
	private static final String SEP = System.getProperty("file.separator");
	private String mediaPath;
	private String taxonId;
	private String charId;
	private String charName;
    public UnscoredImage(String line, String rootDir, String charId, String charName){
    	this.charId = charId;
    	this.charName = charName;
    	//image_not_scored|<relative path of mediafile>|<taxonID>|
    	//image_scored|media/M328520_Thyroptera tricolor AMNH268577Mvent.jpg|<taxonid>
    	String[] parts = line.split(Annotation.ANNOTATION_DELIM_ESCAPED_FOR_USE_WITH_SPLIT);
    	String relativeMediaPath = parts[1];
    	this.mediaPath = rootDir + SEP + relativeMediaPath;
    	this.taxonId = parts[2];
    }

	@Override
	public String getCharacterId() {
		return this.charId;
	}
	@Override
	public String getCharacterName() {
		return this.charName;
	}
	@Override
    public String getMediaPath(){
    	return this.mediaPath;
    }
	@Override
	public String getTaxonId() {
		return this.taxonId;
	}
	
	
	@Override
	public boolean hasConfidence() {
		return false;
	}
	@Override
	public String getConfidence() {
		return null;
	}
	
	
	@Override
	public boolean hasAnnotationCoordinates() {
		return false;
	}
	@Override
	public AnnotationCoordinates getAnnotationCoordinates() {
		return null;
	}

	
	@Override
	public boolean hasCharacterState() {
		return false;
	}
	@Override
	public String getCharacterStateId() {
		return null;
	}
	@Override
	public String getCharacterStateName() {
		return null;
	}
}
