package edu.oregonstate.eecs.iis.avatolcv.morphobank;

import java.util.List;

import edu.oregonstate.eecs.iis.avatolcv.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.core.ImageInfo;
import edu.oregonstate.eecs.iis.avatolcv.core.Step;
import edu.oregonstate.eecs.iis.avatolcv.core.View;
import edu.oregonstate.eecs.iis.avatolcv.obsolete.bisque.BisqueSessionData;

public class MBExclusionPropertyStep implements Step {
    private String view = null;
    private MBSessionData sessionData = null;
    List<ImageInfo> imagesToInclude = null;
    List<ImageInfo> imagesToExclude = null;
    public MBExclusionPropertyStep(String view, MBSessionData sessionData){
        this.view = view;
        this.sessionData = sessionData;
    }
    @Override
    public void init() throws AvatolCVException {
        // TODO Auto-generated method stub

    }

    @Override
    public void consumeProvidedData() throws AvatolCVException {
        // TODO Auto-generated method stub

    }

}
