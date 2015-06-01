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

public class MBImagePullStep implements Step {
    private MorphobankWSClient wsClient = null;
    private String view = null;
    private MBSessionData sessionData = null;
    private List<MBMediaInfo> mbImages = null;
    private boolean imagesLoadedSuccessfully = false;
    
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
                pp.setMessage(processNameFileDownload, "downloading image  : " + ii.getID());
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
            pp.setMessage(processNameFileDownload, "already have image : " + image.getID());
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
    public void downloadImagesForChosenMatrix(ProgressPresenter pp, String processNameInfoDownload, String processNameFileDownload) throws AvatolCVException {
        List<MBCharacter> chars = sessionData.getCharactersForCurrentMatrix();
        List<MBTaxon> taxa = sessionData.getTaxaForCurrentMatrix();
        MBMatrix matrix = sessionData.getChosenMatrix();
        List<String> imageSizes = MBMediaInfo.getMediaTypes();//thumbnail, small, large
        List<MBMediaInfo> allMedia = new ArrayList<MBMediaInfo>();
        int cellCountTotal = 0;
        for (MBTaxon taxon : taxa){
            for (MBCharacter character : chars){
                cellCountTotal += 1;
            }
        }
        try {
            int cellCountCurrent = 0;
            for (MBTaxon taxon : taxa){
                for (MBCharacter character : chars){
                    cellCountCurrent += 1;
                    List<MBMediaInfo> mediaInfos = this.wsClient.getMediaForCell(matrix.getMatrixID(), character.getCharID(), taxon.getTaxonID());
                    sessionData.setImagesForCell(matrix.getMatrixID(), character.getCharID(), taxon.getTaxonID(), mediaInfos);
                    for (MBMediaInfo mi : mediaInfos){
                        allMedia.add(mi);
                    }
                    int percentDone = (int) (100 *(cellCountCurrent / cellCountTotal));
                    pp.updateProgress(processNameInfoDownload, percentDone);
                    pp.setMessage(processNameInfoDownload, "cell: taxon " + taxon.getTaxonName() + " character " + character.getCharName());
                }
            }
        }
        catch(MorphobankWSException e){
            throw new AvatolCVException("problem loading image info for matrix " + matrix.getName());
        }

        sessionData.ensureImageDirsExists();
        //sessionData.clearImageDirs();
        sessionData.setCurrentImages(allMedia);
        double curCount = 0;
        int successCount = 0;
        List<ImageInfo> imagesThumbnail = sessionData.getImagesThumbnail();
        List<ImageInfo> imagesSmall    = sessionData.getImagesSmall();
        List<ImageInfo> imagesLarge     = sessionData.getImagesLarge();
        int imageCount = imagesThumbnail.size() * 3;
        for (ImageInfo image : imagesLarge){
            curCount++;
            if (downloadImageIfNeeded(pp,image,sessionData.getImagesLargeDir(),processNameFileDownload)){
                successCount++;
            }
            int percentDone = (int) (100 *(curCount / imageCount));
            pp.updateProgress(processNameFileDownload, percentDone);
        }
        for (ImageInfo image : imagesSmall){
            curCount++;
            if (downloadImageIfNeeded(pp,image, sessionData.getImagesSmallDir(),processNameFileDownload)){
                successCount++;
            }
            int percentDone = (int) (100 *(curCount / imageCount));
            pp.updateProgress(processNameFileDownload, percentDone);
        }
        
        for (ImageInfo image : imagesThumbnail){
            curCount++;
            if (downloadImageIfNeeded(pp,image, sessionData.getImagesThumbnailDir(),processNameFileDownload)){
                successCount++;
            }
            int percentDone = (int) (100 *(curCount / imageCount));
            pp.updateProgress(processNameFileDownload, percentDone);
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
