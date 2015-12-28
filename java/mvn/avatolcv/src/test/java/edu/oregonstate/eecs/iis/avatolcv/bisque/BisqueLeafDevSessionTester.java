package edu.oregonstate.eecs.iis.avatolcv.bisque;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.imageio.ImageIO;

import junit.framework.Assert;
import junit.framework.TestCase;
import edu.oregonstate.eecs.iis.avatolcv.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.SystemDependent;
import edu.oregonstate.eecs.iis.avatolcv.TestProgressPresenter;
import edu.oregonstate.eecs.iis.avatolcv.algorithm.AlgorithmRunner;
import edu.oregonstate.eecs.iis.avatolcv.core.ImageInfo;
import edu.oregonstate.eecs.iis.avatolcv.orientation.OrientStep1_TrainingExamplesCheck;
import edu.oregonstate.eecs.iis.avatolcv.orientation.OrientStep2_LabelTrainingExamples;
import edu.oregonstate.eecs.iis.avatolcv.orientation.OrientStep3_Run;
import edu.oregonstate.eecs.iis.avatolcv.orientation.OrientationSessionData;
import edu.oregonstate.eecs.iis.avatolcv.segmentation.BogusAlgorithmRunner;
import edu.oregonstate.eecs.iis.avatolcv.segmentation.SegStep1_TrainingExamplesCheck;
import edu.oregonstate.eecs.iis.avatolcv.segmentation.SegStep2_LabelTrainingExamples;
import edu.oregonstate.eecs.iis.avatolcv.segmentation.SegStep3_Run;
import edu.oregonstate.eecs.iis.avatolcv.segmentation.SegmentationSessionData;
import edu.oregonstate.eecs.iis.avatolcv.session.ProgressPresenter;
import edu.oregonstate.eecs.iis.avatolcv.session.StepSequence;
import edu.oregonstate.eecs.iis.avatolcv.steps.ImageTransformReviewStep;
import edu.oregonstate.eecs.iis.avatolcv.steps.Step;
import edu.oregonstate.eecs.iis.avatolcv.ws.BisqueWSClient;
import edu.oregonstate.eecs.iis.avatolcv.ws.BisqueWSClientImpl;
import edu.oregonstate.eecs.iis.obsolete.avatolcv.bisque.BisqueDatasetStep;
import edu.oregonstate.eecs.iis.obsolete.avatolcv.bisque.BisqueExclusionCoachingStep;
import edu.oregonstate.eecs.iis.obsolete.avatolcv.bisque.BisqueExclusionStep;
import edu.oregonstate.eecs.iis.obsolete.avatolcv.bisque.BisqueImagePullStep;
import edu.oregonstate.eecs.iis.obsolete.avatolcv.bisque.BisqueLoginStep;
import edu.oregonstate.eecs.iis.obsolete.avatolcv.bisque.BisqueSessionData;

public class BisqueLeafDevSessionTester extends TestCase {
	private static final String FILESEP = System.getProperty("file.separator");
	public BisqueWSClient getBogusWSClient(){
		BisqueWSClient client = new BogusBisqueWSClient();
		return client;
	}
	public void testSession(){
		//BisqueWSClient client = getBogusWSClient();
		BisqueWSClient client = new BisqueWSClientImpl();
		SystemDependent sd = new SystemDependent();
        String avatolcv_rootDir = sd.getRootDir();
        System.out.println("root dir sensed as " + avatolcv_rootDir);
		/*
		 * create session
		 */
		BisqueSessionData sessionData = null;
		try {
			sessionData = new BisqueSessionData(avatolcv_rootDir);
		}
		catch (AvatolCVException e){
			Assert.fail("problem instantiating BisqueSessionData " + e.getMessage());
		}
		StepSequence ss = new StepSequence();
		Step bisqueLoginStep = new BisqueLoginStep(null, client);
		ss.appendStep(bisqueLoginStep);
		Step bisqueDatasetStep = new BisqueDatasetStep(null, client, sessionData);
		ss.appendStep(bisqueDatasetStep);
		Step bisqueImagePullStep = new BisqueImagePullStep(null, client, sessionData);
		ss.appendStep(bisqueImagePullStep);
		Step bisqueExclusionCoachingStep = new BisqueExclusionCoachingStep(null, client);
		ss.appendStep(bisqueExclusionCoachingStep);
		Step bisqueExclusionStep = new BisqueExclusionStep(null, sessionData);
		ss.appendStep(bisqueExclusionStep);
		
		BisqueLoginStep bls = (BisqueLoginStep)ss.getCurrentStep();
		/*
		 * throw exception on failed login 
		 */
		try {
			bls.setUsername("jedirv");
			bls.setPassword("badPassword");
			bls.consumeProvidedData();
			Assert.fail("should have thrown exception on bad password");
		}
		catch(AvatolCVException bse){
			Assert.assertTrue(true);
		}
		
		/*
		 *  good password should change state
		 */
		try {
			bls.setUsername("jedirv");
			bls.setPassword("Neton3plants**");
			bls.consumeProvidedData();
			Assert.assertTrue(true);
			
		}
		catch(AvatolCVException bse){
			Assert.fail("should not have thrown exception on good password");
		}
		
		/*
		 * load datasets
		 */
		ss.next();
		BisqueDatasetStep bds = (BisqueDatasetStep)ss.getCurrentStep();
		try {
			List<String> datasets = bds.getAvailableDatasets();
			Collections.sort(datasets);
			Assert.assertEquals(datasets.get(0),"jedFlow");
			Assert.assertEquals(datasets.get(1),"jedHome");
			Assert.assertEquals(datasets.get(2),"leafDev");
			bds.setChosenDataset("leafDev");
			bds.consumeProvidedData();
		}
		catch(AvatolCVException e){
			Assert.fail("should not have thrown exception on getDatasets");
		}
		/*
		 * load images
		 */
		ss.next();
		BisqueImagePullStep bips = (BisqueImagePullStep)ss.getCurrentStep();
		ProgressPresenter pp = new TestProgressPresenter();
		try {
			bips.downloadImagesForChosenDataset(pp);
		}
		catch(AvatolCVException e){
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
		List<ImageInfo> currentImages = sessionData.getImagesLarge();
		Assert.assertTrue(currentImages.size() != 0);
		ImageInfo image = currentImages.get(0);
		String name = image.getFilename();
		String imagesLargeDir = sessionData.getImagesLargeDir();
		String pathOfSupposedlyDownloadedImage = imagesLargeDir + FILESEP + name;
		File downloadedImageFile = new File(pathOfSupposedlyDownloadedImage);
		Assert.assertTrue(downloadedImageFile.exists());
		/*
		 * exclusion coaching
		 */
		ss.next();
		BisqueExclusionCoachingStep becs = (BisqueExclusionCoachingStep)ss.getCurrentStep();
		becs.userHasViewed();
		/*
		 * image exclusion
		 */
		ss.next();
		BisqueExclusionStep bes = (BisqueExclusionStep)ss.getCurrentStep();
		List<ImageInfo> images = sessionData.getImagesLarge();
		List<ImageInfo> imagesToInclude = new ArrayList<ImageInfo>();
		List<ImageInfo> imagesToExclude = new ArrayList<ImageInfo>();
		for (ImageInfo ii : images){
			if (ii.getNameAsUploadedNormalized().equals("Neph") || ii.getNameAsUploadedNormalized().equals("Pree")){
				imagesToInclude.add(ii);
			}
			else {
				imagesToExclude.add(ii);
			}
		}
		try {
			bes.setImagesToExclude(imagesToExclude);
			bes.setImagesToInclude(imagesToInclude);
			bes.consumeProvidedData();
		}
		catch(AvatolCVException e){
			Assert.fail(e.getMessage());
		}
		Assert.assertTrue(sessionData.getIncludedImages() != null);
		/*
		 * segmentation
		 */
		
		
		String parentDataDir = avatolcv_rootDir + FILESEP + "sessionData" + FILESEP +"leafDev";
		// leave training images intact - put them there by hand
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
		
		SegStep1_TrainingExamplesCheck segmentationCheckStep = null;
		
		try {
			segmentationCheckStep = new SegStep1_TrainingExamplesCheck(ssd);
			segmentationCheckStep.init();
			segmentationCheckStep.consumeProvidedData();
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
		pp = new TestProgressPresenter();
		segRunStep.run(pp);
		try {
		    segRunStep.consumeProvidedData();
		}
		catch(AvatolCVException e){
            Assert.fail("problem consuming data " + e.getMessage());
        }
		
		ImageTransformReviewStep reviewStep = new ImageTransformReviewStep(null, ssd);
		reviewStep.init();
		
        
		/*
		 * Orientation
		 */
		
        
        /*
         * create session
         */
        String orientationTestImageDir = parentDataDir + FILESEP + "seg" + FILESEP + "output";
        OrientationSessionData osd = null;
        try {
            osd = new OrientationSessionData(parentDataDir, rawImageDir, orientationTestImageDir, SegmentationSessionData.TYPE_SUFFIX_OUTPUT);
        }
        catch(AvatolCVException e){
            Assert.fail("problem instatiating OrientationSessionData " + e.getMessage());
        }
        
        OrientStep1_TrainingExamplesCheck orientationCheckStep = null;
        
        try {
        	orientationCheckStep = new OrientStep1_TrainingExamplesCheck(osd);
        	orientationCheckStep.init();
            orientationCheckStep.consumeProvidedData();
            int trainingCount = osd.getImagesForStage().getTrainingImages().size();
            Assert.assertTrue(trainingCount == 0);
            int testingCount = osd.getImagesForStage().getNonTrainingImages().size();
            int total = trainingCount + testingCount;
            Assert.assertTrue(total != 0);
        }
        
        catch(AvatolCVException acve){
            Assert.fail(acve.getMessage());
        }

        Assert.assertTrue(osd.getImagesForStage().getTrainingImages().size() == 0);
        
        
        OrientStep2_LabelTrainingExamples orientationLabelStep = new OrientStep2_LabelTrainingExamples(null,osd);
        try {
        	orientationLabelStep.init();
        }
        catch(AvatolCVException e){
        	Assert.fail("problem calling init on labelStep.");
        }
        
        
        try {
        	orientationLabelStep.consumeProvidedData();
        }
        catch(AvatolCVException e){
            Assert.fail("problem consuming data " + e.getMessage());
        }   
	}

	/*
	 * need to handle timeout situations with connections!
	 */
	
}
