package edu.oregonstate.eecs.iis.avatolcv.morphobank.javafx;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.ColumnConstraintsBuilder;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import edu.oregonstate.eecs.iis.avatolcv.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.core.ProgressPresenter;
import edu.oregonstate.eecs.iis.avatolcv.core.ScoringAlgorithms;
import edu.oregonstate.eecs.iis.avatolcv.core.StepController;
import edu.oregonstate.eecs.iis.avatolcv.core.TrainTestInfo;
import edu.oregonstate.eecs.iis.avatolcv.morphobank.MBImagePullStep;
import edu.oregonstate.eecs.iis.avatolcv.morphobank.charscore.MBTrainingExampleCheckStep;
import edu.oregonstate.eecs.iis.avatolcv.morphobank.javafx.MBImagePullStepController.ImageDownloadTask;
import edu.oregonstate.eecs.iis.avatolcv.morphobank.javafx.MBImagePullStepController.MessageUpdater;
import edu.oregonstate.eecs.iis.avatolcv.morphobank.javafx.MBImagePullStepController.NavButtonEnablerRunner;
import edu.oregonstate.eecs.iis.avatolcv.morphobank.javafx.MBImagePullStepController.ProgressUpdater;
import edu.oregonstate.eecs.iis.avatolcv.ws.morphobank.AnnotationInfo.MBAnnotation;
import edu.oregonstate.eecs.iis.avatolcv.ws.morphobank.CharacterInfo.MBCharacter;
import edu.oregonstate.eecs.iis.avatolcv.ws.morphobank.TaxaInfo.MBTaxon;

public class MBTrainingExampleCheckStepController implements StepController  {

    public GridPane trainingTestingGridPane;
    private MBTrainingExampleCheckStep step = null;
    private String fxmlDocName;
    private int firstCharacterColumn = 3;
    private Hashtable<String,ToggleButton> toggelButtonForTaxonIdHash = new Hashtable<String, ToggleButton>();
    public MBTrainingExampleCheckStepController(MBTrainingExampleCheckStep step, String fxmlDocName){
        this.step = step;
        this.fxmlDocName = fxmlDocName;
    }
    @Override
    public boolean consumeUIData() {
        // TODO Auto-generated method stub
        return false;
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
            trainingTestingGridPane.getChildren().clear();
            //String ttdName = this.step.getTrainingTestingDescriminatorName();
            // find out which taxa are in play
            List<MBTaxon> taxa = this.step.getTrueTaxaForCurrentMatrix();
            // find out which characters are in play
            List<MBCharacter> characters = this.step.getCharacters();          
            // for each taxon
            int column = firstCharacterColumn;
            for (MBCharacter character : characters){
            	Label charLabel = new Label(character.getCharName());
            	charLabel.setPrefWidth(75);
            	trainingTestingGridPane.add(charLabel, column++, 0);
            }
            ColumnConstraints cc = new ColumnConstraints();
            
            cc.setHgrow(Priority.NEVER);
            for (int i = 0; i < column; i++){
                trainingTestingGridPane.getColumnConstraints().add(cc);
            }
            int row = 1;
            for (MBTaxon taxon: taxa){
            	ToggleGroup group = new ToggleGroup();
            	//ToggleButton trainButton = new ToggleButton("Train");
            	RadioButton radioTrain = new RadioButton("Train");
            	radioTrain.setToggleGroup(group);
            	radioTrain.setSelected(true);
            	//ToggleButton testButton = new ToggleButton("Test");
            	RadioButton radioTest = new RadioButton("Score");
            	radioTest.setToggleGroup(group);
            	Label taxonLabel = new Label(taxon.getTaxonName());
            	GridPane.setHgrow(radioTrain, Priority.NEVER);
            	GridPane.setHgrow(radioTest, Priority.NEVER);
            	GridPane.setHgrow(taxonLabel, Priority.NEVER);

            	trainingTestingGridPane.add(radioTrain, 1, row);
            	trainingTestingGridPane.add(radioTest, 2, row);
            	trainingTestingGridPane.add(taxonLabel, 0, row++);
            	column = firstCharacterColumn;
            	for (MBCharacter character : characters){
                	TrainTestInfo tti = this.step.getTrainTestInfo(taxon.getTaxonID(), character.getCharID());
                }
            	
            	//left off trying to get the grid to be tight
            	
            	
            //     render a row
            //     for each character
            //          count the training samples
            //          count the test samples
            }
            
            //List<MBTaxon> taxa = this.step.getTaxa();
            
            
            //- get all the taxa
            //- for each taxon, get the count of training images and test images
            //- add a row to the gridpane
           // - add a radio button for each, with counts
            trainingTestingGridPane.layout();
            //content.autosize();
            return content;
        }
        catch(IOException ioe){
            throw new AvatolCVException("problem loading ui " + fxmlDocName + " for controller " + this.getClass().getName());
        }
    }
    
    
	@Override
	public boolean delayEnableNavButtons() {
		return false;
	}
	
}
