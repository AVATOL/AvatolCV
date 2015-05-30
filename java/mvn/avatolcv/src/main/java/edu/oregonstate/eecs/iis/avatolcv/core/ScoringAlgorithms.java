package edu.oregonstate.eecs.iis.avatolcv.core;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public class ScoringAlgorithms {
    public Hashtable<String, String> scoringFocusForName = new Hashtable<String,String>();
    public Hashtable<String, String> nameForScoringFocus = new Hashtable<String,String>();
    public Hashtable<String, String> commandForName = new Hashtable<String,String>();
    public Hashtable<String, Boolean> enabledStateForName = new Hashtable<String,Boolean>();
    public List<String> algNames = new ArrayList<String>();
    
    public ScoringAlgorithms(){
        addAlgorithm("DPM", "presence/absence of parts of specimen", "invoke_batskull_system", true);
        addAlgorithm("LEAF", "shape aspects of specimen", "tbd", false);
        addAlgorithm("CRF", "texture aspects of specimen", "tbd", false);
    }
    public void addAlgorithm(String name, String scoringFocus, String commandLineInvocationName, boolean isEnabled){
        algNames.add(name);
        scoringFocusForName.put(name, scoringFocus);
        nameForScoringFocus.put(scoringFocus, name);
        commandForName.put(name,  commandLineInvocationName);
        enabledStateForName.put(name, new Boolean(isEnabled));
    }
    public List<String> getAlgNames(){
        List<String> result = new ArrayList<String>();
        result.addAll(algNames);
        return result;
    }
    public String getScoringFocusForAlgName(String name) throws AvatolCVException {
        if (!algNames.contains(name)){
            throw new AvatolCVException("unknown algorithm specified " + name);
        }
        String focus = scoringFocusForName.get(name);
        return focus;
    }

    public String getNameForScoringFocus(String focus) throws AvatolCVException {
        String name = nameForScoringFocus.get(focus);
        if (null == name){
            throw new AvatolCVException("unknown algorithm focus specified " + focus);
        }
        return name;
    }
    public boolean isAlgorithmEnabled(String name){
        Boolean b = enabledStateForName.get(name);
        if (null == b){
            return false;
        }
        return b.booleanValue();
    }
}
