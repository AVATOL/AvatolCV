package edu.oregonstate.eecs.iis.avatolcv.segmentation;

import java.awt.image.BufferedImage;
import java.util.List;

import edu.oregonstate.eecs.iis.avatolcv.core.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.core.ImageInfo;
import edu.oregonstate.eecs.iis.avatolcv.core.Step;
import edu.oregonstate.eecs.iis.avatolcv.core.View;

public class SegStep4_Review implements Step {
	private View view = null;	
	private SegmentationSessionData ssd = null;
	private boolean userReviewComplete = false;
	public SegStep4_Review(View view, SegmentationSessionData ssd){
		this.ssd = ssd;
		this.view = view;
	}
	public List<ImageInfo> getOriginalImages(){
	    return this.ssd.getCandidateImages();
	}
	public String getImageStatus(String ID) throws AvatolCVException {
	    return this.ssd.getImagesForStage().getStatusForImage(ID);
	}
	public ImageInfo getTrainingImage(String ID) throws AvatolCVException {
	    return this.ssd.getImagesForStage().getTrainingImage(ID);
	}
	public ImageInfo getResultImage(String ID) throws AvatolCVException{
	    return this.ssd.getImagesForStage().getResultImage(ID);
	}
	/*
	 * LEFT OFF HERE 4/22/15
	 * Use cases from this screen
	 * - user is ok with results and wants to move on
	 *      For this case, just move on, data is in place
	 *      
	 * - user unhappy with results, wants to "not save" results, go back to labeling step to label some more and re-run
	 *      For this case, just need to hop back to labeling step, when re-run will clean out old results
	 * 
	 * - user mostly happy with results, but wants to manually fix a few, save, and move on
	 *      For this case, the UI needs to be able to launch a window to do the edit, like it supported at the labeling step.
	 */
	public void deleteTrainingImage(ImageInfo ii)  throws AvatolCVException{
		this.ssd.deleteTrainingImage(ii);
	}
	public void saveSegmentationTrainingImage(BufferedImage bi, ImageInfo ii) throws AvatolCVException {
		this.ssd.saveSegmentationTrainingImage(bi,ii);
	}
	public void disqualifyImage(ImageInfo ii) throws AvatolCVException {
		this.ssd.disqualifyImage(ii);
	}
	public void requalifyImage(ImageInfo ii) throws AvatolCVException {
		this.ssd.requalifyImage(ii);
	}
	@Override
	public void consumeProvidedData() throws AvatolCVException {
	    
		userReviewComplete = true;
	}

	@Override
	public boolean needsAnswering() {
		return !userReviewComplete;
	}

	@Override
	public View getView() {
		return this.view;
	}

}
