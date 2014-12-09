package edu.oregonstate.eecs.iis.avatolcv.mb;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MatrixCellImageUnit {
	private static final String FILESEP = System.getProperty("file.separator");
	private String mediaId;
	private String viewId;
	private MatrixCell containerCell;
	public MatrixCellImageUnit(MatrixCell containerCell,String mediaId, String viewId){
		this.containerCell = containerCell;
		this.mediaId = mediaId;
		this.viewId = viewId;
	}
	public String toString(){
		return getCharId() + "_" + getTaxonId() + "_" + getViewId() + "_" + getMediaId();
	}
	public String getTaxonId(){
		return this.containerCell.getTaxonId();
	}
	public String getCharId(){
		return this.containerCell.getCharId();
	}
	public String getMediaId(){
		return this.mediaId;
	}
	public String getStateId(){
		return this.containerCell.getState();
	}
	public String getViewId(){
		return this.viewId;
	}
	public boolean isUnscored(){
		return this.containerCell.isUnscored();
	}
	public boolean hasWorkableScore(){
		return this.containerCell.hasWorkableScore();
	}
	public boolean hasAnnotationFile(String annotationDir){
		String filename = mediaId + "_" + getCharId() + ".txt";
    	String path = annotationDir + FILESEP + filename;
    	File f = new File(path);
    	if (f.exists()){
    		return true;
    	}
    	return false;
	}
	
}
