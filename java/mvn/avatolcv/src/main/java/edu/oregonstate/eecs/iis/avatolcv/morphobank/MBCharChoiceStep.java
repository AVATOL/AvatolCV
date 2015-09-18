package edu.oregonstate.eecs.iis.avatolcv.morphobank;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edu.oregonstate.eecs.iis.avatolcv.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.algorithm.ScoringAlgorithms;
import edu.oregonstate.eecs.iis.avatolcv.obsolete.View;
import edu.oregonstate.eecs.iis.avatolcv.obsolete.bisque.BisqueSessionData;
import edu.oregonstate.eecs.iis.avatolcv.steps.Step;
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
    private List<MBCharacter> chosenCharacters = null;
    private List<MBCharacter> mbChars = null;
            
            
    public MBCharChoiceStep(String view, MorphobankWSClient wsClient, MBSessionData sessionData){
        this.wsClient = wsClient;
        this.view = view;
        this.sessionData = sessionData;
    }
    public List<MBCharacter> getCharInfo(){
    	return this.sessionData.getCharactersForCurrentMatrix();
    }
    public ScoringAlgorithms getScoringAlgorithms(){
    	return this.sessionData.getScoringAlgorithms();
    }
    public List<MBCharacter> getCharacters()  throws AvatolCVException {
        return sessionData.getCharactersForCurrentMatrix();
    }
    public List<String> getAvailableCharNames() throws AvatolCVException {
        List<String> charNames = new ArrayList<String>();
        this.mbChars = getCharacters();
        for (MBCharacter ch : mbChars){
            charNames.add(ch.getCharName());
        }
        Collections.sort(charNames);
        return charNames;
    }
    public boolean isCharPresenceAbsence(String charName) throws AvatolCVException {
    	return this.sessionData.isCharPresenceAbsence(charName);
    }
    public void setChosenCharacters(List<MBCharacter> characters){
    	this.chosenCharacters = characters;
    }
    public void setChosenCharacter(String s) throws AvatolCVException{
        this.chosenCharacter = null;
        for (MBCharacter ch : this.mbChars){
            String name = ch.getCharName();
            if (name.equals(s)){
                this.chosenCharacter = ch;
                //this.sessionData.setChosenDataset(ds);
            }
        }
        if (this.chosenCharacter == null){
            throw new AvatolCVException("no MBCharacter match for name " + s);
        }
    }
    @Override
    public void init() throws AvatolCVException {
        // TODO Auto-generated method stub

    }

    @Override
    public void consumeProvidedData() throws AvatolCVException {
    	ScoringAlgorithms.ScoringScope scope = getScoringAlgorithms().getScoringScope();
    	if (scope == ScoringAlgorithms.ScoringScope.MULTIPLE_ITEM){
    		sessionData.setChosenCharacters(chosenCharacters);
    	}
    	else {
    		sessionData.setChosenCharacter(chosenCharacter);
    	}
        
    }
}
