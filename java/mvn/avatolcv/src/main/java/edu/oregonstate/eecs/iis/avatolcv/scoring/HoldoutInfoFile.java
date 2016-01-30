package edu.oregonstate.eecs.iis.avatolcv.scoring;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import edu.oregonstate.eecs.iis.avatolcv.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.normalized.NormalizedTypeIDName;
import edu.oregonstate.eecs.iis.avatolcv.util.ClassicSplitter;

public class HoldoutInfoFile {
    private static final String NL= System.getProperty("line.separator");

    public static final String FILE_PREFIX = "holdout_";
    private static final String FILESEP= System.getProperty("file.separator");

    private String scoringConcernType;
    private String scoringConcernID;
    private String scoringConcernName;
    private Hashtable<String,String> scoringConcernValueHash = new Hashtable<String, String>();
    private List<String> imagePaths = new ArrayList<String>();
    private List<String> holdoutLines = new ArrayList<String>();
    
    public HoldoutInfoFile(String scoringConcernType, String scoringConcernID, String scoringConcernName){
        this.scoringConcernType = scoringConcernType;
        this.scoringConcernID   = scoringConcernID;
        this.scoringConcernName = scoringConcernName;
    }
    public String getScoringConcernValueForImagePath(String imagePath){
        return this.scoringConcernValueHash.get(imagePath);
    }
    public String getFilename(){
        
        String typeString = scoringConcernType;
        if (typeString.equals(NormalizedTypeIDName.TYPE_UNSPECIFIED)){
            typeString = "";
        }
        String idString = scoringConcernID;
        if (idString.equals(NormalizedTypeIDName.ID_UNSPECIFIED)){
            idString = "";
        }
        return FILE_PREFIX + typeString + "_" + idString + "_" + scoringConcernName + ".txt";
    }
    public HoldoutInfoFile(String pathname) throws AvatolCVException {
        File f = new File(pathname);
        String filename = f.getName();
        String[] parts = ClassicSplitter.splitt(filename,'.');
        String root = parts[0];
        String[] rootParts = ClassicSplitter.splitt(root,'_');
        this.scoringConcernType = rootParts[1];
        this.scoringConcernID   = rootParts[2];
        this.scoringConcernName = rootParts[3];
        try {
            BufferedReader reader = new BufferedReader(new FileReader(pathname));
            String line = null;
            while (null != (line = reader.readLine())){
                if (line.startsWith("#")){
                    // ignore
                }
                else {
                    holdoutLines.add(line);
                    extractInfo(line);
                }
            }
            reader.close();
        }
        catch(IOException e){
            throw new AvatolCVException("could not load trainingInfoFile " + pathname);
        }
    }
    public void extractInfo(String line) throws AvatolCVException {
        String[] parts = ClassicSplitter.splitt(line,',');
        String filepath = parts[0];
        String scoringConcernValue = parts[1];
        this.imagePaths.add(filepath);
        this.scoringConcernValueHash.put(filepath, scoringConcernValue);
    }
    public void addInfo(String imagePath, String scoringConcernValue){
        String holdoutLine = imagePath+","+scoringConcernValue+ NL;
        holdoutLines.add(holdoutLine);
    }
    public void persist(String parentDir) throws AvatolCVException {
        String path = parentDir + FILESEP + getFilename();
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(path));
            for (String holdoutLine : holdoutLines){
                writer.write(holdoutLine);
            }
            writer.close();
        }
        catch(IOException ioe){
            throw new AvatolCVException("cannot write training info file for " + scoringConcernName);
        }
    }
}
