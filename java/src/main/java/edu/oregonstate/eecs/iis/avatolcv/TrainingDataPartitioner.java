package edu.oregonstate.eecs.iis.avatolcv;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import edu.oregonstate.eecs.iis.avatolcv.mb.Annotations;
import edu.oregonstate.eecs.iis.avatolcv.mb.Matrix;
import edu.oregonstate.eecs.iis.avatolcv.mb.MatrixCell;
import edu.oregonstate.eecs.iis.avatolcv.mb.MorphobankBundle;
import edu.oregonstate.eecs.iis.avatolcv.mb.MorphobankSDDFile;

public class TrainingDataPartitioner {
	private MorphobankBundle bundle = null;
	private static final String FILESEP = System.getProperty("file.separator");
	private static final String TRAINING_ANNOTATIONS_DIRNAME = Annotations.ANNOTATIONS_DIR + "_tr";
	private static final String HOLDOUT_ANNOTATIONS_DIRNAME = Annotations.ANNOTATIONS_DIR + "_ho";
	private static final String PARTITION_FILE_FLAG = "holdoutTrainingDataMarker.txt";
	private boolean partitioningNeeded = false;
    public TrainingDataPartitioner(MorphobankBundle bundle) throws AvatolCVException {
    	this.bundle = bundle;
    	this.partitioningNeeded = isPartitioningNeeded(bundle.getRootDir());
    }
    public String partitionTrainingData() throws AvatolCVException{
    	if (!this.partitioningNeeded){
    		return Annotations.ANNOTATIONS_DIR;
    	}
    	else {
    		String trainingDirPath = this.bundle.getRootDir()  + TRAINING_ANNOTATIONS_DIRNAME;
    		File trainingDir = new File(trainingDirPath);
    		trainingDir.mkdirs();
    		String holdoutDirPath = this.bundle.getRootDir()  + HOLDOUT_ANNOTATIONS_DIRNAME;
    		File holdoutDir = new File(holdoutDirPath);
    		holdoutDir.mkdirs();
    		if (this.bundle.isSpecimenPerRowBundle()){
    			List<MatrixCell> allTrainingCells= new ArrayList<MatrixCell>();
    			List<MatrixCell> allToScoreCells= new ArrayList<MatrixCell>();
    			MorphobankSDDFile sdd = this.bundle.getSDDFile();
    			Matrix matrix = sdd.getMatrix();
    			// for each true taxon
    			List<String> taxonNames = this.bundle.getSDDFile().getMatrix().getScoredTaxonNames();
    			for (String taxonName : taxonNames){
    				String taxonId = this.bundle.getTaxonIdForName(taxonName);
    				List<String> charIds = matrix.getScoredCharacterIds();
    				for (String charId : charIds){
    					if (sdd.isPresenceAbsenceCharacter(charId)){
    						List<MatrixCell> relevantCells = matrix.getCellsForCharacterAndTaxon(charId, taxonId); 
        					HoldoutAssessor ha = new HoldoutAssessor(this.bundle.getRootDir(),relevantCells,sdd, this.bundle.getSystemProperties().getTrainingSplitThreshold());
        					List<MatrixCell> trainingCells = ha.getTrainingCells();
        					List<MatrixCell> toScoreCells = ha.getToScoreCells();
        					copyAnnotationFilesForCells(trainingCells,trainingDirPath);
        					copyAnnotationFilesForCells(toScoreCells,holdoutDirPath);
        					allTrainingCells.addAll(trainingCells);
        					allToScoreCells.addAll(toScoreCells);
    					}
    				}
    			}
    		}
    		else {
    			throw new AvatolCVException("partitionTrainingData not yet implemented for non-specimen-per-row case");
    		}
    		return TRAINING_ANNOTATIONS_DIRNAME;
    	}
    }
    public void copyAnnotationFilesForCells(List<MatrixCell> cells, String destinationDir) throws AvatolCVException {
    	for (MatrixCell cell : cells){
    		String charId = cell.getCharId();
    		List<String> mediaIds = cell.getMediaIds();
    		for (String mediaId : mediaIds){
    			String filename = mediaId + "_" + charId + ".txt";
    			String sourcePath = this.bundle.getRootDir() + Annotations.ANNOTATIONS_DIR + FILESEP + filename;
    			String destPath = destinationDir + FILESEP + filename;
    			copyFile(sourcePath,destPath);
    		}
    	}
    }
    public void copyFile(String sourcePath, String destPath) throws AvatolCVException {
	    InputStream is = null;
	    OutputStream os = null;
	    File source = new File(sourcePath);
	    File dest = new File(destPath);
	    try {
	        is = new FileInputStream(source);
	        os = new FileOutputStream(dest);
	        byte[] buffer = new byte[1024];
	        int length;
	        while ((length = is.read(buffer)) > 0) {
	            os.write(buffer, 0, length);
	        }
	    } 
	    catch (IOException ioe){
	    	throw new AvatolCVException("problem copying " + sourcePath + " to " + destPath);
	    }

	    finally {
	    	try {
	            is.close();
	            os.close();
	    	}
	    	catch(IOException ioe){
	    		throw new AvatolCVException("problem closing files while copying " + sourcePath + " to " + destPath);
	    	}
	    }
    }
    public boolean isPartitioningNeeded(String dirPath) throws AvatolCVException {
    	File f = new File(dirPath);
    	if (!f.exists()){
    		throw new AvatolCVException("bad path given to TrainingDataPartitioner " + dirPath);
    	}
    	File[] files = f.listFiles();
    	for (File curFile : files){
    		if (curFile.getName().equals(PARTITION_FILE_FLAG)){
    			return true;
    		}
    	}
    	return false;
    }
}
