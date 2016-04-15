package edu.oregonstate.eecs.iis.avatolcv.javafxui;

import edu.oregonstate.eecs.iis.avatolcv.AvatolCVException;
import junit.framework.Assert;
import junit.framework.TestCase;

public class TestHomeWindow extends TestCase {
    private static final String FS = System.getProperty("file.separator");
    public void testFindRoot(){
        
        try {
            Assert.assertEquals(AvatolCVJavaFX.findAvatolCVRoot("C:" + FS + "jed" + FS + "avatol" + FS + "git" + FS + "avatol_cv" + FS + "java" + FS + "mvn"), "C:" + FS + "jed" + FS + "avatol" + FS + "git" + FS + "avatol_cv");
            Assert.assertEquals(AvatolCVJavaFX.findAvatolCVRoot("C:" + FS + "jed" + FS + "avatol" + FS + "git" + FS + "avatol_cv" + FS + "java"), "C:" + FS + "jed" + FS + "avatol" + FS + "git" + FS + "avatol_cv");
            Assert.assertEquals(AvatolCVJavaFX.findAvatolCVRoot("C:" + FS + "jed" + FS + "avatol" + FS + "git" + FS + "avatol_cv"), "C:" + FS + "jed" + FS + "avatol" + FS + "git" + FS + "avatol_cv");
            Assert.assertEquals(AvatolCVJavaFX.findAvatolCVRoot("c:" + FS + "avatol_cv"), "c:" + FS + "avatol_cv");
        }
        catch(AvatolCVException ace){
            Assert.fail(ace.getMessage());
        }
        
        // these should fail
        try {
            AvatolCVJavaFX.findAvatolCVRoot("c:");
        }
        catch(AvatolCVException ace){
            Assert.assertTrue(true);
        }
        try {
            AvatolCVJavaFX.findAvatolCVRoot("FS");
        }
        catch(AvatolCVException ace){
            Assert.assertTrue(true);
        }
    }
}
