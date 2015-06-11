package edu.oregonstate.eecs.iis.avatolcv.morphobank;

import java.util.List;

import edu.oregonstate.eecs.iis.avatolcv.core.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.core.ImageInfo;
import edu.oregonstate.eecs.iis.avatolcv.core.Step;
import edu.oregonstate.eecs.iis.avatolcv.core.View;
import edu.oregonstate.eecs.iis.avatolcv.ws.BisqueWSClient;
import edu.oregonstate.eecs.iis.avatolcv.ws.MorphobankWSClient;

public class MBExclusionQualityStep implements Step {
    private MorphobankWSClient wsClient = null;
    private String view = null;
    private boolean userHasViewed = false;
    private MBSessionData sessionData = null;
    public MBExclusionQualityStep(String view, MorphobankWSClient wsClient, MBSessionData sessionData){
        this.wsClient = wsClient;
        this.view = view;
        this.sessionData = sessionData;
    }
    public List<ImageInfo> getImagesLarge(){
        return this.sessionData.getImagesLarge();
    }
    public List<ImageInfo> getImagesThumbnail(){
        return this.sessionData.getImagesThumbnail();
    }
    public void acceptExclusions(){
        this.sessionData.acceptExclusions();
    }
    public ImageInfo getLargeImageForImage(ImageInfo ii) throws AvatolCVException {
    	return this.sessionData.getLargeImageForImage(ii);
    }
    @Override
    public void init() throws AvatolCVException {
        // TODO Auto-generated method stub

    }

    @Override
    public void consumeProvidedData() throws AvatolCVException {
        // TODO Auto-generated method stub

    }

    @Override
    public String getView() {
        // TODO Auto-generated method stub
        return null;
    }
    
    public void rotateVertically(ImageInfo ii) throws AvatolCVException {
        this.sessionData.rotateVertically(ii);
    }

    public void rotateHorizontally(ImageInfo ii) throws AvatolCVException {
        this.sessionData.rotateHorizontally(ii);
    }
}
