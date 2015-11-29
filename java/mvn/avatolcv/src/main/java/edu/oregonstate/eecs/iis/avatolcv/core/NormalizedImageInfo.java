package edu.oregonstate.eecs.iis.avatolcv.core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import edu.oregonstate.eecs.iis.avatolcv.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.AvatolCVFileSystem;

public class NormalizedImageInfo {
    //avcv_annotation=point:21.2571225071225,55.3632478632479+point:21.84729344729345,40.810256410256414
    //character:1824350|Diastema between I2 and C=characterState:4884329|Diastema present
    //taxon=773126|Artibeus jamaicensis
    //view=8905|Skull - ventral annotated teeth
    protected Hashtable<String, Object> keyValueHash = new Hashtable<String, Object>();
    public static final String PREFIX = AvatolCVFileSystem.RESERVED_PREFIX;
    public static final String KEY_ANNOTATION         = PREFIX + "annotation";
    public static final String KEY_IMAGE_NAME         = PREFIX + "imageName";
    public static final String KEY_TIMESTAMP          = PREFIX + "timestamp";
    private static final String KEY_TRAINING_VS_TEST_CONCERN_VALUE  = PREFIX + "trainingVsTestConcernValue";
    private String filename = null;
    private String imageName = "?";
    private String imageID = null;
    private String niiString = null;
    public NormalizedImageInfo(String path) throws AvatolCVException {
        //System.out.println("loading nii " + path);
    	this.filename = new File(path).getName();
        this.imageID = getImageIDFromPath(path);
        loadNormalizedInfoFromPath(path, "Problem loading Normalized Image Info file: ", keyValueHash);
    }
    public NormalizedImageInfo(List<String> lines, String imageID, String path) throws AvatolCVException {
    	this.imageID = imageID;
    	this.filename = new File(path).getName();
    	loadNormalizedInfoFromLines(lines, "Problem loading Normalized Image Info file: ", keyValueHash);
    }
    
    public String getImageFilename(){
    	return this.filename;
    }
    public String getValueForKey(String key){
    	return (String)keyValueHash.get(key);
    }
    public boolean hasValueForKey(String key){
    	String value = getValueForKey(key);
    	if (null == value){
    		return false;
    	}
    	else if ("".equals(value)){
    		return false;
    	}
    	else {
    		return true;
    	}
    }
    public static String getImageIDFromPath(String path){
        File f = new File(path);
        String filename = f.getName();
        return getImageIDFromFilename(filename);
    }
    public static String getImageIDFromFilename(String fname){
        String[] parts = fname.split("\\.");
        String root = parts[0];
        String[] rootParts = root.split("_");
        String id = rootParts[0];
        return id;
    }
    public String getImageID(){
        return this.imageID;
    }
    
    protected void loadNormalizedInfoFromPath(String path, String errorMessage, Hashtable<String, Object> hash)throws AvatolCVException {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(path));
            List<String> lines = new ArrayList<String>();
            String line = null;
            while(null != (line = reader.readLine())){
                lines.add(line);
            }
            reader.close();
            loadNormalizedInfoFromLines(lines, errorMessage, hash);
        }
        catch(IOException ioe){
            throw new AvatolCVException(errorMessage + path + " : " + ioe.getMessage());
        }
    }
    // to support tests
    public void forgetValue(String key){
    	keyValueHash.put(key, "");
    }
    public boolean hasKey(String key){
    	Object val = keyValueHash.get(key);
    	return !(null == val);
    }
    public String getNiiString(){
    	return this.niiString;
    }
    protected void setNiiStringFromLines(List<String> lines){
    	StringBuilder sb = new StringBuilder();
    	for (String line : lines){
    		sb.append(line);
    	}
    	this.niiString = "" + sb;
    }
    protected void loadNormalizedInfoFromLines(List<String> lines, String errorMessage, Hashtable<String, Object> hash) throws AvatolCVException {
    	setNiiStringFromLines(lines);
    	for (String line : lines){
    		if (line.startsWith("#")){
                // ignore
            }
            else {
                if (line.startsWith(AvatolCVFileSystem.RESERVED_PREFIX)){
                    loadAvatolCVKeyedLine(line);
                }
                else {
                    String[] parts = line.split("=");
                    String key = parts[0];
                    String value = "";
                    if (parts.length > 1){
                        value = parts[1];
                    }
                    if (key.contains(":")){
                        // skip for now
                    }
                    else {
                        if (value.contains("|")){
                            String[] valueParts = value.split("|");
                            String id = valueParts[0];
                            String name = valueParts[1];
                            ValueIDandName inv = new ValueIDandName(id, name);
                            hash.put(key, inv);
                        }
                        else {
                            hash.put(key,value);
                        }
                    }
                }
            }
    	}
    }
    
   
   
    public String getImageName(){
        return this.imageName;
    }
    public String getTrainingVsTestName(){
        return (String)keyValueHash.get(KEY_TRAINING_VS_TEST_CONCERN_VALUE);
    }
    private void loadAvatolCVKeyedLine(String line) throws AvatolCVException {
        String[] parts = line.split("=");
        String key = parts[0];
        String value = null;
        if (parts.length > 1){
            value = parts[1];
        }
        if (key.equals(KEY_ANNOTATION)){
            loadAnnotationLine(key, value);
        }
        else if (key.equals(KEY_IMAGE_NAME)){
        	imageName = value;
        }
        else {
            //ignore;
        }
    }
    private void loadAnnotationLine(String key, String value){
      //avcv_annotation=point:21.2571225071225,55.3632478632479+point:21.84729344729345,40.810256410256414
        // ...from MorphobankDataSource.java
        // avcv_annotation=rectangle:25,45;35,87+point:98,92
        // + delimits the annotations in the series
        // ; delimits the points in the annotation
        // , delimits x and y coordinates
        // : delimits type from points
        if (null == value){
            
        }
        else {
            String[] annotationValueParts = value.split("\\+");
            
            for (String annotation : annotationValueParts){
                String[] annotationParts = annotation.split(":");
                String annotationType = annotationParts[0];
                String annotationPointSequence = annotationParts[1];
                String[] annotationPointPairs = annotationPointSequence.split(";");
                for (String pair : annotationPointPairs){
                    String[] pairParts = pair.split(",");
                    String x = pairParts[0];
                    String y = pairParts[1];
                    // FIXME - finish this liogic
                }
            }
        }
        
    }
    public class ValueIDandName{
        private String id = null;
        private String name = null;
        public ValueIDandName(String id, String name){
            this.id = id;
            this.name = name;
        }
        public String getID(){
            return this.id;
        }
        public String getName(){
            return this.name;
        }
    }
}
