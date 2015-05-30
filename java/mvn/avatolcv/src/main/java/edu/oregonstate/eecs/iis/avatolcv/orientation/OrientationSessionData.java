package edu.oregonstate.eecs.iis.avatolcv.orientation;


import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import edu.oregonstate.eecs.iis.avatolcv.core.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.core.ImageInfo;
import edu.oregonstate.eecs.iis.avatolcv.core.ImageTranformReviewData;
import edu.oregonstate.eecs.iis.avatolcv.core.ImagesForStage;
import edu.oregonstate.eecs.iis.avatolcv.generic.FileRootNameList;
import edu.oregonstate.eecs.iis.avatolcv.orientation.files.OrientationRunConfig;

public class OrientationSessionData implements ImageTranformReviewData {
	public static String TYPE_SUFFIX_INPUT = "toBeSetByConstructor";
	public static final String TYPE_SUFFIX_OUTPUT = "rotated";
	private static final String FILESEP = System.getProperty("file.separator");
	private String parentDataDir = null;
	private String rootOrientationDir = null;
	private String trainingImageDir = null;
	private String outputDir = null;
	private String imagesToOrientDir = null;
	private String rawImagesDir = null;
    private ImagesForStage ifs = null;
    private List<ImageInfo> candidateImages = new ArrayList<ImageInfo>();
    
	public OrientationSessionData(String parentDataDir, String rawImagesDir, String imagesToOrientDir, String inputTypeSuffix) throws AvatolCVException  {
	    TYPE_SUFFIX_INPUT = inputTypeSuffix;
		this.parentDataDir = parentDataDir;
		this.rawImagesDir = rawImagesDir;
		this.imagesToOrientDir = imagesToOrientDir;
		loadCandidateImages();
		this.rootOrientationDir = this.parentDataDir + FILESEP + "orient";
		ensureDirExists(this.rootOrientationDir);
		this.outputDir = this.rootOrientationDir + FILESEP + "output";
		ensureDirExists(this.outputDir);
		this.trainingImageDir = this.rootOrientationDir + FILESEP + "trainingImages";
        ensureDirExists(this.trainingImageDir);
	}
	public String getTrainingImageDir(){
	    return this.trainingImageDir;
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
	
	private void loadCandidateImages() throws AvatolCVException {
        File dir = new File(this.imagesToOrientDir);
        File[] files = dir.listFiles();
        for (File f : files){
            String name = f.getName();
            if (!(name.equals(".") || name.equals(".."))){
                ImageInfo ii = ImageInfo.loadImageInfoFromFilename(name, f.getParent());
                this.candidateImages.add(ii);
            }
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
	
	public void createOrientationConfigFile() throws AvatolCVException {
	    OrientationRunConfig orc = new OrientationRunConfig(this, this.ifs);
	    orc.persist();
	}
    public String getRawImagesDir(){
        return this.rawImagesDir;
    }
    public String getOutputDir(){
        return this.outputDir;
    }
	public String getModelFilePath(){
		return rootOrientationDir + FILESEP + "orientationModel.txt";
	}
	public String getConfigFilePath(){
		return rootOrientationDir + FILESEP + "runConfig_orientation.txt";
	}

    public String getTestImageFilePath(){
        return rootOrientationDir + FILESEP + "testingImages_orientation.txt";
    }
    public String getTrainingImageFilePath(){
        return rootOrientationDir + FILESEP + "trainingImages_orientation.txt";
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
			
	public void setImagesForStage(ImagesForStage ifs){
		this.ifs = ifs;
	}
	public List<ImageInfo> getInPlayRawImages(){
		return this.ifs.getInPlayImages();
	}
	
	public String getOrientationOutputDir(){
		return this.outputDir;
	}
	public String getTestImageDir(){
		return this.imagesToOrientDir;
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
	public void disqualifyImage(ImageInfo ii) throws AvatolCVException {
		this.ifs.disqualifyImage(ii);
	}
	@Override
	public void requalifyImage(ImageInfo ii) throws AvatolCVException {
		this.ifs.requalifyImage(ii);
	}
    @Override
    public void deleteTrainingImage(ImageInfo ii) throws AvatolCVException  {
        String targetDir = getTrainingImageDir();
        String targetPath = targetDir + FILESEP + ii.getFilename_IdNameWidth() + TYPE_SUFFIX_OUTPUT + "." + ii.getExtension();
        File f = new File(targetPath);
        if (f.exists()){
            f.delete();
        }
        this.ifs.reload();
        
    }
    @Override
    public void saveTrainingImage(BufferedImage bi, ImageInfo ii) throws AvatolCVException {
        String targetDir = getTrainingImageDir();
        String targetPath = targetDir + FILESEP + ii.getFilename_IdNameWidth() + TYPE_SUFFIX_OUTPUT + "." + ii.getExtension();
        try {
            File outputfile = new File(targetPath);
            ImageIO.write(bi, ii.getExtension() , outputfile);
            this.ifs.reload();
        } catch (IOException e) {
            throw new AvatolCVException("could not save segmentation training image " + targetPath);
        }
        
    }
}
