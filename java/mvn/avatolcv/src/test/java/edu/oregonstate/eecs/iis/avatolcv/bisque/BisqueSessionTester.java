package edu.oregonstate.eecs.iis.avatolcv.bisque;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edu.oregonstate.eecs.iis.avatolcv.AvatolCVFileSystem;
import edu.oregonstate.eecs.iis.avatolcv.SystemDependent;
import edu.oregonstate.eecs.iis.avatolcv.TestProgressPresenter;
import edu.oregonstate.eecs.iis.avatolcv.core.ImageInfo;
import edu.oregonstate.eecs.iis.avatolcv.core.ProgressPresenter;
import edu.oregonstate.eecs.iis.avatolcv.core.Step;
import edu.oregonstate.eecs.iis.avatolcv.core.StepSequence;
import edu.oregonstate.eecs.iis.avatolcv.core.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.generic.CharQuestionsStep;
import edu.oregonstate.eecs.iis.avatolcv.questionnaire.QQuestion;
import edu.oregonstate.eecs.iis.avatolcv.questionnaire.QuestionSequencer;
import edu.oregonstate.eecs.iis.avatolcv.segmentation.SegmentationContainerStep;
import edu.oregonstate.eecs.iis.avatolcv.ws.BisqueWSClient;
import edu.oregonstate.eecs.iis.avatolcv.ws.BisqueWSClientImpl;
import edu.oregonstate.eecs.iis.avatolcv.ws.bisque.BisqueAnnotation;
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
		SystemDependent sd = new SystemDependent();
        String avatolcv_rootDir = sd.getRootDir();
        System.out.println("root dir sensed as " + avatolcv_rootDir);
        try {
            AvatolCVFileSystem afs = new AvatolCVFileSystem(avatolcv_rootDir);
        }
        catch(AvatolCVException e){
            Assert.fail("problem instantiating AvatolCVFileSystem : " + e.getMessage());
        }
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
		Step bisqueCharChoiceStep = new BisqueCharChoiceStep(null, client, sessionData);
		ss.appendStep(bisqueCharChoiceStep);
        Step bisqueImagePullStep = new BisqueImagePullStep(null, client, sessionData);
        ss.appendStep(bisqueImagePullStep);
		Step bisqueExclusionCoachingStep = new BisqueExclusionCoachingStep(null, client);
		ss.appendStep(bisqueExclusionCoachingStep);
		Step bisqueExclusionStep = new BisqueExclusionStep(null, sessionData);
		ss.appendStep(bisqueExclusionStep);
		Step bisqueCharQuestionsStep = new CharQuestionsStep(null, sessionData);
        ss.appendStep(bisqueCharQuestionsStep);
		
		
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
			bds.setChosenDataset("jedHome");
			bds.consumeProvidedData();
		}
		catch(AvatolCVException e){
			Assert.fail("should not have thrown exception on getDatasets");
		}
		/*
		 * choose character
		 */
		ss.next();
		BisqueCharChoiceStep bccs = (BisqueCharChoiceStep)ss.getCurrentStep();
		try {
		    List<BisqueAnnotation> chars = bccs.getCharacters();
		    //Assert.assertTrue(chars.size() == 2); had to comment this out - there are more than two on the live site
            Assert.assertTrue(annotationsContainName(chars,"gender"));
            Assert.assertTrue(annotationsContainName(chars,"name"));
            bccs.setChosenAnnotation(chars.get(0));
            
            
		}
		catch(AvatolCVException e){
            Assert.fail("should not have thrown exception on getCharacters");
        }
		try{
		    bccs.consumeProvidedData();
		}
		catch(AvatolCVException e){
            Assert.fail("should not have thrown exception on consumeProvidedData");
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
		 * character questions
		 */
		ss.next();
		CharQuestionsStep cqs = (CharQuestionsStep)ss.getCurrentStep();
		try {
		    cqs.init();
		}
		catch(AvatolCVException e){
            Assert.fail("problem initializing CharQuestionStep " + e.getMessage());
		}
		QuestionSequencer qs = cqs.getQuestionSequencer();
		QQuestion qquestion = qs.getCurrentQuestion();
		try {
		    Assert.assertTrue(qquestion.getAnswerIntegrity("perimeter").isValid());
            Assert.assertTrue(qquestion.getAnswerIntegrity("interior").isValid());
            Assert.assertFalse(qquestion.getAnswerIntegrity("an African swallow").isValid());
		}
		catch(AvatolCVException e){
		    Assert.fail("problem getting answer integrity");
		}
		try {
		    qs.answerQuestion("perimeter");
		}
		catch(AvatolCVException e){
            Assert.fail("problem answering question");
        }
		try {
		    File f = new File(sessionData.getCharQuestionsAnsweredQuestionsPath());
		    if (f.exists()){
		        f.delete();
		    }
            cqs.consumeProvidedData();
            Assert.assertTrue(f.exists());
        }
        catch(AvatolCVException e){
            Assert.fail("problem consuming data");
        }
	}

	private boolean annotationsContainName(List<BisqueAnnotation> annotations, String s){
	    for (BisqueAnnotation a : annotations){
	        if (a.getName().equals(s)){
	            return true;
	        }
	    }
	    return false;
	}
	/*
	 * need to handle timeout situations with connections!
	 */
	
}
