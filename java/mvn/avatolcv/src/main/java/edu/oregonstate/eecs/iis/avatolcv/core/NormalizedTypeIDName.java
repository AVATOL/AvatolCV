package edu.oregonstate.eecs.iis.avatolcv.core;

import edu.oregonstate.eecs.iis.avatolcv.AvatolCVException;

// keys or values can have name, id and name, or type + id + name
//character:1824350|Diastema between I2 and C=characterState:4884329|Diastema present
//taxon=773126|Artibeus jamaicensis
//view=8905|Skull - ventral annotated teeth
public class NormalizedTypeIDName {
    public static final String TYPE_UNSPECIFIED = "typeNotSpecified";
    public static final String ID_UNSPECIFIED = "idNotSpecified";
    public static final String NAME_UNSPECIFIED = "nameNotSpecified";

    private String type = TYPE_UNSPECIFIED;
    private String ID = ID_UNSPECIFIED;
    private String name = NAME_UNSPECIFIED;
    public NormalizedTypeIDName(String s) throws AvatolCVException {
        int colonCount = s.length() - s.replace(":", "").length();
        if (colonCount > 1){
            throw new AvatolCVException("malformed NormalizedTypeIDName construct - should only have one :  " + s);
        }
        if (colonCount == 0){
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
            this.ID = "ID_" + this.name;
            
        }
        else if (s.endsWith("|")){
            this.ID = s.replaceAll("\\|", "");
            this.name = "NAME_" + this.ID;
        }
        else if (count == 0){
            this.name = s;
            this.ID = "ID_" + name;
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
}
