package edu.oregonstate.eecs.iis.avatolcv.segmentation;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import edu.oregonstate.eecs.iis.avatolcv.core.ImageInfo;

public class SegmentationSessionData {
	private static final String FILESEP = System.getProperty("file.separator");
	private String parentDataDir = null;
	private String rootSegDir = null;
	private String cacheDir = null;
	private String labelDir = null;
	private String modelsDir = null;
	private String outputDir = null;
	private String segmentationDir = null;
	private String sourceImageDir = null;
	private List<ImageInfo> candidateImages = new ArrayList<ImageInfo>();
    private List<ImageInfo> trainingImages = new ArrayList<ImageInfo>();
    private List<ImageInfo> testImages = new ArrayList<ImageInfo>();
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
	public void ensureDirExists(String dir){
		File f = new File(dir);
		if (!f.isDirectory()){
			f.mkdirs();
		}
	}
	public int percentSegTrainingFileExist(){
		double trainingImageCount = this.trainingImages.size();
		double testImageCount = this.testImages.size();
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
				ImageInfo ii = new ImageInfo(this.sourceImageDir, ID, nameAsUploaded, width, extension);
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
    public String getSegmentationLabelDir(){
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
