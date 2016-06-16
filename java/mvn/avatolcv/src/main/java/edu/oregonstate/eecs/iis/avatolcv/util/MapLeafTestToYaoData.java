package edu.oregonstate.eecs.iis.avatolcv.util;

import edu.oregonstate.eecs.iis.avatolcv.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.AvatolCVFileSystem;

public class MapLeafTestToYaoData {

    public static void main(String[] args) {
        MapLeafTestToYaoData x = new MapLeafTestToYaoData();

    }
    public MapLeafTestToYaoData(){
        FileSystemPrimer.prime("C:\\jed\\avatol\\git\\avatol_cv", "explore", "leafDev", "20150924_01", "bisque");
        try {
        	String modulesDir = AvatolCVFileSystem.getModulesDir();
        }
        catch(AvatolCVException ace){
        	
        }
        /*
         * segmentation/yaoSeg/data/labels
         * segmentation/yaoSeg/data/trainin_imgs
         * 
         * orientation/yaoOrient/alignmentShipped/train
         * 
         * scoring/scoringShipped/train
         */
    }
}
