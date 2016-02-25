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
    private static final String NL = System.getProperty("line.separator");
    private List<UploadEvent> events = new ArrayList<UploadEvent>();
    private int uploadSessionNumber = 0;
    
    public UploadSession() throws AvatolCVException {
        String path = AvatolCVFileSystem.getPathForUploadSessionFile();
        File f = new File(path);
        
        if (f.exists()){
            // load it
            try {
                BufferedReader reader = new BufferedReader(new FileReader(path));
                String line = null;
                while (null != (line = reader.readLine())){
                    String[] parts = ClassicSplitter.splitt(line, ',');
                    String sessionNumber = parts[0];
                    String imageID = parts[1];
                    String key = parts[2];
                    String val = parts[3];
                    String origVal = parts[4];
                    Integer sessionNumInteger = new Integer(sessionNumber);
                    uploadSessionNumber = sessionNumInteger.intValue();
                    if ("null".equals(origVal)){
                        addNewKeyValue(uploadSessionNumber, imageID, new NormalizedKey(key), new NormalizedValue(val));
                    }
                    else {
                        reviseValueForKey(uploadSessionNumber, imageID, new NormalizedKey(key), new NormalizedValue(val), new NormalizedValue(origVal));
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
    public boolean isImageUploaded(String imageID){
        for (UploadEvent event : this.events){
            if (event.getImageID().equals(imageID)){
                return true;
            }
        }
        return false;
    }
    public void nextSession(){
        uploadSessionNumber += 1;
    }
    public int getUploadSessionNumber(){
        return uploadSessionNumber;
    }
    public void addNewKeyValue(int sessionNumber, String imageID, NormalizedKey key, NormalizedValue val){
        events.add(new UploadEvent(sessionNumber, imageID, key, val, true, null));
    }
    public void addNewKeyValue(String imageID, NormalizedKey key, NormalizedValue val){
        addNewKeyValue(uploadSessionNumber, imageID, key, val);
    }

    public void reviseValueForKey(int sessionNumber, String imageID, NormalizedKey key, NormalizedValue val, NormalizedValue origValue){
        events.add(new UploadEvent(sessionNumber, imageID, key, val, false, origValue));
    }
    public void reviseValueForKey(String imageID, NormalizedKey key, NormalizedValue val, NormalizedValue origValue){
        reviseValueForKey(uploadSessionNumber,  imageID,  key,  val,  origValue);
    }
    
    public void persist() throws AvatolCVException {
        String path = AvatolCVFileSystem.getPathForUploadSessionFile();
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
                    writer.write(event.getUploadSessionNumber() + "," + event.getImageID() + "," + event.getKey() + "," + event.getVal() + "," + event.getOrigValue() + NL);
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
        private boolean newKey = false;
        private NormalizedValue oldValue = null;
        int uploadSessionNumber = -1;
        public UploadEvent(int uploadSessionNumber, String imageID, NormalizedKey key, NormalizedValue val, boolean newKey, NormalizedValue oldValue){
            this.uploadSessionNumber = uploadSessionNumber;
            this.imageID = imageID;
            this.key = key;
            this.val = val;
            this.newKey = newKey;
            this.oldValue = oldValue;
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
        public boolean wasNewKey(){
            return this.newKey;
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
