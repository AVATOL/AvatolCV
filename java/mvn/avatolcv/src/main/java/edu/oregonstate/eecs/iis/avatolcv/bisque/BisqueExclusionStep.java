package edu.oregonstate.eecs.iis.avatolcv.bisque;

import java.util.List;

import edu.oregonstate.eecs.iis.avatolcv.core.ImageInfo;
import edu.oregonstate.eecs.iis.avatolcv.core.Step;
import edu.oregonstate.eecs.iis.avatolcv.core.View;
import edu.oregonstate.eecs.iis.avatolcv.core.AvatolCVException;

public class BisqueExclusionStep implements Step {
	private View view = null;
	private BisqueSessionData sessionData = null;
	List<ImageInfo> imagesToInclude = null;
	List<ImageInfo> imagesToExclude = null;
	public BisqueExclusionStep(View view, BisqueSessionData sessionData){
		this.view = view;
		this.sessionData = sessionData;
	}
	public List<ImageInfo> getImageInfos(){
		return sessionData.getImagesLarge();
	}
	public void setImagesToInclude(List<ImageInfo> images){
		this.imagesToInclude = images;
	}
	public void setImagesToExclude(List<ImageInfo> images){
		this.imagesToExclude = images;
	}
	@Override
    public void init() {
        // nothing to do
    }
	@Override
	public void consumeProvidedData() throws AvatolCVException {
		sessionData.setImagesToInclude(this.imagesToInclude);
		sessionData.setImagesToExclude(this.imagesToExclude);
	}

	@Override
	public boolean needsAnswering() {
		if (null == this.imagesToInclude){
			return true;
		}
		return false;
	}
	@Override
	public View getView() {
		return this.view;
	}
	
}
