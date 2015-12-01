package edu.oregonstate.eecs.iis.avatolcv.core;

import java.util.List;

public interface ScoringSet {
	List<ModalImageInfo> getImagesToTrainOn();
	List<ModalImageInfo> getImagesToScore();
	List<ModalImageInfo> getImagesToTrainOnForKeyValue(String key, String value);
	List<String> getAllKeys();
}
