package edu.oregonstate.eecs.iis.avatolcv.orientation;

import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import edu.oregonstate.eecs.iis.avatolcv.core.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.core.ImageInfo;
import edu.oregonstate.eecs.iis.avatolcv.core.ImagesForStage;
import edu.oregonstate.eecs.iis.avatolcv.segmentation.files.DarwinDriverFile;

public class OrientationSessionData {
	public static final String GROUND_TRUTH_SUFFIX = "_groundtruth";
	public static final String MASK_SUFFIX = "_mask";
	private static final String FILESEP = System.getProperty("file.separator");
	private static final String NL = System.getProperty("line.separator");
	private String parentDataDir = null;
	private String rootOrientationDir = null;
	private String outputDir = null;
	private String sourceImageDir = null;
	private String rawImagesDir = null;
    private ImagesForStage ifs = null;
    private List<ImageInfo> inPlaySegmentedImages = null;
    
	public OrientationSessionData(String parentDataDir){
		this.parentDataDir = parentDataDir;
		this.rootOrientationDir = this.parentDataDir + FILESEP + "orient";
		ensureDirExists(this.rootOrientationDir);
		this.outputDir = this.rootOrientationDir + FILESEP + "output";
		ensureDirExists(this.outputDir);
	}
	public void cleanResults(){
		File dir = new File(this.outputDir);
		if (!dir.isDirectory()){
			dir.mkdirs();
		}
		File[] files = dir.listFiles();
		for (File f : files){
			f.delete();
		}
	}
	public void setRawImagesDir(String s){
		this.rawImagesDir = s;
	}
	/*
	trainingImages_segmentation.txt and testingImages_segmentation.txt have entries that are the root names of images
	    00-5xayvrdPC3o5foKMpLbZ5H_imgXyz
	    03-uietIOuerto5foKMhUHYUh_imgAbc
    */
	public void createRelevantImageListFile() throws AvatolCVException {
		List<ImageInfo> images = this.ifs.getInPlayImages();
		String path = getRelevantImageFilePath();
		File f = new File(path);
		if (f.exists()){
			f.delete();
		}
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(path));
			for (ImageInfo ii : images){
				String nameRoot = ii.getFilename_IdNameWidth();
				writer.write(nameRoot + NL);
			}
			writer.close();
		}
		catch(IOException ioe){
			throw new AvatolCVException("problem creating relevant image list file: " + ioe.getMessage(),ioe);
		}	
	}
	
	/*
	 * darwinOutputDir,<same as in xml file - points to where segmented files are put>
    rawImagesDir,<path of dir where right sized images from bisque are put>
    relaventImagesFile,<path to file that lists filenames of images to orient>
    orientationResultsDir,<path to dir to put orientation normalized images>
	modelFilePath,<path to pre-trained svm model for apex, base locating>
	apexLocationConvention,left

    output files    ..._rotated.jpg
	 */
	public void createOrientationConfigFile() throws AvatolCVException {
		String path = getConfigFilePath();
		File f = new File(path);
		if (f.exists()){
			f.delete();
		}
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(path));
			writer.write("darwinOutputDir=" + this.sourceImageDir + NL);	
			writer.write("rawImagesDir=" + this.rawImagesDir + NL);
			writer.write("segmentationOutputDir=" + this.sourceImageDir + NL);
			writer.write("relevantImagesFile=" + getRelevantImageFilePath() + NL);
			writer.write("orientationResultsDir=" + this.outputDir + NL);
			writer.write("outputFileSuffix=_rotated" + NL);
			writer.write("modelFilePath=" + getModelFilePath() + NL);
			writer.write("apexLocationConvention=left" + NL);
			writer.close();
		}
		catch(IOException ioe){
			throw new AvatolCVException("problem creating orientation config file: " + ioe.getMessage(), ioe);
		}
		

	}

	public String getModelFilePath(){
		return rootOrientationDir + FILESEP + "orientationModel.txt";
	}
	public String getConfigFilePath(){
		return rootOrientationDir + FILESEP + "runConfig_orientation.txt";
	}
	
	public String getRelevantImageFilePath(){
		return rootOrientationDir + FILESEP + "relevantImages.txt";
	}
	public void ensureDirExists(String dir){
		File f = new File(dir);
		if (!f.isDirectory()){
			f.mkdirs();
		}
	}
	//TODO WAITING ON YAO RESPONSE___do I need to provide the segmentation training images as input in addition to the segmentation output
	
	LEFT OFF WAITING FOR THE ABOVE AND PONDERING...
	
	now that I put ImagesForStage in play - it assumes training and test data.  should I not add steps to allow that to be there in case ther is no model?
			
			
	public void setImagesForStage(ImagesForStage ifs){
		this.ifs = ifs;
	}
	public ImagesForStage getImagesForStage(){
		return this.ifs;
	}
	public List<ImageInfo> getInPlayRawImages(){
		return this.ifs.getInPlayImages();
	}
	public void setInPlaySegOutputImages(List<ImageInfo> images){
		this.inPlaySegmentedImages = images;
	}
	public void setSourceImageDir(String s){
		this.sourceImageDir = s;
	}
	public String getOrientationOutputDir(){
		return this.outputDir;
	}
	public String getSourceImageDir(){
		return this.sourceImageDir;
	}
	
	
	public void disqualifyImage(ImageInfo ii) throws AvatolCVException {
		this.ifs.disqualifyImage(ii);
	}
	public void requalifyImage(ImageInfo ii) throws AvatolCVException {
		this.ifs.requalifyImage(ii);
	}
}
