package edu.oregonstate.eecs.iis.avatolcv.morphobank;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edu.oregonstate.eecs.iis.avatolcv.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.obsolete.View;
import edu.oregonstate.eecs.iis.avatolcv.steps.Step;
import edu.oregonstate.eecs.iis.avatolcv.ws.MorphobankWSClient;
import edu.oregonstate.eecs.iis.avatolcv.ws.MorphobankWSException;
import edu.oregonstate.eecs.iis.avatolcv.ws.morphobank.CellMediaInfo.MBMediaInfo;
import edu.oregonstate.eecs.iis.avatolcv.ws.morphobank.CharacterInfo.MBCharacter;
import edu.oregonstate.eecs.iis.avatolcv.ws.morphobank.ViewInfo.MBView;

public class MBViewChoiceStep implements Step {
    private MorphobankWSClient wsClient = null;
    private MBSessionData sessionData = null;
    private List<MBView> mbViews = null;
    private MBView chosenView = null;
    
    public MBViewChoiceStep(MorphobankWSClient wsClient, MBSessionData sessionData){
        this.wsClient = wsClient;
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
    public List<String> getViewNames() throws AvatolCVException {
        List<String> viewNames = new ArrayList<String>();
        this.mbViews = getViews();
        for (MBView mv : mbViews){
            viewNames.add(mv.getName());
        }
        Collections.sort(viewNames);
        return viewNames;
    }
    public void setChosenView(String viewName) throws AvatolCVException{
        this.chosenView = null;
        for (MBView mv : this.mbViews){
            String name = mv.getName();
            if (name.equals(viewName)){
                this.chosenView = mv;
                //this.sessionData.setChosenDataset(ds);
            }
        }
        if (this.chosenView == null){
            throw new AvatolCVException("no MBView match for name " + viewName);
        }
    }
    
    @Override
    public void init() throws AvatolCVException {
        // TODO Auto-generated method stub

    }

    @Override
    public void consumeProvidedData() throws AvatolCVException {
        sessionData.setChosenView(this.chosenView);

    }
}
