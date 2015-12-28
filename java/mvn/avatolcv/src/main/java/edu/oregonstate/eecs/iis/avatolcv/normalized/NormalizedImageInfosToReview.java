package edu.oregonstate.eecs.iis.avatolcv.normalized;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import edu.oregonstate.eecs.iis.avatolcv.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.AvatolCVFileSystem;
import edu.oregonstate.eecs.iis.avatolcv.scoring.ScoreIndex;
/**
 * 
 * @author admin-jed
 * 
 * This is some notes.
 *
 */
public class NormalizedImageInfosToReview {
    private static final String FILESEP = System.getProperty("file.separator");
    private List<NormalizedImageInfoScored> scoredImages = null;
    private List<NormalizedImageInfoScored> trainingImages = null;
    private ScoreIndex scoreIndex = null;
    public NormalizedImageInfosToReview(String runID) throws AvatolCVException {
        String datasetNormPath = AvatolCVFileSystem.getNormalizedDataDirForDataset(runID);
        String sessionNormPath = AvatolCVFileSystem.getNormalizedDataDirForSession(runID);
        File dir = new File(datasetNormPath);
        if (!dir.exists()){
            throw new AvatolCVException("NormalizedDataDirForDataset does not exist: " + datasetNormPath);
        }
        this.scoreIndex = new ScoreIndex(AvatolCVFileSystem.getScoreIndexPath(runID));
        File[] files = dir.listFiles();
        List<NormalizedImageInfoScored> normIIs = new ArrayList<NormalizedImageInfoScored>();
        for (File f : files){
            if (f.getName().equals(".") || f.getName().equals("..")){
                // skip
            }
            else {
                String path = f.getAbsolutePath();
                String filename = f.getName();
                NormalizedImageInfoScored nii = new NormalizedImageInfoScored(path, scoreIndex);
                String pathOfScoreFile = sessionNormPath + FILESEP + filename;
                nii.loadAVCVScoreFile(pathOfScoreFile, scoreIndex);
                
                normIIs.add(nii);
            }
        }
        sortImageInfos(normIIs);
    }
    public void sortImageInfos(List<NormalizedImageInfoScored> imageInfos) throws AvatolCVException {
        // FIXME - this is not so simple.  I need to look at each NII and ignore any that do not have the relevant key (character)
        // and also ignore any that were excluded on quality grounds
        scoredImages = new ArrayList<NormalizedImageInfoScored>();
        trainingImages = new ArrayList<NormalizedImageInfoScored>();
        for (NormalizedImageInfoScored nii : imageInfos){
            if (nii.isScored()){
                scoredImages.add(nii);
            }
            else {
                trainingImages.add(nii);
            }
        }
    }
    public List<NormalizedImageInfoScored> getScoredImages(String scoringConcern) throws AvatolCVException {
        List<NormalizedImageInfoScored> result = new ArrayList<NormalizedImageInfoScored>();
        for (NormalizedImageInfoScored nii: scoredImages){
            if (nii.hasScoringConcern(scoringConcern, this.scoreIndex)){
                result.add(nii);
            }
        }
        return result;
    }

    public List<NormalizedImageInfoScored> getTrainingImages(String scoringConcern) throws AvatolCVException{
        List<NormalizedImageInfoScored> result = new ArrayList<NormalizedImageInfoScored>();
        for (NormalizedImageInfoScored nii: trainingImages){
            if (nii.hasScoringConcern(scoringConcern, this.scoreIndex)){
                result.add(nii);
            }
        }
        return result;
    }
}
