package edu.oregonstate.eecs.iis.avatolcv;

import java.io.File;

public class SystemDependent {

    public String getRootDir(){
        if (isDesktop()){
            return "C:\\jed\\avatol\\git\\avatol_cv";
        }
        else {
            return "C:\\avatol\\git\\avatol_cv";
        }
    }
    public boolean isDesktop(){
        String desktopPath = "C:\\jed\\avatol\\git\\avatol_cv";
        File f = new File(desktopPath);
        if (f.isDirectory()){
            return true;
        }
        return false;
    }
}
