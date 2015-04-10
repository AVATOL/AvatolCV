package edu.oregonstate.eecs.iis.avatolcv.ws.morphobank;

import java.io.IOException;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;


public class ErrorCheck {
	private boolean isError = false;
	private String errorMessage = "";
    public ErrorCheck(String json){
    	ObjectMapper mapper = new ObjectMapper();
        try {
        	ErrorInfo ei = mapper.readValue(json, ErrorInfo.class);
        	this.errorMessage = ei.getError();
        	this.isError = true;
        	
        }
        catch(JsonParseException jpe){
        	this.isError = false;
        }
        catch(JsonMappingException jme){
        	this.isError = false;
        }
        catch(IOException ioe){
        	this.isError = false;
        }
    }
    public boolean isError(){
    	return isError;
    }
    public String getErrorMessage(){
    	return errorMessage;
    }
}
