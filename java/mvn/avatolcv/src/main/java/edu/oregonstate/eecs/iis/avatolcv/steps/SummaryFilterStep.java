package edu.oregonstate.eecs.iis.avatolcv.steps;

import edu.oregonstate.eecs.iis.avatolcv.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.core.DataFilter;
import edu.oregonstate.eecs.iis.avatolcv.core.SessionInfo;

public class SummaryFilterStep  extends Answerable implements Step {
    private SessionInfo sessionInfo;
    public SummaryFilterStep(SessionInfo sessionInfo){
        this.sessionInfo = sessionInfo;
    }
    @Override
    public void init() throws AvatolCVException {
        // TODO Auto-generated method stub

    }

    public DataFilter getDataFilter() throws AvatolCVException {
    	return this.sessionInfo.getDataFilter();
        //return this.sessionInfo.getDataSource().getDataFilter(AvatolCVFileSystem.getSessionDir());
    }
    @Override
    public void consumeProvidedData() throws AvatolCVException {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean hasFollowUpDataLoadPhase() {
        return false;
    }

    public String getSummaryText(){
        return this.sessionInfo.getDataSource().getDatasetSummaryText();
    }
    @Override
    public boolean isEnabledByPriorAnswers() {
        return true;
    }
}
