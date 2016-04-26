package edu.oregonstate.eecs.iis.avatolcv.results;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.application.Platform;
import edu.oregonstate.eecs.iis.avatolcv.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.datasource.DataSource;
import edu.oregonstate.eecs.iis.avatolcv.datasource.MorphobankDataSource;
import edu.oregonstate.eecs.iis.avatolcv.datasource.UploadSession;
import edu.oregonstate.eecs.iis.avatolcv.normalized.NormalizedKey;
import edu.oregonstate.eecs.iis.avatolcv.normalized.NormalizedValue;
import edu.oregonstate.eecs.iis.avatolcv.session.ProgressPresenter;

public class VotingUploader {
    private DataSource dataSource = null;
    private ProgressPresenter pp = null;
    private UploadSession uploadSession = null;
    private NormalizedKey trainTestConcern = null;
    private Hashtable<NormalizedValue, List<ScoreItem>> scoreItemHash = new Hashtable<NormalizedValue, List<ScoreItem>>();
    private Hashtable<NormalizedValue, NormalizedValue> voteWinnerHash = new Hashtable<NormalizedValue, NormalizedValue>();
    private static final Logger logger = LogManager.getLogger(VotingUploader.class);

    private List<NormalizedValue> ttValuesSeen = new ArrayList<NormalizedValue>();
    public VotingUploader(DataSource dataSource,ProgressPresenter pp, UploadSession uploadSession) throws AvatolCVException {
        this.dataSource = dataSource;
        this.pp = pp;
        this.uploadSession = uploadSession;
    }
    public void addScore(String imageID, NormalizedKey trainTestConcern, NormalizedValue trainTestConcernValue, NormalizedValue newValue, NormalizedValue existingValueForKey) throws AvatolCVException {
        validateTrainTestConcernConsistent(trainTestConcern);
        ScoreItem si = new ScoreItem(imageID, trainTestConcern, trainTestConcernValue, newValue, existingValueForKey);
        List<ScoreItem> itemsForTTValue = scoreItemHash.get(trainTestConcernValue);
        if (null == itemsForTTValue){
            itemsForTTValue = new ArrayList<ScoreItem>();
            scoreItemHash.put(trainTestConcernValue, itemsForTTValue);
        }
        itemsForTTValue.add(si);
        if (! ttValuesSeen.contains(trainTestConcernValue)){
            ttValuesSeen.add(trainTestConcernValue);
        }
    }
    public class ScoreItem{
        private String imageID = null;
        private NormalizedKey trainTestConcern = null;
        private NormalizedValue trainTestConcernValue = null;
        private NormalizedValue newValue = null;
        private NormalizedValue existingValueForKey = null;
       
        public ScoreItem(String imageID, NormalizedKey trainTestConcern, NormalizedValue trainTestConcernValue, NormalizedValue newValue, NormalizedValue existingValueForKey){
            this.imageID = imageID;
            this.trainTestConcern = trainTestConcern;
            this.trainTestConcernValue = trainTestConcernValue;
            this.newValue = newValue;
            this.existingValueForKey = existingValueForKey;
        }
        public String getImageID() {
            return imageID;
        }
        public NormalizedKey getTrainTestConcern() {
            return trainTestConcern;
        }
        public NormalizedValue getTrainTestConcernValue() {
            return trainTestConcernValue;
        }
        public NormalizedValue getNewValue() {
            return newValue;
        }
        public NormalizedValue getExistingValueForKey() {
            return existingValueForKey;
        }
    }
    private void validateTrainTestConcernConsistent(NormalizedKey ttConcern) throws AvatolCVException {
        if (null == this.trainTestConcern){
            this.trainTestConcern = ttConcern;
        }
        else {
            if (!(ttConcern.equals(this.trainTestConcern))){
                throw new AvatolCVException("mixed trainTestConcerns passed to VotingUploader " + this.trainTestConcern + "  " + trainTestConcern);
            }
        }
    }
    public void vote() throws AvatolCVException {
        for (NormalizedValue ttConcernVal : ttValuesSeen){
            List<ScoreItem> itemsForTTValue = scoreItemHash.get(ttConcernVal);
            validateConsistentExistingValues(itemsForTTValue, ttConcernVal);
            NormalizedValue voteWinner = getVoteWinnerForScoreItems(itemsForTTValue, ttConcernVal);
            voteWinnerHash.put(ttConcernVal, voteWinner);
        }
    }
    public NormalizedValue getVoteWinner(NormalizedValue ttConcernVal){
        return voteWinnerHash.get(ttConcernVal);
    }
    private NormalizedValue getVoteWinnerForScoreItems(List<ScoreItem> items, NormalizedValue ttConcernVal){
        Hashtable<NormalizedValue, List<ScoreItem>> hash = new Hashtable<NormalizedValue, List<ScoreItem>>();
        List<NormalizedValue> valuesSeen = new ArrayList<NormalizedValue>();
        // make a hash by newValue of the ScoreItems
        for (ScoreItem item : items){
            NormalizedValue curNewValue = item.getNewValue();
            if (!valuesSeen.contains(curNewValue)){
                valuesSeen.add(curNewValue);
            }
            List<ScoreItem> itemsForVal = hash.get(curNewValue);
            if (null == itemsForVal){
                itemsForVal = new ArrayList<ScoreItem>();
                hash.put(curNewValue, itemsForVal);
            }
            itemsForVal.add(item);
        }
        // get  list of ScoreItems for each value and wrap in a class to facilitate sorting by size
        List<ItemsWithVal> itemsWithValList = new ArrayList<ItemsWithVal>();
        for (NormalizedValue nv : valuesSeen){
            List<ScoreItem> itemsForVal = hash.get(nv);
            ItemsWithVal iwv = new ItemsWithVal(itemsForVal, nv);
            itemsWithValList.add(iwv);
        }
        Collections.sort(itemsWithValList);
        Collections.reverse(itemsWithValList);
        logger.info("finding voteWinner for " + ttConcernVal);
        // if only one value was seen, return that value
        if (valuesSeen.size() == 1){
            logger.info("one value seen - winner is " + valuesSeen.get(0) );
            return valuesSeen.get(0);
        }
        // if there are two or more values seen, if the first two are a tie, then that means there is an n-way tie, which means we should keep the prior answer
        int count0 = itemsWithValList.get(0).getCount();
        int count1 = itemsWithValList.get(1).getCount();
        if (count0 == count1){
            // return prior value
            NormalizedValue priorValue = itemsWithValList.get(0).getItems().get(0).getExistingValueForKey();
            logger.info("tie with first two values having  " + count0 + " ... using priorValue " + priorValue );
            return priorValue;
        }
        // not a tie, return the new value for the top count
        NormalizedValue newValue = itemsWithValList.get(0).getValue();
        logger.info("hands down winning value is  " + count0 + " ... using newValue " + newValue );
        return newValue;
        
    }
    public class ItemsWithVal implements Comparable<ItemsWithVal> {
        private List<ScoreItem> items = null;
        private NormalizedValue val = null;
        public ItemsWithVal(List<ScoreItem> items, NormalizedValue val){
            this.items = items;
            this.val = val;
            int count = items.size();
            System.out.println("ItemsWithVal " + val + " has count " + count);
        }
        public int getCount(){
            return items.size();
        }
        public List<ScoreItem> getItems(){
            return items;
        }
        public NormalizedValue getValue(){
            return this.val;
        }
        @Override
        public int compareTo(ItemsWithVal other) {
        	return Integer.compare(this.getCount(), other.getCount());
        }
    }
    private void validateConsistentExistingValues(List<ScoreItem> items, NormalizedValue ttConcernVal) throws AvatolCVException {
        if (items.size() == 0){
            return;
        }
        NormalizedValue priorVal1 = items.get(0).getExistingValueForKey();
        for (ScoreItem item : items){
            if (!priorVal1.equals(item.getExistingValueForKey())){
                throw new AvatolCVException("Assumption violated - expected all images with the same trainTestConcernValue to have consistent prior values at the dataSource and encountered " + priorVal1 + " and " + item.getExistingValueForKey());
            }
        }
    }
    public void upload(){
        pp.updateProgress("",0.0);
        //double percentProgressPerRow = 1 / rowToUploadCount;
        uploadSession.nextSession();
        
        
    }
}
