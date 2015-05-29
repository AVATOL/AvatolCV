package edu.oregonstate.eecs.iis.avatolcv.morphobank.javafx;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import edu.oregonstate.eecs.iis.avatolcv.core.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.core.ProgressPresenter;
import edu.oregonstate.eecs.iis.avatolcv.morphobank.MBImagePullStep;
import edu.oregonstate.eecs.iis.avatolcv.morphobank.MorphobankSessionJavaFX;

public class MBImagePullStepController implements StepController, ProgressPresenter {
    public ProgressBar imageDownloadProgress;
    public Label imageDownloadMessage;
    private MBImagePullStep step;
    private String fxmlDocName;
    private MorphobankSessionJavaFX fxSession;
    public MBImagePullStepController(MorphobankSessionJavaFX fxSession, MBImagePullStep step, String fxmlDocName){
        this.step = step;
        this.fxmlDocName = fxmlDocName;
        this.fxSession = fxSession;
    }
    @Override
    public boolean consumeUIData() {
        // nothing to consume - just reporting progress
        return true;
    }

    @Override
    public void clearUIFields() {
        imageDownloadProgress.setProgress(0.0);
        imageDownloadMessage.setText("");
    }

    @Override
    public Node getContentNode() throws AvatolCVException {
        try {
            System.out.println("trying to load" +  this.fxmlDocName);
            FXMLLoader loader = new FXMLLoader(this.getClass().getResource(this.fxmlDocName));
            loader.setController(this);
            Node content = loader.load();
            imageDownloadProgress.setProgress(0.0);
            imageDownloadMessage.setText("");
            step.downloadImagesForChosenMatrix(this);
            return content;
        }
        catch(IOException ioe){
            throw new AvatolCVException("problem loading ui " + fxmlDocName + " for controller " + this.getClass().getName());
        } 
    }
    @Override
    public void updateProgress(int percent) {
        imageDownloadProgress.setProgress((double)percent);
    }
    @Override
    public void setMessage(String m) {
        imageDownloadMessage.setText(m);
    }

}
