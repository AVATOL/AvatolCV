package edu.oregonstate.eecs.iis.avatolcv.core;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public class ScoringAlgorithms {
    public enum ScoringSessionFocus { 
        specimenPartPresenceAbsence, 
        specimeShapeAspect, 
        specimenTextureAspect };
    // DPM needs to score all presenceAbsence chars at same time, other might just want one char    
    public enum scoringScope {
        singleCharacter,
        multipleCharacter
    }
    private ScoringAlgorithms.ScoringSessionFocus sessionFocus = null;
    private String chosenScoringAlgorithm = null;
    private Hashtable<ScoringSessionFocus, String> radioButtonTextForFocusHash = new Hashtable<ScoringSessionFocus, String>();
    public Hashtable<String, ScoringSessionFocus> scoringFocusForName = new Hashtable<String,ScoringSessionFocus>();
    public Hashtable<ScoringSessionFocus, List<String>> namesForScoringFocus = new Hashtable<ScoringSessionFocus,List<String>>();
    public Hashtable<String, String> commandForName = new Hashtable<String,String>();
    public Hashtable<String, Boolean> enabledStateForName = new Hashtable<String,Boolean>();
    
    public ScoringAlgorithms() throws AvatolCVException {
        radioButtonTextForFocusHash.put(ScoringSessionFocus.specimenPartPresenceAbsence, "Score presence/absence of parts in a specimen");
        radioButtonTextForFocusHash.put(ScoringSessionFocus.specimeShapeAspect, "Score shape aspects of a specimen");
        radioButtonTextForFocusHash.put(ScoringSessionFocus.specimenTextureAspect, "Score texture aspects a specimen");
        addAlgorithm("DPM", ScoringSessionFocus.specimenPartPresenceAbsence, "invoke_batskull_system", true);
        addAlgorithm("LEAF", ScoringSessionFocus.specimeShapeAspect, "tbd", false);
        addAlgorithm("CRF", ScoringSessionFocus.specimenTextureAspect, "tbd", false);
    }
    
    public String getRadioButtonTextForScoringFocus(ScoringSessionFocus focus){
        return radioButtonTextForFocusHash.get(focus);
    }
    public void setSessionScoringFocus(ScoringAlgorithms.ScoringSessionFocus focus){
        this.sessionFocus = focus;
    }
    public ScoringAlgorithms.ScoringSessionFocus getSessionScoringFocus(){
        return this.sessionFocus;
    }
    public void setChosenAlgorithmName(String name){
        this.chosenScoringAlgorithm = name;
    }
    public String getChosenAlgorithmName(){
        return this.chosenScoringAlgorithm;
    }
    public void addAlgorithm(String name, ScoringSessionFocus scoringFocus, String commandLineInvocationName, boolean isEnabled) throws AvatolCVException {
        ScoringSessionFocus focus = scoringFocusForName.get(name);
        if (null != focus){
            throw new AvatolCVException("Algorithms must have unique names - this occurs twice: " + name);
        }
        scoringFocusForName.put(name, scoringFocus);
        List<String> names = namesForScoringFocus.get(scoringFocus);
        if (null == names){
            names = new ArrayList<String>();
            namesForScoringFocus.put(scoringFocus, names);
        }
        names.add(name);
        commandForName.put(name,  commandLineInvocationName);
        enabledStateForName.put(name, new Boolean(isEnabled));
    }
    public List<String> getAlgNamesForScoringFocus(ScoringSessionFocus focus){
        List<String> result = new ArrayList<String>();
        List<String> names = namesForScoringFocus.get(focus);
        result.addAll(names);
        return result;
    }
    public ScoringSessionFocus getScoringFocusForAlgName(String name) throws AvatolCVException {
        ScoringSessionFocus focus = scoringFocusForName.get(name);
        if (null == focus){
            throw new AvatolCVException("unknown algorithm specified " + name);
        }

        return focus;
    }

    public List<String> getNamesForScoringFocus(ScoringSessionFocus focus)  {
        List<String> result = new ArrayList<String>();
        List<String> names = namesForScoringFocus.get(focus);
        result.addAll(names);
        return result;
    }
    public boolean isAlgorithmEnabled(String name){
        Boolean b = enabledStateForName.get(name);
        if (null == b){
            return false;
        }
        return b.booleanValue();
    }
}
