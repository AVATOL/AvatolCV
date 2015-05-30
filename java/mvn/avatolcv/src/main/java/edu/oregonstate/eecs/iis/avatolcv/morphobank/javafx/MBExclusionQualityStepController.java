package edu.oregonstate.eecs.iis.avatolcv.morphobank.javafx;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import edu.oregonstate.eecs.iis.avatolcv.core.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.core.ImageInfo;
import edu.oregonstate.eecs.iis.avatolcv.morphobank.MBExclusionQualityStep;
import edu.oregonstate.eecs.iis.avatolcv.morphobank.MBViewChoiceStep;

public class MBExclusionQualityStepController implements StepController {
    public HBox excludeImageSequence;
    public VBox imageVBox;
    private MBExclusionQualityStep step;
    private String fxmlDocName;
    private List<String> viewNames = null;
    public MBExclusionQualityStepController(MBExclusionQualityStep step, String fxmlDocName){
        this.step = step;
        this.fxmlDocName = fxmlDocName;
    }
    @Override
    public boolean consumeUIData() {
        return true;
        /*
        try {
            this.step.setChosenView((String)this.selectedView.getValue());
            this.step.consumeProvidedData();
            return true;
        }
        catch (AvatolCVException ace){
            return false;
        }
        */
    }

    @Override
    public void clearUIFields() {
        //selectedView.setValue(viewNames.get(0));
    }

    @Override
    public Node getContentNode() throws AvatolCVException {
        try {
            
            System.out.println("trying to load " +  this.fxmlDocName);
            FXMLLoader loader = new FXMLLoader(this.getClass().getResource(this.fxmlDocName));
            loader.setController(this);
            Node content = loader.load();
            excludeImageSequence.getChildren().clear();
            List<ImageInfo> images = this.step.getImagesLarge();
            for (ImageInfo ii : images){
                VBox vbox = new VBox();
                CheckBox checkBox = new CheckBox("reject this image");
                checkBox.setSelected(false);
                ImageView iv = new ImageView();
                iv.setPreserveRatio(true);
                iv.setFitHeight(300);
                Image image = new Image("file:" + ii.getFilepath());
                iv.setImage(image);
                vbox.getChildren().addAll(checkBox, iv);
                excludeImageSequence.getChildren().add(vbox);
                //hbox.setStyle("-fx-padding: 8;");
            }
            content.autosize();
            return content;
        }
        catch(IOException ioe){
            throw new AvatolCVException("problem loading ui " + fxmlDocName + " for controller " + this.getClass().getName());
        } 
    }

}
