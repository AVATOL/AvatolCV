package edu.oregonstate.eecs.iis.avatolcv.segmentation;

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
import edu.oregonstate.eecs.iis.avatolcv.core.ImageTranformReviewData;
import edu.oregonstate.eecs.iis.avatolcv.core.ImagesForStage;
import edu.oregonstate.eecs.iis.avatolcv.generic.FileRootNameList;
import edu.oregonstate.eecs.iis.avatolcv.segmentation.files.DarwinDriverFile;
import edu.oregonstate.eecs.iis.avatolcv.segmentation.files.SegmentationRunConfig;

public class SegmentationSessionData implements ImageTranformReviewData {
	public static final String OUTPUT_TYPE_SUFFIX = "_groundtruth";
	private static final String FILESEP = System.getProperty("file.separator");
	private static final String NL = System.getProperty("line.separator");
	private String parentDataDir = null;
	private String rootSegDir = null;
	private String cacheDir = null;
	private String trainingImageDir = null;
	private String modelsDir = null;
	private String outputDir = null;
	private String segmentationDir = null;
	private String testImageDir = null;
	private List<ImageInfo> candidateImages = new ArrayList<ImageInfo>();
    private ImagesForStage ifs = null;
    
	public SegmentationSessionData(String parentDataDir, String testImageDir){
		this.parentDataDir = parentDataDir;
		this.testImageDir = testImageDir;
		loadCandidateImages();
		this.rootSegDir = this.parentDataDir + FILESEP + "seg";
		ensureDirExists(this.rootSegDir);
		this.cacheDir = this.rootSegDir + FILESEP + "cache";
		ensureDirExists(this.cacheDir);
		this.trainingImageDir = this.rootSegDir + FILESEP + "trainingImages";
		ensureDirExists(this.trainingImageDir);
		this.modelsDir = this.rootSegDir + FILESEP + "models";
		ensureDirExists(this.modelsDir);
		this.outputDir = this.rootSegDir + FILESEP + "output";
		ensureDirExists(this.outputDir);
	}
	public String getRootSegmentationDir(){
	    return this.rootSegDir;
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
	public void createDarwinDriverFile() throws AvatolCVException {
		String segmentationRootDir = getSegmentationRootDir();
		DarwinDriverFile ddf = new DarwinDriverFile();
		ddf.setImageDir(getTestImageDir());
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
	
	
	public void createTrainingImageListFile() throws AvatolCVException {
	    List<ImageInfo> images = this.ifs.getTrainingImages();
        String path = getTrainingImageFilePath();
        FileRootNameList sif = new FileRootNameList(path, images);
        sif.persist();
	}
	public void createTestImageListFile() throws AvatolCVException {
	    List<ImageInfo> images = this.ifs.getTestImagesPlusTrainingImageAncestors();
        String path = getTestImageFilePath();
        FileRootNameList sif = new FileRootNameList(path, images);
        sif.persist();
	}
	
	
	public void createSegmentationConfigFile() throws AvatolCVException {
	    SegmentationRunConfig src = new SegmentationRunConfig(this, this.ifs);
	    src.persist();
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
		double trainingImageCount = this.ifs.getTrainingImages().size();
		double testImageCount = this.ifs.getNonTrainingImages().size();
		double total = testImageCount + trainingImageCount;
		int percent = (int)(100 * ((double)trainingImageCount / (double)total));
		return percent;
	}
	public void loadCandidateImages(){
		File dir = new File(this.testImageDir);
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
				ImageInfo ii = new ImageInfo(this.testImageDir, ID, nameAsUploaded, width, "", extension);
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
    	return this.trainingImageDir;
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
	public String getTestImageDir(){
		return this.testImageDir;
	}

    public String getOutputDir(){
        return this.outputDir;
    }

    public void setImagesForStage(ImagesForStage ifs){
        this.ifs = ifs;
    }
    @Override
    public ImagesForStage getImagesForStage(){
        return this.ifs;
    }
    @Override
    public List<ImageInfo> getCandidateImages(){
        return this.candidateImages;
    }
    
    @Override
	public void deleteTrainingImage(ImageInfo ii)  throws AvatolCVException{
		String targetDir = getSegmentationTrainingImageDir();
		String targetPath = targetDir + FILESEP + ii.getFilename_IdNameWidth() + OUTPUT_TYPE_SUFFIX + "." + ii.getExtension();
		File f = new File(targetPath);
		if (f.exists()){
			f.delete();
		}
		this.ifs.reload();
	}
	@Override
	public void saveTrainingImage(BufferedImage bi, ImageInfo ii) throws AvatolCVException {
		String targetDir = getSegmentationTrainingImageDir();
		String targetPath = targetDir + FILESEP + ii.getFilename_IdNameWidth() + OUTPUT_TYPE_SUFFIX + "." + ii.getExtension();
		try {
		    File outputfile = new File(targetPath);
		    ImageIO.write(bi, ii.getExtension() , outputfile);
		    this.ifs.reload();
		} catch (IOException e) {
		    throw new AvatolCVException("could not save segmentation training image " + targetPath);
		}
	}
	@Override
	public void disqualifyImage(ImageInfo ii) throws AvatolCVException {
		this.ifs.disqualifyImage(ii);
	}
	@Override
	public void requalifyImage(ImageInfo ii) throws AvatolCVException {
		this.ifs.requalifyImage(ii);
	}
    
   
}
