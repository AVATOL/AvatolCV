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

import edu.oregonstate.eecs.iis.avatolcv.core.AvatolCVDataFiles;
import edu.oregonstate.eecs.iis.avatolcv.core.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.ws.morphobank.CellMediaInfo.MBMediaInfo;
import edu.oregonstate.eecs.iis.avatolcv.ws.morphobank.CharStateInfo.MBCharStateValue;

public class MorphobankDataFiles  implements AvatolCVDataFiles{
    private static final String FILESEP = System.getProperty("file.separator");
    private static final String NL = System.getProperty("line.separator");
    private String datasetDir = null;
    private String sessionsRoot = null;
    public MorphobankDataFiles(){
    }
    public String getImageInfoDir(){
        return datasetDir + FILESEP + "mbData" + FILESEP + "mediaInfo";
    }
    public String getCharStateInfoDir(){
        return datasetDir + FILESEP + "mbData" + FILESEP + "charStates";
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
    public void persistMBMediaInfosForCell(List<MBMediaInfo> mediaInfos, String charID, String taxonID) throws AvatolCVException {
        String imageInfoRootDir = getImageInfoDir();
        File f = new File(imageInfoRootDir);
        f.mkdirs();
        String keyForCell = MorphobankDataSource.getKeyForCell(charID, taxonID);
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
                writer.write("#  charID: " + charID + ", taxonID: " + taxonID + NL);
                writer.close();
            }
            else {
                for (String viewID : viewIDs){
                    List<MBMediaInfo> mediaInfosForView = mediaInfosForViewHash.get(viewID);
                    path = imageInfoRootDir + FILESEP + keyForCell + "_v" + viewID + ".txt";
                    BufferedWriter writer = new BufferedWriter(new FileWriter(path));
                    writer.write("#  charID: " + charID + ", taxonID: " + taxonID + NL);
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
    @Override
    public void setSessionsRoot(String sessionsRoot) {
        this.sessionsRoot = sessionsRoot;
    }
    @Override
    public void setDatasetDirname(String datasetDirName) {
        this.datasetDir = this.sessionsRoot + FILESEP + datasetDirName;
    }
}
