package edu.oregonstate.eecs.iis.avatolcv.normalized;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import edu.oregonstate.eecs.iis.avatolcv.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.AvatolCVFileSystem;
import edu.oregonstate.eecs.iis.avatolcv.core.ImageInfo;

public class NormalizedImageInfo {
    //avcv_annotation=point:21.2571225071225,55.3632478632479+point:21.84729344729345,40.810256410256414
    //character:1824350|Diastema between I2 and C=characterState:4884329|Diastema present
    //taxon=773126|Artibeus jamaicensis
    //view=8905|Skull - ventral annotated teeth
    protected Hashtable<NormalizedKey, NormalizedValue> keyValueHash = new Hashtable<NormalizedKey, NormalizedValue>();
    public static final String NL = System.getProperty("line.separator");
    public static final String PREFIX = AvatolCVFileSystem.RESERVED_PREFIX;
    public static final String KEY_ANNOTATION         = PREFIX + "annotation";
    public static final String KEY_IMAGE_NAME         = PREFIX + "imageName";
    public static final String KEY_TIMESTAMP          = PREFIX + "timestamp";
    public static final String KEY_TRAINING_VS_TEST_CONCERN  = PREFIX + "trainingVsTestConcern";
    private String filename = null;
    protected String imageName = "";
    protected String annotationString = "";
    protected String timestamp = "";
    protected String trainingTestConcern = "";
    private String imageID = null;
    private String niiString = null;
    private String path = null;
    public NormalizedImageInfo(String path) throws AvatolCVException {
        //System.out.println("loading nii " + path);
    	this.filename = new File(path).getName();
    	this.path = path;
        this.imageID = getImageIDFromPath(path);
        loadNormalizedInfoFromPath(path, "Problem loading Normalized Image Info file: ");
    }
    public NormalizedImageInfo(List<String> lines, String imageID, String path) throws AvatolCVException {
    	this.imageID = imageID;
    	this.path = path;
    	this.filename = new File(path).getName();
    	loadNormalizedInfoFromLines(lines, "Problem loading Normalized Image Info file: ");
    }
    public boolean equals(Object other){
    	NormalizedImageInfo otherNii = (NormalizedImageInfo)other;
    	String niiStringOther = otherNii.getNiiString();
    	String niiStringThis = niiString;
    	//System.out.println("other: " + niiStringOther);
    	//System.out.println("this : " + niiString);
    	return niiStringOther.equals(niiString);
    }
    public void persist() throws AvatolCVException {
    	try {
    		List<NormalizedKey> keys = getKeys();
    		Collections.sort(keys);
    		BufferedWriter writer = new BufferedWriter(new FileWriter(this.path));
    		if (!this.imageName.equals("")){
    		    writer.write(KEY_IMAGE_NAME + "=" + this.imageName +  NL);
    		}
    		if (!this.timestamp.equals("")){
    		    writer.write(KEY_TIMESTAMP + "=" + this.timestamp +  NL);
    		}
    		if (!this.annotationString.equals("")){
    		    writer.write(KEY_ANNOTATION + "=" + this.annotationString +  NL);
    		}
            
            if (!this.trainingTestConcern.equals("")){
                writer.write(KEY_TRAINING_VS_TEST_CONCERN + "=" + this.trainingTestConcern +  NL);
            }
    		for (NormalizedKey key : keys){
    			writer.write(key + "=" + keyValueHash.get(key) + NL);
    		}
    		writer.close();
    	}
    	catch(IOException ioe){
    		throw new AvatolCVException("problem persisting NormalizedImageInfo file " + path + " : " + ioe.getMessage());
    	}
    }
    public List<NormalizedKey> getKeys(){
    	List<NormalizedKey> result = new ArrayList<NormalizedKey>();
    	Enumeration<NormalizedKey> keysEnum = keyValueHash.keys();
    	while (keysEnum.hasMoreElements()){
    		NormalizedKey key = keysEnum.nextElement();
    		result.add(key);
    	}
    	return result;
    }
    public void addUnscoredKey(NormalizedKey key) throws AvatolCVException {
    	if (hasValueForKey(key)){
    		throw new AvatolCVException("cannot add unscored key " + key + " to NormalizedImageInfoFile because it already has value " + keyValueHash.get(key) + " (imageID " + imageID + ")");
    	}
    	keyValueHash.put(key, new NormalizedValue(""));
    }
    public String getImageFilename(){
    	return this.filename;
    }
    /*
     * Some keys and values are NormalizedTypeIDName constructs (type:ID|name) ex. (character:123456|big tooth) and some are just simple strings (name) ex. (leaf apex angle)
     * We want to support having client code be able to ask for a value by giving the full type:ID|name construct(character:123456|big tooth) or just name (big tooth) and
     *  have both return the value.  This brings the assumption that name is unique among all keys.  So if we had (type
     */
    public NormalizedValue getValueForKey(NormalizedKey key){
    	return keyValueHash.get(key);
    }
    public boolean hasValueForKey(NormalizedKey key){
    	NormalizedValue value = getValueForKey(key);
    	if (null == value){
    		return false;
    	}
    	else if ("".equals(value.getName())){
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
    
    protected void loadNormalizedInfoFromPath(String path, String errorMessage)throws AvatolCVException {
        loadNormalizedInfoFromPath(path, errorMessage, keyValueHash);
    }
    protected void loadNormalizedInfoFromPath(String path, String errorMessage, Hashtable<NormalizedKey, NormalizedValue> hash)throws AvatolCVException {
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
    public void forgetValue(NormalizedKey key) throws AvatolCVException {
    	keyValueHash.put(key, new NormalizedValue(""));
    }
    public boolean hasKey(NormalizedKey key){
    	Object val = keyValueHash.get(key);
    	return !(null == val);
    }
    public String getNiiString(){
    	return this.niiString;
    }
    protected void setNiiStringFromLines(List<String> lines) throws AvatolCVException {
    	Collections.sort(lines);
    	StringBuilder sb = new StringBuilder();
    	for (String line : lines){
    		String[] parts = line.split("=");
    		String key = parts[0];
    		String val = "";
    	    if (parts.length > 1){
    	    	val = parts[1];
    	    }
    	    if (key.startsWith(PREFIX)){
    	        sb.append(line);
    	    }
    	    else {
    	        NormalizedKey nKey = new NormalizedKey(key);
                NormalizedValue nValue = new NormalizedValue(val);
                sb.append(nKey.toString() + "=" + nValue.toString() + "  ");
    	    }
    	    
    	}
    	this.niiString = "" + sb;
    }
    protected void loadNormalizedInfoFromLines(List<String> lines, String errorMessage) throws AvatolCVException {
        loadNormalizedInfoFromLines(lines, errorMessage, keyValueHash);
    }
    protected void loadNormalizedInfoFromLines(List<String> lines, String errorMessage, Hashtable<NormalizedKey, NormalizedValue> hash) throws AvatolCVException {
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
                    hash.put(new NormalizedKey(key),new NormalizedValue(value));
                }
            }
    	}
    }
    
    public String getImageName(){
        return this.imageName;
    }
    public String getTrainingVsTestName() throws AvatolCVException {
        return this.trainingTestConcern;
    }
    public String getAnnotationString(){
        return this.annotationString;
    }
    private void loadAvatolCVKeyedLine(String line) throws AvatolCVException {
        String[] parts = line.split("=");
        String key = parts[0];
        String value = "";
        if (parts.length > 1){
            value = parts[1];
        }
        if (key.equals(KEY_ANNOTATION)){
            this.annotationString = value;
        }
        else if (key.equals(KEY_IMAGE_NAME)){
        	this.imageName = value;
        }
        else if (key.equals(KEY_TIMESTAMP)){
        	this.timestamp = value;
        }
        else if (key.equals(KEY_TRAINING_VS_TEST_CONCERN)){
            this.trainingTestConcern = value;
        }
        else {
            System.out.println("Warning - unrecognized " + PREFIX + " key encountered loading NormalizedImageFile: " + key);
        }
    }
   
	public String getAnnotationCoordinates() throws AvatolCVException  {
		return this.annotationString;
	}
	public boolean isExcluded() throws AvatolCVException {
		return ImageInfo.isExcluded(this.imageID);
	}
	public void excludeForSession(String reason) throws AvatolCVException {
		ImageInfo.excludeForSession(reason, this.imageID);
	}
	public void excludeForDataset(String reason) throws AvatolCVException {
		ImageInfo.excludeForDataset(reason, this.imageID);
	}
	public void undoExcludeForSession(String reason) throws AvatolCVException {
		ImageInfo.undoExcludeForSession(reason, this.imageID);
	}

	public void undoExcludeForDataset(String reason) throws AvatolCVException {
		ImageInfo.undoExcludeForDataset(reason, this.imageID);
	}
	
}
