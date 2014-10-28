package edu.oregonstate.eecs.iis.avatolcv;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MorphobankBundle {
    private static final String FILESEP = System.getProperty("file.separator");
    private static final String NL = System.getProperty("line.separator");
    public static final String DETECTION_RESULTS_DIRNAME = "detection_results";
    
    private MorphobankSDDFile sddFile = null;
    private String dirName = null;
    private Annotations annotations = null;
    private Media media = null;
    private InputFiles inputFiles = null;
    
    public MorphobankBundle(String dirName) throws MorphobankDataException, AvatolCVException  {
    	this.dirName = dirName;
    	String sddPath = getSDDFilePath(dirName);
    	SPRTaxonIdMapper mapper = null;
    	if (isSpecimenPerRowBundle()){
    		mapper = new SPRTaxonIdMapper(sddPath);
    	}
    	this.sddFile = new MorphobankSDDFile(sddPath, mapper);
        
        this.media = new Media(this.dirName);
    	this.annotations = new Annotations(this.sddFile.getPresenceAbsenceCharacterCells(),this.dirName, this.sddFile, this.media);
    	this.inputFiles = new InputFiles(this.sddFile, this.annotations, this.media, this.dirName);
    	this.inputFiles.generateInputDataFiles();
        emitCharacterInfo();
        integrityCheck();
        //findImagesForBAT();
        
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
        	}
    	}
    	catch(IOException ioe){
    		ioe.printStackTrace();
    		throw new MorphobankDataException(" problem emitting character info - " + ioe.getMessage());
    	}
    }
    public String getDetectionResultsPathname(){
    	return this.dirName + FILESEP + DETECTION_RESULTS_DIRNAME + FILESEP;
    }
    public String getInputFilePathnameForCharacter(String characterName) throws MorphobankDataException {
    	return this.inputFiles.getInputFilepathForCharacter(characterName);
    }
    public String getOutputFilePathnameForCharacter(String characterName) throws MorphobankDataException {
    	return this.inputFiles.getOutputFilepathForCharacter(characterName);
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
    public List<String> getScorableTaxonNames() throws MorphobankDataException {
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
    public void filterInputs(List<String> charIds, String taxonId, String viewId, String algId) throws MorphobankDataException {
    	this.inputFiles.filterInputs(charIds, taxonId, viewId, algId);
    }
    public String getFilteredInputDirName(List<String> charIds, String taxonId, String viewId, String algId){
    	return this.inputFiles.getFilteredDirname(charIds, taxonId, viewId, algId, InputFiles.INPUT_DIRNAME);
    }

    public String getFilteredOutputDirName(List<String> charIds, String taxonId, String viewId, String algId){
    	return this.inputFiles.getFilteredDirname(charIds, taxonId, viewId, algId, InputFiles.OUTPUT_DIRNAME);
    }

    public String getFilteredDetectionResultsDirName(List<String> charIds, String taxonId, String viewId, String algId){
    	return this.inputFiles.getFilteredDirname(charIds, taxonId, viewId, algId, InputFiles.DETECTION_RESULTS_DIRNAME);
    }
    public String getViewIdForName(String name) throws AvatolCVException {
    	return this.sddFile.getViewIdForName(name);
    }
    public String getCharacterIdForName(String id){
    	return this.sddFile.getCharacterIdForName(id);
    } 
    public String getTaxonIdForName(String name) throws MorphobankDataException {
    	return this.sddFile.getTaxonIdForName(name);
    }
}
