package edu.oregonstate.eecs.iis.avatolcv.core;

import edu.oregonstate.eecs.iis.avatolcv.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.AvatolCVFileSystem;

// keys or values can have name, id and name, or type + id + name
//character:1824350|Diastema between I2 and C=characterState:4884329|Diastema present
//taxon=773126|Artibeus jamaicensis
//view=8905|Skull - ventral annotated teeth
public class NormalizedTypeIDName {
    public static final String TYPE_UNSPECIFIED = "?";
    public static final String ID_UNSPECIFIED = "?";
    public static final String NAME_UNSPECIFIED = "?";

    private String type = TYPE_UNSPECIFIED;
    private String ID = ID_UNSPECIFIED;
    private String name = NAME_UNSPECIFIED;
    protected String normalizedValue = null;
    public NormalizedTypeIDName(String s) throws AvatolCVException {
    	//System.out.println("instantiating from " + s);
    	if (null == s){
    		// create a "null" instance 
    		this.type = "";
    		this.ID = "";
    		this.name = "";
    		this.normalizedValue = "";
    		return;		
    	}
    	if (s.startsWith(AvatolCVFileSystem.RESERVED_PREFIX)){
    		// don't add type and id to avcv_* 
    		this.type = "";
    		this.ID = "";
    		this.name = s;
    		this.normalizedValue = s;
    		return;
    	}
        int colonCount = s.length() - s.replace(":", "").length();
        if (colonCount > 1){
            // assume that this is a simple string value with more than one : in it, like a time value
        	// but, since we might be parsing a prior output of buildTypeIdName(), trim off leading ":|" if present
        	if (s.startsWith(":|")){
        		s = s.replaceFirst(":|", "");
        	}
        	parseAsIDName(s);
        }
        else if (colonCount == 0){
            parseAsIDName(s);
        }
        else {
            if (s.startsWith(":")){
                String idAndName = s.replaceAll(":", "");
                parseAsIDName(idAndName);
            }
            else if (s.endsWith(":")){
                throw new AvatolCVException("malformed NormalizedTypeIDName construct - no id or value follows colon :  " + s);
            }
            else {
                String[] parts = s.split(":");
                this.type = parts[0];
                parseAsIDName(parts[1]);
            } 
            
        }
        this.normalizedValue = buildTypeIdName(this.type,this.ID,this.name);
    }
    public void parseAsIDName(String s) throws AvatolCVException {
        int count = s.length() - s.replace("|", "").length();
        if (count > 1){
            throw new AvatolCVException("malformed NormalizedTypeIDName construct - should only have one or zero |  " + s);
        }
        if (s.equals("|")){
            throw new AvatolCVException("malformed NormalizedTypeIDName construct - needs to have id or name  " + s);
        }
        if (s.startsWith("|")){
            this.name = s.replaceAll("\\|", "");
            // no ID specified, make up ID
            this.ID = ID_UNSPECIFIED;
            
        }
        else if (s.endsWith("|")){
            this.ID = s.replaceAll("\\|", "");
            //this.name = "NAME_" + this.ID;
            this.name = this.ID;
        }
        else if (count == 0){
            this.name = s;
            this.ID = ID_UNSPECIFIED;
        }
        else {
            String[] valueParts = s.split("\\|");
            this.ID = valueParts[0];
            this.name = valueParts[1];
        }
    }
    public String getType() {
        return type;
    }
    public String getID() {
        return ID;
    }
    public String getName() {
        return name;
    }
    public static String buildTypeIdName(String type, String id, String name){
        return type + ":" + id + "|" + name;
    }
    public String getNormalizedValue(){
    	return this.normalizedValue;
    }
    public String toString(){
		return this.normalizedValue;
	}
    
}
