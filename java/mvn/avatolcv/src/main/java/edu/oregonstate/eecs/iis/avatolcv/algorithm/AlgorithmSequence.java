package edu.oregonstate.eecs.iis.avatolcv.algorithm;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
    
    private String rawDataDir = null;
    
    private String segmentedDataDir = null;
    private String orientedDataDir = null;
    private String scoredDataDir = null;
    
    private String manuallyProvidedSegmentationLabelsDir = null;
    private String manuallyProvidedOrientationLabelsDir = null;
    private String manuallyProvidedScoringLabelsDir = null;
    private static final Logger logger = LogManager.getLogger(AlgorithmSequence.class);
    public AlgorithmSequence(){
        
    }
    public void setRawDataDir(String rawDataDir){
    	this.rawDataDir = rawDataDir;
    }
    public void setSegmentedDataDir(String segmentedDataDir){
    	this.segmentedDataDir = segmentedDataDir;
    }
    public void setOrientedDataDir(String orientedDataDir){
    	this.orientedDataDir = orientedDataDir;
    }
    public void setScoredDataDir(String scoredDataDir){
    	this.scoredDataDir = scoredDataDir;
    }
    public void setManuallyProvidedSegmentationLabelsDir(String manuallyProvidedSegmentationLabelsDir){
    	this.manuallyProvidedSegmentationLabelsDir = manuallyProvidedSegmentationLabelsDir;
    }
    public void setManuallyProvidedOrientationLabelsDir(String manuallyProvidedOrientationLabelsDir){
    	this.manuallyProvidedOrientationLabelsDir = manuallyProvidedOrientationLabelsDir;
    }
    public void setManuallyProvidedScoringLabelsDir(String manuallyProvidedScoringLabelsDir){
    	this.manuallyProvidedScoringLabelsDir = manuallyProvidedScoringLabelsDir;
    }
    public void enableSegmentation() throws AvatolCVException {
        inputDir =              this.rawDataDir;
        supplementatlInputDir = this.manuallyProvidedSegmentationLabelsDir;
        outputDir =             this.segmentedDataDir;
        
        stages.add(seg);
        currentStage = seg;
    }
    public void enableOrientation() throws AvatolCVException {
        logger.info("enabling orientation... the following should match:");
        logger.info("seg:          " + seg);
        logger.info("currentStage: " + currentStage);
        if (scoring.equals(currentStage)){
        	// backed up , need to 
        	if (stages.contains(seg)){
        		// need to work off output of segmentation
                inputDir = this.segmentedDataDir;
        	}
        	else {
        		// need to work off raw data
                inputDir = this.rawDataDir;
        	}
        }
        else if (orient.equals(currentStage)){
    		// do nothing, must have cancelled alg to re-run - already setup
    	}
    	else if (seg.equals(currentStage)){
            // need to work off output of segmentation
            inputDir = this.segmentedDataDir;
            logger.info("matched");
        }
        else {
            // need to work off raw data
            inputDir = this.rawDataDir;
            logger.info("didn't match");
        }
        supplementatlInputDir = this.manuallyProvidedOrientationLabelsDir;
        outputDir = this.orientedDataDir;
        
        stages.add(orient);
        currentStage = orient;
    }
    public void enableScoring() throws AvatolCVException{
    	if (scoring.equals(currentStage)){
    		// do nothing, must have cancelled alg to re-run - already setup
    	}
    	else if (orient.equals(currentStage)){
            // need to work off output of orientation
            inputDir = this.orientedDataDir;
        }
        else if (seg.equals(currentStage)){
            // need to work off output of segmentation
            inputDir = this.segmentedDataDir;
        }
        else {
            // working off raw data
            inputDir = this.rawDataDir;
        }
        supplementatlInputDir = this.manuallyProvidedScoringLabelsDir;
        outputDir = this.scoredDataDir;
        
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
