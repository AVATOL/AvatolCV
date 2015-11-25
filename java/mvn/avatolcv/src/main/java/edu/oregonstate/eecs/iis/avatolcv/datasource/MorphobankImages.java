package edu.oregonstate.eecs.iis.avatolcv.datasource;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import edu.oregonstate.eecs.iis.avatolcv.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.AvatolCVFileSystem;
import edu.oregonstate.eecs.iis.avatolcv.core.DatasetInfo;
import edu.oregonstate.eecs.iis.avatolcv.core.ImageInfo;
import edu.oregonstate.eecs.iis.avatolcv.core.ProgressPresenter;
import edu.oregonstate.eecs.iis.avatolcv.ws.MorphobankWSClient;
import edu.oregonstate.eecs.iis.avatolcv.ws.MorphobankWSException;
import edu.oregonstate.eecs.iis.avatolcv.ws.morphobank.CellMediaInfo.MBMediaInfo;

public class MorphobankImages {
    private MorphobankWSClient wsClient = null;
    private List<MBMediaInfo> mbImages = null;
    private Hashtable<String,MBMediaInfo> mbImageForID = new Hashtable<String,MBMediaInfo>();
    private Hashtable<String,ImageInfo> thumbnailForID = new Hashtable<String,ImageInfo>();
    private Hashtable<String,ImageInfo> imageLargeForID = new Hashtable<String,ImageInfo>();
    
    private List<ImageInfo> imagesThumbnail = new ArrayList<ImageInfo>();
    private List<ImageInfo> imagesLarge = new ArrayList<ImageInfo>();
    
    public  MorphobankImages(ProgressPresenter pp, MorphobankWSClient wsClient, DatasetInfo dataset, String processName, MorphobankDataSource dataSource) throws AvatolCVException {
        this.wsClient = wsClient;
        setCurrentImages(dataSource.getMBMediaInfoForSession());
        downloadImagesForSession(pp, processName);
    }
    public boolean robustImageDownload(ProgressPresenter pp, ImageInfo ii, String targetDir , String processNameFileDownload) throws AvatolCVException {
        int maxRetries = 4;
        int tries = 0;
        boolean imageNotYetDownloaded = true;
        Exception mostRecentException = null;
        while (maxRetries > tries && imageNotYetDownloaded){
            try {
                tries++;
                //progressMessage(pp, processNameFileDownload, "downloading image  : " + ii.getID());
                // specify original image filename as "" since it isn't known from web service response
                this.wsClient.downloadImageForMediaId(targetDir, ii.getID(), "", ii.getImageSizeName(), ii.getImageWidth());
                imageNotYetDownloaded = false;
            }
            catch(MorphobankWSException e){
                if (e.getMessage().equals("timeout")){
                    progressMessage(pp, processNameFileDownload, "download timed out - retrying image : " + ii.getID() + " - attempt " + (tries+1));
                }
                mostRecentException = e;
            }
        }
        if (imageNotYetDownloaded){
            throw new AvatolCVException("problem downloading image: " + mostRecentException);
        }
        return true;
    }
    public boolean downloadImageIfNeeded(ProgressPresenter pp, ImageInfo image, String targetDir, String processNameFileDownload) throws AvatolCVException {
        String imagePath = image.getFilepath();
        File imageFile = new File(imagePath);
        if (imageFile.exists()){
            //progressMessage(pp, processNameFileDownload, "already have image : " + image.getID());
        }
        else {
            robustImageDownload(pp, image, targetDir, processNameFileDownload);
        }
        File f = new File(imagePath);
        if (f.exists()){
            return true;
        }
        return false;
    }
   
    public void progressUpdate(ProgressPresenter pp, String processName, double percentDone){
        if (null!= pp){
            pp.updateProgress(processName, percentDone);
        }
    }
    public void progressMessage(ProgressPresenter pp, String processName, String message){
        if (null != pp){
            pp.setMessage(processName, message);
        }
    }
    public void downloadImagesForSession(ProgressPresenter pp, String processName) throws AvatolCVException {
        List<ImageInfo> imagesThumbnailUnique = new ArrayList<ImageInfo>();
        List<ImageInfo> imagesLargeUnique = new ArrayList<ImageInfo>();
        List<String> uniqueMediaIdsThumbnail = new ArrayList<String>();
        List<String> uniqueMediaIdsLarge = new ArrayList<String>();
        for (ImageInfo ii : imagesLarge){
            if (!uniqueMediaIdsLarge.contains(ii.getID())){
                uniqueMediaIdsLarge.add(ii.getID());
                imagesLargeUnique.add(ii);
            }
        }
        for (ImageInfo ii : imagesThumbnail){
            if (!uniqueMediaIdsThumbnail.contains(ii.getID())){
                uniqueMediaIdsThumbnail.add(ii.getID());
                imagesThumbnailUnique.add(ii);
            }
        }
        double curCount = 0;
        int successCount = 0;
        int imageCount = imagesLargeUnique.size() * 2;
        for (ImageInfo image : imagesLargeUnique){
            curCount++;
            if (downloadImageIfNeeded(pp,image,AvatolCVFileSystem.getNormalizedImagesLargeDir(),processName)){
                successCount++;
            }
            double percentDone = curCount / imageCount;
            progressUpdate(pp, processName, percentDone);
            if (percentDone == 1.0){
                progressMessage(pp, processName, "Image download complete.");
            }
            else {
                progressMessage(pp, processName, "image " + (int)curCount + " of " + (int)imageCount + " id " + image.getID());
            }
            
        }
        for (ImageInfo image : imagesThumbnailUnique){
            curCount++;
            if (downloadImageIfNeeded(pp,image, AvatolCVFileSystem.getNormalizedImagesThumbnailDir(),processName)){
                successCount++;
            }
            double percentDone = curCount / imageCount;
            progressUpdate(pp, processName, percentDone);
            if (percentDone == 1.0){
                progressMessage(pp, processName, "Image download complete.");
            }
            else {
                progressMessage(pp, processName, "image " + (int)curCount + " of " + (int)imageCount + " id " + image.getID());
            }
        }
        
        if (successCount < curCount){
            int badCount = (int)curCount - successCount;
            throw new AvatolCVException(badCount + " images didn't download correctly from Morphobank");
        }
    }
    public void setCurrentImages(List<MBMediaInfo> mbImages) throws AvatolCVException {
        this.mbImages = mbImages;
        for (MBMediaInfo mi : mbImages){
            String id = mi.getMediaID();
            mbImageForID.put(id, mi);
        }
        generateImageInfoForSize(imagesThumbnail,mbImages,ImageInfo.IMAGE_THUMBNAIL_WIDTH, AvatolCVFileSystem.getNormalizedImagesThumbnailDir());
        for (ImageInfo ii : imagesThumbnail){
            String id = ii.getID();
            thumbnailForID.put(id, ii);
        }
        
        generateImageInfoForSize(imagesLarge,    mbImages,ImageInfo.IMAGE_LARGE_WIDTH,     AvatolCVFileSystem.getNormalizedImagesLargeDir());
        for (ImageInfo ii : imagesLarge){
            String id = ii.getID();
            imageLargeForID.put(id, ii);
        }
    }
    public static void generateImageInfoForSize(List<ImageInfo> listToFill, List<MBMediaInfo> mbImages, String width, String dir){
        for (MBMediaInfo mi : mbImages){
            String name = mi.getName(); // this will always be ""
            ImageInfo ii = new ImageInfo(dir, mi.getMediaID(), name, width, "", ImageInfo.STANDARD_IMAGE_FILE_EXTENSION);
            ii.setNameAsUploadedOriginalForm(name);
            listToFill.add(ii);
        }
    }
}
