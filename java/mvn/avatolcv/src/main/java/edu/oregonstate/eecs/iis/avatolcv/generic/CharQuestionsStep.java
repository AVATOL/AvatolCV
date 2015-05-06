package edu.oregonstate.eecs.iis.avatolcv.generic;

import edu.oregonstate.eecs.iis.avatolcv.core.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.core.Step;
import edu.oregonstate.eecs.iis.avatolcv.core.View;

public class CharQuestionsStep implements Step {

    private String questionsFilePath = null;
    public CharQuestionsStep(String questionsFilePath){
        this.questionsFilePath = questionsFilePath;
    }
    @Override
    public void init() {
        // TODO
    }
    @Override
    public void consumeProvidedData() throws AvatolCVException {
        // TODO Auto-generated method stub
    }


    @Override
    public View getView() {
        // TODO Auto-generated method stub
        return null;
    }

}
