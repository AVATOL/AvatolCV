package edu.oregonstate.eecs.iis.avatolcv.morphobank;

import java.io.File;
import java.util.ArrayList;
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
    private MorphobankWSClient wsClient = null;
    private String view = null;
    private MBSessionData sessionData = null;
    private List<MBMediaInfo> mbImages = null;
    private boolean imagesLoadedSuccessfully = false;
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
                //pp.setMessage(processNameFileDownload, "downloading image  : " + ii.getID());
                // specify original image filename as "" since it isn't known from web service response
                this.wsClient.downloadImageForMediaId(targetDir, ii.getID(), "", ii.getImageWidth());
                imageNotYetDownloaded = false;
            }
            catch(MorphobankWSException e){
                if (e.getMessage().equals("timeout")){
                    pp.setMessage(processNameFileDownload, "download timed out - retrying image : " + ii.getID() + " - attempt " + (tries+1));
                }
                mostRecentException = e;
            }
        }
        if (imageNotYetDownloaded){
            throw new AvatolCVException("problem downloading image: " + mostRecentException);
        }
        imagesLoadedSuccessfully = true;
        return true;
    }
    public boolean downloadImageIfNeeded(ProgressPresenter pp, ImageInfo image, String targetDir, String processNameFileDownload) throws AvatolCVException {
        String imagePath = image.getFilepath();
        File imageFile = new File(imagePath);
        if (imageFile.exists()){
            //pp.setMessage(processNameFileDownload, "already have image : " + image.getID());
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
    public void downloadImageInfoForChosenCharacterAndView(ProgressPresenter pp, String processName) throws AvatolCVException {
        //List<MBCharacter> chars = sessionData.getCharactersForCurrentMatrix();
        MBCharacter chosenCharacter = sessionData.getChosenCharacter();
        String charID = chosenCharacter.getCharID();
        MBView view = sessionData.getChosenView();
        String viewID = view.getViewID();
        List<MBTaxon> taxa = sessionData.getTaxaForCurrentMatrix();
        MBMatrix matrix = sessionData.getChosenMatrix();
        this.allMedia = new ArrayList<MBMediaInfo>();
        double cellCountTotal = (double)taxa.size();
        try {
            double cellCountCurrent = 0.0;
            for (MBTaxon taxon : taxa){
                cellCountCurrent += 1;
                List<MBMediaInfo> relevantMediaInfos = this.sessionData.loadMediaInfo(charID, taxon.getTaxonID(), viewID);
                if (relevantMediaInfos.isEmpty()){
                	// not downloaded loaded yet, so need to download
                	List<MBMediaInfo> mediaInfos = this.wsClient.getMediaForCell(matrix.getMatrixID(), charID, taxon.getTaxonID());
                    for (MBMediaInfo mi : mediaInfos){
                        if (viewID.equals(mi.getViewID())){
                            relevantMediaInfos.add(mi);
                        }
                    }
                    sessionData.persistRelevantMediaInfos(relevantMediaInfos,charID,taxon.getTaxonID(),viewID);
                }
                
                sessionData.setImagesForCell(matrix.getMatrixID(), charID, taxon.getTaxonID(), relevantMediaInfos);
                for (MBMediaInfo mi : relevantMediaInfos){
                        allMedia.add(mi);
                }
                double percentDone = cellCountCurrent / cellCountTotal;
                System.out.println("cellCountCurrent " + cellCountCurrent + " out of cellCountTotal " + cellCountTotal + " = " + percentDone);
                pp.updateProgress(processName, percentDone);
                if (percentDone == 1.0){
                    pp.setMessage(processName, "Done downloading image metadata for " + (int)cellCountCurrent + " cells.");
                }
                else {
                    pp.setMessage(processName, "cell " + (int)cellCountCurrent + " of " + (int)cellCountTotal + ": taxon " + taxon.getTaxonName() + " character " + chosenCharacter.getCharName());
                }
            }
        }
        catch(MorphobankWSException e){
            throw new AvatolCVException("problem loading image info for matrix " + matrix.getName());
        }
    }
    public void downloadImagesForChosenCharacterAndView(ProgressPresenter pp, String processName) throws AvatolCVException {
        List<MBTaxon> taxa = sessionData.getTaxaForCurrentMatrix();
        MBMatrix matrix = sessionData.getChosenMatrix();
        List<String> imageSizes = MBMediaInfo.getMediaTypes();//thumbnail, small, large
        sessionData.ensureImageDirsExists();
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
            pp.updateProgress(processName, percentDone);
            if (percentDone == 1.0){
                pp.setMessage(processName, "Done downlading " + (int)curCount + " images.");
            }
            else {
                pp.setMessage(processName, "image " + (int)curCount + " of " + (int)imageCount + " id " + image.getID());
            }
            
        }
        /*
        for (ImageInfo image : imagesSmall){
            curCount++;
            if (downloadImageIfNeeded(pp,image, sessionData.getImagesSmallDir(),processName)){
                successCount++;
            }
            double percentDone = curCount / imageCount;
            pp.updateProgress(processName, percentDone);
        }
        */
        for (ImageInfo image : imagesThumbnail){
            curCount++;
            if (downloadImageIfNeeded(pp,image, sessionData.getImagesThumbnailDir(),processName)){
                successCount++;
            }
            double percentDone = curCount / imageCount;
            pp.updateProgress(processName, percentDone);
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

    @Override
    public String getView() {
        return this.view;
    }

}
