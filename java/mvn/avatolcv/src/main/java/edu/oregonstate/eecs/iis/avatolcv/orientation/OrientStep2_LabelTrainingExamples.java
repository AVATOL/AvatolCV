package edu.oregonstate.eecs.iis.avatolcv.orientation;

import java.awt.image.BufferedImage;

import edu.oregonstate.eecs.iis.avatolcv.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.core.ImageInfo;
import edu.oregonstate.eecs.iis.avatolcv.core.ImagesForAlgorithmStep;
import edu.oregonstate.eecs.iis.avatolcv.segmentation.SegmentationSessionData;
import edu.oregonstate.eecs.iis.avatolcv.steps.Step;
import edu.oregonstate.eecs.iis.obsolete.avatolcv.View;

public class OrientStep2_LabelTrainingExamples implements Step {
    
    private String view = null;
    private OrientationSessionData osd = null;
    ImagesForAlgorithmStep ifs = null;
    
    public OrientStep2_LabelTrainingExamples(String view, OrientationSessionData osd){
        this.view = view;
        this.osd = osd;
        
    }
    public void deleteTrainingImage(ImageInfo ii)  throws AvatolCVException{
		this.osd.deleteTrainingImage(ii);
	}
	public void saveTrainingImage(BufferedImage bi, ImageInfo ii) throws AvatolCVException {
		this.osd.saveTrainingImage(bi,ii);
	}

	public void disqualifyImage(ImageInfo ii) throws AvatolCVException {
		this.osd.disqualifyImage(ii);
	}
	public void requalifyImage(ImageInfo ii) throws AvatolCVException {
		this.osd.requalifyImage(ii);
	}
    @Override
    public void init() throws AvatolCVException {
        this.ifs = osd.getImagesForStage();
    }
    @Override
    public void consumeProvidedData() throws AvatolCVException {
        this.ifs.reload();
        this.osd.createTrainingImageListFile();
        this.osd.createTestImageListFile();
        this.osd.createOrientationConfigFile();
       
    }
   

}
