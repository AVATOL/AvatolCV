package edu.oregonstate.eecs.iis.avatolcv.core;

import java.util.List;

import edu.oregonstate.eecs.iis.avatolcv.AvatolCVException;

public interface ScoringSet {
	List<ModalImageInfo> getImagesToTrainOn() throws AvatolCVException;
	List<ModalImageInfo> getImagesToScore() throws AvatolCVException;
	List<ModalImageInfo> getImagesToTrainOnForKeyValue(String key, String value) throws AvatolCVException;
	List<String> getAllKeys();
	String getKeyToScore();
}
