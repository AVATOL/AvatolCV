package edu.oregonstate.eecs.iis.avatolcv.core;

import java.io.File;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Properties;

import edu.oregonstate.eecs.iis.avatolcv.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.AvatolCVFileSystem;
import edu.oregonstate.eecs.iis.avatolcv.RunSummary;
import edu.oregonstate.eecs.iis.avatolcv.algorithm.AlgorithmModules;
import edu.oregonstate.eecs.iis.avatolcv.algorithm.Algorithm;
import edu.oregonstate.eecs.iis.avatolcv.algorithm.AlgorithmSequence;
import edu.oregonstate.eecs.iis.avatolcv.algorithm.OrientationAlgorithm;
import edu.oregonstate.eecs.iis.avatolcv.algorithm.ScoringAlgorithm;
import edu.oregonstate.eecs.iis.avatolcv.algorithm.SegmentationAlgorithm;
import edu.oregonstate.eecs.iis.avatolcv.datasource.ChoiceItem;
import edu.oregonstate.eecs.iis.avatolcv.datasource.DataSource;

/*
 * Directory layout:
 * 
 * 
 * avatol_cv/sessions/<dataset>/images/thumbnail
 *                                       /large
 *                                       /exclusions/<imageID>_imageQuality.txt
 *                                       /rotations/<imageID>_rotateV.txt
 * avatol_cv/sessions/<dataset>/imageMetadata/<imageID>.txt

 *                                                  
 *          /sessions/<sessionID>.txt    <- has the info for the session
 *                        ScoringSessionFocus=SPECIMEN_PART_PRESENCE_ABSENCE
 *                        ScoringScope=MULTIPLE_ITEM
 *                        DataSource=Morphobank
 *                        ScoringConcernKey=<characterX>,<characterY>,<characterZ>
 *                        TraingTestConcernKey=taxon
 *                        LoginName=Morphobank:jedirv@foo.com
 *                        LoginPassword=Morphobank:<encryptedPassword>
 *                        Dataset=BAT
 *                        PresenceAbsenceChars=charId1:charName1, charId2:charName2,...
 *                        FilterIncludeKeyValue=view:Ventral
 *                        
 *                        
 *          /sessions/<sessionID>/
 *                        
 *                    
 * 
 * 
 * 
 * 
 * 
 */
public class SessionInfo{
    private String sessionsRootDir = null;
    private static final String NL = System.getProperty("line.separator");
    private static final String FILESEP = System.getProperty("file.separator");
    private String sessionID = null;
    private static DataSource dataSource = null;
    private DatasetInfo chosenDataset = null;
    private List<ChoiceItem> chosenScoringConcerns = null;
    private ChoiceItem chosenScoringConcern = null;
    private AlgorithmModules algorithmModules = null;
    //private Algorithm scoringAlgorithmProperties = null;
    //private ScoringAlgorithms.ScoringScope scoringScope = null;
    //private ScoringAlgorithms.ScoringSessionFocus scoringFocus = null;
    private ScoringAlgorithm chosenScoringAlgorithm = null;
    private DataFilter dataFilter = null;
    private String chosenSegmentationAlg = null;
    private String chosenOrientationAlg = null;
    //private String chosenScoringAlg = null;
    private AlgorithmSequence algorithmSequence = null;
    private NormalizedImageInfos normalizedImageInfos = null;
    /**
     * sessionImages are filled by the MBDataSource during 2nd metadata pull, in BisqueDataSource during first metadata pull,
     * It's contents will be filled after ScoringConcern choice
     */
    private SessionImages sessionImages = null;
    private Hashtable<String, ScoringSet> scoringSetForScoringConcernHash = new Hashtable<String, ScoringSet>();
    private NormalizedKey trainTestConcern = null;
    private boolean scoringModeIsEvaluation = true;
	public SessionInfo() throws AvatolCVException {
		File f = new File(AvatolCVFileSystem.getAvatolCVRootDir());
        if (!f.isDirectory()){
            throw new AvatolCVException("directory does not exist for being avatolCVRootDir " + AvatolCVFileSystem.getAvatolCVRootDir());
        }
        //File avatolCVRootParentFile = f.getParentFile();
        this.algorithmModules = AlgorithmModules.instance;
       
        //this.sessionID = "" + System.currentTimeMillis() / 1000L;
        this.sessionID = AvatolCVFileSystem.createSessionID();
        AvatolCVFileSystem.setSessionID(this.sessionID);
	}
	public String getSessionID(){
		return this.sessionID;
	}
	public List<NormalizedKey> getScoringSortingCandidates() throws AvatolCVException {
		List<ChoiceItem> scoringConcerns = getChosenScoringConcerns();
		List<NormalizedKey> scorableKeys = normalizedImageInfos.getScorableKeys();
		List<NormalizedKey> result = new ArrayList<NormalizedKey>();
		for (NormalizedKey key : scorableKeys){
			boolean keyDisqualified = false;
			String keyName = key.getName();
			for (ChoiceItem scoringConcern : scoringConcerns){
				NormalizedKey nKey = scoringConcern.getNormalizedKey();
				if (nKey.equals(key)){
					keyDisqualified = true;
				}
			}
			if (!keyDisqualified){
				result.add(key);
			}
		}
		return result;
	}
	/*
	 * sessionImages are filled by the DataSource when DataSource is instantiated
	 */
	public SessionImages getSessionImages(){
	    return this.sessionImages;
	}
	public NormalizedImageInfos getNormalizedImageInfos(){
	    return this.normalizedImageInfos;
	}
	public AlgorithmModules getAlgoritmModules(){
		return this.algorithmModules;
	}
	public void setDataSource(DataSource dataSource){
	    this.dataSource = dataSource;
	    AvatolCVFileSystem.setDatasourceName(dataSource.getName());
	    this.sessionImages = new SessionImages();
	    this.dataSource.setSessionImages(this.sessionImages);
	    this.chosenDataset = null;
	    this.dataFilter = null;
	    this.normalizedImageInfos = null;
	}
	public DataSource getDataSource(){
	    return this.dataSource;
	}
	public boolean arePointCoordinatesRelavent() throws AvatolCVException {
		return this.normalizedImageInfos.arePointCoordinatesRelavent();
	}
	
	public String getSessionsRootDir(){
	    return this.sessionsRootDir;
	}
	public void setChosenDataset(DatasetInfo di) throws AvatolCVException {
	    this.chosenDataset = di;
	    AvatolCVFileSystem.setChosenDataset(di);
	    //this.datasetDir = this.sessionsRootDir + FILESEP + di.getName();
	    this.dataSource.setChosenDataset(di);
	    this.normalizedImageInfos = new NormalizedImageInfos(AvatolCVFileSystem.getNormalizedImageInfoDir());
	    this.dataSource.setNormalizedImageInfos(this.normalizedImageInfos);
	}
	public List<ChoiceItem> getChosenScoringConcerns(){
	    List<ChoiceItem> result = new ArrayList<ChoiceItem>();
	    if (this.chosenScoringConcern != null){
	        result.add(this.chosenScoringConcern);
	    }
	    if (this.chosenScoringConcerns != null){
	        for (ChoiceItem ci : this.chosenScoringConcerns){
	            result.add(ci);
	        }
	    }
	    return result;
	}
	public void setScoringConcerns(List<ChoiceItem> chosenItems){
	    chosenScoringConcerns = chosenItems;
	    this.dataSource.setChosenScoringConcerns(chosenItems);
	}
	public void setScoringConcern(ChoiceItem chosenItem){
	    chosenScoringConcern = chosenItem;
	    this.dataSource.setChosenScoringConcern(chosenItem);
	}
	public void setSelectedScoringAlgName(String algName) throws AvatolCVException {
	    chosenScoringAlgorithm = this.algorithmModules.getScoringAlgorithm(algName);
	}
	public ScoringAlgorithm.ScoringScope getScoringScope(){
	    return this.chosenScoringAlgorithm.getScoringScope();
	}
    public ScoringAlgorithm.ScoringSessionFocus getScoringFocus(){
        return this.chosenScoringAlgorithm.getScoringFocus();
    }
    
    public void reAssessImagesInPlay() throws AvatolCVException {
    	this.normalizedImageInfos.focusToSession(this.sessionImages);
    }
    /*
     * DECIDED NEED TO KEEP ALL IMAGES IN PLAY AND CHECK EXCLUSION JUST BEFORE USE
    public List<String> removeExcludedImages(SessionImages sessionImages) throws AvatolCVException {
    	List<String> result = new ArrayList<String>();
    	for (String s : sessionImages){
    		String imageID = NormalizedImageInfo.getImageIDFromPath(s);
    		if (!ImageInfo.isExcluded(imageID)){
    			result.add(s);
    		}
    	}
    	return result;
    }
    */
    /*
     * SEGMENTATION
     */
    public void setChosenSegmentationAlgorithmName(String algName){
        this.chosenSegmentationAlg = algName;
    }
    public boolean isSegmentationAlgChosen(){
        if (null == this.chosenSegmentationAlg){
            return false;
        }
        return true;
    }
    public String getSegmentationAlgName(){
        return this.chosenSegmentationAlg;
    }
    public SegmentationAlgorithm getSelectedSegmentationAlgorithm() throws AvatolCVException {
        Algorithm a =  this.algorithmModules.getAlgWithName(getSegmentationAlgName(), AlgorithmModules.AlgType.SEGMENTATION);
        SegmentationAlgorithm sa = (SegmentationAlgorithm)a;
        return sa;
    }
    /*
     * ORIENTATION
     */
    public void setChosenOrientationAlgorithmName(String algName){
        this.chosenOrientationAlg = algName;
    }
    public boolean isOrientationAlgChosen(){
        if (null == this.chosenOrientationAlg){
            return false;
        }
        return true;
    }
    public String getOrientationAlgName(){
        return this.chosenOrientationAlg;
    }
    public OrientationAlgorithm getSelectedOrientationAlgorithm() throws AvatolCVException {
        Algorithm a =  this.algorithmModules.getAlgWithName(getOrientationAlgName(), AlgorithmModules.AlgType.ORIENTATION);
        OrientationAlgorithm oa = (OrientationAlgorithm)a;
        return oa;
    }
    /*
     * SCORING
     */
    public void setScoringModeToEvaluation(boolean value){
        this.scoringModeIsEvaluation = value;
    }
    public boolean isScoringModeEvaluation(){
        return this.scoringModeIsEvaluation;
    }
    public boolean isScoringAlgChosen(){
        if (null == this.chosenScoringAlgorithm){
            return false;
        }
        return true;
    }
    public String getScoringAlgName() throws AvatolCVException {
        return this.chosenScoringAlgorithm.getAlgName();
    }
    public ScoringAlgorithm getSelectedScoringAlgorithm() throws AvatolCVException {
        Algorithm a =  this.algorithmModules.getAlgWithName(getScoringAlgName(), AlgorithmModules.AlgType.SCORING);
        ScoringAlgorithm sa = (ScoringAlgorithm)a;
        return sa;
    }
    public void setScoringSetForConcernName(String name, ScoringSet scoringSet){
        this.scoringSetForScoringConcernHash.put(name,scoringSet);
    }
    public ScoringSet getScoringSetForScoringConcern(String name){
        return this.scoringSetForScoringConcernHash.get(name);
    }
    public boolean hasTrainTestConcern(){
    	return this.trainTestConcern != null;
    }
    public  NormalizedKey getTrainTestConcern(){
    	return this.trainTestConcern;
    }
    public void setTrainTestConcern(NormalizedKey trainTestConcern){
    	this.trainTestConcern = trainTestConcern;
    }
    public List<NormalizedValue> getValuesForTrainTestConcern(NormalizedKey trainTestConcern) throws AvatolCVException {
    	List<NormalizedValue> result = new ArrayList<NormalizedValue>();
    	List<NormalizedValue> nas = this.normalizedImageInfos.getValuesForKey(trainTestConcern);
    	for (NormalizedValue na : nas){
    		result.add(na);
    	}
    	return result;
    }
    public NormalizedKey getDefaultTrainTestConcern() throws AvatolCVException {
		String ttc = this.dataSource.getDefaultTrainTestConcern();
		if (null == ttc){
			return null;
		}
		return new NormalizedKey(ttc);
	}
    public boolean isAllImagesLabeled() throws AvatolCVException {
        List<NormalizedImageInfo> niis = this.normalizedImageInfos.getNormalizedImageInfosForSession();
        List<ChoiceItem> scoringConcerns = this.getChosenScoringConcerns();
        for (ChoiceItem ci : scoringConcerns){
            NormalizedKey nKey = ci.getNormalizedKey();
            for (NormalizedImageInfo nii : niis){
                if (nii.hasKey(nKey)){
                    if (!nii.hasValueForKey(nKey)){
                        return false;
                    }
                }
            }
        }
        return true;
    }
    /*
     * FILTER
     */
    public DataFilter getDataFilter() throws AvatolCVException {
    	if (this.dataFilter != null){
    		return this.dataFilter;
    	}
        this.dataFilter = new DataFilter(AvatolCVFileSystem.getSessionDir());
        List<NormalizedImageInfo> niis = this.normalizedImageInfos.getNormalizedImageInfosForSessionWithExcluded();
        for (NormalizedImageInfo nii : niis){
        	List<NormalizedKey> keys = nii.getKeys();
        	for (NormalizedKey key : keys){
        		if (!isKeyOneOfTheScoringConcerns(key) && 
        			!isKeySameTypeAsOneOfTheScoringConcerns(key) &&
        			!isKeyTheTrainTestConcern(key)){ // i.e. don't show taxon values for MB
        			NormalizedValue nv = nii.getValueForKey(key);
        			if (!nv.getName().equals("") && !nv.getName().equals("?")){
        				this.dataFilter.addFilterItem(key, nv, true);
        			}
        		}
        	}
        }
        /*
        String dir = AvatolCVFileSystem.getNormalizedImageInfoDir();
        File dirFile = new File(dir);
        File[] files = dirFile.listFiles();
        for (File file: files){
        	if (file.getName().endsWith(".txt")){
        		String path = file.getAbsolutePath();
        		Properties p = dataSource.getAvatolCVDataFiles().loadNormalizedImageFile(path);
        		Enumeration<Object> keysEnum = p.keys();
        		while (keysEnum.hasMoreElements()){
        			String key = (String)keysEnum.nextElement();
        			String val = p.getProperty(key);
        			addKeyValToFilter(this.dataFilter, key, val);
        		}
        	}
        }
        */
        return this.dataFilter;
    }
    public boolean isKeyTheTrainTestConcern(NormalizedKey key){
    	if (this.getDataSource().getDefaultTrainTestConcern().equals(key.getName())){
    		return true;
    	}
    	if (!this.hasTrainTestConcern()){
    		return false;
    	}
    	NormalizedKey ttKey = this.getTrainTestConcern();
    	if (key.equals(ttKey)){
    		return true;
    	}
    	return false;
    }
    public boolean isKeyOneOfTheScoringConcerns(NormalizedKey key){
    	List<ChoiceItem> cis = this.getChosenScoringConcerns();
    	for (ChoiceItem ci : cis){
    		NormalizedKey ciKey = ci.getNormalizedKey();
    		if (ciKey.equals(key)){
    			return true;
    		}
    	}
    	return false;
    }
   
    public boolean isKeySameTypeAsOneOfTheScoringConcerns(NormalizedKey key){
    	List<ChoiceItem> cis = this.getChosenScoringConcerns();
    	ChoiceItem ci = cis.get(0);
    	NormalizedKey ciKey = ci.getNormalizedKey();
    	if (ciKey.getType().equals(key.getType())){
    			return true;
    	}
    	return false;
    }
    public AlgorithmSequence getAlgorithmSequence() {
        if (this.algorithmSequence == null){
            this.algorithmSequence = new AlgorithmSequence();
        }
        return this.algorithmSequence;
    }
    
    public List<EvaluationSet> getEvaluationSets() throws AvatolCVException {
        List<ChoiceItem> scoringConcerns = getChosenScoringConcerns();
        List<EvaluationSet> esets = new ArrayList<EvaluationSet>();
    	for (ChoiceItem item : scoringConcerns){
    		NormalizedKey keyToScore = null;
    		if (item.hasNativeType()){
    			ScoringConcernDetails scd = (ScoringConcernDetails)item.getBackingObject();
        	    keyToScore = new NormalizedKey(NormalizedTypeIDName.buildTypeIdName(scd.getType(), scd.getID(), scd.getName())); 
        	    
    		}
    		else{
    			keyToScore = item.getNormalizedKey();
    		}
    		// in bisque case, we want key to just be the name part
            List<NormalizedImageInfo> niis = this.normalizedImageInfos.getNormalizedImageInfosForSession();
            EvaluationSet es = new EvaluationSet(niis, keyToScore, EvaluationSet.DEFAULT_EVALUATION_SPLIT);
            esets.add(es);
    	}
    	return esets;
    }
    public List<TrueScoringSet> getTrueScoringSets() throws AvatolCVException {
        List<ChoiceItem> scoringConcerns = getChosenScoringConcerns();
        List<TrueScoringSet> tssets = new ArrayList<TrueScoringSet>();
        for (ChoiceItem item : scoringConcerns){
            NormalizedKey keyToScore = item.getNormalizedKey();
            List<NormalizedImageInfo> niis = this.normalizedImageInfos.getNormalizedImageInfosForSession();
            TrueScoringSet tss = new TrueScoringSet(niis, keyToScore);
            tssets.add(tss);
        }
    	return tssets;
    }
    public List<NormalizedKey> getScoreConfigurationSortingValueOptions(ScoringSet ss){
        List<NormalizedKey> allKeys = ss.getAllKeys();
        List<NormalizedKey> result = new ArrayList<NormalizedKey>();
        if (this.chosenScoringConcerns != null){
            List<NormalizedKey> scoringConcernStrings = getScoringConcernKeys(this.chosenScoringConcerns);
            for (NormalizedKey key : allKeys){
                if (!scoringConcernStrings.contains(key)){
                    result.add(key);
                }
            }
        }
        else if (this.chosenScoringConcern != null){
            for (NormalizedKey key : allKeys){
                if (!this.chosenScoringConcern.getNormalizedKey().equals(key)){
                    result.add(key);
                }
            }
        }
        return result;
        
    }
    public List<NormalizedKey> getScoringConcernKeys(List<ChoiceItem> choiceItems){
        List<NormalizedKey> result = new ArrayList<NormalizedKey>();
        for (ChoiceItem item : choiceItems){
            result.add(item.getNormalizedKey());
        }
        return result;
    }
    public static boolean isBisqueSession(){
        return dataSource.getName().equals("bisque");
    }
    public void generateRunSummaries() throws AvatolCVException {
        List<ChoiceItem> scoringConcerns = getChosenScoringConcerns();
        String sessionIDRoot = AvatolCVFileSystem.createSessionID();
        if (scoringConcerns.size() == 1){
            ChoiceItem scoringConcern = scoringConcerns.get(0);
            String runID = sessionIDRoot + "_" + scoringConcern.getNormalizedKey().getName();
            generateRunSummary(runID, scoringConcern);
        }
        else {
            for (ChoiceItem scoringConcern : scoringConcerns){
                String runID = sessionIDRoot + "_" + scoringConcern.getNormalizedKey().getName();
                generateRunSummary(runID, scoringConcern);
            }
        }
    }
    public void generateRunSummary(String runID, ChoiceItem scoringConcern) throws AvatolCVException{
        RunSummary rs = new RunSummary(runID);
        if (this.scoringModeIsEvaluation){
        	rs.setScoringMode("evaluateAlgorithm");
        }
        else {
        	rs.setScoringMode("trueScoring");
        }
        
        rs.setScoringConcern(scoringConcern.getNormalizedKey().toString());
        rs.setDataset(this.chosenDataset.getName());
        rs.setDataSource(dataSource.getName());
        rs.setScoringAlgorithm(this.getScoringAlgName());
        rs.setRunID(runID);
        if (null != this.trainTestConcern){
            rs.setTrainTestConcern(this.trainTestConcern.toString());
        }
        List<NormalizedValue> nvs = this.normalizedImageInfos.getValuesForKey(scoringConcern.getNormalizedKey());
        for (NormalizedValue nv : nvs){
            rs.addScoringConcernValue(nv.toString());
        }
        rs.persist();
        
        
       
    }
}