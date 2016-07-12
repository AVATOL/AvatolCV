package edu.oregonstate.eecs.iis.avatolcv.scoring;

import edu.oregonstate.eecs.iis.avatolcv.normalized.NormalizedImageInfo;

public class ModalImageInfo {
	public enum Mode {
		TRAIN_WITH,
		SCORE, 
		IGNORE
	}
	private NormalizedImageInfo nii = null;
	private Mode mode = null;
	public ModalImageInfo(NormalizedImageInfo nii){
		this.nii = nii;
	}
	public void setAsTraining(){
		this.mode = Mode.TRAIN_WITH;
	}
	public void setAsToScore(){
		this.mode = Mode.SCORE;
	}
	public void setAsToIgnore(){
		this.mode = Mode.IGNORE;
	}
	public boolean isTraining(){
		return this.mode == Mode.TRAIN_WITH;
	}
	public boolean isScoring(){
		return this.mode == Mode.SCORE;
	}
	public boolean isIgnore(){
		return this.mode == Mode.IGNORE;
	}
	public NormalizedImageInfo getNormalizedImageInfo() {
		return this.nii;
	}
}
