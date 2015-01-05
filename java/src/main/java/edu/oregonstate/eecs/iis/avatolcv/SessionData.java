package edu.oregonstate.eecs.iis.avatolcv;

import java.util.List;

import edu.oregonstate.eecs.iis.avatolcv.algata.ImageSet;
import edu.oregonstate.eecs.iis.avatolcv.algata.ResultImage;
import edu.oregonstate.eecs.iis.avatolcv.algata.ImageSetSupplier;

public class SessionData  implements ImageSetSupplier {
	private FOCUS focus = FOCUS.images_scored;
	enum FOCUS {
		training,
		images_scored,
		images_not_scored
	}
	//Need a vector of in-order taxa names, and a map for finding the result image
	protected List<ResultImage> trainingImages = null;
	protected List<ResultImage> scoredImages = null;
	protected List<ResultImage> unscoredImages = null;
	private int trainingIndex = 0;
	private int scoredIndex = 0;
	private int unscoredIndex = 0;
	private String charId = null;
	private String charName = null;
	public SessionData(String charId, String charName,
			List<ResultImage> trainingImages,
			List<ResultImage> scoredImages,
			List<ResultImage> unscoredImages){

		this.trainingImages = trainingImages;
		this.scoredImages = scoredImages;
		this.unscoredImages = unscoredImages;
		this.charId = charId;
		this.charName = charName;
	}
	public boolean canShowImage(){
		int size = getCurrentListSize();
		if (size > 0){
			return true;
		}
		return false;
	}
	public void setFocusAsTraining(){
		this.focus = FOCUS.training;
	}
	public void setFocusAsScored(){
		this.focus = FOCUS.images_scored;
	}
	public void setFocusAsUnscored(){
		this.focus = FOCUS.images_not_scored;
	}
	public boolean isFocusTrainingData(){
		if (this.focus == FOCUS.training){
			return true;
		}
		return false;
	}
	public boolean canGoToNextImage(){
		int listSize = getCurrentListSize();
		int listIndex = getCurrentListIndex();
		if (listIndex < listSize - 1){
			return true;
		}
		return false;
	}
	public void goToNextImage(){
		if (canGoToNextImage()){
			if (this.focus == FOCUS.images_not_scored){
				this.unscoredIndex += 1;
			}
			else if (this.focus == FOCUS.images_scored){
				this.scoredIndex += 1;
			}
			else {
				this.trainingIndex += 1;
			}
		}
	}
    public boolean canGoToPrevImage(){
    	int listIndex = getCurrentListIndex();
		if (listIndex > 0){
			return true;
		}
		return false;
    }
	public void goToPrevImage(){
		if (this.focus == FOCUS.images_not_scored){
			this.unscoredIndex -= 1;
		}
		else if (this.focus == FOCUS.images_scored){
			this.scoredIndex -= 1;
		}
		else {
			this.trainingIndex -= 1;
		}
	}
	public boolean isFocusScoredImages(){
		if (this.focus == FOCUS.images_scored){
			return true;
		}
		return false;
	}
	public boolean isFocusUnscoredImages(){
		if (this.focus == FOCUS.images_not_scored){
			return true;
		}
		return false;
	}
	public ResultImage getCurrentResultImage(){
		if (this.focus == FOCUS.images_not_scored){
			return this.unscoredImages.get(this.unscoredIndex);
		}
		else if (this.focus == FOCUS.images_scored){
			return this.scoredImages.get(this.scoredIndex);
		}
		else {
			return this.trainingImages.get(this.trainingIndex);
		}
	}
	public int getCurrentListSize(){
		if (this.focus == FOCUS.images_not_scored){
			return this.unscoredImages.size();
		}
		else if (this.focus == FOCUS.images_scored){
			return this.scoredImages.size();
		}
		else {
			return this.trainingImages.size();
		}
	}

	public int getCurrentListIndex(){
		if (this.focus == FOCUS.images_not_scored){
			return this.unscoredIndex;
		}
		else if (this.focus == FOCUS.images_scored){
			return this.scoredIndex;
		}
		else {
			return this.trainingIndex;
		}
	}
	public String getPositionInListString(){
		int listSize = getCurrentListSize();
		int listIndex = getCurrentListIndex();
		String positionInList = "";
		if (listSize> 0){
			positionInList = (listIndex + 1) + "/" + listSize;
		}
	    return positionInList;
	}
	public String getImageContextString(){
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
	public int getResultImageIndex(List<ResultImage> images, String taxonId, String charId){
		for (int i = 0; i < images.size(); i++){
			ResultImage ri = images.get(i);
			if (ri.getTaxonId().equals(taxonId) && ri.getCharacterId().equals(charId)){
				return i;
			}
		}
		return -1;
	}
	public ResultImage shiftToResultImage(String charId, String taxonId) throws AvatolCVException {
		int index = getResultImageIndex(this.scoredImages, taxonId, charId);
		if (!(index == -1)){
			// its a scoredImage
			setFocusAsScored();
			this.scoredIndex = index;
			return this.scoredImages.get(index);
		}
		index = getResultImageIndex(this.unscoredImages, taxonId, charId);
		if (!(index == -1)){
			// its an unscoredImage
			setFocusAsUnscored();
			this.unscoredIndex = index;
			return this.unscoredImages.get(index);
		}
		index = getResultImageIndex(this.trainingImages, taxonId, charId);
		if (!(index == -1)){
			// its training
			setFocusAsTraining();
			this.trainingIndex = index;
			return this.trainingImages.get(index);
		}
		throw new AvatolCVException("no resultImage for charId " + charId + " taxonId " + taxonId + " in current session data");
	}
	
	public ImageSet getTrainingImageSet() {
		ImageSet is = new ImageSet(this.trainingImages);
		return is;
	}
	
	public ImageSet getScoredImageSet() {
		ImageSet is = new ImageSet(this.scoredImages);
		return is;
	}
	
	public ImageSet getUnscoredImageSet() {
		ImageSet is = new ImageSet(this.unscoredImages);
		return is;
	}
	@Override
	public String getTrainingTabTitle() {
		// TODO Auto-generated method stub
		return "??? training";
	}
	@Override
	public String getScoredTabTitle() {
		// TODO Auto-generated method stub
		return "??? scored";
	}
	@Override
	public String getUnscoredTabTitle() {
		// TODO Auto-generated method stub
		return "??? unscored";
	}
}


