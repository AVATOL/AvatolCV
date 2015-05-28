package edu.oregonstate.eecs.iis.avatolcv.morphobank.javafx;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import edu.oregonstate.eecs.iis.avatolcv.core.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.morphobank.MBViewChoiceStep;

public class MBViewChoiceStepController implements StepController {
    public ComboBox<String> selectedView;
    private MBViewChoiceStep step;
    private String fxmlDocName;
    private List<String> viewNames = null;
    public MBViewChoiceStepController(MBViewChoiceStep step, String fxmlDocName){
        this.step = step;
        this.fxmlDocName = fxmlDocName;
    }
    @Override
    public boolean consumeUIData() {
        try {
            this.step.setChosenView((String)this.selectedView.getValue());
            this.step.consumeProvidedData();
            return true;
        }
        catch (AvatolCVException ace){
            return false;
        }
    }

    @Override
    public void clearUIFields() {
        selectedView.setValue(viewNames.get(0));
    }

    @Override
    public Node getContentNode() throws AvatolCVException {
        try {
            System.out.println("trying to load" +  this.fxmlDocName);
            FXMLLoader loader = new FXMLLoader(this.getClass().getResource(this.fxmlDocName));
            loader.setController(this);
            Node content = loader.load();
            this.viewNames = this.step.getViewNames();
            if (viewNames.size() < 1){
                throw new AvatolCVException("no valid views detected.");
            }
            Collections.sort(viewNames);
            for (String vn : viewNames){
                ObservableList<String> list = selectedView.getItems();
                list.add(vn);
            }
            selectedView.setValue(viewNames.get(0));
            return content;
        }
        catch(IOException ioe){
            throw new AvatolCVException("problem loading ui " + fxmlDocName + " for controller " + this.getClass().getName());
        } 
    }

}
