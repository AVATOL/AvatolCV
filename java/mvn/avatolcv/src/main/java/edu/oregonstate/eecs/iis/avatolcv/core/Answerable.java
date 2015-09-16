package edu.oregonstate.eecs.iis.avatolcv.core;

import java.util.Hashtable;

public class Answerable {
	private Hashtable<String, String> answers = null;
	private Answerable nextAnswerable = null;
	public void setNextAnswerableInSeries(Answerable answerable){
		this.nextAnswerable = answerable;
	}
	public Answerable getNextAnswerable(){
		return this.nextAnswerable;
	}
	public void saveAnswers(Hashtable<String,String> hash){
		 answers = hash;
	}
	public boolean hasPriorAnswers(){
		 if (null == answers){
			 return false;
		 }
		 return true;
	}
	public Hashtable<String,String> getPriorAnswers(){
		return answers; 
	}
	public void flushAnswers(){
		answers = null;
		if (null != this.nextAnswerable){
			this.nextAnswerable.flushAnswers();
		}
	}
}
