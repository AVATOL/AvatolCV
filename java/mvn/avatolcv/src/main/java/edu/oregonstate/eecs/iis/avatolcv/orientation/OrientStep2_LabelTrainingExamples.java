package edu.oregonstate.eecs.iis.avatolcv.orientation;

import java.awt.image.BufferedImage;

import edu.oregonstate.eecs.iis.avatolcv.core.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.core.ImageInfo;
import edu.oregonstate.eecs.iis.avatolcv.core.ImagesForStage;
import edu.oregonstate.eecs.iis.avatolcv.core.Step;
import edu.oregonstate.eecs.iis.avatolcv.core.View;
import edu.oregonstate.eecs.iis.avatolcv.segmentation.SegmentationSessionData;

public class OrientStep2_LabelTrainingExamples implements Step {
    
    private String view = null;
    private OrientationSessionData osd = null;
    ImagesForStage ifs = null;
    
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


    @Override
    public String getView() {
        return null;
    }
   

}
