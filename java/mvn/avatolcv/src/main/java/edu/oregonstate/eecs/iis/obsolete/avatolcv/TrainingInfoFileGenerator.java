package edu.oregonstate.eecs.iis.obsolete.avatolcv;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import edu.oregonstate.eecs.iis.avatolcv.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.AvatolCVFileSystem;
import edu.oregonstate.eecs.iis.avatolcv.core.TrainingInfoFile;
import edu.oregonstate.eecs.iis.avatolcv.normalized.NormalizedImageInfoScored;
import edu.oregonstate.eecs.iis.avatolcv.scoring.ScoreIndex;
import edu.oregonstate.eecs.iis.avatolcv.util.FileSystemPrimer;

public class TrainingInfoFileGenerator {
	private ScoreIndex scoreIndex;
	public static void main(String[] args){
		String sessionID = "20150927_01";
		String scoringConcern = "leaf apex angle";
		//String sessionID = "20150927_02";
		//String scoringConcern = "leaf apex curvature";
		//String sessionID = "20150927_03";
		//String scoringConcern = "leaf base angle";
		//String sessionID = "20150927_04";
		//String scoringConcern = "leaf base curvature";
		FileSystemPrimer.prime("C:\\avatol\\git\\avatol_cv", "bogus", "leafDev", sessionID, "bisque");
		
		TrainingInfoFileGenerator g = new TrainingInfoFileGenerator(scoringConcern, sessionID);
	}
	
	public TrainingInfoFileGenerator(String scoringConcern, String runID){
		TrainingInfoFile tif = new TrainingInfoFile("annotation", scoringConcern, scoringConcern);
		List<File> toScoreFiles = new ArrayList<File>();
		List<File> toTrainFiles = new ArrayList<File>();
		tif.setImageDir("C:\\avatol\\git\\avatol_cv\\sessions\\leafDev\\normalized\\images");
		try {
			this.scoreIndex = new ScoreIndex(AvatolCVFileSystem.getScoreIndexPath(runID));
			String normDataDir = AvatolCVFileSystem.getNormalizedDataDirForDataset("20150927_01");
			File normDataDirFile = new File(normDataDir);
			File[] files = normDataDirFile.listFiles();
			
			for (File f : files){
				System.out.println(f.getName());
				NormalizedImageInfoScored nii = new NormalizedImageInfoScored(f.getAbsolutePath(), this.scoreIndex);
				String imageFilename = nii.getImageFilename();
				String scoringConcernValue = nii.getValueForKey(scoringConcern);
				if ("null".equals(scoringConcernValue) || null == scoringConcernValue || "".equals(scoringConcernValue)){
					toScoreFiles.add(f);
				}
				else {
					toTrainFiles.add(f);
					tif.addImageInfo(imageFilename, scoringConcernValue, "");
				}
			}
			System.out.println("sessionDir : " + AvatolCVFileSystem.getSessionDir());
			tif.persist( AvatolCVFileSystem.getSessionDir());
			gatherTestInfoFiles(toScoreFiles);
			System.out.println("train: " + toTrainFiles.size() + "  test: " + toScoreFiles.size());
		}
		catch(AvatolCVException e){
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}
	public void gatherTestInfoFiles(List<File> toScoreFiles){
		//___add method into AvatolCVFileSystem that returns dir of orientationOutput (and other dagta passing dirs)
		for (File f: toScoreFiles){
			System.out.println(" score " + f.getName());
		}
	}
}
