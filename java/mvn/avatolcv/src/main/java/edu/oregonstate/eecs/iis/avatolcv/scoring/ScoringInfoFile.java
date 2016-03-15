package edu.oregonstate.eecs.iis.avatolcv.scoring;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import edu.oregonstate.eecs.iis.avatolcv.AvatolCVConstants;
import edu.oregonstate.eecs.iis.avatolcv.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.core.ImageInfo;
import edu.oregonstate.eecs.iis.avatolcv.normalized.NormalizedImageInfo;
import edu.oregonstate.eecs.iis.avatolcv.normalized.NormalizedTypeIDName;
import edu.oregonstate.eecs.iis.avatolcv.normalized.NormalizedValue;
import edu.oregonstate.eecs.iis.avatolcv.util.ClassicSplitter;

/*
 *     training_<scoringConcernType>_<scoringConcernID>_<scoringConcernName>.txt   // scoring concern info is in the filename
    	
    	scoringConcernType=<someType>
		scoringConcernID=<someID>
		scoringConcernName=<someName>
        imageDir=<someDir>
        #imageName,scoringConcernValue,pointCoordinates
        <image1Name>,<scoringConcernValue>,<pointCoords>
        <image2Name>,<scoringConcernValue>,<pointCoords>
        ...
 */
public class ScoringInfoFile {
	public static final String FILE_PREFIX = "scoring_";
	public static final String SCORING_CONCERN_TYPE = "scoringConcernType";
	public static final String SCORING_CONCERN_ID = "scoringConcernID";
	public static final String SCORING_CONCERN_NAME = "scoringConcernName";
	public static final String IMAGE_DIR = "imageDir";
	private static final String FILESEP= System.getProperty("file.separator");
	private static final String NL= System.getProperty("line.separator");
	private String scoringConcernType;
	private String scoringConcernID;
	private String scoringConcernName;
	private String imageDir;
	private List<String> scoringLines = new ArrayList<String>();
    private Hashtable<String,String> pointCoordinatesForImageIDHash = new Hashtable<String, String>();
    private Hashtable<String,String> trainTestConcernForImageIDHash = new Hashtable<String, String>();
    private Hashtable<String,String> trainTestConcernValueImageIDHash = new Hashtable<String, String>();
	
	//private Hashtable<String,String> scoringConcernValueHash = new Hashtable<String, String>();
	//private Hashtable<String,String> pointCoordinatesHash = new Hashtable<String, String>();
	private List<String> imageNames = new ArrayList<String>();
	public ScoringInfoFile(String scoringConcernType, String scoringConcernID, String scoringConcernName){
		this.scoringConcernType = scoringConcernType;
		this.scoringConcernID   = scoringConcernID;
		this.scoringConcernName = scoringConcernName;
	}
	public NormalizedValue getTrainTestConcernValueForImageID(String imageID) throws AvatolCVException {
	    String s = trainTestConcernValueImageIDHash.get(imageID);
        return new NormalizedValue(s);
    }
	public ScoringInfoFile(String pathname) throws AvatolCVException {
		File f = new File(pathname);
		String filename = f.getName();
		String[] parts = ClassicSplitter.splitt(filename,'.');
		String root = parts[0];
		String[] rootParts = ClassicSplitter.splitt(root,'_');
		this.scoringConcernType = rootParts[1];
		this.scoringConcernID   = rootParts[2];
		this.scoringConcernName = rootParts[3];
		try {
			BufferedReader reader = new BufferedReader(new FileReader(pathname));
			String line = null;
			while (null != (line = reader.readLine())){
				if (line.startsWith("#")){
					// ignore
				}
				else {
					extractImageInfo(line);
				}
			}
			reader.close();
		}
		catch(IOException e){
			throw new AvatolCVException("could not load trainingInfoFile " + pathname);
		}
	}
	public void extractImageInfo(String line) throws AvatolCVException {
		String[] parts = ClassicSplitter.splitt(line,',');
		if (parts.length < 4){
			throw new AvatolCVException("ScoringInfoFile should have four fields in each line - some may be empty: filepath, trainTestConcern, trainTestConcernValue, pointCoordinates");
		}
		String filepath = parts[0];
		String trainTestConcern = parts[1];
		String trainTestConcernValue = parts[2];
		String pointCoordinates = AvatolCVConstants.UNDETERMINED;
		pointCoordinates = parts[3];
		//this.imagePaths.add(filepath);
		String imageID = ImageInfo.getImageIDFromPath(filepath);
		this.pointCoordinatesForImageIDHash.put(imageID, pointCoordinates);
	    this.trainTestConcernForImageIDHash.put(imageID, trainTestConcern);
	    this.trainTestConcernValueImageIDHash.put(imageID, trainTestConcernValue); 
		
	}
	public List<String> getImageNames(){
		return this.imageNames;
	}
	public List<String> getMatchingImageNames(List<String> otherImageNames){
		List<String> result = new ArrayList<String>();
		for (String otherName : otherImageNames){
			for (String name : imageNames){
				if (name.equals(otherName)){
					result.add(name);
				}
			}
		}
		return result;
	}
	//public String getScoringConcernValueForImageName(String imageName){
	//	return this.scoringConcernValueHash.get(imageName);
	//}
	public String getValue(String line){
		String[] parts = ClassicSplitter.splitt(line,'=');
		if (parts.length == 1){
			return  "";
		}
		else {
			return parts[1];
		}
	}
	
	
	public void setImageDir(String imageDir){
		this.imageDir = imageDir;
	}
	public String getFilename(){
		
		String typeString = scoringConcernType;
		if (typeString.equals(NormalizedTypeIDName.TYPE_UNSPECIFIED)){
			typeString = "";
		}
		String idString = scoringConcernID;
		if (idString.equals(NormalizedTypeIDName.ID_UNSPECIFIED)){
			idString = "";
		}
		return FILE_PREFIX + typeString + "_" + idString + "_" + scoringConcernName + ".txt";
	}
	public void addImageInfo(String imagePath, String trainTestConcern, String trainTestConcernValue) throws AvatolCVException{
		String imageID = NormalizedImageInfo.getImageIDFromPath(imagePath);
		if (!ImageInfo.isExcluded(imageID)){
			String scoringLine = imagePath+","+ trainTestConcern + "," + trainTestConcernValue +NL;
			scoringLines.add(scoringLine);
		}
	}
	/**
	 * this one is for helping Michael with debugging Shell's alg
	 * @param imagePath
	 * @param trainTestConcern
	 * @param trainTestConcernValue
	 * @param pointCoordinates
	 * @throws AvatolCVException 
	 */
	public void addImageInfo(String imagePath, String trainTestConcern, String trainTestConcernValue, String pointCoordinates) throws AvatolCVException{
		String imageID = NormalizedImageInfo.getImageIDFromPath(imagePath);
		if (!ImageInfo.isExcluded(imageID)){
			String scoringLine = imagePath+","+ trainTestConcern + "," + trainTestConcernValue  + "," + pointCoordinates+NL;
			scoringLines.add(scoringLine);
		}
	}
	public void persist(String parentDir) throws AvatolCVException {
		String path = parentDir + FILESEP + getFilename();
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(path));
			// decided to use full paths 
			//writer.write("imageDir="+imageDir+NL);
			//writer.write("scoringConcernType="+scoringConcernType+NL);
			//writer.write("scoringConcernID="+scoringConcernID+NL);
			//writer.write("scoringConcernName="+scoringConcernName+NL);
			//writer.write("#imageName,scoringConcernValue,pointCoordinates"+NL);
			for (String trainingLine : scoringLines){
				writer.write(trainingLine);
			}
			writer.close();
		}
		catch(IOException ioe){
			throw new AvatolCVException("cannot write training info file for " + scoringConcernName);
		}
	}
}
