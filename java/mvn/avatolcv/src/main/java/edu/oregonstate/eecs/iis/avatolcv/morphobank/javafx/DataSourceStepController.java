package edu.oregonstate.eecs.iis.avatolcv.morphobank.javafx;

import java.io.IOException;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import edu.oregonstate.eecs.iis.avatolcv.core.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.core.ScoringAlgorithms;
import edu.oregonstate.eecs.iis.avatolcv.morphobank.DataSourceStep;
import edu.oregonstate.eecs.iis.avatolcv.morphobank.MBLoginStep;

public class DataSourceStepController implements StepController {

    private DataSourceStep dataSourceStep;
    private String fxmlDocName;
    public DataSourceStepController(DataSourceStep dataSourceStep, String fxmlDocName){
        this.dataSourceStep = dataSourceStep;
        this.fxmlDocName = fxmlDocName;
    }
    @Override
    public boolean consumeUIData() {
        // for now, everything is hardcoded to Morphobank
        return true;
    }

    @Override
    public void clearUIFields() {
        // NA

    }

    @Override
    public Node getContentNode() throws AvatolCVException {
        try {
            //System.out.println("trying to load" +  this.fxmlDocName);
            FXMLLoader loader = new FXMLLoader(this.getClass().getResource(this.fxmlDocName));
            loader.setController(this);
            Node content = loader.load();
            return content;
        }
        catch(IOException ioe){
            throw new AvatolCVException("problem loading ui " + fxmlDocName + " for controller " + this.getClass().getName());
        }
    }

    @Override
    public boolean hasActionToAutoStart() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void startAction() throws AvatolCVException {
        // TODO Auto-generated method stub

    }

}
