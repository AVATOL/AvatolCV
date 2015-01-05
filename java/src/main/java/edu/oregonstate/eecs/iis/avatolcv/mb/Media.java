package edu.oregonstate.eecs.iis.avatolcv.mb;

import java.io.File;
import java.util.Hashtable;

public class Media {
	private static final String FILESEP = System.getProperty("file.separator");
    public static final String MEDIA_DIRNAME = "media";
	private String bundleDir = null;
	private String mediaDir = null;
	private Hashtable<String, String> mediaPathForMediaId = new Hashtable<String, String>();
	private Hashtable<String, String> relativeMediaPathForMediaId = new Hashtable<String, String>();
	private Hashtable<String, String> mediaFilenameForMediaId = new Hashtable<String, String>();
	private Hashtable<String, String> mediaIdForRelativeMediaPath = new Hashtable<String, String>();
	private Hashtable<String, String> mediaIdForMediaPath = new Hashtable<String, String>();
	private Hashtable<String, String> mediaIdForMediaFilename = new Hashtable<String, String>();
	
    public Media(String bundleDir){
    	this.bundleDir = bundleDir;
    	this.mediaDir = this.bundleDir + FILESEP +  MEDIA_DIRNAME;
    	findMedia();
    }
    /*
     * Media filenames look like M151254_.jpg, where the prefix is the mediaId, but with capital M
     * CHANGE
     * As of 1/1/2015, media files look like M151254.jpg, where the root is the mediaId, but with capital M - no underscores!
     */
    public void findMedia(){
    	String[] filenames = new File(mediaDir).list();
        for (int i=0; i < filenames.length; i++){
        	String filename = filenames[i];
        	String[] parts = filename.split("\\.");
        	String mediaId = parts[0].replace('M', 'm');
        	String pathname = this.mediaDir + FILESEP + filename;
        	String relativePathname = MEDIA_DIRNAME + FILESEP + filename;
        	mediaPathForMediaId.put(mediaId, pathname);
        	mediaFilenameForMediaId.put(mediaId, filename);
        	mediaIdForRelativeMediaPath.put(relativePathname, mediaId);
        	mediaIdForMediaPath.put(pathname, mediaId);
        	mediaIdForMediaFilename.put(filename, mediaId);
        	relativeMediaPathForMediaId.put(mediaId, relativePathname);
        }
    }
    
    public boolean isMediaIdBackedByMediaFile(String mediaId){
    	String path = this.mediaPathForMediaId.get(mediaId);
    	if (null == path) {
    		return false;
    	}
    	return true;
    }
    public String getMediaFilenameForMediaId(String mediaId) /*throws MorphobankDataException*/ {
    	String filename = this.mediaFilenameForMediaId.get(mediaId);
    	//if (null == filename){
    	//	throw new MorphobankDataException("no media file present for mediaId " + mediaId + " in bundle " + this.bundleDir);
    	//}
    	return filename;
    }
    public String getRelativeMediaPathnameForMediaId(String mediaId)  {
    	String path = this.relativeMediaPathForMediaId.get(mediaId);
    	return path;
    }
    public String getMediaPathnameForMediaId(String mediaId)  {
    	String path = this.mediaPathForMediaId.get(mediaId);
    	return path;
    }
    public String getMediaIdForMediaPath(String pathname)  {
    	String mediaId = this.mediaIdForMediaPath.get(pathname);
    	return mediaId;
    }
    public String getMediaIdForRelativeMediaPath(String relativePathname)  {
    	String mediaId = this.mediaIdForRelativeMediaPath.get(relativePathname);
    	return mediaId;
    }
}
