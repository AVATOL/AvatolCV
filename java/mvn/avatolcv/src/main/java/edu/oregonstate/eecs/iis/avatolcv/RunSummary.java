package edu.oregonstate.eecs.iis.avatolcv;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import edu.oregonstate.eecs.iis.avatolcv.core.AvatolCVException;

public class RunSummary {
    private static final String KEY_SCORING_CONCERN = "scoring concern";
    private static final String KEY_DATASET = "dataset";
    private static final String KEY_DATA_SOURCE = "data source";
    private static final String KEY_SCORING_ALGORITHM = "scoring algorithm";
    private static final String KEY_RUNID = "runID";
    
    
    private static final String FILESEP = System.getProperty("file.separator");
    private String scoringConcern = null;
    private String dataset = null;
    private String dataSource = null;
    private String scoringAlgorithm = null;
    private String runID = null;
    public RunSummary(String ID) throws AvatolCVException {
        
        
        String dir = AvatolCVFileSystem.getSessionSummariesDir();
        String path = dir + FILESEP + ID + ".txt";
        File f = new File(path);
        if (!f.exists()){
            throw new AvatolCVException("No session file exists for given id " + ID);
        }
        try {
            BufferedReader reader = new BufferedReader(new FileReader(path));
            String line = null;
            while (null != (line = reader.readLine())){
                if (line.startsWith("#")){
                    // ignore
                }
                else {
                    String[] parts = line.split("=");
                    String key = parts[0];
                    String value = parts[1];
                    if (key.equals(KEY_SCORING_CONCERN)){
                        this.scoringConcern = value;
                    }
                    else if (key.equals(KEY_DATASET)){
                        this.dataset = value;
                    }
                    else if (key.equals(KEY_DATA_SOURCE)){
                        this.dataSource = value;
                    }
                    else if (key.equals(KEY_SCORING_ALGORITHM)){
                        this.scoringAlgorithm = value;
                    }
                    else if (key.equals(KEY_RUNID)){
                        this.runID = value;
                    }
                }
            }
            reader.close();
        }
        catch(IOException ioe){
            throw new AvatolCVException("problem reading session file " + path);
        }
    }
    public String getRunID(){
        return this.runID;
    }
    public String getScoringConcern(){
        return this.scoringConcern;
    }
    public String getDataset(){
        return this.dataset;
    }
    public String getScoringAlgorithm(){
        return this.scoringAlgorithm;
    }
    public String getDataSource(){
        return this.dataSource;
    }
}
