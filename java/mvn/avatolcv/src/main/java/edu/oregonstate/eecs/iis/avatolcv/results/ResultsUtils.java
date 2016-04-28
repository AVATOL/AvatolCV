package edu.oregonstate.eecs.iis.avatolcv.results;

import java.io.File;

import edu.oregonstate.eecs.iis.avatolcv.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.AvatolCVFileSystem;
import edu.oregonstate.eecs.iis.avatolcv.core.ImageInfo;


public class ResultsUtils {
	public static boolean isConfidenceStringLessThanThreshold(String confString, String thresholdString){
        Double confDouble = new Double(confString);
        Double threshDouble = new Double(thresholdString);
        if (confDouble.doubleValue() < threshDouble.doubleValue()){
            return true;
        }
        return false;
    }
	public static String limitToTwoDecimalPlaces(String conf){
        Double confDouble = new Double(conf);
        return String.format("%.2f", confDouble);
    }
    public static String getTrueImageNameFromImagePath(String imagePath) throws AvatolCVException {
        String imageDirPath = AvatolCVFileSystem.getNormalizedImagesLargeDir();
        String imageID = ImageInfo.getImageIDFromPath(imagePath);
        File imageDir = new File(imageDirPath);
        File[] files = imageDir.listFiles();
        for (File f : files){
            String fname = f.getName();
            if (fname.contains(imageID)){
                return fname;
            }
        }
        return null;
    }
  
    
    public static String getThumbnailPathWithImagePath(String imagePath) throws AvatolCVException {
    	String thumbnailDirPath = AvatolCVFileSystem.getNormalizedImagesThumbnailDir();
        String imageID = ImageInfo.getImageIDFromPath(imagePath);
    	File thumbnailDir = new File(thumbnailDirPath);
    	File[] files = thumbnailDir.listFiles();
    	for (File f : files){
    		String fname = f.getName();
    		if (fname.contains(imageID)){
    			return f.getAbsolutePath();
    		}
    	}
    	return null;
    }
    
}
