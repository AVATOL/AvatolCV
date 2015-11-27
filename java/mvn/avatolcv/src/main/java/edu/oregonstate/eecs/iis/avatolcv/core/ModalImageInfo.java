package edu.oregonstate.eecs.iis.avatolcv.core;

public class ModalImageInfo {
	public enum Mode {
		TRAIN_WITH,
		SCORE
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
	public boolean isTraining(){
		return this.mode == Mode.TRAIN_WITH;
	}
	public boolean isScoring(){
		return this.mode == Mode.SCORE;
	}
	public NormalizedImageInfo getNormalizedImageInfo() {
		return this.nii;
	}
}
