package edu.oregonstate.eecs.iis.avatolcv.mb;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import edu.oregonstate.eecs.iis.avatolcv.AvatolCVProperties;
import edu.oregonstate.eecs.iis.avatolcv.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.DataIOFile;
import edu.oregonstate.eecs.iis.avatolcv.InputFile;
import edu.oregonstate.eecs.iis.avatolcv.InputFiles;
import edu.oregonstate.eecs.iis.avatolcv.OutputFile;
import edu.oregonstate.eecs.iis.avatolcv.OutputFiles;
import edu.oregonstate.eecs.iis.avatolcv.Platform;
import edu.oregonstate.eecs.iis.avatolcv.TrainingDataPartitioner;

public class MorphobankBundle {
    private static final String FILESEP = System.getProperty("file.separator");
    private static final String NL = System.getProperty("line.separator");
    
    private MorphobankSDDFile sddFile = null;
    private String dirName = null;
    private Annotations annotations = null;
    private Media media = null;
    private InputFiles inputFiles = null;
    private AvatolCVProperties properties = null;
    
    public MorphobankBundle(String dirName) throws MorphobankDataException, AvatolCVException  {
    	this.dirName = dirName;
    	this.properties = new AvatolCVProperties(this.dirName);
    	String sddPath = getSDDFilePath(dirName);
    	SPRTaxonIdMapper mapper = null;
    	if (isSpecimenPerRowBundle()){
    		mapper = new SPRTaxonIdMapper(sddPath);
    	}
        this.media = new Media(this.dirName);
    	this.sddFile = new MorphobankSDDFile(sddPath, mapper, this.media);
        TrainingDataPartitioner tdp = new TrainingDataPartitioner(this);
        String annotationsForTrainingDir = tdp.partitionTrainingData();
    	this.annotations = new Annotations(this.sddFile.getPresenceAbsenceCharacterCells(),this.dirName, this.sddFile, this.media, annotationsForTrainingDir);
    	this.inputFiles = new InputFiles(this.sddFile, this.annotations, this.media, this.dirName);
    	this.inputFiles.generateInputDataFiles();
        emitCharacterInfo();
        integrityCheck();
        //findImagesForBAT();
        
    }
    public AvatolCVProperties getSystemProperties(){
    	return this.properties;
    }
    public List<String> getViewNames(){
		return this.sddFile.getViewNames();
	}
    public boolean isSpecimenPerRowBundle(){
    	String path = this.dirName + FILESEP + "specimenPerRowMarker.txt";
    	File f = new File(path);
    	if (f.exists()){
    		return true;
    	}
    	return false;
    }
    //public void findImagesForBAT() throws MorphobankDataException {
    //	List<String> mediaIds = sddFile.getMatrix().getImageNamesForSpecialCase();
    //	for (String mediaId : mediaIds){
    //		String name = this.media.getMediaFilenameForMediaId(mediaId);
    		//System.out.println("BAT image candidate : " + name);
    //	}
    	
    //}
    public void integrityCheck() throws MorphobankDataException {
    	try {
    		String pathname = this.dirName + FILESEP + "integrityCheck.txt";
        	File f = new File(pathname);
        	if (f.exists()){
        		f.delete();
        	}
        	BufferedWriter writer = new BufferedWriter(new FileWriter(pathname));
        	List<Character> characters = this.sddFile.getPresenceAbsenceCharacters();
        	for (Character character : characters){
        		writer.write("character " + character.getId() + " " + character.getName() + NL);
            	List<MatrixCell> cellsForCharacter = this.sddFile.getPresenceAbsenceCellsForCharacter(character.getId());
            	for (MatrixCell cell : cellsForCharacter){
            		List<String> mediaIdsForCell = cell.getMediaIds();
            		for (String mediaId : mediaIdsForCell){
            			if (!this.media.isMediaIdBackedByMediaFile(mediaId)){
            				writer.write("...media missing : " + mediaId + NL);
            			}
            		}
            	}
        	}
        	writer.close();
    	}
    	catch(IOException ioe){
    		ioe.printStackTrace();
    		throw new MorphobankDataException("problem during integrity check: " + ioe.getMessage());
    	}
    	
    	 
    }
    public void emitCharacterInfo() throws MorphobankDataException {
    	String tempCharsDir = dirName + FILESEP + "tempCharacters";
    	File dir = new File(tempCharsDir);
    	dir.mkdirs();
    	try {
    		List<Character> characters = sddFile.getPresenceAbsenceCharacters();
        	for (Character character : characters){
        		String name = character.getName();
        		String noSlashName = name.replace("/","slash");
            	noSlashName = noSlashName.replace("\\","backslash");
        		String pathname = tempCharsDir + FILESEP + noSlashName + ".txt";
        		File f = new File(pathname);
        		if (f.exists()){
        			f.delete();
        		}
        		BufferedWriter writer = new BufferedWriter(new FileWriter(pathname));
        		writer.write(character.toString());
        		writer.close();
        		Platform.setPermissions(pathname);
        	}
    	}
    	catch(IOException ioe){
    		ioe.printStackTrace();
    		throw new MorphobankDataException(" problem emitting character info - " + ioe.getMessage());
    	}
    }
    public String getDetectionResultsPathname(String characterId, String algId){
    	return this.dirName + FILESEP + DataIOFile.DETECTION_RESULTS_DIRNAME + FILESEP + algId + FILESEP + characterId + FILESEP;
    }
    public String getInputFilePathnameForCharacter(String characterName) throws MorphobankDataException {
    	return this.inputFiles.getInputFilepathForCharacter(characterName);
    }
    public String getDestinationOutputFilePathnameForCharacter(String characterName, String algId) throws MorphobankDataException {
    	return this.inputFiles.getDestinationOutputFilepathForCharacter(characterName, algId);
    }
    public String getTempDirForAlg(String algId){
    	return this.dirName + FILESEP + DataIOFile.OUTPUT_DIRNAME + FILESEP + algId + FILESEP;
    }
    public String getRootDir(){
    	return this.dirName + FILESEP;
    }
    public String getInputDataDir(){
    	return this.inputFiles.getInputDataDir();
    }
    
    
    public static void printString(String s){
    	System.out.println("the given string is " + s);
    }
    public List<String> getScorableCharacterNames(){
    	List<String> scorableCharacterNames = new ArrayList<String>();
    	List<String> presenceAbsenceCharNames = sddFile.getPresenceAbsenceCharacterNames();
    	for (String name : presenceAbsenceCharNames){
    		if (this.inputFiles.doesAnnotationInputFileExistForCharacterName(name)){
    			scorableCharacterNames.add(name);
    		}
    	}
    	return scorableCharacterNames;
    }
    public List<String> getScorableTaxonNames() throws AvatolCVException {
    	List<String> result = new ArrayList<String>();
    	List<String> annotatedMediaIds = this.inputFiles.getAnnotatedMediaIds();
    	for (String mediaId : annotatedMediaIds){
    		String taxonId = this.sddFile.getTaxonIdForMediaId(mediaId);
    		String taxonName = this.sddFile.getTaxonNameForId(taxonId);
    		if (!result.contains(taxonName)){
        		result.add(taxonName);
    		}
    	}
    	return result;
    }
    public String getSDDFilePath(String bundleDirPath) throws MorphobankDataException {
    	String path = "unknown";
        
        File bundleDir = new File(bundleDirPath);
        String[] filenames = bundleDir.list();
        
        for (int i = 0; i < filenames.length; i++){
        	String candidate = filenames[i];
        	if (candidate.endsWith("sdd.xml")){
        		path = bundleDirPath + FILESEP  + candidate;
        	}
        }
           
        if ("unknown".equals(path)){
        	throw new MorphobankDataException("no sdd.xml file present in directory " + bundleDirPath);
        }
        return path;
    }
    public void filterInputs(List<String> charIds, String viewId, String algId) throws AvatolCVException {
    	this.inputFiles.filterInputs(charIds, viewId, algId);
    }
    public String getFilteredInputDirName(List<String> charIds, String viewId, String algId){
    	return this.inputFiles.getFilteredDirname(charIds, viewId, algId, DataIOFile.INPUT_DIRNAME);
    }

    public String getFilteredOutputDirName(List<String> charIds, String viewId, String algId){
    	return this.inputFiles.getFilteredDirname(charIds, viewId, algId, DataIOFile.OUTPUT_DIRNAME);
    }

    public String getFilteredDetectionResultsDirName(List<String> charIds, String viewId, String algId){
    	return this.inputFiles.getFilteredDirname(charIds, viewId, algId, DataIOFile.DETECTION_RESULTS_DIRNAME);
    }
    public String getViewIdForName(String name) throws AvatolCVException {
    	return this.sddFile.getViewIdForName(name);
    }
    public String getCharacterIdForName(String name){
    	return this.sddFile.getCharacterIdForName(name);
    } 
    public String getCharacterNameForId(String id){
    	return this.sddFile.getCharacterNameForId(id);
    } 
    public String getTaxonIdForName(String name) throws AvatolCVException {
    	return this.sddFile.getTaxonIdForName(name);
    }
    public String getTaxonNameForId(String id) throws AvatolCVException {
    	return this.sddFile.getTaxonNameForId(id);
    }
    public Hashtable<String,InputFile> getInputFilesForCharacter(String path) throws AvatolCVException {
    	return this.inputFiles.getInputFilesForCharacter(path);
    }
    public Hashtable<String,OutputFile> getOutputFilesForCharacter(String path) throws AvatolCVException {
    	OutputFiles outputFiles = new OutputFiles(path, this.dirName);
    	return outputFiles.getOutputFilesForCharacter();
    }
    public MorphobankSDDFile getSDDFile(){
    	return this.sddFile;
    }
}
