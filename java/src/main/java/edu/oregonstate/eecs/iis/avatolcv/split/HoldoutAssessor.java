package edu.oregonstate.eecs.iis.avatolcv.split;

import java.util.ArrayList;
import java.util.List;

import edu.oregonstate.eecs.iis.avatolcv.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.mb.*;
import edu.oregonstate.eecs.iis.avatolcv.mb.Character;
public class HoldoutAssessor {
	private static final String NL = System.getProperty("file.separator");
	private int count = 0;
	private double threshold;
	private MorphobankSDDFile sdd = null;
	private List<MatrixCellImageUnit> unscoredUnits = new ArrayList<MatrixCellImageUnit>();
	private List<MatrixCellImageUnit> scoredAbsentUnitsWithAnnotations = new ArrayList<MatrixCellImageUnit>();
	private List<MatrixCellImageUnit> scoredAbsentUnitsWithoutAnnotations = new ArrayList<MatrixCellImageUnit>();
	private List<MatrixCellImageUnit> scoredPresentUnitsWithAnnotations = new ArrayList<MatrixCellImageUnit>();

	private List<MatrixCellImageUnit> trainingUnits = new ArrayList<MatrixCellImageUnit>();
	private List<MatrixCellImageUnit> holdoutUnits = new ArrayList<MatrixCellImageUnit>();
	private PartitionRegister partitionRegister = null;
    public HoldoutAssessor(String rootDir, List<MatrixCellImageUnit> units, MorphobankSDDFile sdd, double threshold, PartitionRegister pr) throws AvatolCVException {
    	if (units.size() == 0){
    		throw new AvatolCVException("zero length units list - cannot compute holdout");
    	}
    	
    	this.partitionRegister = pr;
    	this.threshold = threshold;
    	this.sdd = sdd;
    	this.count = units.size();
    	String holdoutCountextString = getHoldoutContextString(units.get(0));
    	pr.addComment("assess holdout " + holdoutCountextString);
    	for (MatrixCellImageUnit unit : units){
    		if (unit.isUnscored()){
    			this.unscoredUnits.add(unit);
    		}
    		else {
    			if (unit.hasWorkableScore()){
        			String charId = unit.getCharId();
            		String stateId = unit.getStateId();
            		Character character = sdd.getCharacterForId(charId);
            		boolean isAbsent = character.isStateIdRepresentingAbsent(stateId);
        			if (isAbsent){
        				if (unit.hasAnnotationFile(rootDir + Annotations.ANNOTATIONS_DIR)){
            				this.scoredAbsentUnitsWithAnnotations.add(unit);
        				}
        				else {
            				this.scoredAbsentUnitsWithoutAnnotations.add(unit);
        				}
        			}
        			else {
        				if (unit.hasAnnotationFile(rootDir + Annotations.ANNOTATIONS_DIR)){
        					this.scoredPresentUnitsWithAnnotations.add(unit);
        				}
        			}
        		}
    		}
    	}
    	int trainingSetGoalSize = (int)(this.count * this.threshold);
    	int positiveCount = 0;
		int negativeWithAnnotationCount = 0;
		int negativeWithoutAnnotationCount = 0;

    	int countHoldoutPositive = 0;
    	int countHoldoutNegWithAnn = 0;
    	int countHoldoutNegWithoutAnn = 0;
    	while (trainingUnits.size() <= trainingSetGoalSize  && isRemainingTrainingExamples()){
    		// pull one of each present and absent, favoring annotated absent ones
    		// BUT, don't let any media that has been used for training be used for scoring and vice versa
    		int remainingScoredPresent = this.scoredPresentUnitsWithAnnotations.size();
    		int remainingScoredAbsentUnitsWithAnnotations = this.scoredAbsentUnitsWithAnnotations.size();
    		int remainingScoredAbsentUnitsWithoutAnnotations = this.scoredAbsentUnitsWithoutAnnotations.size();
    		
    		if (remainingScoredPresent > 0){
    			boolean searchingForNextScoredPresent = true;
    			while (searchingForNextScoredPresent && remainingScoredPresent > 0){
    				MatrixCellImageUnit trainingExamplePositive = this.scoredPresentUnitsWithAnnotations.remove(remainingScoredPresent - 1);
        			remainingScoredPresent = this.scoredPresentUnitsWithAnnotations.size();
        			if (pr.isLegalTrainingUnit(trainingExamplePositive)){
            			trainingUnits.add(trainingExamplePositive);
            			pr.registerTrainingUnit(trainingExamplePositive);
            			positiveCount += 1;
            			searchingForNextScoredPresent = false;
        			}
        			else {
        				pr.addComment("unit already used for scoring - make for scoring here: " + trainingExamplePositive + " present with annotations");
        				holdoutUnits.add(trainingExamplePositive);
            			pr.registerToScoreUnit(trainingExamplePositive);
            			countHoldoutPositive += 1;
        			}
    			}
    			
    		}
        	if (remainingScoredAbsentUnitsWithAnnotations > 0){
    			boolean searchingForNextScoredAbsentWithAnn = true;
    			while (searchingForNextScoredAbsentWithAnn && remainingScoredAbsentUnitsWithAnnotations > 0){
    				MatrixCellImageUnit favoredTrainingExampleNegative = this.scoredAbsentUnitsWithAnnotations.remove(remainingScoredAbsentUnitsWithAnnotations - 1);
    				remainingScoredAbsentUnitsWithAnnotations = this.scoredAbsentUnitsWithAnnotations.size();
    				if (pr.isLegalTrainingUnit(favoredTrainingExampleNegative)){
    					trainingUnits.add(favoredTrainingExampleNegative);
    					pr.registerTrainingUnit(favoredTrainingExampleNegative);
    	        		negativeWithAnnotationCount += 1;
    	        		searchingForNextScoredAbsentWithAnn = false;
    				}
    				else {
    					pr.addComment("unit already used for scoring - make for scoring here: " + favoredTrainingExampleNegative + " absent with annotations");
    					holdoutUnits.add(favoredTrainingExampleNegative);
    	    			pr.registerToScoreUnit(favoredTrainingExampleNegative);
    	    			countHoldoutNegWithAnn += 1;
    				}
    			}
        		
        		
        	}
        	else if (remainingScoredAbsentUnitsWithoutAnnotations > 0){
    			boolean searchingForNextScoredAbsentWithoutAnn = true;
    			while (searchingForNextScoredAbsentWithoutAnn && remainingScoredAbsentUnitsWithoutAnnotations > 0){
            		MatrixCellImageUnit adequateTrainingExampleNegative = this.scoredAbsentUnitsWithoutAnnotations.remove(remainingScoredAbsentUnitsWithoutAnnotations - 1);
            		remainingScoredAbsentUnitsWithoutAnnotations = this.scoredAbsentUnitsWithoutAnnotations.size();
            		if (pr.isLegalTrainingUnit(adequateTrainingExampleNegative)){
            			trainingUnits.add(adequateTrainingExampleNegative);
            			pr.registerTrainingUnit(adequateTrainingExampleNegative);
                		negativeWithoutAnnotationCount += 1;
                		searchingForNextScoredAbsentWithoutAnn = false;
            		}
            		else {
            			pr.addComment("unit already used for scoring - make for scoring here: " + adequateTrainingExampleNegative + " absent without annotations");
            			holdoutUnits.add(adequateTrainingExampleNegative);
            			pr.registerToScoreUnit(adequateTrainingExampleNegative);
            			countHoldoutNegWithoutAnn += 1;
            		}
            		
    			}
        		
        	}
        	
    	}
    	for (MatrixCellImageUnit unit : this.scoredPresentUnitsWithAnnotations){
    		if (pr.isLegalToScoreUnit(unit)){
    			holdoutUnits.add(unit);
    			pr.registerToScoreUnit(unit);
    			countHoldoutPositive += 1;
    		}
    		else {
    			pr.addComment("unit already used for training - use for training here " + unit + " pos with annotations");
    			trainingUnits.add(unit);
    			pr.registerTrainingUnit(unit);
    			positiveCount += 1;
    		}
    	}

    	for (MatrixCellImageUnit unit : this.scoredAbsentUnitsWithAnnotations){
    		if (pr.isLegalToScoreUnit(unit)){
    			holdoutUnits.add(unit);
    			pr.registerToScoreUnit(unit);
    			countHoldoutNegWithAnn += 1;
    		}
    		else {
    			pr.addComment("unit already used for training - use for training here " + unit + " neg with annotations");
    			trainingUnits.add(unit);
				pr.registerTrainingUnit(unit);
        		negativeWithAnnotationCount += 1;
    		}
    	}

    	for (MatrixCellImageUnit unit : this.scoredAbsentUnitsWithoutAnnotations){
    		if (pr.isLegalToScoreUnit(unit)){
    			holdoutUnits.add(unit);
    			pr.registerToScoreUnit(unit);
    			countHoldoutNegWithoutAnn += 1;
    		}
    		else {
    			pr.addComment("unit already used for training - use for training here " + unit + " neg without annotations");
    			trainingUnits.add(unit);
    			pr.registerTrainingUnit(unit);
        		negativeWithoutAnnotationCount += 1;
    		}
    	}
    	pr.addComment(NL + "HOLDOUT SET taxon " + units.get(0).getTaxonId() + " char " + units.get(0).getCharId());
    	pr.addComment("count      " + this.count);
    	pr.addComment("pos        " + positiveCount);
    	pr.addComment("negWithAnn " + negativeWithAnnotationCount);
    	pr.addComment("negSansAnn " + negativeWithoutAnnotationCount);
    	pr.addComment("holdout pos        " + countHoldoutPositive);
    	pr.addComment("holdout negWithAnn " + countHoldoutNegWithAnn);
    	pr.addComment("holdout negSansAnn " + countHoldoutNegWithoutAnn);
    	pr.addComment("totalTraining " + trainingUnits.size());
    	pr.addComment("totalHoldout " + holdoutUnits.size());
    	
    }
    public String getHoldoutContextString(MatrixCellImageUnit unit) throws AvatolCVException {
    	String charId = unit.getCharId();
    	String charName = this.sdd.getCharacterNameForId(charId);
    	String taxonId = unit.getTaxonId();
    	String taxonName = this.sdd.getTaxonNameForId(taxonId);
    	String viewId = unit.getViewId();
    	String viewName = this.sdd.getViewNameForId(viewId);
    	return charName + ", " + taxonName + ", " + viewName;
    }
    public boolean isRemainingTrainingExamples(){
    	int remainingScoredPresent = this.scoredPresentUnitsWithAnnotations.size();
		int remainingScoredAbsentUnitsWithAnnotations = this.scoredAbsentUnitsWithAnnotations.size();
		int remainingScoredAbsentUnitsWithoutAnnotations = this.scoredAbsentUnitsWithoutAnnotations.size();
		if ((remainingScoredPresent > 0) || (remainingScoredAbsentUnitsWithAnnotations > 0) || (remainingScoredAbsentUnitsWithoutAnnotations > 0)){
			return true;
		}
		return false;
    }
    public List<MatrixCellImageUnit> getTrainingUnits(){
    	List<MatrixCellImageUnit> result = new ArrayList<MatrixCellImageUnit>();
    	result.addAll(this.trainingUnits);
    	return result;
    }
    public List<MatrixCellImageUnit> getToScoreUnits(){
    	List<MatrixCellImageUnit> result = new ArrayList<MatrixCellImageUnit>();
    	result.addAll(this.holdoutUnits);
    	return result;
    }
}
