package edu.oregonstate.eecs.iis.avatolcv.algorithm;

import java.util.ArrayList;
import java.util.List;

import edu.oregonstate.eecs.iis.avatolcv.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.AvatolCVFileSystem;

/*
 * This class encapsulate logic for knowing which input and output dirs are in play depending on whether segmentation and orientation are run or not before scoring.
 * Since the UI will enforce the sequence, I won't error check for things trying to happen out of order
 */
public class AlgorithmSequence {
    private List<String> stages = new ArrayList<String>();
    private String currentStage = null;
    private String seg =     AlgorithmModules.AlgType.SEGMENTATION.toString().toLowerCase();
    private String orient =  AlgorithmModules.AlgType.ORIENTATION.toString().toLowerCase();
    private String scoring = AlgorithmModules.AlgType.SCORING.toString().toLowerCase();
    private String inputDir = null;
    private String supplementatlInputDir = null;
    private String outputDir = null;
            
    public AlgorithmSequence(){
        
    }
    public void enableSegmentation() throws AvatolCVException {
        inputDir =              AvatolCVFileSystem.getNormalizedImagesLargeDir();
        supplementatlInputDir = AvatolCVFileSystem.getManuallyProvidedSegmentationLabelsDir();
        outputDir =             AvatolCVFileSystem.getSegmentedDataDir();
        
        stages.add(seg);
        currentStage = seg;
    }
    public void enableOrientation() throws AvatolCVException {
        System.out.println("enabling orientation... the following should match:");
        System.out.println("seg:          " + seg);
        System.out.println("currentStage: " + currentStage);
        if (seg.equals(currentStage)){
            // need to work off output of segmentation
            inputDir = AvatolCVFileSystem.getSegmentedDataDir();
            System.out.println("matched");
        }
        else {
            // need to work off raw data
            inputDir = AvatolCVFileSystem.getNormalizedImagesLargeDir();
            System.out.println("didn't match");
        }
        supplementatlInputDir = AvatolCVFileSystem.getManuallyProvidedOrientationLabelsDir();
        outputDir = AvatolCVFileSystem.getOrientedDataDir();
        
        stages.add(orient);
        currentStage = orient;
    }
    public void enableScoring() throws AvatolCVException{
        if (orient.equals(currentStage)){
            // need to work off output of orientation
            inputDir = AvatolCVFileSystem.getOrientedDataDir();
        }
        else if (seg.equals(currentStage)){
            // need to work off output of segmentation
            inputDir = AvatolCVFileSystem.getSegmentedDataDir();
        }
        else {
            // working off raw data
            inputDir = AvatolCVFileSystem.getNormalizedImagesLargeDir();
        }
        supplementatlInputDir = AvatolCVFileSystem.getManuallyProvidedScoringLabelsDir();
        outputDir = AvatolCVFileSystem.getScoredDataDir();
        
        stages.add(scoring);
        currentStage = scoring;
    }
    
    public String getInputDir(){
        return inputDir;
    }
    public String getSupplementalInputDir(){
        return supplementatlInputDir;
    }
    public String getOutputDir() {
        return outputDir;
    }
    public String getCurrentStage(){
        return this.currentStage;
    }
}
