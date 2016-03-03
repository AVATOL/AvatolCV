package edu.oregonstate.eecs.iis.avatolcv.core;

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
import edu.oregonstate.eecs.iis.avatolcv.normalized.NormalizedImageInfo;
import edu.oregonstate.eecs.iis.avatolcv.normalized.NormalizedKey;
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
public class TrainingInfoFile {
	public static final String FILE_PREFIX = "training_";
	public static final String SCORING_CONCERN_TYPE = "scoringConcernType";
	public static final String SCORING_CONCERN_ID = "scoringConcernID";
	public static final String SCORING_CONCERN_NAME = "scoringConcernName";
	public static final String IMAGE_DIR = "imageDir";
	private static final String FILESEP= System.getProperty("file.separator");
	private static final String NL= System.getProperty("line.separator");
	private String scoringConcernType;
	private String scoringConcernID;
	private String scoringConcernName;
	//private String imageDir;
	private List<String> trainingLines = new ArrayList<String>();
	
	private Hashtable<String,String> scoringConcernValueForPathHash = new Hashtable<String, String>();
	private Hashtable<String,String> pointCoordinatesForPathHash = new Hashtable<String, String>();
	private Hashtable<String,String> trainTestConcernForPathHash = new Hashtable<String, String>();
	private Hashtable<String,String> trainTestConcernValuePathHash = new Hashtable<String, String>();
	
    private Hashtable<String,String> scoringConcernValueForImageIDHash = new Hashtable<String, String>();
    private Hashtable<String,String> pointCoordinatesForImageIDHash = new Hashtable<String, String>();
    private Hashtable<String,String> trainTestConcernForImageIDHash = new Hashtable<String, String>();
    private Hashtable<String,String> trainTestConcernValueImageIDHash = new Hashtable<String, String>();
    
    private Hashtable<String,String> imagePathForImageIDHash = new Hashtable<String, String>();
	//private List<String> imageNames = new ArrayList<String>();
	private List<String> imagePaths = new ArrayList<String>();
	public TrainingInfoFile(String scoringConcernType, String scoringConcernID, String scoringConcernName){
		this.scoringConcernType = scoringConcernType;
		this.scoringConcernID   = scoringConcernID;
		this.scoringConcernName = scoringConcernName;
	}
	public List<String> getImagePaths(){
		return this.imagePaths;
	}
	public String getScoringConcernValueForImagePath(String imagePath){
		return this.scoringConcernValueForPathHash.get(imagePath);
	}
	public String getTrainTestConcernValueForImagePath(String imagePath){
		return this.trainTestConcernValuePathHash.get(imagePath);
	}
	public String getValue(String line){
		String[] parts = ClassicSplitter.splitt(line,'=');
		if (parts.length == 1){
			return  "";
		}
		else {
			return parts[1];
		}
	}
	public void extractImageInfo(String line) throws AvatolCVException {
		String[] parts = ClassicSplitter.splitt(line,',');
		if (parts.length < 5){
			throw new AvatolCVException("TrainingInfoFile should have five fields in each line - some may be empty: filepath, scoringConcernValue, pointCoordinates, trainTestConcern, trainTestConcernValue");
		}
		String filepath = parts[0];
		String scoringConcernValue = parts[1];
		String pointCoordinates = AvatolCVConstants.UNDETERMINED;
		//if (parts.length == 2){
		//	pointCoordinates = "";
			
		//}
		//else {
			pointCoordinates = parts[2];
		//}
		String trainTestConcern = parts[3];
		String trainTestConcernValue = parts[4];
		this.imagePaths.add(filepath);
		String imageID = ImageInfo.getImageIDFromPath(filepath);
		if (!ImageInfo.isExcluded(imageID)){
			trainingLines.add(line);
			this.scoringConcernValueForPathHash.put(filepath, scoringConcernValue);
			this.pointCoordinatesForPathHash.put(filepath, pointCoordinates);
			this.trainTestConcernForPathHash.put(filepath, trainTestConcern);
			this.trainTestConcernValuePathHash.put(filepath, trainTestConcernValue);
			
			this.scoringConcernValueForImageIDHash.put(imageID, scoringConcernValue);
	        this.pointCoordinatesForImageIDHash.put(imageID, pointCoordinates);
	        this.trainTestConcernForImageIDHash.put(imageID, trainTestConcern);
	        this.trainTestConcernValueImageIDHash.put(imageID, trainTestConcernValue);
	        
	        this.imagePathForImageIDHash.put(imageID, filepath);
		}
	}
	public TrainingInfoFile(String pathname) throws AvatolCVException {
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
	public String getImagePathForImageID(String imageID){
	    return imagePathForImageIDHash.get(imageID);
	}
	public NormalizedKey getNormalizedCharacter() throws AvatolCVException {
	    String s = NormalizedTypeIDName.buildTypeIdName(this.scoringConcernType, this.scoringConcernID, this.scoringConcernName);
	    return new NormalizedKey(s);
	}
	public NormalizedKey getTrainTestConcernForImageID(String imageID) throws AvatolCVException {
	    String s = trainTestConcernForImageIDHash.get(imageID);
	    return new NormalizedKey(s);
	}
	public NormalizedValue getTrainTestConcernValueForImageID(String imageID) throws AvatolCVException {
	    String s = trainTestConcernValueImageIDHash.get(imageID);
        return new NormalizedValue(s);
    }
	//public void setImageDir(String imageDir){
	//	this.imageDir = imageDir;
	//}
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
	public void addImageInfo(String imagePath, String scoringConcernValue, String pointCoordinates, String trainTestConcern, String trainTestConcernValue) throws AvatolCVException {
		String imageID = NormalizedImageInfo.getImageIDFromPath(imagePath);
		if (!ImageInfo.isExcluded(imageID)){
			String trainingLine = imagePath+","+scoringConcernValue+","+pointCoordinates + "," + trainTestConcern + "," + trainTestConcernValue +NL;
			trainingLines.add(trainingLine);
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
			for (String trainingLine : trainingLines){
				writer.write(trainingLine);
			}
			writer.close();
		}
		catch(IOException ioe){
			throw new AvatolCVException("cannot write training info file for " + scoringConcernName);
		}
	}
}
