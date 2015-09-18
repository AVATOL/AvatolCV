package edu.oregonstate.eecs.iis.obsolete.avatolcv.morphobank;

import java.util.List;

import edu.oregonstate.eecs.iis.avatolcv.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.core.ImageInfo;
import edu.oregonstate.eecs.iis.avatolcv.steps.Step;
import edu.oregonstate.eecs.iis.avatolcv.ws.BisqueWSClient;
import edu.oregonstate.eecs.iis.avatolcv.ws.MorphobankWSClient;
import edu.oregonstate.eecs.iis.obsolete.avatolcv.View;

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
    public boolean isRotatedHorizontally(ImageInfo ii){
    	return this.sessionData.isRotatedHorizontally(ii);
    }
    public boolean isRotatedVertically(ImageInfo ii){
    	return this.sessionData.isRotatedVertically(ii);
    }
    public void rotateVertically(ImageInfo ii) throws AvatolCVException {
        this.sessionData.rotateVertically(ii);
    }

    public void rotateHorizontally(ImageInfo ii) throws AvatolCVException {
        this.sessionData.rotateHorizontally(ii);
    }
}
