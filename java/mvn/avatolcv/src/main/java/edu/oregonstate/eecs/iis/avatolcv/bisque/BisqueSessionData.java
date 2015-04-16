package edu.oregonstate.eecs.iis.avatolcv.bisque;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import edu.oregonstate.eecs.iis.avatolcv.ws.BisqueSessionException;
import edu.oregonstate.eecs.iis.avatolcv.ws.bisque.BisqueDataset;
import edu.oregonstate.eecs.iis.avatolcv.ws.bisque.BisqueImage;

public class BisqueSessionData {
	private static final String FILESEP = System.getProperty("file.separator");
	public static final int IMAGE_THUMBNAIL_WIDTH = 80;
	public static final int IMAGE_MEDIUM_WIDTH = 200;
	public static final int IMAGE_LARGE_WIDTH = 400;
	private BisqueDataset dataset = null;
	private List<BisqueImage> images = null;
	private String sessionDataRootDir = null;
	private String sessionDatasetDir = null;
	private List<Integer> imageWidths = new ArrayList<Integer>();
	List<BisqueImage> imagesToInclude = null;
	List<BisqueImage> imagesToExclude = null;
	
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
		imageWidths.add(new Integer(IMAGE_LARGE_WIDTH));// large
		imageWidths.add(new Integer(IMAGE_MEDIUM_WIDTH));// medium
		imageWidths.add(new Integer(IMAGE_THUMBNAIL_WIDTH)); // thumbnail
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
	 
	//public String getImageFilename(String resourceUniq, String name, int width){
	//	return resourceUniq + "_" + name + "_" + width + ".jpg";
	//}
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
	public void setCurrentImages(List<BisqueImage> images){
		this.images = images;
	}
	public List<BisqueImage> getCurrentImages(){
		return this.images;
	}
	public void setImagesToInclude(List<BisqueImage> images){
		this.imagesToInclude = images;
	}
	public void setImagesToExclude(List<BisqueImage> images){
		this.imagesToExclude = images;
	}
	public List<BisqueImage> getIncludedImages(){
		return this.imagesToInclude;
	}
}
