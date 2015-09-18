package edu.oregonstate.eecs.iis.avatolcv.segmentation;

import java.util.List;

import edu.oregonstate.eecs.iis.avatolcv.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.core.StepSequence;
import edu.oregonstate.eecs.iis.avatolcv.morphobank.MBSessionData;
import edu.oregonstate.eecs.iis.avatolcv.obsolete.View;
import edu.oregonstate.eecs.iis.avatolcv.obsolete.bisque.BisqueSessionData;
import edu.oregonstate.eecs.iis.avatolcv.steps.Step;
import edu.oregonstate.eecs.iis.avatolcv.ws.MorphobankWSClient;

/*
 * At the SEGMENTATION step, avatolCV will pass one argument, which is the path to the runConfig_segmentation.txt file

runConfig_segmentation.txt has lines of the following form:

    darwinXMLFileDir,<path to dir containing darwin xml file>
    trainingImagesFile,<path to training images file>  // file will be named trainingImages_segmentation.txt
    testingImagesFile,<path to testing images file>    // file will be named testingImages_segmentation.txt
    rawImagesDir,<path of dir where right sized images from bisque are put>

lines with other prefixes that can be ignored by leaf code:
    segmentationOutputDir,<same as in xml file - points to where segmented files are put>
    trainingImage,...
    testImage,...


trainingImages_segmentation.txt and testingImages_segmentation.txt have entries that are the root names of images
    00-5xayvrdPC3o5foKMpLbZ5H_imgXyz
    03-uietIOuerto5foKMhUHYUh_imgAbc

 */
public class SegmentationStep implements Step {
	private SegmentationSessionData sessionData = null;
	private StepSequence ss = new StepSequence();
	
	public void appendStep(Step s){
		ss.appendStep(s);
	}
	    
	public SegmentationStep(SegmentationSessionData s){
		this.sessionData = s;
	}
	public StepSequence getStepSequence(){
		return this.ss;
	}
	@Override
	public void consumeProvidedData() throws AvatolCVException {
		// TODO Auto-generated method stub
	}
    @Override
    public void init() throws AvatolCVException {
        // TODO Auto-generated method stub
    }

}
