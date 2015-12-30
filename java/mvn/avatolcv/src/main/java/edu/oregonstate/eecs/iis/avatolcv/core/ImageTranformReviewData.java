package edu.oregonstate.eecs.iis.avatolcv.core;

import java.awt.image.BufferedImage;
import java.util.List;

import edu.oregonstate.eecs.iis.avatolcv.AvatolCVException;
import edu.oregonstate.eecs.iis.obsolete.avatolcv.orientation.ObsoleteImagesForAlgorithmStep;

public interface ImageTranformReviewData {
    public ObsoleteImagesForAlgorithmStep getImagesForStage();
    public List<ImageInfo> getCandidateImages();
    public void deleteTrainingImage(ImageInfo ii) throws AvatolCVException;
    public void saveTrainingImage(BufferedImage bi, ImageInfo ii) throws AvatolCVException;
    public void disqualifyImage(ImageInfo ii) throws AvatolCVException;
    public void requalifyImage(ImageInfo ii) throws AvatolCVException;
}
