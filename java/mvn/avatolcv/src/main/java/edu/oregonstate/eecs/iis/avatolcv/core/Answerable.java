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
	public void saveAnswers(Hashtable<String,String> newAnswers){
	    if (null != this.answers){
	        if (!newAnswers.equals(answers)){
	            // answers are changing - need to flush!
	            if (null != this.nextAnswerable){
	                this.nextAnswerable.flushDownstreamAnswers();
	            }
	        }
	    }
		answers = newAnswers;
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
	public void flushDownstreamAnswers(){
	    this.answers = null;
	    System.out.println("....flush...!!");
		if (null != this.nextAnswerable){
			this.nextAnswerable.flushDownstreamAnswers();
		}
	}
}
