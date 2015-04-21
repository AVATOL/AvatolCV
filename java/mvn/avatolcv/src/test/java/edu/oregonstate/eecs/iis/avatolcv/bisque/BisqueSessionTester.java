package edu.oregonstate.eecs.iis.avatolcv.bisque;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edu.oregonstate.eecs.iis.avatolcv.bisque.seg.ObsoleteBisqueSegLabelsCheckStep;
import edu.oregonstate.eecs.iis.avatolcv.bisque.seg.ObsoleteBisqueSegmentationReviewStep;
import edu.oregonstate.eecs.iis.avatolcv.bisque.seg.ObsoleteBisqueSegmentationRunStep;
import edu.oregonstate.eecs.iis.avatolcv.bisque.seg.ObsoleteLeafSegmentationDataPrepStep;
import edu.oregonstate.eecs.iis.avatolcv.core.ImageInfo;
import edu.oregonstate.eecs.iis.avatolcv.core.ProgressPresenter;
import edu.oregonstate.eecs.iis.avatolcv.core.Step;
import edu.oregonstate.eecs.iis.avatolcv.core.StepSequence;
import edu.oregonstate.eecs.iis.avatolcv.core.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.segmentation.SegmentationContainerStep;
import edu.oregonstate.eecs.iis.avatolcv.ws.BisqueWSClient;
import edu.oregonstate.eecs.iis.avatolcv.ws.BisqueWSClientImpl;
import edu.oregonstate.eecs.iis.avatolcv.ws.bisque.BisqueImage;
import junit.framework.Assert;
import junit.framework.TestCase;

public class BisqueSessionTester extends TestCase {
	private static final String FILESEP = System.getProperty("file.separator");
	public BisqueWSClient getBogusWSClient(){
		BisqueWSClient client = new BogusBisqueWSClient();
		return client;
	}
	public void testSession(){
		//BisqueWSClient client = getBogusWSClient();
		BisqueWSClient client = new BisqueWSClientImpl();
		String rootDir = "C:\\avatol\\git\\avatol_cv";
		/*
		 * create session
		 */
		BisqueSessionData sessionData = null;
		try {
			sessionData = new BisqueSessionData(rootDir);
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
		SegmentationContainerStep bisqueSegmentationStep = new SegmentationContainerStep(sessionData);
		
		// fill in the segmentation step with sub-steps
		Step bisqueSegLabelsCheckStep = new ObsoleteBisqueSegLabelsCheckStep(null, sessionData);
		bisqueSegmentationStep.appendStep(bisqueSegLabelsCheckStep);
		Step leafSegmentationDataPrepStep = new ObsoleteLeafSegmentationDataPrepStep(null, sessionData);
		bisqueSegmentationStep.appendStep(leafSegmentationDataPrepStep);
		Step bisqueSegmentationRunStep = new ObsoleteBisqueSegmentationRunStep(null, sessionData);
		bisqueSegmentationStep.appendStep(bisqueSegmentationRunStep);
		Step bisqueSegmentationReviewStep = new ObsoleteBisqueSegmentationReviewStep(null, sessionData);
		bisqueSegmentationStep.appendStep(bisqueSegmentationReviewStep);
		
		ss.appendStep(bisqueSegmentationStep);
		
		
		BisqueLoginStep bls = (BisqueLoginStep)ss.getCurrentStep();
		Assert.assertTrue(bls.needsAnswering());
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
		Assert.assertTrue(bls.needsAnswering());
		
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
		Assert.assertFalse(bls.needsAnswering());
		
		/*
		 * load datasets
		 */
		ss.next();
		BisqueDatasetStep bds = (BisqueDatasetStep)ss.getCurrentStep();
		Assert.assertTrue(bds.needsAnswering());
		try {
			List<String> datasets = bds.getAvailableDatasets();
			Collections.sort(datasets);
			Assert.assertEquals(datasets.get(0),"jedFlow");
			Assert.assertEquals(datasets.get(1),"jedHome");
			bds.setChosenDataset("jedHome");
			bds.consumeProvidedData();
		}
		catch(AvatolCVException e){
			Assert.fail("should not have thrown exception on getDatasets");
		}
		Assert.assertFalse(bds.needsAnswering());
		/*
		 * load images
		 */
		ss.next();
		BisqueImagePullStep bips = (BisqueImagePullStep)ss.getCurrentStep();
		Assert.assertTrue(bips.needsAnswering());
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
		Assert.assertTrue(becs.needsAnswering());
		becs.userHasViewed();
		Assert.assertFalse(becs.needsAnswering());
		/*
		 * image exclusion
		 */
		ss.next();
		BisqueExclusionStep bes = (BisqueExclusionStep)ss.getCurrentStep();
		Assert.assertTrue(bes.needsAnswering());
		List<ImageInfo> images = sessionData.getImagesLarge();
		List<ImageInfo> imagesToInclude = new ArrayList<ImageInfo>();
		List<ImageInfo> imagesToExclude = new ArrayList<ImageInfo>();
		for (ImageInfo ii : images){
			if (ii.getNameAsUploaded().equals("Neph") || ii.getNameAsUploaded().equals("Pree")){
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
		Assert.assertFalse(bes.needsAnswering());
		Assert.assertTrue(sessionData.getIncludedImages() != null);
		/*
		 * segmentation
		 */
		ss.next();
		SegmentationContainerStep bss = (SegmentationContainerStep)ss.getCurrentStep();
		StepSequence segSs = bss.getStepSequence();
		ObsoleteLeafSegmentationDataPrepStep lsdps = (ObsoleteLeafSegmentationDataPrepStep)segSs.getCurrentStep();
		Assert.assertTrue(lsdps.needsAnswering());
		
		// test at the next level down
	}

	/*
	 * need to handle timeout situations with connections!
	 */
	public class TestProgressPresenter implements ProgressPresenter {
		@Override
		public void updateProgress(int percent) {
			System.out.println("percent done " + percent);
		}

		@Override
		public void setMessage(String m) {
			System.out.println(m);	
		}
		
	}
}
