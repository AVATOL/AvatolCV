package edu.oregonstate.eecs.iis.avatolcv.util;

import edu.oregonstate.eecs.iis.avatolcv.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.AvatolCVFileSystem;
import edu.oregonstate.eecs.iis.avatolcv.core.DatasetInfo;

public class FileSystemPrimer {

    public static void prime(String rootDir, String id, String datasetName, String sessionID, String datasourceName){
        try {
            AvatolCVFileSystem.setRootDir(rootDir);
            DatasetInfo di = new DatasetInfo();
            di.setID(id);
            di.setName(datasetName);
            AvatolCVFileSystem.setSessionID(sessionID);
            AvatolCVFileSystem.setDatasourceName(datasourceName);
            AvatolCVFileSystem.setChosenDataset(di);
        }
        catch(AvatolCVException e){
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }
}
