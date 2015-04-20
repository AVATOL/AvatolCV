package edu.oregonstate.eecs.iis.avatolcv.core;

public class ImageInfo {
	private String name = null;
	private String ID = null;
	private String filename = null;
	private String filepath = null;
	private int imageWidth = -1;
	private String rootName = null;
	public ImageInfo(){
		
	}
	public String getFilenameRoot(){
		return this.rootName;
	}
	public String getName(){
		return this.name;
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
	public int getImageWidth(){
		return this.imageWidth;
	}
	public void setFilenameRoot(String root){
		this.rootName = root;
	}
    public void setImageWidth(int width){
    	this.imageWidth = width;
    }
	public void setName(String s){
		this.name = s;
	}
	public void setID(String s){
		this.ID = s;
	}
	public void setFilename(String s){
		this.filename = s;
	}
	public void setFilepath(String s){
		this.filepath = s;
	}
}
