package edu.oregonstate.eecs.iis.avatolcv.steps;

import java.awt.image.BufferedImage;
import java.util.List;

import edu.oregonstate.eecs.iis.avatolcv.core.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.core.ImageInfo;
import edu.oregonstate.eecs.iis.avatolcv.core.ImageTranformReviewData;
import edu.oregonstate.eecs.iis.avatolcv.core.Step;
import edu.oregonstate.eecs.iis.avatolcv.core.View;

public class ImageTransformReviewStep implements Step {
	private String view = null;	
	private ImageTranformReviewData itrd = null;
	private boolean userReviewComplete = false;
	public ImageTransformReviewStep(String view, ImageTranformReviewData itrd){
		this.itrd = itrd;
		this.view = view;
	}
	public List<ImageInfo> getOriginalImages(){
	    return this.itrd.getCandidateImages();
	}
	public String getImageStatus(String ID) throws AvatolCVException {
	    return this.itrd.getImagesForStage().getStatusForImage(ID);
	}
	public ImageInfo getTrainingImage(String ID) throws AvatolCVException {
	    return this.itrd.getImagesForStage().getTrainingImage(ID);
	}
	public ImageInfo getResultImage(String ID) throws AvatolCVException{
	    return this.itrd.getImagesForStage().getResultImage(ID);
	}
	/*
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
		this.itrd.deleteTrainingImage(ii);
	}
	public void saveTrainingImage(BufferedImage bi, ImageInfo ii) throws AvatolCVException {
		this.itrd.saveTrainingImage(bi,ii);
	}
	public void disqualifyImage(ImageInfo ii) throws AvatolCVException {
		this.itrd.disqualifyImage(ii);
	}
	public void requalifyImage(ImageInfo ii) throws AvatolCVException {
		this.itrd.requalifyImage(ii);
	}
	@Override
    public void init() {
        // nothing to do
    }
	@Override
	public void consumeProvidedData() throws AvatolCVException {
	    
		userReviewComplete = true;
	}
    @Override
    public boolean hasDataLoadPhase() {
        // TODO Auto-generated method stub
        return false;
    }
}
