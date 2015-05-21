package edu.oregonstate.eecs.iis.avatolcv.morphobank;

import java.util.List;

import edu.oregonstate.eecs.iis.avatolcv.bisque.BisqueSessionData;
import edu.oregonstate.eecs.iis.avatolcv.core.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.core.Step;
import edu.oregonstate.eecs.iis.avatolcv.core.View;
import edu.oregonstate.eecs.iis.avatolcv.ws.BisqueWSClient;
import edu.oregonstate.eecs.iis.avatolcv.ws.BisqueWSException;
import edu.oregonstate.eecs.iis.avatolcv.ws.MorphobankWSClient;
import edu.oregonstate.eecs.iis.avatolcv.ws.MorphobankWSException;
import edu.oregonstate.eecs.iis.avatolcv.ws.bisque.BisqueAnnotation;
import edu.oregonstate.eecs.iis.avatolcv.ws.bisque.BisqueDataset;
import edu.oregonstate.eecs.iis.avatolcv.ws.bisque.BisqueImage;
import edu.oregonstate.eecs.iis.avatolcv.ws.morphobank.CharacterInfo.MBCharacter;
import edu.oregonstate.eecs.iis.avatolcv.ws.morphobank.MatrixInfo.MBMatrix;

public class MBCharChoiceStep implements Step {
    private MorphobankWSClient wsClient  = null;
    private MBSessionData sessionData = null;
    private String view = null;
    private MBCharacter chosenCharacter = null;
            
            
    public MBCharChoiceStep(String view, MorphobankWSClient wsClient, MBSessionData sessionData){
        this.wsClient = wsClient;
        this.view = view;
        this.sessionData = sessionData;
    }
    public List<MBCharacter> getCharacters()  throws AvatolCVException {
        return sessionData.getCharactersForCurrentMatrix();
    }
    public void setChosenCharacter(MBCharacter bi){
        this.chosenCharacter = bi;
    }
    @Override
    public void init() throws AvatolCVException {
        // TODO Auto-generated method stub

    }

    @Override
    public void consumeProvidedData() throws AvatolCVException {
        sessionData.setChosenCharacter(chosenCharacter);
    }

    @Override
    public String getView() {
        // TODO Auto-generated method stub
        return null;
    }

}
