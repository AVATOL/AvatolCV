package edu.oregonstate.eecs.iis.avatolcv.core;

import java.util.List;

import edu.oregonstate.eecs.iis.avatolcv.generic.DatasetInfo;

public interface DataSource {
    boolean authenticate(String username, String password) throws AvatolCVException ;
    boolean isAuthenticated();
    List<DatasetInfo> getDatasets() throws AvatolCVException ;
    void loadBasicDataForDataset(DatasetInfo di) throws AvatolCVException;
    String getDefaultUsername();
    String getDefaultPassword();
}
