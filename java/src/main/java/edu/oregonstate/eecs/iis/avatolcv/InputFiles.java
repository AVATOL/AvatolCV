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

import edu.oregonstate.eecs.iis.avatolcv.mb.Annotation;
import edu.oregonstate.eecs.iis.avatolcv.mb.Annotations;
import edu.oregonstate.eecs.iis.avatolcv.mb.Character;
import edu.oregonstate.eecs.iis.avatolcv.mb.MatrixCell;
import edu.oregonstate.eecs.iis.avatolcv.mb.Media;
import edu.oregonstate.eecs.iis.avatolcv.mb.MorphobankDataException;
import edu.oregonstate.eecs.iis.avatolcv.mb.MorphobankSDDFile;

public class InputFiles {
	
	private static final String NL = System.getProperty("line.separator");
	private static final String FILESEP = System.getProperty("file.separator");
	
	private Annotations annotations = null;
	private Media media = null;
	private String bundleDir = null;
	private Hashtable<String, String> inputFilepathForCharacterName = new Hashtable<String,String>();
	private Hashtable<String, String> outputFilepathForCharacterName = new Hashtable<String,String>();
	private MorphobankSDDFile sddFile = null;
	private SummaryFile summaryFile = null;

    public InputFiles(MorphobankSDDFile sddFile, Annotations annotations, Media media, String bundleDir) throws AvatolCVException {
    	this.sddFile = sddFile;
    	this.annotations = annotations;
    	this.media = media;
    	this.bundleDir = bundleDir;
    	String inputDir = getInputDataDir();
    	File f = new File(inputDir);
    	f.mkdirs();
    	Platform.setPermissions(inputDir);
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
    	    	Platform.setPermissions(getInputDataDir());
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
        	if (file.getName().startsWith(DataIOFile.SORTED_INPUT_DATA_PREFIX)){
        		return true;
        	}
        }
        return false;
    }
    public List<MatrixCell> getNotPresentCells(List<MatrixCell> cells) throws AvatolCVException{
    	List<MatrixCell> notPresentCells = new ArrayList<MatrixCell>();
    	for (MatrixCell cell : cells){
    		String stateId = cell.getState();
    		if (stateId.equals("s")){
    			// this indicates unscored.
    		}
    		else {
    			String charId = cell.getCharId();
        		Character character = this.sddFile.getCharacterForId(charId);
        		if (character.isStateIdRepresentingAbsent(stateId)){
        			notPresentCells.add(cell);
        		}
    		}
    	}
    	return notPresentCells;
    }
    
    public void registerMediaAndTaxonInSummary(String mediaId, SummaryFile summary) throws AvatolCVException {
    	String viewId = this.sddFile.getViewIdForMediaId(mediaId);
    	String viewName = this.sddFile.getViewNameForId(viewId);
    	summary.addViewEntry(viewId, viewName);
    	String mediaPath = this.media.getMediaPathnameForMediaId(mediaId);
    	summary.addMediaEntry(mediaId, mediaPath);
		String taxonId = this.sddFile.getTaxonIdForMediaId(mediaId);
		String taxonName = this.sddFile.getTaxonNameForId(taxonId);
		summary.addTaxonEntry(taxonId, taxonName);
    }
    public String getSummaryFilePath(){
    	return this.bundleDir + FILESEP + DataIOFile.INPUT_DIRNAME + FILESEP + SummaryFile.SUMMARY_FILENAME;
    }
    public void generateSummaryFile() throws AvatolCVException {
    	this.summaryFile = new SummaryFile(getSummaryFilePath(), this.sddFile);
   		for (String charId : this.annotations.getCharactersTrained()){
			String charName = this.sddFile.getCharacterNameForId(charId);
			this.summaryFile.addCharacterEntry(charId, charName);
			
    		List<Annotation> annotationsForCharacter = this.annotations.getAnnotationsForCharacter(charId);
    		for (Annotation annotation : annotationsForCharacter){
    			String mediaId = annotation.getMediaId();
    			registerMediaAndTaxonInSummary(mediaId, this.summaryFile);
    		}
    		List<MatrixCell> allMatrixCellsForTrainedCharacter = sddFile.getPresenceAbsenceCellsForCharacter(charId);
    		// need to add training lines for "not present" scored training examples since they won't have generated annotations
    		List<MatrixCell> notPresentTrainingCells = getNotPresentCells(allMatrixCellsForTrainedCharacter);
    		for (MatrixCell notPresentCell : notPresentTrainingCells){
    			List<String> notPresentTrainingDataLines = this.sddFile.getNotPresentTrainingDataLines(notPresentCell);
    			for (String s : notPresentTrainingDataLines){
    				String relativeMediaPath = getRelativeMediaPathFromTrainingLine(s);
    				if (relativeMediaPath != null){
    					String mediaId = this.media.getMediaIdForRelativeMediaPath(relativeMediaPath);
    					registerMediaAndTaxonInSummary(mediaId, this.summaryFile);
    				}
    			}
    		}
    		
    		List<MatrixCell> matrixCellsToScore = getCellsToScore(allMatrixCellsForTrainedCharacter, annotationsForCharacter);
    		for (MatrixCell cell : matrixCellsToScore){
    			List<String> mediaIds = cell.getMediaIds();
    			for (String mediaId : mediaIds){
    				registerMediaAndTaxonInSummary(mediaId, this.summaryFile);
    			}
    		}
    	}
   		this.summaryFile.persist();
    }
	  // sorted_input_data_<charID>_charName.txt
    // for each annotation file, add line
    // training_data:media/<name_of_mediafile>:char_state:<pathname_of_annotation_file>:taxonID:lineNumber
    // for each media file which needs scoring, add line
    // image_to_score:media/<name_of_mediafile>:taxonID 
    public void generateInputDataFiles() throws AvatolCVException  {
    	generateSummaryFile();
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
    		// need to add training lines for "not present" scored training examples since they won't have generated annotations
    		List<MatrixCell> notPresentTrainingCells = getNotPresentCells(allMatrixCellsForTrainedCharacter);
    		for (MatrixCell notPresentCell : notPresentTrainingCells){
    			List<String> notPresentTrainingDataLines = this.sddFile.getNotPresentTrainingDataLines(notPresentCell);
    			for (String s : notPresentTrainingDataLines){
    				trainingDataLines.add(s);
    			}
    		}
    		
    		List<MatrixCell> matrixCellsToScore = getCellsToScore(allMatrixCellsForTrainedCharacter, annotationsForCharacter);
    		for (MatrixCell cell : matrixCellsToScore){
    			List<String> mediaIds = cell.getMediaIds();
    			for (String mediaId : mediaIds){
    				String mediaFilename = this.media.getMediaFilenameForMediaId(mediaId);
    				if (null != mediaFilename){
    					String taxonId = sddFile.getTaxonIdForMediaId(mediaId);
            			String scoringDataLine = DataIOFile.IMAGE_TO_SCORE_MARKER + Annotation.ANNOTATION_DELIM + "media" + FILESEP + mediaFilename + Annotation.ANNOTATION_DELIM + taxonId;
            			scoringDataLines.add(scoringDataLine);
    				}
    				
    			}
    		}
    		if (trainingDataLines.size() > 0){
    			String charName = sddFile.getCharacterNameForId(charId);
                String inputFilename = DataIOFile.SORTED_INPUT_DATA_PREFIX + charId + "_" + charName + ".txt";
                String inputFilePathname = this.bundleDir + FILESEP + DataIOFile.INPUT_DIRNAME + FILESEP + inputFilename;
                String outputFilename = "sorted_output_data_" + charId + "_" + charName + ".txt";
                String outputFilePathname = this.bundleDir + FILESEP + DataIOFile.INPUT_DIRNAME + FILESEP + outputFilename;
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
                    Platform.setPermissions(inputFilePathname);
                }
                catch(IOException ioe){
                	ioe.printStackTrace();
                	throw new AvatolCVException("could not write input file " + inputFilePathname);
                }
    		}
    	}
    }
    public boolean isCellScoredNotPresent(MatrixCell cell) throws AvatolCVException {
    	String characterStateId = cell.getState();
    	if (characterStateId.equals("s")){
    		return false;
    	}
    	String charId = cell.getCharId();
    	Character character = this.sddFile.getCharacterForId(charId);
    	if (character.isStateIdRepresentingAbsent(characterStateId)){
    		return true;
    	}
    	return false;
    }
    public List<MatrixCell> getCellsToScore(List<MatrixCell> allMatrixCellsForTrainedCharacter, List<Annotation> annotationsForCharacter) throws AvatolCVException {
		List<MatrixCell> cellsToScore = new ArrayList<MatrixCell>();
		for (MatrixCell cell : allMatrixCellsForTrainedCharacter){
			
			if (this.annotations.isMatrixCellRepresentedByAnyAnnotation(cell, annotationsForCharacter)){
				// it's a positive training example	
			}
			else if (isCellScoredNotPresent(cell)){
				// it's a negative training example.
			}
			else {
				// it's for scoring
				System.out.println("cell to score has state : " + cell.getState());
				cellsToScore.add(cell);
			}
		}
		return cellsToScore;
	}
    public String getInputDataDir(){
    	return this.bundleDir + FILESEP + DataIOFile.INPUT_DIRNAME;
    }
    public String getArchiveDirName(String priorMd5OfSDD){
    	return this.bundleDir + FILESEP + DataIOFile.INPUT_DIRNAME + "_" + priorMd5OfSDD;
    }
    public String getPathOfCharacterInputFile(String charId) throws MorphobankDataException{
    	String prefix = DataIOFile.SORTED_INPUT_DATA_PREFIX + charId;
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
    public String getFilteredDirname(List<String> charIds, String taxonId, String viewId, String algId, String target){
    	String algDirname = this.bundleDir + FILESEP + target + FILESEP + algId;
    	String charIdsDirname = getDirnameFromCharIds(charIds);
    	String newDir = algDirname + FILESEP + taxonId + FILESEP + charIdsDirname + FILESEP + viewId;
    	return newDir;
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
    		Platform.setPermissions(f.getAbsolutePath());
    	}
    }
    public Hashtable<String,InputFile> getInputFilesForCharacter(String path) throws AvatolCVException {
    	Hashtable<String, InputFile> inputFileForChar = new Hashtable<String,InputFile>();
    	File f = new File(path);
    	if (!f.isDirectory()){
    		System.out.println("getInputFiles given non-directory " + path);
    		throw new AvatolCVException("getInputFiles given non-directory " + path);
    	}
    	File[] files = f.listFiles();
    	for (int i = 0; i < files.length; i++){
    		File curFile = files[i];
    		if (curFile.getName().endsWith(".txt")){
    			// treat it as a valid input file
    			InputFile inputFile = new InputFile(curFile.getAbsolutePath(), this.bundleDir);
    			String charId = inputFile.getCharId();
    			inputFileForChar.put(charId,inputFile);
    		}
    	}
    	return inputFileForChar;
    }
 
    /*
     * make a dir name by cat'ing the char_ids that are valid simple, then subdir named for viewId, then can clean before filling
     */
    public void filterInputs(List<String> charIds, String taxonId, String viewId, String algId) throws AvatolCVException {
    	
    	String newInputDir = getFilteredDirname(charIds, taxonId, viewId, algId, DataIOFile.INPUT_DIRNAME);

    	String newOutputDir = getFilteredDirname(charIds, taxonId, viewId, algId, DataIOFile.OUTPUT_DIRNAME);
    	String detectionResultDir = getFilteredDirname(charIds, taxonId, viewId, algId, DataIOFile.DETECTION_RESULTS_DIRNAME);
    	cleanDir(newInputDir);
    	cleanDir(newOutputDir);
    	cleanDir(detectionResultDir);
    	this.summaryFile.filter(charIds, taxonId, viewId, newInputDir);
    	try {
    		for (String charId : charIds){
        		String pathOfCharacterInputFile = getPathOfCharacterInputFile(charId);
        		File tmpFile = new File(pathOfCharacterInputFile);
        		String filename = tmpFile.getName();
        		BufferedReader reader = new BufferedReader(new FileReader(pathOfCharacterInputFile));
        		String newFilePath = newInputDir + FILESEP + filename;
        		BufferedWriter writer = new BufferedWriter(new FileWriter(newFilePath));
        		String line = null;
        		while ((line = reader.readLine()) != null){
        			if (line.startsWith(DataIOFile.TRAINING_DATA_MARKER)){
        				if (isTrainingDataMediaOfTaxon(line, taxonId)){
        					if (isTrainingDataMediaOfView(line, viewId)){
            					writer.write(line + NL);
            				}
        				}
        			}
        			else if (line.startsWith(DataIOFile.IMAGE_TO_SCORE_MARKER)){
        				if (isImageToScoreMediaOfTaxon(line, taxonId)){
        					if (isImageToScoreMediaOfView(line, viewId)){
            					writer.write(line + NL);
            				}
        				}
        			}
        		}
        		
        		reader.close();
        		writer.close();
        		Platform.setPermissions(newFilePath);
        	}
    	}
    	catch(MorphobankDataException mde){
    		throw new AvatolCVException("Data problem trying tp create input files.",mde);
    	}
    	catch(IOException ioe){
    		throw new AvatolCVException("problem reading in existing input file to filter it: " +  ioe.getMessage());
    	}
    }
    public String getMediaIdFromLineToScore(String line){
    	String[] parts = line.split(Annotation.ANNOTATION_DELIM_ESCAPED_FOR_USE_WITH_SPLIT);
    	String mediaPath = parts[1];
    	String result = getMediaIdFromRelativeMediaPath(mediaPath);
    	return result;
    }
    public boolean isImageToScoreMediaOfTaxon(String line, String taxonId) throws AvatolCVException {
    	String mediaId = getMediaIdFromLineToScore(line);
    	return this.sddFile.isMediaOfTaxon(mediaId, taxonId);
    }
    public boolean isImageToScoreMediaOfView(String line, String viewId){
    	String mediaId = getMediaIdFromLineToScore(line);
    	return this.sddFile.isMediaOfView(mediaId, viewId);
    }
    public String getRelativeMediaPathFromTrainingLine(String line){
    	String[] parts = line.split(Annotation.ANNOTATION_DELIM_ESCAPED_FOR_USE_WITH_SPLIT);
    	String mediaPath = parts[1];
    	return mediaPath;
    }
    public String getMediaIdFromTrainingLine(String line){
    	String relativeMediaPath = getRelativeMediaPathFromTrainingLine(line);
    	String result = getMediaIdFromRelativeMediaPath(relativeMediaPath);
    	return result;
    }
    public String getMediaIdFromRelativeMediaPath(String mediaPath){
    	//String[] mediaParts = mediaPath.split(FILESEP); // breaks on Windows so use different approach
    	String mediaDirname = Media.MEDIA_DIRNAME;
    	int lengthOfMediaPlusFileSep = mediaDirname.length() + 1;
    	
    	String mediaFilename = mediaPath.substring(lengthOfMediaPlusFileSep,mediaPath.length());
    	String[] mediaFilenameParts = mediaFilename.split("_");
    	String prefix = mediaFilenameParts[0];
    	String mediaId = prefix.replace("M","m");
    	return mediaId;
    }
    public boolean isTrainingDataMediaOfView(String line, String viewId){
    	String mediaId = getMediaIdFromTrainingLine(line);
    	return this.sddFile.isMediaOfView(mediaId, viewId);
    }
    public boolean isTrainingDataMediaOfTaxon(String line, String taxonId) throws AvatolCVException {
    	String mediaId = getMediaIdFromTrainingLine(line);
    	return this.sddFile.isMediaOfTaxon(mediaId, taxonId);
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
	public String getOutputFilepathForCharacter(String charName, String algId) throws MorphobankDataException {
		String inputPath = outputFilepathForCharacterName.get(charName);
		if (null == inputPath){
			throw new MorphobankDataException("no outputFile pathname for character name: " + charName);
		}
		String outputPath = inputPath.replaceAll(DataIOFile.INPUT_DIRNAME, DataIOFile.OUTPUT_DIRNAME + FILESEP + algId);
		return outputPath;
	}
	public boolean doesAnnotationInputFileExistForCharacterName(String name){
		String pathname = inputFilepathForCharacterName.get(name);
		if (null == pathname){
			return false;
		}
		return true;
	}
	public List<String> getAnnotatedMediaIds(){
		return this.annotations.getMediaAnnotated();
    }
}
