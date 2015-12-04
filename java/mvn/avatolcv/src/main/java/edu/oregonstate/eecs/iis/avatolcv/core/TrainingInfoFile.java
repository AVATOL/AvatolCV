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

import edu.oregonstate.eecs.iis.avatolcv.AvatolCVException;

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
	private String imageDir;
	private List<String> trainingLines = new ArrayList<String>();
	
	private Hashtable<String,String> scoringConcernValueHash = new Hashtable<String, String>();
	private Hashtable<String,String> pointCoordinatesHash = new Hashtable<String, String>();
	private List<String> imageNames = new ArrayList<String>();
	public TrainingInfoFile(String scoringConcernType, String scoringConcernID, String scoringConcernName){
		this.scoringConcernType = scoringConcernType;
		this.scoringConcernID   = scoringConcernID;
		this.scoringConcernName = scoringConcernName;
	}
	public List<String> getImageNames(){
		return this.imageNames;
	}
	public String getScoringConcernValueForImageName(String imageName){
		return this.scoringConcernValueHash.get(imageName);
	}
	public String getValue(String line){
		String[] parts = line.split("=");
		if (parts.length == 1){
			return  "";
		}
		else {
			return parts[1];
		}
	}
	public void extractImageInfo(String line) throws AvatolCVException {
		String[] parts = line.split(",");
		String filename = parts[0];
		String scoringConcernValue = parts[1];
		String pointCoordinates = "?";
		if (parts.length == 2){
			pointCoordinates = "";
		}
		else {
			pointCoordinates = parts[2];
		}
		this.imageNames.add(filename);
		this.scoringConcernValueHash.put(filename, scoringConcernValue);
		this.pointCoordinatesHash.put(filename, pointCoordinates);
	}
	public TrainingInfoFile(String pathname) throws AvatolCVException {
		try {
			BufferedReader reader = new BufferedReader(new FileReader(pathname));
			String line = null;
			while (null != (line = reader.readLine())){
				if (line.startsWith(SCORING_CONCERN_TYPE)){
					this.scoringConcernType = getValue(line);
				}
				else if (line.startsWith(SCORING_CONCERN_ID)){
					this.scoringConcernID = getValue(line);
				}
				else if (line.startsWith(SCORING_CONCERN_NAME)){
					this.scoringConcernName = getValue(line);
				}
				else if (line.startsWith(IMAGE_DIR)){
					this.imageDir = getValue(line);
				}
				else if (line.startsWith("#")){
					// ignore
				}
				else {
					trainingLines.add(line);
					extractImageInfo(line);
				}
			}
			reader.close();
		}
		catch(IOException e){
			throw new AvatolCVException("could not load trainingInfoFile " + pathname);
		}
	}
	
	public void setImageDir(String imageDir){
		this.imageDir = imageDir;
	}
	public String getFilename(){
		return FILE_PREFIX + scoringConcernType + "_" + scoringConcernID + "_" + scoringConcernName + ".txt";
	}
	public void addImageInfo(String imageName, String scoringConcernValue, String pointCoordinates){
		String trainingLine = imageName+","+scoringConcernValue+","+pointCoordinates+NL;
		trainingLines.add(trainingLine);
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
