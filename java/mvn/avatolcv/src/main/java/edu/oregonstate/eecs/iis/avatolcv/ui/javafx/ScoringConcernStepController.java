package edu.oregonstate.eecs.iis.avatolcv.ui.javafx;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import edu.oregonstate.eecs.iis.avatolcv.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.algorithm.ScoringAlgorithm;
import edu.oregonstate.eecs.iis.avatolcv.datasource.ChoiceItem;
import edu.oregonstate.eecs.iis.avatolcv.javafxui.AvatolCVExceptionExpresserJavaFX;
import edu.oregonstate.eecs.iis.avatolcv.session.ProgressPresenter;
import edu.oregonstate.eecs.iis.avatolcv.session.SessionInfo;
import edu.oregonstate.eecs.iis.avatolcv.session.StepController;
import edu.oregonstate.eecs.iis.avatolcv.steps.ScoringConcernStep;

public class ScoringConcernStepController implements StepController {
    public static final String METADATA_DOWNLOAD = "metadataDownload"; 
    private static final String KEY_SCORING_CONCERN = "scoringConcern";
    private static final String VALUE_SCORING_CONCERN_SELECTED = "selected";
    private static final String VALUE_SCORING_CONCERN_UNSELECTED = "unselected";
    //private static final String ANSWER_COUNT = "answerCount";
    //private static final String SINGLE_ANSWER = "singleAnswer";
    //private static final String MULTIPLE_ANSWER = "multipleAnswer";
    public VBox scoringConcernVBox;
    public Label remainingMetadataDownloadLabel;
    public ProgressBar remainingMetadataDownloadProgressBar;
    public Label comboBoxInstructionlabel;
    public ComboBox<String> itemChoiceComboBox;
    private ScoringConcernStep step;
    private String fxmlDocName;
    //private List<String> charNames = null;
    private Hashtable<ChoiceItem, CheckBox> checkBoxForChoiceItemHash;
    private Hashtable<String, ChoiceItem> choiceItemForNameHash;
    private boolean dataDownloadPhaseComplete = false;
    //private List<MBCharacter> characters;
    private List<ChoiceItem> allChoiceItems = null;
    private List<CheckBox> allCheckboxes = new ArrayList<CheckBox>();
    public ScoringConcernStepController(ScoringConcernStep step, String fxmlDocName){
        this.step = step;
        this.fxmlDocName = fxmlDocName;
    }
    @Override
    public boolean consumeUIData() {
        try {
        	Hashtable<String, String> answerHash = new Hashtable<String, String>();
        	if (this.step.getScoringScope() == ScoringAlgorithm.ScoringScope.MULTIPLE_ITEM){
        		List<ChoiceItem> chosenItems = new ArrayList<ChoiceItem>();
        		//answerHash.put(ANSWER_COUNT, MULTIPLE_ANSWER);
        		for (ChoiceItem ci : this.allChoiceItems){
        			if (checkBoxForChoiceItemHash.get(ci).isSelected()){
        				chosenItems.add(ci);
        				answerHash.put(ci.getNormalizedKey().toString(), VALUE_SCORING_CONCERN_SELECTED);
        			}
        			else {
        				answerHash.put(ci.getNormalizedKey().toString(), VALUE_SCORING_CONCERN_UNSELECTED);
        			}
        		}
    			this.step.saveAnswers(answerHash);
        		this.step.setChosenItems(chosenItems);
        		this.step.consumeProvidedData();
        		return true;
        	}
        	else {
        		//answerHash.put(ANSWER_COUNT, SINGLE_ANSWER);
        	    String chosenAnswer = (String)this.itemChoiceComboBox.getValue();
        	    ChoiceItem chosenChoiceItem = choiceItemForNameHash.get(chosenAnswer);
        	    answerHash.put(KEY_SCORING_CONCERN, chosenChoiceItem.getNormalizedKey().getName());
    			this.step.saveAnswers(answerHash);
        		this.step.setChosenChoiceItem(chosenChoiceItem);
                this.step.consumeProvidedData();
                return true;
        	}
            
        }
        catch (Exception e){
            AvatolCVExceptionExpresserJavaFX.instance.showException(e, "problem specifying scoring concern");
            return false;
        }
    }

    @Override
    public void clearUIFields() {
        itemChoiceComboBox.setValue(this.allChoiceItems.get(0).getNormalizedKey().getName());
    }
    public Node getContentNodeForSingleItem() throws AvatolCVException {
    	try {
    		FXMLLoader loader = new FXMLLoader(this.getClass().getResource(this.fxmlDocName));
            loader.setController(this);
            Node content = loader.load();
            String instructions = this.step.getInstructionsForScoringConcernScreen();
            comboBoxInstructionlabel.setText(instructions);
            choiceItemForNameHash = new Hashtable<String, ChoiceItem>();
            ObservableList<String> list = itemChoiceComboBox.getItems();
            for (ChoiceItem ci : this.allChoiceItems){
                list.add(ci.getNormalizedKey().getName());
                choiceItemForNameHash.put(ci.getNormalizedKey().getName(), ci);
            }
            if (this.step.hasPriorAnswers()){
            	dataDownloadPhaseComplete = false;
            	Hashtable<String, String> answerHash = this.step.getPriorAnswers();
            	//if (answerHash.get(ANSWER_COUNT).equals(SINGLE_ANSWER)){
            		String priorAnswer = this.step.getPriorAnswers().get(KEY_SCORING_CONCERN);
                    itemChoiceComboBox.setValue(priorAnswer);
            	//}
            }
            else {
                itemChoiceComboBox.setValue(this.allChoiceItems.get(0).getNormalizedKey().getName());
            }
            return content;
    	}
    	catch(IOException ioe){
    		throw new AvatolCVException("problem loading ui " + fxmlDocName + " for controller " + this.getClass().getName() + " single character scope");
    	}
    }
    public Node getContentNodeForMultipleItem() throws AvatolCVException {
    	allCheckboxes.clear();
        checkBoxForChoiceItemHash = new Hashtable<ChoiceItem, CheckBox>();
    	try {
    		FXMLLoader loader = new FXMLLoader(this.getClass().getResource(this.fxmlDocName));
            loader.setController(this);
            Node content = loader.load();
            VBox vb = (VBox)content.lookup("#scoringConcernVBox");
            // clean it out
            vb.getChildren().clear();
            Region regionTop = new Region();
            VBox.setVgrow(regionTop, Priority.ALWAYS);
            vb.getChildren().add(regionTop);
            // add text about doing best to detect presence/absence
            String instructions = this.step.getInstructionsForScoringConcernScreen();
            Label header = new Label(instructions);
            //header.setPrefWidth(100);
            header.setWrapText(true);
            // add a grid layout
            vb.getChildren().add(header);
            
            
            ScrollPane scrollPane = new ScrollPane();
            GridPane grid = new GridPane();
            // for each char, create label for charname , radio for yes, no
            int curRow = 0;
            for (ChoiceItem ci : this.allChoiceItems){
            	CheckBox cb = new CheckBox("");
            	grid.add(cb,0,curRow);
            	allCheckboxes.add(cb);
            	if (this.step.hasPriorAnswers()){
            		dataDownloadPhaseComplete = false;
            	    String selectionState = this.step.getPriorAnswers().get(ci.getNormalizedKey().toString());
            	    if (VALUE_SCORING_CONCERN_SELECTED.equals(selectionState)){
            	        cb.setSelected(true);
            	    }
            	    else {
            	        cb.setSelected(false);
            	    }
            	} 
            	else {
            	    if (ci.isSelected()){
                        cb.setSelected(true);
                    }
                    else {
                    	cb.setSelected(false);
                    }
            	}
            	
            	checkBoxForChoiceItemHash.put(ci, cb);
            	Label label = new Label(ci.getNormalizedKey().getName());
            	label.getStyleClass().add("columnValue");
            	grid.add(label, 1, curRow);
            	curRow++;
            }
            scrollPane.setContent(grid);
            vb.getChildren().add(scrollPane);
            Button buttonUnselectAll = new Button("Unselect All");
            buttonUnselectAll.setOnAction(new EventHandler<ActionEvent>() {
                @Override public void handle(ActionEvent e) {
                    unselectAll();
                }
            });
            buttonUnselectAll.setStyle("-fx-margin-top:10px");
            vb.getChildren().add(buttonUnselectAll);
            Region regionBottom = new Region();
            VBox.setVgrow(regionBottom, Priority.ALWAYS);
            vb.getChildren().add(regionBottom);
            
           
            return content;
    	}
    	catch(IOException ioe){
    		throw new AvatolCVException("problem loading ui " + fxmlDocName + " for controller " + this.getClass().getName() + " single character scope");
    	}
    }
    @Override
    public Node getContentNode() throws AvatolCVException {
        System.out.println("trying to load" +  this.fxmlDocName);
        this.allChoiceItems = this.step.getScoringConcernItems();
        if (this.allChoiceItems.size() == 0){
            throw new AvatolCVException("no valid ChoiceItems detected for scoring concern screen.");
        }
        JavaFXUtils.clearIssues(JavaFXStepSequencer.vBoxDataIssuesSingleton);
        JavaFXUtils.clearDataInPlay();
        if (this.step.getScoringScope() == ScoringAlgorithm.ScoringScope.MULTIPLE_ITEM){
        	return getContentNodeForMultipleItem();
        }
        else {
        	return getContentNodeForSingleItem();
        }
        
    }
	@Override
	public boolean delayEnableNavButtons() {
		return false;
	}
	@Override
	public void executeFollowUpDataLoadPhase(){
	    ProgressPresenterImpl pp = new ProgressPresenterImpl();
        pp.connectProcessNameToLabel(METADATA_DOWNLOAD, remainingMetadataDownloadLabel);
        pp.connectProcessNameToProgressBar(METADATA_DOWNLOAD,remainingMetadataDownloadProgressBar);
        Task<Boolean> task = new RemainingMetadataDownloadTask(pp, this.step, METADATA_DOWNLOAD);
        new Thread(task).start();
	}
    public class RemainingMetadataDownloadTask extends Task<Boolean> {
        private String processName1;
        private ScoringConcernStep step;
        private ProgressPresenter pp;
        private final Logger logger = LogManager.getLogger(RemainingMetadataDownloadTask.class);
        
        public RemainingMetadataDownloadTask(ProgressPresenter pp, ScoringConcernStep step, String processName1){
            this.pp = pp;
            this.step = step;
            this.processName1 = processName1;
        }
        @Override
        protected Boolean call() {
            try {
                this.step.loadRemainingMetadataForChosenDataset(this.pp, processName1);
                dataDownloadPhaseComplete = true;
                return new Boolean(true);
            }
            catch(Exception e){
                AvatolCVExceptionExpresserJavaFX.instance.showException(e, "AvatolCV error downloading scoring data info");
                return new Boolean(false);
            }
        }
    }
    
    @Override
    public void configureUIForFollowUpDataLoadPhase() {
        scoringConcernVBox.getChildren().clear();
        scoringConcernVBox.setSpacing(10);
        Region regionTop = new Region();
//        regionTop.setStyle(value);
        VBox.setVgrow(regionTop, Priority.ALWAYS);
        scoringConcernVBox.getChildren().add(regionTop);
        // add text about doing best to detect presence/absence
        Label header = new Label("downloading additional metadata");
        //header.setPrefWidth(100);
        header.setWrapText(true);
        // add a grid layout
        scoringConcernVBox.getChildren().add(header);
        
        remainingMetadataDownloadProgressBar = new ProgressBar(0.0);
        remainingMetadataDownloadProgressBar.setMinWidth(400);
        scoringConcernVBox.getChildren().add(remainingMetadataDownloadProgressBar);
        
        remainingMetadataDownloadLabel = new Label("");
        scoringConcernVBox.getChildren().add(remainingMetadataDownloadLabel);
        
        Region regionBottom = new Region();
        VBox.setVgrow(regionBottom, Priority.ALWAYS);
        scoringConcernVBox.getChildren().add(regionBottom);
    }
    @Override
    public boolean isFollowUpDataLoadPhaseComplete() {
        return dataDownloadPhaseComplete;
    }
    public void unselectAll(){
    	for (CheckBox cb : allCheckboxes){
    		cb.setSelected(false);
    	}
    }
}
