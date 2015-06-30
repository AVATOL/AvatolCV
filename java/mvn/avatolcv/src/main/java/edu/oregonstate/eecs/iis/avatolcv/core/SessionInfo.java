package edu.oregonstate.eecs.iis.avatolcv.core;

import java.io.File;

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

	public SessionInfo(String sessionDataRootParent) throws AvatolCVException {
		File f = new File(sessionDataRootParent);
        if (!f.isDirectory()){
            throw new AvatolCVException("directory does not exist for being sessionDataRootParent " + sessionDataRootParent);
        }
        
        this.sessionDataRootDir = sessionDataRootParent + FILESEP + "sessionData";
        f = new File(this.sessionDataRootDir);
        if (!f.isDirectory()){
            f.mkdirs();
        }
        this.scoringAlgorithms = new ScoringAlgorithms();
        this.sessionID = "" + System.currentTimeMillis() / 1000L;
	}
	public ScoringAlgorithms getScoringAlgorithms() {
        return this.scoringAlgorithms;
    }
}