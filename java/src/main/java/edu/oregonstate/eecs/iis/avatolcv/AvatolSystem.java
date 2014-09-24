package edu.oregonstate.eecs.iis.avatolcv;

import java.io.File;

public class AvatolSystem {
	private static final String FILESEP = System.getProperty("file.separator");
	private String avatol_cv_dir;
	private String crf_dir;
    public AvatolSystem(String avatol_cv_dir) throws AvatolCVException {
    	this.avatol_cv_dir = avatol_cv_dir;
    	this.crf_dir = setCrfDir(avatol_cv_dir);
    }
    public String setCrfDir(String avatol_cv_dir) throws AvatolCVException {
    	File f = new File(avatol_cv_dir);
    	if (!f.isDirectory()){
    		throw new AvatolCVException("given dir for avatol_cv_dir invalid : " + avatol_cv_dir);
    	}
    	File parentFile = f.getParentFile();
    	String crf_dir = parentFile.getAbsolutePath() + FILESEP + "nematocyst" + FILESEP;
    	File crfDirFile = new File(crf_dir);
    	if (!crfDirFile.isDirectory()){
    		throw new AvatolCVException("crf directory not present : " + crf_dir);
    	}
    	return crf_dir;
    }
    public String getCrfDir(){
    	return this.crf_dir + FILESEP;
    }
}
