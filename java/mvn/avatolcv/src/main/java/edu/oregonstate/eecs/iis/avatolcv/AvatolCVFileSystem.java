package edu.oregonstate.eecs.iis.avatolcv;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import edu.oregonstate.eecs.iis.avatolcv.core.DatasetInfo;

public class AvatolCVFileSystem {
    public static final String RESERVED_PREFIX = "avcv_";

	public static final String FILESEP = System.getProperty("file.separator");
	private static String avatolCVRootDir = null;
	private static String sessionsDir = null;
	private static String currentProjectDir = null;
    //private static String currentProjectUserAnswersDir = null;
    private static String sessionSummariesDir = null;

    private static String sessionID = null;
    private static String modulesDir = null;
    private static String datasetName = null;
    private static String datasourceName = null;
    
    public static void flushPriorSettings(){
    	currentProjectDir = null;
        //currentProjectUserAnswersDir = null;
        sessionID = null;
        datasetName = null;
        datasourceName = null;
    }
    //private static String uiContentXmlDir = null;
    //private static String charQuestionsDir = null;
	public static void setRootDir(String rootDir) throws AvatolCVException {
		avatolCVRootDir = rootDir;
		sessionsDir = avatolCVRootDir + FILESEP + "sessions";
		//System.out.println("sessionDataDir: " + sessionsDir);
		ensureDir(sessionsDir);
		modulesDir = avatolCVRootDir + FILESEP + "modules";
		ensureDir(modulesDir);
		sessionSummariesDir = avatolCVRootDir + FILESEP + "sessionSummaries";
		ensureDir(sessionSummariesDir);
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
    public static String getSessionSummariesDir(){
        return sessionSummariesDir; 
    }
    
	//
	// session
	//
    public static String getSessionFormatDate(){
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        String dateString = format.format(date);
        System.out.println(dateString);
        return dateString;
    }
    public static String createSessionID() throws AvatolCVException {
        List<String> sessionFilenames = getSessionFilenames();
        String date = getSessionFormatDate();
        List<String> idsFromToday = new ArrayList<String>();
        for (String filename : sessionFilenames){
            String[] parts = filename.split("\\.");
            String root = parts[0];
            if (root.startsWith(date)){
                idsFromToday.add(root);
            }
        }
        String nextIDForToday = getNextIDForDate(date, idsFromToday);
        return nextIDForToday;
    }
    public static String getNextIDForDate(String dateString, List<String> ids){
        if (ids.isEmpty()){
            return dateString + "_01";
        }
        List<String> numbersForToday = new ArrayList<String>();
        for (String s : ids){
            String[] parts = s.split("_");
            String number = parts[1];
            numbersForToday.add(number);
        }
        Collections.sort(numbersForToday);
        String finalNumberString = numbersForToday.get(numbersForToday.size() - 1);
        Integer numberAsInteger = new Integer(finalNumberString);
        int newValue = numberAsInteger.intValue() + 1;
        String newValueString = String.format("%02d", newValue);
        return dateString + "_" + newValueString;
    }
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
	    return sessionsDir + FILESEP + datasetName + FILESEP + sessionID;
	}
	public static List<String> getSessionFilenames() throws AvatolCVException {
        File sessionSummariesDirFile = new File(sessionSummariesDir);
        File[] files = sessionSummariesDirFile.listFiles();
        List<String> names = new ArrayList<String>();
        for (File f : files){
            String name = f.getName();
            if (name.equals(".") || name.equals("..")){
                // skip these
            }
            else {
                String nameRoot = name.replace(".txt","");
                names.add(nameRoot);
            }
        }
        return names;
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
        ensureDir(getNormalizedImageDir());
        ensureDir(getNormalizedImagesLargeDir());
        ensureDir(getNormalizedImagesThumbnailDir());
	    setSpecializedDataDir();
	}
	public static String getDatasetDir() throws AvatolCVException {
	    if (null == datasetName){
            throw new AvatolCVException("DatasetDir not valid until dataSet has been chosen");
        }
	    return sessionsDir + FILESEP + datasetName;
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
	public static String getNormalizedDataDirForDataset(String runID) throws AvatolCVException {
	    //C:\jed\avatol\git\avatol_cv\sessions\AVAToL Computer Vision Matrix\normalized\imageInfo
	    RunSummary rs = new RunSummary(runID);
	    String dataset = rs.getDataset();
	    String path = sessionsDir + FILESEP + dataset + FILESEP + "normalized" + FILESEP + "imageInfo";
	    return path;
	}
	public static String getNormalizedDataDirForSession(String runID) throws AvatolCVException {
        //C:\jed\avatol\git\avatol_cv\sessions\AVAToL Computer Vision Matrix\20151023_02\imageInfo
        RunSummary rs = new RunSummary(runID);
        String dataset = rs.getDataset();
        String path = sessionsDir + FILESEP + dataset + FILESEP + runID + FILESEP + "scoreInfo";
        return path;
    }
	public static String getNormalizedImageInfoDir() throws AvatolCVException {
	    return getNormalizedDataDir() + FILESEP + "imageInfo";
	}

    public static String getNormalizedImageDir() throws AvatolCVException {
        return getNormalizedDataDir() + FILESEP + "images";
    }
    public static String getNormalizedImagesLargeDir() throws AvatolCVException{
        return getNormalizedImageDir() + FILESEP + "large";
    }
    public static String getNormalizedImagesThumbnailDir() throws AvatolCVException{
        return getNormalizedImageDir() + FILESEP + "thumbnail";
    }
	public static void ensureDir(String path){
	    File f = new File(path);
	    f.mkdirs();
	}
	public static String getScoreIndexPath(String runID) throws AvatolCVException {
		return getSessionDir() + FILESEP + "scoreIndex.txt";
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
	    //currentProjectUserAnswersDir = currentProjectDir + FILESEP + "userAnswers";
	}
	public static String getMediaMetadataFilename(String parentDir, String mediaID)  throws AvatolCVException {
        File f = new File(parentDir);
        if (!f.isDirectory()){
            throw new AvatolCVException("normailzed media dir does not exist : " + parentDir);
        }
        File[] files = f.listFiles();
        int count = 0;
        for (File existingMediaFile : files){
            String filename = existingMediaFile.getName();
            String[] parts = filename.split("\\.");
            String root = parts[0];
            String[] rootParts = root.split("_");
            String curMediaID = rootParts[0];
            if (mediaID.equals(curMediaID)){
                count++;
            }
        }
        return mediaID + "_" + (count + 1) + ".txt";
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
