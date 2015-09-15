package edu.oregonstate.eecs.iis.avatolcv.core;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import edu.oregonstate.eecs.iis.avatolcv.AvatolCVFileSystem;

public class NormalizedImageInfos {
    private static final String FILESEP = System.getProperty("file.separator");
    private List<NormalizedImageInfo> scoredImages = null;
    private List<NormalizedImageInfo> trainingImages = null;
    private ScoreIndex scoreIndex = null;
    public NormalizedImageInfos(String runID) throws AvatolCVException {
        String datasetNormPath = AvatolCVFileSystem.getNormalizedDataDirForDataset(runID);
        String sessionNormPath = AvatolCVFileSystem.getNormalizedDataDirForSession(runID);
        File dir = new File(datasetNormPath);
        if (!dir.exists()){
            throw new AvatolCVException("NormalizedDataDirForDataset does not exist: " + datasetNormPath);
        }
        this.scoreIndex = new ScoreIndex(AvatolCVFileSystem.getScoreIndexPath(runID));
        File[] files = dir.listFiles();
        List<NormalizedImageInfo> normIIs = new ArrayList<NormalizedImageInfo>();
        for (File f : files){
            if (f.getName().equals(".") || f.getName().equals("..")){
                // skip
            }
            else {
                String path = f.getAbsolutePath();
                String filename = f.getName();
                NormalizedImageInfo nii = new NormalizedImageInfo(path, scoreIndex);
                String pathOfScoreFile = sessionNormPath + FILESEP + filename;
                nii.setScoreFile(pathOfScoreFile, scoreIndex);
                
                normIIs.add(nii);
            }
        }
        sortImageInfos(normIIs);
    }
    public void sortImageInfos(List<NormalizedImageInfo> imageInfos) throws AvatolCVException {
        scoredImages = new ArrayList<NormalizedImageInfo>();
        trainingImages = new ArrayList<NormalizedImageInfo>();
        for (NormalizedImageInfo nii : imageInfos){
            if (nii.isScored()){
                scoredImages.add(nii);
            }
            else {
                trainingImages.add(nii);
            }
        }
    }
    public List<NormalizedImageInfo> getScoredImages(String scoringConcern){
        List<NormalizedImageInfo> result = new ArrayList<NormalizedImageInfo>();
        for (NormalizedImageInfo nii: scoredImages){
            if (nii.hasScoringConcern(scoringConcern, this.scoreIndex)){
                result.add(nii);
            }
        }
        return result;
    }

    public List<NormalizedImageInfo> getTrainingImages(String scoringConcern){
        List<NormalizedImageInfo> result = new ArrayList<NormalizedImageInfo>();
        for (NormalizedImageInfo nii: trainingImages){
        	if (nii.getImageID().startsWith("00-f4B")){
        		int foo = 3;
        		int bar = foo;
        	}
            if (nii.hasScoringConcern(scoringConcern, this.scoreIndex)){
                result.add(nii);
            }
        }
        return result;
    }
}
