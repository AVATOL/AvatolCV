package edu.oregonstate.eecs.iis.avatolcv.algorithm;

import edu.oregonstate.eecs.iis.avatolcv.AvatolCVException;

public class AlgorithmInput {
    public static final String REFS_FILES_WITH_SUFFIX = "refsFilesWithSuffix";
    public static final String NO_SUFFIX = "*";
    public static final String OF_TYPE = "ofType";
    private String key = null;
    private String suffix = null;
    private String type = null;
    
    public void validateArgs(String args) throws AvatolCVException {
        if ("".equals(args)){
            expressUsageError(args);
        }
        String[] parts = args.split(" ");
        if (parts.length != 5){
            expressUsageError(args);
        }
        this.key = parts[0];
        String refsString = parts[1];
        this.suffix = parts[2];
        String ofType = parts[3];
        this.type = parts[4];
        
        if (!refsString.equals(REFS_FILES_WITH_SUFFIX)){
            expressUsageError(args);
        }
        if (!ofType.equals(OF_TYPE)){
            expressUsageError(args);
        }
    }
    private void expressUsageError(String line) throws AvatolCVException {
        throw new AvatolCVException("declaration malformed : " + line + " should be " + getUsage());
    }
    public String getKey() {
        return key;
    }
    public String getSuffix() {
        return suffix;
    }
    public String getType() {
        return type;
    }
    public boolean hasSuffix(){
        if (suffix.equals(NO_SUFFIX)){
            return false;
        }
        if (suffix.equals("")){
            return false;
        }
        return true;
    }
    protected String getUsage(){
        return "should be overridden";
    }
}
