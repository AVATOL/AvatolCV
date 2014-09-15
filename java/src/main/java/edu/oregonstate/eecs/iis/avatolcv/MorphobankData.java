package edu.oregonstate.eecs.iis.avatolcv;

import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public class MorphobankData {
	private static final String FILESEP = System.getProperty("file.separator");
	private String parentDirPath = null;
	private ArrayList<String> matrixDirNames = new ArrayList<String>();
	private Hashtable<String, MorphobankBundle> bundleForName = new Hashtable<String, MorphobankBundle>();
	
	public MorphobankData(String parentDirPath) throws MorphobankDataException {
    	this.parentDirPath = parentDirPath;
    	File parentDir = new File(parentDirPath);
    	if (!parentDir.isDirectory()){
    		throw new MorphobankDataException("parent dir of MorphobankBundle not valid " + parentDirPath);
    	}
    	String[] candidateDirs = parentDir.list();
    	for (int i = 0; i < candidateDirs.length; i++){
    		String candidateDir = candidateDirs[i];
    		if (".".equals(candidateDir)){
    			//ignore
    		}
    		else if ("..".equals(candidateDir)){
    			//ignore
    		}
    		else {
    			matrixDirNames.add(candidateDir);
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
	public void loadMatrix(String name) throws MorphobankDataException {
		String fullpath = parentDirPath + FILESEP + name;
		MorphobankBundle mbb = new MorphobankBundle(fullpath);
		bundleForName.put(name, mbb);
	}
}
