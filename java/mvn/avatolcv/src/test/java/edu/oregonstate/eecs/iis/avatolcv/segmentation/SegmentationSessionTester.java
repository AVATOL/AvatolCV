package edu.oregonstate.eecs.iis.avatolcv.segmentation;


import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import junit.framework.Assert;
import junit.framework.TestCase;
import edu.oregonstate.eecs.iis.avatolcv.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.SystemDependent;
import edu.oregonstate.eecs.iis.avatolcv.TestProgressPresenter;
import edu.oregonstate.eecs.iis.avatolcv.algorithm.AlgorithmRunner;
import edu.oregonstate.eecs.iis.avatolcv.core.ImageInfo;
import edu.oregonstate.eecs.iis.avatolcv.session.ProgressPresenter;
import edu.oregonstate.eecs.iis.avatolcv.steps.ImageTransformReviewStep;

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
		SegmentationSessionData ssd = null;
		try {
		    ssd = new SegmentationSessionData(parentDataDir, rawImageDir);
		}
		catch(AvatolCVException e){
		    Assert.fail("problem instantiating SegmentationSessionData " + e.getMessage());
		}
		
		SegStep1_TrainingExamplesCheck checkStep = null;
		
		try {
		    checkStep = new SegStep1_TrainingExamplesCheck(ssd);
		    checkStep.init();
		    Assert.assertTrue(ssd.getCandidateImages().size() != 0);
		    
            checkStep.consumeProvidedData();
            
		    Assert.assertTrue(ssd.getImagesForStage().getTrainingImages().size() == 0);
            Assert.assertTrue(ssd.getImagesForStage().getNonTrainingImages().size() == 5);
            Assert.assertTrue(ssd.getImagesForStage().getDisqualifiedImages().size() == 0);
            Assert.assertTrue(ssd.getImagesForStage().getInPlayImages().size() == 5);
            
	        
			int trainingCount = ssd.getImagesForStage().getTrainingImages().size();
			int testingCount = ssd.getImagesForStage().getNonTrainingImages().size();
			int total = trainingCount + testingCount;
			Assert.assertTrue(total != 0);
		}
		
		catch(AvatolCVException acve){
			Assert.fail(acve.getMessage());
		}

		
		
		SegStep2_LabelTrainingExamples labelStep = new SegStep2_LabelTrainingExamples(null, ssd);
		try {
		    labelStep.init();
		}
		catch(AvatolCVException e){
		    Assert.fail("problem calling init on seg step 2 " + e.getMessage());
		}
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
			labelStep.saveTrainingImage(bi1, ii1);
			Assert.assertTrue(ssd.getImagesForStage().getTrainingImages().size() == 1);
			Assert.assertTrue(ssd.getImagesForStage().getNonTrainingImages().size() == 4);
			// add another image
			ii2 = ssd.getCandidateImages().get(1);
			labelStep.saveTrainingImage(bi2, ii2);
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
		

		AlgorithmRunner segRunner = new BogusAlgorithmRunner();
		SegStep3_Run segRunStep = new SegStep3_Run(null, ssd, segRunner);
		try {
		    segRunStep.init();
		}
		catch(AvatolCVException e){
		    Assert.fail("problem calling init on segRunStep " + e.getMessage());
		}
		ProgressPresenter pp = new TestProgressPresenter();
		segRunStep.run(pp);
		try {
		    segRunStep.consumeProvidedData();
		}
		catch(AvatolCVException e){
            Assert.fail("problem consuming data " + e.getMessage());
        }
		
		ImageTransformReviewStep reviewStep = new ImageTransformReviewStep(null, ssd);
		reviewStep.init();
		
        
		// nothing unique to test for reviewStep - all interesting functionality covered at step2 labeling.
		
		
		
	}
}
