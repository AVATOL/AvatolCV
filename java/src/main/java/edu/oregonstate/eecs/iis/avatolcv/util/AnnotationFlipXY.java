package edu.oregonstate.eecs.iis.avatolcv.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import edu.oregonstate.eecs.iis.avatolcv.mb.Annotation;
import edu.oregonstate.eecs.iis.avatolcv.mb.Annotations;
import edu.oregonstate.eecs.iis.avatolcv.mb.MorphobankDataException;

public class AnnotationFlipXY {
	private static final String FILESEP = System.getProperty("file.separator");
	private static final String NL = System.getProperty("line.separator");
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String bundleDir = "C:\\avatol\\git\\avatol_cv\\matrix_downloads\\BAT2";
        String path = "C:\\avatol\\git\\avatol_cv\\matrix_downloads\\BAT2\\imagesFlipped.txt";
        AnnotationFlipXY afox = new AnnotationFlipXY(bundleDir, path);
	}
    public AnnotationFlipXY(String bundleDir, String imageListPath){
    	try {
    		BufferedReader reader = new BufferedReader(new FileReader(imageListPath));
    		String line = null;
    		while (null != (line = reader.readLine())){
    			//String[] parts = line.split("_");
    			String imageIdCapM = line;
    			String imageId = imageIdCapM.replace("M", "m");
    			rotateAllAnnotationsWithImage(bundleDir, imageId);
    		}
    		reader.close();
    	}
    	catch (FileNotFoundException fnfe){
    		System.out.println("could not find file " + imageListPath);
    	}
    	catch(IOException ioe){
    		System.out.println("problem reading file " + imageListPath);
    	}
    	catch(MorphobankDataException mde){
    		System.out.println("problem reading morphobankData " + imageListPath);
    	}
    }
    public static void copyToBackup(String source, String dest){
    	File orig = new File(source);
    	
    	File f = new File(dest);
    	if (f.exists()){
    		f.delete();
    	}
    	System.out.println("backing up " + orig.getName() + " to " + f.getName());
    	try {
    		BufferedReader reader = new BufferedReader(new FileReader(source));
    		BufferedWriter writer = new BufferedWriter(new FileWriter(dest));
    		String line = null;
    		while (null != (line = reader.readLine())){
    			writer.write(line + NL);
    		}
    		reader.close();
    		writer.close();
    	}
    	catch(IOException ioe){
    		System.out.println("problem backing up file " + source);
    	}
    }
    public void rotateAllAnnotationsWithImage(String bundleDir, String imageId) throws MorphobankDataException {
    	String annotationDir = bundleDir + FILESEP + "annotations";
    	File imagesFile = new File(annotationDir);
    	File[] files = imagesFile.listFiles();
    	for (File f : files){
    		if (f.getName().startsWith(imageId)){
    			String annotationsPath = annotationDir + FILESEP + f.getName();
    			List<Annotation> annotationsFromFile = Annotations.loadAnnotations(annotationsPath, imageId);
    			rotateAnnotationsInFile(annotationsFromFile, annotationDir, f.getName());
    		}
    	}
    }
    public void rotateAnnotationsInFile(List<Annotation> annotations, String annotationDir, String filename){
    	String origFilePath = annotationDir + FILESEP + filename;
    	String backupFilePath = annotationDir + FILESEP + "PRE_ROTATION_" + filename;
    	copyToBackup(origFilePath, backupFilePath);
    	File f = new File(origFilePath);
    	f.delete();
    	for (Annotation a : annotations){
    		a.reverseXCoord();
    		a.reverseYCoord();
    	}
    	try {
    		BufferedWriter writer = new BufferedWriter(new FileWriter(origFilePath));
        	for (Annotation a : annotations){
        		//43.7606837606838,37.6495726495726:c71481:Clavicle, medial articulation, type:s180886:point contact
        		String delim = Annotation.ANNOTATION_FILE_DELIM;
        		writer.write(a.getCoordinateList() + delim);
        		writer.write(a.getCharId() + delim);
        		writer.write(a.getCharNameText() + delim);
        		writer.write(a.getCharState() + delim);
        		writer.write(a.getCharStateText() + NL);
        	}
        	writer.close();
    	}
    	catch(IOException ioe){
    		System.out.println("problem writing xFlipped annotation file " + origFilePath);
    	}
    	File annotationsDirFile = new File(annotationDir);
    	String parentDir = annotationsDirFile.getParent();
    	String copyDir = parentDir + FILESEP + "annotationsRotated";
    	File copyDirFile = new File(copyDir);
    	copyDirFile.mkdirs();
    	String sepDirCopyPath = copyDir + FILESEP + filename;
    	copyToBackup(origFilePath, sepDirCopyPath);
    }
}
