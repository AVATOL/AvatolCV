package edu.oregonstate.eecs.iis.avatolcv.mb;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import edu.oregonstate.eecs.iis.avatolcv.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.algata.DataIOFile;

public class Annotation {
	public static final String FILESEP = System.getProperty("file.separator");
	public static final String ANNOTATION_FILE_DELIM = ":";
	public static final String ANNOTATION_DELIM = "|";
	public static final String ANNOTATION_DELIM_ESCAPED_FOR_USE_WITH_SPLIT = "\\|";
    private String coordinateList;
    private String type;
    private String charId;
    private String charNameText;
    private String charState;
    private String charStateText;
    private int lineNumber;
    private String mediaId;
    private String pathname;
    public Annotation(String info, int lineNumber, String mediaId, String pathname){
    	String[] parts = info.split(ANNOTATION_FILE_DELIM);
    	//System.out.println("annotation info: " + info);
        
        this.coordinateList = parts[0];
        this.type = deriveType(this.coordinateList);
        this.charId = parts[1];
        this.charNameText = parts[2];
        this.charState = parts[3];
        this.charStateText = parts[4];
        this.lineNumber = lineNumber;
        this.mediaId = mediaId;
        this.pathname = pathname;
    }
    public String deriveType(String coordinateList){
    	String[] parts = coordinateList.split(";");
        if (parts.length == 1){
        	return "point";
        }  
        else if (parts.length == 2){
            return "box";  
        }
        else{
            return "polygon";
        }
    }
    public String getCoordinateList(){
    	return this.coordinateList;
    }
    public String getType(){
    	return this.type;
    }
    public String getCharId(){
    	return this.charId;
    }
    public String getCharNameText(){
    	return this.charNameText;
    }
    public String getCharState(){
    	return this.charState;
    }
    public String getCharStateText(){
    	return this.charStateText;
    }
    public int getLineNumber(){
    	return this.lineNumber;
    }
    public String getMediaId(){
    	return this.mediaId;
    }
    public String getPathname(){
    	return this.pathname;
    }
    public String getTrainingDataLine(String mediaFilename, String taxonId) throws AvatolCVException {
    	String relativePath = getRelativePathForAnnotationFile(this.pathname);
    	return DataIOFile.TRAINING_DATA_MARKER + ANNOTATION_DELIM + "media" + FILESEP + mediaFilename + ANNOTATION_DELIM + 
                    charState + ANNOTATION_DELIM + charStateText + ANNOTATION_DELIM + relativePath + ANNOTATION_DELIM + taxonId + ANNOTATION_DELIM + lineNumber;          
    }
    public String getRelativePathForAnnotationFile(String path) throws AvatolCVException {
    	File f = new File(path);
        File parent = f.getParentFile();
        if (!parent.getName().startsWith(Annotations.ANNOTATIONS_DIR)){
        	throw new AvatolCVException("expected annotation file to be in an annotations directory");
        }
        String parentDir = parent.getName();
        String relativePath = parentDir + FILESEP + f.getName();
        return relativePath;
    }
    public void reverseXCoord(){
    	String[] parts = this.coordinateList.split(";");
    	List<String> newCoords = new ArrayList<String>();
    	for (String part : parts){
    		String[] coordParts = part.split(",");
    		String x = coordParts[0];
    		String y = coordParts[1];
    		Double xDouble = new Double(x);
    		double xDoubleValue = xDouble.doubleValue();
    		double newXDouble = 100.0 - xDoubleValue;
    		String newXString = "" + newXDouble;
    		newCoords.add(newXString + "," + y);
    	}
    	replaceCoordinateList(newCoords);
    }

    public void replaceCoordinateList(List<String> newCoords){
    	StringBuilder sb = new StringBuilder();
    	int pairsWithTrailingComma = newCoords.size() - 1;
    	for (int i = 0; i < pairsWithTrailingComma; i++){
    		sb.append(newCoords.get(i) + ",");
    	}
    	int finalIndex = newCoords.size() - 1;
    	sb.append(newCoords.get(finalIndex));
    	this.coordinateList = "" + sb;
    }
    public void reverseYCoord(){
    	String[] parts = this.coordinateList.split(";");
    	List<String> newCoords = new ArrayList<String>();
    	for (String part : parts){
    		String[] coordParts = part.split(",");
    		String x = coordParts[0];
    		String y = coordParts[1];
    		Double yDouble = new Double(y);
    		double yDoubleValue = yDouble.doubleValue();
    		double newYDouble = 100.0 - yDoubleValue;
    		String newYString = "" + newYDouble;
    		newCoords.add(x + "," + newYString);
    	}
    	replaceCoordinateList(newCoords);
    }
}
