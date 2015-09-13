package edu.oregonstate.eecs.iis.avatolcv.steps;


import edu.oregonstate.eecs.iis.avatolcv.core.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.core.ProgressPresenter;
import edu.oregonstate.eecs.iis.avatolcv.core.SessionInfo;
import edu.oregonstate.eecs.iis.avatolcv.core.Step;

public class Z_Obsolete_ScoringConcernDataPullStep implements Step {
	private static final String NL = System.getProperty("line.separator");
    private SessionInfo sessionInfo = null;
    public Z_Obsolete_ScoringConcernDataPullStep(SessionInfo sessionInfo){
        this.sessionInfo = sessionInfo;
    }
   
    public void loadPrimaryMetadataForChosenDataset(ProgressPresenter pp, String processName) throws AvatolCVException {
        this.sessionInfo.getDataSource().loadPrimaryMetadataForChosenDataset(pp, processName);
        
    }
    public void progressUpdate(ProgressPresenter pp, String processName, double percentDone){
        if (null!= pp){
            pp.updateProgress(processName, percentDone);
        }
    }
    public void progressMessage(ProgressPresenter pp, String processName, String message){
        if (null != pp){
            pp.setMessage(processName, message);
        }
    }
    
    @Override
    public void init() throws AvatolCVException {
        // TODO Auto-generated method stub

    }

    @Override
    public void consumeProvidedData() throws AvatolCVException {
        // nothing to do - images already download

    }

    @Override
    public boolean hasFollowUpDataLoadPhase() {
        // TODO Auto-generated method stub
        return false;
    }

}
