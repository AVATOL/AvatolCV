package edu.oregonstate.eecs.iis.avatolcv.bisque;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import edu.oregonstate.eecs.iis.avatolcv.core.ImageInfo;
import edu.oregonstate.eecs.iis.avatolcv.core.ProgressPresenter;
import edu.oregonstate.eecs.iis.avatolcv.core.Step;
import edu.oregonstate.eecs.iis.avatolcv.core.View;
import edu.oregonstate.eecs.iis.avatolcv.ws.BisqueSessionException;
import edu.oregonstate.eecs.iis.avatolcv.ws.BisqueWSClient;
import edu.oregonstate.eecs.iis.avatolcv.ws.BisqueWSException;
import edu.oregonstate.eecs.iis.avatolcv.ws.bisque.BisqueDataset;
import edu.oregonstate.eecs.iis.avatolcv.ws.bisque.BisqueImage;

public class BisqueImagePullStep implements Step {
	private BisqueWSClient wsClient = null;
	private View view = null;
	private BisqueSessionData sessionData = null;
	private List<BisqueImage> bisqueImages = null;
	private boolean imagesLoadedSuccessfully = false;
	
	public BisqueImagePullStep(View view, BisqueWSClient wsClient, BisqueSessionData sessionData){
		this.wsClient = wsClient;
		this.view = view;
		this.sessionData = sessionData;
		
	}
	@Override
	public void consumeProvidedData() throws BisqueSessionException {
		
	}

	public boolean robustImageDownload(ProgressPresenter pp, String resourceUniq, int imageWidth, String name, String imageNameRoot ) throws BisqueSessionException {
		int maxRetries = 4;
		int tries = 0;
		boolean imageNotYetDownloaded = true;
		Exception mostRecentException = null;
		while (maxRetries > tries && imageNotYetDownloaded){
			try {
				tries++;
				pp.setMessage("downloading image  : " + name);
				this.wsClient.downloadImageOfWidth(resourceUniq, imageWidth, sessionData.getImagesDir(), "" + imageWidth, imageNameRoot);
				imageNotYetDownloaded = false;
			}
			catch(BisqueWSException e){
				if (e.getMessage().equals("timeout")){
					pp.setMessage("download timed out - retrying image : " + name + " - attempt " + (tries+1));
				}
				mostRecentException = e;
			}
		}
		if (imageNotYetDownloaded){
			throw new BisqueSessionException("problem downloading image: " + mostRecentException);
		}
		return true;
	}
	public boolean downloadImageIfNeeded(ProgressPresenter pp, ImageInfo image) throws BisqueSessionException {
		String imagePath = image.getFilepath();
		File imageFile = new File(imagePath);
		if (imageFile.exists()){
			pp.setMessage("already have image : " + image.getName());
		}
		else {
			robustImageDownload(pp, image.getID(), image.getImageWidth(), image.getName(), image.getFilenameRoot());
		}
		File f = new File(imagePath);
		if (f.exists()){
			return true;
		}
		return false;
	}
	public void downloadImagesForChosenDataset(ProgressPresenter pp) throws BisqueSessionException {
		sessionData.ensureImagesDirExists();
		BisqueDataset dataset = sessionData.getChosenDataset();
		try {
			String datasetResourceUniq = dataset.getResourceUniq();
			bisqueImages = wsClient.getImagesForDataset(datasetResourceUniq);
			sessionData.setCurrentImages(bisqueImages);
			double curCount = 0;
			int successCount = 0;
			List<ImageInfo> imagesThumbnail = sessionData.getImagesThumbnail();
			List<ImageInfo> imagesMedium    = sessionData.getImagesMedium();
			List<ImageInfo> imagesLarge     = sessionData.getImagesLarge();
			int imageCount = imagesThumbnail.size() + imagesMedium.size() + imagesLarge.size();
			for (ImageInfo image : imagesLarge){
				curCount++;
				if (downloadImageIfNeeded(pp,image)){
					successCount++;
				}
				int percentDone = (int) (100 *(curCount / imageCount));
				pp.updateProgress(percentDone);
			}
			for (ImageInfo image : imagesMedium){
				curCount++;
				if (downloadImageIfNeeded(pp,image)){
					successCount++;
				}
				int percentDone = (int) (100 *(curCount / imageCount));
				pp.updateProgress(percentDone);
			}
			for (ImageInfo image : imagesThumbnail){
				curCount++;
				if (downloadImageIfNeeded(pp,image)){
					successCount++;
				}
				int percentDone = (int) (100 *(curCount / imageCount));
				pp.updateProgress(percentDone);
			}
			if (successCount < curCount){
				int badCount = (int)curCount - successCount;
				throw new BisqueSessionException(badCount + " images didn't download correctly from bisque");
			}
		}
		catch(BisqueWSException e){
			throw new BisqueSessionException("problem getting images for dataset " + dataset.getName());
		}
	}
	@Override
	public boolean needsAnswering() {
		if (null == bisqueImages){
			return true;
		}
		if (!imagesLoadedSuccessfully){
			return true;
		}
		return false;
	}
	@Override
	public View getView() {
		return this.view;
	}

}
