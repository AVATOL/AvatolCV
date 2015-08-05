package edu.oregonstate.eecs.iis.avatolcv.steps;

import edu.oregonstate.eecs.iis.avatolcv.bisque.BisqueDataSource;
import edu.oregonstate.eecs.iis.avatolcv.core.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.core.FileSystemDataSource;
import edu.oregonstate.eecs.iis.avatolcv.core.SessionInfo;
import edu.oregonstate.eecs.iis.avatolcv.core.Step;
import edu.oregonstate.eecs.iis.avatolcv.morphobank.MorphobankDataSource;

public class DataSourceStep implements Step {
    private enum DataSourceChoice {
        MORPHOBANK,
        BISQUE,
        FILE_SYSTEM
    }
    
    private DataSourceChoice choice = null; 
    private SessionInfo sessionInfo = null;
    public DataSourceStep(SessionInfo sessionInfo){
        this.sessionInfo = sessionInfo;
    }
    @Override
    public void init() throws AvatolCVException {
        // TODO Auto-generated method stub

    }

    public void setDataSourceToMorphobank(){
        choice = DataSourceChoice.MORPHOBANK;
    }
    public void activateBisqueDataSource(){
        choice = DataSourceChoice.BISQUE;
    }
    public void activateFileSystemDataSource(){
        choice = DataSourceChoice.FILE_SYSTEM;
    }
    @Override
    public void consumeProvidedData() throws AvatolCVException {
        if (choice == DataSourceChoice.MORPHOBANK){
            this.sessionInfo.setDataSource(new MorphobankDataSource(this.sessionInfo.getSessionDataRootDir()));
        }
        else if (choice == DataSourceChoice.BISQUE){
            this.sessionInfo.setDataSource(new BisqueDataSource(this.sessionInfo.getSessionDataRootDir()));
        }
        else {
            this.sessionInfo.setDataSource(new FileSystemDataSource());
        }
    }
    @Override
    public boolean hasDataLoadPhase() {
        // TODO Auto-generated method stub
        return false;
    }
   
}
