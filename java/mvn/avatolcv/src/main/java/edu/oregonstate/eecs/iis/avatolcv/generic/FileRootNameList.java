package edu.oregonstate.eecs.iis.avatolcv.generic;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import edu.oregonstate.eecs.iis.avatolcv.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.core.ImageInfo;
/*
trainingImages_segmentation.txt and testingImages_segmentation.txt, etc have entries that are the root names of images
    00-5xayvrdPC3o5foKMpLbZ5H_imgXyz
    03-uietIOuerto5foKMhUHYUh_imgAbc
*/
public class FileRootNameList {
    private static final String NL = System.getProperty("line.separator");

    private String path = null;
    private List<ImageInfo> images = null;
    public FileRootNameList(String path, List<ImageInfo> images){
        this.images = images;
        this.path = path;
    }
    public void persist() throws AvatolCVException {
        File f = new File(path);
        if (f.exists()){
            f.delete();
        }
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(path));
            for (ImageInfo ii : images){
                String nameRoot = ii.getFilename_IdNameWidth();
                writer.write(nameRoot + NL);
            }
            writer.close();
        }
        catch(IOException ioe){
            throw new AvatolCVException("problem creating input image list file: " + ioe.getMessage(),ioe);
        }   
    }
}
