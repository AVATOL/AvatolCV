package edu.oregonstate.eecs.iis.avatolcv;

import java.io.File;
import java.util.List;

public class MorphobankBundle {
    private static final String FILESEP = System.getProperty("file.separator");
    public static final String INPUT_DIRNAME = "input";
    public static final String MEDIA_DIRNAME = "media";
    
    private MorphobankSDDFile sddFile = null;
    private String dirName = null;
    private Annotations annotations = null;
    
    public MorphobankBundle(String dirName) throws MorphobankDataException {
    	this.dirName = dirName;
    	String sddPath = getSDDFilePath(dirName);
    	this.sddFile = new MorphobankSDDFile(sddPath);
    	this.annotations = new Annotations(this.sddFile.getPresenceAbsenceCharacterCells(),this.dirName, this.sddFile);
        erasePriorInputData();
        createInputDataDir();
    }
  
    public String getInputDataDir(){
    	return this.dirName + FILESEP + "input";
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
    public List<String> getPresenceAbsenceCharacterNames(){
    	return sddFile.getPresenceAbsenceCharacterNames();
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
