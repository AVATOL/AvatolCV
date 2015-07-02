package edu.oregonstate.eecs.iis.avatolcv.morphobank;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import edu.oregonstate.eecs.iis.avatolcv.bisque.BisqueSessionData;
import edu.oregonstate.eecs.iis.avatolcv.core.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.core.ImageInfo;
import edu.oregonstate.eecs.iis.avatolcv.core.ProgressPresenter;
import edu.oregonstate.eecs.iis.avatolcv.core.Step;
import edu.oregonstate.eecs.iis.avatolcv.core.View;
import edu.oregonstate.eecs.iis.avatolcv.ws.BisqueWSClient;
import edu.oregonstate.eecs.iis.avatolcv.ws.BisqueWSException;
import edu.oregonstate.eecs.iis.avatolcv.ws.MorphobankWSClient;
import edu.oregonstate.eecs.iis.avatolcv.ws.MorphobankWSException;
import edu.oregonstate.eecs.iis.avatolcv.ws.bisque.BisqueDataset;
import edu.oregonstate.eecs.iis.avatolcv.ws.bisque.BisqueImage;
import edu.oregonstate.eecs.iis.avatolcv.ws.morphobank.CellMediaInfo.MBMediaInfo;
import edu.oregonstate.eecs.iis.avatolcv.ws.morphobank.CharacterInfo.MBCharacter;
import edu.oregonstate.eecs.iis.avatolcv.ws.morphobank.MatrixInfo.MBMatrix;
import edu.oregonstate.eecs.iis.avatolcv.ws.morphobank.TaxaInfo.MBTaxon;
import edu.oregonstate.eecs.iis.avatolcv.ws.morphobank.ViewInfo.MBView;

public class MBImagePullStep implements Step {
	private static final String NL = System.getProperty("line.separator");
    private MorphobankWSClient wsClient = null;
    private String view = null;
    private MBSessionData sessionData = null;
    private List<MBMediaInfo> mbImages = null;
    private List<MBMediaInfo> allMedia = null;
    public MBImagePullStep(String view, MorphobankWSClient wsClient, MBSessionData sessionData){
        this.wsClient = wsClient;
        this.view = view;
        this.sessionData = sessionData;
        
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
                this.wsClient.downloadImageForMediaId(targetDir, ii.getID(), "", ii.getImageWidth());
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
    public void downloadImageInfoForChosenCharactersAndView(ProgressPresenter pp, String processName) throws AvatolCVException {
        //List<MBCharacter> chars = sessionData.getCharactersForCurrentMatrix();
        List<MBCharacter> chosenCharacters = sessionData.getChosenCharacters();
        
        MBView view = sessionData.getChosenView();
        String viewID = view.getViewID();
        List<MBTaxon> taxa = sessionData.getTaxaForCurrentMatrix();
        MBMatrix matrix = sessionData.getChosenMatrix();
        this.allMedia = new ArrayList<MBMediaInfo>();
        double cellCountTotal = (double)taxa.size() * chosenCharacters.size();
        String path = this.sessionData.getSessionLogPath("imageLoad");
        try {
        	BufferedWriter writer = new BufferedWriter(new FileWriter(path));
            double cellCountCurrent = 0.0;
            for (MBCharacter ch : chosenCharacters){
            	String charID = ch.getCharID();
            	for (MBTaxon taxon : taxa){
                    cellCountCurrent += 1;
                    List<MBMediaInfo> relevantMediaInfos = this.sessionData.loadMediaInfo(charID, taxon.getTaxonID(), viewID);
                    if (relevantMediaInfos.isEmpty()){
                    	writer.write("loading from SITE : char " + charID + " taxon " + taxon.getTaxonID() + NL);
                    	// not downloaded loaded yet, so need to download
                    	List<MBMediaInfo> mediaInfos = this.wsClient.getMediaForCell(matrix.getMatrixID(), charID, taxon.getTaxonID());
                        for (MBMediaInfo mi : mediaInfos){
                            if (viewID.equals(mi.getViewID())){
                                relevantMediaInfos.add(mi);
                            }
                        }
                        sessionData.persistRelevantMBMediaInfos(relevantMediaInfos,charID,taxon.getTaxonID(),viewID);
                    }
                    else {
                    	writer.write("loading from file : char " + charID + " taxon " + taxon.getTaxonID() + NL);
                    }
                    
                    sessionData.setImagesForCell(matrix.getMatrixID(), charID, taxon.getTaxonID(), relevantMediaInfos);
                    for (MBMediaInfo mi : relevantMediaInfos){
                    	writer.write("added : mediaId " + mi.getMediaID() + NL);
                        allMedia.add(mi);
                    }
                    double percentDone = cellCountCurrent / cellCountTotal;
                    System.out.println("cellCountCurrent " + cellCountCurrent + " out of cellCountTotal " + cellCountTotal + " = " + percentDone);
                    progressUpdate(pp, processName, percentDone);
                    if (percentDone == 1.0){
                        progressMessage(pp, processName, "Image metadata download complete for " + (int)cellCountCurrent + " cells.");
                    }
                    else {
                        progressMessage(pp, processName, "cell " + (int)cellCountCurrent + " of " + (int)cellCountTotal + " (character " + ch.getCharName() + " taxon " + taxon.getTaxonName() + ")");
                    }
                }
            }
            writer.close();
            
        }
        catch(IOException ioe){
        	throw new AvatolCVException("problem logging image load for matrix " + matrix.getName());
        }
        catch(MorphobankWSException e){
            throw new AvatolCVException("problem loading image info for matrix " + matrix.getName());
        }
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
    public void downloadImagesForChosenCharactersAndView(ProgressPresenter pp, String processName) throws AvatolCVException {
        //List<MBTaxon> taxa = sessionData.getTaxaForCurrentMatrix();
       // MBMatrix matrix = sessionData.getChosenMatrix();
        //List<String> imageSizes = MBMediaInfo.getMediaTypes();//thumbnail, small, large
        //sessionData.clearImageDirs();
        sessionData.setCurrentImages(this.allMedia);
        double curCount = 0;
        int successCount = 0;
        List<ImageInfo> imagesThumbnail = sessionData.getImagesThumbnail();
        //List<ImageInfo> imagesSmall    = sessionData.getImagesSmall();
        List<ImageInfo> imagesLarge     = sessionData.getImagesLarge();
        int imageCount = imagesLarge.size() * 2;
        //int imageCount = imagesLarge.size() * 3;
        for (ImageInfo image : imagesLarge){
            curCount++;
            if (downloadImageIfNeeded(pp,image,sessionData.getImagesLargeDir(),processName)){
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
        /*
        for (ImageInfo image : imagesSmall){
            curCount++;
            if (downloadImageIfNeeded(pp,image, sessionData.getImagesSmallDir(),processName)){
                successCount++;
            }
            double percentDone = curCount / imageCount;
            progressUpdate(pp, processName, percentDone);
        }
        */
        for (ImageInfo image : imagesThumbnail){
            curCount++;
            if (downloadImageIfNeeded(pp,image, sessionData.getImagesThumbnailDir(),processName)){
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
    @Override
    public void init() throws AvatolCVException {
        // TODO Auto-generated method stub

    }

    @Override
    public void consumeProvidedData() throws AvatolCVException {
        // nothing to do - images already download

    }

}
