package edu.oregonstate.eecs.iis.avatolcv.algorithm;

import edu.oregonstate.eecs.iis.avatolcv.AvatolCVException;

public class AlgorithmInputOptional extends AlgorithmInput{
 
    public AlgorithmInputOptional(String line) throws AvatolCVException {
        // remove the declarationKey
        String args = line.replace(Algorithm.DECLARATION_INPUT_OPTIONAL, "");
        super.validateArgs(args);
    }
    
    protected String getUsage(){
        return "inputOptional:someKey refsFilesWithSuffix <someSuffix> ofType <someTypeRegisteredIn_modules/algorithmDataTypes.txt>";
    }
}
