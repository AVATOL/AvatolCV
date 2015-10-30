package edu.oregonstate.eecs.iis.avatolcv.algorithm;

import edu.oregonstate.eecs.iis.avatolcv.Platform;

public class EclipseAlgorithmLauncher {

    public static void main(String[] args) {
        String runConfigPath = "";
        String algPropertiesPath = "";
        if (Platform.isWindows()){
            runConfigPath = "C:\\avatol\\git\\modules\\segmentation\\yaoSeg\\runConfig.txt";
            algPropertiesPath = "C:\\avatol\\git\\modules\\segmentation\\yaoSeg\\algPropertiesWindows.txt";
        }
        else {
            runConfigPath = "/Users/jedirvine/av/avatol_cv/modules/segmentation/yaoSeg/runConfig.txt";
            algPropertiesPath = "/Users/jedirvine/av/avatol_cv/modules/segmentation/yaoSeg/algPropertiesMac.txt";
        }
        AlgorithmLauncher launcher = new AlgorithmLauncher(algPropertiesPath, runConfigPath);
    }

}
