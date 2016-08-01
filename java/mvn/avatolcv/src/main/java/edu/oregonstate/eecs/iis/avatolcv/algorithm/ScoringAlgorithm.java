package edu.oregonstate.eecs.iis.avatolcv.algorithm;

import java.util.Hashtable;
import java.util.List;

import edu.oregonstate.eecs.iis.avatolcv.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.util.ClassicSplitter;

public class ScoringAlgorithm extends Algorithm {
    public static final String PROPERTY_SCORING_FOCUS = "scoringFocus";
    public static final String PROPERTY_SCORING_SCOPE = "scoringScope";
    public static final String PROPERTY_TRAIN_TEST_CONCERN_REQUIRED = "trainTestConcernRequired";
    public static final String PROPERTY_CAN_TRAIN_ON_MULTIPLE_ANNOTATIONS_PER_IMAGE = "canTrainOnMultipleAnnotationsPerImage";
    private static final String PROPERTY_INCLUDE_POINT_ANNOTATIONS_IN_SCORING_FILE = "includePointAnnotationsInScoringFile";
    private static final String PROPERTY_REQUIRES_PRESENT_AND_ABSENT_TRAINING_EXAMPLES_FOR_CHARACTER = "requiresPresentAndAbsentTrainingExamplesForCharacter";
    public enum ScoringSessionFocus { 
        SPECIMEN_PART_PRESENCE_ABSENCE, 
        SPECIMEN_SHAPE_OR_TEXTURE_ASPECT};
    // DPM needs to score all presenceAbsence chars at same time, other might just want one char    
    public enum ScoringScope {
        SINGLE_ITEM,
        MULTIPLE_ITEM
    }
    private boolean includePointAnnotationsInScoringFile = false;
    private boolean requiresPresentAndAbsentTrainingExamplesForCharacter = false;
    private static Hashtable<ScoringSessionFocus, String> radioButtonTextForFocusHash = new Hashtable<ScoringSessionFocus, String>();
    
    static {
        radioButtonTextForFocusHash.put(ScoringSessionFocus.SPECIMEN_PART_PRESENCE_ABSENCE, "character = part");
        radioButtonTextForFocusHash.put(ScoringSessionFocus.SPECIMEN_SHAPE_OR_TEXTURE_ASPECT, "character = shape or texture");
    }

    private ScoringSessionFocus thisAlgorithmFocus = null;
    private ScoringScope thisAlgorithmScope = null;
    private boolean trainTestConcernRequired = false;
    private boolean canTrainOnMultipleAnnotationsPerImage = false;
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
                String[] parts = ClassicSplitter.splitt(line,'=');
                String key = parts[0];
                String val = "";
                if (parts.length > 1){
                	val = parts[1];
                } 
                if (key.equals(PROPERTY_SCORING_FOCUS)){
                    setFocusValue(val);
                }
                else if (key.equals(PROPERTY_REQUIRES_PRESENT_AND_ABSENT_TRAINING_EXAMPLES_FOR_CHARACTER)){
                    if ("true".equals(val)){
                        requiresPresentAndAbsentTrainingExamplesForCharacter = true;
                    }
                }

                else if (key.equals(PROPERTY_CAN_TRAIN_ON_MULTIPLE_ANNOTATIONS_PER_IMAGE)){
                    if ("true".equals(val)){
                        canTrainOnMultipleAnnotationsPerImage = true;
                    }
                }
                else if (key.equals(PROPERTY_SCORING_SCOPE)){
                    setScopeValue(val);
                }
                else if (key.equals(PROPERTY_TRAIN_TEST_CONCERN_REQUIRED)){
                	if ("true".equals(val)){
                		trainTestConcernRequired = true;
                	}
                	else {
                		trainTestConcernRequired = false;
                	}
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
    public boolean requiresPresentAndAbsentTrainingExamplesForCharacter(){
        return requiresPresentAndAbsentTrainingExamplesForCharacter;
    }
    public boolean canTrainOnMultipleAnnotationsPerImage(){
        return canTrainOnMultipleAnnotationsPerImage;
    }
    public boolean isTrainTestConcernRequired(){
    	return trainTestConcernRequired;
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
