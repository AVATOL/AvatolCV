package edu.oregonstate.eecs.iis.avatol.algorithm;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import edu.oregonstate.eecs.iis.avatolcv.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.AvatolCVFileSystem;
import edu.oregonstate.eecs.iis.avatolcv.algorithm.Algorithm;
import edu.oregonstate.eecs.iis.avatolcv.algorithm.AlgorithmModules;
import edu.oregonstate.eecs.iis.avatolcv.algorithm.OrientationAlgorithm;
import edu.oregonstate.eecs.iis.avatolcv.algorithm.ScoringAlgorithm;
import edu.oregonstate.eecs.iis.avatolcv.algorithm.SegmentationAlgorithm;
import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class TestAlgorithm extends TestCase {
    private static final String FILESEP = System.getProperty("file.separator");
    
    protected void setUp(){
        try {
            AvatolCVFileSystem.setRootDir(TestAlgorithm.getValidRoot());
        }
        catch(AvatolCVException ace){
            Assert.fail("could not find valid avatol_cv root for test");
        }
    }
   
    public void testAlgorithmModules(){
        try {
            String root = getValidRoot();
            String modRoot = root + FILESEP + "modules";
            AlgorithmModules am = new AlgorithmModules(modRoot);
            String description = am.getAlgDescription("basicSegmenter", AlgorithmModules.AlgType.SEGMENTATION);
            Assert.assertTrue(description != null);
            Assert.assertTrue(!description.equals(""));
            
        }
        catch(AvatolCVException ace){
            ace.printStackTrace();
            Assert.fail(ace.getMessage());
        }
    }
    
    public void testAllAlgorithms(){
        try {
            String root = getValidRoot();
            String segRoot = root + FILESEP + "modules" + FILESEP + "segmentation";
            String orientRoot = root + FILESEP + "modules" + FILESEP + "orientation";
            String scoringRoot = root + FILESEP + "modules" + FILESEP + "scoring";
            
            // SEGMENTATION
            String yaoSegMac = segRoot + FILESEP + "yaoSeg" + FILESEP + "algPropertiesMac.txt";
            List<String> lines = loadAlg(yaoSegMac);
            SegmentationAlgorithm segAlg = new SegmentationAlgorithm(lines, yaoSegMac);
            Assert.assertEquals("basicSegmenter", segAlg.getAlgName());
            Assert.assertEquals("segmentation",segAlg.getAlgType());
            Assert.assertEquals("segmentationRunner.sh",segAlg.getLaunchFile());
            Assert.assertTrue(segAlg.getAlgDescription() != null);
            Assert.assertFalse(segAlg.getAlgDescription().equals(""));
            
            //ORIENTATION
            String yaoOrientMac = orientRoot + FILESEP + "yaoOrient" + FILESEP + "algPropertiesMac.txt";
            lines = loadAlg(yaoOrientMac);
            OrientationAlgorithm orientAlg = new OrientationAlgorithm(lines, yaoSegMac);
            Assert.assertEquals("basicOrientation",orientAlg.getAlgName());
            Assert.assertEquals("orientation",orientAlg.getAlgType());
            Assert.assertEquals( "orientationRunner.sh",orientAlg.getLaunchFile());
            Assert.assertTrue(orientAlg.getAlgDescription() != null);
            Assert.assertFalse(orientAlg.getAlgDescription().equals(""));
            
            //SCORING
            String yaoScoringMac = scoringRoot + FILESEP + "leafScore" + FILESEP + "algPropertiesMac.txt";
            lines = loadAlg(yaoScoringMac);
            ScoringAlgorithm scoringAlg = new ScoringAlgorithm(lines, yaoScoringMac);
            Assert.assertEquals("ShapeTextureScoring",scoringAlg.getAlgName());
            Assert.assertEquals("scoring",scoringAlg.getAlgType());
            Assert.assertEquals( "leafScore.sh",scoringAlg.getLaunchFile());
            Assert.assertEquals(scoringAlg.getScoringFocus(), ScoringAlgorithm.ScoringSessionFocus.SPECIMEN_SHAPE_ASPECT);   
            Assert.assertEquals(scoringAlg.getScoringScope(), ScoringAlgorithm.ScoringScope.SINGLE_ITEM);
            Assert.assertTrue(scoringAlg.getAlgDescription() != null);
            Assert.assertFalse(scoringAlg.getAlgDescription().equals(""));
            
            String batskullScoringWindows = scoringRoot + FILESEP + "batskullDPM" + FILESEP + "algPropertiesWindows.txt";
            lines = loadAlg(batskullScoringWindows);
            ScoringAlgorithm scoringAlgBat = new ScoringAlgorithm(lines, batskullScoringWindows);
            Assert.assertEquals("partsScoring",scoringAlgBat.getAlgName());
            Assert.assertEquals("scoring",scoringAlgBat.getAlgType());
            Assert.assertEquals("batSkullScore.bat",scoringAlgBat.getLaunchFile());
            Assert.assertEquals(scoringAlgBat.getScoringFocus(), ScoringAlgorithm.ScoringSessionFocus.SPECIMEN_PART_PRESENCE_ABSENCE);   
            Assert.assertEquals(scoringAlgBat.getScoringScope(), ScoringAlgorithm.ScoringScope.MULTIPLE_ITEM);    
            Assert.assertTrue(scoringAlgBat.getAlgDescription() != null);
            Assert.assertFalse(scoringAlgBat.getAlgDescription().equals(""));
                  
        }
        catch(AvatolCVException ace){
            ace.printStackTrace();
            Assert.fail(ace.getMessage());
        }
    }
    public static List<String> loadAlg(String path){
        List<String> lines = null;
        try {
            lines = Files.readAllLines(Paths.get(path), Charset.defaultCharset());
        }
        catch(IOException ioe){
            Assert.fail(ioe.getMessage() + " for path " + path);
        }
        return lines;
    }
  
    public static String getValidRoot() throws AvatolCVException {
        String jedDesktopRoot = "C:\\jed\\avatol\\git\\avatol_cv";
        File f = new File(jedDesktopRoot);
        if (f.exists()){
            return jedDesktopRoot;
        }
        String jedLaptopRoot = "C:\\avatol\\git\\avatol_cv";
        f = new File(jedLaptopRoot);
        if (f.exists()){
            return jedLaptopRoot;
        }
        String jedMacRoot = "/Users/jedirvine/av/avatol_cv";
        f = new File(jedMacRoot);
        if (f.exists()){
            return jedMacRoot;
        }
        throw new AvatolCVException("no valid algorithm root found");
    }
}
