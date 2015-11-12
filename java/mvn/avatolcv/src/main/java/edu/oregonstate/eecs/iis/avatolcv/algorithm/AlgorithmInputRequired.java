package edu.oregonstate.eecs.iis.avatolcv.algorithm;

import edu.oregonstate.eecs.iis.avatolcv.AvatolCVException;

/*
 * inputRequired:testImagesMaskFile refsFilesWithSuffix _croppedMask ofType mask_SpecimenGreen_BackgroundBlue_ClutterRed
 */
public class AlgorithmInputRequired extends AlgorithmInput{
    
  
    
    public AlgorithmInputRequired(String line) throws AvatolCVException {
        // remove the declarationKey
        String args = line.replace(Algorithm.DECLARATION_INPUT_REQUIRED, "");
        super.validateArgs(args);
    }
    
    protected String getUsage(){
        return "inputRequired:someKey refsFilesWithSuffix <someSuffix> ofType <someTypeRegisteredIn_modules/algorithmDataTypes.txt>";
    }
}
