package edu.oregonstate.eecs.iis.avatolcv.morphobank;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import edu.oregonstate.eecs.iis.avatolcv.AvatolCVFileSystem;
import edu.oregonstate.eecs.iis.avatolcv.core.AvatolCVDataFiles;
import edu.oregonstate.eecs.iis.avatolcv.core.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.ws.morphobank.AnnotationInfo.MBAnnotation;
import edu.oregonstate.eecs.iis.avatolcv.ws.morphobank.AnnotationInfo.MBAnnotationPoint;
import edu.oregonstate.eecs.iis.avatolcv.ws.morphobank.CellMediaInfo.MBMediaInfo;
import edu.oregonstate.eecs.iis.avatolcv.ws.morphobank.CharStateInfo.MBCharStateValue;
import edu.oregonstate.eecs.iis.avatolcv.ws.morphobank.CharacterInfo.MBCharacter;
import edu.oregonstate.eecs.iis.avatolcv.ws.morphobank.TaxaInfo.MBTaxon;

public class MorphobankDataFiles  extends AvatolCVDataFiles{
    private static final String FILESEP = System.getProperty("file.separator");
    private static final String NL = System.getProperty("line.separator");
    private String datasetDir = null;
    private String sessionsRoot = null;
    public MorphobankDataFiles(){
    }
    public void prepareForMetadataDownload() throws AvatolCVException {
    	AvatolCVFileSystem.ensureDir(getAnnotationDataDir());
    }
    public String getImageInfoDir() throws AvatolCVException {
        return AvatolCVFileSystem.getSpecializedDataDir() + FILESEP + "mediaInfo";
    }
    public String getCharStateInfoDir() throws AvatolCVException {
        return AvatolCVFileSystem.getSpecializedDataDir() + FILESEP + "charStates";
    }
    public void persistMBCharStatesForCell(List<MBCharStateValue> charStatesForCell, String charID, String taxonID) throws AvatolCVException {
        String charStateInfoRootDir = getCharStateInfoDir();
        File f = new File(charStateInfoRootDir);
        f.mkdirs();
        String keyForCell = MorphobankDataSource.getKeyForCell(charID, taxonID);
        String path = charStateInfoRootDir + FILESEP + keyForCell + ".txt";
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(path));
            writer.write("#  charID: " + charID + ", taxonID: " + taxonID + NL);
            for (MBCharStateValue cs : charStatesForCell){
                String cellID = cs.getCellID();
                String charStateID = cs.getCharStateID();
                writer.write("cellID=" + cellID + ",charStateID=" + charStateID + NL);
            }
            writer.close();
        }
        catch(IOException ioe){
            throw new AvatolCVException("problem persisting imageInfo to filepath: " + path);
        }
    }
    public List<MBCharStateValue> loadMBCharStatesFromDisk(String charID, String taxonID) throws AvatolCVException {
        List<MBCharStateValue> result = new ArrayList<MBCharStateValue>();
        String charStateInfoRootDir = getCharStateInfoDir();
        String keyForCell = MorphobankDataSource.getKeyForCell(charID, taxonID);
        File charStateInfoDirFile = new File(charStateInfoRootDir);
        boolean foundAFile = false;
        if (charStateInfoDirFile.exists()){
            File[] files = charStateInfoDirFile.listFiles();
            for (File f : files){
                if (f.getName().startsWith(keyForCell)){
                    foundAFile = true;
                    try {
                        BufferedReader reader = new BufferedReader(new FileReader(f.getAbsolutePath()));
                        String line = null;
                        while (null != (line = reader.readLine())){
                            if (!line.startsWith("#")){
                                String[] parts = line.split(",");
                                String cellIdInfo = parts[0];
                                String charStateInfo = parts[1];
                                String[] cellIdInfoParts = cellIdInfo.split("=");
                                String[] charStateInfoParts = charStateInfo.split("=");
                                String cellId = cellIdInfoParts[1];
                                String charStateId = charStateInfoParts[1];
                                MBCharStateValue csv = new MBCharStateValue();
                                csv.setCellID(cellId);
                                csv.setCharStateID(charStateId);
                                result.add(csv);
                            }
                        }
                        reader.close();
                    }
                    catch(IOException ioe){
                        throw new AvatolCVException("could not open mediaInfo file " + f.getAbsolutePath());
                    }
                }
            }
        }
        if (foundAFile){
            return result;
        }
        return null;
    }
    public String getHeaderForMediaInfoFile(MBCharacter character, MBTaxon taxon){
    	return "#  character: " + character.getCharID() + "," + character.getCharName() + ", taxon: " + taxon.getTaxonID()+ "," + taxon.getTaxonName() + NL;
    }
    public void persistMBMediaInfosForCell(List<MBMediaInfo> mediaInfos, MBCharacter character, MBTaxon taxon) throws AvatolCVException {
        String imageInfoRootDir = getImageInfoDir();
        File f = new File(imageInfoRootDir);
        f.mkdirs();
        String keyForCell = MorphobankDataSource.getKeyForCell(character.getCharID(), taxon.getTaxonID());
        String path = "???";
        try {
            List<String> viewIDs = new ArrayList<String>();
            Hashtable<String, List<MBMediaInfo>> mediaInfosForViewHash = new Hashtable<String, List<MBMediaInfo>>();
            // sort the media infos by view
            for (MBMediaInfo mi : mediaInfos){
                String viewID = mi.getViewID();
                List<MBMediaInfo> mediaInfosForView = null;
                if (viewIDs.contains(viewID)){
                    mediaInfosForView = mediaInfosForViewHash.get(viewID);
                }
                else {
                    mediaInfosForView = new ArrayList<MBMediaInfo>();
                    mediaInfosForViewHash.put(viewID, mediaInfosForView);
                    viewIDs.add(viewID);
                }
                mediaInfosForView.add(mi);
            }
            // now the MBmediaInfos are sorted by view and we can lay down the files for each view
            if (viewIDs.isEmpty()){
                path = imageInfoRootDir + FILESEP + keyForCell + "_vNone.txt";
                BufferedWriter writer = new BufferedWriter(new FileWriter(path));
                writer.write(getHeaderForMediaInfoFile(character, taxon));
                writer.close();
            }
            else {
                for (String viewID : viewIDs){
                    List<MBMediaInfo> mediaInfosForView = mediaInfosForViewHash.get(viewID);
                    path = imageInfoRootDir + FILESEP + keyForCell + "_v" + viewID + ".txt";
                    BufferedWriter writer = new BufferedWriter(new FileWriter(path));
                    writer.write(getHeaderForMediaInfoFile(character, taxon));
                    for (MBMediaInfo mi : mediaInfosForView){
                        String imageID = mi.getMediaID();
                        String viewId = mi.getViewID();
                        writer.write("imageID=" + imageID + ",viewID=" + viewId + NL);
                    }
                    writer.close();
                }
            }
            
        }
        catch(IOException ioe){
            throw new AvatolCVException("problem persisting imageInfo to filepath: " + path);
        }
    }
    public List<MBMediaInfo> loadMBMediaInfosForCell(String charID, String taxonID) throws AvatolCVException {
        List<MBMediaInfo> result = new ArrayList<MBMediaInfo>();
        String imageInfoRootDir = getImageInfoDir();
        String keyForCell = MorphobankDataSource.getKeyForCell(charID, taxonID);
        File imageInfoDirFile = new File(imageInfoRootDir);
        boolean foundAFile = false;
        if (imageInfoDirFile.exists()){
            File[] files = imageInfoDirFile.listFiles();
            for (File f : files){
                if (f.getName().startsWith(keyForCell)){
                    try {
                        foundAFile = true;
                        BufferedReader reader = new BufferedReader(new FileReader(f.getAbsolutePath()));
                        String line = null;
                        while (null != (line = reader.readLine())){
                            if (!line.startsWith("#")){
                                String[] parts = line.split(",");
                                String imageIdInfo = parts[0];
                                String viewIdInfo = parts[1];
                                String[] imageIdInfoParts = imageIdInfo.split("=");
                                String[] viewIdInfoParts = viewIdInfo.split("=");
                                String imageId = imageIdInfoParts[1];
                                String viewId = viewIdInfoParts[1];
                                MBMediaInfo mi = new MBMediaInfo();
                                mi.setMediaID(imageId);
                                mi.setViewID(viewId);
                                result.add(mi);
                            }
                        }
                        reader.close();
                    }
                    catch(IOException ioe){
                        throw new AvatolCVException("could not open mediaInfo file " + f.getAbsolutePath());
                    }
                }
            }
        }
        if (foundAFile){
            return result;
        }
        return null;
    }
    private String getAnnotationDataDir() throws AvatolCVException {
    	return AvatolCVFileSystem.getSpecializedDataDir() + FILESEP + "annotations";
    }
    
    public String getKeyForCellMedia(String charID, String taxonID, String mediaID){
    	return "c" + charID + "_m" + mediaID + "_t" + taxonID;
    }
    public String getAnnotationFilePath(String charID, String taxonID, String mediaID) throws AvatolCVException {
    	String cellMediaKey = getKeyForCellMedia(charID, taxonID, mediaID);
		return getAnnotationDataDir() + FILESEP + cellMediaKey + ".txt";
    }
    public List<MBAnnotation> loadMBAnnotationsFromDisk(String charID, String taxonID, String mediaID) throws AvatolCVException{
    	List<MBAnnotation> annotations = new ArrayList<MBAnnotation>();
    	String path = getAnnotationFilePath(charID, taxonID, mediaID);
    	File f = new File(path);
    	if (!f.exists()){
    		return null;
    	}
    	try {
    		BufferedReader reader = new BufferedReader(new FileReader(path));
    		String line = null;
    		while (null != (line = reader.readLine())){
    			MBAnnotation a = new MBAnnotation();
    			//point:73.33903133903134,45.8670465337132
    			String[] parts = line.split(":");
    			String type = parts[0];
    			String pointsInfo = parts[1];
    			String[] points = pointsInfo.split(";");
    			List<MBAnnotationPoint> pointList = new ArrayList<MBAnnotationPoint>();
    			for (String point : points){
    				String[] pointParts = point.split(",");
    				String x = pointParts[0];
    				String y = pointParts[1];
    				MBAnnotationPoint p = new MBAnnotationPoint();
    				p.setX(new Double(x).doubleValue());
    				p.setY(new Double(y).doubleValue());
    				pointList.add(p);
    			}
    			a.setType(type);
    			a.setPoints(pointList);
    			annotations.add(a);
    		}
    		reader.close();
    		return annotations;
    	}
    	catch(IOException ioe){
    		throw new AvatolCVException("problem loading MBAnnotations for " + charID + " " + taxonID + " " + mediaID);
    	}
    }
    public void persistAnnotationsForCell(
			List<MBAnnotation> annotationsForCell, String charID,
			String taxonID, String mediaID) throws AvatolCVException {
    	String path = getAnnotationFilePath(charID, taxonID, mediaID);
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
    /*
    @Override
    public void setSessionsRoot(String sessionsRoot) {
        this.sessionsRoot = sessionsRoot;
    }
    @Override
    public void setDatasetDirname(String datasetDirName) {
        this.datasetDir = this.sessionsRoot + FILESEP + datasetDirName;
    }
    */
}
