package edu.oregonstate.eecs.iis.avatolcv.core;

import java.io.File;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

import edu.oregonstate.eecs.iis.avatolcv.AvatolCVFileSystem;
import edu.oregonstate.eecs.iis.avatolcv.generic.DatasetInfo;
import edu.oregonstate.eecs.iis.avatolcv.ws.MorphobankWSException;
import edu.oregonstate.eecs.iis.avatolcv.ws.morphobank.CharacterInfo.MBCharacter;
import edu.oregonstate.eecs.iis.avatolcv.ws.morphobank.TaxaInfo.MBTaxon;
import edu.oregonstate.eecs.iis.avatolcv.ws.morphobank.ViewInfo.MBView;

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
    private ScoringAlgorithms scoringAlgorithms = null;
    private String sessionID = null;
    private DataSource dataSource = null;
    private DatasetInfo chosenDataset = null;
    private List<ChoiceItem> chosenScoringConcerns = null;
    private ChoiceItem chosenScoringConcern = null;
    private AlgorithmModules algorithmModules = null;
    private AlgorithmProperties scoringAlgorithmProperties = null;
    private ScoringAlgorithms.ScoringScope scoringScope = null;
    private ScoringAlgorithms.ScoringSessionFocus scoringFocus = null;
    public static AvatolCVExceptionExpresser exceptionExpresser = null;
    private DataFilter dataFilter = null;
    
	public SessionInfo(AvatolCVExceptionExpresser exceptionExpresser) throws AvatolCVException {
        SessionInfo.exceptionExpresser = exceptionExpresser;
		File f = new File(AvatolCVFileSystem.getAvatolCVRootDir());
        if (!f.isDirectory()){
            throw new AvatolCVException("directory does not exist for being avatolCVRootDir " + AvatolCVFileSystem.getAvatolCVRootDir());
        }
        //File avatolCVRootParentFile = f.getParentFile();
        this.algorithmModules = new AlgorithmModules(AvatolCVFileSystem.getModulesDir());
        this.scoringAlgorithms = algorithmModules.getScoringAlgorithms();
       
        this.sessionID = "" + System.currentTimeMillis() / 1000L;
        AvatolCVFileSystem.setSessionID(this.sessionID);
	}
	
	public void setDataSource(DataSource dataSource){
	    this.dataSource = dataSource;
	    AvatolCVFileSystem.setDatasourceName(dataSource.getName());
	}
	public DataSource getDataSource(){
	    return this.dataSource;
	}
	public ScoringAlgorithms getScoringAlgorithms() {
        return this.scoringAlgorithms;
    }
	public String getSessionsRootDir(){
	    return this.sessionsRootDir;
	}
	public void setChosenDataset(DatasetInfo di) throws AvatolCVException {
	    this.chosenDataset = di;
	    AvatolCVFileSystem.setChosenDataset(di);
	    //this.datasetDir = this.sessionsRootDir + FILESEP + di.getName();
	    this.dataSource.setChosenDataset(di);
	}
	
	public void setScoringConcerns(List<ChoiceItem> chosenItems){
	    chosenScoringConcerns = chosenItems;
	    this.dataSource.setChosenScoringConcerns(chosenItems);
	}
	public void setScoringConcern(ChoiceItem chosenItem){
	    chosenScoringConcern = chosenItem;
	    this.dataSource.setChosenScoringConcern(chosenItem);
	}
	public void setScoringAlgInfo(ScoringAlgorithms.ScoringSessionFocus focus, String algName) throws AvatolCVException {
	    this.scoringAlgorithmProperties = this.algorithmModules.getAlgPropertiesForAlgName(algName, AlgorithmModules.AlgType.SCORING);
	    this.scoringScope = ScoringAlgorithms.getScopeForScopePropertiesValue(this.scoringAlgorithmProperties.getProperty(AlgorithmProperties.PROPERTY_SCORING_SCOPE));
	    this.scoringFocus = focus;
	}
	public ScoringAlgorithms.ScoringScope getScoringScope(){
	    return this.scoringScope;
	}
    public ScoringAlgorithms.ScoringSessionFocus getScoringFocus(){
        return this.scoringFocus;
    }
    public DataFilter getDataFilter() throws AvatolCVException {
        this.dataFilter = new DataFilter(AvatolCVFileSystem.getSessionDir());
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
        return this.dataFilter;
    }
    public void addKeyValToFilter(DataFilter dataFilter, String key, String val) throws AvatolCVException {
    	//character:1824356|Diastema between M1 and M2=characterState:4884340|Diastema absent
    	//taxon=773126|Artibeus jamaicensis
    	//view=8905|Skull - ventral annotated teeth
    	// if there is a type prefix (something:), then type and string value of type are what's added to the filter (ex : character, Diastema between M1 and M2)
    	// otherwise, the key and the string value portion of the value (ex: taxon, Artibeus jamaicensis)
    	if (key.contains(":")){
    		String[] parts = key.split(":");
    		String type = parts[0];
    		String propertyInfo = parts[1];
    		String[] propertyInfoParts = propertyInfo.split("|");
    		String valueID = propertyInfoParts[0];
    		String propertyValueWeWillUse = propertyInfoParts[1];
    		dataFilter.addPropertyValue(type, propertyValueWeWillUse, valueID, true);
    	}
    	else {
    		String[] valParts = val.split("|");
    		String valID = valParts[0];
    		String valName = valParts[1];
    		dataFilter.addPropertyValue(key, valName, valID, true);
    	}
    }
}