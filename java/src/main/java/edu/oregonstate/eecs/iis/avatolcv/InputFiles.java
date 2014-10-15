package edu.oregonstate.eecs.iis.avatolcv;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;

public class InputFiles {
	private static final String NL = System.getProperty("line.separator");
	private static final String FILESEP = System.getProperty("file.separator");
    public static final String INPUT_DIRNAME = "input";
    public static final String OUTPUT_DIRNAME = "output";
    public static final String DETECTION_RESULTS_DIRNAME = "detection_results";
	
	private Annotations annotations = null;
	private Media media = null;
	private String bundleDir = null;
	private Hashtable<String, String> inputFilepathForCharacterName = new Hashtable<String,String>();
	private Hashtable<String, String> outputFilepathForCharacterName = new Hashtable<String,String>();
	private MorphobankSDDFile sddFile = null;

    public InputFiles(MorphobankSDDFile sddFile, Annotations annotations, Media media, String bundleDir) throws AvatolCVException {
    	this.sddFile = sddFile;
    	this.annotations = annotations;
    	this.media = media;
    	this.bundleDir = bundleDir;
    	String inputDir = getInputDataDir();
    	File f = new File(inputDir);
    	f.mkdirs();
    	reactToChangingSDDFile();
    	//erasePriorInputData
    }
    public void reactToChangingSDDFile() throws AvatolCVException {
    	String priorMd5OfSDD = getPriorMd5OfSDD();
    	String newMd5OfSDD = getMd5OfSDD();
    	if (null == priorMd5OfSDD){
    		persistMd5(newMd5OfSDD);
    	}
    	else {
    		if (priorMd5OfSDD.equals(newMd5OfSDD)){
    			//data hasn't changed, leave input dir intact
    		}
    		else{
    			//data has changed, rename input dir with priorMd5Name
    			String newDirName = getArchiveDirName(priorMd5OfSDD);
    			File inputDirFile = new File(getInputDataDir());
    			File newDirFile = new File(newDirName);
    			inputDirFile.renameTo(newDirFile);
    			persistMd5(newMd5OfSDD);
    			File f = new File(getInputDataDir());
    	    	f.mkdirs();
    		}
    	}
    }
    public void persistMd5(String value) throws AvatolCVException {
    	String pathname = getMd5Path();
    	try {
    		BufferedWriter writer = new BufferedWriter(new FileWriter(pathname));
    		writer.write(value);
    		writer.close();
    	}
    	catch(IOException ioe){
    		throw new AvatolCVException("problem persisting sdd md5: " + ioe.getMessage());
    	}
    	
    }
    public String getMd5OfSDD() throws AvatolCVException {
    	try {
        	MessageDigest m = MessageDigest.getInstance("MD5");
        	this.sddFile.feedMessageDigestSDDContent(m);
        	byte[] digest = m.digest();
        	BigInteger bigInt = new BigInteger(1,digest);
        	String hashtext = bigInt.toString(16);
        	return hashtext;
    	}
    	catch(NoSuchAlgorithmException e){
    		throw new AvatolCVException("Could not compute MD5 of SDD : " + e.getMessage());
    	}
    }
    public String getMd5Path(){
    	return getInputDataDir() + FILESEP + "sddMD5.txt";
    }
    public String getPriorMd5OfSDD() throws AvatolCVException {
    	try {
    		String pathOfMd5File = getMd5Path();
        	File f = new File(pathOfMd5File);
        	if (f.exists()){
        		BufferedReader reader = new BufferedReader(new FileReader(pathOfMd5File));
        		String md5 = reader.readLine();
        		reader.close();
        		return md5;
        	}
        	else {
        		return null;
        	}
    	}
    	catch(IOException ioe){
    		throw new AvatolCVException("could not read input file md5");
    	}
    	
    }
    public void erasePriorInputData(){
        String inputDataDir = getInputDataDir();
        File f = new File(inputDataDir);
        if (f.isDirectory()){
        	File[] files = f.listFiles();
        	for (int i = 0; i < files.length; i++){
        		File someFile = files[i];
        		if (someFile.getName().endsWith(".txt")){
        			someFile.delete();
        		}
        	}
        }
    }
    public boolean doInputDataFilesAlreadyExist(){
    	String inputDataDir = getInputDataDir();
        File f = new File(inputDataDir);
        File[] files = f.listFiles();
        for (File file : files){
        	if (file.getName().startsWith("sorted_input_data_")){
        		return true;
        	}
        }
        return false;
    }
	  // sorted_input_data_<charID>_charName.txt
    // for each annotation file, add line
    // training_data:media/<name_of_mediafile>:char_state:<pathname_of_annotation_file>:taxonID:lineNumber
    // for each media file which needs scoring, add line
    // image_to_score:media/<name_of_mediafile>:taxonID 
    public void generateInputDataFiles() throws MorphobankDataException {
    	// HAD TO COMMENT THE FOLLOWING OUT DUE TO maps not getting setup properly
    	//if (doInputDataFilesAlreadyExist()){
    	//	return;
    	//}
    	for (String charId : this.annotations.getCharactersTrained()){
    		List<String> trainingDataLines = new ArrayList<String>();
    		List<String> scoringDataLines = new ArrayList<String>();
    		List<Annotation> annotationsForCharacter = this.annotations.getAnnotationsForCharacter(charId);
    		for (Annotation annotation : annotationsForCharacter){
    			String mediaFilename = this.media.getMediaFilenameForMediaId(annotation.getMediaId());
    			if (null != mediaFilename){
    				String taxonId = sddFile.getTaxonIdForMediaId(annotation.getMediaId());
        			String trainingDataLine = annotation.getTrainingDataLine(mediaFilename, taxonId);
        			trainingDataLines.add(trainingDataLine);
    			}
    		}
    		List<MatrixCell> allMatrixCellsForTrainedCharacter = sddFile.getPresenceAbsenceCellsForCharacter(charId);
    		List<MatrixCell> matrixCellsToScore = getCellsToScore(allMatrixCellsForTrainedCharacter, annotationsForCharacter);
    		for (MatrixCell cell : matrixCellsToScore){
    			List<String> mediaIds = cell.getMediaIds();
    			for (String mediaId : mediaIds){
    				String mediaFilename = this.media.getMediaFilenameForMediaId(mediaId);
    				if (null != mediaFilename){
    					String taxonId = sddFile.getTaxonIdForMediaId(mediaId);
            			String scoringDataLine = "image_to_score" + Annotation.ANNOTATION_DELIM + "media/" + mediaFilename + Annotation.ANNOTATION_DELIM + taxonId;
            			scoringDataLines.add(scoringDataLine);
    				}
    				
    			}
    		}
    		if (trainingDataLines.size() > 0){
    			String charName = sddFile.getCharacterNameForId(charId);
                String inputFilename = "sorted_input_data_" + charId + "_" + charName + ".txt";
                String inputFilePathname = this.bundleDir + FILESEP + INPUT_DIRNAME + FILESEP + inputFilename;
                String outputFilename = "sorted_output_data_" + charId + "_" + charName + ".txt";
                String outputFilePathname = this.bundleDir + FILESEP + INPUT_DIRNAME + FILESEP + outputFilename;
                this.inputFilepathForCharacterName.put(charName, inputFilePathname);
                this.outputFilepathForCharacterName.put(charName, outputFilePathname);
                File f = new File(inputFilePathname);
                if (f.exists()){
                    f.delete();
                }
                try {
                	BufferedWriter writer = new BufferedWriter(new FileWriter(inputFilePathname));
                    
                    for (String trainingLine : trainingDataLines){
                    	writer.write(trainingLine + NL);
                    }
                    for (String scoringLine : scoringDataLines){
                    	writer.write(scoringLine + NL);
                    }
                    writer.close();
                }
                catch(IOException ioe){
                	ioe.printStackTrace();
                	throw new MorphobankDataException("could not write input file " + inputFilePathname);
                }
    		}
    	}
    }
    public List<MatrixCell> getCellsToScore(List<MatrixCell> allMatrixCellsForTrainedCharacter, List<Annotation> annotationsForCharacter){
		List<MatrixCell> cellsToScore = new ArrayList<MatrixCell>();
		for (MatrixCell cell : allMatrixCellsForTrainedCharacter){
			if (this.annotations.isMatrixCellRepresentedByAnyAnnotation(cell, annotationsForCharacter)){
				// it's a training example	
			}
			else {
				// it's for scoring
				cellsToScore.add(cell);
			}
		}
		return cellsToScore;
	}
    public String getInputDataDir(){
    	return this.bundleDir + FILESEP + INPUT_DIRNAME;
    }
    public String getArchiveDirName(String priorMd5OfSDD){
    	return this.bundleDir + FILESEP + INPUT_DIRNAME + "_" + priorMd5OfSDD;
    }
    public String getPathOfCharacterInputFile(String charId) throws MorphobankDataException{
    	String prefix = "sorted_input_data_" + charId;
    	String inputDataDir = getInputDataDir();
    	File inputDataDirFile = new File(inputDataDir);
    	File[] files = inputDataDirFile.listFiles();
    	for (File f : files){
    		if (f.getName().startsWith(prefix)){
    			return f.getAbsolutePath();
    		}
    	}
    	throw new MorphobankDataException("no initial pass input file starting with " + prefix + " found.");
    }
    public String getDirnameFromCharIds(List<String> charIds){
    	Collections.sort(charIds);
    	StringBuilder sb = new StringBuilder();
    	for (String charId : charIds){
    		sb.append(charId);
    	}
    	return ""+sb;
    }
    public String getFilteredDirname(List<String> charIds, String viewId, String algId, String target){
    	String algDirname = this.bundleDir + FILESEP + target + FILESEP + algId;
    	String charIdsDirname = getDirnameFromCharIds(charIds);
    	String newInputDir = algDirname + FILESEP + charIdsDirname + FILESEP + viewId;
    	return newInputDir;
    }
    public void cleanDir(String path){
    	File f = new File(path);
    	if (f.exists()){
    		// clean out the prior contents
    		File[] files = f.listFiles();
    		for (File file : files){
    			file.delete();
    		}
    	}
    	else {
    		f.mkdirs();
    	}
    }
    /*
     * make a dir name by cat'ing the char_ids that are valid simple, then subdir named for viewId, then can clean before filling
     */
    public void filterInputsByCharsAndView(List<String> charIds, String viewId, String algId) throws MorphobankDataException {
    	
    	String newInputDir = getFilteredDirname(charIds, viewId, algId, INPUT_DIRNAME);
    	String newOutputDir = getFilteredDirname(charIds, viewId, algId, OUTPUT_DIRNAME);
    	String detectionResultDir = getFilteredDirname(charIds, viewId, algId, DETECTION_RESULTS_DIRNAME);
    	cleanDir(newInputDir);
    	cleanDir(newOutputDir);
    	cleanDir(detectionResultDir);
    	try {
    		for (String charId : charIds){
        		String pathOfCharacterInputFile = getPathOfCharacterInputFile(charId);
        		File tmpFile = new File(pathOfCharacterInputFile);
        		String filename = tmpFile.getName();
        		BufferedReader reader = new BufferedReader(new FileReader(pathOfCharacterInputFile));
        		BufferedWriter writer = new BufferedWriter(new FileWriter(newInputDir + FILESEP + filename));
        		String line = null;
        		while ((line = reader.readLine()) != null){
        			if (line.startsWith("training_data")){
        				if (isTrainingDataMediaOfView(line, viewId)){
        					writer.write(line + NL);
        				}
        			}
        			else if (line.startsWith("image_to_score")){
        				if (isImageToScoreMediaOfView(line, viewId)){
        					writer.write(line + NL);
        				}
        			}
        		}
        		
        		reader.close();
        		writer.close();
        	}
    	}
    	catch(IOException ioe){
    		throw new MorphobankDataException("problem reading in existing input file to filter it: " +  ioe.getMessage());
    	}
    }
    public boolean isImageToScoreMediaOfView(String line, String viewId){
    	String[] parts = line.split("\\" +Annotation.ANNOTATION_DELIM);
    	String mediaPath = parts[1];
    	String[] mediaParts = mediaPath.split("/");
    	String mediaFilename = mediaParts[1];
    	String[] mediaFilenameParts = mediaFilename.split("_");
    	String prefix = mediaFilenameParts[0];
    	String mediaId = prefix.replace("M","m");
    	return this.sddFile.isMediaOfView(mediaId, viewId);
    }
    public boolean isTrainingDataMediaOfView(String line, String viewId){
    	String[] parts = line.split("\\" + Annotation.ANNOTATION_DELIM);
    	String mediaPath = parts[1];
    	String[] mediaParts = mediaPath.split("/");
    	String mediaFilename = mediaParts[1];
    	String[] mediaFilenameParts = mediaFilename.split("_");
    	String prefix = mediaFilenameParts[0];
    	String mediaId = prefix.replace("M","m");
    	return this.sddFile.isMediaOfView(mediaId, viewId);
    }
    //return "training_data|media/" + mediaFilename + "|" + 
    //charState + "|" + charStateText + "|" + pathname + "|" + taxonId + "|" + lineNumber;  
    
	public String getInputFilepathForCharacter(String charName) throws MorphobankDataException {
		String path = inputFilepathForCharacterName.get(charName);
		if (null == path){
			throw new MorphobankDataException("no inputFile pathname for character name: " + charName);
		}
		return path;
	}
	public String getOutputFilepathForCharacter(String charName) throws MorphobankDataException {
		String path = outputFilepathForCharacterName.get(charName);
		if (null == path){
			throw new MorphobankDataException("no outputFile pathname for character name: " + charName);
		}
		return path;
	}
	public boolean doesAnnotationInputFileExistForCharacterName(String name){
		String pathname = inputFilepathForCharacterName.get(name);
		if (null == pathname){
			return false;
		}
		return true;
	}
	
}
