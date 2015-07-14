package edu.oregonstate.eecs.iis.avatolcv.core;

import java.util.List;

import edu.oregonstate.eecs.iis.avatolcv.generic.DatasetInfo;

public interface DataSource {
    boolean authenticate(String username, String password) throws AvatolCVException ;
    boolean isAuthenticated();
    List<DatasetInfo> getDatasets() throws AvatolCVException ;
    void loadPrimaryMetadataForChosenDataset(ProgressPresenter pp, String processName) throws AvatolCVException;
    void loadRemainingMetadataForChosenDataset(ProgressPresenter pp, String processName) throws AvatolCVException;
    String getDefaultUsername();
    String getDefaultPassword();
    
    void setChosenDataset(DatasetInfo di);
    List<ChoiceItem> getScoringConcernItems(ScoringAlgorithms sa) throws AvatolCVException;
    String getInstructionsForScoringConcernScreen(ScoringAlgorithms sa);
    
    void setChosenScoringConcerns(List<ChoiceItem> items);
    void setChosenScoringConcern(ChoiceItem item);
    
    String getDatasetSummaryText();
}
