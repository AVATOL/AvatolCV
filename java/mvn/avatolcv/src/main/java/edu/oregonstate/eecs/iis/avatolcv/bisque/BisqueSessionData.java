package edu.oregonstate.eecs.iis.avatolcv.bisque;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import edu.oregonstate.eecs.iis.avatolcv.ws.BisqueSessionException;
import edu.oregonstate.eecs.iis.avatolcv.ws.bisque.BisqueDataset;

public class BisqueSessionData {
	private static final String FILESEP = System.getProperty("file.separator");
	private BisqueDataset dataset = null;
	private String sessionDataRootDir = null;
	private String sessionDatasetDir = null;
	private List<Integer> imageWidths = new ArrayList<Integer>();
	
	public BisqueSessionData(String sessionDataRootParent) throws BisqueSessionException {
		File f = new File(sessionDataRootParent);
		if (!f.isDirectory()){
			throw new BisqueSessionException("directory does not exist for being sessionDataRootParent " + sessionDataRootParent);
		}
		
		this.sessionDataRootDir = sessionDataRootParent + FILESEP + "sessionData";
		f = new File(this.sessionDataRootDir);
		if (!f.isDirectory()){
			f.mkdirs();
		}
		imageWidths.add(new Integer(400));// large
		imageWidths.add(new Integer(200));// medium
		imageWidths.add(new Integer(80)); // thumbnail
	}
	/*
	 * DATASETS
	 */
	public void setChosenDataset(BisqueDataset s){
		this.dataset = s;
		// ensure dir exists for this 
	    this.sessionDatasetDir = this.sessionDataRootDir + FILESEP + this.dataset.getName();
	    File f = new File (this.sessionDatasetDir);
	    if (!f.isDirectory()){
	    	f.mkdirs();
	    }
	}
	public BisqueDataset getChosenDataset(){
		return this.dataset;
	}
	
	/*
	 * IMAGES
	 */
	public String getImageFilename(String resourceUniq, String name, int width){
		return resourceUniq + "_" + name + "_" + width + ".jpg";
	}
	public void ensureImagesDirExists(){
		String imagesDir = getImagesDir();
		File f = new File(imagesDir);
		if (!f.isDirectory()){
			f.mkdirs();
		}
	}
	public int getImageSizeCount(){
		return imageWidths.size();
	}
	public List<Integer> getImageWidths(){
		return imageWidths;
	}
	public String getImagesDir(){
		return sessionDatasetDir + FILESEP + "images";
	}
	public String getImagePath(String filename){
		return getImagesDir() + FILESEP + filename;
	}
}
