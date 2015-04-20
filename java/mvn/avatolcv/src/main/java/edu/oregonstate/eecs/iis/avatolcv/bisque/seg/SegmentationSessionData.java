package edu.oregonstate.eecs.iis.avatolcv.bisque.seg;

import java.io.File;

public class SegmentationSessionData {
	private static final String FILESEP = System.getProperty("file.separator");
	private String parentDataDir = null;
	private String rootSegDir = null;
	private String cacheDir = null;
	private String labelDir = null;
	private String modelsDir = null;
	private String outputDir = null;
	private String segmentationDir = null;
	public SegmentationSessionData(String parentDataDir){
		this.parentDataDir = parentDataDir;
		this.rootSegDir = this.parentDataDir + FILESEP + "seg";
		File f = new File(rootSegDir);
		if (!f.isDirectory()){
			f.mkdirs();
		}
		this.cacheDir = this.rootSegDir + FILESEP + "cache";
		this.labelDir = this.rootSegDir + FILESEP + "labels";
		this.modelsDir = this.rootSegDir + FILESEP + "models";
		this.modelsDir = this.rootSegDir + FILESEP + "output";
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
	
}
