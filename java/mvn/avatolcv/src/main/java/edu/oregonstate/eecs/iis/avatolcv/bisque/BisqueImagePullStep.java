package edu.oregonstate.eecs.iis.avatolcv.bisque;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

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
	private List<BisqueImage> images = null;
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
	public boolean downloadImagesForChosenDataset(ProgressPresenter pp) throws BisqueSessionException {
		sessionData.ensureImagesDirExists();
		BisqueDataset dataset = sessionData.getChosenDataset();
		try {
			String datasetResourceUniq = dataset.getResourceUniq();
			images = wsClient.getImagesForDataset(datasetResourceUniq);
			double imageCount = images.size() * sessionData.getImageSizeCount();
			List<Integer> imageWidths = sessionData.getImageWidths();
			double curCount = 0;
			List<String> imagePaths = new ArrayList<String>();
			for (BisqueImage bi : images){
				for (Integer integer : imageWidths){
					curCount++;
					String name = bi.getName();
					String[] parts = name.split("\\.");
					String imageNameRoot = parts[0];
					String resourceUniq = bi.getResourceUniq();
					int imageWidth = integer.intValue();
					String filename = sessionData.getImageFilename(resourceUniq, name, imageWidth);
					
					String imagePath = sessionData.getImagePath(filename);
					imagePaths.add(imagePath);
					File imageFile = new File(imagePath);
					if (imageFile.exists()){
						pp.setMessage("already have image : " + name);
					}
					else {
						robustImageDownload(pp, resourceUniq, imageWidth, name, imageNameRoot);
					}
					int percentDone = (int) (100 *(curCount / imageCount));
					pp.updateProgress(percentDone);
				}
			}
			this.imagesLoadedSuccessfully = true;
			for (String imagePath : imagePaths){
				File f = new File(imagePath);
				if (!(f.exists())){
					this.imagesLoadedSuccessfully = false;
				}
			}
			return true;
		}
		catch(BisqueWSException e){
			throw new BisqueSessionException("problem getting images for dataset " + dataset.getName());
		}
	}
	@Override
	public boolean needsAnswering() {
		if (null == images){
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
