package edu.oregonstate.eecs.iis.avatolcv;

public class ResultMatrixCell {
	enum ScoreQuality {
		Unknown,
		GoodEnough,
		NotGoodEnough
	};
	private SessionDataForTaxon sdft = null;
	private boolean isFocus = false;
	private String taxonId = null;
	private String taxonName = null;
	private ScoreQuality curQualityState = ScoreQuality.Unknown;
    public ResultMatrixCell(String taxonId, String taxonName, SessionDataForTaxon sdft){
    	this.sdft = sdft;
    	this.taxonId = taxonId;
    	this.taxonName = taxonName;
    }
    public boolean isFocusCell(){
    	return this.isFocus;
    }
    public void setFocus(boolean value){
    	this.isFocus = value;
    }
    public double getCellScore(){
    	return this.sdft.getCombinedScore();
    }
    public ScoreQuality getScoreQuality(){
    	return this.curQualityState;
    }
    public void adjustToNewThreshold(double threshold){
    	if (getCellScore() == -1){
    		this.curQualityState = ScoreQuality.Unknown;
    	}
    	else if (threshold <= getCellScore()){
    		this.curQualityState = ScoreQuality.GoodEnough;
    	}
    	else {
    		this.curQualityState = ScoreQuality.NotGoodEnough;
    	}
    }
    public String getName(){
    	return this.taxonName;
    }
    public boolean hasKnownScore(){
    	if (getCellScore() == -1){
    		return false;
    	}
    	return true;
    }
}
