package edu.oregonstate.eecs.iis.avatolcv.bisque;

import java.util.List;

import edu.oregonstate.eecs.iis.avatolcv.core.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.core.ImageInfo;
import edu.oregonstate.eecs.iis.avatolcv.core.Step;
import edu.oregonstate.eecs.iis.avatolcv.core.View;
import edu.oregonstate.eecs.iis.avatolcv.ws.BisqueWSClient;
import edu.oregonstate.eecs.iis.avatolcv.ws.BisqueWSException;
import edu.oregonstate.eecs.iis.avatolcv.ws.bisque.BisqueAnnotation;
import edu.oregonstate.eecs.iis.avatolcv.ws.bisque.BisqueDataset;
import edu.oregonstate.eecs.iis.avatolcv.ws.bisque.BisqueImage;

public class BisqueCharChoiceStep implements Step {
    private BisqueWSClient wsClient  = null;
    private BisqueSessionData sessionData = null;
    private View view = null;
    private BisqueAnnotation chosenCharacter = null;
            
            
    public BisqueCharChoiceStep(View view, BisqueWSClient wsClient, BisqueSessionData sessionData){
        this.wsClient = wsClient;
        this.view = view;
        this.sessionData = sessionData;
    }
    @Override
    public void init() {
        // nothing to do
    }
    public List<BisqueAnnotation> getCharacters()  throws AvatolCVException {
        try {
            BisqueDataset ds = this.sessionData.getChosenDataset();
            List<BisqueImage> images = this.wsClient.getImagesForDataset(ds.getResourceUniq());
            if (null == images){
                throw new AvatolCVException("no images available to get annotation info from.");
            }
            if (images.size() == 0){
                throw new AvatolCVException("no images available to get annotation info from.");
            }
            List<BisqueAnnotation> annotations = this.wsClient.getAnnotationsForImage(images.get(0).getResourceUniq());
            return annotations;
        }
        catch(BisqueWSException e){
            throw new AvatolCVException("problem loading character annotations. " + e.getMessage(), e);
        }
        
    }
    public void setChosenAnnotation(BisqueAnnotation bi){
        this.chosenCharacter = bi;
    }
    @Override
    public void consumeProvidedData() throws AvatolCVException {
        this.sessionData.setCurrentCharacter(this.chosenCharacter);
    }

    
    @Override
    public View getView() {
        return this.view;
    }
   

}
