package edu.oregonstate.eecs.iis.avatolcv.bisque;

import java.util.List;

import edu.oregonstate.eecs.iis.avatolcv.core.Step;
import edu.oregonstate.eecs.iis.avatolcv.core.View;
import edu.oregonstate.eecs.iis.avatolcv.ws.BisqueSessionException;
import edu.oregonstate.eecs.iis.avatolcv.ws.BisqueWSClient;
import edu.oregonstate.eecs.iis.avatolcv.ws.bisque.BisqueImage;

public class BisqueExclusionStep implements Step {
	private View view = null;
	private BisqueSessionData sessionData = null;
	List<BisqueImage> imagesToInclude = null;
	List<BisqueImage> imagesToExclude = null;
	public BisqueExclusionStep(View view, BisqueSessionData sessionData){
		this.view = view;
		this.sessionData = sessionData;
	}
	public List<BisqueImage> getBisqueImages(){
		return sessionData.getCurrentImages();
	}
	public void setImagesToInclude(List<BisqueImage> images){
		this.imagesToInclude = images;
	}
	public void setImagesToExclude(List<BisqueImage> images){
		this.imagesToExclude = images;
	}
	@Override
	public void consumeProvidedData() throws BisqueSessionException {
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
