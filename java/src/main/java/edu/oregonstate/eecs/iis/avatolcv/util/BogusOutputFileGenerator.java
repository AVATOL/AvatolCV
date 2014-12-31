package edu.oregonstate.eecs.iis.avatolcv.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import edu.oregonstate.eecs.iis.avatolcv.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.AvatolCVProperties;
import edu.oregonstate.eecs.iis.avatolcv.ScoredSetMetadatas;
import edu.oregonstate.eecs.iis.avatolcv.mb.Annotation;
import edu.oregonstate.eecs.iis.avatolcv.mb.Character;
import edu.oregonstate.eecs.iis.avatolcv.mb.CharacterState;
import edu.oregonstate.eecs.iis.avatolcv.mb.MorphobankBundle;
import edu.oregonstate.eecs.iis.avatolcv.mb.MorphobankDataException;

public class BogusOutputFileGenerator {
	private static final String FILESEP = System.getProperty("file.separator");
	private static final String NL = System.getProperty("line.separator");
    public static void main(String[] args){
    	String inputDir ="C:\\avatol\\git\\avatol_cv\\matrix_downloads\\BAT\\input\\DPM\\c427749c427751c427753c427754c427760\\v3540\\split_0.7";
    	String outputDir = "C:\\avatol\\git\\avatol_cv\\matrix_downloads\\BAT\\output\\DPM\\c427749c427751c427753c427754c427760\\v3540\\split_0.7";
    	String detectionResultsDir = "C:\\avatol\\git\\avatol_cv\\matrix_downloads\\BAT\\detection_results\\DPM\\c427749c427751c427753c427754c427760\\v3540\\split_0.7";
    	String detectionResultsRelDir =  "detection_results\\DPM\\c427749c427751c427753c427754c427760\\v3540\\split_0.7";
    	
    	
    	try {
    		MorphobankBundle bundle = new MorphobankBundle("C:\\avatol\\git\\avatol_cv\\matrix_downloads\\BAT");
        	BogusOutputFileGenerator g = new BogusOutputFileGenerator(inputDir, outputDir, detectionResultsRelDir, bundle);
        	ScoredSetMetadatas ssm = new ScoredSetMetadatas("C:\\avatol\\git\\avatol_cv\\");
    		AvatolCVProperties props = new AvatolCVProperties("C:\\avatol\\git\\avatol_cv\\matrix_downloads\\BAT");
        	List<String> charactersTrained = new ArrayList<String>();
        	charactersTrained.add("c427749");
        	charactersTrained.add("c427751");
        	charactersTrained.add("c427753");
        	charactersTrained.add("c427754");
        	charactersTrained.add("c427760");
        	ssm.persistForDPM( "BAT", "UPPER I1 PRESENCE", "c427749", "Ventral", charactersTrained,
        			inputDir, outputDir, detectionResultsDir, props);
    	}
    	catch(AvatolCVException e){
    		System.out.println(e.getMessage());
    		e.printStackTrace();
    	}
    	catch(MorphobankDataException mde){
    		System.out.println(mde.getMessage());
    		mde.printStackTrace();
    	}
    	
    }
    public BogusOutputFileGenerator(String inputDir, String outputDir, String detectionResultsRelDir, MorphobankBundle bundle) throws AvatolCVException {
    	File output = new File(outputDir);
    	output.mkdirs();
    	File[] files = output.listFiles();
    	for (File f : files){
    		f.delete();
    	}
    	File input = new File(inputDir);
    	File[] inputFiles = input.listFiles();
    	for (File inputFile : inputFiles){
    		if (inputFile.getName().startsWith("sorted_input_data")){
    			generateBogusOutputFile(outputDir, inputFile, detectionResultsRelDir, bundle);
    		}
    	}
    }
    public String getCharIdFromFilename(String filename){
    	String filenameSansPrefix = filename.replaceAll("sorted_output_data_", "");
    	String[] parts = filenameSansPrefix.split("_");
    	String charId = parts[0];
    	return charId;
    }
    public void generateBogusOutputFile(String outputDir, File inputFile, String detectionResultsRelDir, MorphobankBundle bundle) throws AvatolCVException {
    	String outputFilename = deriveOutputFilenameFromInputFile(inputFile);
    	String outputFilePath = inputFile.getParent().replaceAll("input","output") + FILESEP + outputFilename;
    	String charId = getCharIdFromFilename(outputFilename);
    	try {
    		BufferedReader reader = new BufferedReader(new FileReader(inputFile.getAbsolutePath()));
    		BufferedWriter writer = new BufferedWriter(new FileWriter(outputFilePath));
    		String line = null;
    		while (null != (line = reader.readLine())){
    			if (line.startsWith("training")){
    				System.out.println("training line " + line);
    				writer.write(line + NL);
    			}
    			else if (line.startsWith("image_to_score")){
    				String outputLine = generateScoredLine(charId, line, detectionResultsRelDir, bundle);
    				writer.write(outputLine + NL);
    				
    				File outputFile = new File(outputFilePath);
    				File parentFile = outputFile.getParentFile();
    				String parentFilePath = parentFile.getAbsolutePath();
    				String detectionResultsParentPath = parentFilePath.replace("output", "detection_results");
    				String[] parts = line.split(Annotation.ANNOTATION_DELIM_ESCAPED_FOR_USE_WITH_SPLIT);
    				String relMediaPath = parts[1];
    				String filename = relMediaPath.substring(6);
    				String[] filenameParts = filename.split("_");
    				String mediaId = filenameParts[0].replaceFirst("M","m");
    				String drFilename = mediaId + "_" + charId + ".txt";
    				String drPath = detectionResultsParentPath + FILESEP + drFilename;
    				//image_to_score|media\M283379_.jpg|t171198|annotations\m283379_c427749.txt|1
    				BogusAnnotationGenerator.generateAnnotationFile(drPath, charId, bundle.getCharacterNameForId(charId), "someStateId", "present");
    			}
    		}
    		reader.close();
    		writer.close();
    	}
    	catch (IOException ioe){
    		
    	}
    }
    public String getMediaIdFromRelPath(String relPath){
    	String[] parts = relPath.split("\\\\");
    	String mediaFilename = parts[1];
    	String[] filenameParts = mediaFilename.split("\\.");
    	String mediaFilenameWithCapM = filenameParts[0];
    	String mediaIdWithCapM = mediaFilenameWithCapM.split("_")[0];
    	String mediaId = mediaIdWithCapM.replaceAll("M", "m");
    	return mediaId;
    }
    public String generateScoredLine(String charId, String imageToScoreLine, String detectionResultsRelDir, MorphobankBundle bundle) throws AvatolCVException {
    	//image_scored|<relative path of mediafile>|<characterStateID>|<characterStateName>|<**relative path of annotation file>|<taxonID>|<***line number in annotations file>|<****score_confidence>
        //image_not_scored|<relative path of mediafile>|<taxonID>|
    	//image_to_score|<relative path of media file>|<taxonID>
    	String splitDelim = Annotation.ANNOTATION_DELIM_ESCAPED_FOR_USE_WITH_SPLIT;
    	String[] parts = imageToScoreLine.split(splitDelim);
    	String relMediaPath = parts[1];
    	String taxonId = parts[2];
    	String mediaId = getMediaIdFromRelPath(relMediaPath);
    	String annotationFileName = mediaId + "_" + charId + ".txt";
    	String relAnnotationPath = detectionResultsRelDir + FILESEP + annotationFileName;
    	String delim = Annotation.ANNOTATION_DELIM;
    	
    	Character ch = bundle.getSDDFile().getCharacterForId(charId);
    	List<CharacterState> states = ch.getCharacterStates();
    	int count = states.size();
    	Random r = new Random();
    	int randomIndex = r.nextInt(count);
    	CharacterState chosenState = states.get(randomIndex);
    	String chosenStateName = chosenState.getName();
    	String chosenStateId = chosenState.getFullId();
    	String randomScore = "" + getRandomInRange(0.6,1.0);
    	String line = "image_scored" + delim + relMediaPath + delim + chosenStateId + delim + chosenStateName + delim + relAnnotationPath + delim + taxonId + delim + "1" + delim + randomScore;
    	return line;
    }
    public double getRandomInRange(double min, double max){
    	double curVal = max + 1;
    	while(curVal < min || curVal > max){
    		Random r = new Random();
            curVal = r.nextDouble() % 1.0;
    	}
    	return curVal;
    }
    public String deriveOutputFilenameFromInputFile(File inputFile){
    	String inputFilename = inputFile.getName();
    	// sorted_input_data_c427760_M3 presence.txt
    	String filenameSansPrefix = inputFilename.replaceAll("sorted_input_data_", "");
    	String outputFilename = "sorted_output_data_" + filenameSansPrefix;
    	return outputFilename;
    }
}
