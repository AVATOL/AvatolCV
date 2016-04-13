package edu.oregonstate.eecs.iis.avatolcv.datasource;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;

import edu.oregonstate.eecs.iis.avatolcv.AvatolCVDataFiles;
import edu.oregonstate.eecs.iis.avatolcv.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.AvatolCVFileSystem;
import edu.oregonstate.eecs.iis.avatolcv.algorithm.ScoringAlgorithm;
import edu.oregonstate.eecs.iis.avatolcv.normalized.NormalizedImageInfo;
import edu.oregonstate.eecs.iis.avatolcv.normalized.NormalizedImageInfos;
import edu.oregonstate.eecs.iis.avatolcv.normalized.NormalizedKey;
import edu.oregonstate.eecs.iis.avatolcv.normalized.NormalizedTypeIDName;
import edu.oregonstate.eecs.iis.avatolcv.normalized.NormalizedValue;
import edu.oregonstate.eecs.iis.avatolcv.session.DataFilter;
import edu.oregonstate.eecs.iis.avatolcv.session.DatasetInfo;
import edu.oregonstate.eecs.iis.avatolcv.session.ProgressPresenter;
import edu.oregonstate.eecs.iis.avatolcv.session.SessionImages;
import edu.oregonstate.eecs.iis.avatolcv.session.DataFilter.FilterItem;
import edu.oregonstate.eecs.iis.avatolcv.ws.MorphobankWSClient;
import edu.oregonstate.eecs.iis.avatolcv.ws.MorphobankWSClientImpl;
import edu.oregonstate.eecs.iis.avatolcv.ws.MorphobankWSException;
import edu.oregonstate.eecs.iis.avatolcv.ws.morphobank.AnnotationInfo.MBAnnotation;
import edu.oregonstate.eecs.iis.avatolcv.ws.morphobank.AnnotationInfo.MBAnnotationPoint;
import edu.oregonstate.eecs.iis.avatolcv.ws.morphobank.CellMediaInfo.MBMediaInfo;
import edu.oregonstate.eecs.iis.avatolcv.ws.morphobank.CharStateInfo.MBCharStateValue;
import edu.oregonstate.eecs.iis.avatolcv.ws.morphobank.CharacterInfo.MBCharState;
import edu.oregonstate.eecs.iis.avatolcv.ws.morphobank.CharacterInfo.MBCharacter;
import edu.oregonstate.eecs.iis.avatolcv.ws.morphobank.MatrixInfo.MBMatrix;
import edu.oregonstate.eecs.iis.avatolcv.ws.morphobank.TaxaInfo.MBTaxon;
import edu.oregonstate.eecs.iis.avatolcv.ws.morphobank.ViewInfo.MBView;

public class MorphobankDataSource implements DataSource {
	private static final String FILESEP = System.getProperty("file.separator");
    private MorphobankWSClient wsClient = null;
    private List<MBCharacter> charactersForMatrix = null;
    private List<MBCharacter> chosenCharacters = null;
    private List<MBTaxon> taxaForMatrix = null;
    private DatasetInfo chosenDataset = null;
    private List<MBView> viewsForProject = null;
    private List<MBView> viewsPresent = null;
    private List<MBMediaInfo> mediaInfosForSession = new ArrayList<MBMediaInfo>();
    private List<String> mediaIDsForSession = new ArrayList<String>();
    private Hashtable<String, MBCharacter> charForIDHash = new Hashtable<String, MBCharacter>();
    private Hashtable<String, MBTaxon> taxonForIDHash = new Hashtable<String, MBTaxon>();
    private Hashtable<String, MBView> viewForIDHash = new Hashtable<String, MBView>();
    private Hashtable<String,List<MBCharStateValue>> charStateValuesForCellHash = new Hashtable<String, List<MBCharStateValue>>();
    private Hashtable<String,List<MBMediaInfo>> mediaInfoForCellHash = new Hashtable<String, List<MBMediaInfo>>();
    private MorphobankDataFiles mbDataFiles = null;
    private DataFilter dataFilter = null;
    private MorphobankImages morphobankImages = null;
    private NormalizedImageInfos niis = null;
    private SessionImages sessionImages = null;
    private Hashtable<String, String> cellIDsForCellKeyHash = new Hashtable<String, String>();
    public MorphobankDataSource(){
        wsClient = new MorphobankWSClientImpl();
        mbDataFiles = new MorphobankDataFiles();
    }
    @Override
    public void setSessionImages(SessionImages sessionImages){
    	this.sessionImages = sessionImages;
    }
    @Override
    public boolean authenticate(String username, String password) throws AvatolCVException {
        boolean result = false;
        try {
            result = this.wsClient.authenticate(username, password);
        }
        catch(MorphobankWSException e){
            throw new AvatolCVException(e.getMessage(),e);
        }
        return result;
    }
    @Override
    public boolean isAuthenticated() {
        return this.wsClient.isAuthenticated(); 
    }
    @Override
    public List<DatasetInfo> getDatasets() throws AvatolCVException {
        List<DatasetInfo> datasets = new ArrayList<DatasetInfo>();
        List<MBMatrix> matrices = null;
        try {
            matrices = wsClient.getMorphobankMatricesForUser();
            for (MBMatrix mm : matrices){
                DatasetInfo di = new DatasetInfo();
                di.setName(mm.getName());
                di.setID(mm.getMatrixID());
                di.setProjectID(mm.getProjectID());
                datasets.add(di);
            }
            Collections.sort(datasets);
            return datasets;
        }
        catch(MorphobankWSException e){
            throw new AvatolCVException("problem loading matrices from Morphobank ", e);
        }
    }
    
    @Override
    public String getDefaultUsername() {
        return "irvine@eecs.oregonstate.edu";
        //return "jedirv@gmail.com";
    }
    @Override
    public String getDefaultPassword() {
        return "squonkmb";
    }
    @Override
    public void loadPrimaryMetadataForChosenDataset(ProgressPresenter pp,
            String processName) throws AvatolCVException {
        try {
        	mbDataFiles.prepareForMetadataDownload();
            String matrixID = this.chosenDataset.getID();
            String projectID = this.chosenDataset.getProjectID();
            pp.setMessage(processName, "loading info on characters...");
            pp.updateProgress(processName, 0.0);
            
            this.charactersForMatrix = this.wsClient.getCharactersForMatrix(matrixID);
            for (MBCharacter ch : this.charactersForMatrix){
                this.charForIDHash.put(ch.getCharID(), ch);
            }
            pp.setMessage(processName, "loading info on taxa...");
            pp.updateProgress(processName, 0.4);
            this.taxaForMatrix = this.wsClient.getTaxaForMatrix(matrixID);
            for (MBTaxon taxon : this.taxaForMatrix){
                this.taxonForIDHash.put(taxon.getTaxonID(), taxon);
            }
            pp.setMessage(processName, "loading info on views...");
            pp.updateProgress(processName, 0.8);
            this.viewsForProject = this.wsClient.getViewsForProject(projectID);
            for (MBView v : this.viewsForProject){
                this.viewForIDHash.put(v.getViewID(), v);
            }
            pp.setMessage(processName, "finished.  Click Next to continue.");
            pp.updateProgress(processName, 1.0);
        }
        catch(MorphobankWSException e){
            throw new AvatolCVException("problem loading data for matrix. " + e.getMessage(), e);
        }
    }
    @Override
    public void setChosenDataset(DatasetInfo di) {
        this.chosenDataset = di;
    }
    public List<ChoiceItem> getScoringConcernItemsNoneSelected() throws AvatolCVException {
        List<ChoiceItem> result = new ArrayList<ChoiceItem>();
        for (MBCharacter character : this.charactersForMatrix){
        	String normalizedString = NormalizedTypeIDName.buildTypeIdName("character", character.getCharID(), character.getCharName());
        	NormalizedKey nKey = new NormalizedKey(normalizedString);
            ChoiceItem ci = new ChoiceItem(nKey, false, true, character);
            result.add(ci);
        }
        Collections.sort(result);
        return result;
    }
    public List<ChoiceItem> getPresenceAbsenceScoringConcernItems() throws AvatolCVException {
        List<ChoiceItem> result = new ArrayList<ChoiceItem>();
        for (MBCharacter character : this.charactersForMatrix){
            boolean isPresenceAbsence = character.isPresenceAbsence();
            String normalizedString = NormalizedTypeIDName.buildTypeIdName("character", character.getCharID(), character.getCharName());
        	NormalizedKey nKey = new NormalizedKey(normalizedString);
            ChoiceItem ci = new ChoiceItem(nKey, isPresenceAbsence, true, character);
            result.add(ci);
        }
        Collections.sort(result);
        return result;
    }
   
    @Override
    public List<ChoiceItem> getScoringConcernOptions(ScoringAlgorithm.ScoringScope scoringScope, ScoringAlgorithm.ScoringSessionFocus scoringFocus) throws AvatolCVException{
        if (scoringScope == ScoringAlgorithm.ScoringScope.MULTIPLE_ITEM && scoringFocus == ScoringAlgorithm.ScoringSessionFocus.SPECIMEN_PART_PRESENCE_ABSENCE){
            return getPresenceAbsenceScoringConcernItems();
        }
        else {
            return getScoringConcernItemsNoneSelected();
            
        }
    }
    @Override
    public String getInstructionsForScoringConcernScreen(ScoringAlgorithm.ScoringScope scoringScope, ScoringAlgorithm.ScoringSessionFocus scoringFocus) {
        if (scoringScope == ScoringAlgorithm.ScoringScope.MULTIPLE_ITEM){
            return "Place a check mark next to characters to score";
        }
        else {
            return "Select the character to score";
        }
    }
   
    @Override
    public void setChosenScoringConcerns(List<ChoiceItem> items) {
        this.chosenCharacters = new ArrayList<MBCharacter>();
        for (ChoiceItem ci : items){
            this.chosenCharacters.add((MBCharacter)ci.getBackingObject());
        }
    }
    @Override
    public void setChosenScoringConcern(ChoiceItem item) {
        this.chosenCharacters = new ArrayList<MBCharacter>();
        this.chosenCharacters.add((MBCharacter)item.getBackingObject());
    }
    //List<MBAnnotation> annotationsForCell = robustAnnotationDataDownload(pp, matrixID, charID, taxonID , mediaID, processName);
    @Override
    public void loadRemainingMetadataForChosenDataset(ProgressPresenter pp,
            String processName) throws AvatolCVException {
        try {
        	mbDataFiles.clearNormalizedImageFiles();
        	mediaInfosForSession.clear();
            int rowCount = this.taxaForMatrix.size();
            int colCount = this.chosenCharacters.size();
            String matrixID = this.chosenDataset.getID();
            int totalItemCount = colCount * rowCount;
            double increment = 1.0 / totalItemCount;
            int curCount = 0;
            this.viewsPresent = new ArrayList<MBView>();
            List<String> viewIDsSeen = new ArrayList<String>();
            //for (MBCharacter character : this.charactersForMatrix){
            for (MBCharacter character : this.chosenCharacters){
                for (MBTaxon taxon : this.taxaForMatrix){
                    String charID = character.getCharID();
                    String taxonID = taxon.getTaxonID();
                    // GET CHARACTER STATES FOR THIS CELL
                    pp.setMessage(processName, "character " + character.getCharName() + " taxon " + taxon.getTaxonName());
                    String key = getKeyForCell( charID,taxonID);
                    List<MBCharStateValue> charStatesForCell = this.mbDataFiles.loadMBCharStatesFromDisk(charID, taxonID);
                    if (null == charStatesForCell){
                        charStatesForCell = this.wsClient.getCharStatesForCell(matrixID, charID, taxonID);
                        this.mbDataFiles.persistMBCharStatesForCell(charStatesForCell, charID, taxonID);
                    }
                    charStateValuesForCellHash.put(key, charStatesForCell);
                    
                    // GET MEDIA INFOS FOR CELL
                    List<MBMediaInfo> mediaInfosForCell = this.mbDataFiles.loadMBMediaInfosForCell(charID, taxonID);
                    if (null == mediaInfosForCell){
                        mediaInfosForCell = this.wsClient.getMediaForCell(matrixID, charID, taxonID);
                        this.mbDataFiles.persistMBMediaInfosForCell(mediaInfosForCell, character, taxon);
                    }
                    mediaInfosForSession.addAll(mediaInfosForCell);
                    for (MBMediaInfo mi : mediaInfosForCell){
                        String viewID = mi.getViewID();
                        if (null == viewID){
                            viewID = MBMediaInfo.VIEW_ID_NOT_SPECIFIED;
                            mi.setViewID(MBMediaInfo.VIEW_ID_NOT_SPECIFIED);
                        }
                        if (!viewIDsSeen.contains(viewID)){
                            viewIDsSeen.add(viewID);
                        }
                    }
                   
                    // LOAD ANNOTATIONS FOR CELL AND CREATE NORMALIZED IMAGE INFO FILES
                    for (MBMediaInfo mi : mediaInfosForCell){
                    	String mediaID = mi.getMediaID();
                    	List<MBAnnotation> annotationsForCell = this.mbDataFiles.loadMBAnnotationsFromDisk(charID, taxonID, mediaID);
                    	if (null == annotationsForCell){
                    		annotationsForCell = robustAnnotationDataDownload(pp, matrixID, charID, taxonID , mediaID, processName);
                    		this.mbDataFiles.persistAnnotationsForCell(annotationsForCell, charID, taxonID, mediaID);
                    	}
                    	String niiFilename = createNormalizedImageInfoForSession(mi,character, taxon, charStatesForCell, annotationsForCell, this.chosenCharacters);
                        if (!sessionImages.contains(niiFilename)){
                            this.sessionImages.add(niiFilename);
                        }    
                    }
                    mediaInfoForCellHash.put(key, mediaInfosForCell);
                    curCount++;
                    pp.updateProgress(processName, curCount * increment);
                }
            }
            for (MBView v : this.viewsForProject){
                if (viewIDsSeen.contains(v.getViewID())){
                    this.viewsPresent.add(v);
                }
            }
            
        }
        catch(MorphobankWSException e){
            throw new AvatolCVException("problem loading data for matrix. " + e.getMessage(), e);
        }
        
    }
    
    public List<MBAnnotation> robustAnnotationDataDownload(ProgressPresenter pp, String matrixID, String charID, String taxonID ,String mediaID, String processName) throws AvatolCVException {
        int maxRetries = 4;
        int tries = 0;
        boolean dataNotYetDownloaded = true;
        Exception mostRecentException = null;
        while (maxRetries > tries && dataNotYetDownloaded){
            try {
                tries++;
        		List<MBAnnotation> annotationsForCell = this.wsClient.getAnnotationsForCellMedia(matrixID, charID, taxonID, mediaID);

                dataNotYetDownloaded = false;
                return annotationsForCell;
            }
            catch(MorphobankWSException e){
                if (e.getMessage().equals("timeout")){
                    pp.setMessage(processName, "download timed out - retrying trainingData : charID " + charID + " taxonID " + taxonID + " - attempt " + (tries+1));
                }
                mostRecentException = e;
            }
        }
        if (dataNotYetDownloaded){
            throw new AvatolCVException("problem downloading data: " + mostRecentException);
        }
        return null;
    }
    public String getViewNameForID(String viewID) throws AvatolCVException {
        if (viewID.equals(MBMediaInfo.VIEW_ID_NOT_SPECIFIED)){
            return MBMediaInfo.VIEW_ID_NOT_SPECIFIED;
        }
    	for (MBView v : this.viewsForProject){
    		if (v.getViewID().equals(viewID)){
    			return v.getName();
    		}
    	}
    	throw new AvatolCVException("no view name for given viewID " + viewID);
    }
    public String getCharStateNameForID(MBCharacter character, String charStateID) throws AvatolCVException {
    	List<MBCharState> charStates = character.getCharStates();
    	for (MBCharState cs : charStates){
    		if (cs.getCharStateID().equals(charStateID)){
    			return cs.getCharStateName();
    		}
    	}
    	throw new AvatolCVException("no charState name for given charStateID");
    }
    
    
    public String createNormalizedImageInfoForSession(MBMediaInfo mi,MBCharacter character, MBTaxon taxon, List<MBCharStateValue> charStatesForCell, List<MBAnnotation> annotationsForCell, List<MBCharacter> chosenScoringConcerns) throws AvatolCVException {
    	
        // FIXME - need to rework/simplify the format of these files as per 9/4/2015 decisions, and also add in the new avcv_scoringConcernLocation, avcv_scoreValueLocation keys using chosenScoringConcerns.
        
        String mediaID = mi.getMediaID();
    	String characterKey = NormalizedTypeIDName.buildTypeIdName("character",character.getCharID() ,character.getCharName());
    	String characterValue = "";
    	//System.out.println("charStateCount " + charStatesForCell.size() + " for key " + characterKey);
    	/*for (int i = 0; i < charStatesForCell.size(); i++){
    		MBCharStateValue csv = charStatesForCell.get(i);
    		String charStateID = csv.getCharStateID();
    		String charStateName = getCharStateNameForID(character, charStateID);
    		if (i == charStatesForCell.size() - 1){
    			characterValue = characterValue + NormalizedTypeIDName.buildTypeIdName("characterState",charStateID,charStateName);
    		}
    		else {
    			characterValue = characterValue + NormalizedTypeIDName.buildTypeIdName("characterState",charStateID,charStateName) + ",";
    		}
    	}*/
    	MBCharStateValue csv = charStatesForCell.get(0);
		String charStateID = csv.getCharStateID();
		String charStateName = "";
		if (charStateID.equals("unscored")){
			charStateName = ""; // for now, leave it as empty string
		}   
		else {
			charStateName = getCharStateNameForID(character, charStateID);
		}
		//System.out.println("charStateID : " + charStateID + " char : " + character.getCharName() + " taxon " + taxon.getTaxonName());
		
		characterValue = characterValue + NormalizedTypeIDName.buildTypeIdName("characterState",charStateID,charStateName);
    	List<String> lines = new ArrayList<String>();
    	lines.add(characterKey + "=" + characterValue);
    	lines.add("taxon=" + taxon.getTaxonID() + "|" + taxon.getTaxonName());
    	//System.out.println("taxon " + taxon.getTaxonName());
    	String viewValue = mi.getViewID() + "|" + getViewNameForID(mi.getViewID());
    	lines.add("view=" + viewValue);
    	String annotationsValueString = getAnnotationsValueString(annotationsForCell);
    	lines.add(NormalizedImageInfo.KEY_ANNOTATION + "=" + annotationsValueString);
    	lines.add(NormalizedImageInfo.KEY_IMAGE_NAME + "=" + mediaID);
    	String path =  this.niis.createNormalizedImageInfoFromLines(mediaID,lines);
    	File f = new File(path);
        return f.getName();
    }
    public static String getAnnotationsValueString(List<MBAnnotation> annotations){
        // avcv_annotation=rectangle:25-45;35-87+point:98-92
        // + delimits the annotations in the series
        // ; delimits the points in the annotation
        // - delimits x and y coordinates
        // : delimits type from points
        if (annotations.isEmpty()){
            return "";
        }
    	StringBuilder sb = new StringBuilder();
    	for (int i = 0; i < annotations.size() - 1; i++){
    		String annotationValueString = getAnnotationValueStringForAnnotation(annotations.get(i));
    		sb.append(annotationValueString + PointAnnotations.ANNOTATION_SEQUENCE_DELIMETER);
    	}
    	String finalAnnotationValueString = getAnnotationValueStringForAnnotation(annotations.get(annotations.size() -1));
		sb.append(finalAnnotationValueString);
    	return "" + sb;
    }
    public static String getAnnotationValueStringForAnnotation(MBAnnotation a){
    	StringBuilder sb = new StringBuilder();
    	String annotationType = a.getType();
    	sb.append(annotationType + PointAnnotations.ANNOTATION_TYPE_DELIMETER);
		List<MBAnnotationPoint> points = a.getPoints();
		if (points.isEmpty()){
		    return "";
		}
		for (int i = 0; i < points.size() - 1; i++){
			MBAnnotationPoint p = points.get(i);
			String value = p.getX() + PointAnnotations.XY_DELIMETER + p.getY();
			sb.append(value + PointAnnotations.POINT_SERIES_DELIMETER);
		}
		MBAnnotationPoint finalp = points.get(points.size() -1);
		String finalValue = finalp.getX() + PointAnnotations.XY_DELIMETER + finalp.getY();
		sb.append(finalValue);
		return "" + sb;
    }
    public static String getKeyForCell(String charID, String taxonID){
        return "c" + charID + "_t" + taxonID;
    }
    
    private static final String NL = System.getProperty("line.separator");
    @Override
    public String getDatasetSummaryText() {        
        StringBuilder sb = new StringBuilder();
        sb.append("Matrix: " + this.chosenDataset.getName() + NL);
        sb.append(NL);
        sb.append("Taxa:" + NL);
        for (MBTaxon taxon : taxaForMatrix){
            sb.append("    " + taxon.getTaxonName() + NL);
        }
        sb.append(NL);
        sb.append("Selected Characters: " + NL);
        for (MBCharacter character : chosenCharacters){
            sb.append("    " + character.getCharName() + NL);
        }
        sb.append("Views in project: " + NL);
        for (MBView view : this.viewsForProject){
            sb.append("    " + view.getName() + NL);
        }
        sb.append(NL);
        return "" + sb;
    }
    @Override
    public AvatolCVDataFiles getAvatolCVDataFiles() {
        return this.mbDataFiles;
    }
    
   
    @Override
    public void acceptFilter() {
        /*
        List<Pair> pairs = this.dataFilter.getItems();
        for (Pair p : pairs){
            if (p.isSelected()){
                if (p.getName().equals("character")){
                    acceptCharacterForSession(p);
                }
                else if (p.getName().equals("taxon")){
                    acceptTaxonForSession(p);
                }
                else {
                    accept(p);
                }
            }
            
        }
    */
    }
    private void filterCharacter(FilterItem p){
        if (p.isSelected()){
            
        }
        else {
            
        }
    }
    @Override
    public String getName() {
        return "morphobank";
    }
    @Override
    public void downloadImages(ProgressPresenter pp, String processName)
            throws AvatolCVException {
        this.morphobankImages = new MorphobankImages(pp, wsClient, chosenDataset, processName, this);
    }
    @Override
    public String getDatasetTitleText() {
        return "Matrix";
    }
	public List<MBMediaInfo> getMBMediaInfoForSession(){
	    return this.mediaInfosForSession;
	}
	private void rememberFilenamesForSession(List<String> names) throws AvatolCVException {
	    String path = AvatolCVFileSystem.getSessionDir() ;
	}
	@Override
	public void setNormalizedImageInfos(NormalizedImageInfos niis) {
	    this.niis = niis;
	}
	@Override
	public String getDefaultTrainTestConcern() {
		return "taxon";
	}
    @Override
    public NormalizedValue getValueForKeyAtDatasourceForImage(NormalizedKey normCharKey,
            String imageID, NormalizedKey trainTestConcern,
            NormalizedValue trainTestConcernValue) throws AvatolCVException {
        // Any character asked about here will be present because in the MB session the user specified and requested it explicitly
        try {
            String matrixID = this.chosenDataset.getID();
            String charID = normCharKey.getID();
            String ttConcernValueID = trainTestConcernValue.getID();
            List<MBCharStateValue> charStateValues = wsClient.getCharStatesForCell(matrixID, charID, ttConcernValueID);
            // really only need to determine if there is at least one
            if (null == charStateValues){
                return null;
            }
            if (charStateValues.size() == 0){
                return null;
            }
            MBCharStateValue csv = charStateValues.get(0);
            String cellID = csv.getCellID();
            if (null == cellID){
                return null;
            }
            if ("".equals(cellID)){
                return null;
            }
            String taxonID = trainTestConcernValue.getID();
            cellIDsForCellKeyHash.put(getKeyForCell(charID, taxonID), cellID);
            String charStateID = csv.getCharStateID();
            String charStateName = null;
            if (null == this.charactersForMatrix){
                this.charactersForMatrix = this.wsClient.getCharactersForMatrix(matrixID);
            }
            for (MBCharacter mbChar : this.charactersForMatrix){
                List<MBCharState> charStates = mbChar.getCharStates();
                for (MBCharState charState : charStates){
                    if (charStateID.equals(charState.getCharStateID())){
                        charStateName = charState.getCharStateName();
                    }
                }
            }
            if (null == charStateName){
                return new NormalizedValue(charStateID);
            }
            else {
                return new NormalizedValue("charState:" + charStateID + "|" + charStateName);
            }
        }
        catch(MorphobankWSException e){
            throw new AvatolCVException("problem determining existing value of score for image " + imageID + " character " + normCharKey.getName() + " taxon " + trainTestConcernValue.getName());
        }
    }
	@Override
	public boolean reviseValueForKey(String imageID, NormalizedKey key,
			NormalizedValue value, NormalizedKey trainTestConcern, NormalizedValue trainTestConcernValue) throws AvatolCVException {
		// http://morphobank.org/service.php/AVATOLCv/recordScore/username/irvine@eecs.oregonstate.edu/password/squonkmb/matrixID/23331/cellID/58835485/stateID/4876140
	    try {
	        String matrixID = this.chosenDataset.getID();
	        String charStateID = value.getID();
	        String charID = key.getID();
	        String taxonID = trainTestConcernValue.getID();
	        String cellID = cellIDsForCellKeyHash.get(getKeyForCell(charID, taxonID));
	        wsClient.reviseScore(matrixID, cellID, charStateID);
	        return true;
	    }
	    catch(MorphobankWSException e){
	        throw new AvatolCVException ("problem revising score " + e.getMessage());
	    }
	}
	@Override
	public boolean addKeyValue(String imageID, NormalizedKey key,
			NormalizedValue value, NormalizedKey trainTestConcern, NormalizedValue trainTestConcernValue) throws AvatolCVException {
		// http://morphobank.org/service.php/AVATOLCv/recordScore/username/irvine@eecs.oregonstate.edu/password/squonkmb/matrixID/23331/characterID/1820895/taxonID/770536/stateID/4876140
	    try {
	        String matrixID = this.chosenDataset.getID();
	        String taxonID = trainTestConcernValue.getID();
	        String charStateID = value.getID();
	        String charID = key.getID();
	        wsClient.addNewScore(matrixID, charID, taxonID, charStateID);
	        return true;
	    }
	    catch(MorphobankWSException e){
	        throw new AvatolCVException ("problem adding new score " + e.getMessage());
	    }
		
	}
    @Override
    public List<String> filterBadSortCandidates(List<String> list) {
        List<String> result = new ArrayList<String>();
        for (String s : list){
            if (!s.equals("view")){
                result.add(s);
            }
        }
        return result;
    }
}
