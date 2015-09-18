package edu.oregonstate.eecs.iis.avatolcv.morphobank;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;

import edu.oregonstate.eecs.iis.avatolcv.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.core.FileUtils;
import edu.oregonstate.eecs.iis.avatolcv.core.ImageInfo;
import edu.oregonstate.eecs.iis.avatolcv.core.ScoringAlgorithms;
import edu.oregonstate.eecs.iis.avatolcv.core.SessionDataInterface;
import edu.oregonstate.eecs.iis.avatolcv.core.TrainTestInfo;
import edu.oregonstate.eecs.iis.avatolcv.ws.morphobank.AnnotationInfo.MBAnnotation;
import edu.oregonstate.eecs.iis.avatolcv.ws.morphobank.AnnotationInfo.MBAnnotationPoint;
import edu.oregonstate.eecs.iis.avatolcv.ws.morphobank.CellMediaInfo.MBMediaInfo;
import edu.oregonstate.eecs.iis.avatolcv.ws.morphobank.CharStateInfo.MBCharStateValue;
import edu.oregonstate.eecs.iis.avatolcv.ws.morphobank.CharacterInfo.MBCharState;
import edu.oregonstate.eecs.iis.avatolcv.ws.morphobank.CharacterInfo.MBCharacter;
import edu.oregonstate.eecs.iis.avatolcv.ws.morphobank.MatrixInfo.MBMatrix;
import edu.oregonstate.eecs.iis.avatolcv.ws.morphobank.TaxaInfo;
import edu.oregonstate.eecs.iis.avatolcv.ws.morphobank.TaxaInfo.MBTaxon;
import edu.oregonstate.eecs.iis.avatolcv.ws.morphobank.ViewInfo.MBView;
/*
 * Directory layout:
 * 
 * 
 * avatol_cv/sourceData/<datasource>/<dataset>/media/thumbnail
 *                                                  /large
 *                                                  /exclusions/<imageID>_imageQuality.txt
 *                                                  /rotations/<imageID>_rotateV.txt
 * avatol_cv/sourceData/<datasource>/<dataset>/trainingData/<key>.txt
 * avatol_cv/sourceData/<datasource>/<dataset>/annotations/<key>.txt

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
public class MBSessionData implements SessionDataInterface {

    public static final String ROTATION_STATES_DIRNAME = "userRotations";

    public static final String ROTATE_VERTICALLY = "rotateVerticaly";
    public static final String ROTATE_HORIZONTALLY = "rotateHorizontally";
    
    private static final String NL = System.getProperty("line.separator");
    private static final String FILESEP = System.getProperty("file.separator");
    public static final String STANDARD_IMAGE_FILE_EXTENSION = "jpg";
    private MBMatrix    currentMatrix    = null;
    //private MBCharacter currentCharacter = null;
    private List<MBCharacter> currentCharacters = null;
    private MBView      currentView      = null;
    
    private List<MBCharacter> charactersForCurrentMatrix = null;
    private List<MBTaxon> taxaForCurrentMatrix = null;

    private String sessionMatrixDir   = null;
    private String sessionViewDir     = null;
    private String sessionsRootDir = null;
    
    private List<MBMediaInfo> mbImages = null;

    private String sessionID = null;
    private SPRTaxonIdMapper mapper = null;
	private Hashtable<String, List<MBCharStateValue>> charStatesForCellHash = new Hashtable<String, List<MBCharStateValue>>();
		
    private Hashtable<String,List<MBCharStateValue>> charStateForCellHash = new Hashtable<String,List<MBCharStateValue>>();
    private Hashtable<String,MBMediaInfo> mbImageForID = new Hashtable<String,MBMediaInfo>();
    private Hashtable<String,ImageInfo> thumbnailForID = new Hashtable<String,ImageInfo>();
    private Hashtable<String,ImageInfo> imageSmallForID = new Hashtable<String,ImageInfo>();
    private Hashtable<String,ImageInfo> imageLargeForID = new Hashtable<String,ImageInfo>();

    private List<ImageInfo> imagesThumbnail = new ArrayList<ImageInfo>();
    private List<ImageInfo> imagesSmall = new ArrayList<ImageInfo>();
    private List<ImageInfo> imagesLarge = new ArrayList<ImageInfo>();
    private Hashtable<String, List<MBMediaInfo>> mediaForCell = new Hashtable<String,  List<MBMediaInfo>>();
    private ScoringAlgorithms scoringAlgorithms = null;
    private String chosenAlgorithm = null;
    public MBSessionData(String sessionsRootParent) throws AvatolCVException {
        File f = new File(sessionsRootParent);
        if (!f.isDirectory()){
            throw new AvatolCVException("directory does not exist for being sessionsRootParent " + sessionsRootParent);
        }
        
        this.sessionsRootDir = sessionsRootParent + FILESEP + "sessionData";
        f = new File(this.sessionsRootDir);
        if (!f.isDirectory()){
            f.mkdirs();
        }
        this.scoringAlgorithms = new ScoringAlgorithms();
        this.sessionID = "" + System.currentTimeMillis() / 1000L;
    }
    
    public String getSessionLogPath(String logName){
    	return sessionMatrixDir + FILESEP + this.sessionID + FILESEP + logName + ".log";
    }
    
    /*
     * Images
     */
    
    
    public ImageInfo getLargeImageForImage(ImageInfo ii) throws AvatolCVException {
    	String imageID = ii.getID();
    	ImageInfo large = this.imageLargeForID.get(imageID);
    	if (null == large){
    		throw new AvatolCVException("no large image found with imageID " + imageID);
    	}
    	return large;
    }
    public void acceptExclusions(){
        // for now the plan is to always check the exclusion property of ImageInfo when used, so no need to keep that data separate;
    }
    public void ensureImageDirsExists(){
        FileUtils.ensureDirExists(getImagesThumbnailDir());
        FileUtils.ensureDirExists(getImagesSmallDir());
        FileUtils.ensureDirExists(getImagesLargeDir());
        FileUtils.ensureDirExists(getImageMBMediaInfoDir());
        FileUtils.ensureDirExists(getImageExclusionStatesDir());
        FileUtils.ensureDirExists(getImageRotationStateDir());
        FileUtils.ensureDirExists(getSessionDir());
        FileUtils.ensureDirExists(getAnnotationDataDir());
        FileUtils.ensureDirExists(getTrainingDataDir());
    }
    public void clearImageMBMediaInfoDir(){
    	FileUtils.clearDir(getImageMBMediaInfoDir());
    }
    public void clearImageDirs(){
        FileUtils.clearDir(getImagesThumbnailDir());
        FileUtils.clearDir(getImagesSmallDir());
        FileUtils.clearDir(getImagesLargeDir());
    }

    public String getImageExclusionStateDir(){
    	return getImagesDir() + FILESEP + "exclusionState";
    }
    public String getImagesThumbnailDir(){
        return getImagesDir() + FILESEP + MBMediaInfo.IMAGE_SIZE_THUMBNAIL;
    } 
    public String getImagesSmallDir(){
        return getImagesDir() + FILESEP + MBMediaInfo.IMAGE_SIZE_SMALL;
    } 
    public String getImagesLargeDir(){
        return getImagesDir() + FILESEP + MBMediaInfo.IMAGE_SIZE_LARGE;
    }
    public String getImageRotationStateDir(){
       return getImagesDir() + FILESEP + ROTATION_STATES_DIRNAME;
    }

    public String getSessionDir(){
       return this.sessionMatrixDir + FILESEP + this.sessionID;
    }
    public String getImagesDir(){
       return this.sessionMatrixDir + FILESEP + "media";
    }
    public String getTrainingDataDir(){
        return this.sessionMatrixDir + FILESEP + "trainingData";
     }
    public String getAnnotationDataDir(){
        return this.sessionMatrixDir + FILESEP + "annotations";
     }
    public String getImageMBMediaInfoDir(){
        return getImagesDir() + FILESEP + "mbMediaInfo";
    }
    public String getImageExclusionStatesDir(){
    	return getImagesDir() + FILESEP + ImageInfo.EXCLUSION_STATES_DIRNAME;
    }
    public String getImageInfoDir(){
        return this.sessionMatrixDir + FILESEP + "imageInfo";
    }
    public static void generateImageInfoForSize(List<ImageInfo> listToFill, List<MBMediaInfo> mbImages, String width, String dir){
        for (MBMediaInfo mi : mbImages){
            String name = mi.getName(); // this will always be ""
            ImageInfo ii = new ImageInfo(dir, mi.getMediaID(), name, width, "", STANDARD_IMAGE_FILE_EXTENSION);
            ii.setNameAsUploadedOriginalForm(name);
            listToFill.add(ii);
        }
    }

   
    public List<ImageInfo> getImagesThumbnail(){
        return this.imagesThumbnail;
    }
    public List<ImageInfo> getImagesSmall(){
        return this.imagesSmall;
    }
    public List<ImageInfo> getImagesLarge(){
        return this.imagesLarge;
    }
    public void setCurrentImages(List<MBMediaInfo> mbImages){
        this.mbImages = mbImages;
        for (MBMediaInfo mi : mbImages){
            String id = mi.getMediaID();
            mbImageForID.put(id, mi);
        }
        generateImageInfoForSize(imagesThumbnail,mbImages,MBMediaInfo.IMAGE_SIZE_THUMBNAIL, getImagesThumbnailDir());
        for (ImageInfo ii : imagesThumbnail){
            String id = ii.getID();
            thumbnailForID.put(id, ii);
        }
        generateImageInfoForSize(imagesSmall,   mbImages,MBMediaInfo.IMAGE_SIZE_SMALL,    getImagesSmallDir());
        for (ImageInfo ii : imagesSmall){
            String id = ii.getID();
            imageSmallForID.put(id, ii);
        }
       
        generateImageInfoForSize(imagesLarge,    mbImages,MBMediaInfo.IMAGE_SIZE_LARGE,     getImagesLargeDir());
        for (ImageInfo ii : imagesLarge){
            String id = ii.getID();
            imageLargeForID.put(id, ii);
        }
    }
    public static String getMediaInfoFilenameRoot(List<String> list){
    	Collections.sort(list);
    	String result = "";
    	int i = 0;
    	for (; i < list.size() - 1; i++){
    		result = result + list.get(i) +  "_";
    	}
    	result = result + list.get(i);
    	return result;
    }
    public void persistRelevantMBMediaInfos(List<MBMediaInfo> relevantMediaInfos, String charID, String taxonID, String viewID) throws AvatolCVException {
    	List<String> idList = new ArrayList<String>();
    	idList.add(charID);
    	idList.add(taxonID);
    	idList.add(viewID);
    	String filenameRoot = MBSessionData.getMediaInfoFilenameRoot(idList);
    	String imageInfoDir = getImageMBMediaInfoDir();
    	String path = imageInfoDir + FILESEP + filenameRoot + ".txt";
    	try {
        	BufferedWriter writer = new BufferedWriter(new FileWriter(path));
        	for (MBMediaInfo mi : relevantMediaInfos){
        		String imageID = mi.getMediaID();
        		String viewId = mi.getViewID();
        		writer.write("imageID=" + imageID + ",viewID=" + viewId + NL);
        	}
        	writer.close();
    	}
    	catch(IOException ioe){
    		throw new AvatolCVException("problem persisting imageInfo to filepath: " + path);
    	}
    }
    public List<MBMediaInfo> loadMediaInfo(String charID, String taxonID, String viewID){
    	List<MBMediaInfo> list = new ArrayList<MBMediaInfo>();
    	List<String> idList = new ArrayList<String>();
    	idList.add(charID);
    	idList.add(taxonID);
    	idList.add(viewID);
    	String filenameRoot = MBSessionData.getMediaInfoFilenameRoot(idList);
    	String imageInfoDir = getImageMBMediaInfoDir();
    	String path = imageInfoDir + FILESEP + filenameRoot + ".txt";
    	File f = new File(path);
    	if (!f.exists()){
    		return list;// empty list
    	}
    	try {
    		BufferedReader reader = new BufferedReader(new FileReader(path));
    		String line = null;
    		while (null != (line = reader.readLine())){
    			String[] parts = line.split(",");
    			String mediaIdInfo = parts[0];
    			String viewIdInfo = parts[1];
    			String[] mediaIdInfoParts = mediaIdInfo.split("=");
    			String[] viewIdInfoParts = viewIdInfo.split("=");
    			String mediaId = mediaIdInfoParts[1];
    			String viewId = viewIdInfoParts[1];
    			MBMediaInfo mi = new MBMediaInfo();
    			mi.setMediaID(mediaId);
    			mi.setViewID(viewId);
    			list.add(mi);
    		}
    		reader.close();
    		return list;
    	}
    	catch(IOException ioe){
    		return list;
    	}
    	
    }
    
    /*
     * Rotation vertical
     */
    public boolean isRotatedVertically(ImageInfo ii){
        String path = getRotateVerticallyPath(ii.getID());
        File f = new File(path);
        if (f.exists()){
            return true;
        } 
        return false;
    }
    public String getRotateVerticallyPath(String imageID){
        return  getImageRotationStateDir() + FILESEP + imageID + "_" + ROTATE_VERTICALLY + ".txt";
    }
    public void rotateVertically(ImageInfo ii) throws AvatolCVException {
        String path = getRotateVerticallyPath(ii.getID());
        File f = new File(path);
        if (f.exists()){
            f.delete();
        }
        else {
            try {
                BufferedWriter writer = new BufferedWriter(new FileWriter(path));
                writer.write(ROTATE_VERTICALLY + NL);
                writer.close();
            }
            catch(IOException ioe){
                throw new AvatolCVException("problem writing rotateVertically state to path " + path);
            }
        }
    }
    
   
    /*
     * Rotation horizontal
     */
    public boolean isRotatedHorizontally(ImageInfo ii){
        String path = getRotateHorizontallyPath(ii.getID());
        File f = new File(path);
        if (f.exists()){
            return true;
        } 
        return false;
    }
    public String getRotateHorizontallyPath(String imageID){
        return  getImageRotationStateDir() + FILESEP + imageID + "_" + ROTATE_HORIZONTALLY + ".txt";
    }
    public void rotateHorizontally(ImageInfo ii) throws AvatolCVException {
        String path = getRotateHorizontallyPath(ii.getID());
        File f = new File(path);
        if (f.exists()){
            f.delete();
        }
        else {
            try {
                BufferedWriter writer = new BufferedWriter(new FileWriter(path));
                writer.write(ROTATE_HORIZONTALLY + NL);
                writer.close();
            }
            catch(IOException ioe){
                throw new AvatolCVException("problem writing rotateHorizontally state to path " + path);
            }
        }
    }
    /*
     * MATRICES
     */
    public void setChosenMatrix(MBMatrix mm){
        this.currentMatrix = mm;
        // ensure dir exists for this 
        this.sessionMatrixDir = this.sessionsRootDir + FILESEP + this.currentMatrix.getName();
        File f = new File (this.sessionMatrixDir);
        if (!f.isDirectory()){
            f.mkdirs();
        }
        ensureImageDirsExists();

    }
    @Override
    public MBMatrix getChosenMatrix(){
        return this.currentMatrix;
    }

    /*
     * Character
     */
    public void setCharactersForCurrentMatrix(List<MBCharacter> chars){
        this.charactersForCurrentMatrix = chars;
    }
    public List<MBCharacter> getCharactersForCurrentMatrix(){
        return this.charactersForCurrentMatrix;
    }
    public void setChosenCharacters(List<MBCharacter> chosenCharacters){
        this.currentCharacters = chosenCharacters;
    }

    public void setChosenCharacter(MBCharacter ch){
    	this.currentCharacters = new ArrayList<MBCharacter>();
        this.currentCharacters.add(ch);
    }
    @Override
    public List<MBCharacter> getChosenCharacters(){
        return this.currentCharacters;
    }
    public boolean isCharPresenceAbsence(String charName) throws AvatolCVException {
    	for (MBCharacter mbc : this.charactersForCurrentMatrix){
    		if (mbc.getCharName().equals(charName)){
    			return mbc.isPresenceAbsence();
    		}
    		
    	}
    	throw new AvatolCVException("charName " + charName + " does not refer to a known character");
    }
    /*
     * Taxa
     */
    public void setTaxaForCurrentMatrix(List<MBTaxon> taxa){
        this.taxaForCurrentMatrix = taxa;
        if (this.currentMatrix.getMatrixID().equals("1909")){
        	this.mapper = new SPRTaxonIdMapper(taxa);
    	}
        
    }
    public List<MBTaxon> getTaxaForCurrentMatrix(){
        return this.taxaForCurrentMatrix;
    }
    public boolean isSpecimenPerRowMatrix(){
    	if (this.currentMatrix.getMatrixID().equals("1909")){
    		return true;
    	}
    	return false;
    }
    public List<MBTaxon> getTrueTaxaForCurrentMatrix(){
    	if (isSpecimenPerRowMatrix()){
    		Hashtable<String, MBTaxon> taxonForIdHash = new Hashtable<String, MBTaxon>();
        	for (MBTaxon taxon : this.taxaForCurrentMatrix){
        		taxonForIdHash.put(taxon.getTaxonID(), taxon);
        	}
        	List<MBTaxon> trueTaxa = new ArrayList<MBTaxon>();
        	for (MBTaxon taxon : this.taxaForCurrentMatrix){
        		String id = this.mapper.getNormalizedTaxonId(taxon.getTaxonID());
        		MBTaxon trueTaxon = taxonForIdHash.get(id);
        		if (!trueTaxa.contains(trueTaxon)){
        			trueTaxa.add(trueTaxon);
        		}
        	}
        	return trueTaxa;
    	}
    	else {
    		return this.taxaForCurrentMatrix;
    	}
    	
    }
    /*
     * View
     */
    public void setChosenView(MBView v){
        this.currentView = v;
    }
    public MBView getChosenView(){
        return this.currentView;
    }
    @Override
    public String getCharQuestionsSourcePath() throws AvatolCVException {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public String getCharQuestionsAnsweredQuestionsPath()
            throws AvatolCVException {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public void setChosenAlgorithm(String s) {
        this.chosenAlgorithm = s;
    }
    @Override
    public String getTrainingTestingDescriminatorName() {
        return "Taxon";
    }
    @Override
    public List<MBTaxon> getTaxa() {
        return taxaForCurrentMatrix;
    }
    @Override
    public void setImagesForCell(String matrixID, String charID,
            String taxonID, List<MBMediaInfo> mediaInfos) {
        String key = matrixID + "_" + charID + "_" + taxonID;
        this.mediaForCell.put(key,mediaInfos);
    }
    @Override
    public List<MBMediaInfo> getImagesForCell(String matrixID, String charID,
            String taxonID){
        String key = matrixID + "_" + charID + "_" + taxonID;
        return this.mediaForCell.get(key);
    }
    @Override
    public ScoringAlgorithms getScoringAlgorithms() {
        return this.scoringAlgorithms;
    }

//make a getKey method that prepends identifiers, then alphabetizes, like c890984_m12345_t83477

//changte the other methods so they use the same key

//should I remove the matrixID as superfluous as the data is under a matrix-specific dir?
    
    public String getKeyForCell(String charID, String taxonID){
    	return "c" + charID + "_t" + taxonID;
    }
    public String getKeyForCellMedia(String charID, String taxonID, String mediaID){
    	return "c" + charID + "_m" + mediaID + "_t" + taxonID;
    }
	@Override
	public void registerAnnotationsForCell(
			List<MBAnnotation> annotationsForCell, String charID,
			String taxonID, String mediaID) throws AvatolCVException {
		String cellMediaKey = getKeyForCellMedia(charID, taxonID, mediaID);
		String path = getAnnotationDataDir() + FILESEP + cellMediaKey + ".txt";
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(path));
			for (MBAnnotation a : annotationsForCell){
				
				String type = a.getType();
				writer.write(type + ":");
				List<MBAnnotationPoint> points = a.getPoints();
				int i = 0;
				for (; i < points.size() - 1 ; i++){
					MBAnnotationPoint p = points.get(i);
					writer.write(p.getX() + "," + p.getY() + ";");
				}
				MBAnnotationPoint p = points.get(i);
				writer.write(p.getX() + "," + p.getY() + NL);
			}
			writer.close();
		}
		catch(IOException ioe){
			throw new AvatolCVException("problem writing annotation data for cell: char " + charID + " taxon " + taxonID + " mediaID " + mediaID);
		}
	}
	public boolean isStatesForCellOnDisk(String charID, String taxonID){
		String cellKey = getKeyForCell(charID, taxonID);
		String path = getTrainingDataDir() + FILESEP + cellKey + ".txt";
		File f = new File(path);
		if (f.exists()){
			return true;
		}
		return false;
	}
	public boolean isAnnotationOnDisk(String charID, String taxonID, String mediaID){
		String cellMediaKey = getKeyForCellMedia(charID, taxonID, mediaID);
		String path = getAnnotationDataDir() + FILESEP + cellMediaKey + ".txt";
		File f = new File(path);
		if (f.exists()){
			return true;
		}
		return false;
	}
	public String getTrainingDataPath(String cellKey){
		return getTrainingDataDir() + FILESEP + cellKey + ".txt";
	}
	public String getAnnotationPath(String cellMediaKey){
		return getAnnotationDataDir() + FILESEP + cellMediaKey + ".txt";
	}
	@Override
	public void registerStatesForCell(List<MBCharStateValue> statesForCell,
			String charID, String taxonID) throws AvatolCVException {
		String cellKey = getKeyForCell(charID, taxonID);
		String path = getTrainingDataPath(cellKey);
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(path));
			for (MBCharStateValue csv : statesForCell){
				
				String charState = csv.getCharStateID();
				writer.write(charState + NL);
			}
			writer.close();
		}
		catch(IOException ioe){
			throw new AvatolCVException("problem writing annotation data for cell: char " + charID + " taxon " + taxonID);
		}
	}
	public MBAnnotationPoint getAnnotationPointForString(String s){
		String[] coords = s.split(",");
		String x = coords[0];
		String y = coords[1];
		MBAnnotationPoint p = new MBAnnotationPoint();
		p.setX(new Double(x).doubleValue());
		p.setY(new Double(y).doubleValue());
		return p;
	}
	public List<MBAnnotation> loadAnnotationsForCellMedia(String charID, String taxonID, String mediaID) throws AvatolCVException {
		List<MBAnnotation> annotations = new ArrayList<MBAnnotation>();
		String cellMediaKey = getKeyForCellMedia(charID, taxonID, mediaID);
		String path = getAnnotationPath(cellMediaKey);
		try {
			BufferedReader reader = new BufferedReader(new FileReader(path));
			String line = null;
			while (null != (line = reader.readLine())){
				String[] parts = line.split(":");
				String type = parts[0];
				MBAnnotation annotation = new MBAnnotation();
				annotation.setType(type);
				String pointString = parts[1];
				List<MBAnnotationPoint> points = new ArrayList<MBAnnotationPoint>();
				if (type.equals(MBAnnotation.POINT)){
					MBAnnotationPoint p = getAnnotationPointForString(pointString);
					points.add(p);
					annotation.setPoints(points);
				}
				else {
					// rectangle or polygon - two or more points
					String[] pointStrings = pointString.split(";");
					for (String ps : pointStrings){
						MBAnnotationPoint p = getAnnotationPointForString(ps);
						points.add(p);
						annotation.setPoints(points);
					}
				}
				annotations.add(annotation);
			}
			reader.close();
			return annotations;
		}
		catch(IOException ioe){
			throw new AvatolCVException("problem writing annotation data for cell: char " + charID + " taxon " + taxonID + " mediaID " + mediaID);
		}
	}
	
	public void loadTrainingInfo() throws AvatolCVException {
		 for (MBCharacter character : charactersForCurrentMatrix){
			 for (MBTaxon taxon : taxaForCurrentMatrix){
				 String cellKey = this.getKeyForCell(character.getCharID(), taxon.getTaxonID());
				 String path = getTrainingDataPath(cellKey);
				 List<MBCharStateValue> stateIds = new ArrayList<MBCharStateValue>();
				 File f = new File(path);
				 if (f.exists()){
					 try {
						 BufferedReader reader = new BufferedReader(new FileReader(path));
						 String line = null;
						 while (null != (line = reader.readLine())){
							 MBCharStateValue charStateValue = new MBCharStateValue();
							 charStateValue.setCellID(cellKey);
							 charStateValue.setCharStateID(line);
							 stateIds.add(charStateValue);
						 }
						 charStatesForCellHash.put(cellKey, stateIds);
					 }
					 catch(IOException ioe){
						 throw new AvatolCVException("problem reading training info for cellKey " + cellKey);
					 }
				 }
			 }
		 }

	}
	public TrainTestInfo getTrainTestInfo(String taxonID, String charID) {
		//loadTrainingInfo();
		//left off here...
		TrainTestInfo iit = new TrainTestInfo(taxonID, charID);
		//iit.setExcludedCount(getExludedCount(taxonID, charID));
		//iit.setTrainingCount(getTrainingCount(taxonID, charID));
		//iit.setTestCount(getToTestCount(taxonID, charID));
		return iit;
	}
	
	public int getExcludedCount(String taxonID, String charId){
		return 0;
	}
   
    
}
