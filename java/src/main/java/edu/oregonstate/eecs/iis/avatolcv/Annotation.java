package edu.oregonstate.eecs.iis.avatolcv;

public class Annotation {
	public static final String ANNOTATION_DELIM = "|";
    private String coordinateList;
    private String type;
    private String charId;
    private String charNameText;
    private String charState;
    private String charStateText;
    private int lineNumber;
    private String mediaId;
    private String pathname;
    public Annotation(String info, int lineNumber, String mediaId, String pathname){
    	String[] parts = info.split(":");
    	System.out.println("annotation info: " + info);
        
        this.coordinateList = parts[0];
        this.type = deriveType(this.coordinateList);
        this.charId = parts[1];
        this.charNameText = parts[2];
        this.charState = parts[3];
        this.charStateText = parts[4];
        this.lineNumber = lineNumber;
        this.mediaId = mediaId;
        this.pathname = pathname;
    }
    public String deriveType(String coordinateList){
    	String[] parts = coordinateList.split(";");
        if (parts.length == 1){
        	return "point";
        }  
        else if (parts.length == 2){
            return "box";  
        }
        else{
            return "polygon";
        }
    }
    public String getType(){
    	return this.type;
    }
    public String getCharId(){
    	return this.charId;
    }
    public String getCharNameText(){
    	return this.charNameText;
    }
    public String getCharState(){
    	return this.charState;
    }
    public String getCharStateText(){
    	return this.charStateText;
    }
    public int getLineNumber(){
    	return this.lineNumber;
    }
    public String getMediaId(){
    	return this.mediaId;
    }
    public String getPathname(){
    	return this.pathname;
    }
    public String getTrainingDataLine(String mediaFilename, String taxonId){
    	return "training_data" + ANNOTATION_DELIM + "media/" + mediaFilename + ANNOTATION_DELIM + 
                    charState + ANNOTATION_DELIM + charStateText + ANNOTATION_DELIM + pathname + ANNOTATION_DELIM + taxonId + ANNOTATION_DELIM + lineNumber;          
    }
}
