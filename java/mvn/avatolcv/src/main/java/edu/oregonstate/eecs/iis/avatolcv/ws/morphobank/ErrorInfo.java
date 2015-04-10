package edu.oregonstate.eecs.iis.avatolcv.ws.morphobank;

public class ErrorInfo {
    private String ok;
    private String error;
    
    public void setOk(String s){
    	this.ok = s;
    }
    public String getOk(){
    	return ok;
    }
    public void setError(String s){
    	this.error = s;
    }
    public String getError(){
    	return error;
    }
    
}
