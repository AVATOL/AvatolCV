package edu.oregonstate.eecs.iis.avatolcv.segmentation;


import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import junit.framework.Assert;
import junit.framework.TestCase;
import edu.oregonstate.eecs.iis.avatolcv.SystemDependent;
import edu.oregonstate.eecs.iis.avatolcv.TestProgressPresenter;
import edu.oregonstate.eecs.iis.avatolcv.core.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.core.ImageInfo;
import edu.oregonstate.eecs.iis.avatolcv.core.ProgressPresenter;
import edu.oregonstate.eecs.iis.avatolcv.generic.AlgorithmRunner;
import edu.oregonstate.eecs.iis.avatolcv.generic.ImageTransformReviewStep;

public class SegmentationSessionTester extends TestCase {

	private static final String FILESEP = System.getProperty("file.separator");
	
	public void testSession(){
	    SystemDependent sd = new SystemDependent();
	    String avatolcv_rootDir = sd.getRootDir();
		String parentDataDir = avatolcv_rootDir + FILESEP + "sessionData" + FILESEP +"jedHome";
		String trainingImageDir = parentDataDir + FILESEP + "seg" + FILESEP + "trainingImages";
		File dir = new File(trainingImageDir);
		File[] files = dir.listFiles();
		if (null != files){
		    for (File f : files){
	            f.delete();
	        }
		}
		
		/*
		 * create session
		 */
		String rawImageDir = parentDataDir + FILESEP + "images" + FILESEP + "large";
		SegmentationSessionData ssd = new SegmentationSessionData(parentDataDir, rawImageDir);
		
		SegStep1_TrainingExamplesCheck checkStep = null;
		
		try {
		    checkStep = new SegStep1_TrainingExamplesCheck(ssd);
	        Assert.assertTrue(checkStep.needsAnswering());
			checkStep.consumeProvidedData();
			int trainingCount = ssd.getImagesForStage().getTrainingImages().size();
			int testingCount = ssd.getImagesForStage().getNonTrainingImages().size();
			int total = trainingCount + testingCount;
			Assert.assertTrue(total != 0);
		}
		
		catch(AvatolCVException acve){
			Assert.fail(acve.getMessage());
		}

		Assert.assertTrue(ssd.getImagesForStage().getTrainingImages().size() == 0);
		Assert.assertFalse(checkStep.needsAnswering());
		
		
		SegStep2_LabelTrainingExamples labelStep = new SegStep2_LabelTrainingExamples(null, ssd);
		Assert.assertTrue(labelStep.needsAnswering());
		// add an image
		
		BufferedImage bi1 = null;
		BufferedImage bi2 = null;
		try {
		    bi1 = ImageIO.read(new File(avatolcv_rootDir + FILESEP + "testSupportData" + FILESEP + "cymbal1.jpg"));
		    bi2 = ImageIO.read(new File(avatolcv_rootDir + FILESEP + "testSupportData" + FILESEP + "cymbal2.jpg"));
		} catch (IOException e)
		{
			Assert.fail("couldn't load images");
		}
		ImageInfo ii1 = null;
		ImageInfo ii2 = null;
		try {
			ii1 = ssd.getCandidateImages().get(0);
			labelStep.saveSegmentationTrainingImage(bi1, ii1);
			Assert.assertTrue(ssd.getImagesForStage().getTrainingImages().size() == 1);
			Assert.assertTrue(ssd.getImagesForStage().getNonTrainingImages().size() == 4);
			// add another image
			ii2 = ssd.getCandidateImages().get(1);
			labelStep.saveSegmentationTrainingImage(bi2, ii2);
			Assert.assertTrue(ssd.getImagesForStage().getTrainingImages().size() == 2);
			Assert.assertTrue(ssd.getImagesForStage().getNonTrainingImages().size() == 3);
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
		
		Assert.assertTrue(ssd.getImagesForStage().getDisqualifiedImages().size() == 0);
		Assert.assertTrue(ssd.getImagesForStage().getInPlayImages().size() == 5);
		
		Assert.assertTrue(ssd.getImagesForStage().getTrainingImages().size() == 1);
		Assert.assertTrue(ssd.getImagesForStage().getNonTrainingImages().size() == 4);
		
		// disqualification
		try {
			labelStep.disqualifyImage(ii1);
			
			Assert.assertTrue(ssd.getImagesForStage().getTrainingImages().size() == 0);
			Assert.assertTrue(ssd.getImagesForStage().getNonTrainingImages().size() == 4);

			Assert.assertTrue(ssd.getImagesForStage().getDisqualifiedImages().contains(ii1));
			Assert.assertTrue(ssd.getImagesForStage().getDisqualifiedImages().size() == 1);
			Assert.assertFalse(ssd.getImagesForStage().getInPlayImages().contains(ii1));
			Assert.assertTrue(ssd.getImagesForStage().getInPlayImages().size() == 4);
			
			labelStep.disqualifyImage(ii2);

			Assert.assertTrue(ssd.getImagesForStage().getTrainingImages().size() == 0);
			Assert.assertTrue(ssd.getImagesForStage().getNonTrainingImages().size() == 3);

			Assert.assertTrue(ssd.getImagesForStage().getDisqualifiedImages().contains(ii2));
			Assert.assertTrue(ssd.getImagesForStage().getDisqualifiedImages().size() == 2);
			Assert.assertFalse(ssd.getImagesForStage().getInPlayImages().contains(ii2));
			Assert.assertTrue(ssd.getImagesForStage().getInPlayImages().size() == 3);
		}
		catch(AvatolCVException se){
			Assert.fail("problem disqualifying image " + se.getMessage());
		}
		
		// requalification 
		try {
			labelStep.requalifyImage(ii1);
			Assert.assertTrue(ssd.getImagesForStage().getTrainingImages().size() == 1);
			Assert.assertTrue(ssd.getImagesForStage().getNonTrainingImages().size() == 3);

			Assert.assertFalse(ssd.getImagesForStage().getDisqualifiedImages().contains(ii1));
			Assert.assertTrue(ssd.getImagesForStage().getDisqualifiedImages().size() == 1);
			Assert.assertTrue(ssd.getImagesForStage().getInPlayImages().contains(ii1));
			Assert.assertTrue(ssd.getImagesForStage().getInPlayImages().size() == 4);
			
			labelStep.requalifyImage(ii2);
			Assert.assertTrue(ssd.getImagesForStage().getTrainingImages().size() == 1);
			Assert.assertTrue(ssd.getImagesForStage().getNonTrainingImages().size() == 4);

			Assert.assertFalse(ssd.getImagesForStage().getDisqualifiedImages().contains(ii2));
			Assert.assertTrue(ssd.getImagesForStage().getDisqualifiedImages().size() == 0);
			Assert.assertTrue(ssd.getImagesForStage().getInPlayImages().contains(ii2));
			Assert.assertTrue(ssd.getImagesForStage().getInPlayImages().size() == 5);
		}
		catch(AvatolCVException se){
			Assert.fail("problem requalifying image " + se.getMessage());
		}
		
		
		try {
			labelStep.consumeProvidedData();
		}
		catch(AvatolCVException e){
			Assert.fail("problem consuming data " + e.getMessage());
		}
		
		Assert.assertFalse(labelStep.needsAnswering());

		AlgorithmRunner segRunner = new BogusSegmentationRunner();
		SegStep3_Run segRunStep = new SegStep3_Run(null, ssd, segRunner);
		Assert.assertTrue(segRunStep.needsAnswering());
		ProgressPresenter pp = new TestProgressPresenter();
		segRunStep.run(pp);
		try {
		    segRunStep.consumeProvidedData();
		}
		catch(AvatolCVException e){
            Assert.fail("problem consuming data " + e.getMessage());
        }
		Assert.assertFalse(segRunStep.needsAnswering());
		
		ImageTransformReviewStep reviewStep = new ImageTransformReviewStep(null, ssd);
		// nothing unique to test for reviewStep - all interesting functionality covered at step2 labeling.
		
		
		
	}
}
