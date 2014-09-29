package edu.oregonstate.eecs.iis.avatolcv;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MorphobankBundle {
    private static final String FILESEP = System.getProperty("file.separator");
    public static final String INPUT_DIRNAME = "input";
    public static final String MEDIA_DIRNAME = "media";
    public static final String DETECTION_RESULTS_DIRNAME = "detection_results";
    
    private MorphobankSDDFile sddFile = null;
    private String dirName = null;
    private Annotations annotations = null;
    
    public MorphobankBundle(String dirName) throws MorphobankDataException {
    	this.dirName = dirName;
    	String sddPath = getSDDFilePath(dirName);
    	this.sddFile = new MorphobankSDDFile(sddPath);
    	erasePriorInputData();
        createInputDataDir();
    	this.annotations = new Annotations(this.sddFile.getPresenceAbsenceCharacterCells(),this.dirName, this.sddFile);
        emitCharacterInfo();
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
    	return this.annotations.getInputFilepathForCharacter(characterName);
    }
    public String getOutputFilePathnameForCharacter(String characterName) throws MorphobankDataException {
    	return this.annotations.getOutputFilepathForCharacter(characterName);
    }
    public String getRootDir(){
    	return this.dirName + FILESEP;
    }
    public String getInputDataDir(){
    	return this.dirName + FILESEP + INPUT_DIRNAME;
    }
    public void createInputDataDir(){
    	String inputDir = getInputDataDir();
    	File f = new File(inputDir);
    	f.mkdirs();
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
    public static void printString(String s){
    	System.out.println("the given string is " + s);
    }
    public List<String> getScorableCharacterNames(){
    	List<String> scorableCharacterNames = new ArrayList<String>();
    	List<String> presenceAbsenceCharNames = sddFile.getPresenceAbsenceCharacterNames();
    	for (String name : presenceAbsenceCharNames){
    		if (this.annotations.doesAnnotationInputFileExistForCharacterName(name)){
    			scorableCharacterNames.add(name);
    		}
    	}
    	return scorableCharacterNames;
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
}
