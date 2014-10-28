package edu.oregonstate.eecs.iis.avatolcv;

import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class MorphobankData {
	//System.setProperty("log4j.configuration","");;
	Logger logger;
	private static final String FILESEP = System.getProperty("file.separator");
	private String parentDirPath = null;
	private ArrayList<String> matrixDirNames = new ArrayList<String>();
	private Hashtable<String, MorphobankBundle> bundleForName = new Hashtable<String, MorphobankBundle>();
	
	public MorphobankData(String parentDirPath) throws MorphobankDataException {
		//System.setProperty("log4j.configuration","/nfs/guille/tgd/users/irvine/matlabui/java/lib/log4j.properties");
		logger = LoggerFactory.getLogger(MorphobankData.class);
    	this.parentDirPath = parentDirPath;
    	File parentDir = new File(parentDirPath);
    	if (!parentDir.isDirectory()){
    		throw new MorphobankDataException("parent dir of MorphobankBundle not valid " + parentDirPath);
    	}
    	File[] candidateDirs = parentDir.listFiles();
    	for (int i = 0; i < candidateDirs.length; i++){
    		File candidateDir = candidateDirs[i];
    		if (".".equals(candidateDir.getName())){
    			//ignore
    		}
    		else if ("..".equals(candidateDir.getName())){
    			//ignore
    		}
    		else if (candidateDir.isDirectory()){
    			logger.info("Adding matrix dir name {}.", candidateDir.getName());
    			//System.out.println("Should have logged : Adding matrix dir name...");
    			matrixDirNames.add(candidateDir.getName());
    		}
    		else {
    			//ignore other files
    		}
    	}
    }
	
	public List<String> getMatrixNames(){
		List<String> matrixNames = new ArrayList<String>();
		for (String dirName : this.matrixDirNames){
			matrixNames.add(dirName);
		}
		return matrixNames;
	}
	public String getMatrixNameAtIndex(int matlabIndex){
		int javaIndex = matlabIndex - 1;
		return this.matrixDirNames.get(javaIndex);
	}
	public MorphobankBundle loadMatrix(String name) throws MorphobankDataException, AvatolCVException  {
		String fullpath = parentDirPath + FILESEP + name;
		MorphobankBundle mbb = new MorphobankBundle(fullpath);
		bundleForName.put(name, mbb);
		return mbb;
	}
	public MorphobankBundle getBundle(String bundleName){
		return this.bundleForName.get(bundleName);
	}
}
