package edu.oregonstate.eecs.iis.avatolcv.obsolete.bisque;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import edu.oregonstate.eecs.iis.avatolcv.core.ImageInfo;
import edu.oregonstate.eecs.iis.avatolcv.core.ProgressPresenter;
import edu.oregonstate.eecs.iis.avatolcv.core.Step;
import edu.oregonstate.eecs.iis.avatolcv.core.View;
import edu.oregonstate.eecs.iis.avatolcv.core.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.ws.BisqueWSClient;
import edu.oregonstate.eecs.iis.avatolcv.ws.BisqueWSException;
import edu.oregonstate.eecs.iis.avatolcv.ws.bisque.BisqueDataset;
import edu.oregonstate.eecs.iis.avatolcv.ws.bisque.BisqueImage;

public class BisqueImagePullStep implements Step {
	private BisqueWSClient wsClient = null;
	private String view = null;
	private BisqueSessionData sessionData = null;
	private List<BisqueImage> bisqueImages = null;
	private boolean imagesLoadedSuccessfully = false;
	
	public BisqueImagePullStep(String view, BisqueWSClient wsClient, BisqueSessionData sessionData){
		this.wsClient = wsClient;
		this.view = view;
		this.sessionData = sessionData;
		
	}
	@Override
    public void init() {
        // nothing to do
    }
	@Override
	public void consumeProvidedData() throws AvatolCVException {
		
	}

	public boolean robustImageDownload(ProgressPresenter pp, ImageInfo ii, String targetDir ) throws AvatolCVException {
		int maxRetries = 4;
		int tries = 0;
		boolean imageNotYetDownloaded = true;
		Exception mostRecentException = null;
		while (maxRetries > tries && imageNotYetDownloaded){
			try {
				tries++;
				pp.setMessage("...","downloading image  : " + ii.getNameAsUploadedNormalized());
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
					pp.setMessage("...","download timed out - retrying image : " + ii.getNameAsUploadedNormalized() + " - attempt " + (tries+1));
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
			pp.setMessage("...","already have image : " + image.getNameAsUploadedNormalized());
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
	public void downloadImagesForChosenDataset(ProgressPresenter pp) throws AvatolCVException {
		sessionData.ensureImageDirsExists();
		//sessionData.clearImageDirs();
		BisqueDataset dataset = sessionData.getChosenDataset();
		try {
			String datasetResourceUniq = dataset.getResourceUniq();
			bisqueImages = wsClient.getImagesForDataset(datasetResourceUniq);
			sessionData.setCurrentImages(bisqueImages);
			double curCount = 0;
			int successCount = 0;
			List<ImageInfo> imagesThumbnail = sessionData.getImagesThumbnail();
			List<ImageInfo> imagesSmall    = sessionData.getImagesSmall();
			List<ImageInfo> imagesMedium    = sessionData.getImagesMedium();
			List<ImageInfo> imagesLarge     = sessionData.getImagesLarge();
			int imageCount = imagesThumbnail.size() * 4;
			for (ImageInfo image : imagesLarge){
				curCount++;
				if (downloadImageIfNeeded(pp,image,sessionData.getImagesLargeDir())){
					successCount++;
				}
				int percentDone = (int) (100 *(curCount / imageCount));
				pp.updateProgress("...",percentDone);
			}
			for (ImageInfo image : imagesMedium){
				curCount++;
				if (downloadImageIfNeeded(pp,image, sessionData.getImagesMediumDir())){
					successCount++;
				}
				int percentDone = (int) (100 *(curCount / imageCount));
				pp.updateProgress("...",percentDone);
			}
			for (ImageInfo image : imagesSmall){
				curCount++;
				if (downloadImageIfNeeded(pp,image, sessionData.getImagesSmallDir())){
					successCount++;
				}
				int percentDone = (int) (100 *(curCount / imageCount));
				pp.updateProgress("...",percentDone);
			}
			for (ImageInfo image : imagesThumbnail){
				curCount++;
				if (downloadImageIfNeeded(pp,image, sessionData.getImagesThumbnailDir())){
					successCount++;
				}
				int percentDone = (int) (100 *(curCount / imageCount));
				pp.updateProgress("...",percentDone);
			}
			if (successCount < curCount){
				int badCount = (int)curCount - successCount;
				throw new AvatolCVException(badCount + " images didn't download correctly from bisque");
			}
			sessionData.createSegmentationSessionData(sessionData.getImagesLargeDir());
		}
		catch(BisqueWSException e){
			throw new AvatolCVException("problem getting images for dataset " + dataset.getName());
		}
	}
}
