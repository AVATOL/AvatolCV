package edu.oregonstate.eecs.iis.avatolcv.core;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public class ScoringAlgorithms {
	public static final String SHELL_BATSKULL = "DPM";
    public enum ScoringSessionFocus { 
        SPECIMEN_PART_PRESENCE_ABSENCE, 
        SPECIMEN_SHAPE_ASPECT, 
        SPECIMEN_TEXTURE_ASPECT };
    // DPM needs to score all presenceAbsence chars at same time, other might just want one char    
    public enum ScoringScope {
        SINGLE_ITEM,
        MULTIPLE_ITEM
    }
    // 
    public enum LaunchThrough {
    	MATLAB,
    	OTHER
    }
    private ScoringAlgorithms.ScoringSessionFocus sessionFocus = null;
    private String chosenScoringAlgorithm = null;
    private Hashtable<ScoringSessionFocus, String> radioButtonTextForFocusHash = new Hashtable<ScoringSessionFocus, String>();
    public Hashtable<String, ScoringSessionFocus> scoringFocusForName = new Hashtable<String,ScoringSessionFocus>();
    public Hashtable<ScoringSessionFocus, List<String>> namesForScoringFocus = new Hashtable<ScoringSessionFocus,List<String>>();
    public Hashtable<String, String> commandForName = new Hashtable<String,String>();
    public Hashtable<String, Boolean> enabledStateForName = new Hashtable<String,Boolean>();
    
    public ScoringAlgorithms() throws AvatolCVException {
        radioButtonTextForFocusHash.put(ScoringSessionFocus.SPECIMEN_PART_PRESENCE_ABSENCE, "Score presence/absence of parts in a specimen");
        radioButtonTextForFocusHash.put(ScoringSessionFocus.SPECIMEN_SHAPE_ASPECT, "Score shape aspects of a specimen");
        radioButtonTextForFocusHash.put(ScoringSessionFocus.SPECIMEN_TEXTURE_ASPECT, "Score texture aspects a specimen");
        
        //addAlgorithm(SHELL_BATSKULL, ScoringSessionFocus.SPECIMEN_PART_PRESENCE_ABSENCE, ScoringScope.MULTIPLE_ITEM, LaunchThrough.OTHER, "invoke_batskull_system", true, "bogusDir");
        //addAlgorithm("LEAF", ScoringSessionFocus.SPECIMEN_SHAPE_ASPECT,ScoringScope.SINGLE_ITEM, LaunchThrough.OTHER,  "tbd", false, "bogusDir");
        //addAlgorithm("CRF", ScoringSessionFocus.SPECIMEN_TEXTURE_ASPECT,ScoringScope.SINGLE_ITEM, LaunchThrough.OTHER,  "tbd", false, "bogusDir");
    }
    
    public ScoringScope getScoringScope(){
    	if (sessionFocus == ScoringSessionFocus.SPECIMEN_PART_PRESENCE_ABSENCE && chosenScoringAlgorithm == SHELL_BATSKULL){
    		return ScoringScope.MULTIPLE_ITEM;
    	}
    	return ScoringScope.SINGLE_ITEM;
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
    public void addAlgorithm(String name, ScoringSessionFocus scoringFocus, ScoringScope scoringScope, LaunchThrough launchThrough, String commandLineInvocationName, boolean isEnabled, String parentDir) throws AvatolCVException {
        ScoringSessionFocus priorSetFocus = scoringFocusForName.get(name);
        if (null != priorSetFocus){
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
        if (null != names){
            result.addAll(names);
        }
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
        if (null != names){
            result.addAll(names);
        }
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
