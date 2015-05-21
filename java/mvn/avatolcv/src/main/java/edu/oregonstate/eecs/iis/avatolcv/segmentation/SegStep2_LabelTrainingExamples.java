package edu.oregonstate.eecs.iis.avatolcv.segmentation;

import java.awt.image.BufferedImage;

import edu.oregonstate.eecs.iis.avatolcv.core.ImageInfo;
import edu.oregonstate.eecs.iis.avatolcv.core.ImagesForStage;
import edu.oregonstate.eecs.iis.avatolcv.core.Step;
import edu.oregonstate.eecs.iis.avatolcv.core.View;
import edu.oregonstate.eecs.iis.avatolcv.core.AvatolCVException;

/*
* This step supports the UI that allows for segmentation labeling or reviewing of segmentation labels
*/
public class SegStep2_LabelTrainingExamples implements Step {	
	private String view = null;
	private SegmentationSessionData ssd = null;
	ImagesForStage ifs = null;
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
	public String getView() {
		return this.view;
	}
    @Override
    public void init() throws AvatolCVException {
        // nothing to do
        
    }
}