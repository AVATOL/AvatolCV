package edu.oregonstate.eecs.iis.avatolcv.scoring;

import java.util.List;

import edu.oregonstate.eecs.iis.avatolcv.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.core.ModalImageInfo;
import edu.oregonstate.eecs.iis.avatolcv.normalized.NormalizedKey;
import edu.oregonstate.eecs.iis.avatolcv.normalized.NormalizedValue;

public interface ScoringSet {
	List<ModalImageInfo> getImagesToTrainOn() throws AvatolCVException;
	List<ModalImageInfo> getImagesToScore() throws AvatolCVException;
	List<ModalImageInfo> getImagesToTrainOnForKeyValue(NormalizedKey key, NormalizedValue value) throws AvatolCVException;
	List<NormalizedKey> getAllKeys();
	NormalizedKey getKeyToScore();
	String getScoringConcernName();
}
