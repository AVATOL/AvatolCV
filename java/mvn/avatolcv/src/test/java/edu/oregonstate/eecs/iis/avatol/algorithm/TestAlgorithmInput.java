package edu.oregonstate.eecs.iis.avatol.algorithm;

import edu.oregonstate.eecs.iis.avatolcv.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.algorithm.AlgorithmInputOptional;
import edu.oregonstate.eecs.iis.avatolcv.algorithm.AlgorithmInputRequired;
import junit.framework.Assert;
import junit.framework.TestCase;

/*
 * inputRequired:testImagesMaskFile refsFilesWithSuffix _croppedMask ofType mask_SpecimenGreen_BackgroundBlue_ClutterRed
 */
public class TestAlgorithmInput extends TestCase {
    public void testAlgorithmInputRequired(){
        // fail on insufficient content
        try {
            AlgorithmInputRequired ad = new AlgorithmInputRequired("inputRequired:");
            Assert.fail("should have thrown exception");
        }
        catch(AvatolCVException ace){
            System.out.println(ace.getMessage());
            Assert.assertTrue(ace.getMessage().contains("inputRequired:"));
            Assert.assertTrue(true);
        }
        // fail on too few args
        try {
            AlgorithmInputRequired ad = new AlgorithmInputRequired("inputRequired:one");
            Assert.fail("should have thrown exception");
        }
        catch(AvatolCVException ace){
            Assert.assertTrue(true);
        }
        // fail on too few args
        try {
            AlgorithmInputRequired ad = new AlgorithmInputRequired("inputRequired:one two");
            Assert.fail("should have thrown exception");
        }
        catch(AvatolCVException ace){
            Assert.assertTrue(true);
        }
        // fail on too few args
        try {
            AlgorithmInputRequired ad = new AlgorithmInputRequired("inputRequired:one two three");
            Assert.fail("should have thrown exception");
        }
        catch(AvatolCVException ace){
            Assert.assertTrue(true);
        }
        // fail on too few args
        try {
            AlgorithmInputRequired ad = new AlgorithmInputRequired("inputRequired:one twop three four");
            Assert.fail("should have thrown exception");
        }
        catch(AvatolCVException ace){
            Assert.assertTrue(true);
        }
        // fail on typ0 arg2
        try {
            AlgorithmInputRequired ad = new AlgorithmInputRequired("inputRequired:key refsFilesWithSuffixx _suffix ofType typeX");
            Assert.fail("should have thrown exception");
        }
        catch(AvatolCVException ace){
            Assert.assertTrue(true);
        }
        // fail on typo arg 4
        try {
            AlgorithmInputRequired ad = new AlgorithmInputRequired("inputRequired:key refsFilesWithSuffix _suffix oofType typeX");
            Assert.fail("should have thrown exception");
        }
        catch(AvatolCVException ace){
            Assert.assertTrue(true);
        }
        // should succeed
        try {
            AlgorithmInputRequired ad = new AlgorithmInputRequired("inputRequired:key refsFilesWithSuffix _suffix ofType typeX");
            Assert.assertEquals(ad.getKey(), "key");
            Assert.assertEquals(ad.hasSuffix(), true);
            Assert.assertEquals(ad.getSuffix(), "_suffix");
            Assert.assertEquals(ad.getType(), "typeX");
        }
        catch(AvatolCVException ace){
            Assert.fail(ace.getMessage());
        }
        // should succeed
        try {
            AlgorithmInputRequired ad = new AlgorithmInputRequired("inputRequired:key refsFilesWithSuffix * ofType typeX");
            Assert.assertEquals(ad.getKey(), "key");
            Assert.assertEquals(ad.hasSuffix(), false);
            Assert.assertEquals(ad.getType(), "typeX");
        }
        catch(AvatolCVException ace){
            Assert.fail(ace.getMessage());
        }
    }
    
    public void testAlgorithmInputOptional(){
        // fail on insufficient content
        try {
            AlgorithmInputOptional ad = new AlgorithmInputOptional("inputOptional:");
            Assert.fail("should have thrown exception");
        }
        catch(AvatolCVException ace){
            Assert.assertTrue(ace.getMessage().contains("inputOptional:"));
            Assert.assertTrue(true);
        }
        
    }
}
