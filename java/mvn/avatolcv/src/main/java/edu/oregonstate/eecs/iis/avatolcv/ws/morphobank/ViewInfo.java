package edu.oregonstate.eecs.iis.avatolcv.ws.morphobank;

import java.util.List;

public class ViewInfo {
    //{"ok":true,"views":[{"viewID":"2236","name":"Cats"},{"viewID":"2369","name":"Cats2"},{"viewID":"4497","name":"test"},{"viewID":"4498","name":"test2"},{"viewID":"6280","name":"smith"},{"viewID":"6281","name":"wesson"},{"viewID":"6282","name":"clink"},{"viewID":"6856","name":"testing all the time, blah"}]}
    private String ok;
    private List<MBView> views;

    public void setOk(String s){
		 this.ok = s;
	 }
	 public String getOk(){
		 return this.ok;
	 }
	 
	 public void setViews(List<MBView> s){
		 this.views = s;
	 }
	 public List<MBView> getViews(){
		 return this.views;
	 }
	 
    public static class MBView {
    	private String viewID;
    	private String name;
    	
    	 public void setViewID(String s){
			 this.viewID = s;
		 }
		 public String getViewID(){
			 return this.viewID;
		 }
		 
		 public void setName(String s){
			 this.name = s;
		 }
		 public String getName(){
			 return this.name;
		 }
		 
    }
}
