package edu.oregonstate.eecs.iis.avatolcv.bisque;

import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import edu.oregonstate.eecs.iis.avatolcv.core.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.segmentation.SegmentationSessionData;
//import edu.oregonstate.eecs.iis.avatolcv.segmentation.SegmentationToolHarness;
import edu.oregonstate.eecs.iis.avatolcv.ws.bisque.BisqueDataset;
import edu.oregonstate.eecs.iis.avatolcv.ws.bisque.BisqueImage;
import edu.oregonstate.eecs.iis.avatolcv.core.ImageInfo;

public class BisqueSessionData {
	private static final String FILESEP = System.getProperty("file.separator");
	public static final String STANDARD_IMAGE_FILE_EXTENSION = ".jpg";
	public static final String IMAGE_THUMBNAIL_WIDTH = "80";
	public static final String IMAGE_MEDIUM_WIDTH = "200";
	public static final String IMAGE_LARGE_WIDTH = "400";
	private BisqueDataset dataset = null;
	private List<BisqueImage> bisqueImages = null;
	
	private Hashtable<String,BisqueImage> bisqueImageForID = new Hashtable<String,BisqueImage>();
	private Hashtable<String,ImageInfo> thumbnailForID = new Hashtable<String,ImageInfo>();
	private Hashtable<String,ImageInfo> imageMediumForID = new Hashtable<String,ImageInfo>();
	private Hashtable<String,ImageInfo> imageLargeForID = new Hashtable<String,ImageInfo>();
	
	private List<ImageInfo> imagesThumbnail = new ArrayList<ImageInfo>();
	private List<ImageInfo> imagesMedium = new ArrayList<ImageInfo>();
	private List<ImageInfo> imagesLarge = new ArrayList<ImageInfo>();
	
	private String sessionDataRootDir = null;
	private String sessionDatasetDir = null;
	// TODO? imagesToInclude and imagesToExclude are now ImageInfo - may need to translate that to BisqueImage at some point.
	List<ImageInfo> imagesToInclude = null;
	List<ImageInfo> imagesToExclude = null;
	SegmentationSessionData ssd = null;
	public BisqueSessionData(String sessionDataRootParent) throws AvatolCVException {
		File f = new File(sessionDataRootParent);
		if (!f.isDirectory()){
			throw new AvatolCVException("directory does not exist for being sessionDataRootParent " + sessionDataRootParent);
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
	public void ensureImageDirsExists(){
		ensureDirExists(getImagesThumbnailDir());
		ensureDirExists(getImagesMediumDir());
		ensureDirExists(getImagesLargeDir());
		
	}
	public void ensureDirExists(String dir){
		File f = new File(dir);
		if (!f.isDirectory()){
			f.mkdirs();
		}
	}
	public String getThumbnailWidth(){
		return IMAGE_THUMBNAIL_WIDTH;
	}
	public String getMediumImageWidth(){
		return IMAGE_MEDIUM_WIDTH;
	}
	public String getLargeImageWidth(){
		return IMAGE_LARGE_WIDTH;
	}
	
	private String getImagesDir(){
		return sessionDatasetDir + FILESEP + "images";
	}
	
    public String getImagesThumbnailDir(){
    	return getImagesDir() + FILESEP + "thumbnails";
    } 
    public String getImagesMediumDir(){
    	return getImagesDir() + FILESEP + "medium";
    } 
    public String getImagesLargeDir(){
    	return getImagesDir() + FILESEP + "large";
    }
    
	public static void generateImageInfoForSize(List<ImageInfo> listToFill, List<BisqueImage> bisqueImages, String width, String dir){
		for (BisqueImage bi : bisqueImages){
			ImageInfo ii = new ImageInfo(dir, bi.getResourceUniq(), bi.getName(), width, STANDARD_IMAGE_FILE_EXTENSION);
			listToFill.add(ii);
		}
	}
	public void setCurrentImages(List<BisqueImage> bisqueImages){
		this.bisqueImages = bisqueImages;
		for (BisqueImage bi : bisqueImages){
			String id = bi.getResourceUniq();
			bisqueImageForID.put(id, bi);
		}
		generateImageInfoForSize(imagesThumbnail,bisqueImages,IMAGE_THUMBNAIL_WIDTH, getImagesThumbnailDir());
		for (ImageInfo ii : imagesThumbnail){
			String id = ii.getID();
			thumbnailForID.put(id, ii);
		}
		generateImageInfoForSize(imagesMedium,   bisqueImages,IMAGE_MEDIUM_WIDTH,    getImagesMediumDir());
		for (ImageInfo ii : imagesMedium){
			String id = ii.getID();
			imageMediumForID.put(id, ii);
		}
		generateImageInfoForSize(imagesLarge,    bisqueImages,IMAGE_LARGE_WIDTH,     getImagesLargeDir());
		for (ImageInfo ii : imagesLarge){
			String id = ii.getID();
			imageLargeForID.put(id, ii);
		}
		
	}
	public ImageInfo getThumbnailForId(String id) throws AvatolCVException {
		ImageInfo ii = null;
		ii = thumbnailForID.get(id);
		if (null == ii){
			throw new AvatolCVException("no thumbnail exists for ID " + id);
		}
		return ii;
	}
	public ImageInfo getImageMediumForId(String id) throws AvatolCVException {
		ImageInfo ii = null;
		ii = imageMediumForID.get(id);
		if (null == ii){
			throw new AvatolCVException("no medium size image exists for ID " + id);
		}
		return ii;
	}
	public ImageInfo getImageLargeForId(String id) throws AvatolCVException {
		ImageInfo ii = null;
		ii = imageLargeForID.get(id);
		if (null == ii){
			throw new AvatolCVException("no large size exists for ID " + id);
		}
		return ii;
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
	public void setImagesToInclude(List<ImageInfo> images){
		this.imagesToInclude = images;
	}
	public void setImagesToExclude(List<ImageInfo> images){
		this.imagesToExclude = images;
	}
	public List<ImageInfo> getIncludedImages(){
		return this.imagesToInclude;
	}
	/*
	 * Segmentation
	 */
	
    public SegmentationSessionData getSegmentationSessionData(){
    	return this.ssd;
    }
	
	
	
}
