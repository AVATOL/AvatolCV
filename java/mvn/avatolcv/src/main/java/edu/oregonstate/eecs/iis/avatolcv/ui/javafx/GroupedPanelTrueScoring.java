package edu.oregonstate.eecs.iis.avatolcv.ui.javafx;

import java.util.List;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import edu.oregonstate.eecs.iis.avatolcv.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.javafxui.AvatolCVExceptionExpresserJavaFX;
import edu.oregonstate.eecs.iis.avatolcv.normalized.NormalizedKey;
import edu.oregonstate.eecs.iis.avatolcv.normalized.NormalizedValue;
import edu.oregonstate.eecs.iis.avatolcv.scoring.KeySorterEvaluation;
import edu.oregonstate.eecs.iis.avatolcv.scoring.KeySorterTrueScoring;
import edu.oregonstate.eecs.iis.avatolcv.scoring.ScoringSet;
import edu.oregonstate.eecs.iis.avatolcv.scoring.TrueScoringSet;
import edu.oregonstate.eecs.iis.avatolcv.ui.javafx.GroupedPanelEvaluation.NumbersUpdater;
import edu.oregonstate.eecs.iis.avatolcv.ui.javafx.GroupedPanelEvaluation.RadioChangeListener;
/*
 * For eval mode, user is in control of what to select for train and test
 * For true scoring more, we just show what is already labeled as training data and the remainder as test
 * True scoring cases:
 *     - show by property group (i.e. by taxon)
 *     		==> TrueScoringDataSorter organizes by taxon but has a train list and test list for each, supports "make them all test" or "omit testing, just use training ones"
 *     		- well behaved in that all taxon x either training or test
 *     			show usual group view with selections disabled as per TrueScoringDataSorter
 *     		- sloppy in that taxon x has some training and some test
 *              ==> show two entries for the taxon, one with train chosen, one with test chosen,
 *     			trainTestConcern required by alg
 *     				just show the score ones and a note that training is being ignored 
 *     			trainTestConcern not required by alg
 *     				show both the score and train ones on different lines
 *     				
 *     - show by image
 * 
 */
public class GroupedPanelTrueScoring extends VBox {
	private List<ScoringSet> sets = null;
	private String trainTestConcern = null;
	private NormalizedKey trainTestConcernKey = null;
	private List<NormalizedValue> trainTestValues = null;
	private KeySorterTrueScoring keySorter = null;
	private NumbersUpdater numbersUpdater = null;
	private boolean trainTestSplitRequiredByAlgorithm = false;
	public GroupedPanelTrueScoring(List<ScoringSet> sets, String trainTestConcern, NormalizedKey trainTestConcernKey, List<NormalizedValue> trainTestValues, boolean trainTestSplitRequiredByAlgorithm) throws AvatolCVException {
		this.sets = sets;
		this.trainTestSplitRequiredByAlgorithm = true;
		this.trainTestConcern = trainTestConcern;
		this.trainTestConcernKey = trainTestConcernKey;
		this.trainTestValues = trainTestValues;
		if (null == keySorter){
        	 keySorter = new KeySorterTrueScoring(sets,trainTestConcernKey);
        }
		HBox hbox = createStatusPanel();
		hbox.setPrefHeight(USE_COMPUTED_SIZE);
		ScrollPane mainPane = createTrainTestPanel();
		mainPane.setPrefHeight(USE_COMPUTED_SIZE);
		this.getChildren().add(mainPane);
		this.getChildren().add(hbox);
		this.setPrefHeight(USE_COMPUTED_SIZE);
	}
	public ScrollPane createTrainTestPanel() throws AvatolCVException {
		GridPane gp = new GridPane();
        gp.setHgap(17);
        gp.setVgap(4);
        
        
        Label ttConcernLabel = new Label(this.trainTestConcern);
        gp.add(ttConcernLabel, 2, 0);
        Label countTitleLabel = new Label("image count");
        gp.add(countTitleLabel, 3, 0);
        /*
        int column = 4;
        for (ScoringSet set : sets){
            String scName = set.getScoringConcernName();
            Label scNameLabel = new Label(scName);
            gp.add(countLabel, column++, 0);
        } 
        */
        int row = 1;
        for (NormalizedValue ttValue : this.trainTestValues){
            String ttValName = ttValue.getName();
            // put train/test radios in grid line
            boolean hasTraining = this.keySorter.hasTrainingImagesForKey(ttValue);
            boolean hasScoring = this.keySorter.hasScoringImagesForKey(ttValue);
            int trainingImageCount = this.keySorter.getTrainingImageCount(ttValue);
            int scoringImageCount = this.keySorter.getScoringImageCount(ttValue);
            boolean onlyShowScoring = false;
            if (hasTraining && hasScoring){
            	// if we are in a trainTestConcern regimen, only add the toScore items and add a note about wht training skipped
            	if (this.trainTestSplitRequiredByAlgorithm){
            		onlyShowScoring = true;
            	}
            }
            if (hasTraining){
            	if (!onlyShowScoring){
            		showTrainingLine(gp, row++, ttValName, trainingImageCount);
            	}
            }
            if (hasScoring){
            	if (onlyShowScoring){
            		showScoringLineWithTrainingExclusion(gp, row++, ttValName, scoringImageCount, trainingImageCount);
            	}
            	else {
            		showScoringLine(gp, row++, ttValName, scoringImageCount);
            	}
            }
        }
        ScrollPane sp = new ScrollPane();
        sp.setContent(gp);
        sp.setStyle("-fx-font-size: 20px;");  // set the font size to something big.
        gp.setStyle("-fx-font-size: 10px;");
        AnchorPane.setTopAnchor(sp, 0.0);
        AnchorPane.setLeftAnchor(sp, 0.0);
        AnchorPane.setRightAnchor(sp, 0.0);
        AnchorPane.setBottomAnchor(sp, 0.0);
        return sp;
	}
	private void showTrainingLine(GridPane gp, int row, String name, int countTraining){
		gp.add(new Label("train"), 0,row);
		gp.add(new Label(name), 2, row);
		gp.add(new Label("" + countTraining), 3, row);
	}
	private void showScoringLine(GridPane gp, int row, String name, int countScoring){
		gp.add(new Label("score"), 1,row);
		gp.add(new Label(name), 2, row);
		gp.add(new Label("" + countScoring), 3, row);
	}
	private void showScoringLineWithTrainingExclusion(GridPane gp, int row, String name, int countScoring, int countTraining){
		gp.add(new Label("score"), 1,row);
		gp.add(new Label(name), 2, row);
		gp.add(new Label("" + countScoring), 3, row);
		gp.add(new Label("* " + countTraining + " training examples ignored"), 4, row);
	}
	public HBox createStatusPanel(){
		HBox percentageExpressionHbox = new HBox();
		percentageExpressionHbox.getChildren().clear();
		Label totalImagesLabel =         new Label("Total images in play: ");
		Label totalImagesValueLabel =    new Label();
		Label percentToTrainLabel =      new Label("    % to train: ");
		Label percentToTainValueLabel =  new Label();
		Label percentToScoreLabel =      new Label("    % to score: ");
		Label percentToScoreValueLabel = new Label();
		Label ratioCountLabel =          new Label();
		
		percentageExpressionHbox.getChildren().add(totalImagesLabel);
		percentageExpressionHbox.getChildren().add(totalImagesValueLabel);
		percentageExpressionHbox.getChildren().add(percentToTrainLabel);
		percentageExpressionHbox.getChildren().add(percentToTainValueLabel);
		percentageExpressionHbox.getChildren().add(percentToScoreLabel);
		percentageExpressionHbox.getChildren().add(percentToScoreValueLabel);
		percentageExpressionHbox.getChildren().add(ratioCountLabel);
		return percentageExpressionHbox;
	}
}
