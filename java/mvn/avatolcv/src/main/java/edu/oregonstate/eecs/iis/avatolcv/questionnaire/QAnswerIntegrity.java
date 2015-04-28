package edu.oregonstate.eecs.iis.avatolcv.questionnaire;

public class QAnswerIntegrity {
	private boolean isValid = false;
	private String reason = null;
	public QAnswerIntegrity(boolean isValid, String reason){
		this.isValid = isValid;
		this.reason = reason;
	}
	public boolean isValid(){
		return this.isValid;
	}
	public String getReason(){
		return this.reason;
	}
}
