package edu.oregonstate.eecs.iis.avatolcv.ui.javafx;

import java.util.List;

import edu.oregonstate.eecs.iis.avatolcv.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.javafxui.AvatolCVExceptionExpresserJavaFX;
import edu.oregonstate.eecs.iis.avatolcv.normalized.NormalizedKey;
import edu.oregonstate.eecs.iis.avatolcv.normalized.NormalizedValue;
import edu.oregonstate.eecs.iis.avatolcv.scoring.EvaluationSet;
import edu.oregonstate.eecs.iis.avatolcv.scoring.ScoringSet;
import edu.oregonstate.eecs.iis.avatolcv.scoring.KeySorterEvaluation;
import edu.oregonstate.eecs.iis.avatolcv.scoring.TrueScoringSet;
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
 *     				highlight to the user that they need to choose to either : make them all test, or just use the training samples and omit testing the ones that aren't labeled
 *     				 below the two entries, add a choice box for the two options 
 *     			trainTestConcern not required by alg
 *     				no choice box needed
 *     				
 *     - show by image
 * 
 */
public class GroupedPanelEvaluation extends VBox {
	public enum RadioType {
		Train,
		Test,
		Ignore
	};
	private List<ScoringSet> sets = null;
	private String trainTestConcern = null;
	private NormalizedKey trainTestConcernKey = null;
	private List<NormalizedValue> trainTestValues = null;
	private KeySorterEvaluation keySorter = null;
	private NumbersUpdater numbersUpdater = null;
	public GroupedPanelEvaluation(List<ScoringSet> sets, String trainTestConcern, NormalizedKey trainTestConcernKey, List<NormalizedValue> trainTestValues) throws AvatolCVException {
		this.sets = sets;
		this.trainTestConcern = trainTestConcern;
		this.trainTestConcernKey = trainTestConcernKey;
		this.trainTestValues = trainTestValues;
		if (null == keySorter){
        	 keySorter = new KeySorterEvaluation(sets,trainTestConcernKey);
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
        gp.add(ttConcernLabel, 3, 0);
        Label countTitleLabel = new Label("image count");
        gp.add(countTitleLabel, 4, 0);
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
            RadioButton radioTrain = new RadioButton("train");
            RadioButton radioScore = new RadioButton("score");
            RadioButton radioIgnore = new RadioButton("ignore");
            ToggleGroup tg = new ToggleGroup();
            radioIgnore.setToggleGroup(tg);
            radioTrain.setToggleGroup(tg);
            radioTrain.setStyle("styleClass:indentedFirstColumn");
            radioScore.setToggleGroup(tg);
            radioTrain.setSelected(true);
            radioTrain.selectedProperty().addListener(new RadioChangeListener(ttValue, keySorter, RadioType.Train, numbersUpdater));
            radioScore.selectedProperty().addListener(new RadioChangeListener(ttValue, keySorter, RadioType.Test, numbersUpdater));
            radioIgnore.selectedProperty().addListener(new RadioChangeListener(ttValue, keySorter, RadioType.Ignore, numbersUpdater));
            int column = 0;
            gp.add(radioTrain, column++,row);
            gp.add(radioScore, column++,row);
            gp.add(radioIgnore, column++, row);
            
            
            // put (ex.) taxon name in grid line
            Label ttValNameLabel = new Label(ttValName);
            gp.add(ttValNameLabel, column++, row);
            // put total count for that taxa
            Label countLabel = new Label();
            if (keySorter.isValueToTrain(ttValue)){
                radioTrain.setSelected(true);
                countLabel.setText("" + keySorter.getCountForValue(ttValue));
            }
            else {
                radioScore.setSelected(true);
                countLabel.setText("" + keySorter.getCountForValue(ttValue));
            }
            gp.add(countLabel, column++, row);
            row++;
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
		Label countToIgnoreLabel =      new Label("    # to ignore: "); 
		Label ignoreCountValueLabel =         new Label();
		
		percentageExpressionHbox.getChildren().add(totalImagesLabel);
		percentageExpressionHbox.getChildren().add(totalImagesValueLabel);
		percentageExpressionHbox.getChildren().add(countToIgnoreLabel);
		percentageExpressionHbox.getChildren().add(ignoreCountValueLabel);
		percentageExpressionHbox.getChildren().add(percentToTrainLabel);
		percentageExpressionHbox.getChildren().add(percentToTainValueLabel);
		percentageExpressionHbox.getChildren().add(percentToScoreLabel);
		percentageExpressionHbox.getChildren().add(percentToScoreValueLabel);
		percentageExpressionHbox.getChildren().add(ratioCountLabel);
		this.numbersUpdater = new NumbersUpdater(keySorter, totalImagesValueLabel, percentToTainValueLabel, percentToScoreValueLabel,ratioCountLabel, ignoreCountValueLabel);
		return percentageExpressionHbox;
	}
	
	public class NumbersUpdater {
		private Label totalImages;
		private Label percentToTrain;
		private Label percentToScore;
		private Label ratioCount;
		private Label ignoreCount;
		private KeySorterEvaluation kse;
		public NumbersUpdater(KeySorterEvaluation kse, Label totalImages, Label percentToTrain, Label percentToScore, Label ratioCount, Label ignoreCount){
			this.totalImages = totalImages;
			this.percentToTrain = percentToTrain;
			this.percentToScore = percentToScore;
			this.ratioCount = ratioCount;
			this.ignoreCount = ignoreCount;
			this.kse = kse;
			update();
		}
		public void update(){ 
			double imageCount = kse.getTotalScoringCount() + kse.getTotalTrainingCount();
			totalImages.setText("" + (int)imageCount);
			double pTrain = 100 * kse.getTotalTrainingCount() / imageCount;
			String pTrainString = String.format( "%.2f", pTrain );
			percentToTrain.setText("" + pTrainString);
			double pScore = 100 * kse.getTotalScoringCount() / imageCount;
			String pScoreString = String.format( "%.2f", pScore );
			percentToScore.setText("" + pScoreString);
			ratioCount.setText("    (" + kse.getTotalTrainingCount() + " vs " + kse.getTotalScoringCount() + ")");
			int numberIgnored = kse.getTotalIgnoreCount();
			ignoreCount.setText("" + numberIgnored);
		}
		
	}
	public class RadioChangeListener implements ChangeListener<Boolean> {
		
	    private NormalizedValue nv = null;
	    private KeySorterEvaluation ssks = null;
	    private RadioType radioType = RadioType.Ignore;
	    private NumbersUpdater nu = null;
	    public RadioChangeListener(NormalizedValue nv, KeySorterEvaluation ssks, RadioType radioType, NumbersUpdater nu){
	        this.nv = nv;
	        this.ssks = ssks;
	        this.radioType = radioType;
	        this.nu = nu;
	    }
        @Override
        public void changed(ObservableValue<? extends Boolean> obs,
                Boolean wasPreviouslySelected, Boolean isNowSelected) {
            if (isNowSelected){
                try {
                    if (radioType == RadioType.Train){
                        ssks.setValueToTrain(this.nv);
                        nu.update();
                    }
                    else if (radioType == RadioType.Test) {
                        ssks.setValueToScore(this.nv);
                        nu.update();
                    }
                    else {
                    	ssks.setValueToIgnore(this.nv);
                    	nu.update();
                    }
                }
                catch(AvatolCVException ace){
                    AvatolCVExceptionExpresserJavaFX.instance.showException(ace, "problem trying to adjust train/score selections " + ace.getMessage());
                }
            }
        }
	}
}
