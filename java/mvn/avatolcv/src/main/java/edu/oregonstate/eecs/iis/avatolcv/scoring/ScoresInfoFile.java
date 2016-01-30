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

import edu.oregonstate.eecs.iis.avatolcv.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.util.ClassicSplitter;

/*
 *    scores_<scoringConcernType>_<scoringConcernID>_<scoringConcernName>.txt contains 
    
        classNames=<val1>,<val2>,<val3>...
        #imageName,scoringConcernValue,pointCoordinates,confVal1,confVal2,confVal3...
        <image1Name>,<scoringConcernValue>,<pointCoords>,<confVal1>,<confVal2>,<confVal3>...
        ...
 */
public class ScoresInfoFile {
	public static final String FILE_PREFIX = "scored_";
	public static final String FILE_PREFIX_ALTERNATE = "scoring_";
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
	//private List<String> imageNames = new ArrayList<String>();
	private List<String> imagePaths = new ArrayList<String>();
	private Hashtable<String,String> confidenceHash = new Hashtable<String, String>();
	public ScoresInfoFile(String scoringConcernType, String scoringConcernID, String scoringConcernName){
		this.scoringConcernType = scoringConcernType;
		this.scoringConcernID   = scoringConcernID;
		this.scoringConcernName = scoringConcernName;
	}
	public List<String> getImagePaths(){
		return this.imagePaths;
	}
	public boolean hasImage(String path){
		return imagePaths.contains(path);
	}
	public String getScoringConcernValueForImagePath(String imagePath){
		return this.scoringConcernValueHash.get(imagePath);
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
        //#imageName,scoringConcernValue,pointCoordinates,confVal1,confVal2,confVal3...
		int confCount = this.valuesList.size();
		int i = 0;
		String[] parts = ClassicSplitter.splitt(line,',');
		String pathname = parts[i++];
		String scoringConcernValue = parts[i++];
		String pointCoordinates = parts[i++];
		this.imagePaths.add(pathname);
		this.scoringConcernValueHash.put(pathname, scoringConcernValue);
		this.pointCoordinatesHash.put(pathname, pointCoordinates);
		System.out.println("line : " + line);
		for (int j = 0; j < confCount; j++){
			String key = pathname+this.valuesList.get(j);
			String val = parts[i++];
			System.out.println("key " + key + "   val " + val);
			this.confidenceHash.put(key, val);
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
				if (line.startsWith(SCORING_CONCERN_VALUES)){
					String valuesString = getValue(line);
					String[] values = ClassicSplitter.splitt(valuesString,',');
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
	public String getConfidenceForImageValue(String path, String value){
		System.out.println("confidenceHashKey " + path+value);
		return this.confidenceHash.get(path+value);
	}
	//public void setImageDir(String imageDir){
	//	this.imageDir = imageDir;
	//ß}
	public String getFilename(){
		return FILE_PREFIX + scoringConcernType + "_" + scoringConcernID + "_" + scoringConcernName + ".txt";
	}
	//public void addImageInfo(String imageName, String scoringConcernValue, String pointCoordinates){
	//	String trainingLine = imageName+","+scoringConcernValue+","+pointCoordinates+NL;
	//	trainingLines.add(trainingLine);
	//}
	

}
