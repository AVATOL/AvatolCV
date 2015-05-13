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
    private View view = null;
    private MBSessionData sessionData = null;
    private List<MBMediaInfo> mbImages = null;
    private boolean imagesLoadedSuccessfully = false;
    
    public MBImagePullStep(View view, MorphobankWSClient wsClient, MBSessionData sessionData){
        this.wsClient = wsClient;
        this.view = view;
        this.sessionData = sessionData;
        
    }
   
    public boolean robustImageDownload(ProgressPresenter pp, ImageInfo ii, String targetDir ) throws AvatolCVException {
        int maxRetries = 4;
        int tries = 0;
        boolean imageNotYetDownloaded = true;
        Exception mostRecentException = null;
        while (maxRetries > tries && imageNotYetDownloaded){
            try {
                tries++;
                pp.setMessage("downloading image  : " + ii.getID());
                // specify original image filename as "" since it isn't known from web service response
                this.wsClient.downloadImageForMediaId(targetDir, ii.getID(), "", ii.getImageWidth());
                imageNotYetDownloaded = false;
            }
            catch(MorphobankWSException e){
                if (e.getMessage().equals("timeout")){
                    pp.setMessage("download timed out - retrying image : " + ii.getID() + " - attempt " + (tries+1));
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
    public boolean downloadImageIfNeeded(ProgressPresenter pp, ImageInfo image, String targetDir) throws AvatolCVException {
        String imagePath = image.getFilepath();
        File imageFile = new File(imagePath);
        if (imageFile.exists()){
            pp.setMessage("already have image : " + image.getNameAsUploadedNormalized());
        }
        else {
            robustImageDownload(pp, image, targetDir);
        }
        File f = new File(imagePath);
        if (f.exists()){
            return true;
        }
        return false;
    }
    public void downloadImagesForChosenMatrix(ProgressPresenter pp) throws AvatolCVException {
        List<MBCharacter> chars = sessionData.getCharactersForCurrentMatrix();
        List<MBTaxon> taxa = sessionData.getTaxaForCurrentMatrix();
        MBMatrix matrix = sessionData.getChosenMatrix();
        List<String> imageSizes = MBMediaInfo.getMediaTypes();//thumbnail, small, large
        List<MBMediaInfo> allMedia = new ArrayList<MBMediaInfo>();
        try {
            for (MBTaxon taxon : taxa){
                for (MBCharacter character : chars){
                    List<MBMediaInfo> mediaInfos = this.wsClient.getMediaForCell(matrix.getMatrixID(), character.getCharID(), taxon.getTaxonID());
                    for (MBMediaInfo mi : mediaInfos){
                        allMedia.add(mi);
                    }
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
            if (downloadImageIfNeeded(pp,image,sessionData.getImagesLargeDir())){
                successCount++;
            }
            int percentDone = (int) (100 *(curCount / imageCount));
            pp.updateProgress(percentDone);
        }
        for (ImageInfo image : imagesSmall){
            curCount++;
            if (downloadImageIfNeeded(pp,image, sessionData.getImagesSmallDir())){
                successCount++;
            }
            int percentDone = (int) (100 *(curCount / imageCount));
            pp.updateProgress(percentDone);
        }
        
        for (ImageInfo image : imagesThumbnail){
            curCount++;
            if (downloadImageIfNeeded(pp,image, sessionData.getImagesThumbnailDir())){
                successCount++;
            }
            int percentDone = (int) (100 *(curCount / imageCount));
            pp.updateProgress(percentDone);
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
    public View getView() {
        return this.view;
    }

}
