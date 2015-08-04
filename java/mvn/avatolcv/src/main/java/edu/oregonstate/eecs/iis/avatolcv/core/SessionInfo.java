package edu.oregonstate.eecs.iis.avatolcv.core;

import java.io.File;
import java.util.List;

import edu.oregonstate.eecs.iis.avatolcv.generic.DatasetInfo;
import edu.oregonstate.eecs.iis.avatolcv.ws.MorphobankWSException;
import edu.oregonstate.eecs.iis.avatolcv.ws.morphobank.CharacterInfo.MBCharacter;
import edu.oregonstate.eecs.iis.avatolcv.ws.morphobank.TaxaInfo.MBTaxon;

/*
 * Directory layout:
 * 
 * 
 * avatol_cv/sessionData/<dataset>/images/thumbnail
 *                                       /large
 *                                       /exclusions/<imageID>_imageQuality.txt
 *                                       /rotations/<imageID>_rotateV.txt
 * avatol_cv/sessionData/<dataset>/imageMetadata/<imageID>.txt

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
 *          /sessionData/<sessionID>/
 *                        
 *                    
 * 
 * 
 * 
 * 
 * 
 */
public class SessionInfo{
    private String sessionDataRootDir = null;
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
    private String datasetDir = null;
    
	public SessionInfo(String avatolCVRootDir, AvatolCVExceptionExpresser exceptionExpresser) throws AvatolCVException {
        SessionInfo.exceptionExpresser = exceptionExpresser;
		File f = new File(avatolCVRootDir);
        if (!f.isDirectory()){
            throw new AvatolCVException("directory does not exist for being avatolCVRootDir " + avatolCVRootDir);
        }
        //File avatolCVRootParentFile = f.getParentFile();
        String moduleRootDir = avatolCVRootDir + FILESEP + "modules";
        this.algorithmModules = new AlgorithmModules(moduleRootDir);
        this.scoringAlgorithms = algorithmModules.getScoringAlgorithms();
        
        this.sessionDataRootDir = avatolCVRootDir + FILESEP + "sessionData";
        f = new File(this.sessionDataRootDir);
        if (!f.isDirectory()){
            f.mkdirs();
        }
        
        this.sessionID = "" + System.currentTimeMillis() / 1000L;
	}
	public void setDataSource(DataSource dataSource){
	    this.dataSource = dataSource;
	}
	public DataSource getDataSource(){
	    return this.dataSource;
	}
	public ScoringAlgorithms getScoringAlgorithms() {
        return this.scoringAlgorithms;
    }
	public String getSessionDataRootDir(){
	    return this.sessionDataRootDir;
	}
	public void setChosenDataset(DatasetInfo di) throws AvatolCVException {
	    this.chosenDataset = di;
	    this.datasetDir = this.sessionDataRootDir + FILESEP + di.getName();
	    this.dataSource.setChosenDataset(di);
	}
	public String getDatasetDir(){
	    return this.datasetDir;
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
}