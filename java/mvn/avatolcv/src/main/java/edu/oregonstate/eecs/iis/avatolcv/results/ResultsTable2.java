package edu.oregonstate.eecs.iis.avatolcv.results;

import java.util.Hashtable;
import java.util.List;

public class ResultsTable2 {
    private Hashtable<String, ImageIDColumnValue> imageIDColumnValueHashforIdColumnNameHash = new Hashtable<String, ImageIDColumnValue>();
    private Hashtable<String, Object>             widgetforIdColumnNameHash = new Hashtable<String, Object>();
    
    private Hashtable<String, List<ImageIDColumnValue>> valuesListForColumnNameHash = new Hashtable<String, List<ImageIDColumnValue>>();
    
    public class ImageIDColumnValue{
        private String imageID = null;
        private String value = null;
        public ImageIDColumnValue(String imageID, String value){
            this.imageID = imageID;
            this.value = value;
        }
    }
}
