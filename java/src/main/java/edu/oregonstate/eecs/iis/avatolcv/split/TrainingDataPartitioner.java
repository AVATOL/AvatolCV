package edu.oregonstate.eecs.iis.avatolcv.split;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import edu.oregonstate.eecs.iis.avatolcv.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.RunNumber;
import edu.oregonstate.eecs.iis.avatolcv.algata.DataIOFile;
import edu.oregonstate.eecs.iis.avatolcv.algata.ToScoreLine;
import edu.oregonstate.eecs.iis.avatolcv.algata.TrainingSample;
import edu.oregonstate.eecs.iis.avatolcv.mb.Annotation;
import edu.oregonstate.eecs.iis.avatolcv.mb.Annotations;
import edu.oregonstate.eecs.iis.avatolcv.mb.Matrix;
import edu.oregonstate.eecs.iis.avatolcv.mb.MatrixCell;
import edu.oregonstate.eecs.iis.avatolcv.mb.MatrixCellImageUnit;
import edu.oregonstate.eecs.iis.avatolcv.mb.MorphobankBundle;
import edu.oregonstate.eecs.iis.avatolcv.mb.MorphobankSDDFile;
import edu.oregonstate.eecs.iis.avatolcv.mb.Taxon;
import edu.oregonstate.eecs.iis.avatolcv.ui.TaxonTrainingSelector;

public class TrainingDataPartitioner {
	private MorphobankBundle bundle = null;
	private static final String FILESEP = System.getProperty("file.separator");
	//private static final String PARTITION_FILE_FLAG = "holdoutTrainingDataMarker.txt";
	private static final String STATUS_TRAINING = "training";
	private static final String STATUS_TO_SCORE = "toScore";
	//private boolean partitioningNeeded = false;
	private Hashtable<String,String> statusForKey = new Hashtable<String,String>();
	private PartitionRegister partitionRegister = new PartitionRegister();
	private TaxonTrainingSelector tts = null;
	private RunNumber runNumber = null;
    public TrainingDataPartitioner(MorphobankBundle bundle) throws AvatolCVException {
    	this.bundle = bundle;
    	this.runNumber = bundle.getRunNumberController();
    	//this.partitioningNeeded = ;
    }
    public MorphobankBundle getBundle(){
    	return this.bundle;
    }
    public boolean isTrainingImage(String mediaId){
    	return this.partitionRegister.isTrainingImage(mediaId);
    }
    public void persistRegister() throws AvatolCVException {
    	this.partitionRegister.persist();
    }
    public void setPersistDirectory(String dir){
    	this.partitionRegister.setPersistDirectory(dir);
    }
    public void setTaxonTrainingSelector(TaxonTrainingSelector tts){
    	this.tts = tts;
    }
    public boolean isRegime1(){
    	return tts.isAllTaxaSelected();
    }
    public boolean isTaxonForTraining(String taxonId){
    	List<Taxon> selectedTaxa = tts.getSelectedTaxa();
    	for (Taxon taxon : selectedTaxa){
    		String curTaxonId = taxon.getId();
    		if (taxonId.equals(curTaxonId)){
    			return true;
    		}
    	}
    	return false;
    }
    public String getPartitionDirName() throws AvatolCVException {
    	if (tts.isAllTaxaSelected()){
    		//regime 1, we need to split the data for training and testing as per the specified split threshold
    		double threshold = this.bundle.getSystemProperties().getTrainingDataSplitThreshold();
        	if (threshold == -1.0){
        		throw new AvatolCVException("need to split training and testing for regime 1 but no percentage specified in avatolcv_properties.txt");
        	}
        	String dirname = "split_" + threshold + this.runNumber.getCurrentRunNumber();
        	return dirname;
    	}
    	else {
    		//regime2, we need to split the data as per the tts selections
    		String dirname = "regime2_" + this.runNumber.getCurrentRunNumber();
    		return dirname;
    	}
    	
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
    		System.out.println(key + " ... " + status);
    		statusForKey.put(key, status);
    	}
    }
    
    public void partitionTrainingData(List<String> charIds, String viewId) throws AvatolCVException{
    	if (this.tts.isAllTaxaSelected()){
    		//regime1
    		if (this.bundle.getSystemProperties().isSplitPartitioningSupported()){
    			if (this.bundle.getSystemProperties().isSpecimenPerRowBundle()){
    				regime1SpecimensPerRowSplit(charIds, viewId);
        		}
        		else {
        			throw new AvatolCVException("split partitionTrainingData not yet implemented for non-specimen-per-row case");
        		}
    		}
    		else {
    			throw new AvatolCVException("split partition needed but not supported by split value in properties file");
    		}
    	}
    	else {
    		//regime 2
    		regime2Split(charIds, viewId);
    	}
    	
    }
    public void regime2Split(List<String> charIds, String viewId) throws AvatolCVException {
    	MorphobankSDDFile sdd = this.bundle.getSDDFile();
		Matrix matrix = sdd.getMatrix();
    	//foreach training taxa
    	//  foreach relevant charId
    	//      get the Matrix cell, 
    	//      get the imageunits from the cell
    	//      assignUnitsToStatus(trainingUnits,STATUS_TRAINING);
    	List<Taxon> selectedTaxa = this.tts.getSelectedTaxa();
    	for (Taxon taxon : selectedTaxa){
    		String taxonId = taxon.getId();
    		for (String charId : charIds){
				if (sdd.isPresenceAbsenceCharacter(charId)){
					List<MatrixCell> relevantCells = matrix.getCellsForCharacterAndTaxon(charId, taxonId); 
					System.out.println("specify TRAIN relevantCells for charId " + charId + " taxonId " + taxonId);
					specifyCellsForTraining(relevantCells, viewId);
				}
			}
    	}
    	
    	//foreach testing taxa
    	//  foreach relevant charId
    	//      get the Matrix cell, 
    	//      get the imageunits from the cell
    	//      assignUnitsToStatus(trainingUnits,STATUS_TESTING);
    	List<Taxon> unselectedTaxa = this.tts.getUnselectedTaxa();
    	for (Taxon taxon : unselectedTaxa){
    		String taxonId = taxon.getId();
    		for (String charId : charIds){
				if (sdd.isPresenceAbsenceCharacter(charId)){
					List<MatrixCell> relevantCells = matrix.getCellsForCharacterAndTaxon(charId, taxonId); 
					System.out.println("specify TEST relevantCells for charId " + charId + " taxonId " + taxonId);
					specifyCellsForScoring(relevantCells, viewId);
				}
			}
    	}
    }
    public void specifyCellsForTraining(List<MatrixCell> cells, String focusViewId) throws AvatolCVException {
    	Hashtable<String, List<MatrixCellImageUnit>> unitListForView = getUnitListForView(cells);
    	// at this point we have a list of viewIds and a hash of those vs the lists of relevant units
    	Enumeration<String> keysEnum = unitListForView.keys();
    	while (keysEnum.hasMoreElements()){
    		String viewId = keysEnum.nextElement();
    		if (viewId.equals(focusViewId)){
    			List<MatrixCellImageUnit> unitList = unitListForView.get(viewId);
    			assignUnitsToStatus(unitList,STATUS_TRAINING);
    			for (MatrixCellImageUnit unit : unitList){
					this.partitionRegister.registerTrainingUnit(unit);
				}
    		}
    	}
    }
    public void specifyCellsForScoring(List<MatrixCell> cells, String focusViewId) throws AvatolCVException {
    	Hashtable<String, List<MatrixCellImageUnit>> unitListForView = getUnitListForView(cells);
    	// at this point we have a list of viewIds and a hash of those vs the lists of relevant units
    	Enumeration<String> keysEnum = unitListForView.keys();
    	while (keysEnum.hasMoreElements()){
    		String viewId = keysEnum.nextElement();
    		if (viewId.equals(focusViewId)){
    			List<MatrixCellImageUnit> unitList = unitListForView.get(viewId);
    			assignUnitsToStatus(unitList,STATUS_TO_SCORE);
    			for (MatrixCellImageUnit unit : unitList){
					this.partitionRegister.registerToScoreUnit(unit);
				}
    		}
    	}
    }
    public void regime1SpecimensPerRowSplit(List<String> charIds, String viewId) throws AvatolCVException {
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
					partitionCellsSpecimensPerRow(relevantCells, viewId);
				}
			}
		}
    }
    public Hashtable<String, List<MatrixCellImageUnit>> getUnitListForView(List<MatrixCell> cells){
    	Hashtable<String, List<MatrixCellImageUnit>> unitListForView = new Hashtable<String, List<MatrixCellImageUnit>>();
    	List<String> viewIds = new ArrayList<String>();
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
    	return unitListForView;
    }
    
    /*
     * for a set of char and taxon cells, convert them to units by view and then for each view, assess the holdout
     */
    public void partitionCellsSpecimensPerRow(List<MatrixCell> cells, String focusViewId) throws AvatolCVException {
    	MorphobankSDDFile sdd = this.bundle.getSDDFile();
    	Hashtable<String, List<MatrixCellImageUnit>> unitListForView = getUnitListForView(cells);
    	// at this point we have a list of viewIds and a hash of those vs the lists of relevant units
    	Enumeration<String> keysEnum = unitListForView.keys();
    	while (keysEnum.hasMoreElements()){
    		String viewId = keysEnum.nextElement();
    		if (viewId.equals(focusViewId)){
    			List<MatrixCellImageUnit> unitList = unitListForView.get(viewId);
        		
        		HoldoutAssessor ha = new HoldoutAssessor(this.bundle.getRootDir(),unitList,sdd, this.bundle.getSystemProperties().getTrainingDataSplitThreshold(), this.partitionRegister);
        		List<MatrixCellImageUnit> trainingUnits = ha.getTrainingUnits();
        		assignUnitsToStatus(trainingUnits,STATUS_TRAINING);
        		//this.partitionRegister.registerTrainingUnits(trainingUnits);
        		List<MatrixCellImageUnit> toScoreUnits = ha.getToScoreUnits();
        		assignUnitsToStatus(toScoreUnits,STATUS_TO_SCORE);
        		//this.partitionRegister.registerToScoreUnits(toScoreUnits);
    		}
    	}
    }
}
