package edu.oregonstate.eecs.iis.avatolcv.util;

import edu.oregonstate.eecs.iis.avatolcv.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.AvatolCVFileSystem;

public class CreateTestImagesFile {
    public static final String FILESEP = System.getProperty("file.separator");
    public static void main(String[] args) {
        CreateTestImagesFile ctif = new CreateTestImagesFile();
    }
    public CreateTestImagesFile(){
        FileSystemPrimer.prime("C:\\jed\\avatol\\git\\avatol_cv", "explore", "leafDev", "20150924_01", "bisque");
        try {
        	String modulesDir = AvatolCVFileSystem.getModulesDir();
        }
        catch(AvatolCVException ace){
        	
        }
        
    }
}
