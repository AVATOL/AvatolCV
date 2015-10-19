package edu.oregonstate.eecs.iis.avatolcv.ui.javafx;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Accordion;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.control.Tab;
import javafx.scene.control.TitledPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import edu.oregonstate.eecs.iis.avatolcv.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.AvatolCVExceptionExpresser;
import edu.oregonstate.eecs.iis.avatolcv.AvatolCVFileSystem;
import edu.oregonstate.eecs.iis.avatolcv.RunSummary;
import edu.oregonstate.eecs.iis.avatolcv.core.DatasetInfo;
import edu.oregonstate.eecs.iis.avatolcv.core.NormalizedImageInfosToReview;
import edu.oregonstate.eecs.iis.avatolcv.core.NormalizedImageInfo;
import edu.oregonstate.eecs.iis.avatolcv.core.ScoreIndex;
import edu.oregonstate.eecs.iis.avatolcv.core.ScoresInfoFile;
import edu.oregonstate.eecs.iis.avatolcv.core.TrainingInfoFile;
import edu.oregonstate.eecs.iis.avatolcv.javafxui.AvatolCVJavaFX;
import edu.oregonstate.eecs.iis.avatolcv.results.ResultsTable;
import edu.oregonstate.eecs.iis.avatolcv.results.SortableRow;

public class ResultsReview {
	public Slider thresholdSlider = null;
	private ScoreIndex scoreIndex = null;
    public Accordion runDetailsAccordion = null;
    public ScrollPane runDetailsScrollPane = null;
    public Tab scoredImagesTab = null;
    public Tab trainingImagesTab = null;
    public Label runIDValue = null;
    public Label datasetValue = null;
    public Label scoringConcernValue = null;
    public Label dataSourceValue = null;
    public Label scoringAlgorithmValue = null;
    public GridPane scoredImagesGridPane = null;
    public GridPane trainingImagesGridPane = null;
    public VBox trainingImagesVBox = null;
    private AvatolCVExceptionExpresser exceptionExpresser = null;
    private Stage mainWindow = null;
    private Scene scene = null;
    private String runID = null;
    private RunSummary runSummary = null;
    private AvatolCVJavaFX mainScreen = null;
    private ResultsTable resultsTable = null;
    public ResultsReview(AvatolCVExceptionExpresser exceptionExpresser){
        this.exceptionExpresser = exceptionExpresser;
    }
    public void init(String avatolCVRootDir, AvatolCVJavaFX mainScreen, Stage mainWindow, String runID) throws AvatolCVException {
        this.mainWindow = mainWindow;
        this.mainScreen = mainScreen;
        this.runID = runID;
        initUI();
    }
    public void initUI() throws AvatolCVException {
        try {
            FXMLLoader loader = new FXMLLoader(JavaFXStepSequencer.class.getResource("ResultsReview.fxml"));
            loader.setController(this);
            Parent resultsReview = loader.load();
            
            this.scene = new Scene(resultsReview, AvatolCVJavaFX.MAIN_WINDOW_WIDTH, AvatolCVJavaFX.MAIN_WINDOW_HEIGHT);
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
            setupSlider();
            //scoredImagesGridPane.setStyle("-fx-background-color:yellow;");
            
        }
        catch(Exception e){
            throw new AvatolCVException(e.getMessage(),e);
        }
    }
    private void setupSlider(){
    	double initValue = thresholdSlider.getValue();
    	adjustConfidencesToThreshold(initValue);
    	thresholdSlider.valueProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<? extends Number> ov,
                Number old_val, Number new_val) {
            	    adjustConfidencesToThreshold(new_val.doubleValue());
            }
        });
    }
    public void adjustConfidencesToThreshold(double value){
	    double newValPercent = value/100;
        String newValString = "" + newValPercent;
        String twoDecimalString = limitToTwoDecimalPlaces(newValString);
	    disableAllUnderThreshold(twoDecimalString);
    }
    private void disableAllUnderThreshold(String threshold){
    	List<SortableRow> rows = resultsTable.getRows();
        for (SortableRow row : rows){
            int index = ResultsTable.getIndexOfColumn(ResultsTable.COLNAME_CONFIDENCE);
            Label confLabel = (Label)row.getWidget(ResultsTable.COLNAME_CONFIDENCE);
            Label nameLabel = (Label)row.getWidget(ResultsTable.COLNAME_NAME);
            Label scoreLabel = (Label)row.getWidget(ResultsTable.COLNAME_SCORE);
            Label truthLabel = (Label)row.getWidget(ResultsTable.COLNAME_TRUTH);
            if (row.hasDoubleValueLessThanThisAtIndex(threshold, index)){
            	//confLabel.setStyle("-fx-background-color:#CC0000;");
            	confLabel.setDisable(true);
            	nameLabel.setDisable(true);
            	scoreLabel.setDisable(true);
            	truthLabel.setDisable(true);
            }
            else {
            	//confLabel.setStyle("-fx-background-color:#00CC00;");
            	confLabel.setDisable(false);
            	nameLabel.setDisable(false);
            	scoreLabel.setDisable(false);
            	truthLabel.setDisable(false);
            }
        }
        
    }
    private void addEventhandlerForImageClick(ImageView iv, SortableRow sr){
        iv.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (sr.isLargeImageShown()){
                    hideLargeImage(sr);
                }
                else {
                    showLargeImage(sr);
                }
                event.consume();
            }
            private void showLargeImage(SortableRow sr){
                int targetRowIndex = (sr.getIndex()*2) + 2;
                String thumbnailPath = sr.getValue(ResultsTable.getIndexOfColumn(ResultsTable.COLNAME_IMAGE));
                try {
                    String largeImagePath = AvatolCVFileSystem.getLargeImagePathForThumbnailPath(thumbnailPath);
                    //System.out.println("put big image " + largeImagePath + " at index " + targetIndex);
                    Image image = new Image("file:"+largeImagePath);
                    ImageView iv = new ImageView(image);
                    sr.rememberReferenceToLargeImage(iv);
                    scoredImagesGridPane.add(iv, 0, targetRowIndex, 5, 1);
                }
                catch(AvatolCVException e){
                    // just print error for now
                    System.out.println(e.getMessage());
                }
            }
            private void hideLargeImage(SortableRow sr){
                ImageView iv = (ImageView)sr.forgetLargeImageObject();
                scoredImagesGridPane.getChildren().remove(iv);
            }
       });
    }
    private void generateScoreWidgets(SortableRow sr){
        String thumbnailPathname = sr.getValue(ResultsTable.getIndexOfColumn(ResultsTable.COLNAME_IMAGE));
        Image image = new Image("file:"+thumbnailPathname);
        ImageView iv = new ImageView(image);
        addEventhandlerForImageClick(iv, sr);
        //if (isImageTallerThanWide(image)){
        //    iv.setRotate(90);
        //}
        
        sr.setWidget(ResultsTable.COLNAME_IMAGE, iv);
        
        // get trainingVsTestConcern if relevant, OR image name
        String name = sr.getValue(ResultsTable.getIndexOfColumn(ResultsTable.COLNAME_NAME));
        Label nameLabel = new Label(name);
        nameLabel.getStyleClass().add("columnValue");
        sr.setWidget(ResultsTable.COLNAME_NAME, nameLabel);
     
        // get truth
        String truth = sr.getValue(ResultsTable.getIndexOfColumn(ResultsTable.COLNAME_TRUTH));
        Label truthLabel = new Label(truth);
        truthLabel.getStyleClass().add("columnValue");
        sr.setWidget(ResultsTable.COLNAME_TRUTH, truthLabel);

        // get score
        String score = sr.getValue(ResultsTable.getIndexOfColumn(ResultsTable.COLNAME_SCORE));
        Label scoreLabel = new Label(score);
        scoreLabel.getStyleClass().add("columnValue");
        sr.setWidget(ResultsTable.COLNAME_SCORE, scoreLabel);
        
        // get confidence
        String scoreConf = sr.getValue(ResultsTable.getIndexOfColumn(ResultsTable.COLNAME_CONFIDENCE));
        String trimmedScoreConf = limitToTwoDecimalPlaces(scoreConf);
        Label confidenceLabel = new Label(trimmedScoreConf);
        confidenceLabel.getStyleClass().add("columnValue");
        sr.setWidget(ResultsTable.COLNAME_CONFIDENCE, confidenceLabel);
    }
    private void renderResultsTable(ResultsTable rt){
        List<SortableRow> rows = rt.getRows();
        //scoredImagesGridPane.setGridLinesVisible(true);
        for(int i = 0; i < rows.size(); i++){
            int offset = (2*i)+1;
            // get the image
            SortableRow row = rows.get(i);
            ImageView iv = (ImageView)row.getWidget(ResultsTable.COLNAME_IMAGE);
           
            int column = 0;
            System.out.println("col " + column + " row " + offset);
            scoredImagesGridPane.add(iv,column,offset);
            column++;
            // get truth
            Label truthLabel = (Label)row.getWidget(ResultsTable.COLNAME_TRUTH);
            System.out.println("col " + column + " row " + offset);
            scoredImagesGridPane.add(truthLabel,column,offset);
            column++;
            // get score
            
            Label scoreLabel = (Label)row.getWidget(ResultsTable.COLNAME_SCORE);
            System.out.println("col " + column + " row " + offset);
            scoredImagesGridPane.add(scoreLabel, column, offset);
            column++;
            // get confidence
            Label confidenceLabel = (Label)row.getWidget(ResultsTable.COLNAME_CONFIDENCE);
            System.out.println("col " + column + " row " + offset);
            scoredImagesGridPane.add(confidenceLabel,column, offset);
            column++;

            // get trainingVsTestConcern if relevant, OR image name
            Label nameLabel = (Label)row.getWidget(ResultsTable.COLNAME_NAME);
            System.out.println("col " + column + " row " + offset);
            scoredImagesGridPane.add(nameLabel,column,offset);
        }
        //scoredImagesGridPane.setGridLinesVisible(true);
        ensureConstraintsForGridPane(scoredImagesGridPane);
    }
    private void ensureConstraintsForGridPane(GridPane gp){
        ObservableList<ColumnConstraints> colConstraints = gp.getColumnConstraints();
        for (ColumnConstraints cc : colConstraints){
            cc.setHgrow(Priority.NEVER);
        }
        ObservableList<RowConstraints> rowConstraints = gp.getRowConstraints();
        for (RowConstraints rc : rowConstraints){
            rc.setVgrow(Priority.NEVER);
        }
    }
    private void setScoredImagesInfoCookingShow(String runID, String scoringConcernName) throws AvatolCVException {
    	scoredImagesTab.setText(scoringConcernName + " - SCORED images");
    	trainingImagesTab.setText(scoringConcernName + " - TRAINING images");

    	scoredImagesGridPane.getChildren().clear();
    	addScoredImagesHeader(scoredImagesGridPane);
    	
    	String scoreFilePath = AvatolCVFileSystem.getScoreFilePath(runID, scoringConcernName);
    	ScoresInfoFile sif = new ScoresInfoFile(scoreFilePath);
    	
    	String trainingFilePath = AvatolCVFileSystem.getTrainingFilePath(runID, scoringConcernName);
    	TrainingInfoFile tif = new TrainingInfoFile(trainingFilePath);
    	
    	resultsTable = new ResultsTable();
    	List<String> scoringImageNames = sif.getImageNames();
    	int row = 1;
    	for (String name : scoringImageNames){
    		//System.out.println("got image " + name);
    		String value = sif.getScoringConcernValueForImageName(name);
    		String conf = sif.getConfidenceForImageValue(name, value);
    		String truth = tif.getScoringConcernValueForImageName(name);
        	String origImageName = getTrueImageNameFromImageNameForCookingShow(name);
        	String thumbnailPathname = getThumbnailPathWithImageNameForCookingShow(name);
        	SortableRow sortableRow = resultsTable.createRow(thumbnailPathname, origImageName, value, conf, truth, row - 1);
        	generateScoreWidgets(sortableRow);
        	row++;
        	//scoredImagesGridPane.getRowConstraints().get(row).setPrefHeight(imageHeight);
        	//System.out.println("rc count : " + scoredImagesGridPane.getRowConstraints().size());
    	}
    	renderResultsTable(resultsTable);
       // NormalizedImageInfosToReview normalizedImageInfos = new NormalizedImageInfosToReview(runID);
       // List<NormalizedImageInfo> scoredImages = normalizedImageInfos.getScoredImages(scoringConcernValue);
       // for (int i=0; i < scoredImages.size(); i++){
       // 	addScoredImageToGridPaneRow(scoredImages.get(i), scoredImagesGridPane, i+1);
       // }
        //
    	addTrainingImagesHeader(trainingImagesGridPane);
    	List<String> imageNames = tif.getImageNames();
    	row = 1;
    	for (String name : imageNames){
    		//System.out.println("got image " + name);
    		if (!sif.hasImage(name)){
    			String value = tif.getScoringConcernValueForImageName(name);
        		String trueName = getTrueImageNameFromImageNameForCookingShow(name);
        		addTrainingImageToGridPaneRowForCookingShow(name, trueName, value, trainingImagesGridPane, row++);
    		}
    	}
        
        
       // List<NormalizedImageInfo> trainingImages = normalizedImageInfos.getTrainingImages(scoringConcernValue);
       // for (int i=0; i < trainingImages.size(); i++){
       // 	addTrainingImageToGridPaneRow(trainingImages.get(i), trainingImagesGridPane, i+1);
       // }
    	trainingImagesGridPane.requestLayout();
        scoredImagesGridPane.requestLayout();
    }
    private void setScoredImagesInfo(String runID, String scoringConcernName) throws AvatolCVException {
    	scoredImagesGridPane.getChildren().clear();
    	if (this.runSummary.isCookingShow()){
    		setScoredImagesInfoCookingShow(runID, scoringConcernName);
    	}
    	else {
    		addScoredImagesHeader(scoredImagesGridPane);
            NormalizedImageInfosToReview normalizedImageInfos = new NormalizedImageInfosToReview(runID);
            List<NormalizedImageInfo> scoredImages = normalizedImageInfos.getScoredImages(scoringConcernName);
            for (int i=0; i < scoredImages.size(); i++){
            	addScoredImageToGridPaneRow(scoredImages.get(i), scoredImagesGridPane, i+1);
            }
            //
            addTrainingImagesHeader(trainingImagesGridPane);
            List<NormalizedImageInfo> trainingImages = normalizedImageInfos.getTrainingImages(scoringConcernName);
            for (int i=0; i < trainingImages.size(); i++){
            	addTrainingImageToGridPaneRow(trainingImages.get(i), trainingImagesGridPane, i+1);
            }
            scoredImagesGridPane.requestLayout();
    	}
    }
    private void addScoredImagesHeader(GridPane gp){
    	Label imageLabel = new Label("    ");
    	int column = 0;
    	gp.add(imageLabel, column++, 0);
    	Label itemLabel = null;
    	if (this.runSummary.hasTrainTestConcern()){
    		itemLabel = new Label(this.runSummary.getTrainTestConcern());
        	gp.add(itemLabel, column++, 0);
    	}
    	else {
    		itemLabel = new Label("Name");
    		gp.add(itemLabel, column++, 0);
    	}
    	//itemLabel.setStyle("-fx-background-color:green;");
    	itemLabel.getStyleClass().add("columnHeader");
    	
    	Label truthLabel = new Label("Truth");
    	truthLabel.getStyleClass().add("columnHeader");
    	gp.add(truthLabel, column++, 0);
    	
    	Label scoreLabel = new Label("Score");
    	scoreLabel.getStyleClass().add("columnHeader");
    	gp.add(scoreLabel, column++, 0);
    	
    	Label confidence = new Label("Confidence");
    	confidence.getStyleClass().add("columnHeader");
    	gp.add(confidence, column++, 0);
    }

    private void addTrainingImagesHeader(GridPane gp){
    	Label imageLabel = new Label("    ");
    	int column = 0;
    	gp.add(imageLabel, column++, 0);
    	Label itemLabel = null;
    	if (this.runSummary.hasTrainTestConcern()){
    		itemLabel = new Label(this.runSummary.getTrainTestConcern());
        	gp.add(itemLabel, column++, 0);
    	}
    	else {
    		itemLabel = new Label("Name");
    		gp.add(itemLabel, column++, 0);
    	}
    	
    	Label truthLabel = new Label("Truth");
    	gp.add(truthLabel, column++, 0);
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
    private boolean isImageTallerThanWide(Image image){
    	double h = image.getHeight();
    	double w = image.getWidth();
    	if (h > w){
    		return true;
    	}
    	return false;
    }
    private double addScoredImageToGridPaneRowForCookingShow(String name, String trueName, String scoreValue, String scoreConf, String trueValue, GridPane gp, int row) throws AvatolCVException {
        // get the image
    	String thumbnailPathname = getThumbnailPathWithImageNameForCookingShow(name);
        Image image = new Image("file:"+thumbnailPathname);
        ImageView iv = new ImageView(image);
        if (isImageTallerThanWide(image)){
        	iv.setRotate(90);
        }
        
        int column = 0;
        gp.add(iv,column,row);
        column++;
        
        // get trainingVsTestConcern if relevant, OR image name
        Label itemLabel = new Label(trueName);
    	itemLabel.getStyleClass().add("columnValue");
        gp.add(itemLabel,column,row);
        column++;
     
        // get truth
        Label truthLabel = new Label(trueValue);
    	truthLabel.getStyleClass().add("columnValue");
        gp.add(truthLabel,column,row);
        column++;
        // get score
        
        Label scoreLabel = new Label(scoreValue);
    	scoreLabel.getStyleClass().add("columnValue");
        gp.add(scoreLabel, column, row);
        column++;
        // get confidence
        String trimmedScoreConf = limitToTwoDecimalPlaces(scoreConf);
        Label confidenceLabel = new Label(trimmedScoreConf);
    	confidenceLabel.getStyleClass().add("columnValue");
        gp.add(confidenceLabel,column, row);
        return image.getHeight();
    }
    
    public static String limitToTwoDecimalPlaces(String conf){
    	//assume it's always going to be 0.xyz, so just take the first four chars
    	if (conf.length() > 4){
    		return conf.substring(0, 4);
    	}
    	return conf;
    }
    private void addScoredImageToGridPaneRow(NormalizedImageInfo si, GridPane gp, int row) throws AvatolCVException {
        // get the image
        String thumbnailPath = getThumbailPath(si);
        Image image = new Image("file:"+thumbnailPath);
        ImageView iv = new ImageView(image);
        if (isImageTallerThanWide(image)){
        	iv.setRotate(90);
        }
        
        int column = 0;
        gp.add(iv,column,row);
        column++;
        
        // get trainingVsTestConcern if relevant, OR image name
        Label itemLabel = new Label("?");
        if (this.runSummary.hasTrainTestConcern()){
        	String trainingVsTestName = si.getTrainingVsTestName();
        	if (null != trainingVsTestName){
        		itemLabel.setText(trainingVsTestName);
        	}
        }
        else {
        	String imageName = si.getImageName();
        	itemLabel.setText(imageName);
        }
        gp.add(itemLabel,column,row);
        column++;
     
        // get truth
        String truth = si.getTruthValue(this.scoreIndex);
        Label truthLabel = new Label();
        if (null != truth){
            truthLabel.setText(truth);
        }
        else {
        	truthLabel.setText("?");
        }
        gp.add(truthLabel,column,row);
        column++;
        // get score
        String score = si.getScoreValue(this.scoreIndex);
        Label scoreLabel = new Label();
        if (null != score){
            scoreLabel.setText(score);
        }
        else {
        	scoreLabel.setText("?");
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
    private String getTrueImageNameFromImageName(String imageName) throws AvatolCVException {
    	String thumbnailDirPath = AvatolCVFileSystem.getNormalizedImagesThumbnailDir();
    	String[] imageNameParts = imageName.split("\\.");
    	String fileRoot = imageNameParts[0];
    	String[] fileRootParts = fileRoot.split("_");
    	String fileRootSansNumberSuffix = fileRootParts[0];
    	File thumbnailDir = new File(thumbnailDirPath);
    	File[] files = thumbnailDir.listFiles();
    	for (File f : files){
    		if (f.getName().startsWith(fileRootSansNumberSuffix)){
    			String name = f.getName();
    			String[] parts = name.split("_");
    			String trueName = parts[1];
    			return trueName;
    		}
    	}
    	return "?";
    }

    private String getTrueImageNameFromImageNameForCookingShow(String imageName) throws AvatolCVException {
    	String[] imageNameParts = imageName.split("\\.");
    	String fileRoot = imageNameParts[0];
    	return fileRoot;
    }
    private String getThumbnailPathWithImageName(String imageName) throws AvatolCVException {
    	String thumbnailDirPath = AvatolCVFileSystem.getNormalizedImagesThumbnailDir();
    	String[] imageNameParts = imageName.split("\\.");
    	String fileRoot = imageNameParts[0];
    	String[] fileRootParts = fileRoot.split("_");
    	String fileRootSansNumberSuffix = fileRootParts[0];
    	File thumbnailDir = new File(thumbnailDirPath);
    	File[] files = thumbnailDir.listFiles();
    	for (File f : files){
    		if (f.getName().startsWith(fileRootSansNumberSuffix)){
    			return f.getAbsolutePath();
    		}
    	}
    	return null;
    }
    private String getThumbnailPathWithImageNameForCookingShow(String imageName) throws AvatolCVException {
    	String thumbnailDirPath = AvatolCVFileSystem.getNormalizedImagesThumbnailDir();
    	String[] imageNameParts = imageName.split("\\.");
    	String fileRoot = imageNameParts[0];
    	File thumbnailDir = new File(thumbnailDirPath);
    	File[] files = thumbnailDir.listFiles();
    	for (File f : files){
    		String fname = f.getName();
    		if (fname.contains(fileRoot)){
    			return f.getAbsolutePath();
    		}
    	}
    	return null;
    }
    private void addTrainingImageToGridPaneRowForCookingShow(String imageName, String trueName, String value, GridPane gp, int row) throws AvatolCVException {
        // get the image
        //String thumbnailPath = getThumbailPath(si);
    	int column = 0;
        String thumbnailPathname = getThumbnailPathWithImageNameForCookingShow(imageName);
        if (null != thumbnailPathname){
        	Image image = new Image("file:"+thumbnailPathname);
            ImageView iv = new ImageView(image);
            if (isImageTallerThanWide(image)){
            	iv.setRotate(90);
            }
            
            gp.add(iv,column,row);
        }
        
        column++;
        
        // get trainingVsTestConcern if relevant, OR image name
        Label itemLabel = new Label(trueName);
    	itemLabel.getStyleClass().add("columnValue");
        
        //if (this.runSummary.hasTrainTestConcern()){
        //	String trainingVsTestName = si.getTrainingVsTestName();
        //	if (null != trainingVsTestName){
        //		itemLabel.setText(trainingVsTestName);
        //	}
        //}
        //else {
        	//String imageName = si.getImageName();
        //	itemLabel.setText(trueName);
        //}
        gp.add(itemLabel,column,row);
        column++;
     
        // get truth
        //String truth = si.getTruthValue(this.scoreIndex);
        Label truthLabel = new Label();
    	truthLabel.getStyleClass().add("columnValue");
        if (null != value){
            truthLabel.setText(value);
        }
        else {
        	truthLabel.setText("?");
        }
        gp.add(truthLabel,column,row);
        column++;
    }

    private void addTrainingImageToGridPaneRow(NormalizedImageInfo si, GridPane gp, int row) throws AvatolCVException {
        // get the image
        String thumbnailPath = getThumbailPath(si);
        Image image = new Image("file:"+thumbnailPath);
        ImageView iv = new ImageView(image);
        if (isImageTallerThanWide(image)){
        	iv.setRotate(90);
        }
        int column = 0;
        gp.add(iv,column,row);
        column++;
        
        // get trainingVsTestConcern if relevant, OR image name
        Label itemLabel = new Label("?");
        if (this.runSummary.hasTrainTestConcern()){
        	String trainingVsTestName = si.getTrainingVsTestName();
        	if (null != trainingVsTestName){
        		itemLabel.setText(trainingVsTestName);
        	}
        }
        else {
        	String imageName = si.getImageName();
        	itemLabel.setText(imageName);
        }
        gp.add(itemLabel,column,row);
        column++;
     
        // get truth
        String truth = si.getTruthValue(this.scoreIndex);
        Label truthLabel = new Label();
        if (null != truth){
            truthLabel.setText(truth);
        }
        else {
        	truthLabel.setText("?");
        }
        gp.add(truthLabel,column,row);
        column++;
    }
    private void setRunDetails(String runID) throws AvatolCVException {
        RunSummary rs = new RunSummary(runID);
        this.runSummary = rs;
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
        
        this.scoreIndex = new ScoreIndex(AvatolCVFileSystem.getScoreIndexPath(rs.getRunID()));
    }
    public void doneWithResultsReview(){
    	this.mainScreen.start(this.mainWindow);
    }
}
