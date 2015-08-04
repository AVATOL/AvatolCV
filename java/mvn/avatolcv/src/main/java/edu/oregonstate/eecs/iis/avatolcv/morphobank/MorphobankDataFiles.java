package edu.oregonstate.eecs.iis.avatolcv.morphobank;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import edu.oregonstate.eecs.iis.avatolcv.core.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.ws.morphobank.CellMediaInfo.MBMediaInfo;

public class MorphobankDataFiles {
    private static final String FILESEP = System.getProperty("file.separator");
    private static final String NL = System.getProperty("line.separator");

    public void persistMBMediaInfosForCell(List<MBMediaInfo> mediaInfos, String charID, String taxonID) throws AvatolCVException {
        String imageInfoRootDir = "???";
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
            for (String viewID : viewIDs){
                List<MBMediaInfo> mediaInfosForView = mediaInfosForViewHash.get(viewID);
                for (MBMediaInfo mi : mediaInfosForView){
                    path = imageInfoRootDir + FILESEP + keyForCell + "_" + viewID + ".txt";
                    BufferedWriter writer = new BufferedWriter(new FileWriter(path));
                    String imageID = mi.getMediaID();
                    String viewId = mi.getViewID();
                    writer.write("imageID=" + imageID + ",viewID=" + viewId + NL);
                    writer.close();
                }
            }
        }
        catch(IOException ioe){
            throw new AvatolCVException("problem persisting imageInfo to filepath: " + path);
        }
    }
}
