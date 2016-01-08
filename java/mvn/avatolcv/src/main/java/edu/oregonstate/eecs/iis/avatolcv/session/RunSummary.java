package edu.oregonstate.eecs.iis.avatolcv.session;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import edu.oregonstate.eecs.iis.avatolcv.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.AvatolCVFileSystem;
import edu.oregonstate.eecs.iis.avatolcv.normalized.NormalizedValue;

/**
 * 
 * @author admin-jed
 * 
 * File that holds all the information about a prior run.  As of 9/18/2015, not sure if I will generate this file incrementally 
 * so I can use it during an active session or not.  So far just supporting results review screen.  I think I have to support 
 * incremental generation in order to support session resume.
 *
 */
public class RunSummary {
    private static final String KEY_COOKING_SHOW = "prebaked";
    private static final String KEY_SCORING_CONCERN = "scoring concern";
    private static final String KEY_TRAIN_TEST_CONCERN = "train test concern";
    private static final String KEY_DATASET = "dataset";
    private static final String KEY_DATA_SOURCE = "data source";
    private static final String KEY_SCORING_ALGORITHM = "scoring algorithm";
    private static final String KEY_RUNID = "runID";
    private static final String KEY_SCORING_CONCERN_VALUE = "scoring concern value";
    private static final String NL = System.getProperty("line.separator");
    
    private static final String FILESEP = System.getProperty("file.separator");
    private String scoringConcern = null;
    
    private String dataset = null;
    private String dataSource = null;
    private String scoringAlgorithm = null;
    private String runID = null;
    private String trainTestConcern = null;
    private List<String> scoringConcernValues = new ArrayList<String>();
    private boolean cookingShow = false;
    private String scoringMode = null;
    public RunSummary(String ID) throws AvatolCVException {
        this.runID = runID;
    }
    /*
     * remove the scoring concern from the filename to yield the true runID
     */
    public static String getRunIDFromRunSummaryName(String name){
    	String[] parts = name.split("_");
    	String result = parts[0] + "_" + parts[1];
    	return result;
    }
    public String getSessionName(){
    	return this.runID + "_" + getScoringConcern();
    }
    public void persist() throws AvatolCVException {
        String dir = AvatolCVFileSystem.getSessionSummariesDir();
        String sessionName = getSessionName();
        String path = dir + FILESEP + getSessionName() + ".txt";
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(path));
            writer.write(KEY_SCORING_CONCERN + "=" + getScoringConcern() + NL);
            writer.write(KEY_DATASET + "=" + getDataset() + NL);
            writer.write(KEY_DATA_SOURCE + "=" + getDataSource() + NL);
            writer.write(KEY_SCORING_ALGORITHM + "=" + getScoringAlgorithm() + NL);
            writer.write(KEY_RUNID + "=" + getRunID() + NL);
            if (null != getTrainTestConcern()){
                writer.write(KEY_TRAIN_TEST_CONCERN + "=" + getTrainTestConcern() + NL);
            }
            
            //if (isCookingShow()){
            //    writer.write(KEY_COOKING_SHOW + "=" + isCookingShow() + NL);
            //}
            for (String scVal : this.scoringConcernValues){
            	NormalizedValue nv = new NormalizedValue(scVal);
            	if (nv.isNameSpecified()){
            		writer.write(KEY_SCORING_CONCERN_VALUE + "=" + scVal + NL);
            	}
            }
            writer.close();
        }
        catch(IOException ioe){
            throw new AvatolCVException("problem persisting session summary " + ioe.getMessage());
        }
    }
    public static RunSummary loadSummary(String ID) throws AvatolCVException {
        RunSummary rs = new RunSummary(ID);
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
                if (line.startsWith("#") || line.trim().equals("")){
                    // ignore
                }
                else {
                    String[] parts = line.split("=");
                    String key = parts[0];
                    String value = parts[1];
                    if (key.equals(KEY_SCORING_CONCERN)){
                        rs.setScoringConcern(new NormalizedValue(value).getName());
                    }
                    else if (key.equals(KEY_DATASET)){
                        rs.setDataset(value);
                    }
                    else if (key.equals(KEY_DATA_SOURCE)){
                        rs.setDataSource(value);
                    }
                    else if (key.equals(KEY_SCORING_ALGORITHM)){
                        rs.setScoringAlgorithm(value);
                    }
                    else if (key.equals(KEY_RUNID)){
                        rs.setRunID(value);
                    }
                    else if (key.equals(KEY_TRAIN_TEST_CONCERN)){
                        rs.setTrainTestConcern(value);
                    }
                    else if (key.equals(KEY_COOKING_SHOW)){
                        rs.setCookingShow(true);
                    }
                    else if (key.equals(KEY_SCORING_CONCERN_VALUE)){
                    	NormalizedValue nv = new NormalizedValue(value);
                    	if (nv.isNameSpecified()){
                    		rs.addScoringConcernValue(new NormalizedValue(value).getName());
                    	}
                    }
                }
            }
            reader.close();
            return rs;
        }
        catch(IOException ioe){
            throw new AvatolCVException("problem reading session file " + path);
        }
    }
    public void setCookingShow(boolean cookingShow) {
        this.cookingShow = cookingShow;
    }

    public void setScoringMode(String scoringMode) {
        this.scoringMode = scoringMode;
    }
    public boolean isCookingShow(){
    	return this.cookingShow;
    }
    public String getRunID(){
        return this.runID;
    }
    public void setRunID(String id){
        this.runID = id;
    }
    
    public void setScoringConcern(String scoringConcern) {
        this.scoringConcern = scoringConcern;
    }
    public void setDataset(String dataset) {
        this.dataset = dataset;
    }
    public void setDataSource(String dataSource) {
        this.dataSource = dataSource;
    }
    public void setScoringAlgorithm(String scoringAlgorithm) {
        this.scoringAlgorithm = scoringAlgorithm;
    }
    public void setTrainTestConcern(String trainTestConcern) {
        this.trainTestConcern = trainTestConcern;
    }
    public void addScoringConcernValue(String scoringConcernValue) {
        this.scoringConcernValues.add(scoringConcernValue);
    }
    
    public String getScoringMode(){
    	return this.scoringMode;
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
    public boolean hasTrainTestConcern(){
    	if (this.trainTestConcern == null){
    		return false;
    	}
    	return true;
    }
    public String getTrainTestConcern(){
    	return this.trainTestConcern;
    }
    public List<String> getScoringConcernValues(){
        return this.scoringConcernValues;
    }
}
