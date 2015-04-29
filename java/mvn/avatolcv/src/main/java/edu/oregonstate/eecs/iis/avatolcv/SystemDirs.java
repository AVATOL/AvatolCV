package edu.oregonstate.eecs.iis.avatolcv;

import java.io.File;

import edu.oregonstate.eecs.iis.avatolcv.core.AvatolCVException;

public class SystemDirs {
	public static final String FILESEP = System.getProperty("line.separator");
	private static String avatolCVRootDir = null;
	private static String sessionDataDir = null;
	private static String currentProjectDir = null;
    private static String currentProjectUserAnswersDir = null;
	public SystemDirs(String launchDir){
		avatolCVRootDir = launchDir;
		sessionDataDir = avatolCVRootDir + FILESEP + "sessionData";
		ensureDir(sessionDataDir);
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
}
