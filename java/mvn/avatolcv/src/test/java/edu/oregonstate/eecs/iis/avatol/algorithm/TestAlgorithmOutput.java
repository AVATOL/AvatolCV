package edu.oregonstate.eecs.iis.avatol.algorithm;

import edu.oregonstate.eecs.iis.avatolcv.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.algorithm.AlgorithmInputRequired;
import edu.oregonstate.eecs.iis.avatolcv.algorithm.AlgorithmOutput;
import junit.framework.Assert;
import junit.framework.TestCase;

public class TestAlgorithmOutput extends TestCase {
	 public void testAlgorithmInputRequired(){
	        // fail on insufficient content
	        try {
	            AlgorithmOutput ao = new AlgorithmOutput("outputGenerated:");
	            Assert.fail("should have thrown exception");
	        }
	        catch(AvatolCVException ace){
	            System.out.println(ace.getMessage());
	            Assert.assertTrue(ace.getMessage().contains("outputGenerated:"));
	            Assert.assertTrue(true);
	        }
	        // fail on too few args
	        try {
	        	AlgorithmOutput ao = new AlgorithmOutput("outputGenerated:one");
	            Assert.fail("should have thrown exception");
	        }
	        catch(AvatolCVException ace){
	            Assert.assertTrue(true);
	        }
	        // fail on too few args
	        try {
	        	AlgorithmOutput a0 = new AlgorithmOutput("outputGenerated:one two");
	            Assert.fail("should have thrown exception");
	        }
	        catch(AvatolCVException ace){
	            Assert.assertTrue(true);
	        }
	        // fail on too few args
	        try {
	        	AlgorithmOutput ao = new AlgorithmOutput("outputGenerated:one two three");
	            Assert.fail("should have thrown exception");
	        }
	        catch(AvatolCVException ace){
	            Assert.assertTrue(true);
	        }
	       
	        // fail on typo arg1
	        try {
	        	AlgorithmOutput ao = new AlgorithmOutput("outputGenerated:ofTypee typeX withSuffix _suffix");
	            Assert.fail("should have thrown exception");
	        }
	        catch(AvatolCVException ace){
	            Assert.assertTrue(true);
	        }
	        // fail on typo arg 3
	        try {
	        	AlgorithmOutput ao = new AlgorithmOutput("outputGenerated:ofType typeX withSuffixx _suffix");
	            Assert.fail("should have thrown exception");
	        }
	        catch(AvatolCVException ace){
	            Assert.assertTrue(true);
	        }
	        // should succeed
	        try {
	        	AlgorithmOutput ao = new AlgorithmOutput("outputGenerated:ofType typeX withSuffix _suffix");
	            Assert.assertEquals(ao.hasSuffix(), true);
	            Assert.assertEquals(ao.getSuffix(), "_suffix");
	            Assert.assertEquals(ao.getType(), "typeX");
	        }
	        catch(AvatolCVException ace){
	            Assert.fail(ace.getMessage());
	        }
	        // should succeed
	        try {
	        	AlgorithmOutput ao = new AlgorithmOutput("outputGenerated:ofType typeX withSuffix *");
	            Assert.assertEquals(ao.hasSuffix(), false);
	            Assert.assertEquals(ao.getType(), "typeX");
	        }
	        catch(AvatolCVException ace){
	            Assert.fail(ace.getMessage());
	        }
	    }
	    
}
