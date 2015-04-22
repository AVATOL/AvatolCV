package edu.oregonstate.eecs.iis.avatolcv.segmentation;

import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;

import edu.oregonstate.eecs.iis.avatolcv.bisque.BisqueSessionData;
import edu.oregonstate.eecs.iis.avatolcv.core.ImageInfo;
import edu.oregonstate.eecs.iis.avatolcv.core.Step;
import edu.oregonstate.eecs.iis.avatolcv.core.View;
import edu.oregonstate.eecs.iis.avatolcv.core.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.segmentation.files.DarwinDriverFile;

/*
* This step supports the UI that allows for segmentation labeling or reviewing of segmentation labels
*/
public class SegStep2_LabelTrainingExamples implements Step {
	public static final String GROUND_TRUTH_SUFFIX = "_groundtruth";	
	private static final String FILESEP = System.getProperty("file.separator");
	private static final String NL = System.getProperty("line.separator");
	private View view = null;
	private SegmentationSessionData ssd = null;
	SegmentationImages si = null;
	boolean needsAnswering = true;
	public SegStep2_LabelTrainingExamples(View view, SegmentationSessionData ssd){
		this.view = view;
		this.ssd = ssd;
		this.si = ssd.getSegmentationImages();
	}
	public void deleteTrainingImage(ImageInfo ii)  throws AvatolCVException{
		String targetDir = this.ssd.getSegmentationTrainingImageDir();
		String targetPath = targetDir + FILESEP + ii.getFilename_IdNameWidth() + GROUND_TRUTH_SUFFIX + "." + ii.getExtension();
		File f = new File(targetPath);
		if (f.exists()){
			f.delete();
		}
		try {
			this.si.reload();
		}
		catch(SegmentationException se){
			throw new AvatolCVException("could not reload SegmentationImages after training image delete: " + se.getMessage(), se);
		}
	}
	public void saveSegmentationTrainingImage(BufferedImage bi, ImageInfo ii) throws AvatolCVException {
		String targetDir = this.ssd.getSegmentationTrainingImageDir();
		String targetPath = targetDir + FILESEP + ii.getFilename_IdNameWidth() + GROUND_TRUTH_SUFFIX + "." + ii.getExtension();
		try {
		    File outputfile = new File(targetPath);
		    ImageIO.write(bi, ii.getExtension() , outputfile);
		    this.si.reload();
		} catch (IOException e) {
		    throw new AvatolCVException("could not save segmentation training image " + targetPath);
		}
		catch(SegmentationException se){
			throw new AvatolCVException("could not reload SegmentationImages after training image add: " + se.getMessage(), se);
		}
	}
	
	
	
	@Override
	public void consumeProvidedData() throws AvatolCVException {
		try {
			this.si.reload();
			this.ssd.createDarwinDriverFile();
			this.ssd.createTrainingImageListFile();
			this.ssd.createTestImageListFile();
			this.ssd.createSegmentationConfigFile();
			this.needsAnswering = false;
		}
		catch(SegmentationException se){
			throw new AvatolCVException("problem reloading SegmentationImages ",se);
		}
	}

	@Override
	public boolean needsAnswering() {
		return this.needsAnswering;
	}

	@Override
	public View getView() {
		return this.view;
	}
}