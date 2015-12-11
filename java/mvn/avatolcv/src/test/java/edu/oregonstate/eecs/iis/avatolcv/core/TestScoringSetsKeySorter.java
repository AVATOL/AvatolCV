package edu.oregonstate.eecs.iis.avatolcv.core;

import java.util.List;

import junit.framework.TestCase;

public class TestScoredSetsKeySorter extends TestCase {

	/**********
	 * ONE EVAL SET
	 **********/
	/*
	 *  taxon1,2,3,4 each has 2 images , train 25 percent, no  images in both training and scoring
	 */
	public void testSingelSetSimple(){
		//List<String> 
		//NormalizedImageInfo nii1 = new NormalizedImageInfo(List<String> lines, String imageID, String path)
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
