package edu.oregonstate.eecs.iis.avatolcv;

import java.io.File;

import edu.oregonstate.eecs.iis.avatolcv.core.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.generic.DatasetInfo;

public class AvatolCVFileSystem {
	public static final String FILESEP = System.getProperty("file.separator");
	private static String avatolCVRootDir = null;
	private static String sessionsDir = null;
	private static String currentProjectDir = null;
    private static String currentProjectUserAnswersDir = null;
    private static String sessionID = null;
    private static String modulesDir = null;
    private static String datasetName = null;
    private static String datasourceName = null;
    //private static String uiContentXmlDir = null;
    //private static String charQuestionsDir = null;
	public AvatolCVFileSystem(String rootDir) throws AvatolCVException {
		avatolCVRootDir = rootDir;
		sessionsDir = avatolCVRootDir + FILESEP + "sessions";
		//System.out.println("sessionDataDir: " + sessionsDir);
		ensureDir(sessionsDir);
		modulesDir = avatolCVRootDir + FILESEP + "modules";
		ensureDir(modulesDir);
		//uiContentXmlDir = avatolCVRootDir + FILESEP + "uiContentXml";
		//System.out.println("uiContentXmlDir: " + uiContentXmlDir);
		//ensureDirIsPresent(uiContentXmlDir);
		//charQuestionsDir = uiContentXmlDir + FILESEP + "characterQuestions";
		//System.out.println("charQuestionsDir: " + charQuestionsDir);
		//ensureDirHasContents(charQuestionsDir, "xml");
	}
	//
	//  These are defined at construction time
	//
	public static String getAvatolCVRootDir(){
		return avatolCVRootDir;
	}
	public static String getModulesDir(){
	    return modulesDir;
	}
    public static String getSessionsRoot(){
        return sessionsDir;
    }
	//
	// session
	//
	public static void setSessionID(String id) throws AvatolCVException {
        sessionID = id; 
        
    }
	public static String getSessionDir() throws AvatolCVException {
	    if (null == sessionID){
	        throw new AvatolCVException("SessionDir not valid until sessionID has been set");
	    }
	    if (null == datasetName){
            throw new AvatolCVException("SessionDir not valid until dataSet has been chosen");
        }
	    return avatolCVRootDir + FILESEP + datasetName + FILESEP + sessionID;
	}
	//
	// datasource
	//
	public static void setDatasourceName(String name){
	    datasourceName = name;
	}
	
	//
	// dataset
	//
	public static void setChosenDataset(DatasetInfo di) throws AvatolCVException {
	    datasetName = di.getName();
	    ensureDir(getSessionDir());
	    ensureDir(getDatasetDir());
	    ensureDir(getNormalizedDataDir());
	    ensureDir(getNormalizedImageInfoDir());
	    setSpecializedDataDir();
	}
	public static String getDatasetDir() throws AvatolCVException {
	    if (null == datasetName){
            throw new AvatolCVException("DatasetDir not valid until dataSet has been chosen");
        }
	    return avatolCVRootDir + FILESEP + datasetName;
	}
	
	public static void setSpecializedDataDir() throws AvatolCVException {
	    if (null == datasourceName){
	        throw new AvatolCVException("SpecializedDir cannot be set until datasource has been chosen");
	    }
	    ensureDir(getSpecializedDataDir());
	    ensureDir(getSpecializedImageInfoDir());
	}
	public static String getSpecializedDataDir() throws AvatolCVException {
	    return getDatasetDir() + FILESEP + datasourceName;
	}
    public static String getSpecializedImageInfoDir() throws AvatolCVException {
        return getSpecializedDataDir() + FILESEP + "imageInfo";
    }
    
	public static String getNormalizedDataDir() throws AvatolCVException {
	    return getDatasetDir() + FILESEP + "normalized";
	}
	public static String getNormalizedImageInfoDir() throws AvatolCVException {
	    return getNormalizedDataDir() + FILESEP + "imageInfo";
	}

	public static void ensureDir(String path){
	    File f = new File(path);
	    f.mkdirs();
	}
	public static void ensureDirHasContents(String path, String fileSuffix) throws AvatolCVException {
	    File dir = new File(path);
	    File[] files = dir.listFiles();
	    int matchCount = 0;
	    for (File file : files){
	        if (file.getName().endsWith(fileSuffix)){
	            matchCount++;
	        }
	    }
	    if (matchCount == 0){
	        throw new AvatolCVException("expected files ending in '.xml' tp be present in " + path);
	    }
	}
	public static void ensureDirIsPresent(String path) throws AvatolCVException {
        File dir = new File(path);
        if (!dir.isDirectory()){
            throw new AvatolCVException("expected directory to exist: " + path);
        }
    }
	public static void setCurrentProject(String name){
	    currentProjectDir = sessionsDir + FILESEP + name;
	    ensureDir(currentProjectDir);
	    currentProjectUserAnswersDir = currentProjectDir + FILESEP + "userAnswers";
	}
	/*public static String getUserAnswerDir() throws AvatolCVException {
	    if (null == currentProjectUserAnswersDir){
	        throw new AvatolCVException("getUserAnswerDir() called prior to setCurrentProject()");
	    }
	    return currentProjectUserAnswersDir;
	}*/
	/*
	public static String getCharacterQuestionsDir(){
	    return charQuestionsDir;
	}
	*/
}
