package edu.oregonstate.eecs.iis.avatolcv.algata;

import java.util.ArrayList;
import java.util.List;

import edu.oregonstate.eecs.iis.avatolcv.AvatolCVException;

public class ImageSet {
	private int index = 0;
	private List<ResultImage> images = null;
    public ImageSet(List<ResultImage> images){
    	this.images = images;
    	if (null == images){
    		this.index = -1;
    	}
    	if (images.size() == 0){
    		this.index = -1;
    	}
    }
    public List<String> getAllThumbnailPaths(){
    	List<String> result = new ArrayList<String>();
    	for (ResultImage image : images){
    		String thumbnailPath = image.getThumbnailMediaPath();
    		result.add(thumbnailPath);
    	}
    	return result;
    }
    public boolean hasData(){
    	if (null != this.images && this.images.size() > 0){
    		return true;
    	}
    	return false;
    }
	public boolean canShowImage(){
		int size = getCurrentListSize();
		if (size > 0){
			return true;
		}
		return false;
	}
	public boolean canGoToNextImage(){
		if (this.index == -1){
			return false;
		}
		int listSize = getCurrentListSize();
		int listIndex = getCurrentListIndex();
		if (listIndex < listSize - 1){
			return true;
		}
		return false;
	}
	public void goToNextImage(){
		if (canGoToNextImage()){
			this.index += 1;
		}
	}
    public boolean canGoToPrevImage(){
    	if (this.index == -1){
			return false;
		}
    	int listIndex = getCurrentListIndex();
		if (listIndex > 0){
			return true;
		}
		return false;
    }
	public void goToPrevImage(){
		if (canGoToPrevImage()){
			this.index -= 1;
		}
	}
	public ResultImage getCurrentResultImage(){
		if (this.index == -1){
			return null;
		}
		return this.images.get(this.index);
	}
	public int getCurrentListSize(){
		return this.images.size();
	}

	public int getCurrentListIndex(){
		return this.index;
	}
	public String getPositionInListString(){
		if (this.index == -1){
			return "no images";
		}
		int listSize = getCurrentListSize();
		int listIndex = getCurrentListIndex();
		String positionInList = "";
		if (listSize> 0){
			positionInList = (listIndex + 1) + "/" + listSize;
		}
	    return positionInList;
	}
	public String getImageContextString(){
		if (this.index == -1){
			return "no image";
		}
		String context = "";
		if (getCurrentListSize() > 0){
			ResultImage ri = getCurrentResultImage();
			String characterStateName = "";
			if (ri.hasCharacterState()){
				characterStateName = " , " + ri.getCharacterStateName();
			}
			context = ri.getCharacterName() + characterStateName;
		}
		return context;
	}
	public int getResultImageIndexForTaxon(List<ResultImage> images, String taxonId){
		for (int i = 0; i < images.size(); i++){
			ResultImage ri = images.get(i);
			if (ri.getTaxonId().equals(taxonId)){
				return i;
			}
		}
		return -1;
	}
	public ResultImage shiftToTaxonImage(String taxonId) throws AvatolCVException {
		if (this.index == -1){
			return null;
		}
		int index = getResultImageIndexForTaxon(this.images, taxonId);
		if (!(index == -1)){
			this.index = index;
			return this.images.get(index);
		}
		throw new AvatolCVException("no resultImage for taxonId " + taxonId + " in current ImageSet");
	}
}
