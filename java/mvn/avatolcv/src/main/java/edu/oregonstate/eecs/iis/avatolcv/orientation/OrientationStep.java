package edu.oregonstate.eecs.iis.avatolcv.orientation;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import junit.framework.Assert;
import edu.oregonstate.eecs.iis.avatolcv.SystemDependent;
import edu.oregonstate.eecs.iis.avatolcv.TestProgressPresenter;
import edu.oregonstate.eecs.iis.avatolcv.core.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.core.ImageInfo;
import edu.oregonstate.eecs.iis.avatolcv.core.ProgressPresenter;
import edu.oregonstate.eecs.iis.avatolcv.core.Step;
import edu.oregonstate.eecs.iis.avatolcv.core.View;
import edu.oregonstate.eecs.iis.avatolcv.generic.AlgorithmRunner;
import edu.oregonstate.eecs.iis.avatolcv.generic.ImageTransformReviewStep;
import edu.oregonstate.eecs.iis.avatolcv.segmentation.BogusAlgorithmRunner;
import edu.oregonstate.eecs.iis.avatolcv.segmentation.SegmentationSessionData;

public class OrientationStep implements Step {
    private static final String FILESEP = System.getProperty("file.separator");

    private String projectDir = null;
    public OrientationStep(String projectDir){
        this.projectDir = projectDir;
    }
	@Override
	public void consumeProvidedData() throws AvatolCVException {
		// TODO Auto-generated method stub

	}

	@Override
	public String getView() {
		// TODO Auto-generated method stub
		return null;
	}

    @Override
    public void init() {
        // TODO Auto-generated method stub
        
    }

}
