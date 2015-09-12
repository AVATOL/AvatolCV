package edu.oregonstate.eecs.iis.avatolcv.ui.javafx;

import java.io.File;
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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import edu.oregonstate.eecs.iis.avatolcv.AvatolCVFileSystem;
import edu.oregonstate.eecs.iis.avatolcv.RunSummary;
import edu.oregonstate.eecs.iis.avatolcv.core.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.core.AvatolCVExceptionExpresser;
import edu.oregonstate.eecs.iis.avatolcv.core.NormalizedImageInfos;
import edu.oregonstate.eecs.iis.avatolcv.core.NormalizedImageInfo;
import edu.oregonstate.eecs.iis.avatolcv.generic.DatasetInfo;
import edu.oregonstate.eecs.iis.avatolcv.javafxui.AvatolCVJavaFXMB;

public class ResultsReview {
    public Accordion runDetailsAccordion = null;
    public ScrollPane runDetailsScrollPane = null;
    public Label runIDValue = null;
    public Label datasetValue = null;
    public Label scoringConcernValue = null;
    public Label dataSourceValue = null;
    public Label scoringAlgorithmValue = null;
    public GridPane scoredImagesGridPane = null;
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
    	scoredImagesGridPane.getChildren().clear();
        addScoredImagesHeader(scoredImagesGridPane);
        NormalizedImageInfos normalizedImageInfos = new NormalizedImageInfos(runID);
        List<NormalizedImageInfo> scoredImages = normalizedImageInfos.getScoredImages(scoringConcernValue);
        for (int i=0; i < scoredImages.size(); i++){
        	addScoredImageToGridPaneRow(scoredImages.get(i), scoredImagesGridPane, i+1);
        }
        scoredImagesGridPane.requestLayout();
    }
    private void addScoredImagesHeader(GridPane gp){
    	Label imageLabel = new Label("    ");
    	gp.add(imageLabel, 0, 0);
    	
    	Label trainTestLabel = new Label("trainVsTest");
    	gp.add(trainTestLabel, 1, 0);
    	
    	Label imageNameLabel = new Label("name");
    	gp.add(imageNameLabel, 2, 0);
    	
    	Label truthLabel = new Label("truth");
    	gp.add(truthLabel, 3, 0);
    	
    	Label scoreLabel = new Label("score");
    	gp.add(scoreLabel, 4, 0);
    	
    	Label confidence = new Label("confidence");
    	gp.add(confidence, 5, 0);
    }
    private String getThumbailPath(NormalizedImageInfo si) throws AvatolCVException {
        String id = si.getImageID();
        String thumbnailDir = AvatolCVFileSystem.getNormalizedImagesThumbnailDir();
        File thumbnailDirFile = new File(thumbnailDir);
        File[] files = thumbnailDirFile.listFiles();
        for (File f : files){
            if (f.getName().startsWith(id)){
                return f.getAbsolutePath();
            }
        }
        throw new AvatolCVException("Could not find thumbnail for normalizedImage with ID " + id);
    }
    private void addScoredImageToGridPaneRow(NormalizedImageInfo si, GridPane gp, int row) throws AvatolCVException {
        // get the image
        String thumbnailPath = getThumbailPath(si);
        Image image = new Image("file:"+thumbnailPath);
        ImageView iv = new ImageView(image);
        int column = 0;
        gp.add(iv,column,row);
        column++;
        // get trainingVsTestConcern
        String trainingVsTestName = si.getTrainingVsTestName();
        Label trainingVsTestLabel = new Label(" ");
        if (null != trainingVsTestName){
            trainingVsTestLabel.setText(trainingVsTestName);
            gp.add(trainingVsTestLabel,column,row);
        }
        column++;
        
        // get image name
        String imageName = si.getImageName();
        Label imageNameLabel = new Label(" name? ");
        if (null != imageName){
            imageNameLabel.setText(imageName);
        }
        gp.add(imageNameLabel,column,row);
        column++;
        // get truth
        String truth = si.getTruthValue();
        Label truthLabel = new Label();
        if (null != truth){
            truthLabel.setText(truth);
        }
        gp.add(truthLabel,column,row);
        column++;
        // get score
        String score = si.getScoreValue();
        Label scoreLabel = new Label();
        if (null != score){
            scoreLabel.setText(score);
        }
        gp.add(scoreLabel, column, row);
        column++;
        // get confidence
        String confidence = si.getScoringConfidence();
        Label confidenceLabel = new Label(" conf? ");
        if (null != confidence){
            confidenceLabel.setText(confidence);
        }
        gp.add(confidenceLabel,column, row);
    }
    private void setRunDetails(String runID) throws AvatolCVException {
        RunSummary rs = new RunSummary(runID);
        AvatolCVFileSystem.setDatasourceName(rs.getDataSource());
        AvatolCVFileSystem.setSessionID(rs.getRunID());
     // tell the fileSystem which dataset is in play
        DatasetInfo di = new DatasetInfo();
        di.setName(rs.getDataset());
        AvatolCVFileSystem.setChosenDataset(di);
        
    	runIDValue.setText(rs.getRunID());
        datasetValue.setText(rs.getDataset());
        scoringConcernValue.setText(rs.getScoringConcern());
        dataSourceValue.setText(rs.getDataSource());
        scoringAlgorithmValue.setText(rs.getScoringAlgorithm());
    }
}
