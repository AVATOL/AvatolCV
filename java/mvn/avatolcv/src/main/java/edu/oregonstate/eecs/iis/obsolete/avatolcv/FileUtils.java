package edu.oregonstate.eecs.iis.obsolete.avatolcv;

import java.io.File;

public class FileUtils {
    public static void clearDir(String dir){
        File dirFile = new File(dir);
        if (dirFile.isDirectory()){
            File[] files = dirFile.listFiles();
            for (File f : files){
                if (f.getName().equals(".") || f.getName().equals("..")){
                    //leave these
                }
                else {
                    f.delete();
                }
            }
        }
    }
    public static void ensureDirExists(String dir){
        File f = new File(dir);
        if (!f.isDirectory()){
            f.mkdirs();
        }
    }
}
