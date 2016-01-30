package edu.oregonstate.eecs.iis.avatolcv.algorithm;

import edu.oregonstate.eecs.iis.avatolcv.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.util.ClassicSplitter;

/*
 *  outputGenerated:ofType mask_SpecimenGreen_BackgroundBlue_ClutterRed withSuffix _croppedMask
 */
public class AlgorithmOutput {
    public static final String REFS_FILES_WITH_SUFFIX = "withSuffix";
    public static final String NO_SUFFIX = "*";
    public static final String OF_TYPE = "ofType";
    private String suffix = null;
    private String type = null;
    
    public AlgorithmOutput(String line) throws AvatolCVException {
        // remove the declarationKey
        String args = line.replace(Algorithm.DECLARATION_OUTPUT_GENERATED, "");
        validateArgs(args);
    }
    
    public void validateArgs(String args) throws AvatolCVException {
        if ("".equals(args)){
            expressUsageError(args);
        }
        String[] parts = ClassicSplitter.splitt(args,' ');
        if (parts.length != 4){
            expressUsageError(args);
        }

        String ofType = parts[0];
        this.type = parts[1];
        String refsString = parts[2];
        this.suffix = parts[3];
        
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
        return "outputGenerated:ofType <someTypeRegisteredIn_modules/algorithmDataTypes.txt> withSuffix <someSuffix>";
    }
}

