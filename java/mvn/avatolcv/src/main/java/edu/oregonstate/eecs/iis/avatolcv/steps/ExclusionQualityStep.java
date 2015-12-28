package edu.oregonstate.eecs.iis.avatolcv.steps;

import java.util.List;

import edu.oregonstate.eecs.iis.avatolcv.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.AvatolCVFileSystem;
import edu.oregonstate.eecs.iis.avatolcv.core.ImageInfo;
import edu.oregonstate.eecs.iis.avatolcv.session.ImagesForStep;
import edu.oregonstate.eecs.iis.avatolcv.session.SessionInfo;

public class ExclusionQualityStep extends Answerable implements Step {
    private SessionInfo sessionInfo = null;
    private ImagesForStep imagesForStep = null;
    public ExclusionQualityStep(SessionInfo sessionInfo) throws AvatolCVException {
        this.sessionInfo = sessionInfo;
    }
    public void loadImages() throws AvatolCVException {
    	if (this.imagesForStep == null){
    		String largeImagesDir = AvatolCVFileSystem.getNormalizedImagesLargeDir();
            String thumbImagesDir = AvatolCVFileSystem.getNormalizedImagesThumbnailDir();
            this.imagesForStep = new ImagesForStep(largeImagesDir, thumbImagesDir);
    	}
    }
    public List<ImageInfo> getImagesLarge(){
        return this.imagesForStep.getImagesLarge();
    }
    public List<ImageInfo> getImagesThumbnail(){
        return this.imagesForStep.getImagesThumbnail();
    }
    //public void acceptExclusions(){
    //    this.imagesForStep.acceptExclusions();
    //}
    public ImageInfo getLargeImageForImage(ImageInfo ii) throws AvatolCVException {
    	return this.imagesForStep.getLargeImageForImage(ii);
    }
    @Override
    public void init() throws AvatolCVException {
        
    }

    @Override
    public void consumeProvidedData() throws AvatolCVException {
    	// consumed as user clicks to exclude or unexclude
    }
    public boolean isRotatedHorizontally(ImageInfo ii) throws AvatolCVException{
    	return this.imagesForStep.isRotatedHorizontally(ii);
    }
    public boolean isRotatedVertically(ImageInfo ii) throws AvatolCVException{
    	return this.imagesForStep.isRotatedVertically(ii);
    }
    public void rotateVertically(ImageInfo ii) throws AvatolCVException {
        this.imagesForStep.rotateVertically(ii);
    }

    public void rotateHorizontally(ImageInfo ii) throws AvatolCVException {
        this.imagesForStep.rotateHorizontally(ii);
    }
	@Override
	public boolean hasFollowUpDataLoadPhase() {
		// TODO Auto-generated method stub
		return false;
	}
    @Override
    public boolean isEnabledByPriorAnswers() {
        return true;
    }
	@Override
	public boolean shouldRenderIfBackingIntoIt() {
		return true;
	}
}

