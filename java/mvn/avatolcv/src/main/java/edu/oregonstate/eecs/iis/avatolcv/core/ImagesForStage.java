package edu.oregonstate.eecs.iis.avatolcv.core;

import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
/*
 * ImagesForStage is a generic image file handler that each tool can use.  
 * - it starts out with candidate images
 * - images can be disqualified or re-qualified
 * - it maintains lists of training, test and result images
 */
public class ImagesForStage {
	 public static final String TRAINING_IMAGE = "train";
	    public static final String TEST_IMAGE = "test";
		private List<ImageInfo> trainingImages = new ArrayList<ImageInfo>();
		private List<ImageInfo> testImages = new ArrayList<ImageInfo>();
		private List<ImageInfo> resultImages = new ArrayList<ImageInfo>(); 
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
		/*
		 * First, find training files that match inPlayImages reference list
		 * Use their presence/absence to divide inPlayImage list entries into training and test image lists ,
		 * keeping track of their assignment with the imagesStatusForId hash.
		 */
		public void reload()  throws AvatolCVException {
		    imageStatusForId = new Hashtable<String, String>();
			trainingImages = new ArrayList<ImageInfo>();
			testImages = new ArrayList<ImageInfo>();
			
			/*
			 * sense the training image files now in play
			 */
			File trainingImageDir = new File(trainingImageDirPath);
            if (!trainingImageDir.isDirectory()){
                throw new AvatolCVException("training image dir does not exist " + trainingImageDirPath);
            }
            File[] trainingImageFiles = trainingImageDir.listFiles();
			for (ImageInfo ii : inPlayImages){
			    String ID = ii.getID();
				String fileRootToLookFor  = ii.getFilename_IdName();
				boolean match = false;
				for (File f : trainingImageFiles){
					if (f.getName().startsWith(fileRootToLookFor)){
					    ImageInfo trainingImage = ImageInfo.loadImageInfoFromFilename(f.getName(), trainingImageDirPath);
						this.trainingImages.add(trainingImage);
						imageStatusForId.put(ID, TRAINING_IMAGE);
						match = true;
					}
				}
				if (!match){
					this.testImages.add(ii);
					imageStatusForId.put(ID, TEST_IMAGE);
				}
			}
			if (testImages.size() == 0){
				throw new AvatolCVException("test image directory is empty.");
			}
			/*
	         * Some stages are preparatory stages to get images ready for character scoring.
	         * In order for the images that were used for training at one of the prep stages to be 
	         * available to the following stage, we can't leave them behind at that stage.  If 
	         * we did leave them behind, each stage would remove "good" images from the final 
	         * set that will be scored for character state
	         * 
	         * Thus, we have the concept of ancestor images. An ancestor image of a training image is the 
	         * raw image that was used to create the training image.  These can be consulted later to help
	         * build the full testing list - the test images plus the ancestors of the training images.
	         */
			
			/*
			 * Set ancestor images on the training images
			 * - assume parent dir should be same as test image
			 * - assume outputType should be same as test image
			 */
			String parentDirFromTestImage = testImages.get(0).getParentDir();
			String outputTypeFromTestImage = testImages.get(0).getOutputType();
			for (ImageInfo tii : this.trainingImages){
			    ImageInfo ancestorImage = new ImageInfo(parentDirFromTestImage, tii.getID(), tii.getNameAsUploaded(), tii.getImageWidth(), outputTypeFromTestImage, tii.getExtension());
			    tii.setAncestorImage(ancestorImage);
			}
			/*
			 * sense the result files now in play
			 */
			File segOutputDir = new File(outputImageDirPath);
			if (!segOutputDir.isDirectory()){
				throw new AvatolCVException("given segmentationOutputDir does not exist " + outputImageDirPath);
			}
			File[] outputFiles = segOutputDir.listFiles();
	        resultImages = new ArrayList<ImageInfo>();

			for (ImageInfo ii : inPlayImages){   // for each in play image
				String fileRootToLookFor  = ii.getFilename_IdName();
				for (File f : outputFiles){
					if (f.getName().startsWith(fileRootToLookFor)){ // find the associated training image, if exists
						String filename = f.getName();
						String parentDir = f.getParent();
						ImageInfo outputImageInfo = ImageInfo.loadImageInfoFromFilename(filename, parentDir);
						this.resultImages.add(outputImageInfo);
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
		
		public List<ImageInfo> getTestImagesPlusTrainingImageAncestors() throws AvatolCVException {
		    List<ImageInfo> result = getNonTrainingImages();
		    for (ImageInfo ii : trainingImages){
		        ImageInfo ancestor = ii.getAncestorImage();
		        if (null == ancestor){
		            throw new AvatolCVException("training image does not have ancestor image recorded");
		        }
		        result.add(ancestor);
		    }
		    return result;
		}
		public List<ImageInfo> getNonTrainingImages(){
			List<ImageInfo> result = new ArrayList<ImageInfo>();
			for (ImageInfo ii : testImages){
				result.add(ii);
			}
			return result;
		}
}
