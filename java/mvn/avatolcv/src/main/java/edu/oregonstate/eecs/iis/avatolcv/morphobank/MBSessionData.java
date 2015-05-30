package edu.oregonstate.eecs.iis.avatolcv.morphobank;

import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import edu.oregonstate.eecs.iis.avatolcv.core.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.core.FileUtils;
import edu.oregonstate.eecs.iis.avatolcv.core.ImageInfo;
import edu.oregonstate.eecs.iis.avatolcv.core.SessionData;
import edu.oregonstate.eecs.iis.avatolcv.ws.morphobank.CellMediaInfo.MBMediaInfo;
import edu.oregonstate.eecs.iis.avatolcv.ws.morphobank.CharacterInfo.MBCharacter;
import edu.oregonstate.eecs.iis.avatolcv.ws.morphobank.MatrixInfo.MBMatrix;
import edu.oregonstate.eecs.iis.avatolcv.ws.morphobank.TaxaInfo.MBTaxon;
import edu.oregonstate.eecs.iis.avatolcv.ws.morphobank.ViewInfo.MBView;

public class MBSessionData implements SessionData {
    
    private static final String FILESEP = System.getProperty("file.separator");
    public static final String STANDARD_IMAGE_FILE_EXTENSION = "jpg";
    private MBMatrix    currentMatrix    = null;
    private MBCharacter currentCharacter = null;
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
    }
    /*
     * Images
     */
    public void acceptExclusions(){
        // for now the plan is to always check the exclusion property of ImageInfo when used, so no need to keep that data separate;
    }
    public void ensureImageDirsExists(){
        FileUtils.ensureDirExists(getImagesThumbnailDir());
        FileUtils.ensureDirExists(getImagesSmallDir());
        FileUtils.ensureDirExists(getImagesLargeDir());
        
    }
    public void clearImageDirs(){
        FileUtils.clearDir(getImagesThumbnailDir());
        FileUtils.clearDir(getImagesSmallDir());
        FileUtils.clearDir(getImagesLargeDir());
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
        
    }
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
    public void setChosenCharacter(MBCharacter ch){
        this.currentCharacter = ch;
    }
    public MBCharacter getChosenCharacter(){
        return this.currentCharacter;
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
}
