package edu.oregonstate.eecs.iis.obsolete.avatolcv.segmentation;

import java.awt.image.BufferedImage;

import edu.oregonstate.eecs.iis.avatolcv.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.core.ImageInfo;
import edu.oregonstate.eecs.iis.avatolcv.steps.Step;
import edu.oregonstate.eecs.iis.obsolete.avatolcv.View;
import edu.oregonstate.eecs.iis.obsolete.avatolcv.orientation.ObsoleteImagesForAlgorithmStep;

/*
* This step supports the UI that allows for segmentation labeling or reviewing of segmentation labels
*/
public class SegStep2_LabelTrainingExamples implements Step {	
	private String view = null;
	private SegmentationSessionData ssd = null;
	ObsoleteImagesForAlgorithmStep ifs = null;
	boolean needsAnswering = true;
	public SegStep2_LabelTrainingExamples(String view, SegmentationSessionData ssd){
		this.view = view;
		this.ssd = ssd;
		this.ifs = ssd.getImagesForStage();
	}
	public void deleteTrainingImage(ImageInfo ii)  throws AvatolCVException{
		this.ssd.deleteTrainingImage(ii);
	}
	public void saveTrainingImage(BufferedImage bi, ImageInfo ii) throws AvatolCVException {
		this.ssd.saveTrainingImage(bi,ii);
	}

	public void disqualifyImage(ImageInfo ii) throws AvatolCVException {
		this.ssd.disqualifyImage(ii);
	}
	public void requalifyImage(ImageInfo ii) throws AvatolCVException {
		this.ssd.requalifyImage(ii);
	}
	@Override
	public void consumeProvidedData() throws AvatolCVException {
		this.ifs.reload();
		this.ssd.createDarwinDriverFile();
		this.ssd.createTrainingImageListFile();
		this.ssd.createTestImageListFile();
		this.ssd.createSegmentationConfigFile();
		this.needsAnswering = false;
	}

    @Override
    public void init() throws AvatolCVException {
        // nothing to do
        
    }
}