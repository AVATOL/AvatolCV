package edu.oregonstate.eecs.iis.avatol.algorithm;

import edu.oregonstate.eecs.iis.avatolcv.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.AvatolCVFileSystem;
import edu.oregonstate.eecs.iis.avatolcv.algorithm.AlgorithmSequence;
import edu.oregonstate.eecs.iis.avatolcv.session.DatasetInfo;
import junit.framework.Assert;
import junit.framework.TestCase;

public class TestAlgorithmSequence extends TestCase {
    protected void setUp(){
        try {
            AvatolCVFileSystem.setRootDir(TestAlgorithm.getValidRoot());
            DatasetInfo di = new DatasetInfo();
            di.setName("unitTest");
            di.setID("xyz");
            di.setProjectID("abc");
            AvatolCVFileSystem.setDatasourceName("local");
            AvatolCVFileSystem.setSessionID("testSession");
            AvatolCVFileSystem.setChosenDataset(di);
        }
        catch(AvatolCVException ace){
            Assert.fail("Proplem initializing AvatolCVFileSystem " + ace.getMessage());
        }
    }
    
    
    public void testSegOrientScore(){
        try {
            AlgorithmSequence as = new AlgorithmSequence();
            as.setRawDataDir(                           "rawData");
            as.setSegmentedDataDir(                     "segOut");
            as.setScoredDataDir(                        "scoredOut");
            as.setOrientedDataDir(                      "oriented");
            as.setManuallyProvidedSegmentationLabelsDir("manSeg");
            as.setManuallyProvidedOrientationLabelsDir( "manOrient");
            as.setManuallyProvidedScoringLabelsDir(     "manScored");
            as.enableSegmentation();
            Assert.assertEquals(as.getInputDir(),             "rawData");
            Assert.assertEquals(as.getOutputDir(),            "segOut");
            Assert.assertEquals(as.getSupplementalInputDir(), "manSeg");
            
            as.enableOrientation();
            Assert.assertEquals(as.getInputDir(),             "segOut");
            Assert.assertEquals(as.getOutputDir(),            "oriented");
            Assert.assertEquals(as.getSupplementalInputDir(), "manOrient");
            
            as.enableScoring();
            Assert.assertEquals(as.getInputDir(),             "oriented");
            Assert.assertEquals(as.getOutputDir(),            "scoredOut");
            Assert.assertEquals(as.getSupplementalInputDir(), "manScored");
            
            // backup
            
            as.enableScoring();
            Assert.assertEquals(as.getInputDir(),             "oriented");
            Assert.assertEquals(as.getOutputDir(),            "scoredOut");
            Assert.assertEquals(as.getSupplementalInputDir(), "manScored");
            
            as.enableOrientation();
            Assert.assertEquals(as.getInputDir(),             "segOut");
            Assert.assertEquals(as.getOutputDir(),            "oriented");
            Assert.assertEquals(as.getSupplementalInputDir(), "manOrient");
            
            as.enableSegmentation();
            Assert.assertEquals(as.getInputDir(),             "rawData");
            Assert.assertEquals(as.getOutputDir(),            "segOut");
            Assert.assertEquals(as.getSupplementalInputDir(), "manSeg");
        }
        catch(AvatolCVException ace){
            Assert.fail(ace.getMessage());
        }
    }
    
   
    
    public void testSegScoreCase(){
        try {
            AlgorithmSequence as = new AlgorithmSequence();
            as.setRawDataDir(                           "rawData");
            as.setSegmentedDataDir(                     "segOut");
            as.setScoredDataDir(                        "scoredOut");
            as.setOrientedDataDir(                      "oriented");
            as.setManuallyProvidedSegmentationLabelsDir("manSeg");
            as.setManuallyProvidedOrientationLabelsDir( "manOrient");
            as.setManuallyProvidedScoringLabelsDir(     "manScored");
            as.enableSegmentation();
            Assert.assertEquals(as.getInputDir(),             "rawData");
            Assert.assertEquals(as.getOutputDir(),            "segOut");
            Assert.assertEquals(as.getSupplementalInputDir(), "manSeg");
            
            as.enableScoring();
            Assert.assertEquals(as.getInputDir(),             "segOut");
            Assert.assertEquals(as.getOutputDir(),            "scoredOut");
            Assert.assertEquals(as.getSupplementalInputDir(), "manScored");

            // backup
            
            as.enableScoring();
            Assert.assertEquals(as.getInputDir(),             "segOut");
            Assert.assertEquals(as.getOutputDir(),            "scoredOut");
            Assert.assertEquals(as.getSupplementalInputDir(), "manScored");
            
            as.enableSegmentation();
            Assert.assertEquals(as.getInputDir(),             "rawData");
            Assert.assertEquals(as.getOutputDir(),            "segOut");
            Assert.assertEquals(as.getSupplementalInputDir(), "manSeg");
        }
        catch(AvatolCVException ace){
            Assert.fail(ace.getMessage());
        }
    }
    public void testOrientScoreCase(){
        try {
            AlgorithmSequence as = new AlgorithmSequence();
            as.setRawDataDir(                           "rawData");
            as.setSegmentedDataDir(                     "segOut");
            as.setScoredDataDir(                        "scoredOut");
            as.setOrientedDataDir(                      "oriented");
            as.setManuallyProvidedSegmentationLabelsDir("manSeg");
            as.setManuallyProvidedOrientationLabelsDir( "manOrient");
            as.setManuallyProvidedScoringLabelsDir(     "manScored");
            as.enableOrientation();
            Assert.assertEquals(as.getInputDir(),             "rawData");
            Assert.assertEquals(as.getOutputDir(),            "oriented");
            Assert.assertEquals(as.getSupplementalInputDir(), "manOrient");
            
            as.enableScoring();
            Assert.assertEquals(as.getInputDir(),             "oriented");
            Assert.assertEquals(as.getOutputDir(),            "scoredOut");
            Assert.assertEquals(as.getSupplementalInputDir(), "manScored");
            
            // backup 
            
            as.enableScoring();
            Assert.assertEquals(as.getInputDir(),             "oriented");
            Assert.assertEquals(as.getOutputDir(),            "scoredOut");
            Assert.assertEquals(as.getSupplementalInputDir(), "manScored");
            
            as.enableOrientation();
            Assert.assertEquals(as.getInputDir(),             "rawData");
            Assert.assertEquals(as.getOutputDir(),            "oriented");
            Assert.assertEquals(as.getSupplementalInputDir(), "manOrient");
        }
        catch(AvatolCVException ace){
            Assert.fail(ace.getMessage());
        }
    }
    public void testScoreCase(){
        try {
            AlgorithmSequence as = new AlgorithmSequence();
            as.setRawDataDir(                           "rawData");
            as.setSegmentedDataDir(                     "segOut");
            as.setScoredDataDir(                        "scoredOut");
            as.setOrientedDataDir(                      "oriented");
            as.setManuallyProvidedSegmentationLabelsDir("manSeg");
            as.setManuallyProvidedOrientationLabelsDir( "manOrient");
            as.setManuallyProvidedScoringLabelsDir(     "manScored");
            as.enableScoring();
            Assert.assertEquals(as.getInputDir(),             "rawData");
            Assert.assertEquals(as.getOutputDir(),            "scoredOut");
            Assert.assertEquals(as.getSupplementalInputDir(), "manScored");
            
            //backup
            
            as.enableScoring();
            Assert.assertEquals(as.getInputDir(),             "rawData");
            Assert.assertEquals(as.getOutputDir(),            "scoredOut");
            Assert.assertEquals(as.getSupplementalInputDir(), "manScored");
        }
        catch(AvatolCVException ace){
            Assert.fail(ace.getMessage());
        }
    }
   
}
