package edu.oregonstate.eecs.iis.avatolcv.segmentation;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import edu.oregonstate.eecs.iis.avatolcv.bisque.BisqueSessionData;
import edu.oregonstate.eecs.iis.avatolcv.core.ImageInfo;
import edu.oregonstate.eecs.iis.avatolcv.core.Step;
import edu.oregonstate.eecs.iis.avatolcv.core.View;
import edu.oregonstate.eecs.iis.avatolcv.core.AvatolCVException;

/*
* This step supports the UI that allows for segmentation labeling or reviewing of segmentation labels
*/
public class SegStep2_LabelTrainingExamples implements Step {
	public static final String GROUND_TRUTH_SUFFIX = "_groundtruth";	
	private static final String FILESEP = System.getProperty("file.separator");
	private View view = null;
	private SegmentationSessionData ssd = null;
    //private SegmentationToolHarness segToolHarness= null;

	public SegStep2_LabelTrainingExamples(View view, SegmentationSessionData ssd){
		this.view = view;
		//this.segToolHarness = segToolHarness;
		this.ssd = ssd;
	}
	public void deleteTrainingImage(ImageInfo ii){
		String targetDir = this.ssd.getSegmentationLabelDir();
		String targetPath = targetDir + FILESEP + ii.getFilename_IdNameWidth() + GROUND_TRUTH_SUFFIX + "." + ii.getExtension();
		File f = new File(targetPath);
		if (f.exists()){
			f.delete();
		}
	}
	public void saveSegmentationTrainingImage(BufferedImage bi, ImageInfo ii) throws AvatolCVException {
		String targetDir = this.ssd.getSegmentationLabelDir();
		String targetPath = targetDir + FILESEP + ii.getFilename_IdNameWidth() + GROUND_TRUTH_SUFFIX + "." + ii.getExtension();
		try {
		    File outputfile = new File(targetPath);
		    ImageIO.write(bi, ii.getExtension() , outputfile);
		} catch (IOException e) {
		    throw new AvatolCVException("could not save segmentation training image " + targetPath);
		}
	}
	@Override
	public void consumeProvidedData() throws AvatolCVException {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean needsAnswering() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public View getView() {
		return this.view;
	}
}