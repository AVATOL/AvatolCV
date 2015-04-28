package edu.oregonstate.eecs.iis.avatolcv;

public class RunEnvironment {
	public static final String FILESEP = System.getProperty("line.separator");
	private static String avatolCVRootDir = null;
	private static String sessionDataDir = null;
	public RunEnvironment(String launchDir){
		avatolCVRootDir = launchDir;
		sessionDataDir = avatolCVRootDir + FILESEP + "sessionData";
	}
	public static String getAvatolCVRootDir(){
		return avatolCVRootDir;
	}
	public static String getSessionDataDir(){
		return sessionDataDir;
	}
}
