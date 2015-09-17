package edu.oregonstate.eecs.iis.avatolcv.core;

import java.util.List;

import edu.oregonstate.eecs.iis.avatolcv.generic.DatasetInfo;

public interface DataSource {
    String getName();
    boolean authenticate(String username, String password) throws AvatolCVException ;
    boolean isAuthenticated();
    List<DatasetInfo> getDatasets() throws AvatolCVException ;
    void loadPrimaryMetadataForChosenDataset(ProgressPresenter pp, String processName) throws AvatolCVException;
    void loadRemainingMetadataForChosenDataset(ProgressPresenter pp, String processName) throws AvatolCVException;
    String getDefaultUsername();
    String getDefaultPassword();
    
    void setChosenDataset(DatasetInfo di);
    List<ChoiceItem> getScoringConcernOptions(ScoringAlgorithms.ScoringScope scope, ScoringAlgorithms.ScoringSessionFocus focus) throws AvatolCVException;
    String getInstructionsForScoringConcernScreen(ScoringAlgorithms.ScoringScope scope, ScoringAlgorithms.ScoringSessionFocus focus);
    
    void setChosenScoringConcerns(List<ChoiceItem> items);
    void setChosenScoringConcern(ChoiceItem item);
    
    String getDatasetSummaryText();
    AvatolCVDataFiles getAvatolCVDataFiles();
    DataFilter getDataFilter(String specificSessionDir)  throws AvatolCVException;
    void acceptFilter();
    
    void downloadImages(ProgressPresenter pp, String processName)  throws AvatolCVException;
    //void setStandard
    public String getDatasetTitleText();
}
