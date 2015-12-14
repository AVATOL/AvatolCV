package edu.oregonstate.eecs.iis.avatolcv.algorithm;

import java.util.Hashtable;
import java.util.List;

import edu.oregonstate.eecs.iis.avatolcv.AvatolCVException;

public class ScoringAlgorithm extends Algorithm {
    public static final String PROPERTY_SCORING_FOCUS = "scoringFocus";
    public static final String PROPERTY_SCORING_SCOPE = "scoringScope";
    private static final String PROPERTY_INCLUDE_POINT_ANNOTATIONS_IN_SCORING_FILE = "includePointAnnotationsInScoringFile";
    public enum ScoringSessionFocus { 
        SPECIMEN_PART_PRESENCE_ABSENCE, 
        SPECIMEN_SHAPE_ASPECT, 
        SPECIMEN_TEXTURE_ASPECT };
    // DPM needs to score all presenceAbsence chars at same time, other might just want one char    
    public enum ScoringScope {
        SINGLE_ITEM,
        MULTIPLE_ITEM
    }
    private boolean includePointAnnotationsInScoringFile = false;
    private static Hashtable<ScoringSessionFocus, String> radioButtonTextForFocusHash = new Hashtable<ScoringSessionFocus, String>();
    private static Hashtable<String, ScoringSessionFocus> scoringFocusForName = new Hashtable<String,ScoringSessionFocus>();
    private static Hashtable<ScoringSessionFocus, List<String>> namesForScoringFocus = new Hashtable<ScoringSessionFocus,List<String>>();
    
    static {
        radioButtonTextForFocusHash.put(ScoringSessionFocus.SPECIMEN_PART_PRESENCE_ABSENCE, "Score presence/absence of parts in a specimen");
        radioButtonTextForFocusHash.put(ScoringSessionFocus.SPECIMEN_SHAPE_ASPECT, "Score shape or texture aspects of a specimen");
        //radioButtonTextForFocusHash.put(ScoringSessionFocus.SPECIMEN_TEXTURE_ASPECT, "Score texture aspects a specimen");
    }
    //private static ScoringSessionFocus focusChosenForSession = null;
    //private static ScoringScope scopeChosenForSession = null;
    
    private ScoringSessionFocus thisAlgorithmFocus = null;
    private ScoringScope thisAlgorithmScope = null;
    public ScoringAlgorithm(List<String> propsLines, String path) throws AvatolCVException {
        super(propsLines, path);
        loadScoringAlgorithmSpecificProperties(algPropsEntriesNotYetConsumed);
    }
    private void loadScoringAlgorithmSpecificProperties(List<String> lines) throws AvatolCVException {
        for (String line : lines){
            if (line.startsWith("#") || line.equals("")){
                // ignore
            }
            else {
                String[] parts = line.split("=");
                String key = parts[0];
                String val = "";
                if (parts.length > 1){
                	val = parts[1];
                } 
                if (key.equals(PROPERTY_SCORING_FOCUS)){
                    setFocusValue(val);
                }
                else if (key.equals(PROPERTY_SCORING_SCOPE)){
                    setScopeValue(val);
                }
                else if (key.equalsIgnoreCase(PROPERTY_INCLUDE_POINT_ANNOTATIONS_IN_SCORING_FILE)){
                	if (val.equals("true")){
                		includePointAnnotationsInScoringFile = true;
                	}
                }
                else {
                    // ignore the rest at this level
                }
            }
        }
    }
    public boolean shouldIncludePointAnnotationsInScoringFile(){
    	return includePointAnnotationsInScoringFile;
    }
    public boolean hasFocus(ScoringSessionFocus focus){
        if (focus == thisAlgorithmFocus){
            return true;
        }
        return false;
    }
    
    /*
     * SESSION LEVEL SCOPE AND FOCUS
     */
    /*
    public static ScoringSessionFocus getFocusChosenForSession(){
        return focusChosenForSession;
    }
    public static void setFocusChosenForSession(ScoringSessionFocus focus){
        focusChosenForSession = focus;
    }
    public static ScoringScope getScopeChosenForSession(){
        return scopeChosenForSession;
    }
    public static void setScopeChosenForSession(ScoringScope scope){
        scopeChosenForSession = scope;
    }
    */
    
    public static ScoringScope getScopeForScopePropertiesValue(String val){
        if (ScoringScope.SINGLE_ITEM.name().equals(val)){
            return ScoringScope.SINGLE_ITEM;
        }
        else {
            return ScoringScope.MULTIPLE_ITEM;
        }
    }
    public static String getRadioButtonTextForScoringFocus(ScoringSessionFocus focus){
        return radioButtonTextForFocusHash.get(focus);
    }
    
    /*
     * THIS INSTANCE'S SCOPE AND FOCUS
     */
    private void setScoringFocus(ScoringSessionFocus focus){
        thisAlgorithmFocus = focus;
    }
    public ScoringSessionFocus getScoringFocus(){
        return thisAlgorithmFocus;
    }
    private void setScoringScope(ScoringScope scope){
        thisAlgorithmScope = scope;
    }
    public ScoringScope getScoringScope(){
        return thisAlgorithmScope;
    }
    private void setFocusValue(String val) throws AvatolCVException {
        ScoringSessionFocus focus = getFocusEnumValueForString(val, this.getAlgName());
        setScoringFocus(focus);
    }
    public static ScoringSessionFocus getFocusEnumValueForString(String s, String name) throws AvatolCVException {
        ScoringSessionFocus focus = null;
        try {
            focus = ScoringSessionFocus.valueOf(s);
        }
        catch(Exception e){
            throw new AvatolCVException("invalid scoringFocus value for scoring algorithm " + name + ": " + s);
        }
        return focus;
    }
    
    
    private void setScopeValue(String val) throws AvatolCVException {
        ScoringScope scope = getScopeEnumValueForString(val, this.getAlgName());
        setScoringScope(scope);
    }
    public static ScoringScope getScopeEnumValueForString(String s, String name) throws AvatolCVException {
        ScoringScope scope = null;
        try {
            scope = ScoringScope.valueOf(s);
        }
        catch(Exception e){
            throw new AvatolCVException("invalid scoringScope value for scoring algorithm " + name + ": " + s);
        }
        return scope;
    }
}
