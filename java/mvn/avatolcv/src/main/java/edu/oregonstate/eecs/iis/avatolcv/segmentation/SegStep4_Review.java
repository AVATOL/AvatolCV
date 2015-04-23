package edu.oregonstate.eecs.iis.avatolcv.segmentation;

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
	public String getImageStatus(String ID) throws SegmentationException {
	    return this.ssd.getSegmentationImages().getStatusForImage(ID);
	}
	public ImageInfo getTrainingImage(String ID) throws SegmentationException {
	    return this.ssd.getSegmentationImages().getTrainingImage(ID);
	}
	public ImageInfo getResultImage(String ID) throws SegmentationException{
	    return this.ssd.getSegmentationImages().getResultImage(ID);
	}
	/*
	 * LEFT OFF HERE 4/22/15
	 * Use cases from this screen
	 * - user is ok with results and wants to move on
	 * - user wants to manually fix a few and move on
	 * - user wants to manually fix a few and then re-run segmentation to see if it does better
	 */
	public void validateResultFile(ImageInfo ii){
	    
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
