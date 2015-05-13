package edu.oregonstate.eecs.iis.avatolcv.morphobank;

import java.util.List;

import edu.oregonstate.eecs.iis.avatolcv.core.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.core.Step;
import edu.oregonstate.eecs.iis.avatolcv.core.View;
import edu.oregonstate.eecs.iis.avatolcv.ws.MorphobankWSClient;
import edu.oregonstate.eecs.iis.avatolcv.ws.MorphobankWSException;
import edu.oregonstate.eecs.iis.avatolcv.ws.morphobank.CellMediaInfo.MBMediaInfo;
import edu.oregonstate.eecs.iis.avatolcv.ws.morphobank.ViewInfo.MBView;

public class MBViewChoiceStep implements Step {
    private MorphobankWSClient wsClient = null;
    private View view = null;
    private MBSessionData sessionData = null;
    private List<MBView> mbImages = null;
    private MBView chosenView = null;
    
    public MBViewChoiceStep(View view, MorphobankWSClient wsClient, MBSessionData sessionData){
        this.wsClient = wsClient;
        this.view = view;
        this.sessionData = sessionData;
    }
    public List<MBView> getViews() throws AvatolCVException {
        try {
            String projectID = sessionData.getChosenMatrix().getProjectID();
            return wsClient.getViewsForProject(projectID);
        }
        catch(MorphobankWSException e){
            throw new AvatolCVException("problem getting views for project. " + e.getMessage());
        }
    }
    public void setChosenView(MBView v){
        this.chosenView = v;
    }
    
    @Override
    public void init() throws AvatolCVException {
        // TODO Auto-generated method stub

    }

    @Override
    public void consumeProvidedData() throws AvatolCVException {
        sessionData.setChosenView(this.chosenView);

    }

    @Override
    public View getView() {
        // TODO Auto-generated method stub
        return null;
    }

}
