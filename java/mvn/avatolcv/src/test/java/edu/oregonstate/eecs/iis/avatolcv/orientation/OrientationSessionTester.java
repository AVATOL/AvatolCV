package edu.oregonstate.eecs.iis.avatolcv.orientation;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import edu.oregonstate.eecs.iis.avatolcv.SystemDependent;
import edu.oregonstate.eecs.iis.avatolcv.TestProgressPresenter;
import edu.oregonstate.eecs.iis.avatolcv.core.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.core.ImageInfo;
import edu.oregonstate.eecs.iis.avatolcv.core.ProgressPresenter;
import edu.oregonstate.eecs.iis.avatolcv.generic.AlgorithmRunner;
import edu.oregonstate.eecs.iis.avatolcv.generic.ImageTransformReviewStep;
import edu.oregonstate.eecs.iis.avatolcv.segmentation.BogusAlgorithmRunner;
import junit.framework.Assert;
import junit.framework.TestCase;

public class OrientationSessionTester extends TestCase {

private static final String FILESEP = System.getProperty("file.separator");
    
    public void testSession(){
        SystemDependent sd = new SystemDependent();
        String avatolcv_rootDir = sd.getRootDir();
        String parentDataDir = avatolcv_rootDir + FILESEP + "sessionData" + FILESEP +"jedHome";
        String trainingImageDir = parentDataDir + FILESEP + "orient" + FILESEP + "trainingImages";
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
        String testImageDir = parentDataDir + FILESEP + "seg" + FILESEP + "output";
        OrientationSessionData osd = null;
        try {
            osd = new OrientationSessionData(parentDataDir, rawImageDir, testImageDir, "_segOut");
        }
        catch(AvatolCVException e){
            Assert.fail("problem instatiating OrientationSessionData " + e.getMessage());
        }
        
        OrientStep1_TrainingExamplesCheck checkStep = null;
        
        try {
            checkStep = new OrientStep1_TrainingExamplesCheck(osd);
            checkStep.init();
            Assert.assertTrue(checkStep.needsAnswering());
            checkStep.consumeProvidedData();
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
        Assert.assertFalse(checkStep.needsAnswering());
        
        
        OrientStep2_LabelTrainingExamples labelStep = new OrientStep2_LabelTrainingExamples(null,osd);
        try {
        	labelStep.init();
        }
        catch(AvatolCVException e){
        	Assert.fail("problem calling init on labelStep.");
        }
        Assert.assertTrue(labelStep.needsAnswering());
        // add an image
        
        BufferedImage bi1 = null;
		BufferedImage bi2 = null;
		try {
		    bi1 = ImageIO.read(new File(avatolcv_rootDir + FILESEP + "testSupportData" + FILESEP + "cymbal1.jpg"));
		} catch (IOException e)
		{
			Assert.fail("couldn't load images");
		}
		ImageInfo ii1 = null;
		ImageInfo ii2 = null;
		try {
			ii1 = osd.getCandidateImages().get(0);
			labelStep.saveTrainingImage(bi1, ii1);
			Assert.assertTrue(osd.getImagesForStage().getTrainingImages().size() == 1);
			Assert.assertTrue(osd.getImagesForStage().getNonTrainingImages().size() == 4);
		}
		catch(AvatolCVException e){
			Assert.fail("problem saving Training image " + e.getMessage());
		}
        
        try {
            labelStep.consumeProvidedData();
        }
        catch(AvatolCVException e){
            Assert.fail("problem consuming data " + e.getMessage());
        }
        
        Assert.assertFalse(labelStep.needsAnswering());

        AlgorithmRunner runner = new BogusAlgorithmRunner();
        OrientStep3_Run orientRunStep = new OrientStep3_Run(null, osd, runner);
        Assert.assertTrue(orientRunStep.needsAnswering());
        ProgressPresenter pp = new TestProgressPresenter();
        orientRunStep.run(pp);
        try {
            orientRunStep.consumeProvidedData();
        }
        catch(AvatolCVException e){
            Assert.fail("problem consuming data " + e.getMessage());
        }
        Assert.assertFalse(orientRunStep.needsAnswering());
        
        ImageTransformReviewStep reviewStep = new ImageTransformReviewStep(null, osd);
        // nothing unique to test for reviewStep - all interesting functionality covered at step2 labeling.
        
        
    }
}
