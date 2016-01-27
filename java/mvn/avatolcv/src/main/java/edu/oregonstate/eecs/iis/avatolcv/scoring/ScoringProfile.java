package edu.oregonstate.eecs.iis.avatolcv.scoring;

import edu.oregonstate.eecs.iis.avatolcv.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.algorithm.AlgorithmSequence;
import edu.oregonstate.eecs.iis.avatolcv.algorithm.ScoringAlgorithm;
import edu.oregonstate.eecs.iis.avatolcv.normalized.NormalizedImageInfo;
import edu.oregonstate.eecs.iis.avatolcv.normalized.NormalizedKey;
import edu.oregonstate.eecs.iis.avatolcv.normalized.NormalizedValue;
import edu.oregonstate.eecs.iis.avatolcv.session.SessionInfo;

public class ScoringProfile {
    private SessionInfo sessionInfo = null;
    public ScoringProfile(SessionInfo sessionInfo) throws AvatolCVException {
        this.sessionInfo = sessionInfo;
        
        ScoringAlgorithm sa  = sessionInfo.getSelectedScoringAlgorithm();
        AlgorithmSequence algSequence = sessionInfo.getAlgorithmSequence();
    }
    public NormalizedKey getTrainTestConcern() throws AvatolCVException {
        if (sessionInfo.hasTrainTestConcern()){
            return sessionInfo.getTrainTestConcern();
        }
        else {
            return new NormalizedKey(null);
        }
    }
    public NormalizedValue getTrainTestConcernValue(ModalImageInfo mii) throws AvatolCVException{
        if (!sessionInfo.hasTrainTestConcern()){
            return new NormalizedValue(null);
        }
        else if (this.sessionInfo.getTrainTestConcern().getName().equals("")){
            return new NormalizedValue(null);
        }
        else {
            NormalizedImageInfo nii = mii.getNormalizedImageInfo();
            return nii.getValueForKey(getTrainTestConcern());
        }
    }
    public boolean emitPointAnnotationsInScoringFile() throws AvatolCVException {
        return sessionInfo.getSelectedScoringAlgorithm().shouldIncludePointAnnotationsInScoringFile();
    }
}