package edu.oregonstate.eecs.iis.avatolcv.steps;

import edu.oregonstate.eecs.iis.avatolcv.AvatolCVFileSystem;
import edu.oregonstate.eecs.iis.avatolcv.core.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.core.DataFilter;
import edu.oregonstate.eecs.iis.avatolcv.core.SessionInfo;
import edu.oregonstate.eecs.iis.avatolcv.core.Step;

public class SummaryFilterStep implements Step {
    private SessionInfo sessionInfo;
    public SummaryFilterStep(SessionInfo sessionInfo){
        this.sessionInfo = sessionInfo;
    }
    @Override
    public void init() throws AvatolCVException {
        // TODO Auto-generated method stub

    }

    public DataFilter getDataFilter() throws AvatolCVException {
        return this.sessionInfo.getDataSource().getDataFilter(AvatolCVFileSystem.getSessionDir());
    }
    @Override
    public void consumeProvidedData() throws AvatolCVException {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean hasDataLoadPhase() {
        return false;
    }

    public String getSummaryText(){
        return this.sessionInfo.getDataSource().getDatasetSummaryText();
    }
}
