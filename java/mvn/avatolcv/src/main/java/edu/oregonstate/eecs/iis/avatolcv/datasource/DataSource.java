package edu.oregonstate.eecs.iis.avatolcv.datasource;

import java.util.List;

import edu.oregonstate.eecs.iis.avatolcv.AvatolCVDataFiles;
import edu.oregonstate.eecs.iis.avatolcv.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.algorithm.ScoringAlgorithm;
import edu.oregonstate.eecs.iis.avatolcv.algorithm.ScoringAlgorithm.ScoringScope;
import edu.oregonstate.eecs.iis.avatolcv.algorithm.ScoringAlgorithm.ScoringSessionFocus;
import edu.oregonstate.eecs.iis.avatolcv.normalized.NormalizedImageInfos;
import edu.oregonstate.eecs.iis.avatolcv.normalized.NormalizedKey;
import edu.oregonstate.eecs.iis.avatolcv.normalized.NormalizedValue;
import edu.oregonstate.eecs.iis.avatolcv.scoring.ScoreIndex;
import edu.oregonstate.eecs.iis.avatolcv.session.DataFilter;
import edu.oregonstate.eecs.iis.avatolcv.session.DatasetInfo;
import edu.oregonstate.eecs.iis.avatolcv.session.ProgressPresenter;
import edu.oregonstate.eecs.iis.avatolcv.session.SessionImages;

public interface DataSource {
    String getName();
    boolean authenticate(String username, String password) throws AvatolCVException ;
    boolean isAuthenticated();
    List<DatasetInfo> getDatasets() throws AvatolCVException ;
    void loadPrimaryMetadataForChosenDataset(ProgressPresenter pp, String processName) throws AvatolCVException;
    void loadRemainingMetadataForChosenDataset(ProgressPresenter pp, String processName) throws AvatolCVException;
    String getDefaultUsername();
    String getDefaultPassword();
    
    void setChosenDataset(DatasetInfo di) throws AvatolCVException ;
    List<ChoiceItem> getScoringConcernOptions(ScoringAlgorithm.ScoringScope scope, ScoringAlgorithm.ScoringSessionFocus focus) throws AvatolCVException;
    String getInstructionsForScoringConcernScreen(ScoringAlgorithm.ScoringScope scope, ScoringAlgorithm.ScoringSessionFocus focus);
    
    void setChosenScoringConcerns(List<ChoiceItem> items) throws AvatolCVException;
    void setChosenScoringConcern(ChoiceItem item)  throws AvatolCVException;
    
    String getDatasetSummaryText();
    AvatolCVDataFiles getAvatolCVDataFiles();
    //DataFilter getDataFilter(String specificSessionDir)  throws AvatolCVException;
    void setSessionImages(SessionImages sessionImages);
    void acceptFilter();
    
    void downloadImages(ProgressPresenter pp, String processName)  throws AvatolCVException;
    //void setStandard
    String getDatasetTitleText();
    void setNormalizedImageInfos(NormalizedImageInfos niis) throws AvatolCVException ;
    String getDefaultTrainTestConcern();
    String getMandatoryTrainTestConcern();
    NormalizedValue getValueForKeyAtDatasourceForImage(NormalizedKey normCharKey, String imageID, NormalizedKey trainTestConcern, NormalizedValue trainTestConcernValue) throws AvatolCVException ;
    boolean deleteScoreForKey(String imageID, NormalizedKey key, NormalizedKey trainTestConcern, NormalizedValue trainTestConcernValue) throws AvatolCVException ;
    boolean reviseValueForKey(String provenanceString, String imageID, NormalizedKey key, NormalizedValue value, NormalizedKey trainTestConcern, NormalizedValue trainTestConcernValue) throws AvatolCVException ;
    boolean addKeyValue(String provenaceString, String imageID, NormalizedKey key, NormalizedValue value, NormalizedKey trainTestConcern, NormalizedValue trainTestConcernValue) throws AvatolCVException ;
    List<String> filterBadSortCandidates(List<String> list);
    void prepForUpload(List<String> charIDs, List<String> trainTestConcernValueIDs) throws AvatolCVException ;
    boolean groupByTrainTestConcernValueAndVoteForUpload();
    String getDatasetIDforName(String name) throws AvatolCVException;
    void forgetMetadata() throws AvatolCVException ;
    String getRepullPrompt();
}
