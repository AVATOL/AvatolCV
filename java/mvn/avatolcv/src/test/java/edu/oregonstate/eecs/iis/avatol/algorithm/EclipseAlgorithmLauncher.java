package edu.oregonstate.eecs.iis.avatol.algorithm;

import edu.oregonstate.eecs.iis.avatolcv.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.AvatolCVFileSystem;
import edu.oregonstate.eecs.iis.avatolcv.Platform;
import edu.oregonstate.eecs.iis.avatolcv.algorithm.AlgorithmLauncher;
import edu.oregonstate.eecs.iis.avatolcv.core.DatasetInfo;

public class EclipseAlgorithmLauncher {

    public static void main(String[] args) {
        String runConfigPath = "";
        String algPropertiesPath = "";
        if (Platform.isWindows()){
            //runConfigPath = "C:\\avatol\\git\\avatol_cv\\modules\\segmentation\\yaoSeg\\runConfig.txt";
            //algPropertiesPath = "C:\\avatol\\git\\avatol_cv\\modules\\segmentation\\yaoSeg\\algPropertiesWindows.txt";
            //runConfigPath = "C:\\avatol\\git\\avatol_cv\\modules\\segmentation\\launchTest\\runConfig.txt";
            //algPropertiesPath = "C:\\avatol\\git\\avatol_cv\\modules\\segmentation\\launchTest\\algPropertiesWindows.txt";
            runConfigPath = "C:\\jed\\avatol\\git\\avatol_cv\\modules\\segmentation\\launchTest\\runConfig.txt";
            algPropertiesPath = "C:\\jed\\avatol\\git\\avatol_cv\\modules\\segmentation\\launchTest\\algPropertiesWindows.txt";
        }
        else {
            runConfigPath = "/Users/jedirvine/av/avatol_cv/modules/segmentation/yaoSeg/runConfig.txt";
            algPropertiesPath = "/Users/jedirvine/av/avatol_cv/modules/segmentation/yaoSeg/algPropertiesMac.txt";
        }
        try {
            AvatolCVFileSystem.setRootDir(TestAlgorithm.getValidRoot());
            DatasetInfo di = new DatasetInfo();
            di.setName("eclipseTest");
            di.setID("xyz");
            di.setProjectID("abc");
            AvatolCVFileSystem.setDatasourceName("local");
            AvatolCVFileSystem.setSessionID("testSession");
            AvatolCVFileSystem.setChosenDataset(di);
        }
        catch(AvatolCVException ace){
            System.out.println("ERROR - could not find valid avatol_cv root for test");
        }
        AlgorithmLauncher launcher = new AlgorithmLauncher(algPropertiesPath, runConfigPath);
        launcher.launch(null);
    }

}
