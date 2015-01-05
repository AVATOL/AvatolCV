package edu.oregonstate.eecs.iis.avatolcv.algata;

import edu.oregonstate.eecs.iis.avatolcv.images.ImageScaler;
import edu.oregonstate.eecs.iis.avatolcv.mb.Annotation;
import edu.oregonstate.eecs.iis.avatolcv.mb.AnnotationCoordinates;
import edu.oregonstate.eecs.iis.avatolcv.mb.Media;

public class UnscoredImage implements ResultImage {
	private static final String SEP = System.getProperty("file.separator");
	private String mediaPath;
	private String taxonId;
	private String charId;
	private String charName;
	private String mediaId = null;
    public UnscoredImage(String line, String rootDir, String charId, String charName){
    	this.charId = charId;
    	this.charName = charName;
    	//image_not_scored|<relative path of mediafile>|<taxonID>|
    	//image_scored|media/M328520_Thyroptera tricolor AMNH268577Mvent.jpg|<taxonid>
    	String[] parts = line.split(Annotation.ANNOTATION_DELIM_ESCAPED_FOR_USE_WITH_SPLIT);
    	String relativeMediaPath = parts[1];
    	this.mediaPath = rootDir + SEP + relativeMediaPath;
    	this.mediaId = TrainingSample.getMediaIdFromRelativePath(relativeMediaPath);
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
/*
	@Override
	public String getScaledMediaPath() {
		String mediaPath = this.getMediaPath();
		String scaledMediaPath = mediaPath.replaceFirst(Media.MEDIA_DIRNAME, ImageScaler.SCALED_IMAGE_DIR);
		return scaledMediaPath;
	}
*/
	@Override
	public String getThumbnailMediaPath() {
		String mediaPath = this.getMediaPath();
		String thumbnailMediaPath = mediaPath.replaceFirst(Media.MEDIA_DIRNAME, ImageScaler.THUMBNAIL_DIR);
		return thumbnailMediaPath;
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

	@Override
	public String getMediaId() {
		return this.mediaId;
	}

}
