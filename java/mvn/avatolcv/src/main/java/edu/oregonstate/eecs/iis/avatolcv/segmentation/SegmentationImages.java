package edu.oregonstate.eecs.iis.avatolcv.segmentation;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import edu.oregonstate.eecs.iis.avatolcv.core.ImageInfo;

public class SegmentationImages {

	private List<ImageInfo> trainingImages = null;
	private List<ImageInfo> testImages = null;
	private List<ImageInfo> resultImages = null; 
	private String segTrainingImageDir = null;
	private String segOutputImageDir = null;
	private List<ImageInfo> candidateImages = null;
	public SegmentationImages(String segTrainingImageDir, String segOutputImageDir, List<ImageInfo> candidateImages) throws SegmentationException {
		this.segTrainingImageDir = segTrainingImageDir;
		this.segOutputImageDir = segOutputImageDir;
		this.candidateImages = candidateImages;
		File f = new File(this.segTrainingImageDir);
		if (!f.isDirectory()){
		    f.mkdirs();
		}
		f = new File(this.segOutputImageDir);
        if (!f.isDirectory()){
            f.mkdirs();
        }
	}
	public void reload()  throws SegmentationException {
		trainingImages = new ArrayList<ImageInfo>();
		testImages = new ArrayList<ImageInfo>();
		File segLabelDir = new File(segTrainingImageDir);
		if (!segLabelDir.isDirectory()){
			throw new SegmentationException("given segmentationLabelDir does not exist " + segTrainingImageDir);
		}
		File[] labelFiles = segLabelDir.listFiles();
		/*
		 * to avoid having to know about potential suffixes on the root names, search just by root names
		 */
		for (ImageInfo ii : candidateImages){
			String fileRootToLookFor  = ii.getFilename_IdName();
			boolean match = false;
			for (File f : labelFiles){
				if (f.getName().startsWith(fileRootToLookFor)){
					this.trainingImages.add(ii);
					match = true;
				}
			}
			if (!match){
				this.testImages.add(ii);
			}
		}
		
		resultImages = new ArrayList<ImageInfo>();
		File segOutputDir = new File(segOutputImageDir);
		if (!segOutputDir.isDirectory()){
			throw new SegmentationException("given segmentationOutputDir does not exist " + segOutputImageDir);
		}
		File[] outputFiles = segOutputDir.listFiles();
		
		for (ImageInfo ii : candidateImages){
			String fileRootToLookFor  = ii.getFilename_IdName();
			for (File f : labelFiles){
				if (f.getName().startsWith(fileRootToLookFor)){
					String filename = f.getName();
					String parentDir = f.getParent();
					String[] filenameParts = filename.split("\\.");
					String rootName = filenameParts[0];
					String extension = filenameParts[1];
					String[] rootNameParts = rootName.split("_");
					String ID = rootNameParts[0];
					String nameAsUploaded = rootNameParts[1];
					String imageWidth = rootNameParts[2];
					String outputType = rootNameParts[3];
					ImageInfo resultImageInfo = new ImageInfo(parentDir, ID, nameAsUploaded, imageWidth, outputType, extension);
					this.resultImages.add(resultImageInfo);
				}
			}
		}
		
	}
	public List<ImageInfo> getTrainingImages(){
		List<ImageInfo> result = new ArrayList<ImageInfo>();
		for (ImageInfo ii : trainingImages){
			result.add(ii);
		}
		return result;
	}
	public List<ImageInfo> getTestImages(){
		List<ImageInfo> result = new ArrayList<ImageInfo>();
		for (ImageInfo ii : testImages){
			result.add(ii);
		}
		return result;
	}
}
