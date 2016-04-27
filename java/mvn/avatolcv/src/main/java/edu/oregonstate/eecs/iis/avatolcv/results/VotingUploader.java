package edu.oregonstate.eecs.iis.avatolcv.results;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.oregonstate.eecs.iis.avatolcv.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.datasource.DataSource;
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
    private Hashtable<NormalizedValue, ScoreItem> voteWinnerHash = new Hashtable<NormalizedValue, ScoreItem>();
    private static final Logger logger = LogManager.getLogger(VotingUploader.class);

    private List<NormalizedValue> ttValuesSeen = new ArrayList<NormalizedValue>();
    public VotingUploader(DataSource dataSource,ProgressPresenter pp, UploadSession uploadSession) throws AvatolCVException {
        this.dataSource = dataSource;
        this.pp = pp;
        this.uploadSession = uploadSession;
    }
    public void addScore(ScoreItem si) throws AvatolCVException {
        validateTrainTestConcernConsistent(si.getTrainTestConcern());
        
        List<ScoreItem> itemsForTTValue = scoreItemHash.get(si.getTrainTestConcernValue());
        if (null == itemsForTTValue){
            itemsForTTValue = new ArrayList<ScoreItem>();
            scoreItemHash.put(si.getTrainTestConcernValue(), itemsForTTValue);
        }
        itemsForTTValue.add(si);
        if (! ttValuesSeen.contains(si.getTrainTestConcernValue())){
            ttValuesSeen.add(si.getTrainTestConcernValue());
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
            ScoreItem winner = getVoteWinnerForScoreItems(itemsForTTValue, ttConcernVal);
            voteWinnerHash.put(ttConcernVal, winner);
        }
    }
    public ScoreItem getVoteWinner(NormalizedValue ttConcernVal){
        return voteWinnerHash.get(ttConcernVal);
    }
    private ScoreItem getVoteWinnerForScoreItems(List<ScoreItem> items, NormalizedValue ttConcernVal){
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
            ScoreItem result = itemsWithValList.get(0).getItems().get(0);
            result.setNewValue(valuesSeen.get(0));
            result.deduceScoringFate();
            result.setImageIDsRepresentedByWinner(getImageIdList(items));
            return result;
        }
        // if there are two or more values seen, if the first two are a tie, then that means there is an n-way tie, which means we should abstain from scoring
        int count0 = itemsWithValList.get(0).getCount();
        int count1 = itemsWithValList.get(1).getCount();
        if (count0 == count1){
            // return prior value
            logger.info("tie with first two values having  " + count0 + " ... NOT uploading" );
            ScoreItem result = itemsWithValList.get(0).getItems().get(0);
            result.noteTieVote();
            result.setImageIDsRepresentedByWinner(getImageIdList(items));
            return result;
        }
        // not a tie, return the new value for the top count
        logger.info("hands down winning value is  " + count0 + " ... using newValue " + items.get(0).getNewValue() );
        ScoreItem result = itemsWithValList.get(0).getItems().get(0);
        result.deduceScoringFate();
        result.setImageIDsRepresentedByWinner(getImageIdList(items));
        return result;
        
    }
    public List<String> getImageIdList(List<ScoreItem> items){
    	List<String> result = new ArrayList<String>();
    	for (ScoreItem si : items){
    		result.add(si.getImageID());
    	}
    	return result;
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
            if (null == priorVal1){
                if (null != item.getExistingValueForKey()){
                    throw new AvatolCVException("Assumption violated - expected all images with the same trainTestConcernValue to have consistent prior values at the dataSource and encountered " + priorVal1 + " and " + item.getExistingValueForKey());
                }
            }
            else {
                if (!priorVal1.equals(item.getExistingValueForKey())){
                    throw new AvatolCVException("Assumption violated - expected all images with the same trainTestConcernValue to have consistent prior values at the dataSource and encountered " + priorVal1 + " and " + item.getExistingValueForKey());
                }
            }
        }
    }
    public List<ScoreItem> getVoteWinners(){
    	List<ScoreItem> result = new ArrayList<ScoreItem>();
    	Collections.sort(ttValuesSeen);
    	for (NormalizedValue nv : ttValuesSeen){
    		result.add(voteWinnerHash.get(nv));
    	}
    	return result;
    }
}
