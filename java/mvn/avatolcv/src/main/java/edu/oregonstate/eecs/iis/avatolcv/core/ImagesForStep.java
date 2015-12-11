package edu.oregonstate.eecs.iis.avatolcv.core;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import edu.oregonstate.eecs.iis.avatolcv.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.AvatolCVFileSystem;

public class ImagesForStep {

    private static final String NL = System.getProperty("line.separator");
	private String largeImageDir = null;
	private String thumbImageDir = null;
	private List<ImageInfo> largeImages = new ArrayList<ImageInfo>();
	private List<ImageInfo> thumbImages = new ArrayList<ImageInfo>();

    private Hashtable<String,ImageInfo> thumbnailForID = new Hashtable<String,ImageInfo>();
    private Hashtable<String,ImageInfo> imageLargeForID = new Hashtable<String,ImageInfo>();
    
	public ImagesForStep(String largeImageDir, String thumbImageDir) throws AvatolCVException {
		this.largeImageDir = largeImageDir;
		this.thumbImageDir = thumbImageDir;
		load();
		mapImages();
	}
	private void mapImages(){
		for (ImageInfo ii : this.largeImages){
			String id = ii.getID();
			imageLargeForID.put(id, ii);
		}
		for (ImageInfo ii : this.thumbImages){
			String id = ii.getID();
		    thumbnailForID.put(id, ii);
		}
	}
	private void load() throws AvatolCVException  {
		largeImages = loadImages(this.largeImageDir);
		thumbImages = loadImages(this.thumbImageDir);
	}
	private List<ImageInfo> loadImages(String dirPath) throws AvatolCVException {
		List<ImageInfo> iis = new ArrayList<ImageInfo>();
		File dirFile = new File(dirPath);
		File[] files = dirFile.listFiles();
		for (File f : files){
			ImageInfo ii = ImageInfo.loadImageInfoFromFilename(f.getName(), f.getParent());
			iis.add(ii);
		}
		return iis;
	}
	public List<ImageInfo> getImagesLarge(){
		return this.largeImages;
	}
	public List<ImageInfo> getImagesThumbnail(){
		return this.thumbImages;
	}
	public ImageInfo getLargeImageForImage(ImageInfo ii) throws AvatolCVException {
    	String imageID = ii.getID();
    	return getLargeImageForID(imageID);
    }
	public ImageInfo getLargeImageForID(String imageID) throws AvatolCVException {
    	ImageInfo large = this.imageLargeForID.get(imageID);
    	if (null == large){
    		throw new AvatolCVException("no large image found with imageID " + imageID);
    	}
    	return large;
    }
	public ImageInfo getThumbnailImageForID(String imageID) throws AvatolCVException {
    	ImageInfo tn = this.thumbnailForID.get(imageID);
    	if (null == tn){
    		throw new AvatolCVException("no thumbnail image found with imageID " + imageID);
    	}
    	return tn;
    }
	 /*
     * Rotation vertical
     */
    public boolean isRotatedVertically(ImageInfo ii) throws AvatolCVException {
        String path = AvatolCVFileSystem.getRotateVerticallyPath(ii.getID());
        File f = new File(path);
        if (f.exists()){
            return true;
        } 
        return false;
    }
    
    public void rotateVertically(ImageInfo ii) throws AvatolCVException {
        String path = AvatolCVFileSystem.getRotateVerticallyPath(ii.getID());
        File f = new File(path);
        if (f.exists()){
            f.delete();
        }
        else {
            try {
                BufferedWriter writer = new BufferedWriter(new FileWriter(path));
                writer.write(AvatolCVFileSystem.ROTATE_VERTICALLY + NL);
                writer.close();
            }
            catch(IOException ioe){
                throw new AvatolCVException("problem writing rotateVertically state to path " + path);
            }
        }
    }
    
   
    /*
     * Rotation horizontal
     */
    public boolean isRotatedHorizontally(ImageInfo ii) throws AvatolCVException {
        String path = AvatolCVFileSystem.getRotateHorizontallyPath(ii.getID());
        File f = new File(path);
        if (f.exists()){
            return true;
        } 
        return false;
    }
   
    public void rotateHorizontally(ImageInfo ii) throws AvatolCVException {
        String path = AvatolCVFileSystem.getRotateHorizontallyPath(ii.getID());
        File f = new File(path);
        if (f.exists()){
            f.delete();
        }
        else {
            try {
                BufferedWriter writer = new BufferedWriter(new FileWriter(path));
                writer.write(AvatolCVFileSystem.ROTATE_HORIZONTALLY + NL);
                writer.close();
            }
            catch(IOException ioe){
                throw new AvatolCVException("problem writing rotateHorizontally state to path " + path);
            }
        }
    }
}
