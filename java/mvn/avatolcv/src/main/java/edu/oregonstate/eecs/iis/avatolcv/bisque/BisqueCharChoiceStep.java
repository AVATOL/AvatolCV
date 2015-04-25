package edu.oregonstate.eecs.iis.avatolcv.bisque;

import java.util.List;

import edu.oregonstate.eecs.iis.avatolcv.core.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.core.ImageInfo;
import edu.oregonstate.eecs.iis.avatolcv.core.Step;
import edu.oregonstate.eecs.iis.avatolcv.core.View;
import edu.oregonstate.eecs.iis.avatolcv.ws.BisqueWSClient;
import edu.oregonstate.eecs.iis.avatolcv.ws.BisqueWSException;
import edu.oregonstate.eecs.iis.avatolcv.ws.bisque.BisqueAnnotation;

public class BisqueCharChoiceStep implements Step {
    private BisqueWSClient wsClient  = null;
    private BisqueSessionData sessionData = null;
    private View view = null;
    
    public BisqueCharChoiceStep(View view, BisqueWSClient wsClient, BisqueSessionData sessionData){
        this.wsClient = wsClient;
        this.view = view;
        this.sessionData = sessionData;
    }
    public List<BisqueAnnotation> getCharacters()  throws AvatolCVException {
        List<ImageInfo> imagesLarge = this.sessionData.getImagesLarge();
        if (null == imagesLarge){
            throw new AvatolCVException("no large images in place to get annotation info from.");
        }
        if (imagesLarge.size() == 0){
            throw new AvatolCVException("no large images in place to get annotation info from.");
        }
        String imageID = imagesLarge.get(0).getID();
        try {
            List<BisqueAnnotation> annotations = this.wsClient.getAnnotationsForImage(imageID);
            return annotations;
        }
        catch(BisqueWSException e){
            throw new AvatolCVException("problem loading annotations. " + e.getMessage(), e);
        }
        
    }
    @Override
    public void consumeProvidedData() throws AvatolCVException {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean needsAnswering() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public View getView() {
        return this.view;
    }

}
