package edu.oregonstate.eecs.iis.avatolcv.bisque;

import java.util.Collections;
import java.util.List;

import edu.oregonstate.eecs.iis.avatolcv.core.ProgressPresenter;
import edu.oregonstate.eecs.iis.avatolcv.core.Step;
import edu.oregonstate.eecs.iis.avatolcv.core.StepSequence;
import edu.oregonstate.eecs.iis.avatolcv.ws.BisqueSessionException;
import edu.oregonstate.eecs.iis.avatolcv.ws.BisqueWSClient;
import edu.oregonstate.eecs.iis.avatolcv.ws.BisqueWSClientImpl;
import junit.framework.Assert;
import junit.framework.TestCase;

public class BisqueSessionTester extends TestCase {
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
		catch (BisqueSessionException e){
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
		catch(BisqueSessionException bse){
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
		catch(BisqueSessionException bse){
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
		catch(BisqueSessionException e){
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
		catch(BisqueSessionException e){
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
		
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
