package edu.oregonstate.eecs.iis.avatolcv.orientation;

import edu.oregonstate.eecs.iis.avatolcv.core.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.core.Step;
import edu.oregonstate.eecs.iis.avatolcv.core.View;

/*
 * place holder step for when we add labeling step for orientation
 */
public class OrientStep2_LabelTrainingExamples implements Step {
    
    public OrientStep2_LabelTrainingExamples(){
        
    }
    @Override
    public void consumeProvidedData() throws AvatolCVException {

    }

    @Override
    public boolean needsAnswering() {
        return false;
    }

    @Override
    public View getView() {
        return null;
    }
    @Override
    public void init() throws AvatolCVException {
        // TODO Auto-generated method stub
        
    }

}
