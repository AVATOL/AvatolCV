package edu.oregonstate.eecs.iis.avatol.algorithm;

import java.util.List;

import edu.oregonstate.eecs.iis.avatolcv.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.AvatolCVFileSystem;
import edu.oregonstate.eecs.iis.avatolcv.algorithm.RunConfigFile;
import edu.oregonstate.eecs.iis.avatolcv.algorithm.SegmentationAlgorithm;
import edu.oregonstate.eecs.iis.avatolcv.core.DatasetInfo;
import junit.framework.Assert;
import junit.framework.TestCase;

public class TestRunConfigFile extends TestCase {
    private static final String FILESEP = System.getProperty("file.separator");
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
    /*
     * launchWith=launchTest.bat
algName=launchTest
algType=segmentation
description=This is a dummy algorithm used to test AvatolCV's module system.

inputRequired:testImagesFile refsFilesWithSuffix * ofType rawImage

inputOptional:userProvidedGroundTruthImagesFile refsFilesWithSuffix _in ofType inputTestType 

outputGenerated:ofType outputTestType withSuffix _out

dependency:testDependency=<modules>\3rdParty\foo\bar\baz

     */
    public void testRunConfigFile(){
        try {
            
            String root = TestAlgorithm.getValidRoot();
            String segRoot = root + FILESEP + "modules" + FILESEP + "segmentation";
            String launchTestPath = segRoot + FILESEP + "launchTest" + FILESEP + "algPropertiesWindows.txt";
            List<String> lines = TestAlgorithm.loadAlg(launchTestPath);
            SegmentationAlgorithm segAlg = new SegmentationAlgorithm(lines, launchTestPath);
            
            // dependency:testDependency=<modules>/3rdParty/foo/bar/baz
            String entry = RunConfigFile.generateEntryForDependency(segAlg.getDependencies().get(0));
            String modulesRoot = AvatolCVFileSystem.getModulesDir();
            String depPath = modulesRoot + FILESEP + "3rdParty\\foo\\bar\\baz";
            Assert.assertEquals(entry,"testDependency=" + depPath);
            
            entries = RunConfigFile.generateLinesForRequiredInputs(segAlg);
            Assert.assertEquals(entries.get(0),"testImagesFile=" + AvatolCVFileSystem.getSegmentationInputDir() + FILESEP + "testImagesFile.txt");
            
        }
        catch(AvatolCVException ace){
            ace.printStackTrace();
            Assert.fail(ace.getMessage());
        }
    }
}
