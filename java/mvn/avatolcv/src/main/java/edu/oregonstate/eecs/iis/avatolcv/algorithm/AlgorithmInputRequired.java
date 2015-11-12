package edu.oregonstate.eecs.iis.avatolcv.algorithm;

import edu.oregonstate.eecs.iis.avatolcv.AvatolCVException;

/*
 * inputRequired:testImagesMaskFile refsFilesWithSuffix _croppedMask ofType mask_SpecimenGreen_BackgroundBlue_ClutterRed
 */
public class AlgorithmInputRequired {
    public static final String REFS_FILES_WITH_SUFFIX = "refsFilesWithSuffix";
    public static final String NO_SUFFIX = "*";
    public static final String OF_TYPE = "ofType";
    private static final String usage = "inputRequired:someKey refsFilesWithSuffix <someSuffix> ofType <someTypeRegisteredIn_modules/algorithmDataTypes.txt>";
    private String key = null;
    private String suffix = null;
    private String type = null;
    public AlgorithmInputRequired(String line) throws AvatolCVException {
        // remove the declarationKey
        line = line.replace(Algorithm.DECLARATION_INPUT_REQUIRED, "");
        if ("".equals(line)){
            expressUsageError(line);
        }
        String[] parts = line.split(" ");
        if (parts.length != 5){
            expressUsageError(line);
        }
        this.key = parts[0];
        String refsString = parts[1];
        this.suffix = parts[2];
        String ofType = parts[3];
        this.type = parts[4];
        
        if (!refsString.equals(REFS_FILES_WITH_SUFFIX)){
            expressUsageError(line);
        }
        if (!ofType.equals(OF_TYPE)){
            expressUsageError(line);
        }
    }
    private void expressUsageError(String line) throws AvatolCVException {
        throw new AvatolCVException("inputRequired: declaration malformed : " + line + " should be " + usage);
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
    
}
