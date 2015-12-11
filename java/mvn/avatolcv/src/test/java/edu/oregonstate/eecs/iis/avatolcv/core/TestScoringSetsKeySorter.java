package edu.oregonstate.eecs.iis.avatolcv.core;

import java.util.ArrayList;
import java.util.List;

import edu.oregonstate.eecs.iis.avatolcv.AvatolCVException;
import junit.framework.Assert;
import junit.framework.TestCase;

public class TestScoringSetsKeySorter extends TestCase {

	/**********
	 * ONE EVAL SET
	 **********/
	/*
	 *  taxon 1,2,3,4 each has 2 images , train 25 percent, no  images in both training and scoring
	 */
	public void testSingelSetSimple(){
	    try {
	        String path = "somePath";

	        List<String> lines1 = new ArrayList<String>(); lines1.add("key1=val1"); lines1.add("scoringConcern1=scVal"); NormalizedImageInfo nii1 = new NormalizedImageInfo(lines1, "image1", path);
            List<String> lines2 = new ArrayList<String>(); lines2.add("key1=val1"); lines2.add("scoringConcern1=scVal"); NormalizedImageInfo nii2 = new NormalizedImageInfo(lines2, "image2", path);
            
            List<String> lines3 = new ArrayList<String>(); lines3.add("key1=val2"); lines3.add("scoringConcern1=scVal"); NormalizedImageInfo nii3 = new NormalizedImageInfo(lines3, "image3", path);
            List<String> lines4 = new ArrayList<String>(); lines4.add("key1=val2"); lines4.add("scoringConcern1=scVal"); NormalizedImageInfo nii4 = new NormalizedImageInfo(lines4, "image4", path);
            
            List<String> lines5 = new ArrayList<String>(); lines5.add("key1=val3"); lines5.add("scoringConcern1=scVal"); NormalizedImageInfo nii5 = new NormalizedImageInfo(lines5, "image5", path);
            List<String> lines6 = new ArrayList<String>(); lines6.add("key1=val3"); lines6.add("scoringConcern1=scVal"); NormalizedImageInfo nii6 = new NormalizedImageInfo(lines6, "image6", path);
            
            List<String> lines7 = new ArrayList<String>(); lines7.add("key1=val4"); lines7.add("scoringConcern1=scVal"); NormalizedImageInfo nii7 = new NormalizedImageInfo(lines7, "image7", path);
            List<String> lines8 = new ArrayList<String>(); lines8.add("key1=val4"); lines8.add("scoringConcern1=scVal"); NormalizedImageInfo nii8 = new NormalizedImageInfo(lines8, "image8", path);

            List<String> lines9 = new ArrayList<String>(); lines9.add("key2=whatever"); NormalizedImageInfo nii9 = new NormalizedImageInfo(lines9, "image9", path);
            
            List<NormalizedImageInfo> niis = new ArrayList<NormalizedImageInfo>();
            niis.add(nii1);
            niis.add(nii2);
            niis.add(nii3);
            niis.add(nii4);
            niis.add(nii5);
            niis.add(nii6);
            niis.add(nii7);
            niis.add(nii8);
            niis.add(nii9);
            EvaluationSet eset = new EvaluationSet(niis, new NormalizedKey("scoringConcern1"), 0.25);
            List<EvaluationSet> esets = new ArrayList<EvaluationSet>();
            esets.add(eset);
            ScoringSetsKeySorter ssks = new ScoringSetsKeySorter(esets, new NormalizedKey("key1"));
            List<String> values = ssks.getValuesPresentForKey();
            Assert.assertEquals(ssks.getValuesPresentForKey().get(0),"val1");
            Assert.assertEquals(ssks.getValuesPresentForKey().get(1),"val2");
            Assert.assertEquals(ssks.getValuesPresentForKey().get(2),"val3");
            Assert.assertEquals(ssks.getValuesPresentForKey().get(3),"val4");
            
            Assert.assertEquals(ssks.isValueToTrain("val1"),true);
            Assert.assertEquals(ssks.isValueToTrain("val2"),false);
            Assert.assertEquals(ssks.isValueToTrain("val3"),false);
            Assert.assertEquals(ssks.isValueToTrain("val4"),false);
            
            Assert.assertEquals(ssks.getScoringConcernNames().size(),1);
            Assert.assertEquals(ssks.getScoringConcernNames().get(0),"scoringConcern1");
            
            Assert.assertEquals(ssks.getTotalTrainingCount(),2);
            Assert.assertEquals(ssks.getTotalScoringCount(),6);
            
            try {
                ssks.setValueToTrain("val3");
                Assert.assertEquals(ssks.isValueToTrain("val1"),true);
                Assert.assertEquals(ssks.isValueToTrain("val2"),false);
                Assert.assertEquals(ssks.isValueToTrain("val3"),true);
                Assert.assertEquals(ssks.isValueToTrain("val4"),false);
                Assert.assertEquals(ssks.getTotalTrainingCount(),4);
                Assert.assertEquals(ssks.getTotalScoringCount(),4);
                
                ssks.setValueToTrain("val4");
                Assert.assertEquals(ssks.isValueToTrain("val1"),true);
                Assert.assertEquals(ssks.isValueToTrain("val2"),false);
                Assert.assertEquals(ssks.isValueToTrain("val3"),true);
                Assert.assertEquals(ssks.isValueToTrain("val4"),true);
                Assert.assertEquals(ssks.getTotalTrainingCount(),6);
                Assert.assertEquals(ssks.getTotalScoringCount(),2);

                ssks.setValueToTrain("val1"); //shouldn't change anything
                Assert.assertEquals(ssks.isValueToTrain("val1"),true);
                Assert.assertEquals(ssks.isValueToTrain("val2"),false);
                Assert.assertEquals(ssks.isValueToTrain("val3"),true);
                Assert.assertEquals(ssks.isValueToTrain("val4"),true);
                Assert.assertEquals(ssks.getTotalTrainingCount(),6);
                Assert.assertEquals(ssks.getTotalScoringCount(),2);
                
                ssks.setValueToScore("val1"); 
                Assert.assertEquals(ssks.isValueToTrain("val1"),false);
                Assert.assertEquals(ssks.isValueToTrain("val2"),false);
                Assert.assertEquals(ssks.isValueToTrain("val3"),true);
                Assert.assertEquals(ssks.isValueToTrain("val4"),true);
                Assert.assertEquals(ssks.getTotalTrainingCount(),4);
                Assert.assertEquals(ssks.getTotalScoringCount(),4);
            }
            catch(AvatolCVException e){
                Assert.fail(e.getMessage());
            }
	    }
	    catch(AvatolCVException ace){
	        Assert.fail(ace.getMessage());
	    }
		
	}
	
	/*
	 *  taxon1,2,3,4 each has 2 images , train 25 percent, DOES HAVE  images in both training and scoring
	 *  
	 *  (check the EvaluationSets to verify data there as well.)
	 */
	
	/**********
	 * TWO EVAL SETS
	 **********/
	/*
	 *  set 1 : taxon1,2 each has 2 images , train 25 percent, no  images in both training and scoring
	 *  set 2 : taxon 3,4 each has two images
	 */
	
	/*
	 *  set 1 : taxon1,2 each has 2 images , train 25 percent, DOES HAVE  images in both training and scoring
	 *  set 2 : taxon 3,4 each has two images
	 */
}
