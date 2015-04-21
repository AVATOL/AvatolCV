package edu.oregonstate.eecs.iis.avatolcv.core;

public class ImageInfo {
	private static final String FILESEP = System.getProperty("file.separator");
	private String nameAsUploaded = null;
	private String ID = null;
	private String filename = null;
	private String parentDir = null;
	private String filepath = null;
	private String imageWidth = null;
	private String ID_name = null;
	private String ID_name_imageWidth = null;
	private String extension = null;
	public ImageInfo(String parentDir, String ID, String nameAsUploaded, String imageWidth, String extension){
		this.parentDir = parentDir;
		this.ID = ID;
		this.nameAsUploaded = nameAsUploaded;
		this.imageWidth = imageWidth;
		this.extension = extension;
		this.ID_name = ID + "_" + nameAsUploaded;
		this.ID_name_imageWidth = this.ID_name +  "_" + imageWidth;
		this.filename = this.ID_name_imageWidth + "." + extension;
		this.filepath = this.parentDir + FILESEP + this.filename;
	}
	public String getFilename_IdName(){
		return this.ID_name;
	}
	public String getFilename_IdNameWidth(){
		return this.ID_name_imageWidth;
	}
	public String getNameAsUploaded(){
		return this.nameAsUploaded;
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
	
}
