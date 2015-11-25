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
		return (isExcludedForDataset() || isExcludedForSession());
	}
	
	public boolean isExcludedForDataset() throws AvatolCVException {
		String path = AvatolCVFileSystem.getDatasetExclusionInfoFilePath(this.ID);
		File f = new File(path);
		return f.exists();
	}
	public boolean isExcludedForSession() throws AvatolCVException {
		String path = AvatolCVFileSystem.getSessionExclusionInfoFilePath(this.ID);
		File f = new File(path);
		return f.exists();
	}
	public void undoExclude() throws AvatolCVException {
		if (isExcludedForSession()){
			String path = AvatolCVFileSystem.getSessionExclusionInfoFilePath(this.ID);
			File f = new File(path);
			f.delete();
		}
		else if (isExcludedForDataset()){
			String path = AvatolCVFileSystem.getDatasetExclusionInfoFilePath(this.ID);
			File f = new File(path);
			f.delete();
		}
		
	}
	public void excludeForReason(String s, boolean excludeJustForSession) throws AvatolCVException {
		String path = null;
	    if (excludeJustForSession){
	    	path = AvatolCVFileSystem.getSessionExclusionInfoFilePath(this.ID);
	    }
	    else {
	    	path = AvatolCVFileSystem.getDatasetExclusionInfoFilePath(this.ID);
	    }
		
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(path));
			writer.write(s + NL);
			writer.close();
		}
	    catch(IOException ioe){
	    	throw new AvatolCVException("problem writing exclusion state to path " + path);
	    }
	}
	public String getExclusionReason() throws AvatolCVException {
		String path = null;
		if (isExcludedForSession()){
			path = AvatolCVFileSystem.getSessionExclusionInfoFilePath(this.ID);
		}
		else if (isExcludedForDataset()){
			path = AvatolCVFileSystem.getDatasetExclusionInfoFilePath(this.ID);
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
