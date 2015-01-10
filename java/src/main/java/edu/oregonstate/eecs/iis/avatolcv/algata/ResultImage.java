package edu.oregonstate.eecs.iis.avatolcv.algata;

import edu.oregonstate.eecs.iis.avatolcv.mb.AnnotationCoordinates;
import edu.oregonstate.eecs.iis.avatolcv.mb.MorphobankDataException;

public interface ResultImage {
    public String getCharacterId();
    public String getCharacterName();
    public String getMediaPath();
    //public String getScaledMediaPath();
    public String getThumbnailMediaPath();
    public String getTaxonId();
    
	public boolean hasConfidence();
	public String getConfidence();
	
    public boolean hasAnnotationCoordinates();
    public AnnotationCoordinates getAnnotationCoordinates();
    
    public boolean hasCharacterState();
    public String getCharacterStateId();
    public String getCharacterStateName();
    public String getMediaId();
    public String getHumanLabel() throws MorphobankDataException;
}
