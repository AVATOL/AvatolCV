package edu.oregonstate.eecs.iis.avatolcv.results;

import java.util.ArrayList;
import java.util.List;

import edu.oregonstate.eecs.iis.avatolcv.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.core.ImageInfo;
import edu.oregonstate.eecs.iis.avatolcv.normalized.NormalizedValue;

public class ResultsTable {
    public static final String COLNAME_IMAGE = "image";
    public static final String COLNAME_TRUTH = "truth";
    public static final String COLNAME_SCORE = "score";
    public static final String COLNAME_CONFIDENCE = "confidence";
    public static final String COLNAME_NAME = "name";
    public static final String COLNAME_TRAIN_TEST = "";
    
    private static List<String> colNames;
    private List<SortableRow> rows = new ArrayList<SortableRow>();
    static {
        setColumnNames();
    }
    public ResultsTable(){
        
    }
    public static void setColumnNames(){
        colNames = new ArrayList<String>();
        colNames.add(COLNAME_IMAGE);
        colNames.add(COLNAME_TRUTH);
        colNames.add(COLNAME_SCORE);
        colNames.add(COLNAME_CONFIDENCE);
        colNames.add(COLNAME_NAME);
        
        
    }
    public static int getIndexOfColumn(String colName){
        int answer = colNames.indexOf(colName);
    	//System.out.println("colName: " + colName + " at index " + answer);
        return answer;
    }
    public SortableRow createRow(String thumbnailPathname, String origImageName, String score, String conf, String truth, int index, String trainTestConcernValue) throws AvatolCVException {
    	System.out.println("creating row for " + score + " , " + conf + " , " + truth + " , " + origImageName );
        List<String> values = new ArrayList<String>();
        values.add(thumbnailPathname);
        values.add(new NormalizedValue(truth).getName());
        values.add(new NormalizedValue(score).getName());
        values.add(conf);
        if (null != trainTestConcernValue){
        	values.add(trainTestConcernValue);
        }
        values.add(origImageName);
        String imageID = ImageInfo.getImageIDFromPath(thumbnailPathname);
        SortableRow row = new SortableRow(imageID, values, index);
        rows.add(row);
        return row;
    }
    public List<SortableRow> getRows(){
        return this.rows;
    }
    
}
