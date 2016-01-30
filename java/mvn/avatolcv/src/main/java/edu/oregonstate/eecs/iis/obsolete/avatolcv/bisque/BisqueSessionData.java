package edu.oregonstate.eecs.iis.obsolete.avatolcv.bisque;

import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import edu.oregonstate.eecs.iis.avatolcv.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.AvatolCVFileSystem;
import edu.oregonstate.eecs.iis.avatolcv.util.ClassicSplitter;
import edu.oregonstate.eecs.iis.avatolcv.ws.bisque.BisqueAnnotation;
//import edu.oregonstate.eecs.iis.avatolcv.segmentation.SegmentationToolHarness;
import edu.oregonstate.eecs.iis.avatolcv.ws.bisque.BisqueDataset;
import edu.oregonstate.eecs.iis.avatolcv.ws.bisque.BisqueImage;
import edu.oregonstate.eecs.iis.avatolcv.ws.morphobank.CellMediaInfo.MBMediaInfo;
import edu.oregonstate.eecs.iis.avatolcv.ws.morphobank.CharacterInfo.MBCharacter;
import edu.oregonstate.eecs.iis.avatolcv.ws.morphobank.MatrixInfo.MBMatrix;
import edu.oregonstate.eecs.iis.avatolcv.ws.morphobank.TaxaInfo.MBTaxon;
import edu.oregonstate.eecs.iis.avatolcv.core.ImageInfo;
import edu.oregonstate.eecs.iis.obsolete.avatolcv.FileUtils;
import edu.oregonstate.eecs.iis.obsolete.avatolcv.SessionDataInterface;
import edu.oregonstate.eecs.iis.obsolete.avatolcv.orientation.OrientationSessionData;
import edu.oregonstate.eecs.iis.obsolete.avatolcv.segmentation.SegmentationSessionData;

public class BisqueSessionData implements SessionDataInterface{
	private static final String FILESEP = System.getProperty("file.separator");
	public static final String STANDARD_IMAGE_FILE_EXTENSION = "jpg";
	public static final String IMAGE_THUMBNAIL_WIDTH = "80";
	public static final String IMAGE_SMALL_WIDTH = "200";
	public static final String IMAGE_MEDIUM_WIDTH = "400";
	public static final String IMAGE_LARGE_WIDTH = "1000";
	public static final String BISQUE_CHAR_QUESIONS_FILENAME = "bisqueCharQuestions.xml";
	private BisqueDataset dataset = null;
	private List<BisqueImage> bisqueImages = null;
	
	private Hashtable<String,BisqueImage> bisqueImageForID = new Hashtable<String,BisqueImage>();
	private Hashtable<String,ImageInfo> thumbnailForID = new Hashtable<String,ImageInfo>();
	private Hashtable<String,ImageInfo> imageSmallForID = new Hashtable<String,ImageInfo>();
	private Hashtable<String,ImageInfo> imageMediumForID = new Hashtable<String,ImageInfo>();
	private Hashtable<String,ImageInfo> imageLargeForID = new Hashtable<String,ImageInfo>();
	
	private List<ImageInfo> imagesThumbnail = new ArrayList<ImageInfo>();
	private List<ImageInfo> imagesSmall = new ArrayList<ImageInfo>();
	private List<ImageInfo> imagesMedium = new ArrayList<ImageInfo>();
	private List<ImageInfo> imagesLarge = new ArrayList<ImageInfo>();
	
	private String sessionsRootDir = null;
	private String sessionDatasetDir = null;
	// TODO? imagesToInclude and imagesToExclude are now ImageInfo - may need to translate that to BisqueImage at some point.
	private List<ImageInfo> imagesToInclude = null;
	private List<ImageInfo> imagesToExclude = null;
	private SegmentationSessionData ssd = null;
	private OrientationSessionData osd = null;
	
	private BisqueAnnotation currentCharacter = null;
	public BisqueSessionData(String sessionsRootParent) throws AvatolCVException {
		File f = new File(sessionsRootParent);
		if (!f.isDirectory()){
			throw new AvatolCVException("directory does not exist for being sessionsRootParent " + sessionsRootParent);
		}
		
		this.sessionsRootDir = sessionsRootParent + FILESEP + "sessions";
		f = new File(this.sessionsRootDir);
		if (!f.isDirectory()){
			f.mkdirs();
		}
	}
	/*
	 * DATASETS
	 */
	public void setChosenDataset(BisqueDataset s){
		this.dataset = s;
		// ensure dir exists for this 
	    this.sessionDatasetDir = this.sessionsRootDir + FILESEP + this.dataset.getName();
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
		FileUtils.ensureDirExists(getImagesThumbnailDir());
		FileUtils.ensureDirExists(getImagesSmallDir());
		FileUtils.ensureDirExists(getImagesMediumDir());
		FileUtils.ensureDirExists(getImagesLargeDir());
		
	}
	public void clearImageDirs(){
	    FileUtils.clearDir(getImagesThumbnailDir());
	    FileUtils.clearDir(getImagesSmallDir());
		FileUtils.clearDir(getImagesMediumDir());
		FileUtils.clearDir(getImagesLargeDir());
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
    public String getImagesSmallDir(){
    	return getImagesDir() + FILESEP + "small";
    } 
    public String getImagesMediumDir(){
    	return getImagesDir() + FILESEP + "medium";
    } 
    public String getImagesLargeDir(){
    	return getImagesDir() + FILESEP + "large";
    }
    
	public static void generateImageInfoForSize(List<ImageInfo> listToFill, List<BisqueImage> bisqueImages, String width, String dir){
		for (BisqueImage bi : bisqueImages){
			String[] nameParts = ClassicSplitter.splitt(bi.getName(),'.');
			String name = nameParts[0];
			// we use underscore as delimiter so can't have them in filename
			String normalizedName = name.replaceAll("_", "-");
			ImageInfo ii = new ImageInfo(dir, bi.getResourceUniq(), normalizedName, width, "", STANDARD_IMAGE_FILE_EXTENSION);
			ii.setNameAsUploadedOriginalForm(name);
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
		generateImageInfoForSize(imagesSmall,   bisqueImages,IMAGE_SMALL_WIDTH,    getImagesSmallDir());
		for (ImageInfo ii : imagesSmall){
			String id = ii.getID();
			imageSmallForID.put(id, ii);
		}
		generateImageInfoForSize(imagesLarge,    bisqueImages,IMAGE_LARGE_WIDTH,     getImagesLargeDir());
		for (ImageInfo ii : imagesLarge){
			String id = ii.getID();
			imageLargeForID.put(id, ii);
		}
	}
	public void createSegmentationSessionData(String sourceImageDirPath) throws AvatolCVException {
	    this.ssd = new SegmentationSessionData(this.sessionDatasetDir, sourceImageDirPath);
	}
	public ImageInfo getThumbnailForId(String id) throws AvatolCVException {
		ImageInfo ii = null;
		ii = thumbnailForID.get(id);
		if (null == ii){
			throw new AvatolCVException("no thumbnail exists for ID " + id);
		}
		return ii;
	}
	public ImageInfo getImageSmallForId(String id) throws AvatolCVException {
		ImageInfo ii = null;
		ii = imageSmallForID.get(id);
		if (null == ii){
			throw new AvatolCVException("no small size image exists for ID " + id);
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
	public List<ImageInfo> getImagesSmall(){
		return this.imagesSmall;
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
	/*
	 * Orientation
	 * 
	 */
    // TODO - have to decide when to set this, or if even this class should have references to the subsessionDatas - maybe
    // the wrapper step should won it entirely - might be cleaner
    public OrientationSessionData getOrientationSessionData(){
        return this.osd;
    }
	/*
	 * Scoring
	 */
	public void setCurrentCharacter(BisqueAnnotation character){
	    this.currentCharacter = character;
	}
	public BisqueAnnotation getCurrentCharacter(){
	    return this.currentCharacter;
	}
    @Override
    public String getCharQuestionsSourcePath() throws AvatolCVException {
        String charQuestionsDir = AvatolCVFileSystem.getCharacterQuestionsDir();
        String path = charQuestionsDir + FILESEP + BISQUE_CHAR_QUESIONS_FILENAME;
        return path;
    }
    @Override
    public String getCharQuestionsAnsweredQuestionsPath()
            throws AvatolCVException {
        BisqueAnnotation ba = getCurrentCharacter();
        if (null == ba){
            throw new AvatolCVException("undefined currentCharacter prevents generation of pathname");
        }
        String path = sessionDatasetDir + FILESEP + ba.getAnnotationID() + "_" + ba.getName() + FILESEP + "charQuestionAnswers.txt";
        return path;
    }
    @Override
    public void setChosenAlgorithm(String s) {
        // TODO Auto-generated method stub
        
    }
    @Override
    public String getTrainingTestingDescriminatorName() {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public List<MBTaxon> getTaxa() {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public MBMatrix getChosenMatrix() {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public List<MBCharacter> getChosenCharacters() {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public void setImagesForCell(String matrixID, String charID,
            String taxonID, List<MBMediaInfo> mediaInfos) {
        // TODO Auto-generated method stub
        
    }
    @Override
    public List<MBMediaInfo> getImagesForCell(String matrixID, String charID,
            String taxonID) {
        // TODO Auto-generated method stub
        return null;
    }
}
