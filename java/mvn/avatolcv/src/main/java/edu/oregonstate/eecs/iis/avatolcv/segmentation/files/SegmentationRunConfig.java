package edu.oregonstate.eecs.iis.avatolcv.segmentation.files;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import edu.oregonstate.eecs.iis.avatolcv.core.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.core.ImageInfo;
import edu.oregonstate.eecs.iis.avatolcv.core.ImagesForStage;
import edu.oregonstate.eecs.iis.avatolcv.segmentation.SegmentationSessionData;

public class SegmentationRunConfig {
    private static final String FILESEP = System.getProperty("file.separator");
    private static final String NL = System.getProperty("line.separator");
    private SegmentationSessionData ssd = null;
    private ImagesForStage ifs = null;
    public SegmentationRunConfig(SegmentationSessionData ssd, ImagesForStage ifs)throws AvatolCVException {
        this.ifs = ifs;
        this.ssd = ssd;
    }
    public void persist() throws AvatolCVException {
        String path = ssd.getConfigFilePath();
        File f = new File(path);
        if (f.exists()){
            f.delete();
        }
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(path));
            writer.write("darwinXMLFileDir=" + ssd.getRootSegmentationDir() + NL);
            writer.write("trainingImagesFile=" + ssd.getTrainingImageFilePath() + NL);
            writer.write("testingImagesFile=" + ssd.getTestImageFilePath() + NL);
            writer.write("rawImagesDir=" + ssd.getSourceImageDir() + NL);
            writer.write("trainingImagesDir=" + ssd.getSegmentationTrainingImageDir());
            writer.write("segmentationOutputDir=" + ssd.getOutputDir() + NL);
            writer.write("modelXmlPath=" + ssd.getRootSegmentationDir() + FILESEP + "model.xml" +NL);
            writer.write("trainingFileSuffix=_groundtruth" + NL);
            writer.write("outputFileSuffix=_mask" + NL);
            writer.write("#" + NL);
            for (ImageInfo ii : ifs.getTrainingImages()){
                writer.write("trainingImage="+ii.getFilename() + NL);
            }
            for (ImageInfo ii : ifs.getTestImages()){
                writer.write("testImage="+ii.getFilename() + NL);
            }
            writer.close();
        }
        catch(IOException ioe){
            throw new AvatolCVException("problem creating segmentation config file: " + ioe.getMessage(), ioe);
        }
        
    }
}
