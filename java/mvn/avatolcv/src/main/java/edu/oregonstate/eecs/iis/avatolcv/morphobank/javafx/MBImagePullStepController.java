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
    public static final String IMAGE_INFO_DOWNLOAD = "imageInfoDownload"; 
    public static final String IMAGE_FILE_DOWNLOAD = "imageFileDownload"; 
    public ProgressBar imageFileDownloadProgress;
    public Label imageFileDownloadMessage;
    public ProgressBar imageInfoDownloadProgress;
    public Label imageInfoDownloadMessage;
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
        imageInfoDownloadProgress.setProgress(0.0);
        imageInfoDownloadMessage.setText("");
        
        imageFileDownloadProgress.setProgress(0.0);
        imageFileDownloadMessage.setText("");
    }

    @Override
    public Node getContentNode() throws AvatolCVException {
        try {
            System.out.println("trying to load" +  this.fxmlDocName);
            FXMLLoader loader = new FXMLLoader(this.getClass().getResource(this.fxmlDocName));
            loader.setController(this);
            Node content = loader.load();
            
            imageInfoDownloadProgress.setProgress(0.0);
            imageInfoDownloadMessage.setText("");
            
            imageFileDownloadProgress.setProgress(0.0);
            imageFileDownloadMessage.setText("");
            
            return content;
        }
        catch(IOException ioe){
            throw new AvatolCVException("problem loading ui " + fxmlDocName + " for controller " + this.getClass().getName());
        } 
    }
    @Override
    public void updateProgress(String processName, int percent) {
        if (IMAGE_FILE_DOWNLOAD.equals(processName)){
            imageFileDownloadProgress.setProgress((double)percent);
        }
        else if (IMAGE_INFO_DOWNLOAD.equals(processName)){
            imageInfoDownloadProgress.setProgress((double)percent);
        }
    }
    @Override
    public void setMessage(String processName, String m) {
        if (IMAGE_FILE_DOWNLOAD.equals(processName)){
            imageFileDownloadMessage.setText(m);
        }
        else if (IMAGE_INFO_DOWNLOAD.equals(processName)){
            imageInfoDownloadMessage.setText(m);
        }
    }
    @Override
    public boolean hasActionToAutoStart() {
        return true;
    }
    @Override
    public void startAction() throws AvatolCVException {
        step.downloadImagesForChosenMatrix(this, IMAGE_INFO_DOWNLOAD, IMAGE_FILE_DOWNLOAD);
        
    }

}
