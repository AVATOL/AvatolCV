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
	private String ID_name_imagewidth_type = null;
	private String outputType = null;
	private String extension = null;
	private ImageInfo ancestorImage = null;
	public ImageInfo(String parentDir, String ID, String nameAsUploaded, String imageWidth, String outputType, String extension){
		this.parentDir = parentDir;
		this.ID = ID;
		this.nameAsUploaded = nameAsUploaded;
		this.imageWidth = imageWidth;
		this.outputType = outputType;
		this.extension = extension;
		this.ID_name = ID + "_" + nameAsUploaded;
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
	public ImageInfo clone(){
	    ImageInfo clone = new ImageInfo(this.parentDir, this.ID, this.nameAsUploaded, this.imageWidth, this.outputType, this.extension);
	    return clone;
	}
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
}
