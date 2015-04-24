package edu.oregonstate.eecs.iis.avatolcv.core;

import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public class ImagesForStage {
	 public static final String TRAINING_IMAGE = "train";
	    public static final String TEST_IMAGE = "test";
		private List<ImageInfo> trainingImages = null;
		private List<ImageInfo> testImages = null;
		private List<ImageInfo> resultImages = null; 
		private String trainingImageDirPath = null;
		private String outputImageDirPath = null;
		private List<ImageInfo> inPlayImages = null;
		private List<ImageInfo> disqualifiedImages = new ArrayList<ImageInfo>();
		private Hashtable<ImageInfo, String> disqualifiedSource = new Hashtable<ImageInfo, String>();
		private Hashtable<String, String> imageStatusForId = null;
		public ImagesForStage(String trainingImageDirPath, String outputImageDirPath, List<ImageInfo> candidateImages){
			this.trainingImageDirPath = trainingImageDirPath;
			this.outputImageDirPath = outputImageDirPath;
			this.inPlayImages = candidateImages;
			
			File f = new File(this.trainingImageDirPath);
			if (!f.isDirectory()){
			    f.mkdirs();
			}
			f = new File(this.outputImageDirPath);
	        if (!f.isDirectory()){
	            f.mkdirs();
	        }
		}
		public List<ImageInfo> getInPlayImages(){
			List<ImageInfo> result = new ArrayList<ImageInfo>();
			for (ImageInfo ii : this.inPlayImages){
				result.add(ii);
			}
			return result;
		}
		public List<ImageInfo> getDisqualifiedImages(){
			List<ImageInfo> result = new ArrayList<ImageInfo>();
			for (ImageInfo ii : this.disqualifiedImages){
				result.add(ii);
			}
			return result;
		}
		public void disqualifyImage(ImageInfo ii) throws AvatolCVException {
			ImageInfo target = null;
			for (ImageInfo image : inPlayImages){
				if (image.getID().equals(ii.getID())){
					target = image;
				}
			}
			if (null == target){
				throw new AvatolCVException("trying to disqualify image that is not present in the inPlayImages list " + ii.getID());
			}
			else {
				this.inPlayImages.remove(target);
			}
			this.disqualifiedImages.add(target);
			if (this.trainingImages.remove(target)){
				disqualifiedSource.put(target,TRAINING_IMAGE);
			}
			if (this.testImages.remove(target)){
				disqualifiedSource.put(target,TEST_IMAGE);
			}
		}
		public void requalifyImage(ImageInfo ii) throws AvatolCVException {
			if (!disqualifiedImages.contains(ii)){
				throw new AvatolCVException("Image " + ii.getFilename_IdNameWidth() + " not contained in disqualifiedImages list. Cannot requalify");
			}
			disqualifiedImages.remove(ii);
			inPlayImages.add(ii);
			String state = disqualifiedSource.get(ii);
			if (null == state){
				throw new AvatolCVException("Image " + ii.getFilename_IdNameWidth() + " not recalled as being disqualified in the disqualifiedSource hash.");
			}
			if (state.equals(TRAINING_IMAGE)){
				this.trainingImages.add(ii);
			}
			else if (state.equals(TEST_IMAGE)){
				this.testImages.add(ii);
			}
			else {
				throw new AvatolCVException("unknown state found for image " + ii.getFilename_IdNameWidth() + " in disqualifiedSource hash: " + state);
			}
		}
		public ImageInfo getTrainingImage(String ID) throws AvatolCVException {
	        for (ImageInfo ii : trainingImages){
	            if (ii.getID().equals(ID)){
	                return ii;
	            }
	        }
	        throw new AvatolCVException("ID " + ID + " is not a training image.");
	    }
	    public ImageInfo getResultImage(String ID) throws AvatolCVException{
	        for (ImageInfo ii : resultImages){
	            if (ii.getID().equals(ID)){
	                return ii;
	            }
	        }
	        throw new AvatolCVException("ID " + ID + " is not a result image.");
	    }
	    
		public String getStatusForImage(String ID) throws AvatolCVException{
		    if (!imageStatusForId.containsKey(ID)){
		        throw new AvatolCVException("no image status known for ID " + ID);
		    }
		    return imageStatusForId.get(ID);
		}
		public void reload()  throws AvatolCVException {
		    imageStatusForId = new Hashtable<String, String>();
			trainingImages = new ArrayList<ImageInfo>();
			testImages = new ArrayList<ImageInfo>();
			File trainingImageDir = new File(trainingImageDirPath);
			if (!trainingImageDir.isDirectory()){
				throw new AvatolCVException("given segmentationLabelDir does not exist " + trainingImageDirPath);
			}
			File[] trainingImageFiles = trainingImageDir.listFiles();
			/*
			 * to avoid having to know about potential suffixes on the root names, search just by root names
			 */
			for (ImageInfo ii : inPlayImages){
			    String ID = ii.getID();
				String fileRootToLookFor  = ii.getFilename_IdName();
				boolean match = false;
				for (File f : trainingImageFiles){
					if (f.getName().startsWith(fileRootToLookFor)){
						this.trainingImages.add(ii);
						imageStatusForId.put(ID, TRAINING_IMAGE);
						match = true;
					}
				}
				if (!match){
					this.testImages.add(ii);
					imageStatusForId.put(ID, TEST_IMAGE);
				}
			}
			
			resultImages = new ArrayList<ImageInfo>();
			File segOutputDir = new File(outputImageDirPath);
			if (!segOutputDir.isDirectory()){
				throw new AvatolCVException("given segmentationOutputDir does not exist " + outputImageDirPath);
			}
			File[] outputFiles = segOutputDir.listFiles();
			
			for (ImageInfo ii : inPlayImages){
				String fileRootToLookFor  = ii.getFilename_IdName();
				for (File f : trainingImageFiles){
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
