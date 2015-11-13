package edu.oregonstate.eecs.iis.avatolcv.algorithm;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import edu.oregonstate.eecs.iis.avatolcv.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.AvatolCVFileSystem;

/*
 *  segmentationOutputDir=<path of dir where output goes>
    avatolCVStatusFile=<path to file to write status to> (avatolCV will poll that file)
    
AvatolCV generates this line and the associated files due to inputRequired line in algProperties file:
    testImagesFile=<someAbsolutePath>/testImagesFile.txt
    
AvatolCV generates this line and the associated files entries(if any) due to inputOptional line in algProperties file:
    userProvidedGroundTruthImagesFile=<someAbsolutePath>/userProvidedGroundTruthImagesFile.txt
    userProvidedTrainImagesFile=<someAbsolutePath>/userProvidedTrainImagesFile.txt   
 */
public class RunConfigFile {
    private static final String FILESEP = System.getProperty("file.separator");
    private static final String NL = System.getProperty("line.separator");
    private String pathOfSessionInputFiles = null;
    private String pathOfUserProvidedFiles = null;
    List<String> dependencyEntries = new ArrayList<String>();
    List<String> inputRequiredEntries = new ArrayList<String>();
    List<String> inputOptionalEntries = new ArrayList<String>();
    
    public RunConfigFile(Algorithm alg, String pathOfSessionInputFiles, String pathOfUserProvidedFiles) throws AvatolCVException {
        handleDependencies(alg);
        handleOptionalInputs(alg);
        handleRequiredInputs(alg);
        this.pathOfSessionInputFiles = pathOfSessionInputFiles;
        this.pathOfUserProvidedFiles = pathOfUserProvidedFiles;
    }

    public String getRunConfigPath(){
        return null;
    }
    public static String getFileListPathnameForKey(String key, String algType) throws AvatolCVException {
        String pathRoot = "";
        if (algType.equals(Algorithm.PROPERTY_ALG_TYPE_VALUE_SEGMENTATION)){
            pathRoot = AvatolCVFileSystem.getSegmentationInputDir();
        }
        else if  (algType.equals(Algorithm.PROPERTY_ALG_TYPE_VALUE_ORIENTATION)){
            pathRoot = AvatolCVFileSystem.getOrientationInputDir();
            
        }
        else if  (algType.equals(Algorithm.PROPERTY_ALG_TYPE_VALUE_SCORING)){
            pathRoot = AvatolCVFileSystem.getScoringInputDir();
        }
        else {
            throw new AvatolCVException("unrecognized algType " + algType);
        }
        return pathRoot + FILESEP + key + ".txt";
    }
    //
    // dependencies
    //
    public void handleDependencies(Algorithm alg){
        List<AlgorithmDependency> dependencies = alg.getDependencies();
        for (AlgorithmDependency ad : dependencies){
            dependencyEntries.add(generateEntryForDependency(ad));
        }
    }
    public static String generateEntryForDependency(AlgorithmDependency ad){
        return ad.getKey() + "=" + ad.getPath();
    }
    
    //
    // required inputs
    //
    public void handleRequiredInputs(Algorithm alg) throws AvatolCVException {
        String algType = alg.getAlgType();
        List<AlgorithmInputRequired> requiredInputs = alg.getRequiredInputs();
        for (AlgorithmInputRequired air : requiredInputs){
            inputRequiredEntries.add(generateEntryForRequiredInput(air, algType));
            String path = getFileListPathnameForKey(air.getKey(),algType);
            generateFileList(path, air.getSuffix(), this.pathOfSessionInputFiles);
        }
    }
    public static String generateEntryForRequiredInput(AlgorithmInputRequired air, String type) throws AvatolCVException {
        String key = air.getKey();
        return key + "=" + getFileListPathnameForKey(key, type);
    }
   
    //
    // optional inputs
    //
    public void handleOptionalInputs(Algorithm alg) throws AvatolCVException {
        String algType = alg.getAlgType();
        List<AlgorithmInputOptional> optionalInputs = alg.getOptionalInputs();
        for (AlgorithmInputOptional oi : optionalInputs){
            inputOptionalEntries.add(generateEntryForOptionalInput(oi, algType));
            String path = getFileListPathnameForKey(oi.getKey(),algType);
            generateFileList(path, oi.getSuffix(), this.pathOfUserProvidedFiles);
        }
    }
    public static String generateEntryForOptionalInput(AlgorithmInputOptional aio, String type) throws AvatolCVException {
        String key = aio.getKey();
        return key + "=" + getFileListPathnameForKey(key, type);
    }
   
    public void generateFileList(String pathOfFileToCreate, String suffix, String pathWhereDataLives) throws AvatolCVException {
        File sourceDir = new File(pathWhereDataLives);
        if (!sourceDir.exists()){
            throw new AvatolCVException("Cannot generate runConfigFile - no directory where input files should be " + pathWhereDataLives);
        }
        File[] files = sourceDir.listFiles();
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(pathOfFileToCreate));
            for (File f : files){
                String fName = f.getName();
                String[] parts = fName.split("\\.");
                String fileDescriptor = parts[parts.length - 1];
                String rootName = fName.replace("." + fileDescriptor, "");
                
                if (rootName.endsWith(suffix)){
                    writer.write(f.getAbsolutePath() + NL);
                }
            }
            writer.close();
        }
        catch(IOException ioe){
            throw new AvatolCVException("problem creating runConfigFile at " + pathOfFileToCreate);
        }
        
    }
}
