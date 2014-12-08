package edu.oregonstate.eecs.iis.avatolcv;

import java.util.ArrayList;
import java.util.List;

import edu.oregonstate.eecs.iis.avatolcv.mb.*;
import edu.oregonstate.eecs.iis.avatolcv.mb.Character;
public class HoldoutAssessor {
	private static final String NL = System.getProperty("file.separator");
	private int count = 0;
	private double threshold;
	private MorphobankSDDFile sdd = null;
	List<MatrixCell> unscoredCells = new ArrayList<MatrixCell>();
	List<MatrixCell> scoredAbsentCellsWithAnnotations = new ArrayList<MatrixCell>();
	List<MatrixCell> scoredAbsentCellsWithoutAnnotations = new ArrayList<MatrixCell>();
	List<MatrixCell> scoredPresentCellsWithAnnotations = new ArrayList<MatrixCell>();

	List<MatrixCell> trainingCells = new ArrayList<MatrixCell>();
	List<MatrixCell> holdoutCells = new ArrayList<MatrixCell>();
    public HoldoutAssessor(String rootDir, List<MatrixCell> cells, MorphobankSDDFile sdd, double threshold) throws AvatolCVException {
    	this.threshold = threshold;
    	this.sdd = sdd;
    	this.count = cells.size();
    	for (MatrixCell cell : cells){
    		if (cell.isUnscored()){
    			this.unscoredCells.add(cell);
    		}
    		else {
    			if (cell.hasWorkableScore()){
        			String charId = cell.getCharId();
            		String stateId = cell.getState();
            		Character character = sdd.getCharacterForId(charId);
            		boolean isAbsent = character.isStateIdRepresentingAbsent(stateId);
        			if (isAbsent){
        				if (cell.hasAnnotationFile(rootDir + Annotations.ANNOTATIONS_DIR)){
            				this.scoredAbsentCellsWithAnnotations.add(cell);
        				}
        				else {
            				this.scoredAbsentCellsWithoutAnnotations.add(cell);
        				}
        			}
        			else {
        				if (cell.hasAnnotationFile(rootDir + Annotations.ANNOTATIONS_DIR)){
        					this.scoredPresentCellsWithAnnotations.add(cell);
        				}
        			}
        		}
    		}
    	}
    	int trainingSetGoalSize = (int)(this.count * this.threshold);
    	int positiveCount = 0;
		int negativeWithAnnotationCount = 0;
		int negativeWithoutAnnotationCount = 0;
    	while (trainingCells.size() <= trainingSetGoalSize  && isRemainingTrainingExamples()){
    		// pull one of each present and absent, favoring annotated absent ones
    		int remainingScoredPresent = this.scoredPresentCellsWithAnnotations.size();
    		int remainingScoredAbsentCellsWithAnnotations = this.scoredAbsentCellsWithAnnotations.size();
    		int remainingScoredAbsentCellsWithoutAnnotations = this.scoredAbsentCellsWithoutAnnotations.size();
    		
    		if (remainingScoredPresent > 0){
    			MatrixCell trainingExamplePositive = this.scoredPresentCellsWithAnnotations.remove(remainingScoredPresent - 1);
    			trainingCells.add(trainingExamplePositive);
    			positiveCount += 1;
    		}
        	if (remainingScoredAbsentCellsWithAnnotations > 0){
        		MatrixCell favoredTrainingExampleNegative = this.scoredAbsentCellsWithAnnotations.remove(remainingScoredAbsentCellsWithAnnotations - 1);
        		trainingCells.add(favoredTrainingExampleNegative);
        		negativeWithAnnotationCount += 1;
        	}
        	else if (remainingScoredAbsentCellsWithoutAnnotations > 0){
        		MatrixCell adequateTrainingExampleNegative = this.scoredAbsentCellsWithoutAnnotations.remove(remainingScoredAbsentCellsWithoutAnnotations - 1);
        		trainingCells.add(adequateTrainingExampleNegative);
        		negativeWithoutAnnotationCount += 1;
        	}
        	
    	}
    	System.out.println(NL + "HOLDOUT SET taxon " + cells.get(0).getTaxonId() + " char " + cells.get(0).getCharId());
    	System.out.println("count      " + this.count);
    	System.out.println("pos        " + positiveCount);
    	System.out.println("negWithAnn " + negativeWithAnnotationCount);
    	System.out.println("negSansAnn " + negativeWithoutAnnotationCount);
    	System.out.println("holdout pos        " + this.scoredPresentCellsWithAnnotations.size());
    	System.out.println("holdout negWithAnn " + this.scoredAbsentCellsWithAnnotations.size());
    	System.out.println("holdout negSansAnn " + this.scoredAbsentCellsWithoutAnnotations.size());
    	holdoutCells.addAll(this.scoredPresentCellsWithAnnotations);
    	holdoutCells.addAll(this.scoredAbsentCellsWithAnnotations);
    	holdoutCells.addAll(this.scoredAbsentCellsWithoutAnnotations);
    	System.out.println("totalTraining " + trainingCells.size());
    	System.out.println("totalHoldout " + holdoutCells.size());
    }
    public boolean isRemainingTrainingExamples(){
    	int remainingScoredPresent = this.scoredPresentCellsWithAnnotations.size();
		int remainingScoredAbsentCellsWithAnnotations = this.scoredAbsentCellsWithAnnotations.size();
		int remainingScoredAbsentCellsWithoutAnnotations = this.scoredAbsentCellsWithoutAnnotations.size();
		if ((remainingScoredPresent > 0) || (remainingScoredAbsentCellsWithAnnotations > 0) || (remainingScoredAbsentCellsWithoutAnnotations > 0)){
			return true;
		}
		return false;
    }
    public List<MatrixCell> getTrainingCells(){
    	List<MatrixCell> result = new ArrayList<MatrixCell>();
    	result.addAll(this.trainingCells);
    	return result;
    }
    public List<MatrixCell> getToScoreCells(){
    	List<MatrixCell> result = new ArrayList<MatrixCell>();
    	result.addAll(this.holdoutCells);
    	return result;
    }
}
//FIXME - change isScored to factor in new info from Seth