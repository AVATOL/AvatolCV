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
import java.util.Hashtable;
import java.util.List;

import edu.oregonstate.eecs.iis.avatolcv.mb.Annotation;
import edu.oregonstate.eecs.iis.avatolcv.mb.Annotations;
import edu.oregonstate.eecs.iis.avatolcv.mb.Matrix;
import edu.oregonstate.eecs.iis.avatolcv.mb.MatrixCell;
import edu.oregonstate.eecs.iis.avatolcv.mb.MatrixCellImageUnit;
import edu.oregonstate.eecs.iis.avatolcv.mb.MorphobankBundle;
import edu.oregonstate.eecs.iis.avatolcv.mb.MorphobankSDDFile;

public class TrainingDataPartitioner {
	private MorphobankBundle bundle = null;
	private static final String FILESEP = System.getProperty("file.separator");
	//private static final String TRAINING_ANNOTATIONS_DIRNAME = Annotations.ANNOTATIONS_DIR + "_tr";
	//private static final String HOLDOUT_ANNOTATIONS_DIRNAME = Annotations.ANNOTATIONS_DIR + "_ho";
	private static final String PARTITION_FILE_FLAG = "holdoutTrainingDataMarker.txt";
	private static final String STATUS_TRAINING = "training";
	private static final String STATUS_TO_SCORE = "toScore";
	private boolean partitioningNeeded = false;
	private Hashtable<String,String> statusForKey = new Hashtable<String,String>();
	private PartitionRegister partitionRegister = new PartitionRegister();
    public TrainingDataPartitioner(MorphobankBundle bundle) throws AvatolCVException {
    	this.bundle = bundle;
    	this.partitioningNeeded = isPartitioningNeeded(bundle.getRootDir());
    }
    public void persistRegister() throws AvatolCVException {
    	this.partitionRegister.persist();
    }
    public void setPersistDirectory(String dir){
    	this.partitionRegister.setPersistDirectory(dir);
    }
    public String getPartitionDirName(){
    	double threshold = this.bundle.getSystemProperties().getTrainingSplitThreshold();
    	if (threshold == -1.0){
    		return "";
    	}
    	String dirname = "split_" + threshold;
    	return dirname;
    }
    public String getPartitionedLineForTrainingLine(String charId, String trainingLine){
    	String charName = this.bundle.getCharacterNameForId(charId);
    	TrainingSample ts = new TrainingSample(trainingLine, this.bundle.getRootDir(), charId, charName);
    	String taxonId = ts.getTaxonId();
    	String mediaId = ts.getMediaId();
    	String key = getKey(charId, taxonId, mediaId);
    	//System.out.println("Key is " + key);
    	String status = statusForKey.get(key);
    	if (null == status){
    		return null;
    	}
    	else if (status.equals(STATUS_TRAINING)){
    		return trainingLine;
    	}
    	else{
    		String toScoreLine = convertTrainingLineToAToScoreLine(trainingLine);
    		return toScoreLine;
    	}
    }
    public String convertTrainingLineToAToScoreLine(String trainingLine){
    	//training_data|media\M328516_Thyroptera tricolor AMNH273160Fvent.jpg|s946109|I1 absent|NA|t281048|NA
    	//image_to_score|media\M283392_.jpg|t171193
    	String[] parts = trainingLine.split(Annotation.ANNOTATION_DELIM_ESCAPED_FOR_USE_WITH_SPLIT);
    	String relMediaPath = parts[1];
    	String relAnnotationPath = parts[4];
    	String annotationLineNum = parts[6];
    	String taxonId = parts[5];
    	String delim = Annotation.ANNOTATION_DELIM;
    	String toScoreLine = DataIOFile.IMAGE_TO_SCORE_MARKER + delim + relMediaPath + delim + taxonId + delim + relAnnotationPath + delim + annotationLineNum;
    	return toScoreLine;
    }
    public String getPartitionedLineForToScoreLine(String charId, String toScoreLine) throws AvatolCVException{
    	ToScoreLine tsl = new ToScoreLine(toScoreLine, this.bundle.getRootDir());
    	String taxonId = tsl.getTaxonId();
    	String mediaId = tsl.getMediaId();
    	String key = getKey(charId, taxonId, mediaId);
    	String status = statusForKey.get(key);
    	if (null == status){
    		return null;
    	}
    	else if (status.equals(STATUS_TRAINING)){
    		throw new AvatolCVException("TrainingDataPartitioner trying to change a toScore line to a training line " + toScoreLine);
    	}
    	else {
    		return toScoreLine;
    	}
    }
    public String getKey(String charId, String taxonId, String mediaId){
    	String key = charId + "_" + taxonId + "_" + mediaId;
    	return key;
    }
    public void assignUnitsToStatus(List<MatrixCellImageUnit> units, String status){
    	for (MatrixCellImageUnit unit : units){
    		String mediaId = unit.getMediaId();
    		String charId = unit.getCharId();
    		String taxonId = unit.getTaxonId();
    		String key = getKey(charId, taxonId, mediaId);
    		statusForKey.put(key, status);
    	}
    }
    public void partitionTrainingData(List<String> charIds, String viewId) throws AvatolCVException{
    	if (!this.partitioningNeeded){
    		//do nothing
    	}
    	else {
    		//String trainingDirPath = this.bundle.getRootDir()  + TRAINING_ANNOTATIONS_DIRNAME;
    		//File trainingDir = new File(trainingDirPath);
    		//trainingDir.mkdirs();
    		//String holdoutDirPath = this.bundle.getRootDir()  + HOLDOUT_ANNOTATIONS_DIRNAME;
    		//File holdoutDir = new File(holdoutDirPath);
    		//holdoutDir.mkdirs();
    		if (this.bundle.isSpecimenPerRowBundle()){
    			//List<MatrixCell> allTrainingCells= new ArrayList<MatrixCell>();
    			//List<MatrixCell> allToScoreCells= new ArrayList<MatrixCell>();
    			MorphobankSDDFile sdd = this.bundle.getSDDFile();
    			Matrix matrix = sdd.getMatrix();
    			// for each true taxon
    			List<String> taxonNames = this.bundle.getSDDFile().getMatrix().getScoredTaxonNames();
    			for (String taxonName : taxonNames){
    				String taxonId = this.bundle.getTaxonIdForName(taxonName);
    				//List<String> charIds = matrix.getScoredCharacterIds();
    				for (String charId : charIds){
    					if (sdd.isPresenceAbsenceCharacter(charId)){
    						List<MatrixCell> relevantCells = matrix.getCellsForCharacterAndTaxon(charId, taxonId); 
    						partitionCells(relevantCells, viewId);
    					}
    				}
    			}
    		}
    		else {
    			throw new AvatolCVException("partitionTrainingData not yet implemented for non-specimen-per-row case");
    		}
    	}
    }
    /*
     * for a set of char and taxon cells, convert them to units by view and then for each view, assess the holdout
     */
    public void partitionCells(List<MatrixCell> cells, String focusViewId) throws AvatolCVException {
    	MorphobankSDDFile sdd = this.bundle.getSDDFile();
    	List<String> viewIds = new ArrayList<String>();
    	Hashtable<String, List<MatrixCellImageUnit>> unitListForView = new Hashtable<String, List<MatrixCellImageUnit>>();
    	//divide the cells up into slices - one image per slice
    	for (MatrixCell cell : cells){
			List<MatrixCellImageUnit> units = cell.getImageUnits(this.bundle);
			for (MatrixCellImageUnit unit : units){
				String viewId = unit.getViewId();
				if (!(viewIds.contains(viewId))){
					viewIds.add(viewId);
				}
				List<MatrixCellImageUnit> unitList = unitListForView.get(viewId);
				if (unitList == null){
					unitList = new ArrayList<MatrixCellImageUnit>();
					unitListForView.put(viewId,  unitList);
				}
				unitList.add(unit);
			}
		}
    	// at this point we have a list of viewIds and a hash of those vs the lists of relevant units
    	for (String viewId : viewIds){
    		if (viewId.equals(focusViewId)){
    			List<MatrixCellImageUnit> unitList = unitListForView.get(viewId);
        		
        		HoldoutAssessor ha = new HoldoutAssessor(this.bundle.getRootDir(),unitList,sdd, this.bundle.getSystemProperties().getTrainingSplitThreshold(), this.partitionRegister);
        		List<MatrixCellImageUnit> trainingUnits = ha.getTrainingUnits();
        		assignUnitsToStatus(trainingUnits,STATUS_TRAINING);
        		//this.partitionRegister.registerTrainingUnits(trainingUnits);
        		List<MatrixCellImageUnit> toScoreUnits = ha.getToScoreUnits();
        		assignUnitsToStatus(toScoreUnits,STATUS_TO_SCORE);
        		//this.partitionRegister.registerToScoreUnits(toScoreUnits);
    		}
    	}
    }
    
    /*
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
    */
    /*
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
    */
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
