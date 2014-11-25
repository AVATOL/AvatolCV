package edu.oregonstate.eecs.iis.avatolcv;
import org.junit.Assert;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class TestSessionData {
	private static final String SEP = System.getProperty("file.separator");
	private static final String NL = System.getProperty("line.separator");
	
	public void removerTempFile(String path){
		File f = new File(path);
		if (f.exists()){
			f.delete();
		}
	}
	public File makeTempFile(List<String> lines, String path) throws AvatolCVException {
		File f = new File(path);
		if (f.exists()){
			f.delete();
		}
		File parent = f.getParentFile();
		parent.mkdirs();
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(path));
			for (String line : lines){
				writer.write(line + NL);
			}
			writer.close();
		}
		catch(IOException ioe){
			throw new AvatolCVException(ioe.getMessage());
		}
		return f;
	}
	@Test
    public void testSessionDataNavigation_4_3_2(){
		try {
			List<File> tmpFiles = new ArrayList<File>();
			String charId = "c1";
			String charName = "charFoo";
			String rootDir = System.getProperty("user.dir");
			List<ResultImage> trainingImages = new ArrayList<ResultImage>();
			List<ResultImage> scoredImages = new ArrayList<ResultImage>();
			List<ResultImage> unscoredImages = new ArrayList<ResultImage>();
			

			//make the detection results files so scored images can be instantiated
			List<String> annotationLines = new ArrayList<String>();
			annotationLines.add("10,10:c1:charFoo:s1:present");
			annotationLines.add("20,20:c1:charFoo:s1:present");
			File annot1 = makeTempFile(annotationLines, rootDir + SEP + "detection_results" + SEP + "c1_m1.txt");
			tmpFiles.add(annot1);
		    
			String scoredLine1 = "image_scored|media/m1.jpg|s1|present|detection_results" + SEP + "c1_m1.txt|t6|1|0.7";
			String scoredLine2 = "image_scored|media/m1.jpg|s1|present|detection_results" + SEP + "c1_m1.txt|t6|2|0.8";
			String scoredLine3 = "image_scored|media/m3.jpg|s2|absent|NA|t6|NA|0.9";
			ScoredImage s1 = new ScoredImage(scoredLine1, rootDir, charId, charName);
			ScoredImage s2 = new ScoredImage(scoredLine2, rootDir, charId, charName);
			ScoredImage s3 = new ScoredImage(scoredLine3, rootDir, charId, charName);
			scoredImages.add(s1);
			scoredImages.add(s2);
			scoredImages.add(s3);
			
			String unscoredLine1 = "image_not_scored|media/m4.jpg|t6";
			String unscoredLine2 = "image_not_scored|media/m5.jpg|t6";
			UnscoredImage us1 = new UnscoredImage(unscoredLine1,rootDir, charId, charName);
			UnscoredImage us2 = new UnscoredImage(unscoredLine2,rootDir, charId, charName);
			unscoredImages.add(us1);
			unscoredImages.add(us2);
			
			//make the annotaion files so training exmples can be instantiated
			List<String> annotationLines2 = new ArrayList<String>();
			annotationLines2.add("10,10:c1:charFoo:s1:present");
			File annot2 = makeTempFile(annotationLines, rootDir + SEP + "annotations" + SEP + "c1_m6.txt");
			tmpFiles.add(annot2);
			
			List<String> annotationLines3 = new ArrayList<String>();
			annotationLines3.add("10,10:c1:charFoo:s1:present");
			File annot3 = makeTempFile(annotationLines, rootDir + SEP + "annotations" + SEP + "c1_m7.txt");
			tmpFiles.add(annot3);

			List<String> annotationLines4 = new ArrayList<String>();
			annotationLines4.add("10,10:c1:charFoo:s1:present");
			File annot4 = makeTempFile(annotationLines, rootDir + SEP + "annotations" + SEP + "c1_m8.txt");
			tmpFiles.add(annot4);
			
			String trainingLine1 = "training_data|media/m6.jpg|s1|present|annotations" + SEP + "c1_m6.txt|t6|1";
			String trainingLine2 = "training_data|media/m7.jpg|s1|present|annotations" + SEP + "c1_m7.txt|t6|1";
			String trainingLine3 = "training_data|media/m8.jpg|s1|present|annotations" + SEP + "c1_m8.txt|t6|1";
			String trainingLine4 = "training_data|media/m9.jpg|s2|absent|NA|t6|NA";
			TrainingSample ts1 = new TrainingSample(trainingLine1, rootDir, charId, charName);
			TrainingSample ts2 = new TrainingSample(trainingLine2, rootDir, charId, charName);
			TrainingSample ts3 = new TrainingSample(trainingLine3, rootDir, charId, charName);
			TrainingSample ts4 = new TrainingSample(trainingLine4, rootDir, charId, charName);
	    	trainingImages.add(ts1);
	    	trainingImages.add(ts2);
	    	trainingImages.add(ts3);
	    	trainingImages.add(ts4);
	    	SessionData sd = new SessionData(charId, charName, trainingImages, scoredImages, unscoredImages);
	    	
	        sd.setFocusAsTraining();
	        Assert.assertTrue(sd.isFocusTrainingData());
	        Assert.assertEquals(sd.getCurrentListSize(), 4);
	        Assert.assertTrue(sd.canShowImage());
	        sd.setFocusAsScored();
	        Assert.assertTrue(sd.isFocusScoredImages());
	        Assert.assertEquals(sd.getCurrentListSize(), 3);
	        Assert.assertTrue(sd.canShowImage());
	        sd.setFocusAsUnscored();
	        Assert.assertTrue(sd.isFocusUnscoredImages());
	        Assert.assertEquals(sd.getCurrentListSize(), 2);
	        Assert.assertTrue(sd.canShowImage());
	        
	        // navigate unscored forward
	        Assert.assertEquals(sd.getCurrentListIndex(), 0);
	        Assert.assertEquals(sd.getPositionInListString(),"1/2");
	        Assert.assertEquals(sd.getImageContextString(),"charFoo");
	        Assert.assertEquals(sd.getCurrentResultImage().getMediaPath(),rootDir + SEP + "media/m4.jpg");
	        Assert.assertTrue(sd.canGoToNextImage());
	        sd.goToNextImage();
	        Assert.assertEquals(sd.getCurrentListIndex(), 1);
	        Assert.assertEquals(sd.getPositionInListString(),"2/2");
	        Assert.assertEquals(sd.getImageContextString(),"charFoo");
	        Assert.assertEquals(sd.getCurrentResultImage().getMediaPath(),rootDir + SEP + "media/m5.jpg");
	        Assert.assertFalse(sd.canGoToNextImage());
	        // navigate unscored backward
	        Assert.assertTrue(sd.canGoToPrevImage());
	        sd.goToPrevImage();
	        Assert.assertEquals(sd.getCurrentListIndex(), 0);
	        Assert.assertEquals(sd.getPositionInListString(),"1/2");
	        Assert.assertEquals(sd.getImageContextString(),"charFoo");
	        Assert.assertEquals(sd.getCurrentResultImage().getMediaPath(),rootDir + SEP + "media/m4.jpg");
	        Assert.assertFalse(sd.canGoToPrevImage());
	        
	        // navigate scored forward
	        sd.setFocusAsScored();
	        Assert.assertEquals(sd.getCurrentListIndex(), 0);
	        Assert.assertEquals(sd.getPositionInListString(),"1/3");
	        Assert.assertEquals(sd.getImageContextString(),"charFoo , present");
	        Assert.assertEquals(sd.getCurrentResultImage().getMediaPath(),rootDir + SEP + "media/m1.jpg");
	        Assert.assertTrue(sd.canGoToNextImage());
	        sd.goToNextImage();
	        Assert.assertEquals(sd.getCurrentListIndex(), 1);
	        Assert.assertEquals(sd.getPositionInListString(),"2/3");
	        Assert.assertEquals(sd.getImageContextString(),"charFoo , present");
	        Assert.assertEquals(sd.getCurrentResultImage().getMediaPath(),rootDir + SEP + "media/m1.jpg");
	        Assert.assertTrue(sd.canGoToNextImage());
	        sd.goToNextImage();
	        Assert.assertEquals(sd.getCurrentListIndex(), 2);
	        Assert.assertEquals(sd.getPositionInListString(),"3/3");
	        Assert.assertEquals(sd.getImageContextString(),"charFoo , absent");
	        Assert.assertEquals(sd.getCurrentResultImage().getMediaPath(),rootDir + SEP + "media/m3.jpg");
	        Assert.assertFalse(sd.canGoToNextImage());
	        // navigate scored backward
	        Assert.assertTrue(sd.canGoToPrevImage());
	        sd.goToPrevImage();
	        Assert.assertEquals(sd.getCurrentListIndex(), 1);
	        Assert.assertEquals(sd.getPositionInListString(),"2/3");
	        Assert.assertEquals(sd.getImageContextString(),"charFoo , present");
	        Assert.assertEquals(sd.getCurrentResultImage().getMediaPath(),rootDir + SEP + "media/m1.jpg");
	        Assert.assertTrue(sd.canGoToPrevImage());
	        sd.goToPrevImage();
	        Assert.assertEquals(sd.getCurrentListIndex(), 0);
	        Assert.assertEquals(sd.getPositionInListString(),"1/3");
	        Assert.assertEquals(sd.getImageContextString(),"charFoo , present");
	        Assert.assertEquals(sd.getCurrentResultImage().getMediaPath(),rootDir + SEP + "media/m1.jpg");
	        Assert.assertFalse(sd.canGoToPrevImage());
	        
	        // navigate training forward
	        sd.setFocusAsTraining();
	        Assert.assertEquals(sd.getCurrentListIndex(), 0);
	        Assert.assertEquals(sd.getPositionInListString(),"1/4");
	        Assert.assertEquals(sd.getImageContextString(),"charFoo , present");
	        Assert.assertEquals(sd.getCurrentResultImage().getMediaPath(),rootDir + SEP + "media/m6.jpg");
	        Assert.assertTrue(sd.canGoToNextImage());
	        sd.goToNextImage();
	        Assert.assertEquals(sd.getCurrentListIndex(), 1);
	        Assert.assertEquals(sd.getPositionInListString(),"2/4");
	        Assert.assertEquals(sd.getImageContextString(),"charFoo , present");
	        Assert.assertEquals(sd.getCurrentResultImage().getMediaPath(),rootDir + SEP + "media/m7.jpg");
	        Assert.assertTrue(sd.canGoToNextImage());
	        sd.goToNextImage();
	        Assert.assertEquals(sd.getCurrentListIndex(), 2);
	        Assert.assertEquals(sd.getPositionInListString(),"3/4");
	        Assert.assertEquals(sd.getImageContextString(),"charFoo , present");
	        Assert.assertEquals(sd.getCurrentResultImage().getMediaPath(),rootDir + SEP + "media/m8.jpg");
	        Assert.assertTrue(sd.canGoToNextImage());
	        sd.goToNextImage();
	        Assert.assertEquals(sd.getCurrentListIndex(), 3);
	        Assert.assertEquals(sd.getPositionInListString(),"4/4");
	        Assert.assertEquals(sd.getImageContextString(),"charFoo , absent");
	        Assert.assertEquals(sd.getCurrentResultImage().getMediaPath(),rootDir + SEP + "media/m9.jpg");
	        Assert.assertFalse(sd.canGoToNextImage());
	        // navigate training backward
	        Assert.assertTrue(sd.canGoToPrevImage());
	        sd.goToPrevImage();
	        Assert.assertEquals(sd.getCurrentListIndex(), 2);
	        Assert.assertEquals(sd.getPositionInListString(),"3/4");
	        Assert.assertEquals(sd.getImageContextString(),"charFoo , present");
	        Assert.assertEquals(sd.getCurrentResultImage().getMediaPath(),rootDir + SEP + "media/m8.jpg");
	        Assert.assertTrue(sd.canGoToPrevImage());
	        sd.goToPrevImage();
	        Assert.assertEquals(sd.getCurrentListIndex(), 1);
	        Assert.assertEquals(sd.getPositionInListString(),"2/4");
	        Assert.assertEquals(sd.getImageContextString(),"charFoo , present");
	        Assert.assertEquals(sd.getCurrentResultImage().getMediaPath(),rootDir + SEP + "media/m7.jpg");
	        Assert.assertTrue(sd.canGoToPrevImage());
	        sd.goToPrevImage();
	        Assert.assertEquals(sd.getCurrentListIndex(), 0);
	        Assert.assertEquals(sd.getPositionInListString(),"1/4");
	        Assert.assertEquals(sd.getImageContextString(),"charFoo , present");
	        Assert.assertEquals(sd.getCurrentResultImage().getMediaPath(),rootDir + SEP + "media/m6.jpg");
	        Assert.assertFalse(sd.canGoToPrevImage());
	        
	        for (File curFile : tmpFiles){
	        	curFile.delete();
	        }
	        
		}
		catch(AvatolCVException e){
			Assert.fail(e.getMessage());
		}
		
    	
    }
	
	@Test
    public void testSessionDataNavigation_4_3_0(){
		try {
			List<File> tmpFiles = new ArrayList<File>();
			String charId = "c1";
			String charName = "charFoo";
			String rootDir = System.getProperty("user.dir");
			List<ResultImage> trainingImages = new ArrayList<ResultImage>();
			List<ResultImage> scoredImages = new ArrayList<ResultImage>();
			List<ResultImage> unscoredImages = new ArrayList<ResultImage>();
			

			//make the detection results files so scored images can be instantiated
			List<String> annotationLines = new ArrayList<String>();
			annotationLines.add("10,10:c1:charFoo:s1:present");
			annotationLines.add("20,20:c1:charFoo:s1:present");
			File annot1 = makeTempFile(annotationLines, rootDir + SEP + "detection_results" + SEP + "c1_m1.txt");
			tmpFiles.add(annot1);
		    
			String scoredLine1 = "image_scored|media/m1.jpg|s1|present|detection_results" + SEP + "c1_m1.txt|t6|1|0.7";
			String scoredLine2 = "image_scored|media/m1.jpg|s1|present|detection_results" + SEP + "c1_m1.txt|t6|2|0.8";
			String scoredLine3 = "image_scored|media/m3.jpg|s2|absent|NA|t6|NA|0.9";
			ScoredImage s1 = new ScoredImage(scoredLine1, rootDir, charId, charName);
			ScoredImage s2 = new ScoredImage(scoredLine2, rootDir, charId, charName);
			ScoredImage s3 = new ScoredImage(scoredLine3, rootDir, charId, charName);
			scoredImages.add(s1);
			scoredImages.add(s2);
			scoredImages.add(s3);
			
			
			
			//make the annotation files so training exmples can be instantiated
			List<String> annotationLines2 = new ArrayList<String>();
			annotationLines2.add("10,10:c1:charFoo:s1:present");
			File annot2 = makeTempFile(annotationLines, rootDir + SEP + "annotations" + SEP + "c1_m6.txt");
			tmpFiles.add(annot2);
			
			List<String> annotationLines3 = new ArrayList<String>();
			annotationLines3.add("10,10:c1:charFoo:s1:present");
			File annot3 = makeTempFile(annotationLines, rootDir + SEP + "annotations" + SEP + "c1_m7.txt");
			tmpFiles.add(annot3);

			List<String> annotationLines4 = new ArrayList<String>();
			annotationLines4.add("10,10:c1:charFoo:s1:present");
			File annot4 = makeTempFile(annotationLines, rootDir + SEP + "annotations" + SEP + "c1_m8.txt");
			tmpFiles.add(annot4);
			
			String trainingLine1 = "training_data|media/m6.jpg|s1|present|annotations" + SEP + "c1_m6.txt|t6|1";
			String trainingLine2 = "training_data|media/m7.jpg|s1|present|annotations" + SEP + "c1_m7.txt|t6|1";
			String trainingLine3 = "training_data|media/m8.jpg|s1|present|annotations" + SEP + "c1_m8.txt|t6|1";
			String trainingLine4 = "training_data|media/m9.jpg|s2|absent|NA|t6|NA";
			TrainingSample ts1 = new TrainingSample(trainingLine1, rootDir, charId, charName);
			TrainingSample ts2 = new TrainingSample(trainingLine2, rootDir, charId, charName);
			TrainingSample ts3 = new TrainingSample(trainingLine3, rootDir, charId, charName);
			TrainingSample ts4 = new TrainingSample(trainingLine4, rootDir, charId, charName);
	    	trainingImages.add(ts1);
	    	trainingImages.add(ts2);
	    	trainingImages.add(ts3);
	    	trainingImages.add(ts4);
	    	SessionData sd = new SessionData(charId, charName, trainingImages, scoredImages, unscoredImages);
	    	
	        sd.setFocusAsTraining();
	        Assert.assertTrue(sd.isFocusTrainingData());
	        Assert.assertEquals(sd.getCurrentListSize(), 4);
	        Assert.assertTrue(sd.canShowImage());
	        sd.setFocusAsScored();
	        Assert.assertTrue(sd.isFocusScoredImages());
	        Assert.assertEquals(sd.getCurrentListSize(), 3);
	        Assert.assertTrue(sd.canShowImage());
	        sd.setFocusAsUnscored();
	        Assert.assertTrue(sd.isFocusUnscoredImages());
	        Assert.assertEquals(sd.getCurrentListSize(), 0);
	        Assert.assertFalse(sd.canShowImage());
	        
	        // no unscored 
	        Assert.assertEquals(sd.getCurrentListIndex(), 0);
	        Assert.assertEquals(sd.getPositionInListString(),"");
	        Assert.assertEquals(sd.getImageContextString(),"");
	        
	        // navigate scored forward
	        sd.setFocusAsScored();
	        Assert.assertEquals(sd.getCurrentListIndex(), 0);
	        Assert.assertEquals(sd.getPositionInListString(),"1/3");
	        Assert.assertEquals(sd.getImageContextString(),"charFoo , present");
	        Assert.assertEquals(sd.getCurrentResultImage().getMediaPath(),rootDir + SEP + "media/m1.jpg");
	        Assert.assertTrue(sd.canGoToNextImage());
	        sd.goToNextImage();
	        Assert.assertEquals(sd.getCurrentListIndex(), 1);
	        Assert.assertEquals(sd.getPositionInListString(),"2/3");
	        Assert.assertEquals(sd.getImageContextString(),"charFoo , present");
	        Assert.assertEquals(sd.getCurrentResultImage().getMediaPath(),rootDir + SEP + "media/m1.jpg");
	        Assert.assertTrue(sd.canGoToNextImage());
	        sd.goToNextImage();
	        Assert.assertEquals(sd.getCurrentListIndex(), 2);
	        Assert.assertEquals(sd.getPositionInListString(),"3/3");
	        Assert.assertEquals(sd.getImageContextString(),"charFoo , absent");
	        Assert.assertEquals(sd.getCurrentResultImage().getMediaPath(),rootDir + SEP + "media/m3.jpg");
	        Assert.assertFalse(sd.canGoToNextImage());
	        // navigate scored backward
	        Assert.assertTrue(sd.canGoToPrevImage());
	        sd.goToPrevImage();
	        Assert.assertEquals(sd.getCurrentListIndex(), 1);
	        Assert.assertEquals(sd.getPositionInListString(),"2/3");
	        Assert.assertEquals(sd.getImageContextString(),"charFoo , present");
	        Assert.assertEquals(sd.getCurrentResultImage().getMediaPath(),rootDir + SEP + "media/m1.jpg");
	        Assert.assertTrue(sd.canGoToPrevImage());
	        sd.goToPrevImage();
	        Assert.assertEquals(sd.getCurrentListIndex(), 0);
	        Assert.assertEquals(sd.getPositionInListString(),"1/3");
	        Assert.assertEquals(sd.getImageContextString(),"charFoo , present");
	        Assert.assertEquals(sd.getCurrentResultImage().getMediaPath(),rootDir + SEP + "media/m1.jpg");
	        Assert.assertFalse(sd.canGoToPrevImage());
	        
	        // navigate training forward
	        sd.setFocusAsTraining();
	        Assert.assertEquals(sd.getCurrentListIndex(), 0);
	        Assert.assertEquals(sd.getPositionInListString(),"1/4");
	        Assert.assertEquals(sd.getImageContextString(),"charFoo , present");
	        Assert.assertEquals(sd.getCurrentResultImage().getMediaPath(),rootDir + SEP + "media/m6.jpg");
	        Assert.assertTrue(sd.canGoToNextImage());
	        sd.goToNextImage();
	        Assert.assertEquals(sd.getCurrentListIndex(), 1);
	        Assert.assertEquals(sd.getPositionInListString(),"2/4");
	        Assert.assertEquals(sd.getImageContextString(),"charFoo , present");
	        Assert.assertEquals(sd.getCurrentResultImage().getMediaPath(),rootDir + SEP + "media/m7.jpg");
	        Assert.assertTrue(sd.canGoToNextImage());
	        sd.goToNextImage();
	        Assert.assertEquals(sd.getCurrentListIndex(), 2);
	        Assert.assertEquals(sd.getPositionInListString(),"3/4");
	        Assert.assertEquals(sd.getImageContextString(),"charFoo , present");
	        Assert.assertEquals(sd.getCurrentResultImage().getMediaPath(),rootDir + SEP + "media/m8.jpg");
	        Assert.assertTrue(sd.canGoToNextImage());
	        sd.goToNextImage();
	        Assert.assertEquals(sd.getCurrentListIndex(), 3);
	        Assert.assertEquals(sd.getPositionInListString(),"4/4");
	        Assert.assertEquals(sd.getImageContextString(),"charFoo , absent");
	        Assert.assertEquals(sd.getCurrentResultImage().getMediaPath(),rootDir + SEP + "media/m9.jpg");
	        Assert.assertFalse(sd.canGoToNextImage());
	        // navigate training backward
	        Assert.assertTrue(sd.canGoToPrevImage());
	        sd.goToPrevImage();
	        Assert.assertEquals(sd.getCurrentListIndex(), 2);
	        Assert.assertEquals(sd.getPositionInListString(),"3/4");
	        Assert.assertEquals(sd.getImageContextString(),"charFoo , present");
	        Assert.assertEquals(sd.getCurrentResultImage().getMediaPath(),rootDir + SEP + "media/m8.jpg");
	        Assert.assertTrue(sd.canGoToPrevImage());
	        sd.goToPrevImage();
	        Assert.assertEquals(sd.getCurrentListIndex(), 1);
	        Assert.assertEquals(sd.getPositionInListString(),"2/4");
	        Assert.assertEquals(sd.getImageContextString(),"charFoo , present");
	        Assert.assertEquals(sd.getCurrentResultImage().getMediaPath(),rootDir + SEP + "media/m7.jpg");
	        Assert.assertTrue(sd.canGoToPrevImage());
	        sd.goToPrevImage();
	        Assert.assertEquals(sd.getCurrentListIndex(), 0);
	        Assert.assertEquals(sd.getPositionInListString(),"1/4");
	        Assert.assertEquals(sd.getImageContextString(),"charFoo , present");
	        Assert.assertEquals(sd.getCurrentResultImage().getMediaPath(),rootDir + SEP + "media/m6.jpg");
	        Assert.assertFalse(sd.canGoToPrevImage());
	        
	        for (File curFile : tmpFiles){
	        	curFile.delete();
	        }
	        
		}
		catch(AvatolCVException e){
			Assert.fail(e.getMessage());
		}
		
    	
    }
		
}
