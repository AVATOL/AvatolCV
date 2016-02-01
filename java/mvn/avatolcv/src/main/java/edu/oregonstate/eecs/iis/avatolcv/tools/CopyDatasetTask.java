package edu.oregonstate.eecs.iis.avatolcv.tools;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javafx.concurrent.Task;
import edu.oregonstate.eecs.iis.avatolcv.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.AvatolCVFileSystem;
import edu.oregonstate.eecs.iis.avatolcv.javafxui.AvatolCVExceptionExpresserJavaFX;

public class CopyDatasetTask extends Task<Boolean> {
    private static final String NL = System.getProperty("line.separator");
    private static final String FILESEP = System.getProperty("file.separator");
    private String sourceNormalizedDir;
    private String destNormalizedDir;
    private CopyDatasetTab copyDatasetTab;
    
    public CopyDatasetTask(String sourceDatasetName, String newDataset, CopyDatasetTab copyDatasetTab) throws AvatolCVException {
        this.copyDatasetTab = copyDatasetTab;
        String newDatasetPath = AvatolCVFileSystem.getSessionsRoot() + FILESEP + newDataset;
        File newDatasetDirFile = new File(newDatasetPath);
        newDatasetDirFile.mkdir();
		String sourceDatasetDir = AvatolCVFileSystem.getSessionsRoot() + FILESEP + sourceDatasetName;
		this.sourceNormalizedDir = sourceDatasetDir + FILESEP + "normalized";
		this.destNormalizedDir = newDatasetPath + FILESEP + "normalized";
    }
    @Override
    protected Boolean call() throws Exception {
        try {
        	recursiveCopyDir(sourceNormalizedDir,destNormalizedDir);
            return new Boolean(true);
        }
        catch(AvatolCVException ace){
            AvatolCVExceptionExpresserJavaFX.instance.showException(ace, "AvatolCV error copying dataset");
            return new Boolean(false);
        }
    }
    private void recursiveCopyDir(String sourceNormalizedDir, String destNormalizedDir) throws AvatolCVException {
		//File newNormalizedDir = new File(destNormalizedDir);
		Path destNormalizedPath = Paths.get(destNormalizedDir);
		Path sourceNormalizedPath = Paths.get(sourceNormalizedDir);
		try {
            
            copyDatasetTab.disableCopyButton();
            Files.walk(sourceNormalizedPath).forEach(path ->{
                    try {
                    	copyDatasetTab.appendText("copying " + path + NL);
                        Files.copy(path, Paths.get(path.toString().replace(
                        		sourceNormalizedPath.toString(),
                        		destNormalizedPath.toString())));
                    } catch (Exception e) {
                    	copyDatasetTab.appendText(e.getMessage() + NL);
                    	StackTraceElement[] stes = e.getStackTrace();
                    	for (StackTraceElement ste : stes){
                    		copyDatasetTab.appendText("" + ste);
                    	}
                    }
            });
            
            copyDatasetTab.appendText("copy complete! " + NL);
            
            copyDatasetTab.enableCopyButton();
        } catch (IOException e1) {
            throw new AvatolCVException("problem copying dataset: " + e1.getMessage());
        }
	}
    public static boolean isNewDatasetAlreadyExist(String newDataset) throws AvatolCVException {
    	String newDatasetPath = AvatolCVFileSystem.getSessionsRoot() + FILESEP + newDataset;
		File newDatasetDirFile = new File(newDatasetPath);
		if (newDatasetDirFile.exists()){
			return true;
		}
		return false;
    }
    public static boolean isValidCopySource(File candidateSessionDir){
    	String imageInfoPath  = candidateSessionDir + FILESEP + "normalized" + FILESEP + "imageInfo";
		String imagesPath     = candidateSessionDir + FILESEP + "normalized" + FILESEP + "images";
		File f1 = new File(imageInfoPath);
		File f2 = new File(imagesPath);
		if (f1.isDirectory() && f2.isDirectory()){
			return true;
		}
		return false;
    	
    }
}