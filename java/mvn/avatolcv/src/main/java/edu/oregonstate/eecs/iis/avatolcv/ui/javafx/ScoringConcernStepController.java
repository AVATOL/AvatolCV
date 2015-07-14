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
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import edu.oregonstate.eecs.iis.avatolcv.core.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.core.ChoiceItem;
import edu.oregonstate.eecs.iis.avatolcv.core.ProgressPresenter;
import edu.oregonstate.eecs.iis.avatolcv.core.ScoringAlgorithms;
import edu.oregonstate.eecs.iis.avatolcv.core.StepController;
import edu.oregonstate.eecs.iis.avatolcv.steps.ScoringConcernStep;

public class ScoringConcernStepController implements StepController {
    public static final String METADATA_DOWNLOAD = "metadataDownload"; 
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
    List<ChoiceItem> allChoiceItems = null;
    public ScoringConcernStepController(ScoringConcernStep step, String fxmlDocName){
        this.step = step;
        this.fxmlDocName = fxmlDocName;
    }
    @Override
    public boolean consumeUIData() {
        try {
        	if (this.step.getScoringAlgorithms().getScoringScope() == ScoringAlgorithms.ScoringScope.MULTIPLE_ITEM){
        		List<ChoiceItem> chosenItems = new ArrayList<ChoiceItem>();
        		for (ChoiceItem ci : this.allChoiceItems){
        			if (checkBoxForChoiceItemHash.get(ci).isSelected()){
        				chosenItems.add(ci);
        			}
        		}
        		this.step.setChosenItems(chosenItems);
        		this.step.consumeProvidedData();
        		return true;
        	}
        	else {
        	    String chosenAnswer = (String)this.itemChoiceComboBox.getValue();
        	    ChoiceItem chosenChoiceItem = choiceItemForNameHash.get(chosenAnswer);
        		this.step.setChosenChoiceItem(chosenChoiceItem);
                this.step.consumeProvidedData();
                return true;
        	}
            
        }
        catch (AvatolCVException ace){
            return false;
        }
    }

    @Override
    public void clearUIFields() {
        itemChoiceComboBox.setValue(this.allChoiceItems.get(0).getName());
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
                list.add(ci.getName());
                choiceItemForNameHash.put(ci.getName(), ci);
            }
            itemChoiceComboBox.setValue(this.allChoiceItems.get(0).getName());
            return content;
    	}
    	catch(IOException ioe){
    		throw new AvatolCVException("problem loading ui " + fxmlDocName + " for controller " + this.getClass().getName() + " single character scope");
    	}
    }
    public Node getContentNodeForMultipleItem() throws AvatolCVException {
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
            	if (ci.isSelected()){
            		cb.setSelected(true);
            	}
            	else {
            		cb.setSelected(false);
            	}
            	checkBoxForChoiceItemHash.put(ci, cb);
            	Label label = new Label(ci.getName());
            	grid.add(label, 1, curRow);
            	curRow++;
            }
            scrollPane.setContent(grid);
            vb.getChildren().add(scrollPane);
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
        ScoringAlgorithms sa = this.step.getScoringAlgorithms();
        this.allChoiceItems = this.step.getScoringConcernItems();
        if (this.allChoiceItems.size() == 0){
            throw new AvatolCVException("no valid ChoiceItems detected for scoring concern screen.");
        }
        if (sa.getScoringScope() == ScoringAlgorithms.ScoringScope.MULTIPLE_ITEM){
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
	public void executeDataLoadPhase(){
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
        protected Boolean call() throws Exception {
            try {
                this.step.loadRemainingMetadataForChosenDataset(this.pp, processName1);
                dataDownloadPhaseComplete = true;
                return new Boolean(true);
            }
            catch(AvatolCVException ace){
                logger.error("AvatolCV error downloading scoring data info");
                logger.error(ace.getMessage());
                System.out.println("AvatolCV error downloading scoring data info");
                ace.printStackTrace();
                return new Boolean(false);
            }
        }
    }
    
    @Override
    public void configureUIForDataLoadPhase() {
        scoringConcernVBox.getChildren().clear();
        Region regionTop = new Region();
        VBox.setVgrow(regionTop, Priority.ALWAYS);
        scoringConcernVBox.getChildren().add(regionTop);
        // add text about doing best to detect presence/absence
        String instructions = this.step.getInstructionsForScoringConcernScreen();
        Label header = new Label("downloading additional metadata");
        //header.setPrefWidth(100);
        header.setWrapText(true);
        // add a grid layout
        scoringConcernVBox.getChildren().add(header);
        
        remainingMetadataDownloadProgressBar = new ProgressBar(0.0);
        remainingMetadataDownloadProgressBar.setMinWidth(300);
        scoringConcernVBox.getChildren().add(remainingMetadataDownloadProgressBar);
        
        remainingMetadataDownloadLabel = new Label("");
        scoringConcernVBox.getChildren().add(remainingMetadataDownloadLabel);
        
        Region regionBottom = new Region();
        VBox.setVgrow(regionBottom, Priority.ALWAYS);
        scoringConcernVBox.getChildren().add(regionBottom);
        
    }
    @Override
    public boolean isDataLoadPhaseComplete() {
        return dataDownloadPhaseComplete;
    }
}
