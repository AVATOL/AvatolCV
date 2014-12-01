package edu.oregonstate.eecs.iis.avatolcv;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import edu.oregonstate.eecs.iis.avatolcv.mb.Annotation;

public class BogusOutputFileGenerator {
	private static final String FILESEP = System.getProperty("file.separator");
	private static final String NL = System.getProperty("line.separator");
    public static void main(String[] args){
    	String inputDir ="C:\\avatol\\git\\avatol_cv\\matrix_downloads\\BAT\\input\\DPM\\c427749c427751c427753c427754c427760\\v3540";
    	String outputDir = "C:\\avatol\\git\\avatol_cv\\matrix_downloads\\BAT\\output\\DPM\\c427749c427751c427753c427754c427760\\v3540";
    	String detectionResultsRelDir =  "detection_results\\DPM\\c427749c427751c427753c427754c427760\\v3540";
    	BogusOutputFileGenerator g = new BogusOutputFileGenerator(inputDir, outputDir, detectionResultsRelDir);
    }
    public BogusOutputFileGenerator(String inputDir, String outputDir, String detectionResultsRelDir){
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
    			generateBogusOutputFile(outputDir, inputFile, detectionResultsRelDir);
    		}
    	}
    }
    public String getCharIdFromFilename(String filename){
    	String filenameSansPrefix = filename.replaceAll("sorted_output_data_", "");
    	String[] parts = filenameSansPrefix.split("_");
    	String charId = parts[0];
    	return charId;
    }
    public void generateBogusOutputFile(String outputDir, File inputFile, String detectionResultsRelDir){
    	String outputFilename = deriveOutputFilenameFromInputFile(inputFile);
    	String outputFilePath = inputFile.getParent().replaceAll("input","output") + FILESEP + outputFilename;
    	String charId = getCharIdFromFilename(outputFilename);
    	try {
    		BufferedReader reader = new BufferedReader(new FileReader(inputFile.getAbsolutePath()));
    		BufferedWriter writer = new BufferedWriter(new FileWriter(outputFilePath));
    		String line = null;
    		while (null != (line = reader.readLine())){
    			if (line.startsWith("training")){
    				writer.write(line + NL);
    			}
    			else if (line.startsWith("image_to_score")){
    				String outputLine = generateScoredLine(charId, line, detectionResultsRelDir);
    				writer.write(outputLine + NL);
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
    public String generateScoredLine(String charId, String imageToScoreLine, String detectionResultsRelDir){
    	//image_scored|<relative path of mediafile>|<characterStateID>|<characterStateName>|<**relative path of annotation file>|<taxonID>|<***line number in annotations file>|<****score_confidence>
        //image_not_scored|<relative path of mediafile>|<taxonID>|
    	//image_to_score|<relative path of media file>|<taxonID>
    	String splitDelim = Annotation.ANNOTATION_DELIM_ESCAPED_FOR_USE_WITH_SPLIT;
    	String[] parts = imageToScoreLine.split(splitDelim);
    	String relMediaPath = parts[1];
    	String taxonId = parts[2];
    	String mediaId = getMediaIdFromRelPath(relMediaPath);
    	String annotationFileName = charId + "_" + mediaId + ".txt";
    	String relAnnotationPath = detectionResultsRelDir + FILESEP + annotationFileName;
    	String delim = Annotation.ANNOTATION_DELIM;
    	String line = "image_scored" + delim + relMediaPath + delim + "someCharStateId" + delim + "someCharStateName" + delim + relAnnotationPath + delim + taxonId + delim + "1" + delim + "0.7";
    	return line;
    }
    public String deriveOutputFilenameFromInputFile(File inputFile){
    	String inputFilename = inputFile.getName();
    	// sorted_input_data_c427760_M3 presence.txt
    	String filenameSansPrefix = inputFilename.replaceAll("sorted_input_data_", "");
    	String outputFilename = "sorted_output_data_" + filenameSansPrefix;
    	return outputFilename;
    }
}
