package edu.oregonstate.eecs.iis.avatolcv.segmentation;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import edu.oregonstate.eecs.iis.avatolcv.core.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.core.ImageInfo;
import edu.oregonstate.eecs.iis.avatolcv.segmentation.files.DarwinDriverFile;

public class SegmentationSessionData {
	private static final String FILESEP = System.getProperty("file.separator");
	private static final String NL = System.getProperty("line.separator");
	private String parentDataDir = null;
	private String rootSegDir = null;
	private String cacheDir = null;
	private String labelDir = null;
	private String modelsDir = null;
	private String outputDir = null;
	private String segmentationDir = null;
	private String sourceImageDir = null;
	private List<ImageInfo> candidateImages = new ArrayList<ImageInfo>();
    private SegmentationImages si = null;
    
	public SegmentationSessionData(String parentDataDir){
		this.parentDataDir = parentDataDir;
		this.rootSegDir = this.parentDataDir + FILESEP + "seg";
		ensureDirExists(this.rootSegDir);
		this.cacheDir = this.rootSegDir + FILESEP + "cache";
		ensureDirExists(this.cacheDir);
		this.labelDir = this.rootSegDir + FILESEP + "trainingImages";
		ensureDirExists(this.labelDir);
		this.modelsDir = this.rootSegDir + FILESEP + "models";
		ensureDirExists(this.modelsDir);
		this.outputDir = this.rootSegDir + FILESEP + "output";
		ensureDirExists(this.outputDir);
	}
	
	public void createDarwinDriverFile() throws AvatolCVException {
		String segmentationRootDir = getSegmentationRootDir();
		DarwinDriverFile ddf = new DarwinDriverFile();
		ddf.setImageDir(getSourceImageDir());
		ddf.setCachedDir(getSegmentationCacheDir());
		ddf.setLabelDir(getSegmentationTrainingImageDir());
		ddf.setModelsDir(getSegmentationModelsDir());
		ddf.setOutputDir(getSegmentationOutputDir());
		ddf.setSegmentationDir(getSegmentationDir());
		String xml = ddf.getXMLContentString();
		String darwinDriverFilePath = segmentationRootDir + FILESEP + "darwinDriver.xml";
		File f = new File(darwinDriverFilePath);
		if (f.exists()){
			f.delete();
		}
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(darwinDriverFilePath));
			writer.write(xml + NL);
			writer.close();
		}
		catch(IOException ioe){
			throw new AvatolCVException("could not create darwin driver xml file");
		}
		
	}
	/*
	trainingImages_segmentation.txt and testingImages_segmentation.txt have entries that are the root names of images
	    00-5xayvrdPC3o5foKMpLbZ5H_imgXyz
	    03-uietIOuerto5foKMhUHYUh_imgAbc
    */
	public void createTrainingImageListFile() throws AvatolCVException {
		List<ImageInfo> images = this.si.getTrainingImages();
		String path = getTrainingImageFilePath();
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
			throw new AvatolCVException("problem creating training image list file: " + ioe.getMessage(),ioe);
		}	
	}
	
	public void createTestImageListFile() throws AvatolCVException {
		List<ImageInfo> images = this.si.getTestImages();
		String path = getTestImageFilePath();
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
			throw new AvatolCVException("problem creating test image list file: " + ioe.getMessage(),ioe);
		}	
	}
	public void createSegmentationConfigFile() throws AvatolCVException {
		String path = getConfigFilePath();
		File f = new File(path);
		if (f.exists()){
			f.delete();
		}
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(path));
			writer.write("darwinXMLFileDir=" + rootSegDir + NL);
			writer.write("trainingImagesFile=" + getTrainingImageFilePath() + NL);
			writer.write("testingImagesFile=" + getTestImageFilePath() + NL);
			writer.write("rawImagesDir=" + sourceImageDir + NL);
			writer.write("segmentationOutputDir=" + outputDir + NL);
			writer.write("modelXmlPath=" + rootSegDir + FILESEP + "model.xml" +NL);
			writer.write("trainingFileSuffix=_groundtruth" + NL);
			writer.write("outputFileSuffix=_mask" + NL);
			writer.write("#" + NL);
			for (ImageInfo ii : this.si.getTrainingImages()){
				writer.write("trainingImage="+ii.getFilename() + NL);
			}
			for (ImageInfo ii : this.si.getTestImages()){
				writer.write("testImage="+ii.getFilename() + NL);
			}
			writer.close();
		}
		catch(IOException ioe){
			throw new AvatolCVException("problem creating segmentation config file: " + ioe.getMessage(), ioe);
		}
		

	}

	public String getConfigFilePath(){
		return rootSegDir + FILESEP + "runConfig_segmentation.txt";
	}
	public String getTestImageFilePath(){
		return rootSegDir + FILESEP + "testingImages_segmentation.txt";
	}
	public String getTrainingImageFilePath(){
		return rootSegDir + FILESEP + "trainingImages_segmentation.txt";
	}
	public void ensureDirExists(String dir){
		File f = new File(dir);
		if (!f.isDirectory()){
			f.mkdirs();
		}
	}
	public int percentSegTrainingFileExist(){
		double trainingImageCount = this.si.getTrainingImages().size();
		double testImageCount = this.si.getTestImages().size();
		double total = testImageCount + trainingImageCount;
		int percent = (int)(100 * ((double)trainingImageCount / (double)total));
		return percent;
	}
	public void setSegmentationImages(SegmentationImages si){
		this.si = si;
	}
	public SegmentationImages getSegmentationImages(){
		return this.si;
	}
	public List<ImageInfo> getCandidateImages(){
		return this.candidateImages;
	}
	public void setSourceImageDir(String s){
		this.sourceImageDir = s;
		File dir = new File(this.sourceImageDir);
		File[] files = dir.listFiles();
		for (File f : files){
			String name = f.getName();
			if (!(name.equals(".") || name.equals(".."))){
				String[] parts = name.split("\\.");
				String root = parts[0];
				String extension = parts[1];
				String[] rootParts = root.split("_");
				String ID = rootParts[0];
				String nameAsUploaded = rootParts[1];
				String width = rootParts[2];
				ImageInfo ii = new ImageInfo(this.sourceImageDir, ID, nameAsUploaded, width, "", extension);
				this.candidateImages.add(ii);
			}
		}
	}
	public String getSegmentationRootDir(){
		return this.rootSegDir;
	}
    public String getSegmentationCacheDir(){
    	return this.cacheDir;
    }
    public String getSegmentationTrainingImageDir(){
    	return this.labelDir;
    }
	public String getSegmentationModelsDir(){
		return this.modelsDir;
	}
	public String getSegmentationOutputDir(){
		return this.outputDir;
	}
	public String getSegmentationDir(){
		return this.segmentationDir;
	}
	public String getSourceImageDir(){
		return this.sourceImageDir;
	}
}
