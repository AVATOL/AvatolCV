package edu.oregonstate.eecs.iis.avatolcv.bisque;

import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import edu.oregonstate.eecs.iis.avatolcv.bisque.seg.SegmentationSessionData;
import edu.oregonstate.eecs.iis.avatolcv.ws.BisqueSessionException;
import edu.oregonstate.eecs.iis.avatolcv.ws.bisque.BisqueDataset;
import edu.oregonstate.eecs.iis.avatolcv.ws.bisque.BisqueImage;
import edu.oregonstate.eecs.iis.avatolcv.core.ImageInfo;

public class BisqueSessionData {
	private static final String FILESEP = System.getProperty("file.separator");
	public static final int IMAGE_THUMBNAIL_WIDTH = 80;
	public static final int IMAGE_MEDIUM_WIDTH = 200;
	public static final int IMAGE_LARGE_WIDTH = 400;
	private BisqueDataset dataset = null;
	private List<BisqueImage> bisqueImages = null;
	
	private Hashtable<String,BisqueImage> bisqueImageForID = new Hashtable<String,BisqueImage>();
	private List<ImageInfo> imagesThumbnail = new ArrayList<ImageInfo>();
	private List<ImageInfo> imagesMedium = new ArrayList<ImageInfo>();
	private List<ImageInfo> imagesLarge = new ArrayList<ImageInfo>();
	
	private String sessionDataRootDir = null;
	private String sessionDatasetDir = null;
	//private List<Integer> imageWidths = new ArrayList<Integer>();
	List<BisqueImage> imagesToInclude = null;
	List<BisqueImage> imagesToExclude = null;
	SegmentationSessionData ssd = null;
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
		//imageWidths.add(new Integer(IMAGE_LARGE_WIDTH));// large
		//imageWidths.add(new Integer(IMAGE_MEDIUM_WIDTH));// medium
		//imageWidths.add(new Integer(IMAGE_THUMBNAIL_WIDTH)); // thumbnail
		this.ssd = new SegmentationSessionData(this.sessionDatasetDir);
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
	public int getThumbnailWidth(){
		return IMAGE_THUMBNAIL_WIDTH;
	}
	public int getMediumImageWidth(){
		return IMAGE_MEDIUM_WIDTH;
	}
	public int getLargeImageWidth(){
		return IMAGE_LARGE_WIDTH;
	}
	//public int getImageSizeCount(){
	//	return imageWidths.size();
	//}
	//public List<Integer> getImageWidths(){
	//	return imageWidths;
	//}
	public String getImagesDir(){
		return sessionDatasetDir + FILESEP + "images";
	}
	//public String getImagePath(String filename){
	//	return getImagesDir() + FILESEP + filename;
	//}
    public String getImagesThumbnailDir(){
    	return getImagesDir() + FILESEP + "thumbnails";
    } 
    public String getImagesMediumDir(){
    	return getImagesDir() + FILESEP + "medium";
    } 
    public String getImagesLargeDir(){
    	return getImagesDir() + FILESEP + "large";
    }
    
	public static String getImageFilename(BisqueImage bi, int width){
		return getImageFileRootname(bi, width) + ".jpg";
	}
	public static String getImageFileRootname(BisqueImage bi, int width){
		return bi.getResourceUniq() + "_" + bi.getName() + "_" + width;
	}
	public static void generateImageInfoForSize(List<ImageInfo> listToFill, List<BisqueImage> bisqueImages, int width, String dir){
		for (BisqueImage bi : bisqueImages){
			ImageInfo ii = new ImageInfo();
			ii.setID(bi.getResourceUniq());
			ii.setName(bi.getName());
			ii.setImageWidth(width);
			ii.setFilenameRoot(bi.getResourceUniq() + "_" + bi.getName());
			String filename = getImageFilename(bi,width);
			ii.setFilename(filename);
			String filepath = dir + FILESEP + filename;
			ii.setFilepath(filepath);
			listToFill.add(ii);
		}
	}
	public void setCurrentImages(List<BisqueImage> bisqueImages){
		this.bisqueImages = bisqueImages;
		generateImageInfoForSize(imagesThumbnail,bisqueImages,IMAGE_THUMBNAIL_WIDTH, getImagesThumbnailDir());
		generateImageInfoForSize(imagesMedium,   bisqueImages,IMAGE_MEDIUM_WIDTH,    getImagesMediumDir());
		generateImageInfoForSize(imagesLarge,    bisqueImages,IMAGE_LARGE_WIDTH,     getImagesLargeDir());
		
	}
	public List<ImageInfo> getImagesThumbnail(){
		return this.imagesThumbnail;
	}
	public List<ImageInfo> getImagesMedium(){
		return this.imagesMedium;
	}
	public List<ImageInfo> getImagesLarge(){
		return this.imagesLarge;
	}
	public List<BisqueImage> getCurrentBisqueImages(){
		return this.bisqueImages;
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
	/*
	 * Segmentation
	 */
	
    public SegmentationSessionData getSegmentationSessionData(){
    	return this.ssd;
    }
	
}
