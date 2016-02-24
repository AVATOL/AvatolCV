package edu.oregonstate.eecs.iis.avatolcv.datasource;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import edu.oregonstate.eecs.iis.avatolcv.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.AvatolCVFileSystem;
import edu.oregonstate.eecs.iis.avatolcv.normalized.NormalizedKey;
import edu.oregonstate.eecs.iis.avatolcv.normalized.NormalizedValue;

public class UploadSession {
    private static final String NL = System.getProperty("file.separator");
    private List<UploadEvent> events = new ArrayList<UploadEvent>();
    private int nextUploadSessionNumber = 1;
    private int prevUploadSessionNumber = -1;
    public void addNewKeyValue(String imageID, NormalizedKey key, NormalizedValue val){
        events.add(new UploadEvent(nextUploadSessionNumber, imageID, key, val, true, null));
    }
    public void reviseValueForKey(String imageID, NormalizedKey key, NormalizedValue val, NormalizedValue origValue){
        events.add(new UploadEvent(nextUploadSessionNumber, imageID, key, val, false, origValue));
    }
    
    public void persist() throws AvatolCVException {
        String path = AvatolCVFileSystem.getPathForUploadSessionFile();
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(path));
            for (UploadEvent event : events){
                writer.write(event.getImageID() + "," + event.getKey() + "," + event.getVal() + "," + event.getOrigValue() + NL);
            }
            writer.close();
        }
        catch(IOException ioe){
            throw new AvatolCVException("problem persisting upload session info.", ioe);
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
        public NormalizedValue getOrigValue(){
            return this.oldValue;
        }
        public int getUploadSessionNumber(){
            return this.uploadSessionNumber;
        }
    }
    
    public List<UploadEvent> getEventsForUndo(){
        int relevantUploadNum = prevUploadSessionNumber;
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
        persist();
    }
}
