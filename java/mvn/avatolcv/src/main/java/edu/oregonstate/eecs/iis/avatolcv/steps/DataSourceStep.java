package edu.oregonstate.eecs.iis.avatolcv.steps;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.oregonstate.eecs.iis.avatolcv.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.datasource.BisqueDataSource;
import edu.oregonstate.eecs.iis.avatolcv.datasource.FileSystemDataSource;
import edu.oregonstate.eecs.iis.avatolcv.datasource.MorphobankDataSource;
import edu.oregonstate.eecs.iis.avatolcv.session.DataIssue;
import edu.oregonstate.eecs.iis.avatolcv.session.SessionInfo;

public class DataSourceStep  extends Answerable implements Step {
    private static final Logger logger = LogManager.getLogger(DataSourceStep.class);

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
        logger.info("datasource set to : morphobank");
        choice = DataSourceChoice.MORPHOBANK;
    }
    public void activateBisqueDataSource(){
        logger.info("datasource set to : bisque");
        choice = DataSourceChoice.BISQUE;
    }
    public void activateFileSystemDataSource(){
        logger.info("datasource set to : file system");
        choice = DataSourceChoice.FILE_SYSTEM;
    }
    @Override
    public void consumeProvidedData() throws AvatolCVException {
        if (choice == DataSourceChoice.MORPHOBANK){
            this.sessionInfo.setDataSource(new MorphobankDataSource());
        }
        else if (choice == DataSourceChoice.BISQUE){
            this.sessionInfo.setDataSource(new BisqueDataSource());
        }
        else {
            this.sessionInfo.setDataSource(new FileSystemDataSource());
        }
    }
    @Override
    public boolean hasFollowUpDataLoadPhase() {
        // TODO Auto-generated method stub
        return false;
    }
    @Override
    public boolean isEnabledByPriorAnswers() {
        return true;
    }
	@Override
	public boolean shouldRenderIfBackingIntoIt() {
		return true;
	}
	@Override
	public SessionInfo getSessionInfo() {
		return this.sessionInfo;
	}
   
}
