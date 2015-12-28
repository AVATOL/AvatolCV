package edu.oregonstate.eecs.iis.avatolcv.orientation.files;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import edu.oregonstate.eecs.iis.avatolcv.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.core.ImageInfo;
import edu.oregonstate.eecs.iis.avatolcv.core.ObsoleteImagesForAlgorithmStep;
import edu.oregonstate.eecs.iis.obsolete.avatolcv.orientation.OrientationSessionData;

/*
 * darwinOutputDir,<same as in xml file - points to where segmented files are put>
rawImagesDir,<path of dir where right sized images from bisque are put>
relaventImagesFile,<path to file that lists filenames of images to orient>
orientationResultsDir,<path to dir to put orientation normalized images>
modelFilePath,<path to pre-trained svm model for apex, base locating>
apexLocationConvention,left

output files    ..._rotated.jpg
 */
public class OrientationRunConfig {
    private static final String NL = System.getProperty("line.separator");
    private OrientationSessionData osd = null;
    private ObsoleteImagesForAlgorithmStep ifs = null;
    public OrientationRunConfig(OrientationSessionData osd, ObsoleteImagesForAlgorithmStep ifs)throws AvatolCVException {
        this.ifs = ifs;
        this.osd = osd;
    }
    public void persist() throws AvatolCVException {
        String path = osd.getConfigFilePath();
        File f = new File(path);
        if (f.exists()){
            f.delete();
        }
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(path));
            writer.write("testImageDir=" + osd.getTestImageDir() + NL);    
            writer.write("trainingImageDir=" + osd.getTrainingImageDir() + NL);    
            writer.write("rawImagesDir=" + osd.getRawImagesDir() + NL);
            writer.write("trainingImagesFile=" + osd.getTrainingImageFilePath() + NL);
            writer.write("testingImagesFile=" + osd.getTestImageFilePath() + NL);
            writer.write("orientationResultsDir=" + osd.getOutputDir() + NL);
            writer.write("inputFileSuffix=_" + OrientationSessionData.TYPE_SUFFIX_INPUT+ NL);
            writer.write("outputFileSuffix=_" + OrientationSessionData.TYPE_SUFFIX_OUTPUT+ NL);
            writer.write("modelFilePath=" + osd.getModelFilePath() + NL);
            writer.write("apexLocationConvention=left" + NL);
            for (ImageInfo ii : ifs.getTrainingImages()){
                writer.write("trainingImage="+ii.getFilename() + NL);
            }
            for (ImageInfo ii : ifs.getTestImagesPlusTrainingImageAncestors()){
                writer.write("testImage="+ii.getFilename() + NL);
            }
            writer.close();
            writer.close();
        }
        catch(IOException ioe){
            throw new AvatolCVException("problem creating segmentation config file: " + ioe.getMessage(), ioe);
        }
        
    }
}
