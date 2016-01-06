package edu.oregonstate.eecs.iis.avatolcv.ui.javafx;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Accordion;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TitledPane;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import edu.oregonstate.eecs.iis.avatolcv.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.AvatolCVFileSystem;
import edu.oregonstate.eecs.iis.avatolcv.core.ImageInfo;
import edu.oregonstate.eecs.iis.avatolcv.core.ImageWithInfo;
import edu.oregonstate.eecs.iis.avatolcv.javafxui.AvatolCVExceptionExpresserJavaFX;
import edu.oregonstate.eecs.iis.avatolcv.normalized.NormalizedKey;
import edu.oregonstate.eecs.iis.avatolcv.normalized.NormalizedValue;
import edu.oregonstate.eecs.iis.avatolcv.scoring.EvaluationSet;
import edu.oregonstate.eecs.iis.avatolcv.scoring.ModalImageInfo;
import edu.oregonstate.eecs.iis.avatolcv.scoring.ScoringSet;
import edu.oregonstate.eecs.iis.avatolcv.scoring.ScoringSetsKeySorter;
import edu.oregonstate.eecs.iis.avatolcv.scoring.TrueScoringSet;
import edu.oregonstate.eecs.iis.avatolcv.session.ImagesForStep;
import edu.oregonstate.eecs.iis.avatolcv.session.SessionInfo;
import edu.oregonstate.eecs.iis.avatolcv.session.StepController;
import edu.oregonstate.eecs.iis.avatolcv.steps.ScoringConfigurationStep;

public class ScoringConfigurationStepController implements StepController {
	private ScoringConfigurationStep step = null;
    private String fxmlDocName = null;
    public RadioButton radioViewByImage = null;
    public RadioButton radioViewByGroup = null;
    public ChoiceBox<String> choiceBoxGroupProperty = null;
    public TextArea groupDescriptionTextArea = null;
    public AnchorPane trainTestSettingsAnchorPane = null;
    public HBox percentageExpressionHbox = null;
    private List<EvaluationSet> evaluationSets = null;
    private List<TrueScoringSet> trueScoringSets = null;
    private boolean activeSetIsEvaluation = true;
    private boolean sortByImage = true;
    private Hashtable<String, NormalizedKey> normalizedKeyHash = new Hashtable<String, NormalizedKey>();
    private ImagesForStep imageAccessor = null;
    private ScoringSetsKeySorter ssks = null;
    
    public ScoringConfigurationStepController(ScoringConfigurationStep step, String fxmlDocName) throws AvatolCVException {
        this.step = step;
        this.fxmlDocName = fxmlDocName;
    }
    public List<ScoringSet> getActiveScoringSets(){
    	List<ScoringSet> sets = new ArrayList<ScoringSet>();
    	if (this.activeSetIsEvaluation){
	    	for (EvaluationSet es : this.evaluationSets){
	    		sets.add(es);
	    	}
	    }
	    else {
	    	for (TrueScoringSet tss : this.trueScoringSets){
	    		sets.add(tss);
	    	}
	    }
    	return sets;
    }
    private boolean hasTrainingExamplesSelected(){
    	if (null != evaluationSets){
    		for (EvaluationSet es : evaluationSets){
    			if (es.getImagesToTrainOn().size() != 0){
    				return true;
    			}
    		}
    		return false;
    	}
    	else if (null != trueScoringSets){
    		for (TrueScoringSet tss : trueScoringSets){
    			if (tss.getImagesToTrainOn().size() != 0){
    				return true;
    			}
    		}
    		return false;
    	}
    	else {
    		return true;
    	}
    }
	@Override
	public boolean consumeUIData() {
		if (!hasTrainingExamplesSelected()){
			Platform.runLater(new NoTrainingDataAlert());
			return false;
		}
		else {
			if (SessionInfo.isBisqueSession()){
		        System.out.println("true scoring set for bisque");
		        consumeTrueScoringSets();
		    }
		    else {
		        System.out.println("evaluation set for morphobank");
		        consumeEvaluationSets();
		    }
		    
		    try {
	            this.step.consumeProvidedData();
	        }
	        catch(Exception e){
	            AvatolCVExceptionExpresserJavaFX.instance.showException(e, "problem trying to consume data at segmentation configuration");
	        }
	        return true;
		}
	    
	}

	public class NoTrainingDataAlert implements Runnable {
		public void run(){
			Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error Dialog");
            alert.setHeaderText("Scoring configuration problem");
            alert.setContentText("training examples need to be selected.");
            alert.showAndWait();
		}
	}
	private void consumeEvaluationSets(){
		List<ScoringSet> sSets = new ArrayList<ScoringSet>();
        sSets.addAll(this.evaluationSets);
        this.step.setScoringSets(sSets);
	}
	private void consumeTrueScoringSets(){
		List<ScoringSet> sSets = new ArrayList<ScoringSet>();
        sSets.addAll(this.trueScoringSets);
        this.step.setScoringSets(sSets);
	}
	@Override
	public void clearUIFields() {
		// TODO Auto-generated method stub
	}

	@Override
	public Node getContentNode() throws AvatolCVException {
        try {
            System.out.println("trying to load" +  this.fxmlDocName);
            FXMLLoader loader = new FXMLLoader(this.getClass().getResource(this.fxmlDocName));
            loader.setController(this);
            Node content = loader.load();
            //trainTestSettingsPane.setStyle("-fx-border-color: black;");
            this.step.reAssessImagesInPlay();
            this.evaluationSets = this.step.getEvaluationSets();
            
            try {
            	this.trueScoringSets = this.step.getTrueScoringSets();
            	
            }
            catch(AvatolCVException ace){
            	
            }
            String pathOfLargeImages = AvatolCVFileSystem.getNormalizedImagesLargeDir();
            String pathOfThumbnailImages = AvatolCVFileSystem.getNormalizedImagesThumbnailDir();
            this.imageAccessor = new ImagesForStep(pathOfLargeImages, pathOfThumbnailImages);
            populateSortingChoiceBox();
            configureAsSortByImage();
            NormalizedKey trainTestKey = this.step.getDefaultTrainTestConcern();
            this.step.setTrainTestConcern(trainTestKey);
            return content;
        }
        catch(IOException ioe){
            throw new AvatolCVException("problem loading ui " + fxmlDocName + " for controller " + this.getClass().getName());
        } 
    }
	private void populateSortingChoiceBox() throws AvatolCVException {
		List<NormalizedKey> sortCandidates = this.step.getScoringSortingCandidates();
		List<String> sortCandidateStrings = new ArrayList<String>();
		for (NormalizedKey nk : sortCandidates){
			String keyName = nk.getName();
			normalizedKeyHash.put(keyName, nk);
			sortCandidateStrings.add(keyName);
		}
		Collections.sort(sortCandidateStrings);
        this.choiceBoxGroupProperty.getItems().addAll(sortCandidateStrings);
        this.choiceBoxGroupProperty.setValue(this.choiceBoxGroupProperty.getItems().get(0));
        this.choiceBoxGroupProperty.getSelectionModel().selectedIndexProperty().addListener(new GroupChangeListener(this.choiceBoxGroupProperty, this.groupDescriptionTextArea, this.step));
	}
	public class GroupChangeListener implements ChangeListener<Number> {
        private ChoiceBox<String> cb;
        private TextArea ta;
        private ScoringConfigurationStep step;
        public GroupChangeListener(ChoiceBox<String> cb, TextArea ta, ScoringConfigurationStep step){
            this.ta = ta;
            this.cb = cb;
            this.step = step;
        }
        @Override
        public void changed(ObservableValue ov, Number value, Number newValue) {
            try {
                //System.out.println("new Value " + newValue);
                configureAsGroupByProperty();
            }
            catch(Exception e){
                AvatolCVExceptionExpresserJavaFX.instance.showException(e, "Problem changing grouping of training vs score ");
            }
        }
    }
	
	private void setSortSelector(ComboBox<String> choiceBox, RadioButton radioButton, ObservableList<String> oList, String key){
        choiceBox.setItems(oList);
        if (oList.size() > 0){
            if (this.step.hasPriorAnswers()){
                String paAlg = this.step.getPriorAnswers().get(key);
                if(null == paAlg){
                    // alg was not selected prior
                    choiceBox.setValue(oList.get(0));
                }
                else {
                    // alg was selected prior, use that value
                    choiceBox.setValue(paAlg);
                    radioButton.setSelected(true);
                }
            }
            else {
                choiceBox.setValue(oList.get(0));
            }
            choiceBox.requestLayout();
        }
    }
	
	public void configureAsSortByImage(){
		try {
			this.sortByImage = true;
			choiceBoxGroupProperty.setDisable(true);
			radioViewByImage.setSelected(true);
			List<ScoringSet> sets = getActiveScoringSets();
			if (sets.get(0) instanceof EvaluationSet){
			    renderByImage(sets, true);
			}
			else {
			    renderByImage(sets, false);
			}
			//configureAsSortByImage(sets);
		}
		catch(AvatolCVException ace){
			AvatolCVExceptionExpresserJavaFX.instance.showException(ace, "Problem setting 'sort by image' ");
		}
	}
	public void configureAsGroupByProperty() throws AvatolCVException {
		this.sortByImage = false;
		choiceBoxGroupProperty.setDisable(false);
		radioViewByGroup.setSelected(true);
		List<ScoringSet> sets = getActiveScoringSets();
		this.step.setTrainTestConcern(new NormalizedKey(choiceBoxGroupProperty.getValue()));
		if (sets.get(0) instanceof EvaluationSet){
            renderEvalByGroup(sets);
        }
        else {
            renderScoreByGroup(sets);
        }
		//configureAsGroupByProperty(sets);
	}
	

	public void renderByImage(List<ScoringSet> sets, boolean allowUserChanges) throws AvatolCVException {
	    trainTestSettingsAnchorPane.getChildren().clear();
        Accordion accordian = loadAccordionWithSetsByImage(sets, allowUserChanges);
        AnchorPane.setTopAnchor(accordian, 0.0);
        AnchorPane.setLeftAnchor(accordian, 0.0);
        AnchorPane.setRightAnchor(accordian, 0.0);
        AnchorPane.setBottomAnchor(accordian, 0.0);
        trainTestSettingsAnchorPane.getChildren().add(accordian);
	}
	
	public void renderEvalByGroup(List<ScoringSet> sets) throws AvatolCVException {
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
		
	    trainTestSettingsAnchorPane.getChildren().clear();
	    
	    List<EvaluationSet> esets = new ArrayList<EvaluationSet>();
	    for (ScoringSet set : sets){
	        esets.add((EvaluationSet)set);
	    }
	    String trainTestConcern = choiceBoxGroupProperty.getValue();
        NormalizedKey trainTestConcernKey = normalizedKeyHash.get(trainTestConcern);
        List<NormalizedValue> trainTestValues = this.step.getValuesForTrainTestConcern(trainTestConcernKey);
        if (null == ssks){
        	 ssks = new ScoringSetsKeySorter(esets,trainTestConcernKey);
        }
	   
	    
	    NumbersUpdater nu = new NumbersUpdater(ssks, totalImagesValueLabel, percentToTainValueLabel, percentToScoreValueLabel,ratioCountLabel);
	    GridPane gp = new GridPane();
        gp.setHgap(17);
        gp.setVgap(4);
        
        
        Label ttConcernLabel = new Label(trainTestConcern);
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
        for (NormalizedValue ttValue : trainTestValues){
            String ttValName = ttValue.getName();
            // put train/test radios in grid line
            RadioButton radioTrain = new RadioButton("train");
            RadioButton radioScore = new RadioButton("score");
            ToggleGroup tg = new ToggleGroup();
            radioTrain.setToggleGroup(tg);
            radioTrain.setStyle("styleClass:indentedFirstColumn");
            radioScore.setToggleGroup(tg);
            radioTrain.setSelected(true);
            radioTrain.selectedProperty().addListener(new RadioChangeListener(ttValue, ssks, true, nu));
            radioScore.selectedProperty().addListener(new RadioChangeListener(ttValue, ssks, false, nu));
            gp.add(radioTrain, 0,row);
            gp.add(radioScore, 1,row);
            
            
            // put (ex.) taxon name in grid line
            Label ttValNameLabel = new Label(ttValName);
            gp.add(ttValNameLabel, 2, row);
            // put total count for that taxa
            Label countLabel = new Label();
            if (ssks.isValueToTrain(ttValue)){
                radioTrain.setSelected(true);
                countLabel.setText("" + ssks.getCountForValue(ttValue));
            }
            else {
                radioScore.setSelected(true);
                countLabel.setText("" + ssks.getCountForValue(ttValue));
            }
            gp.add(countLabel, 3, row);
            // put count for each character <--- defer this
            
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
        trainTestSettingsAnchorPane.getChildren().add(sp);
	}
	
	public class NumbersUpdater {
		private Label totalImages;
		private Label percentToTrain;
		private Label percentToScore;
		private Label ratioCount;
		private ScoringSetsKeySorter ssks;
		public NumbersUpdater(ScoringSetsKeySorter ssks, Label totalImages, Label percentToTrain, Label percentToScore, Label ratioCount){
			this.totalImages = totalImages;
			this.percentToTrain = percentToTrain;
			this.percentToScore = percentToScore;
			this.ratioCount = ratioCount;
			this.ssks = ssks;
			update();
		}
		public void update(){
			double imageCount = ssks.getTotalScoringCount() + ssks.getTotalTrainingCount();
			totalImages.setText("" + (int)imageCount);
			double pTrain = 100 * ssks.getTotalTrainingCount() / imageCount;
			String pTrainString = String.format( "%.2f", pTrain );
			percentToTrain.setText("" + pTrainString);
			double pScore = 100 * ssks.getTotalScoringCount() / imageCount;
			String pScoreString = String.format( "%.2f", pScore );
			percentToScore.setText("" + pScoreString);
			ratioCount.setText("    (" + ssks.getTotalTrainingCount() + " vs " + ssks.getTotalScoringCount() + ")");
		}
		
	}
	public class RadioChangeListener implements ChangeListener<Boolean> {
	    private NormalizedValue nv = null;
	    private ScoringSetsKeySorter ssks = null;
	    private boolean isTraining = false;
	    private NumbersUpdater nu = null;
	    public RadioChangeListener(NormalizedValue nv, ScoringSetsKeySorter ssks, boolean isTraining, NumbersUpdater nu){
	        this.nv = nv;
	        this.ssks = ssks;
	        this.isTraining = isTraining;
	        this.nu = nu;
	    }
        @Override
        public void changed(ObservableValue<? extends Boolean> obs,
                Boolean wasPreviouslySelected, Boolean isNowSelected) {
            if (isNowSelected){
                try {
                    if (isTraining){
                        ssks.setValueToTrain(this.nv);
                        nu.update();
                    }
                    else {
                        ssks.setValueToScore(this.nv);
                        nu.update();
                    }
                }
                catch(AvatolCVException ace){
                    AvatolCVExceptionExpresserJavaFX.instance.showException(ace, "problem trying to adjust train/score selections " + ace.getMessage());
                }
            }
        }
	}
	public void renderScoreByGroup(List<ScoringSet> sets) throws AvatolCVException {
    
	}
	public Accordion loadAccordionWithSetsByImage(List<ScoringSet> sets, boolean allowUserChanges) throws AvatolCVException {
        Accordion accordion = new Accordion();
        for (ScoringSet ss : sets){
            TitledPane tp = new TitledPane();
            //tp.setAnimated(false);
            tp.setText(ss.getScoringConcernName());
            GridPane gp = loadGridPaneWithSetByImage(ss, allowUserChanges);
            ScrollPane sp = new ScrollPane();
            sp.setContent(gp);
            tp.setContent(sp);
            accordion.getPanes().add(tp);
        }
        accordion.setExpandedPane(accordion.getPanes().get(0));
        return accordion;
    }
	public GridPane loadGridPaneWithSetByImage(ScoringSet ss, boolean allowUserChanges) throws AvatolCVException { 
        GridPane gp = new GridPane();
        System.out.println("Laying out gp for " + ss.getScoringConcernName());
        gp.setHgap(4);
        gp.setVgap(4);
        //DropShadow dsTraining = new DropShadow( 20, Color.AQUA );
        //DropShadow dsScoring = new DropShadow( 20, Color.TOMATO );
        List<ModalImageInfo> trainingImages = ss.getImagesToTrainOn();
        List<ModalImageInfo> scoringImages = ss.getImagesToScore();
        int row = 0;
        int column = 0;
        for (ModalImageInfo mii : trainingImages){
            System.out.println("training " + mii.getNormalizedImageInfo().getNiiString());
            String imageId = mii.getNormalizedImageInfo().getImageID();
            ImageView iv = getImageViewForImageID(imageId);
            Label label = new Label();
            label.setGraphic(iv);
            label.setStyle("-fx-border-color: #80808FF;-fx-border-width:4px");
            gp.add(label, column++, row);
            if (column == 10){
                column = 0;
                row++;
            }
        }
        for (ModalImageInfo mii : scoringImages){
            System.out.println("test " + mii.getNormalizedImageInfo().getNiiString());
            String imageId = mii.getNormalizedImageInfo().getImageID();
            ImageView iv = getImageViewForImageID(imageId);
            Label label = new Label();
            label.setGraphic(iv);
            label.setStyle("-fx-border-color: #80FF80;-fx-border-width:4px");
            gp.add(label, column++, row);
            if (column == 10){
                column = 0;
                row++;
            }
        }
        return gp;
    }
	public ImageView getImageViewForImageID(String imageID) throws AvatolCVException {
        ImageInfo ii = this.imageAccessor.getThumbnailImageForID(imageID);
        ImageView iv = new ImageView();
        iv.setPreserveRatio(true);
        ImageWithInfo imageWithInfo = new ImageWithInfo("file:" + ii.getFilepath(), ii);
        iv.setImage(imageWithInfo);
        iv.setFitHeight(80);
        return iv;
    }
	@Override
	public boolean delayEnableNavButtons() {
		// NA
		return false;
	}

	@Override
	public void executeFollowUpDataLoadPhase() throws AvatolCVException {
		// NA
	}

	@Override
	public void configureUIForFollowUpDataLoadPhase() {
		// NA
	}

	@Override
	public boolean isFollowUpDataLoadPhaseComplete() {
		// NA
		return false;
	}
	
}
