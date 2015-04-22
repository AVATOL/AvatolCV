package edu.oregonstate.eecs.iis.avatolcv.segmentation;


import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import junit.framework.Assert;
import junit.framework.TestCase;
import edu.oregonstate.eecs.iis.avatolcv.core.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.core.ImageInfo;

public class SegmentationSessionTester extends TestCase {

	private static final String FILESEP = System.getProperty("file.separator");
	
	public void testSession(){
		String parentDataDir = "C:\\avatol\\git\\avatol_cv\\sessionData\\jedHome";
		String trainingImageDir = parentDataDir + FILESEP + "seg" + FILESEP + "trainingImages";
		File dir = new File(trainingImageDir);
		File[] files = dir.listFiles();
		for (File f : files){
			f.delete();
		}
		/*
		 * create session
		 */
		SegmentationSessionData ssd = new SegmentationSessionData(parentDataDir);
		ssd.setSourceImageDir(parentDataDir + FILESEP + "images" + FILESEP + "large");
		
		SegStep1_TrainingExamplesCheck checkStep = new SegStep1_TrainingExamplesCheck(null, ssd);
		Assert.assertTrue(checkStep.needsAnswering());
		try {
			checkStep.assess();
			checkStep.consumeProvidedData();
			int trainingCount = ssd.getSegmentationImages().getTrainingImages().size();
			int testingCount = ssd.getSegmentationImages().getTestImages().size();
			int total = trainingCount + testingCount;
			Assert.assertTrue(total != 0);
		}
		catch(SegmentationException se){
			Assert.fail(se.getMessage());
		}
		catch(AvatolCVException acve){
			Assert.fail(acve.getMessage());
		}

		Assert.assertTrue(ssd.getSegmentationImages().getTrainingImages().size() == 0);
		Assert.assertFalse(checkStep.needsAnswering());
		
		
		SegStep2_LabelTrainingExamples labelStep = new SegStep2_LabelTrainingExamples(null, ssd);
		Assert.assertTrue(labelStep.needsAnswering());
		// add an image
		
		BufferedImage bi1 = null;
		BufferedImage bi2 = null;
		try {
		    bi1 = ImageIO.read(new File("C:\\avatol\\git\\avatol_cv\\testSupportData\\cymbal1.jpg"));
		    bi2 = ImageIO.read(new File("C:\\avatol\\git\\avatol_cv\\testSupportData\\cymbal2.jpg"));
		} catch (IOException e) 
		{
			Assert.fail("couldn't load images");
		}
		ImageInfo ii1 = null;
		ImageInfo ii2 = null;
		try {
			ii1 = ssd.getCandidateImages().get(0);
			labelStep.saveSegmentationTrainingImage(bi1, ii1);
			// add another image
			ii2 = ssd.getCandidateImages().get(1);
			labelStep.saveSegmentationTrainingImage(bi2, ii2);
			Assert.assertTrue(ssd.getSegmentationImages().getTrainingImages().size() == 2);
		}
		catch(AvatolCVException e){
			Assert.fail("problem saving Training image " + e.getMessage());
		}
		
		// delete first image
		try {
			labelStep.deleteTrainingImage(ii2);
		}
		catch(AvatolCVException e){
			Assert.fail("problem deleting image " + e.getMessage());
		}
		
		Assert.assertTrue(ssd.getSegmentationImages().getTrainingImages().size() == 1);
		try {
			labelStep.consumeProvidedData();
		}
		catch(AvatolCVException e){
			Assert.fail("problem consuming data " + e.getMessage());
		}
		
		Assert.assertFalse(labelStep.needsAnswering());

		SegStep3_Run segRunStep = new SegStep3_Run(null, ssd);
		SegStep4_Review reviewStep = new SegStep4_Review(null, ssd);
		
		
		
		
	}
}
