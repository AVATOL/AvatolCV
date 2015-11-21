package edu.oregonstate.eecs.iis.avatolcv.datasource;

import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import edu.oregonstate.eecs.iis.avatolcv.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.AvatolCVFileSystem;
import edu.oregonstate.eecs.iis.avatolcv.core.DatasetInfo;
import edu.oregonstate.eecs.iis.avatolcv.core.ImageInfo;
import edu.oregonstate.eecs.iis.avatolcv.core.ProgressPresenter;
import edu.oregonstate.eecs.iis.avatolcv.ws.BisqueWSClient;
import edu.oregonstate.eecs.iis.avatolcv.ws.BisqueWSException;
import edu.oregonstate.eecs.iis.avatolcv.ws.bisque.BisqueImage;

public class BisqueImages {
    private static final String FILESEP = System.getProperty("file.separator");
    private List<BisqueImage> bisqueImages = null;
    private BisqueWSClient wsClient = null;
    private Hashtable<String,BisqueImage> bisqueImageForID = new Hashtable<String,BisqueImage>();
    private Hashtable<String,ImageInfo> thumbnailForID = new Hashtable<String,ImageInfo>();
    private Hashtable<String,ImageInfo> imageLargeForID = new Hashtable<String,ImageInfo>();
    
    private List<ImageInfo> imagesThumbnail = new ArrayList<ImageInfo>();
    private List<ImageInfo> imagesLarge = new ArrayList<ImageInfo>();
    private boolean imagesLoadedSuccessfully = false;

    public  BisqueImages(ProgressPresenter pp, BisqueWSClient wsClient, DatasetInfo dataset, String processName) throws AvatolCVException {
        //sessionData.clearImageDirs();
        this.wsClient = wsClient;
        try {
            String datasetResourceUniq = dataset.getID();
            bisqueImages = wsClient.getImagesForDataset(datasetResourceUniq);
            setCurrentImages(bisqueImages);
            double curCount = 0;
            int successCount = 0;
            
            int imageCount = imagesThumbnail.size() * 2;
            double increment = 1.0 / imageCount;
            for (ImageInfo image : imagesLarge){
                if (image.isExcluded()){
                    // we don't want the fact that we are abstaining from loading to affect the success count
                    successCount++;
                }
                else {
                    if (downloadImageIfNeeded(pp,image,AvatolCVFileSystem.getNormalizedImagesLargeDir(), processName, "image")){
                        successCount++;
                    } 
                }
                curCount++;
                pp.updateProgress(processName,curCount * increment);
            }
           
            for (ImageInfo image : imagesThumbnail){
                if (image.isExcluded()){
                    // we don't want the fact that we are abstaining from loading to affect the success count
                    successCount++;
                }
                else {
                    if (downloadImageIfNeeded(pp,image, AvatolCVFileSystem.getNormalizedImagesThumbnailDir(), processName, "thumbnail")){
                        successCount++;
                    }
                }
                curCount++;
                pp.updateProgress(processName,curCount * increment);
            }
            if (successCount < curCount){
                int badCount = (int)curCount - successCount;
                throw new AvatolCVException(badCount + " images didn't download correctly from bisque");
            }
            //sessionData.createSegmentationSessionData(sessionData.getImagesLargeDir());
            // WHY WAS THIS HERE?
        }
        catch(BisqueWSException e){
            throw new AvatolCVException("problem getting images for dataset " + dataset.getName());
        }
        
    }

    public boolean robustImageDownload(ProgressPresenter pp, ImageInfo ii, String targetDir, String processName, String context) throws AvatolCVException {
        int maxRetries = 4;
        int tries = 0;
        boolean imageNotYetDownloaded = true;
        Exception mostRecentException = null;
        while (maxRetries > tries && imageNotYetDownloaded){
            try {
                tries++;
                pp.setMessage(processName,"downloading " + context + ": " + ii.getNameAsUploadedNormalized());
                int imageWidthAsInt = -1;
                try {
                    imageWidthAsInt = new Integer(ii.getImageWidth()).intValue();
                }
                catch(NumberFormatException nfe){
                    throw new AvatolCVException("could not convert image width " + ii.getImageWidth() + " to a number.");
                }
                this.wsClient.downloadImageOfWidth(ii.getID(), imageWidthAsInt, targetDir, ii.getFilename_IdName());
                imageNotYetDownloaded = false;
            }
            catch(BisqueWSException e){
                if (e.getMessage().equals("timeout")){
                    pp.setMessage(processName,"download timed out - retrying image : " + ii.getNameAsUploadedNormalized() + " - attempt " + (tries+1));
                }
                mostRecentException = e;
            }
        }
        if (imageNotYetDownloaded){
            imagesLoadedSuccessfully = false;
            return false;
        }
        imagesLoadedSuccessfully = true;
        return true;
    }
    public boolean downloadImageIfNeeded(ProgressPresenter pp, ImageInfo image, String targetDir, String processName, String context) throws AvatolCVException {
        String imagePath = image.getFilepath();
        File imageFile = new File(imagePath);
        if (imageFile.exists()){
            pp.setMessage(processName,"already have " + context +": " + image.getNameAsUploadedNormalized());
        }
        else {
            robustImageDownload(pp, image, targetDir, processName, context);
        }
        File f = new File(imagePath);
        if (f.exists()){
            return true;
        }
        else {
            image.excludeForReason(ImageInfo.EXCLUSION_REASON_UNAVAILABLE, false);
            return false;
        }
    }
    public void setCurrentImages(List<BisqueImage> bisqueImages) throws AvatolCVException {
        this.bisqueImages = bisqueImages;
        for (BisqueImage bi : bisqueImages){
            String id = bi.getResourceUniq();
            bisqueImageForID.put(id, bi);
        }
        generateImageInfoForSize(imagesThumbnail,bisqueImages,ImageInfo.IMAGE_THUMBNAIL_WIDTH, AvatolCVFileSystem.getNormalizedImagesThumbnailDir());
        for (ImageInfo ii : imagesThumbnail){
            String id = ii.getID();
            thumbnailForID.put(id, ii);
        }
        
        generateImageInfoForSize(imagesLarge,    bisqueImages,ImageInfo.IMAGE_LARGE_WIDTH,     AvatolCVFileSystem.getNormalizedImagesLargeDir());
        for (ImageInfo ii : imagesLarge){
            String id = ii.getID();
            imageLargeForID.put(id, ii);
        }
    }
    
    public static void generateImageInfoForSize(List<ImageInfo> listToFill, List<BisqueImage> bisqueImages, String width, String dir) throws AvatolCVException {
        for (BisqueImage bi : bisqueImages){
            String[] nameParts = bi.getName().split("\\.");
            String name = nameParts[0];
            // we use underscore as delimiter so can't have them in filename
            String normalizedName = name.replaceAll("_", "-");
            ImageInfo ii = new ImageInfo(dir, bi.getResourceUniq(), normalizedName, width, "", ImageInfo.STANDARD_IMAGE_FILE_EXTENSION);
            ii.setNameAsUploadedOriginalForm(name);
            listToFill.add(ii);
        }
    }
}
