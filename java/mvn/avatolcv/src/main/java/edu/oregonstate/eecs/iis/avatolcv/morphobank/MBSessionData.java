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

import edu.oregonstate.eecs.iis.avatolcv.core.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.core.FileUtils;
import edu.oregonstate.eecs.iis.avatolcv.core.ImageInfo;
import edu.oregonstate.eecs.iis.avatolcv.core.ScoringAlgorithms;
import edu.oregonstate.eecs.iis.avatolcv.core.SessionData;
import edu.oregonstate.eecs.iis.avatolcv.ws.morphobank.CellMediaInfo.MBMediaInfo;
import edu.oregonstate.eecs.iis.avatolcv.ws.morphobank.CharacterInfo.MBCharState;
import edu.oregonstate.eecs.iis.avatolcv.ws.morphobank.CharacterInfo.MBCharacter;
import edu.oregonstate.eecs.iis.avatolcv.ws.morphobank.MatrixInfo.MBMatrix;
import edu.oregonstate.eecs.iis.avatolcv.ws.morphobank.TaxaInfo;
import edu.oregonstate.eecs.iis.avatolcv.ws.morphobank.TaxaInfo.MBTaxon;
import edu.oregonstate.eecs.iis.avatolcv.ws.morphobank.ViewInfo.MBView;

public class MBSessionData implements SessionData {
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
    private String sessionDataRootDir = null;
    
    private List<MBMediaInfo> mbImages = null;

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
    public MBSessionData(String sessionDataRootParent) throws AvatolCVException {
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
    public String getImagesDir(){
       return this.sessionMatrixDir + FILESEP + "media";
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
     * MATRICES
     */
    public void setChosenMatrix(MBMatrix mm){
        this.currentMatrix = mm;
        // ensure dir exists for this 
        this.sessionMatrixDir = this.sessionDataRootDir + FILESEP + this.currentMatrix.getName();
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
    }
    public List<MBTaxon> getTaxaForCurrentMatrix(){
        return this.taxaForCurrentMatrix;
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
}
