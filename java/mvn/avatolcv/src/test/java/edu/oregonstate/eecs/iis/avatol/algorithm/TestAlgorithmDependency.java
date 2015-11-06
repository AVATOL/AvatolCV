package edu.oregonstate.eecs.iis.avatol.algorithm;

import edu.oregonstate.eecs.iis.avatolcv.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.AvatolCVFileSystem;
import edu.oregonstate.eecs.iis.avatolcv.algorithm.AlgorithmDependency;
import junit.framework.Assert;
import junit.framework.TestCase;

/*
 * 
dependency:pathLibSsvmMatlab=<modules>/3rdParty/libsvm/libsvm-318/matlab
dependency:pathVlfeat=<modules>/3rdParty/vlfeat/vlfeat-0.9.20/toolbox/v1_setup

 */
public class TestAlgorithmDependency extends TestCase {
    public static final String FILESEP = System.getProperty("file.separator");
    public void testAlgorithmDependency() {
        try {
            AvatolCVFileSystem.setRootDir(TestAlgorithm.getValidRoot());
        }
        catch(AvatolCVException ace){
            Assert.fail("could not find valid avatol_cv root for test");
        }
        
        // fail on insufficient content
        try {
            AlgorithmDependency ad = new AlgorithmDependency("dependency:");
            Assert.fail("should have thrown exception");
        }
        catch(AvatolCVException ace){
            Assert.assertTrue(true);
        }
        // fail on insufficient content
        try {
            AlgorithmDependency ad = new AlgorithmDependency("dependency:foo");
            Assert.fail("should have thrown exception");
        }
        catch(AvatolCVException ace){
            Assert.assertTrue(true);
        }

        // fail on no equals sign 
        try {
            AlgorithmDependency ad = new AlgorithmDependency("dependency:foo.noEqualsSign");
            Assert.fail("should have thrown exception");
        }
        catch(AvatolCVException ace){
            Assert.assertTrue(true);
        }

        // fail on lack of key
        try {
            AlgorithmDependency ad = new AlgorithmDependency("dependency:=");
            Assert.fail("should have thrown exception");
        }
        catch(AvatolCVException ace){
            Assert.assertTrue(true);
        }

        // fail on lack of value
        try {
            AlgorithmDependency ad = new AlgorithmDependency("dependency:key=");
            Assert.fail("should have thrown exception");
        }
        catch(AvatolCVException ace){
            Assert.assertTrue(true);
        }

        // valid path
        try {
            AlgorithmDependency ad = new AlgorithmDependency("dependency:key=path/a/b");
            Assert.assertEquals(ad.getKey(),"key");
            Assert.assertEquals(ad.getPath(),"path/a/b");
        }
        catch(AvatolCVException ace){
            Assert.fail("should have succeeded on valid path path/a/b");
        }
        // valid path with <modules> reference
        try {
            String avatolRoot = TestAlgorithm.getValidRoot();
            String moduleRoot = avatolRoot + FILESEP + "modules";
            AlgorithmDependency ad = new AlgorithmDependency("dependency:key=<modules>/a/b");
            Assert.assertEquals(ad.getKey(),"key");
            Assert.assertEquals(ad.getPath(),moduleRoot + "/a/b");
        }
        catch(AvatolCVException ace){
            Assert.fail("should have succeeded on valid path <modules>/a/b");
        }
        
    }


}
