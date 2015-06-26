package edu.oregonstate.eecs.iis.avatolcv.core;

public class TrainTestInfo {
	private String scoringFocusID = null;
	private String trainTestdescriminatorID = null;
	private int testCount = -1;
	public int getTestCount() {
		return testCount;
	}
	public void setTestCount(int testCount) {
		this.testCount = testCount;
	}
	private int trainCount = -1;
	public int getTrainCount() {
		return trainCount;
	}
	public void setTrainCount(int trainCount) {
		this.trainCount = trainCount;
	}
	private int excludedCount = -1;
	public int getExcludedCount() {
		return excludedCount;
	}
	public void setExcludedCount(int excludedCount) {
		this.excludedCount = excludedCount;
	}
	public TrainTestInfo(String trainTestdescriminatorID, String scoringFocusID){
		this.trainTestdescriminatorID = trainTestdescriminatorID;
		this.scoringFocusID = scoringFocusID;
	}
	
}
