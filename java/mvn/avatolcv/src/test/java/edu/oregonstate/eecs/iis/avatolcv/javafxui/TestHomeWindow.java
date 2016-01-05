package edu.oregonstate.eecs.iis.avatolcv.javafxui;

import edu.oregonstate.eecs.iis.avatolcv.AvatolCVException;
import junit.framework.Assert;
import junit.framework.TestCase;

public class TestHomeWindow extends TestCase {
    private static final String FS = System.getProperty("file.separator");
    public void testFindRoot(){
        
        try {
            Assert.assertEquals(AvatolCVJavaFX.findRoot("C:" + FS + "jed" + FS + "avatol" + FS + "git" + FS + "avatol_cv" + FS + "java" + FS + "mvn"), "C:" + FS + "jed" + FS + "avatol" + FS + "git" + FS + "avatol_cv");
            Assert.assertEquals(AvatolCVJavaFX.findRoot("C:" + FS + "jed" + FS + "avatol" + FS + "git" + FS + "avatol_cv" + FS + "java"), "C:" + FS + "jed" + FS + "avatol" + FS + "git" + FS + "avatol_cv");
            Assert.assertEquals(AvatolCVJavaFX.findRoot("C:" + FS + "jed" + FS + "avatol" + FS + "git" + FS + "avatol_cv"), "C:" + FS + "jed" + FS + "avatol" + FS + "git" + FS + "avatol_cv");
            Assert.assertEquals(AvatolCVJavaFX.findRoot("c:" + FS + "avatol_cv"), "c:" + FS + "avatol_cv");
        }
        catch(AvatolCVException ace){
            Assert.fail(ace.getMessage());
        }
        
        // these should fail
        try {
            AvatolCVJavaFX.findRoot("c:");
        }
        catch(AvatolCVException ace){
            Assert.assertTrue(true);
        }
        try {
            AvatolCVJavaFX.findRoot("FS");
        }
        catch(AvatolCVException ace){
            Assert.assertTrue(true);
        }
    }
}
