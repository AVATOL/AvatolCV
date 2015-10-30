package edu.oregonstate.eecs.iis.avatol.algorithm;

import edu.oregonstate.eecs.iis.avatolcv.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.Platform;
import edu.oregonstate.eecs.iis.avatolcv.algorithm.AlgorithmLauncher;
import junit.framework.Assert;
import junit.framework.TestCase;

public class TestAlgorithmLauncher extends TestCase {
    public void testFindQuestionById(){
        
        if (Platform.isWindows()){
            try {
                Assert.assertEquals(AlgorithmLauncher.getModulesRootFromAlgPropsPath("C:\\foo\\modules\\segmentation\\x\\algProps.txt"),"C:\\foo\\modules");
            }
            catch(AvatolCVException e){
                Assert.fail(e.getMessage());
            }
            try {
                AlgorithmLauncher.getModulesRootFromAlgPropsPath("C:\\foo\\bar\\segmentation\\x\\algProps.txt");
                Assert.fail("should have thrown exception for bad path");
            }
            catch(AvatolCVException e){
                
            }
        }
        else {
            try {
                Assert.assertEquals(AlgorithmLauncher.getModulesRootFromAlgPropsPath("/foo/modules/segmentation/x/algProps.txt"),"/foo/modules");
            }
            catch(AvatolCVException e){
                Assert.fail(e.getMessage());
            }
            try {
                AlgorithmLauncher.getModulesRootFromAlgPropsPath("/foo/bar/segmentation/x/algProps.txt");
                Assert.fail("should have thrown exception for bad path");
            }
            catch(AvatolCVException e){
                
            }
        }
    }
}
