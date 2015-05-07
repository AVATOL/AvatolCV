package edu.oregonstate.eecs.iis.avatolcv;

import java.io.File;

import edu.oregonstate.eecs.iis.avatolcv.core.AvatolCVException;

public class AvatolCVFileSystem {
	public static final String FILESEP = System.getProperty("file.separator");
	private static String avatolCVRootDir = null;
	private static String sessionDataDir = null;
	private static String currentProjectDir = null;
    private static String currentProjectUserAnswersDir = null;
    private static String uiContentXmlDir = null;
    private static String charQuestionsDir = null;
	public AvatolCVFileSystem(String launchDir) throws AvatolCVException {
		avatolCVRootDir = launchDir;
		sessionDataDir = avatolCVRootDir + FILESEP + "sessionData";
		System.out.println("sessionDataDir: " + sessionDataDir);
		ensureDir(sessionDataDir);
		uiContentXmlDir = avatolCVRootDir + FILESEP + "uiContentXml";
		System.out.println("uiContentXmlDir: " + uiContentXmlDir);
		ensureDirIsPresent(uiContentXmlDir);
		charQuestionsDir = uiContentXmlDir + FILESEP + "characterQuestions";
		System.out.println("charQuestionsDir: " + charQuestionsDir);
		ensureDirHasContents(charQuestionsDir, "xml");
	}
	public static String getAvatolCVRootDir(){
		return avatolCVRootDir;
	}
	public static String getSessionDataRoot(){
	    return sessionDataDir;
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
	    currentProjectDir = sessionDataDir + FILESEP + name;
	    ensureDir(currentProjectDir);
	    currentProjectUserAnswersDir = currentProjectDir + FILESEP + "userAnswers";
	}
	public static String getUserAnswerDir() throws AvatolCVException {
	    if (null == currentProjectUserAnswersDir){
	        throw new AvatolCVException("getUserAnswerDir() called prior to setCurrentProject()");
	    }
	    return currentProjectUserAnswersDir;
	}
	public static String getCharacterQuestionsDir(){
	    return charQuestionsDir;
	}
}
