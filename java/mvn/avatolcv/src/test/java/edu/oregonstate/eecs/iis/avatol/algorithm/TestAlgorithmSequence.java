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
    public void testSegOrientScoreCase(){
        try {
            AlgorithmSequence as = new AlgorithmSequence();
            as.enableSegmentation();
            Assert.assertEquals(as.getInputDir(),             AvatolCVFileSystem.getNormalizedImagesLargeDir());
            Assert.assertEquals(as.getOutputDir(),            AvatolCVFileSystem.getSegmentedDataDir());
            Assert.assertEquals(as.getSupplementalInputDir(), AvatolCVFileSystem.getManuallyProvidedSegmentationLabelsDir());
            
            as.enableOrientation();
            Assert.assertEquals(as.getInputDir(),             AvatolCVFileSystem.getSegmentedDataDir());
            Assert.assertEquals(as.getOutputDir(),            AvatolCVFileSystem.getOrientedDataDir());
            Assert.assertEquals(as.getSupplementalInputDir(), AvatolCVFileSystem.getManuallyProvidedOrientationLabelsDir());
            
            as.enableScoring();
            Assert.assertEquals(as.getInputDir(),             AvatolCVFileSystem.getOrientedDataDir());
            Assert.assertEquals(as.getOutputDir(),            AvatolCVFileSystem.getScoredDataDir());
            Assert.assertEquals(as.getSupplementalInputDir(), AvatolCVFileSystem.getManuallyProvidedScoringLabelsDir());
        }
        catch(AvatolCVException ace){
            Assert.fail(ace.getMessage());
        }
    }
    
    public void testSegScoreCase(){
        try {
            AlgorithmSequence as = new AlgorithmSequence();
            as.enableSegmentation();
            Assert.assertEquals(as.getInputDir(),             AvatolCVFileSystem.getNormalizedImagesLargeDir());
            Assert.assertEquals(as.getOutputDir(),            AvatolCVFileSystem.getSegmentedDataDir());
            Assert.assertEquals(as.getSupplementalInputDir(), AvatolCVFileSystem.getManuallyProvidedSegmentationLabelsDir());
            
            as.enableScoring();
            Assert.assertEquals(as.getInputDir(),             AvatolCVFileSystem.getSegmentedDataDir());
            Assert.assertEquals(as.getOutputDir(),            AvatolCVFileSystem.getScoredDataDir());
            Assert.assertEquals(as.getSupplementalInputDir(), AvatolCVFileSystem.getManuallyProvidedScoringLabelsDir());
        }
        catch(AvatolCVException ace){
            Assert.fail(ace.getMessage());
        }
    }
    
    public void testOrientScoreCase(){
        try {
            AlgorithmSequence as = new AlgorithmSequence();
            as.enableOrientation();
            Assert.assertEquals(as.getInputDir(),             AvatolCVFileSystem.getNormalizedImagesLargeDir());
            Assert.assertEquals(as.getOutputDir(),            AvatolCVFileSystem.getOrientedDataDir());
            Assert.assertEquals(as.getSupplementalInputDir(), AvatolCVFileSystem.getManuallyProvidedOrientationLabelsDir());
            
            as.enableScoring();
            Assert.assertEquals(as.getInputDir(),             AvatolCVFileSystem.getOrientedDataDir());
            Assert.assertEquals(as.getOutputDir(),            AvatolCVFileSystem.getScoredDataDir());
            Assert.assertEquals(as.getSupplementalInputDir(), AvatolCVFileSystem.getManuallyProvidedScoringLabelsDir());
        }
        catch(AvatolCVException ace){
            Assert.fail(ace.getMessage());
        }
    }
    public void testScoreCase(){
        try {
            AlgorithmSequence as = new AlgorithmSequence();
            
            as.enableScoring();
            Assert.assertEquals(as.getInputDir(),             AvatolCVFileSystem.getNormalizedImagesLargeDir());
            Assert.assertEquals(as.getOutputDir(),            AvatolCVFileSystem.getScoredDataDir());
            Assert.assertEquals(as.getSupplementalInputDir(), AvatolCVFileSystem.getManuallyProvidedScoringLabelsDir());
        }
        catch(AvatolCVException ace){
            Assert.fail(ace.getMessage());
        }
    }
   
}
