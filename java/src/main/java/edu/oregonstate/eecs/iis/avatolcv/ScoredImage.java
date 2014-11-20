package edu.oregonstate.eecs.iis.avatolcv;

import edu.oregonstate.eecs.iis.avatolcv.mb.Annotation;
import edu.oregonstate.eecs.iis.avatolcv.mb.AnnotationCoordinates;

public class ScoredImage extends AnnotatedItem implements ResultImage {
	private static final String SEP = System.getProperty("file.separator");
	private String mediaPath;
	private String stateId;
	private String stateName;
	private String annotationFilePath;
	private String confidence;
	private String charId;
	private String charName;
	private String taxonId;
	private String annotationLineNumber;
	
    public ScoredImage(String line, String rootDir, String charId, String charName) throws AvatolCVException {
    	this.charId = charId;
    	this.charName = charName;
    	//image_scored|<relative path of mediafile>|<characterStateID>|<characterStateName>|<**relative path of annotation file>|<taxonID>|<***line number in annotations file>|<****score_confidence>
    	//image_scored|media/M328520_Thyroptera tricolor AMNH268577Mvent.jpg|<stateid>|<statename>|<annotation path>|<taxonID>|<lineNumberInAnnotationFile>|confidence
    	String[] parts = line.split(Annotation.ANNOTATION_DELIM_ESCAPED_FOR_USE_WITH_SPLIT);
    	String relativeMediaPath = parts[1];
    	this.mediaPath = rootDir + SEP + relativeMediaPath;
    	this.stateId = parts[2];
    	this.stateName=parts[3];
    	String relativeAnnotationPath = parts[4];
    	this.annotationFilePath = rootDir + SEP + relativeAnnotationPath;
    	this.taxonId = parts[5];
    	this.annotationLineNumber = parts[6];
    	if (!this.annotationLineNumber.equals("NA")){
    		try {
        	    int lineNumberint = new Integer(this.annotationLineNumber).intValue();
        	}
        	catch(NumberFormatException nfe){
        		throw new AvatolCVException("given line number is not a number : " + this.annotationLineNumber);
        	}
    	}
    	
    	this.confidence = parts[7];
    	if (!relativeAnnotationPath.equals("NA")){
    		//annotation line has coords, character and character name : 4588,1822:c427749:Upper I1 presence:s946108:I1 present
    		loadAnnotationCoordinates(this.annotationFilePath, this.annotationLineNumber);
    	}
    }

    public String getAnnotationPathname(){
    	return this.annotationFilePath;
    }
    
	@Override
    public String getMediaPath(){
    	return this.mediaPath;
    }
	@Override
    public String getConfidence(){
    	return this.confidence;
    }
	@Override
	public boolean hasAnnotationCoordinates() {
		return true;
	}
	@Override
	public String getCharacterStateId() {
		return this.stateId;
	}
	@Override
	public String getCharacterStateName() {
		return this.stateName;
	}
	@Override
	public String getTaxonId() {
		return this.taxonId;
	}
	@Override
	public AnnotationCoordinates getAnnotationCoordinates() {
		return super.getAnnotationCoordinates();
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
	public boolean hasConfidence() {
		return true;
	}

	@Override
	public boolean hasCharacterState() {
		return true;
	}
}
