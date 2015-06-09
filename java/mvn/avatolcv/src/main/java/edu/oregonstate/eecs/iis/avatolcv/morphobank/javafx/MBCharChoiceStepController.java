package edu.oregonstate.eecs.iis.avatolcv.morphobank.javafx;

import java.awt.GridLayout;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;

import javax.swing.BorderFactory;

import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Border;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import edu.oregonstate.eecs.iis.avatolcv.core.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.core.ScoringAlgorithms;
import edu.oregonstate.eecs.iis.avatolcv.morphobank.MBCharChoiceStep;
import edu.oregonstate.eecs.iis.avatolcv.ws.morphobank.CharacterInfo.MBCharacter;

public class MBCharChoiceStepController implements StepController {
    public ComboBox<String> selectedCharacter;
    private MBCharChoiceStep step;
    private String fxmlDocName;
    private List<String> charNames = null;
    private Hashtable<MBCharacter, CheckBox> checkBoxForCharHash;
    //private List<MBCharacter> characters;
    private ScoringAlgorithms.ScoringScope scoringScope = null; 
    List<MBCharacter> allChars = null;
    public MBCharChoiceStepController(MBCharChoiceStep step, String fxmlDocName){
        this.step = step;
        this.fxmlDocName = fxmlDocName;
    }
    @Override
    public boolean consumeUIData() {
        try {
        	if (this.scoringScope == ScoringAlgorithms.ScoringScope.MULTIPLE_ITEM){
        		List<MBCharacter> chosenCharacters = new ArrayList<MBCharacter>();
        		for (MBCharacter ch : this.allChars){
        			if (checkBoxForCharHash.get(ch).isSelected()){
        				chosenCharacters.add(ch);
        			}
        		}
        		this.step.setChosenCharacters(chosenCharacters);
        		this.step.consumeProvidedData();
        		return true;
        	}
        	else {
        		this.step.setChosenCharacter((String)this.selectedCharacter.getValue());
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
        selectedCharacter.setValue(charNames.get(0));
    }
    public Node getContentNodeForSingleCharChoice() throws AvatolCVException {
    	try {
    		FXMLLoader loader = new FXMLLoader(this.getClass().getResource(this.fxmlDocName));
            loader.setController(this);
            Node content = loader.load();
            this.charNames = this.step.getAvailableCharNames();
            if (charNames.size() < 1){
                throw new AvatolCVException("no valid characters detected.");
            }
            Collections.sort(charNames);
            for (String ch : charNames){
                ObservableList<String> list = selectedCharacter.getItems();
                list.add(ch);
            }
            selectedCharacter.setValue(charNames.get(0));
            return content;
    	}
    	catch(IOException ioe){
    		throw new AvatolCVException("problem loading ui " + fxmlDocName + " for controller " + this.getClass().getName() + " single character scope");
    	}
    }
    public Node getContentNodeForAllPresenceAbsence() throws AvatolCVException {
    	checkBoxForCharHash = new Hashtable<MBCharacter, CheckBox>();
    	try {
    		FXMLLoader loader = new FXMLLoader(this.getClass().getResource(this.fxmlDocName));
            loader.setController(this);
            Node content = loader.load();
            VBox vb = (VBox)content.lookup("#characterQuestionVBox");
            // clean it out
            vb.getChildren().clear();
            Region regionTop = new Region();
            VBox.setVgrow(regionTop, Priority.ALWAYS);
            vb.getChildren().add(regionTop);
            // add text about doing best to detect presence/absence
            Label header = new Label("Place a check mark next to characters that refer to presence/absence of a part." +
                    "(AvatolCV has tried to deduce this from metadata.)");
            //header.setPrefWidth(100);
            header.setWrapText(true);
            // add a grid layout
            vb.getChildren().add(header);
            this.allChars = this.step.getCharInfo();
            
            ScrollPane scrollPane = new ScrollPane();
            GridPane grid = new GridPane();
            // for each char, create label for charname , radio for yes, no
            int curRow = 0;
            for (MBCharacter ch : allChars){
            	CheckBox cb = new CheckBox("");
            	grid.add(cb,0,curRow);
            	String charName = ch.getCharName();
            	if (this.step.isCharPresenceAbsence(charName)){
            		cb.setSelected(true);
            	}
            	else {
            		cb.setSelected(false);
            	}
            	checkBoxForCharHash.put(ch, cb);
            	Label label = new Label(charName);
            	grid.add(label, 1, curRow);
            	//RadioButton rbNo = new RadioButton("other");
            	//grid.add(rbNo,2, curRow);
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
        this.scoringScope = sa.getScoringScope();
        if (this.scoringScope == ScoringAlgorithms.ScoringScope.MULTIPLE_ITEM
        		&& sa.getSessionScoringFocus() == ScoringAlgorithms.ScoringSessionFocus.SPECIMEN_PART_PRESENCE_ABSENCE){
        	return getContentNodeForAllPresenceAbsence();
        }
        else {
        	return getContentNodeForSingleCharChoice();
        }
    }
	@Override
	public boolean delayEnableNavButtons() {
		return false;
	}

}
