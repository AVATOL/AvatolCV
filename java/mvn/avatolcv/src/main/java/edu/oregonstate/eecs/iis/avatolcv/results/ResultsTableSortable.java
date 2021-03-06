package edu.oregonstate.eecs.iis.avatolcv.results;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;

import edu.oregonstate.eecs.iis.avatolcv.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.normalized.NormalizedValue;

public class ResultsTableSortable {
    private Hashtable<String, String> valueForColumnNameHash = new Hashtable<String, String>();
    private Hashtable<String, Object> widgetforIdColumnNameHash = new Hashtable<String, Object>();
    
    private Hashtable<String, List<ImageIDColumnValue>> valuesListForColumnNameHash = new Hashtable<String, List<ImageIDColumnValue>>();
    private List<String> currentImageIDsInOrder = new ArrayList<String>();
    private List<String> colNames = new ArrayList<String>();
    private Hashtable<String, Object> largeImagesForIDHash = new Hashtable<String, Object>();
    private String previousSortColumn = "";
    private int repeatSortClickCount = 0;
    public class ImageIDColumnValue implements Comparable<Object> {
        private String imageID = null;
        private String value = null;
        public ImageIDColumnValue(String imageID, String value) throws AvatolCVException {
            this.imageID = imageID;
            this.value = new NormalizedValue(value).getName();
        }
        public String getImageID(){
            return this.imageID;
        }
        public String getValue(){
            return this.value;
        }
        @Override
        public int compareTo(Object arg0) {
            ImageIDColumnValue other = (ImageIDColumnValue)arg0;
            return this.value.compareTo(other.getValue());
        }
    }
    
    public boolean isLargeImageShown(String imageID){
        Object obj = largeImagesForIDHash.get(imageID);
        if (null == obj){
            return false;
        }
        return true;
    }
    public Object forgetLargeImageObject(String imageID){
        Object obj = largeImagesForIDHash.get(imageID);
        largeImagesForIDHash.remove(imageID);
        return obj;
    }
    public void addLargeImage(String imageID, Object obj){
        largeImagesForIDHash.put(imageID, obj);
    }
    public int getTargetRowForLargeImage(String imageID){
        int indexOfThumbnail = currentImageIDsInOrder.indexOf(imageID);
        return (indexOfThumbnail * 2) + 2;
    }
    
    
    public String getValue(String imageID, String colName){
        String key = getKey(imageID, colName);
        return valueForColumnNameHash.get(key);
    }
    public void sortOnColumn(String colName) throws AvatolCVException {
        if (!colNames.contains(colName)){
            return;
        }
        List<ImageIDColumnValue> colVals = valuesListForColumnNameHash.get(colName);
        Collections.sort(colVals);
        if (colName.equals(this.previousSortColumn)){
            repeatSortClickCount++;
            if (repeatSortClickCount %2 == 1){
                Collections.reverse(colVals);
            }
            valuesListForColumnNameHash.put(colName, colVals);
        }
        else {
            repeatSortClickCount=0;
        }
        this.previousSortColumn = colName;
       
       

        //System.out.println("sort result");
        currentImageIDsInOrder = new ArrayList<String>();
        for (ImageIDColumnValue v : colVals){
            //System.out.println(v.getValue() + " " + v.getImageID());
            currentImageIDsInOrder.add(v.getImageID());
        }
    }
    public List<String> getImageIDsInCurrentOrder(){
        List<String> result = new ArrayList<String>();
        result.addAll(currentImageIDsInOrder);
        return result;
    }
    public String getKey(String imageID, String colName){
        return imageID + "_" + colName;
    }
    public void addValueForColumn(String imageID, String colName, String value) throws AvatolCVException {
        if (!colNames.contains(colName)){
            colNames.add(colName);
        }
        ImageIDColumnValue v = new ImageIDColumnValue(imageID, value);
        List<ImageIDColumnValue> colVals = valuesListForColumnNameHash.get(colName);
        if (null == colVals){
            colVals = new ArrayList<ImageIDColumnValue>();
            valuesListForColumnNameHash.put(colName, colVals);
        }
        colVals.add(v);
        String key = getKey(imageID, colName);
        valueForColumnNameHash.put(key, value);
    }
    public void addWidgetForColumn(String imageID, String colName, Object widget){
        String key = getKey(imageID, colName);
        widgetforIdColumnNameHash.put(key, widget);
    }
    public Object getWidget(String imageID, String colName){
        String key = getKey(imageID, colName);
        return widgetforIdColumnNameHash.get(key);
    }
}
