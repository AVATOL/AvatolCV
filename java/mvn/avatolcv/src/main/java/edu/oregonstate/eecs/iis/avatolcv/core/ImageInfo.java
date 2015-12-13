package edu.oregonstate.eecs.iis.avatolcv.core;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Hashtable;

import edu.oregonstate.eecs.iis.avatolcv.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.AvatolCVFileSystem;

public class ImageInfo {
    public static final String IMAGE_THUMBNAIL_WIDTH = "80";
    public static final String IMAGE_THUMBNAIL_STRING = "thumbnail";
    public static final String IMAGE_LARGE_WIDTH = "1000";
    public static final String IMAGE_LARGE_STRING = "large";
    
    public static final String STANDARD_IMAGE_FILE_EXTENSION = "jpg";
    
	private static final String FILESEP = System.getProperty("file.separator");	
	private static final String NL = System.getProperty("line.separator");
	public static final String EXCLUSION_REASON_IMAGE_QUALITY = "imageQuality";
	public static final String EXCLUSION_REASON_ORIENTATION = "imageOrientation";
    public static final String EXCLUSION_REASON_UNAVAILABLE = "imageUnavailable";
    public static final String EXCLUSION_REASON_MISSING_ANNOTATION = "trainingSampleMissingPointAnnotation";

	private String nameAsUploadedNormalized = null;
	private String nameAsUploadedOriginal = null;
	private String ID = null;
	private String filename = null;
	private String parentDir = null;
	private String filepath = null;
	private String imageWidth = null;
	private String ID_name = null;
	private String ID_name_imageWidth = null;
	private String ID_name_imagewidth_type = null;
	private String outputType = null;
	private String extension = null;
	private ImageInfo ancestorImage = null;
    private static Hashtable<String, String> imageSizeNameHash = new Hashtable<String, String>();
    static {
        imageSizeNameHash.put(IMAGE_LARGE_WIDTH, IMAGE_LARGE_STRING);
        imageSizeNameHash.put(IMAGE_THUMBNAIL_WIDTH, IMAGE_THUMBNAIL_STRING);
    }
	public ImageInfo(String parentDir, String ID, String nameAsUploadedNormalized, String imageWidth, String outputType, String extension)  {
		this.parentDir = parentDir;      
		this.ID = ID;
		this.nameAsUploadedNormalized = nameAsUploadedNormalized;
		this.imageWidth = imageWidth;
		this.outputType = outputType;
		this.extension = extension;
		this.ID_name = ID + "_" + nameAsUploadedNormalized;
		this.ID_name_imageWidth = this.ID_name +  "_" + imageWidth;
		this.ID_name_imagewidth_type = this.ID_name_imageWidth + "_" + outputType;
		ingestOutputType();
	}
	private void ingestOutputType(){
	 // filename
        if (this.outputType.equals("")){
            this.filename = this.ID_name_imageWidth + "." + extension;
        }
        else {
            this.filename = this.ID_name_imagewidth_type + "." + extension;
        }
        
        this.filepath = this.parentDir + FILESEP + this.filename;
	}
	public String getImageSizeName(){
	    return imageSizeNameHash.get(imageWidth);
	}
	public String getParentDir(){
	    return this.parentDir;
	}
    public String getOutputType(){
        return this.outputType;
    }
	public String getFilename_IdName(){
		return this.ID_name;
	}
	public String getFilename_IdNameWidth(){
		return this.ID_name_imageWidth;
	}
	public String getFilename_IdNameWidthType(){
		return this.ID_name_imagewidth_type;
	}
	public String getNameAsUploadedNormalized(){
		return this.nameAsUploadedNormalized;
	}
	public String getID(){
		return this.ID;
	}
	public String getFilename(){
		return this.filename;
	}
	public String getFilepath(){
		return this.filepath;
	}
	public String getImageWidth(){
		return this.imageWidth;
	}
	public String getExtension(){
		return this.extension;
	}
	public void setNameAsUploadedOriginalForm(String originalName){
		this.nameAsUploadedOriginal = originalName;
	}
	public String getNameAsUploadedOriginalForm(){
		return this.nameAsUploadedOriginal;
	}
	//public ImageInfo clone() {
	//    ImageInfo clone = new ImageInfo(this.parentDir, this.ID, this.nameAsUploadedNormalized, this.imageWidth, this.outputType, this.extension);
	//    clone.setNameAsUploadedOriginalForm(this.nameAsUploadedOriginal);
	//    return clone;
	//}
	public void setOutputType(String outputType){
	    this.outputType = outputType;
	    ingestOutputType();
	}
	public void setAncestorImage(ImageInfo ancestorImage){
	    this.ancestorImage = ancestorImage;
	}
	public ImageInfo getAncestorImage(){
	    return this.ancestorImage;
	}
	/*
	 * challenge - what if nameAsUploaded has underscores - need to translate underscores to dashes upone initial download
	 */
	public static ImageInfo loadImageInfoFromFilename(String filename, String parentDir) throws AvatolCVException {
	    String[] filenameParts = filename.split("\\.");
        String rootName = filenameParts[0];
        String extension = filenameParts[1];
        String[] rootNameParts = rootName.split("_");
        String ID = rootNameParts[0];
        String nameAsUploaded = rootNameParts[1];
        String imageWidth = rootNameParts[2];
        String outputType = "?";
        if (rootNameParts.length == 3){
            outputType = "";
        }
        else if (rootNameParts.length == 4){
            outputType = rootNameParts[3];
        }
        else {
            throw new AvatolCVException("more rootNameParts than expected in image filename " + filename);
        }
        ImageInfo ii = new ImageInfo(parentDir, ID, nameAsUploaded, imageWidth, outputType, extension);
        return ii;
	}
	/*
	 * Exclusion
	 */
	
	public boolean isExcluded() throws AvatolCVException {
		return isExcluded(this.ID);
	}
	public static boolean isExcluded(String imageID) throws AvatolCVException {
		return (isExcludedForDataset(imageID) || isExcludedForSession(imageID));
	}
	
	public static boolean isExcludedForDataset(String imageID) throws AvatolCVException {
		String path = AvatolCVFileSystem.getDatasetExclusionInfoFilePath(imageID);
		File f = new File(path);
		return f.exists();
	}
	public static boolean isExcludedForSession(String imageID) throws AvatolCVException {
		String path = AvatolCVFileSystem.getSessionExclusionInfoFilePath(imageID);
		File f = new File(path);
		return f.exists();
	}
	
	
	/*public void undoExclude() throws AvatolCVException {
		if (isExcludedForSession(this.ID)){
			String path = AvatolCVFileSystem.getSessionExclusionInfoFilePath(this.ID);
			File f = new File(path);
			f.delete();
		}
		else if (isExcludedForDataset(this.ID)){
			String path = AvatolCVFileSystem.getDatasetExclusionInfoFilePath(this.ID);
			File f = new File(path);
			f.delete();
		}
		
	}
	*/
	/**
	 * Undo exclusion for dataset by deleting the file if it contains the specified reason
	 * @param reason
	 * @throws AvatolCVException
	 */
	public void undoExcludeForDataset(String reason) throws AvatolCVException {
		undoExcludeForDataset(reason, this.ID);
	}
	public static void undoExcludeForDataset(String reason, String id) throws AvatolCVException {
		if (isExcludedForDataset(id)){
			String path =  AvatolCVFileSystem.getDatasetExclusionInfoFilePath(id);
			deleteExclusionAtPathForReason(path, reason, id);
		}
	}
	/**
	 * Undo exclusion for session by deleting the file if it contains the specified reason
	 * @param reason
	 * @throws AvatolCVException
	 */
	public void undoExcludeForSession(String reason) throws AvatolCVException {
		undoExcludeForSession(reason, this.ID);
	}
	public static void undoExcludeForSession(String reason, String id) throws AvatolCVException {
		if (isExcludedForSession(id)){
			String path =  AvatolCVFileSystem.getSessionExclusionInfoFilePath(id);
			deleteExclusionAtPathForReason(path, reason, id);
		}
	}
	public static void deleteExclusionAtPathForReason(String path, String reason, String id) throws AvatolCVException {
		try {
			BufferedReader reader = new BufferedReader(new FileReader(path));
			String storedReason = reader.readLine();
			reader.close();
			if (storedReason.equals(reason)){
				File f = new File(path);
				f.delete();
			}
		}
		catch(IOException ioe){
			throw new AvatolCVException("problem re-including image " + id + " : " + ioe.getMessage());
		}
	}
	/**
	 * 
	 * @param reason
	 * @throws AvatolCVException
	 */
	public void excludeForDataset(String reason)  throws AvatolCVException {
		excludeForDataset(reason, this.ID);
	}
	public static void excludeForDataset(String reason, String ID) throws AvatolCVException {
	    String path =  AvatolCVFileSystem.getDatasetExclusionInfoFilePath(ID);
	    excludeAtPath(path, reason);
	}
	 
	public void excludeForSession(String reason) throws AvatolCVException {
		excludeForSession(reason, this.ID);
	}
	public static void excludeForSession(String reason, String ID) throws AvatolCVException {
		String path = AvatolCVFileSystem.getSessionExclusionInfoFilePath(ID);
	    excludeAtPath(path, reason);
	}
   
    public static void excludeAtPath(String path, String reason) throws AvatolCVException {
    	try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(path));
			writer.write(reason + NL);
			writer.close();
		}
	    catch(IOException ioe){
	    	throw new AvatolCVException("problem writing exclusion reason " + reason + "  to path " + path);
	    }
    }
	
	public String getExclusionReason() throws AvatolCVException {
		return getExclusionReason(this.ID);
	}
	public static String getExclusionReason(String imageID) throws AvatolCVException {
		String path = null;
		if (isExcludedForSession(imageID)){
			path = AvatolCVFileSystem.getSessionExclusionInfoFilePath(imageID);
		}
		else if (isExcludedForDataset(imageID)){
			path = AvatolCVFileSystem.getDatasetExclusionInfoFilePath(imageID);
		}
		else {
			path = null;
		}
		if (null == path){
			return "-no exclusion reason found-";
		}
		else {
			try {
				BufferedReader reader = new BufferedReader(new FileReader(path));
				String line = reader.readLine();
				reader.close();
				return line;
			}
		    catch(IOException ioe){
		    	throw new AvatolCVException("problem readin exclusion state from path " + path);
		    }
		}
		
	}
	
}
