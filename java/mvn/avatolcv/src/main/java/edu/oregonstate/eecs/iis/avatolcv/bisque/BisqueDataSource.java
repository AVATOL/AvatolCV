package edu.oregonstate.eecs.iis.avatolcv.bisque;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edu.oregonstate.eecs.iis.avatolcv.core.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.core.DataSource;
import edu.oregonstate.eecs.iis.avatolcv.generic.DatasetInfo;
import edu.oregonstate.eecs.iis.avatolcv.ws.BisqueWSClient;
import edu.oregonstate.eecs.iis.avatolcv.ws.BisqueWSClientImpl;
import edu.oregonstate.eecs.iis.avatolcv.ws.BisqueWSException;
import edu.oregonstate.eecs.iis.avatolcv.ws.bisque.BisqueDataset;

public class BisqueDataSource implements DataSource {
    BisqueWSClient wsClient = null;
    public BisqueDataSource(){
        wsClient = new BisqueWSClientImpl();
    }
    @Override
    public boolean authenticate(String username, String password) throws AvatolCVException {
        boolean result = false;
        try {
            result = this.wsClient.authenticate(username, password);
        }
        catch(BisqueWSException e){
            throw new AvatolCVException(e.getMessage(), e);
        }
        return result;
    }
    @Override
    public boolean isAuthenticated() {
        // TODO Auto-generated method stub
        return this.wsClient.isAuthenticated();
    }
    
    @Override
    public List<DatasetInfo> getDatasets() throws AvatolCVException {
        List<DatasetInfo> datasets = new ArrayList<DatasetInfo>();
        try {
            List<BisqueDataset> bds = wsClient.getDatasets();
            
            for (BisqueDataset ds : bds){
                DatasetInfo di = new DatasetInfo();
                di.setName(ds.getName());
                di.setID(ds.getResourceUniq());
                di.setProjectID(DatasetInfo.NO_CONTAINING_PROJECT_ID);
                datasets.add(di);
            }
            Collections.sort(datasets);
            return datasets;
        }
        catch(BisqueWSException e){
            throw new AvatolCVException("problem loading datasets from Bisque ", e);
        }
    }
    @Override
    public void loadBasicDataForDataset(DatasetInfo di)
            throws AvatolCVException {
        // nothing to do here yet
    }
    @Override
    public String getDefaultUsername() {
        return "jedirv";
    }
    @Override
    public String getDefaultPassword() {
        return "Neton3plants**";
    }

}
