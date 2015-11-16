package edu.oregonstate.eecs.iis.avatol.algorithm;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import edu.oregonstate.eecs.iis.avatolcv.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.AvatolCVFileSystem;
import edu.oregonstate.eecs.iis.avatolcv.algorithm.Algorithm;
import edu.oregonstate.eecs.iis.avatolcv.algorithm.AlgorithmInput;
import edu.oregonstate.eecs.iis.avatolcv.algorithm.AlgorithmInputRequired;
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
  /*  public void testAAA(){
        Hashtable<Integer, List<String>> hash = new Hashtable<Integer, List<String>>();
        List<String> list = new ArrayList<String>();
        list.add("a");
        hash.put(new Integer"a", list);
        modHash(hash);
        int size = list.size();
        List<String> pulledList = hash.get("a");
        int size2 = pulledList.size();
        int bar = 3;
    }*/
    private void modHash(Hashtable<String, List<String>> hash){
        int size = hash.get("a").size();
        hash.get("a").add("2");
        int size2 = hash.get("a").size();
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
            
            entry = RunConfigFile.generateEntryForRequiredInput(segAlg.getRequiredInputs().get(0), Algorithm.PROPERTY_ALG_TYPE_VALUE_SEGMENTATION);
            Assert.assertEquals(entry,"testImagesFile=" + AvatolCVFileSystem.getSegmentationInputDir() + FILESEP + "testImagesFile.txt");
            
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
            
            RunConfigFile.suffixFileSort(inputs,  pathListHash, allPathsFromDir);
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
            
            RunConfigFile.suffixFileSort(inputs,  pathListHash, allPathsFromDir);
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
            
            RunConfigFile.suffixFileSort(inputs,  pathListHash, allPathsFromDir);
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
            
            RunConfigFile.suffixFileSort(inputs,  pathListHash, allPathsFromDir);
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
            
            RunConfigFile.suffixFileSort(inputs,  pathListHash, allPathsFromDir);
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
            
            RunConfigFile.suffixFileSort(inputs,  pathListHash, allPathsFromDir);
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
            
            RunConfigFile.suffixFileSort(inputs,  pathListHash, allPathsFromDir);
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
            
            RunConfigFile.suffixFileSort(inputs,  pathListHash, allPathsFromDir);
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
            
            RunConfigFile.suffixFileSort(inputs,  pathListHash, allPathsFromDir);
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
            
            RunConfigFile.suffixFileSort(inputs,  pathListHash, allPathsFromDir);
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
}





























