package edu.oregonstate.eecs.iis.avatolcv.datasource;

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
import edu.oregonstate.eecs.iis.avatolcv.normalized.NormalizedKey;
import edu.oregonstate.eecs.iis.avatolcv.normalized.NormalizedValue;
import edu.oregonstate.eecs.iis.avatolcv.util.ClassicSplitter;

public class UploadSession {
    public static final String STRING_LIST_DELIM = ";";
    private static final String NL = System.getProperty("line.separator");
    public static final String TYPE_NEW = "NEW";
    public static final String TYPE_REVISE = "REVISE";
    public static final String TYPE_ABSTAIN_TIE = "ABSTAIN_TIE";
    public static final String TYPE_ABSTAIN_VALUE_SAME = "ABSTAIN_VALUE_SAME";
    private List<UploadEvent> events = new ArrayList<UploadEvent>();
    private int uploadSessionNumber = 0;
    private String runName = null;
    public UploadSession(String runName) throws AvatolCVException {
        this.runName = runName;
        String path = AvatolCVFileSystem.getPathForUploadSessionFile(runName);
        File f = new File(path);
        
        if (f.exists()){
            // load it
            try {
                BufferedReader reader = new BufferedReader(new FileReader(path));
                String line = null;
                while (null != (line = reader.readLine())){
                    String[] parts = ClassicSplitter.splitt(line, ',');
                    String sessionNumber = parts[0];
                    String type = parts[1];
                    String imageID = parts[2];
                    String key = parts[3];
                    String val = parts[4];
                    String origVal = parts[5];
                    String trainTestConcern = parts[6];
                    String trainTestConcernValue = parts[7];
                    Integer sessionNumInteger = new Integer(sessionNumber);
                    uploadSessionNumber = sessionNumInteger.intValue();
                    if (type.equals(TYPE_NEW)){
                        addNewKeyValue(uploadSessionNumber, imageID, new NormalizedKey(key), new NormalizedValue(val), new NormalizedKey(trainTestConcern), new NormalizedValue(trainTestConcernValue));
                    }
                    else {
                        reviseValueForKey(uploadSessionNumber, imageID, new NormalizedKey(key), new NormalizedValue(val), new NormalizedValue(origVal), new NormalizedKey(trainTestConcern), new NormalizedValue(trainTestConcernValue));
                    }
                }
                reader.close();
            }
            catch(NumberFormatException nfe){
                throw new AvatolCVException("bad integer found when loading session number for upload log file " + path, nfe);
            }
            catch(IOException ioe){
                throw new AvatolCVException("problem  loading upload log for session", ioe);
            }
        }
    }
    //public boolean isImageUploaded(String imageID){
    //    for (UploadEvent event : this.events){
    //        if (event.getImageID().equals(imageID)){
    //            return true;
    //        }
    //    }
    //    return false;
    //}
    public void nextSession(){
        uploadSessionNumber += 1;
    }
    public int getUploadSessionNumber(){
        return uploadSessionNumber;
    }
    
    public static String getImageIDListString(List<String> ids){
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < ids.size() - 1; i++){
            String id = ids.get(i);
            sb.append(id + STRING_LIST_DELIM);
        }
        String finalID = ids.get(ids.size() - 1);
        sb.append(finalID);
        return "" + sb;
    }
    
    public void addNewKeyValue(int sessionNumber, String imageID, NormalizedKey key, NormalizedValue val, NormalizedKey trainTestConcern, NormalizedValue trainTestConcernValue){
        events.add(new UploadEvent(sessionNumber, TYPE_NEW, imageID, key, val, true, null, trainTestConcern, trainTestConcernValue));
    }
    public void addNewKeyValue(String imageID, NormalizedKey key, NormalizedValue val, NormalizedKey trainTestConcern, NormalizedValue trainTestConcernValue){
        addNewKeyValue(uploadSessionNumber, imageID, key, val, trainTestConcern, trainTestConcernValue);
    }
    public void addNewKeyValue(List<String> imageIDs, NormalizedKey key, NormalizedValue val, NormalizedKey trainTestConcern, NormalizedValue trainTestConcernValue){
        String imageIDString = getImageIDListString(imageIDs);
        addNewKeyValue(uploadSessionNumber, imageIDString, key, val, trainTestConcern, trainTestConcernValue);
    }
    
    
    public void reviseValueForKey(int sessionNumber, String imageID, NormalizedKey key, NormalizedValue val, NormalizedValue origValue, NormalizedKey trainTestConcern, NormalizedValue trainTestConcernValue){
        events.add(new UploadEvent(sessionNumber, TYPE_REVISE, imageID, key, val, false, origValue, trainTestConcern, trainTestConcernValue));
    }
    public void reviseValueForKey(String imageID, NormalizedKey key, NormalizedValue val, NormalizedValue origValue, NormalizedKey trainTestConcern, NormalizedValue trainTestConcernValue){
        reviseValueForKey(uploadSessionNumber,  imageID,  key,  val,  origValue, trainTestConcern, trainTestConcernValue);
    }
    public void reviseValueForKey(List<String> imageIDs, NormalizedKey key, NormalizedValue val, NormalizedValue origValue, NormalizedKey trainTestConcern, NormalizedValue trainTestConcernValue){
        String imageIDString = getImageIDListString(imageIDs);
        reviseValueForKey(uploadSessionNumber,  imageIDString,  key,  val,  origValue, trainTestConcern, trainTestConcernValue);
    }
    
    
    public void abstainSinceValueSame(int sessionNumber, String imageID, NormalizedKey key, NormalizedValue val, NormalizedValue origValue, NormalizedKey trainTestConcern, NormalizedValue trainTestConcernValue){
        events.add(new UploadEvent(sessionNumber, TYPE_ABSTAIN_VALUE_SAME, imageID, key, val, false, origValue, trainTestConcern, trainTestConcernValue));
    }
    public void abstainSinceValueSame(String imageID, NormalizedKey key, NormalizedValue val, NormalizedValue origValue, NormalizedKey trainTestConcern, NormalizedValue trainTestConcernValue){
        reviseValueForKey(uploadSessionNumber,  imageID,  key,  val,  origValue, trainTestConcern, trainTestConcernValue);
    }
    public void abstainSinceValueSame(List<String> imageIDs, NormalizedKey key, NormalizedValue val, NormalizedValue origValue, NormalizedKey trainTestConcern, NormalizedValue trainTestConcernValue){
        String imageIDString = getImageIDListString(imageIDs);
        reviseValueForKey(uploadSessionNumber,  imageIDString,  key,  val,  origValue, trainTestConcern, trainTestConcernValue);
    }
    
    
    public void abstainSinceTieVote(int sessionNumber, String imageID, NormalizedKey key, NormalizedValue val, NormalizedValue origValue, NormalizedKey trainTestConcern, NormalizedValue trainTestConcernValue){
        events.add(new UploadEvent(sessionNumber, TYPE_ABSTAIN_TIE, imageID, key, val, false, origValue, trainTestConcern, trainTestConcernValue));
    }
    public void abstainSinceTieVote(String imageID, NormalizedKey key, NormalizedValue val, NormalizedValue origValue, NormalizedKey trainTestConcern, NormalizedValue trainTestConcernValue){
        reviseValueForKey(uploadSessionNumber,  imageID,  key,  val,  origValue, trainTestConcern, trainTestConcernValue);
    }
    public void abstainSinceTieVote(List<String> imageIDs, NormalizedKey key, NormalizedValue val, NormalizedValue origValue, NormalizedKey trainTestConcern, NormalizedValue trainTestConcernValue){
        String imageIDString = getImageIDListString(imageIDs);
        reviseValueForKey(uploadSessionNumber,  imageIDString,  key,  val,  origValue, trainTestConcern, trainTestConcernValue);
    }
    
    public void persist() throws AvatolCVException {
        String path = AvatolCVFileSystem.getPathForUploadSessionFile(this.runName);
        if (events.size() == 0){
            File f = new File(path);
            if (f.exists()){
                f.delete();
            }
        }
        else {
            try {
                BufferedWriter writer = new BufferedWriter(new FileWriter(path));
                for (UploadEvent event : events){
                    writer.write(event.getUploadSessionNumber() + "," + event.getType() + "," + event.getImageID() + "," + event.getKey() + "," + event.getVal() + "," + event.getOrigValue()  + "," + event.getTrainTestConcern()  + "," + event.getTrainTestConcernValue() + NL);
                }
                writer.close();
            }
            catch(IOException ioe){
                throw new AvatolCVException("problem persisting upload session info.", ioe);
            }
        }
        
    }
    public class UploadEvent {
        private String imageID = null;
        private NormalizedKey key = null;
        private NormalizedValue val = null;
        private NormalizedValue oldValue = null;
        int uploadSessionNumber = -1;
        private NormalizedKey trainTestConcern = null;
        private NormalizedValue trainTestConcernValue = null;
        private String type = null;
        public UploadEvent(int uploadSessionNumber, String type, String imageID, NormalizedKey key, NormalizedValue val, boolean newKey, NormalizedValue oldValue, NormalizedKey trainTestConcern, NormalizedValue trainTestConcernValue){
            this.uploadSessionNumber = uploadSessionNumber;
            this.type = type;
            this.imageID = imageID;
            this.key = key;
            this.val = val;
            this.oldValue = oldValue;
            this.trainTestConcern = trainTestConcern;
            this.trainTestConcernValue = trainTestConcernValue;
        }
        public String getImageID(){
            return this.imageID;
        }
        public NormalizedKey getKey(){
            return this.key;
        }
        public NormalizedValue getVal(){
            return this.val;
        }
        public NormalizedKey getTrainTestConcern(){
            return this.trainTestConcern;
        }
        public NormalizedValue getTrainTestConcernValue(){
            return this.trainTestConcernValue;
        }
        
        public String getType(){
            return this.type;
        }
        public NormalizedValue getOrigValue() throws AvatolCVException {
            if (null == this.oldValue){
                return new NormalizedValue("");
            }
            return this.oldValue;
        }
        public int getUploadSessionNumber(){
            return this.uploadSessionNumber;
        }
    }
    
    public List<UploadEvent> getEventsForUndo(){
        int relevantUploadNum = uploadSessionNumber;
        List<UploadEvent> relevantEvents = new ArrayList<UploadEvent>();
        for (UploadEvent event : this.events){
            if (event.getUploadSessionNumber() == relevantUploadNum){
                relevantEvents.add(event);
            }
        }
        
        return relevantEvents;
    }
    public void forgetEvents(List<UploadEvent> events) throws AvatolCVException {
        for (UploadEvent event : events){
            this.events.remove(event);
        }
        uploadSessionNumber -= 1;
        persist();
    }
}
