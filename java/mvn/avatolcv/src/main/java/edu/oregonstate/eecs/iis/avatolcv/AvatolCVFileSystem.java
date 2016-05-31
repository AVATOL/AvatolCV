package edu.oregonstate.eecs.iis.avatolcv;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import edu.oregonstate.eecs.iis.avatolcv.algorithm.Algorithm;
import edu.oregonstate.eecs.iis.avatolcv.core.ImageInfo;
import edu.oregonstate.eecs.iis.avatolcv.core.TrainingInfoFile;
import edu.oregonstate.eecs.iis.avatolcv.scoring.HoldoutInfoFile;
import edu.oregonstate.eecs.iis.avatolcv.scoring.ScoresInfoFile;
import edu.oregonstate.eecs.iis.avatolcv.scoring.ScoringInfoFile;
import edu.oregonstate.eecs.iis.avatolcv.session.DatasetInfo;
import edu.oregonstate.eecs.iis.avatolcv.session.RunSummary;
import edu.oregonstate.eecs.iis.avatolcv.util.ClassicSplitter;

/**
 * 
 * @author admin-jed
 *
 * Encapsulates the directory layout for everything under avatolCV: modules, datasets, sessions, etc.
 * Also handles sessionID generation as that hinges on existing files.  The idea was that if I wanted to change the layout, 
 * it would all be handled here.
 */
public class AvatolCVFileSystem {
	private static final String COPY_MARKER_BISQUE = "datasetCopyOfBisqueDataset.txt";
	private static final String COPY_MARKER_MORPHOBANK = "datasetCopyOfMorphobankDataset.txt";
    private static final String NL = System.getProperty("line.separator");
    public static final String ROTATE_VERTICALLY = "rotateVerticaly";
    public static final String ROTATE_HORIZONTALLY = "rotateHorizontally";
    public static final String ROTATION_STATES_DIRNAME = "userRotations";
    public static final String RESERVED_PREFIX = "avcv_";

	public static final String FILESEP = System.getProperty("file.separator");
    public static final String EXCLUSION_STATES_DIRNAME = "exclusions";
    
    private static final String DIR_NAME_THUMBNAIL = "thumbnail";
    private static final String DIR_NAME_LARGE = "large";
    
    private static final String DIR_NAME_SEGMENTATION_OUTPUT = "segmentedData";
    private static final String DIR_NAME_ORIENTATION_OUTPUT  = "orientedData";
    private static final String DIR_NAME_SCORING_OUTPUT      = "scoredData";
    private static final String DIR_NAME_SCORING_TRAINING    = "trainingDataForScoring";
    private static final String DIR_NAME_SEGMENTATION_MANUAL_INPUT = "segmentedInputSupplemental";
    private static final String DIR_NAME_ORIENTATION_MANUAL_INPUT  = "orientedInputSupplemental";
    private static final String DIR_NAME_SCORING_MANUAL_INPUT      = "scoredInputSupplemental";

	private static String avatolCVRootDir = null;
	private static String sessionsDir = null;
	//private static String currentProjectDir = null;
    //private static String currentProjectUserAnswersDir = null;
    private static String sessionSummariesDir = null;

    private static String sessionID = null;
    private static String modulesDir = null;
    private static String datasetName = null;
    private static String datasourceName = null;
    
    public static void flushPriorSettings(){
    	//currentProjectDir = null;
        //currentProjectUserAnswersDir = null;
        sessionID = null;
        datasetName = null;
        datasourceName = null;
    }
	public static void setRootDir(String rootDir) throws AvatolCVException {
		avatolCVRootDir = rootDir;
		sessionsDir = avatolCVRootDir + FILESEP + "sessions";
		//System.out.println("sessionDataDir: " + sessionsDir);
		ensureDir(sessionsDir);
		modulesDir = avatolCVRootDir + FILESEP + "modules";
		ensureDir(modulesDir);
		sessionSummariesDir = avatolCVRootDir + FILESEP + "sessionSummaries";
		ensureDir(sessionSummariesDir);
	}
	//
	//  These are defined at construction time
	//
	public static String getAvatolCVRootDir() throws AvatolCVException{
	    if (null == avatolCVRootDir){
            throw new AvatolCVException("avatolCVRootDir consulted before its set.");
        }
		return avatolCVRootDir;
	}
	public static String getModulesDir() throws AvatolCVException {
	    if (null == modulesDir){
	        throw new AvatolCVException("modulesDir consulted before its set.");
	    }
	    return modulesDir;
	}
    public static String getSessionsRoot() throws AvatolCVException {
        if (null == sessionsDir){
            throw new AvatolCVException("sessionsDir consulted before its set.");
        }
        return sessionsDir;
    }
    public static String getSessionSummariesDir() throws AvatolCVException {
        if (null == sessionSummariesDir){
            throw new AvatolCVException("sessionSummariesDir consulted before its set.");
        }
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
        List<String> datedSessionDirs = getDatedSessionDirsForInstallation();
        String date = getSessionFormatDate();
        List<String> idsFromToday = new ArrayList<String>();
        for (String datedSessionDir : datedSessionDirs){
            if (datedSessionDir.startsWith(date)){
                idsFromToday.add(datedSessionDir);
            }
        }
        String reusePreviousSessionPath = getSessionsRoot() + FILESEP + "reusePrevSessionId.txt";
        File f = new File(reusePreviousSessionPath);
        String nextIDForToday = null;
        if (f.exists()){
            nextIDForToday = getMostRecentIDForDate(date, idsFromToday);
        }
        else {
            nextIDForToday = getNextIDForDate(date, idsFromToday);
        }
        return nextIDForToday;
    }
    public static String getMostRecentIDForDate(String dateString, List<String> ids){
        if (ids.isEmpty()){
            return dateString + "_01";
        }
        List<String> numbersForToday = new ArrayList<String>();
        for (String s : ids){
            String[] parts = ClassicSplitter.splitt(s,'_');
            String number = parts[1];
            numbersForToday.add(number);
        }
        Collections.sort(numbersForToday);
        String finalNumberString = numbersForToday.get(numbersForToday.size() - 1);
        return dateString + "_" + finalNumberString;
    }
    public static String getNextIDForDate(String dateString, List<String> ids){
        if (ids.isEmpty()){
            return dateString + "_01";
        }
        List<String> numbersForToday = new ArrayList<String>();
        for (String s : ids){
            String[] parts = ClassicSplitter.splitt(s,'_');
            String number = parts[1];
            numbersForToday.add(number);
        }
        Integer highestNumber = getHighestNumber(numbersForToday);
       // Collections.sort(numbersForToday);
       // String finalNumberString = numbersForToday.get(numbersForToday.size() - 1);
       // Integer numberAsInteger = new Integer(finalNumberString);
        int newValue = highestNumber.intValue() + 1;
        String newValueString = "";
        if (newValue > 999){
        	newValueString = String.format("%04d", newValue);
        }
        else if (newValue > 99){
        	newValueString = String.format("%03d", newValue);
        }
        else {
        	newValueString = String.format("%02d", newValue);
        }
        
        return dateString + "_" + newValueString;
    }
    public static Integer getHighestNumber(List<String> numStrings){
    	Integer highest = new Integer(0);
    	for (String n : numStrings){
    		Integer cur = new Integer(n);
    		if (cur.intValue() > highest.intValue()){
    			highest = cur;
    		}
    	}
    	return highest;
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
	public static String getSessionExclusionDir() throws AvatolCVException {
		return getSessionDir() + FILESEP + EXCLUSION_STATES_DIRNAME;
	}
	public static List<String> getSessionFilenames() throws AvatolCVException {
        File sessionSummariesDirFile = new File(sessionSummariesDir);
        File[] files = sessionSummariesDirFile.listFiles();
        
       
        List<String> names = new ArrayList<String>();
        for (File f : files){
            String name = f.getName();
            if (name.startsWith(".")){
                // skip these
            }
            else {
                String nameRoot = name.replace(".txt","");
                names.add(nameRoot);
            }
        }
        return names;
	}
	public static boolean nameIsDatedSession(String name){
	    if (name.indexOf("_") == -1){
	        return false;
	    }
	    String[] parts = ClassicSplitter.splitt(name,'_');
        if (!(parts[0].length() == 8)){
            return false;
        }
        try {
            Integer test = new Integer(parts[0]);
            int intValue = test.intValue();
            return true;
        }
        catch(Exception e){
            return false;
        }
	}
	public static String getPathForUploadSessionFile(String runName) throws AvatolCVException {
	    String sessionDir = getSessionDir();
	    String path = sessionDir + FILESEP + "uploadLog_" + runName + ".txt";
	    return path;
	}
	public static List<String> getDatedSessionFilesFromDirFile(File datasetDirFile){
        File[] datedSessionDirFiles = datasetDirFile.listFiles();
        List<String> datedSessions = new ArrayList<String>();
        for (File f : datedSessionDirFiles){
            String name = f.getName();
            if (nameIsDatedSession(name)){
                datedSessions.add(name);
            }
        }
        return datedSessions;
	}
	public static List<String> getDatedSessionDirsForInstallation() throws AvatolCVException {
	    List<String> allFiles = new ArrayList<String>();
	    File sessionsDirFile = new File(sessionsDir);
	    File[] datasetDirCandidates = sessionsDirFile.listFiles();
	    for (File candidate : datasetDirCandidates){
	        if (candidate.isDirectory()){
	            List<String> datedSessionFilesForDataset = getDatedSessionFilesFromDirFile(candidate);
	            allFiles.addAll(datedSessionFilesForDataset);
	        }
	    }
	    Collections.sort(allFiles);
        return allFiles;
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
	public static boolean isBisqueDataset(File datasetDirFile){
		String bisqueDir     = datasetDirFile.getAbsolutePath() + FILESEP + "bisque";
		File bisqueFile = new File(bisqueDir);
		if (bisqueFile.exists()){
			return true;
		}
		return false;
	}
	public static boolean isMorphobankDataset(File datasetDirFile){
		String mbDir     = datasetDirFile.getAbsolutePath() + FILESEP + "morphobank";
		File mbFile = new File(mbDir);
		if (mbFile.exists()){
			return true;
		}
		return false;
	}
	public static boolean isBisqueDatasetCopy(File datasetFile){
		String path = datasetFile.getAbsolutePath() + FILESEP + COPY_MARKER_BISQUE;
		File f = new File(path);
		if (f.exists()){
			return true;
		}
		return false;
	}
	public static boolean isMorphobankDatasetCopy(File datasetFile){
		String path = datasetFile.getAbsolutePath() + FILESEP + COPY_MARKER_MORPHOBANK;
		File f = new File(path);
		if (f.exists()){
			return true;
		}
		return false;
	}
	public static void createProvenanceMarkerForBisqueCopy(File datasetFile, File sourceDatasetFile) throws AvatolCVException {
		String path = datasetFile.getAbsolutePath() + FILESEP + COPY_MARKER_BISQUE;
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(path)); 
			writer.write("bisque dataset copied from " + sourceDatasetFile.getAbsolutePath() + NL);
			writer.close();
		}
		catch(IOException ioe){
			throw new AvatolCVException("problem writing dataset copy provenance marker for Bisque dataset");
		}
	}
	public static void createProvenanceMarkerForMorphobankCopy(File datasetFile, File sourceDatasetFile) throws AvatolCVException {
		String path = datasetFile.getAbsolutePath() + FILESEP + COPY_MARKER_MORPHOBANK;
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(path)); 
			writer.write("morphobank dataset copied from " + sourceDatasetFile.getAbsolutePath() + NL);
			writer.close();
		}
		catch(IOException ioe){
			throw new AvatolCVException("problem writing dataset copy provenance marker for Bisque dataset");
		}
	}
	public static boolean isDatasetLocal(File datasetDirFile){
		if (isBisqueDataset(datasetDirFile)){
			return false;
		}
		else if (isMorphobankDataset(datasetDirFile)){
			return false;	
		}
		return true;
	}
	public static void setChosenDataset(DatasetInfo di) throws AvatolCVException {
	    datasetName = di.getName();
	    ensureDir(getSessionDir());
	    ensureDir(getSegmentedDataDir());
        ensureDir(getOrientedDataDir());
        ensureDir(getScoredDataDir());
        ensureDir(getTrainingDataDirForScoring());
	    ensureDir(getSessionExclusionDir());
	    ensureDir(getDatasetDir());
	    
	    ensureDir(getNormalizedDataDir());
	    ensureDir(getNormalizedImageInfoDir());
        ensureDir(getNormalizedImageDir());
        ensureDir(getNormalizedImagesLargeDir());
        ensureDir(getNormalizedImagesThumbnailDir());
        
        ensureDir(getManuallyProvidedSegmentationLabelsDir());
        ensureDir(getManuallyProvidedOrientationLabelsDir());
        ensureDir(getManuallyProvidedScoringLabelsDir());
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
        ensureDir(getNormalizedExclusionDir());
	}
	
	public static String getSpecializedDataDir() throws AvatolCVException {
	    return getDatasetDir() + FILESEP + datasourceName;
	}
    public static String getSpecializedImageInfoDir() throws AvatolCVException {
        return getSpecializedDataDir() + FILESEP + "imageInfo";
    }

    public static String getNormalizedExclusionDir() throws AvatolCVException {
        return getNormalizedDataDir() + FILESEP + EXCLUSION_STATES_DIRNAME;
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
        return getNormalizedImageDir() + FILESEP + DIR_NAME_LARGE;
    }
    public static String getNormalizedImagesThumbnailDir() throws AvatolCVException{
        return getNormalizedImageDir() + FILESEP + DIR_NAME_THUMBNAIL;
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
	//public static void setCurrentProject(String name){
	//    currentProjectDir = sessionsDir + FILESEP + name;
	//    ensureDir(currentProjectDir);
	    //currentProjectUserAnswersDir = currentProjectDir + FILESEP + "userAnswers";
	//}
	
	public static String getMediaMetadataFilename(String parentDir, String mediaID)  throws AvatolCVException {
        File f = new File(parentDir);
        if (!f.isDirectory()){
            throw new AvatolCVException("normailzed media dir does not exist : " + parentDir);
        }
        File[] files = f.listFiles();
        int count = 0;
        for (File existingMediaFile : files){
            String filename = existingMediaFile.getName();
            String[] parts = ClassicSplitter.splitt(filename,'.');
            String root = parts[0];
            String[] rootParts = ClassicSplitter.splitt(root,'_');
            String curMediaID = rootParts[0];
            if (mediaID.equals(curMediaID)){
                count++;
            }
        }
        return mediaID + "_" + (count + 1) + ".txt";
    }

	public static String getScoreFilePath(String runID, String scoringConcernName) throws AvatolCVException {
		String dirToSearch = getDatasetDir() + FILESEP + runID + FILESEP + "scoredData";
		
		File dir = new File(dirToSearch);
		File[] files = dir.listFiles();
		for (File f : files){
			String fname = f.getName();
			if (fname.contains(scoringConcernName) && (fname.startsWith(ScoresInfoFile.FILE_PREFIX) || fname.startsWith(ScoresInfoFile.FILE_PREFIX_ALTERNATE))){
				return f.getAbsolutePath();
			}
		}
		return null;
	}
	public static String getScoringFilePath(String runID, String scoringConcernName) throws AvatolCVException {
		String dirToSearch = getDatasetDir() + FILESEP + runID + FILESEP + "trainingDataForScoring";
		
		File dir = new File(dirToSearch);
		File[] files = dir.listFiles();
		for (File f : files){
			String fname = f.getName();
			if (fname.contains(scoringConcernName) && (fname.startsWith(ScoringInfoFile.FILE_PREFIX))){
				return f.getAbsolutePath();
			}
		}
		return null;
	}
	public static String getTrainingFilePath(String runID, String scoringConcernName) throws AvatolCVException {
		String dirToSearch = getDatasetDir() + FILESEP + runID + FILESEP + "trainingDataForScoring";
		
		File dir = new File(dirToSearch);
		File[] files = dir.listFiles();
		for (File f : files){
			if (f.getName().contains(scoringConcernName) && f.getName().startsWith(TrainingInfoFile.FILE_PREFIX)){
				return f.getAbsolutePath();
			}
		}
		return null;
	}
	
	public static String getHoldoutFilePath(String runID, String scoringConcernName) throws AvatolCVException {
        String dirToSearch = getDatasetDir() + FILESEP + runID + FILESEP + "trainingDataForScoring";
        
        File dir = new File(dirToSearch);
        File[] files = dir.listFiles();
        for (File f : files){
            if (f.getName().contains(scoringConcernName) && f.getName().startsWith(HoldoutInfoFile.FILE_PREFIX)){
                return f.getAbsolutePath();
            }
        }
        return null;
    }
    
	// IMAGES
	public static String getDatasetExclusionInfoFilePath(String imageID) throws AvatolCVException {
		return  AvatolCVFileSystem.getNormalizedExclusionDir() + FILESEP + imageID + ".txt";
	}
	public static String getSessionExclusionInfoFilePath(String imageID) throws AvatolCVException {
		return  AvatolCVFileSystem.getSessionExclusionDir() + FILESEP + imageID + ".txt";
	}
	public static String getImageRotationStateDir() throws AvatolCVException {
	    return getNormalizedImageDir() + FILESEP + ROTATION_STATES_DIRNAME;
	}
	public static String getRotateHorizontallyPath(String imageID) throws AvatolCVException {
	    return  getImageRotationStateDir() + FILESEP + imageID + "_" + ROTATE_HORIZONTALLY + ".txt";
	}
	public static String getLargeImagePathForThumbnailPath(String thumbnailPath) throws AvatolCVException {
	    /*
	     * thumbnail and large paths end with:
	     * images/large/00-2cNgRYKCyKB6MWSBa4Y5EA_MonocostusUniflorus-mcl30-mod_1000.jpg  
	     * images/thumbnail/00-2cNgRYKCyKB6MWSBa4Y5EA_MonocostusUniflorus-mcl30-mod_80.jpg
	     */
	    // get the fileRootName (sans _80)
	    File thumbnailFile = new File(thumbnailPath);
	    String filename = thumbnailFile.getName();
	    String[] parts = ClassicSplitter.splitt(filename,'_');
	    String fileRoot = parts[0];
	    // get the large image dir
	    File thumbnailDirFile = thumbnailFile.getParentFile();
	    File imagesDirFile = thumbnailDirFile.getParentFile();
	    String largeDirFilePath = imagesDirFile + FILESEP + DIR_NAME_LARGE;
	    File largeDirFile = new File(largeDirFilePath);
	    // find a file with matching fileRoot
	    File[] files = largeDirFile.listFiles();
	    for (File f : files){
	        if (f.getName().startsWith(fileRoot)){
	            return f.getAbsolutePath();
	        }
	    }
	    throw new AvatolCVException("Could not find large image corresponding to thumbnail " + thumbnailPath);
	}
	public static String getThumbnailPathForImagePath(String path) throws AvatolCVException {
        /*
         * thumbnail and large paths end with:
         * images/large/00-2cNgRYKCyKB6MWSBa4Y5EA_MonocostusUniflorus-mcl30-mod_1000.jpg  
         * images/thumbnail/00-2cNgRYKCyKB6MWSBa4Y5EA_MonocostusUniflorus-mcl30-mod_80.jpg
         */
	    // just look in the thumbnails dir for matching imageID
	    String imageID = ImageInfo.getImageIDFromPath(path);
	    String thumbnailDirFilePath = getNormalizedImagesThumbnailDir();
        File thumbnailDirFile = new File(thumbnailDirFilePath);
        // find a file with matching fileRoot
        File[] files = thumbnailDirFile.listFiles();
        for (File f : files){
            if (f.getName().startsWith(imageID)){
                return f.getAbsolutePath();
            }
        }
        throw new AvatolCVException("Could not find thumbnail image corresponding to path " + path);
    }
	public static String getRotateVerticallyPath(String imageID) throws AvatolCVException {
        return  getImageRotationStateDir() + FILESEP + imageID + "_" + ROTATE_VERTICALLY + ".txt";
    }
	/*public static String getUserAnswerDir() throws AvatolCVException {
	    if (null == currentProjectUserAnswersDir){
	        throw new AvatolCVException("getUserAnswerDir() called prior to setCurrentProject()");
	    }
	    return currentProjectUserAnswersDir;
	}*/

	
	// segmentation
	public static String getSegmentedDataDir()  throws AvatolCVException {
		String dir = getSessionDir() + FILESEP + DIR_NAME_SEGMENTATION_OUTPUT;
		return dir;
	}
	
	// orientation
    public static String getOrientedDataDir()  throws AvatolCVException {
        String dir = getSessionDir() + FILESEP + DIR_NAME_ORIENTATION_OUTPUT;
        return dir;
    }
   
    // scoring
    public static String getScoredDataDir()  throws AvatolCVException {
        String dir = getSessionDir() + FILESEP + DIR_NAME_SCORING_OUTPUT;
        return dir;
    }
    public static String getTrainingDataDirForScoring() throws AvatolCVException {
        String dir = getSessionDir() + FILESEP + DIR_NAME_SCORING_TRAINING;
        return dir;
    }
    public static String getManuallyProvidedSegmentationLabelsDir() throws AvatolCVException {
        return getSessionDir() + FILESEP + DIR_NAME_SEGMENTATION_MANUAL_INPUT;
    }
    public static String getManuallyProvidedOrientationLabelsDir() throws AvatolCVException {
        return getSessionDir() + FILESEP + DIR_NAME_ORIENTATION_MANUAL_INPUT;
    }
    public static String getManuallyProvidedScoringLabelsDir() throws AvatolCVException {
        return getSessionDir() + FILESEP + DIR_NAME_SCORING_MANUAL_INPUT;
    }
    
    public static boolean doScoringLogsExist() throws AvatolCVException {
        return doSessionLogsExist("scoring");
    } 
    public static boolean doOrientationLogsExist() throws AvatolCVException {
        return doSessionLogsExist("scoring");
    } 
    public static boolean doSegmentationLogsExist() throws AvatolCVException {
        return doSessionLogsExist("scoring");
    }
    public static boolean doSessionLogsExist(String logType) throws AvatolCVException {
        String dir = getSessionDir() + FILESEP + "logs" + FILESEP + logType;
        File f = new File(dir);
        if (!f.exists()){
            return false;
        }
        File[] files = f.listFiles();
        if (files.length > 0){
            return true;
        }
        return false;
    }
    public static String loadScoringLogs() throws AvatolCVException {
        return loadSessionLogs("scoring");
    }
    public static String loadSessionLogs(String logType) throws AvatolCVException {
        String result = "";
        String dir = getSessionDir() + FILESEP + "logs" + FILESEP + logType;
        File f = new File(dir);
        StringBuilder sb = new StringBuilder();
        if (f.exists()){
            File[] files = f.listFiles();
            for (File file : files){
                String path = file.getAbsolutePath();
                sb.append(NL);
                sb.append("===================================================================" + NL);
                sb.append(" LOGFILE: " + path + NL);
                sb.append("===================================================================" + NL);
                try {
                    BufferedReader reader = new BufferedReader(new FileReader(path));
                    String line = null;
                    while (null != (line = reader.readLine())){
                        sb.append(line + NL);
                    }
                    reader.close();
                }
                catch(Exception e){
                    throw new AvatolCVException(e.getMessage());
                }
                
            }
        }
        result = "" + sb;
        return result;
    }
    public static void clearScoringLogs() throws AvatolCVException {
        clearSessionLogs("scoring");
    }
    public static void clearSessionLogs(String logType) throws AvatolCVException {
        String dir = getSessionDir() + FILESEP + "logs" + FILESEP + logType;
        File f = new File(dir);
        if (f.exists()){
            File[] files = f.listFiles();
            for (File file : files){
                file.delete();
            }
        }
    }
    public static String getImagesDir(){
    	return avatolCVRootDir + FILESEP + "images";
    }
    public static void deleteNormalizedMetadataForDataset() throws AvatolCVException {
    	String imageInfoPath = AvatolCVFileSystem.getNormalizedImageInfoDir();
    	deleteDirectory(imageInfoPath);
    }
    public static void deleteDirectory(String path){
    	File f = new File(path);
    	File[] files = f.listFiles();
    	for (File file : files){
    		if (file.isDirectory()){
    			deleteDirectory(file.getAbsolutePath());
    		}
    		else {
    			file.delete();
    		}
    	}
    }
}
