package edu.oregonstate.eecs.iis.avatolcv.core;

import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import edu.oregonstate.eecs.iis.avatolcv.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.util.ClassicSplitter;

public class SegmentationResults {
	public static final String FILESEP = System.getProperty("file.separator");
	public static final String CROPPED_MASK_SUFFIX = "_croppedMask";
	public static final String CROPPED_ORIG_SUFFIX = "_croppedOrig";
	private Hashtable<String, String> fileDescriptorForRoot = new Hashtable<String, String>();
	private List<SegmentationOutputPair> segOutPairs = new ArrayList<SegmentationOutputPair>();
	public SegmentationResults(String outputDir) throws AvatolCVException {
		File dirFile = new File(outputDir);
		if (!dirFile.exists()){
			throw new AvatolCVException("segmentation results directory does not exist");
		}
		File[] files = dirFile.listFiles();
		List<String> rootNames = new ArrayList<String>();
		for (File f : files){
			
			String nameLowerCase = f.getName().toLowerCase();
			if (nameLowerCase.endsWith(".jpg") || nameLowerCase.endsWith(".png")){
				String rootName = f.getName().replaceAll(CROPPED_MASK_SUFFIX, "");
				fileDescriptorForRoot.put(rootName,getFileType(f.getName()));
			    rootName = rootName.replaceAll(CROPPED_ORIG_SUFFIX, "");
			    if (!rootNames.contains(rootName)){
			    	rootNames.add(rootName);
			    }
			}
		}
		for (String rootName : rootNames){
			String type = fileDescriptorForRoot.get(rootName);
			String origPath = outputDir + FILESEP + rootName + CROPPED_ORIG_SUFFIX + type;
			String maskPath = outputDir + FILESEP + rootName + CROPPED_MASK_SUFFIX + type;
			File origFile = new File(origPath);
			boolean origExists = origFile.exists();
			if (!origExists){
				System.out.println("origPath missing "  + origPath);
			}
			File maskFile = new File(maskPath);
			boolean maskExists = maskFile.exists();
			if (!maskExists){
				System.out.println("maskPath missing "  + maskPath);
			}
			if (maskExists & origExists){
				SegmentationOutputPair sop = new SegmentationOutputPair(origPath, maskPath);
				segOutPairs.add(sop);
			}
			
		}
	}
	private String getFileType(String s){
		String[] parts = ClassicSplitter.splitt(s,'.');
		int partsCount = parts.length;
		String type = parts[partsCount - 1];
		return type;
	}
	public class SegmentationOutputPair{
		private String orig;
		private String mask;
	    public SegmentationOutputPair(String orig, String mask){
	    	this.orig = orig;
	    	this.mask = mask;
	    }
		public String getCroppedOrigPath(){
			return orig;
		}
		public String getCroppedMaskPath(){
			return mask;
		}
	}
}
