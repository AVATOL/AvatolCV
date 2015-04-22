package edu.oregonstate.eecs.iis.avatolcv.segmentation;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import edu.oregonstate.eecs.iis.avatolcv.core.ImageInfo;

public class SegmentationImages {

	private List<ImageInfo> trainingImages = null;
	private List<ImageInfo> testImages = null;
	private String segTrainingImageDir = null;
	private List<ImageInfo> candidateImages = null;
	public SegmentationImages(String segTrainingImageDir, List<ImageInfo> candidateImages) throws SegmentationException {
		this.segTrainingImageDir = segTrainingImageDir;
		this.candidateImages = candidateImages;
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
