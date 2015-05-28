package edu.oregonstate.eecs.iis.avatolcv.morphobank.javafx;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import edu.oregonstate.eecs.iis.avatolcv.core.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.morphobank.MBCharChoiceStep;

public class MBCharChoiceStepController implements StepController {
    public ComboBox<String> selectedCharacter;
    private MBCharChoiceStep step;
    private String fxmlDocName;
    private List<String> charNames = null;
    public MBCharChoiceStepController(MBCharChoiceStep step, String fxmlDocName){
        this.step = step;
        this.fxmlDocName = fxmlDocName;
    }
    @Override
    public boolean consumeUIData() {
        try {
            this.step.setChosenCharacter((String)this.selectedCharacter.getValue());
            this.step.consumeProvidedData();
            return true;
        }
        catch (AvatolCVException ace){
            return false;
        }
    }

    @Override
    public void clearUIFields() {
        selectedCharacter.setValue(charNames.get(0));
    }

    @Override
    public Node getContentNode() throws AvatolCVException {
        try {
            System.out.println("trying to load" +  this.fxmlDocName);
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
            throw new AvatolCVException("problem loading ui " + fxmlDocName + " for controller " + this.getClass().getName());
        } 
    }

}
