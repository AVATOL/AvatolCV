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
	
	//private Hashtable<String,String> scoringConcernValueHash = new Hashtable<String, String>();
	//private Hashtable<String,String> pointCoordinatesHash = new Hashtable<String, String>();
	private List<String> imageNames = new ArrayList<String>();
	public ScoringInfoFile(String scoringConcernType, String scoringConcernID, String scoringConcernName){
		this.scoringConcernType = scoringConcernType;
		this.scoringConcernID   = scoringConcernID;
		this.scoringConcernName = scoringConcernName;
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
		String[] parts = line.split("=");
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
	public void addImageInfo(String imageName, String trainTestConcern, String trainTestConcernValue){
		String scoringLine = imageName+","+ trainTestConcern + "," + trainTestConcernValue +NL;
		scoringLines.add(scoringLine);
	}
	/**
	 * this one is for helping Michael with debugging Shell's alg
	 * @param imageName
	 * @param trainTestConcern
	 * @param trainTestConcernValue
	 * @param pointCoordinates
	 */
	public void addImageInfo(String imageName, String trainTestConcern, String trainTestConcernValue, String pointCoordinates){
		String scoringLine = imageName+","+ trainTestConcern + "," + trainTestConcernValue  + "," + pointCoordinates+NL;
		scoringLines.add(scoringLine);
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
