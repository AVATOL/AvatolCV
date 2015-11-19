package edu.oregonstate.eecs.iis.avatol.algorithm;

import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import edu.oregonstate.eecs.iis.avatolcv.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.AvatolCVFileSystem;
import edu.oregonstate.eecs.iis.avatolcv.algorithm.Algorithm;
import edu.oregonstate.eecs.iis.avatolcv.algorithm.AlgorithmInput;
import edu.oregonstate.eecs.iis.avatolcv.algorithm.AlgorithmInputRequired;
import edu.oregonstate.eecs.iis.avatolcv.algorithm.AlgorithmSequence;
import edu.oregonstate.eecs.iis.avatolcv.algorithm.OrientationAlgorithm;
import edu.oregonstate.eecs.iis.avatolcv.algorithm.RunConfigFile;
import edu.oregonstate.eecs.iis.avatolcv.algorithm.ScoringAlgorithm;
import edu.oregonstate.eecs.iis.avatolcv.algorithm.SegmentationAlgorithm;
import edu.oregonstate.eecs.iis.avatolcv.algorithm.AlgorithmModules.AlgType;
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


    private void modHash(Hashtable<String, List<String>> hash){
        int size = hash.get("a").size();
        hash.get("a").add("2");
        int size2 = hash.get("a").size();
    }
    /*  ALG PROPERTIES
     * launchWith=launchTest.bat
algName=launchTest
algType=segmentation
description=This is a dummy algorithm used to test AvatolCV's module system.

inputRequired:testImagesFile refsFilesWithSuffix * ofType rawImage

inputOptional:userProvidedGroundTruthImagesFile refsFilesWithSuffix _in ofType inputTestType 

outputGenerated:ofType outputTestType withSuffix _out

dependency:testDependency=<modules>\3rdParty\foo\bar\baz

     */
    
    
    /*  RUN CONFIG
     *  segmentationOutputDir=<path of dir where output goes>
        avatolCVStatusFile=<path to file to write status to> (avatolCV will poll that file)
        
    AvatolCV generates this line and the associated files due to inputRequired line in algProperties file:
        testImagesFile=<someAbsolutePath>/testImagesFile.txt
        
    AvatolCV generates this line and the associated files entries(if any) due to inputOptional line in algProperties file:
        userProvidedGroundTruthImagesFile=<someAbsolutePath>/userProvidedGroundTruthImagesFile.txt
        userProvidedTrainImagesFile=<someAbsolutePath>/userProvidedTrainImagesFile.txt   
     */
    
    public void testRunConfigFileSegmentation(){
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
            
            // required input
            AlgorithmSequence algSequence = new AlgorithmSequence();
            algSequence.enableSegmentation();
            entry = RunConfigFile.generateEntryForRequiredInput(segAlg.getRequiredInputs().get(0), algSequence);
            Assert.assertEquals(entry,"testImagesFile=" + AvatolCVFileSystem.getSessionDir() + FILESEP + "testImagesFile_segmentation.txt");
            
            // 
            String testImagesFilePath = AvatolCVFileSystem.getSessionDir() + FILESEP + "testImagesFile_segmentation.txt";
            File tif = new File(testImagesFilePath);
            tif.delete();
            String path = AvatolCVFileSystem.getSessionDir() + FILESEP + "runConfig_" + "segmentation" + ".txt";
            File f = new File(path);
            f.delete();
            RunConfigFile runConfigFile = new RunConfigFile(segAlg, algSequence);
            Assert.assertTrue(f.exists());
            Assert.assertTrue(tif.exists());
        }
        catch(AvatolCVException ace){
            ace.printStackTrace();
            Assert.fail(ace.getMessage());
        }
    }
    public void testRunConfigFileOrientation(){
        try {
            
            String root = TestAlgorithm.getValidRoot();
            String orientRoot = root + FILESEP + "modules" + FILESEP + "orientation";
            String launchTestPath = orientRoot + FILESEP + "launchTest" + FILESEP + "algPropertiesWindows.txt";
            List<String> lines = TestAlgorithm.loadAlg(launchTestPath);
            OrientationAlgorithm orientAlg = new OrientationAlgorithm(lines, launchTestPath);
            
            AlgorithmSequence algSequence = new AlgorithmSequence();
            algSequence.enableSegmentation();
            algSequence.enableOrientation();
            //inputRequired:testImagesFile refsFilesWithSuffix _seg ofType segmentedImage
            String entry = RunConfigFile.generateEntryForRequiredInput(orientAlg.getRequiredInputs().get(0), algSequence);
            Assert.assertEquals(entry,"testImagesFile=" + AvatolCVFileSystem.getSessionDir() + FILESEP + "testImagesFile_orientation.txt");
        }
        catch(AvatolCVException ace){
            ace.printStackTrace();
            Assert.fail(ace.getMessage());
        }
    }
    public void testRunConfigFileScoring(){
        try {
            
            String root = TestAlgorithm.getValidRoot();
            String scoringRoot = root + FILESEP + "modules" + FILESEP + "scoring";
            String launchTestPath = scoringRoot + FILESEP + "launchTest" + FILESEP + "algPropertiesWindows.txt";
            List<String> lines = TestAlgorithm.loadAlg(launchTestPath);
            ScoringAlgorithm scoringAlg = new ScoringAlgorithm(lines, launchTestPath);
            
            AlgorithmSequence algSequence = new AlgorithmSequence();
            algSequence.enableSegmentation();
            algSequence.enableOrientation();
            algSequence.enableScoring();
            //inputRequired:testImagesFile refsFilesWithSuffix _seg ofType segmentedImage
            String entry = RunConfigFile.generateEntryForRequiredInput(scoringAlg.getRequiredInputs().get(0), algSequence);
            Assert.assertEquals(entry,"testImagesFile=" + AvatolCVFileSystem.getSessionDir() + FILESEP + "testImagesFile_scoring.txt");
        }
        catch(AvatolCVException ace){
            ace.printStackTrace();
            Assert.fail(ace.getMessage());
        }
    }
    public void testVerifyUniqueSuffixesFail(){
        List<AlgorithmInput> inputs = new ArrayList<AlgorithmInput>();
        try {
            AlgorithmInputRequired air1 = new AlgorithmInputRequired("key1 refsFilesWithSuffix _suffix1 ofType foo");
            AlgorithmInputRequired air2 = new AlgorithmInputRequired("key2 refsFilesWithSuffix _suffix1 ofType bar");
            inputs.add(air1);
            inputs.add(air2);
            RunConfigFile.verifyUniqueSuffixes(inputs);
            Assert.fail("should have thrown exception on duplicate suffixes");
        }
        catch(AvatolCVException ace){
            Assert.assertTrue(ace.getMessage().startsWith("two input types have the same suffix."));
        }
    }
    public void testVerifyUniqueSuffixesPass(){
        List<AlgorithmInput> inputs = new ArrayList<AlgorithmInput>();
        try {
            AlgorithmInputRequired air1 = new AlgorithmInputRequired("key1 refsFilesWithSuffix _suffix1 ofType foo");
            AlgorithmInputRequired air2 = new AlgorithmInputRequired("key2 refsFilesWithSuffix _suffix2 ofType bar");
            inputs.add(air1);
            inputs.add(air2);
            RunConfigFile.verifyUniqueSuffixes(inputs);
            Assert.assertTrue(true);
        }
        catch(AvatolCVException ace){
            Assert.fail(ace.getMessage());
        }
    }
    public void testSuffixSort_failNoInputFilesAtAll(){
        try {
            List<AlgorithmInput> inputs = new ArrayList<AlgorithmInput>();
            AlgorithmInputRequired air1 = new AlgorithmInputRequired("key1 refsFilesWithSuffix * ofType foo");
            inputs.add(air1);
            Hashtable<AlgorithmInput, List<String>> pathListHash = new Hashtable<AlgorithmInput, List<String>>();
            List<String> allPathsFromDir = new ArrayList<String>();
            
            
            RunConfigFile.suffixFileSort(inputs,  pathListHash, allPathsFromDir, "/foo");
            Assert.fail("should have thrown exception on lack of data case");
        }
        catch(AvatolCVException ace){
            Assert.assertTrue(ace.getMessage().startsWith("No input data present"));
        }
    }
    public void testSuffixSort_failConflictingNoSuffixEntries1(){
        try {
            List<AlgorithmInput> inputs = new ArrayList<AlgorithmInput>();
            AlgorithmInputRequired air1 = new AlgorithmInputRequired("key1 refsFilesWithSuffix * ofType foo");
            AlgorithmInputRequired air2 = new AlgorithmInputRequired("key2 refsFilesWithSuffix * ofType bar");
            inputs.add(air1);
            inputs.add(air2);
            Hashtable<AlgorithmInput, List<String>> pathListHash = new Hashtable<AlgorithmInput, List<String>>();
            List<String> allPathsFromDir = new ArrayList<String>();
            allPathsFromDir.add("foo/bar/a_suffix1.jpg");
            allPathsFromDir.add("foo/bar/b_suffix1.jpg");
            
            RunConfigFile.suffixFileSort(inputs,  pathListHash, allPathsFromDir, "foo/bar");
            Assert.fail("should have thrown exception on duplicate noSuffix case");
        }
        catch(AvatolCVException ace){
            Assert.assertTrue(ace.getMessage().startsWith("More than one input has no identifying suffix."));
        }
    }
    public void testSuffixSort_failConflictingNoSuffixEntries2(){
        try {
            List<AlgorithmInput> inputs = new ArrayList<AlgorithmInput>();
            AlgorithmInputRequired air1 = new AlgorithmInputRequired("key1 refsFilesWithSuffix * ofType foo");
            AlgorithmInputRequired air2 = new AlgorithmInputRequired("key2 refsFilesWithSuffix * ofType bar");
            AlgorithmInputRequired air3 = new AlgorithmInputRequired("key3 refsFilesWithSuffix _suffix1 ofType bar");
            inputs.add(air1);
            inputs.add(air2);
            inputs.add(air3);
            Hashtable<AlgorithmInput, List<String>> pathListHash = new Hashtable<AlgorithmInput, List<String>>();
            List<String> allPathsFromDir = new ArrayList<String>();
            allPathsFromDir.add("foo/bar/a_suffix1.jpg");
            allPathsFromDir.add("foo/bar/b_suffix1.jpg");
            
            RunConfigFile.suffixFileSort(inputs,  pathListHash, allPathsFromDir, "foo/bar");
            Assert.fail("should have thrown exception on duplicate noSuffix case");
        }
        catch(AvatolCVException ace){
            Assert.assertTrue(ace.getMessage().startsWith("More than one input has no identifying suffix."));
        }
    }
    public void testSuffixSort_failNoneMatchingSuffix(){
    	try {
            List<AlgorithmInput> inputs = new ArrayList<AlgorithmInput>();
            AlgorithmInputRequired air1 = new AlgorithmInputRequired("key1 refsFilesWithSuffix _suffix2 ofType foo");
            inputs.add(air1);
            Hashtable<AlgorithmInput, List<String>> pathListHash = new Hashtable<AlgorithmInput, List<String>>();
            List<String> allPathsFromDir = new ArrayList<String>();
            allPathsFromDir.add("foo/bar/a_suffix1.jpg");
            allPathsFromDir.add("foo/bar/b_suffix1.jpg");
            
            RunConfigFile.suffixFileSort(inputs,  pathListHash, allPathsFromDir, "foo/bar");
            Assert.fail("should have thrown exception on no match of desired suffix case");
        }
        catch(AvatolCVException ace){
            Assert.assertTrue(ace.getMessage().startsWith("No files with required suffix"));
        }
    }
    public void testSuffixSort_passNoLackingSuffixRemainingAfterThoseWithSuffixPickedOut(){
    	try {
            List<AlgorithmInput> inputs = new ArrayList<AlgorithmInput>();
            AlgorithmInputRequired air1 = new AlgorithmInputRequired("key1 refsFilesWithSuffix _suffix1 ofType foo");
            AlgorithmInputRequired air2 = new AlgorithmInputRequired("key2 refsFilesWithSuffix _suffix2 ofType foo");
            inputs.add(air1);
            Hashtable<AlgorithmInput, List<String>> pathListHash = new Hashtable<AlgorithmInput, List<String>>();
            List<String> allPathsFromDir = new ArrayList<String>();
            allPathsFromDir.add("foo/bar/a_suffix1.jpg");
            allPathsFromDir.add("foo/bar/b_suffix2.jpg");
            
            RunConfigFile.suffixFileSort(inputs,  pathListHash, allPathsFromDir, "foo/bar");
            Assert.assertTrue(true);
        }
        catch(AvatolCVException ace){
            Assert.fail(ace.getMessage());
        }
    }
    public void testSuffixSort_failNoLackingSuffixRemainingAfterThoseWithSuffixPickedOut(){
    	try {
            List<AlgorithmInput> inputs = new ArrayList<AlgorithmInput>();
            AlgorithmInputRequired air1 = new AlgorithmInputRequired("key1 refsFilesWithSuffix _suffix1 ofType foo");
            AlgorithmInputRequired air2 = new AlgorithmInputRequired("key2 refsFilesWithSuffix * ofType foo");
            inputs.add(air1);
            inputs.add(air2);
            Hashtable<AlgorithmInput, List<String>> pathListHash = new Hashtable<AlgorithmInput, List<String>>();
            List<String> allPathsFromDir = new ArrayList<String>();
            allPathsFromDir.add("foo/bar/a_suffix1.jpg");
            allPathsFromDir.add("foo/bar/b_suffix1.jpg");
            
            RunConfigFile.suffixFileSort(inputs,  pathListHash, allPathsFromDir, "foo/bar");
            Assert.fail("should have thrown exception on no remaining to match * declaration");
        }
        catch(AvatolCVException ace){
            Assert.assertTrue(ace.getMessage().startsWith("all the input files matched specified suffixes, leaving none to match the 'no suffix' * declaration"));
        }
    }
    public void testSuffixSort_noneWithNoSuffix_oneWithSuffix(){
      
        try {
            List<AlgorithmInput> inputs = new ArrayList<AlgorithmInput>();
            AlgorithmInputRequired air1 = new AlgorithmInputRequired("key1 refsFilesWithSuffix _x ofType foo");
            inputs.add(air1);
            
            Hashtable<AlgorithmInput, List<String>> pathListHash = new Hashtable<AlgorithmInput, List<String>>();

            List<String> allPathsFromDir = new ArrayList<String>();
            allPathsFromDir.add("foo/bar/a_x.jpg");
            allPathsFromDir.add("foo/bar/b_y.jpg"); // this should be ignored
            
            RunConfigFile.suffixFileSort(inputs,  pathListHash, allPathsFromDir, "foo/bar");
            List<String> list1 = pathListHash.get(air1);
            Assert.assertTrue(list1.get(0).equals("foo/bar/a_x.jpg"));
            Assert.assertTrue(list1.size() == 1);
        }
        catch(AvatolCVException ace){
            Assert.fail(ace.getMessage());
        }
    }
    public void testSuffixSort_oneWithNoSuffix_noneWithSuffix(){
       
        try {
            List<AlgorithmInput> inputs = new ArrayList<AlgorithmInput>();
            AlgorithmInputRequired air1 = new AlgorithmInputRequired("key1 refsFilesWithSuffix * ofType foo");
            inputs.add(air1);
            
            Hashtable<AlgorithmInput, List<String>> pathListHash = new Hashtable<AlgorithmInput, List<String>>();
            
            
            List<String> allPathsFromDir = new ArrayList<String>();
            allPathsFromDir.add("foo/bar/a.jpg");
            allPathsFromDir.add("foo/bar/b_suffixWhatever.jpg");
            
            RunConfigFile.suffixFileSort(inputs,  pathListHash, allPathsFromDir, "foo/bar");
            List<String> list1 = pathListHash.get(air1);
            Assert.assertTrue(list1.get(0).equals("foo/bar/a.jpg"));
            Assert.assertTrue(list1.get(1).equals("foo/bar/b_suffixWhatever.jpg"));  // all will hit if only * specified
            Assert.assertTrue(list1.size() == 2);
        }
        catch(AvatolCVException ace){
            Assert.fail(ace.getMessage());
        }
    }
    public void testSuffixSort_oneWithNoSuffix_oneWithSuffix(){
        
        try {
            List<AlgorithmInput> inputs = new ArrayList<AlgorithmInput>();
            AlgorithmInputRequired air1 = new AlgorithmInputRequired("key1 refsFilesWithSuffix * ofType foo");
            AlgorithmInputRequired air2 = new AlgorithmInputRequired("key2 refsFilesWithSuffix _x ofType bar");
            inputs.add(air1);
            inputs.add(air2);
            
            Hashtable<AlgorithmInput, List<String>> pathListHash = new Hashtable<AlgorithmInput, List<String>>();
           
            
            List<String> allPathsFromDir = new ArrayList<String>();
            allPathsFromDir.add("foo/bar/a_x.jpg");
            allPathsFromDir.add("foo/bar/b.jpg");
            
            RunConfigFile.suffixFileSort(inputs,  pathListHash, allPathsFromDir, "foo/bar");
            List<String> list1 = pathListHash.get(air1);
            List<String> list2 = pathListHash.get(air2);
            Assert.assertTrue(list1.get(0).equals("foo/bar/b.jpg"));
            Assert.assertTrue(list1.size() == 1);
            Assert.assertTrue(list2.get(0).equals("foo/bar/a_x.jpg"));
            Assert.assertTrue(list2.size() == 1);
        }
        catch(AvatolCVException ace){
            Assert.fail(ace.getMessage());
        }
    }
    public void testSuffixSort_oneWithNoSuffix_twoWithSuffix(){
    	try {
            List<AlgorithmInput> inputs = new ArrayList<AlgorithmInput>();
            AlgorithmInputRequired air1 = new AlgorithmInputRequired("key1 refsFilesWithSuffix * ofType foo");
            AlgorithmInputRequired air2 = new AlgorithmInputRequired("key2 refsFilesWithSuffix _x ofType bar");
            AlgorithmInputRequired air3 = new AlgorithmInputRequired("key3 refsFilesWithSuffix _y ofType bar");
            inputs.add(air1);
            inputs.add(air2);
            inputs.add(air3);
            
            Hashtable<AlgorithmInput, List<String>> pathListHash = new Hashtable<AlgorithmInput, List<String>>();
           
            
            List<String> allPathsFromDir = new ArrayList<String>();
            allPathsFromDir.add("foo/bar/a_x.jpg");
            allPathsFromDir.add("foo/bar/b_x.jpg");
            allPathsFromDir.add("foo/bar/a_y.jpg");
            allPathsFromDir.add("foo/bar/b_y.jpg");
            allPathsFromDir.add("foo/bar/c_y.jpg");
            allPathsFromDir.add("foo/bar/b.jpg");
            
            RunConfigFile.suffixFileSort(inputs,  pathListHash, allPathsFromDir, "foo/bar");
            List<String> list1 = pathListHash.get(air1);
            List<String> list2 = pathListHash.get(air2);
            List<String> list3 = pathListHash.get(air3);
            Assert.assertTrue(list1.get(0).equals("foo/bar/b.jpg"));
            Assert.assertTrue(list1.size() == 1);
            Assert.assertTrue(list2.get(0).equals("foo/bar/a_x.jpg"));
            Assert.assertTrue(list2.get(1).equals("foo/bar/b_x.jpg"));
            Assert.assertTrue(list2.size() == 2);
            Assert.assertTrue(list3.get(0).equals("foo/bar/a_y.jpg"));
            Assert.assertTrue(list3.get(1).equals("foo/bar/b_y.jpg"));
            Assert.assertTrue(list3.get(2).equals("foo/bar/c_y.jpg"));
            Assert.assertTrue(list3.size() == 3);
        }
        catch(AvatolCVException ace){
            Assert.fail(ace.getMessage());
        }
       
    }

    public void testSuffixSort_oneWithNoSuffix_threeWithSuffix(){
    	try {
            List<AlgorithmInput> inputs = new ArrayList<AlgorithmInput>();
            AlgorithmInputRequired air1 = new AlgorithmInputRequired("key1 refsFilesWithSuffix * ofType foo");
            AlgorithmInputRequired air2 = new AlgorithmInputRequired("key2 refsFilesWithSuffix _x ofType bar");
            AlgorithmInputRequired air3 = new AlgorithmInputRequired("key3 refsFilesWithSuffix _y ofType bar");
            AlgorithmInputRequired air4 = new AlgorithmInputRequired("key4 refsFilesWithSuffix _z ofType bar");
            inputs.add(air1);
            inputs.add(air2);
            inputs.add(air3);
            inputs.add(air4);
            
            Hashtable<AlgorithmInput, List<String>> pathListHash = new Hashtable<AlgorithmInput, List<String>>();
           
            
            List<String> allPathsFromDir = new ArrayList<String>();
            allPathsFromDir.add("foo/bar/a_x.jpg");
            allPathsFromDir.add("foo/bar/b_x.jpg");
            allPathsFromDir.add("foo/bar/a_y.jpg");
            allPathsFromDir.add("foo/bar/b_y.jpg");
            allPathsFromDir.add("foo/bar/c_z.jpg");
            allPathsFromDir.add("foo/bar/b.jpg");
            
            RunConfigFile.suffixFileSort(inputs,  pathListHash, allPathsFromDir, "foo/bar");
            List<String> list1 = pathListHash.get(air1);
            List<String> list2 = pathListHash.get(air2);
            List<String> list3 = pathListHash.get(air3);
            List<String> list4 = pathListHash.get(air4);
            Assert.assertTrue(list1.get(0).equals("foo/bar/b.jpg"));
            Assert.assertTrue(list1.size() == 1);
            Assert.assertTrue(list2.get(0).equals("foo/bar/a_x.jpg"));
            Assert.assertTrue(list2.get(1).equals("foo/bar/b_x.jpg"));
            Assert.assertTrue(list2.size() == 2);
            Assert.assertTrue(list3.get(0).equals("foo/bar/a_y.jpg"));
            Assert.assertTrue(list3.get(1).equals("foo/bar/b_y.jpg"));
            Assert.assertTrue(list3.size() == 2);
            Assert.assertTrue(list4.get(0).equals("foo/bar/c_z.jpg"));
            Assert.assertTrue(list4.size() == 1);
        }
        catch(AvatolCVException ace){
            Assert.fail(ace.getMessage());
        }
       
    }
    public void testPathHasSuffix(){
        Assert.assertTrue(RunConfigFile.pathHasSuffix("/foo/bar/x_suffix.jpg", "_suffix"));
        Assert.assertTrue(RunConfigFile.pathHasSuffix("/foo/bar/x_suffix.jpg", "suffix"));
        Assert.assertTrue(RunConfigFile.pathHasSuffix("/foo/bar/x_suffix.jpg", "fix"));
        Assert.assertFalse(RunConfigFile.pathHasSuffix("/foo/bar/x_x.jpg", "_y"));
        Assert.assertFalse(RunConfigFile.pathHasSuffix("/foo/bar/x.jpg", "_y"));
    }
    
    public void testGenRunConfigYaoOrient(){
        try {
            
            String root = TestAlgorithm.getValidRoot();
            String orientRoot = root + FILESEP + "modules" + FILESEP + "orientation";
            String launchTestPath = orientRoot + FILESEP + "yaoOrient" + FILESEP + "algPropertiesMac.txt";
            List<String> lines = TestAlgorithm.loadAlg(launchTestPath);
            OrientationAlgorithm orientAlg = new OrientationAlgorithm(lines, launchTestPath);
            
          
            AlgorithmSequence algSequence = new AlgorithmSequence();
            algSequence.enableSegmentation();
            algSequence.enableOrientation();
            
            RunConfigFile runConfigFile = new RunConfigFile(orientAlg, algSequence);
           
        }
        catch(AvatolCVException ace){
            ace.printStackTrace();
            Assert.fail(ace.getMessage());
        }
    }
    public void testGenRunConfigLeafScore(){
        try {
            
            String root = TestAlgorithm.getValidRoot();
            String scoringRoot = root + FILESEP + "modules" + FILESEP + "scoring";
            String launchTestPath = scoringRoot + FILESEP + "leafScore" + FILESEP + "algPropertiesMac.txt";
            List<String> lines = TestAlgorithm.loadAlg(launchTestPath);
            OrientationAlgorithm orientAlg = new OrientationAlgorithm(lines, launchTestPath);
            
          
            AlgorithmSequence algSequence = new AlgorithmSequence();
            algSequence.enableSegmentation();
            algSequence.enableOrientation();
            
            RunConfigFile runConfigFile = new RunConfigFile(orientAlg, algSequence);
           
        }
        catch(AvatolCVException ace){
            ace.printStackTrace();
            Assert.fail(ace.getMessage());
        }
    }
}





























