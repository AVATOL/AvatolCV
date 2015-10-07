package edu.oregonstate.eecs.iis.avatolcv.core;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import edu.oregonstate.eecs.iis.avatolcv.AvatolCVException;

/*
 *    scores_<scoringConcernType>_<scoringConcernID>_<scoringConcernName>.txt contains 
    
        imageDir=<someDir>
		scoringConcernType=<someType>
		scoringConcernID=<someID>
		scoringConcernName=<someName>
        scoringConcernValues=<val1>,<val2>,<val3>...
        #imageName,scoringConcernValue,pointCoordinates,confVal1,confVal2,confVal3...
        <image1Name>,<scoringConcernValue>,<pointCoords>,<confVal1>,<confVal2>,<confVal3>...
        ...
 */
public class ScoresInfoFile {
	public static final String FILE_PREFIX = "scores_";
	public static final String IMAGE_DIR = "imageDir";
	private static final String SCORING_CONCERN_VALUES = "classNames";
	private String scoringConcernType;
	private String scoringConcernID;
	private String scoringConcernName;
	private String imageDir;
	private List<String> scoringLines = new ArrayList<String>();
	private List<String> valuesList = new ArrayList<String>();
	private Hashtable<String,String> scoringConcernValueHash = new Hashtable<String, String>();
	private Hashtable<String,String> pointCoordinatesHash = new Hashtable<String, String>();
	private List<String> imageNames = new ArrayList<String>();
	private Hashtable<String,String> confidenceHash = new Hashtable<String, String>();
	public ScoresInfoFile(String scoringConcernType, String scoringConcernID, String scoringConcernName){
		this.scoringConcernType = scoringConcernType;
		this.scoringConcernID   = scoringConcernID;
		this.scoringConcernName = scoringConcernName;
	}
	public List<String> getImageNames(){
		return this.imageNames;
	}
	public boolean hasImage(String name){
		return imageNames.contains(name);
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
        //#imageName,scoringConcernValue,pointCoordinates,confVal1,confVal2,confVal3...
		int confCount = this.valuesList.size();
		int i = 0;
		String[] parts = line.split(",");
		String filename = parts[i++];
		String scoringConcernValue = parts[i++];
		String pointCoordinates = parts[i++];
		this.imageNames.add(filename);
		this.scoringConcernValueHash.put(filename, scoringConcernValue);
		this.pointCoordinatesHash.put(filename, pointCoordinates);
		for (int j = 0; j < confCount; j++){
			this.confidenceHash.put(filename+this.valuesList.get(j), parts[i++]);
		}
	}
	/*
	 *    scores_<scoringConcernType>_<scoringConcernID>_<scoringConcernName>.txt contains 
	    
	        imageDir=<someDir>
			scoringConcernType=<someType>
			scoringConcernID=<someID>
			scoringConcernName=<someName>
	        scoringConcernValues=<val1>,<val2>,<val3>...
	        #imageName,scoringConcernValue,pointCoordinates,confVal1,confVal2,confVal3...
	        <image1Name>,<scoringConcernValue>,<pointCoords>,<confVal1>,<confVal2>,<confVal3>...
	        ...
	 */
	public ScoresInfoFile(String pathname) throws AvatolCVException {
		try {
			BufferedReader reader = new BufferedReader(new FileReader(pathname));
			String line = null;
			while (null != (line = reader.readLine())){
				if (line.startsWith(TrainingInfoFile.SCORING_CONCERN_TYPE)){
					this.scoringConcernType = getValue(line);
				}
				else if (line.startsWith(TrainingInfoFile.SCORING_CONCERN_ID)){
					this.scoringConcernID = getValue(line);
				}
				else if (line.startsWith(TrainingInfoFile.SCORING_CONCERN_NAME)){
					this.scoringConcernName = getValue(line);
				}
				else if (line.startsWith(IMAGE_DIR)){
					this.imageDir = getValue(line);
				}
				else if (line.startsWith(SCORING_CONCERN_VALUES)){
					String valuesString = getValue(line);
					String[] values = valuesString.split(",");
					for (String v : values){
						valuesList.add(v);
					}
				}
				else if (line.startsWith("#")){
					// ignore
				}
				else {
					scoringLines.add(line);
					extractImageInfo(line);
				}
			}
			reader.close();
		}
		catch(IOException e){
			throw new AvatolCVException("could not load trainingInfoFile " + pathname);
		}
	}
	public String getConfidenceForImageValue(String filename, String value){
		return this.confidenceHash.get(filename+value);
	}
	public void setImageDir(String imageDir){
		this.imageDir = imageDir;
	}
	public String getFilename(){
		return FILE_PREFIX + scoringConcernType + "_" + scoringConcernID + "_" + scoringConcernName + ".txt";
	}
	//public void addImageInfo(String imageName, String scoringConcernValue, String pointCoordinates){
	//	String trainingLine = imageName+","+scoringConcernValue+","+pointCoordinates+NL;
	//	trainingLines.add(trainingLine);
	//}
	

}
