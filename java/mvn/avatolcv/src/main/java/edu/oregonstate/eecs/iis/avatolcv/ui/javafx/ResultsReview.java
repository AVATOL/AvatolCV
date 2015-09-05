package edu.oregonstate.eecs.iis.avatolcv.ui.javafx;

import java.util.List;

import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Accordion;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TitledPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import edu.oregonstate.eecs.iis.avatolcv.AvatolCVFileSystem;
import edu.oregonstate.eecs.iis.avatolcv.RunSummary;
import edu.oregonstate.eecs.iis.avatolcv.core.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.core.AvatolCVExceptionExpresser;
import edu.oregonstate.eecs.iis.avatolcv.core.NormalizedImageInfos;
import edu.oregonstate.eecs.iis.avatolcv.core.NormalizedImageInfo;
import edu.oregonstate.eecs.iis.avatolcv.javafxui.AvatolCVJavaFXMB;

public class ResultsReview {
    public Accordion runDetailsAccordion = null;
    public ScrollPane runDetailsScrollPane = null;
    public Label runIDValue = null;
    public Label datasetValue = null;
    public Label scoringConcernValue = null;
    public Label dataSourceValue = null;
    public Label scoringAlgorithmValue = null;
    public VBox scoredImagesVBox = null;
    public VBox trainingImagesVBox = null;
    private AvatolCVExceptionExpresser exceptionExpresser = null;
    private Stage mainWindow = null;
    private Scene scene = null;
    private String runID = null;
    
    public ResultsReview(AvatolCVExceptionExpresser exceptionExpresser){
        this.exceptionExpresser = exceptionExpresser;
    }
    public void init(String avatolCVRootDir, Stage mainWindow, String runID) throws AvatolCVException {
        this.mainWindow = mainWindow;
        this.runID = runID;
        initUI();
    }
    public void initUI() throws AvatolCVException {
        try {
            FXMLLoader loader = new FXMLLoader(JavaFXStepSequencer.class.getResource("ResultsReview.fxml"));
            loader.setController(this);
            Parent resultsReview = loader.load();
            
            this.scene = new Scene(resultsReview, AvatolCVJavaFXMB.MAIN_WINDOW_WIDTH, AvatolCVJavaFXMB.MAIN_WINDOW_HEIGHT);
            this.mainWindow.setScene(scene);
            runDetailsAccordion.getPanes().remove(2);
            runDetailsAccordion.setExpandedPane(runDetailsAccordion.getPanes().get(0));
            runDetailsScrollPane.setCache(false); // to fix blurriness bug (http://stackoverflow.com/questions/23728517/blurred-text-in-javafx-textarea)
            for (Node n : runDetailsScrollPane.getChildrenUnmodifiable()) {
                n.setCache(false);
            }
            setRunDetails(this.runID);
            setScoredImagesInfo(this.runID, scoringConcernValue.getText());
            //runDetailsAccordion.requestLayout();
        }
        catch(Exception e){
            throw new AvatolCVException(e.getMessage(),e);
        }
    }
    private void setScoredImagesInfo(String runID, String scoringConcernValue) throws AvatolCVException {
        scoredImagesVBox.getChildren().clear();
        NormalizedImageInfos normalizedImageInfos = new NormalizedImageInfos(runID);
        List<NormalizedImageInfo> scoredImages = normalizedImageInfos.getScoredImages(scoringConcernValue);
        for (NormalizedImageInfo si : scoredImages){
            HBox scoredImageHBox = getScoredImageHBox(si);
            scoredImagesVBox.getChildren().add(scoredImageHBox);
        }
        scoredImagesVBox.requestLayout();
    }
    private HBox getScoredImageHBox(NormalizedImageInfo si) throws AvatolCVException {
        HBox hb = new HBox();
        // get the image
        ImageView iv = new ImageView();
        hb.getChildren().add(iv);
        
        // get trainingVsTestConcern
        String trainingVsTestName = si.getTrainingVsTestName();
        Label trainingVsTestLabel = new Label(" ");
        if (null != trainingVsTestName){
            trainingVsTestLabel.setText(trainingVsTestName);
            hb.getChildren().add(trainingVsTestLabel);
        }
        
        
        // get image name
        String imageName = si.getImageName();
        Label imageNameLabel = new Label(" name? ");
        if (null != imageName){
            imageNameLabel.setText(imageName);
        }
        hb.getChildren().add(imageNameLabel);
        
        // get truth
        String truth = si.getTruthValue();
        Label truthLabel = new Label();
        if (null != truth){
            truthLabel.setText(truth);
        }
        hb.getChildren().add(truthLabel);
        
        // get score
        String score = si.getScoreValue();
        Label scoreLabel = new Label();
        if (null != score){
            scoreLabel.setText(score);
        }
        hb.getChildren().add(scoreLabel);
        
        // get confidence
        String confidence = si.getScoringConfidence();
        Label confidenceLabel = new Label(" conf? ");
        if (null != confidence){
            confidenceLabel.setText(confidence);
        }
        hb.getChildren().add(confidenceLabel);
        return null;
    }
    private void setRunDetails(String runID) throws AvatolCVException {
        RunSummary rs = new RunSummary(runID);
    	runIDValue.setText(rs.getRunID());
        datasetValue.setText(rs.getDataset());
        scoringConcernValue.setText(rs.getScoringConcern());
        dataSourceValue.setText(rs.getDataSource());
        scoringAlgorithmValue.setText(rs.getScoringAlgorithm());
    }
}
