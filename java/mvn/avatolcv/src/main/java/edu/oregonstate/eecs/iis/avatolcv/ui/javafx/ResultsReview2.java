package edu.oregonstate.eecs.iis.avatolcv.ui.javafx;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.HPos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Accordion;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.control.Tab;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import edu.oregonstate.eecs.iis.avatolcv.AvatolCVConstants;
import edu.oregonstate.eecs.iis.avatolcv.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.AvatolCVFileSystem;
import edu.oregonstate.eecs.iis.avatolcv.core.ImageInfo;
import edu.oregonstate.eecs.iis.avatolcv.core.TrainingInfoFile;
import edu.oregonstate.eecs.iis.avatolcv.datasource.BisqueDataSource;
import edu.oregonstate.eecs.iis.avatolcv.datasource.DataSource;
import edu.oregonstate.eecs.iis.avatolcv.datasource.FileSystemDataSource;
import edu.oregonstate.eecs.iis.avatolcv.datasource.MorphobankDataSource;
import edu.oregonstate.eecs.iis.avatolcv.datasource.UploadSession;
import edu.oregonstate.eecs.iis.avatolcv.datasource.UploadSession.UploadEvent;
import edu.oregonstate.eecs.iis.avatolcv.javafxui.AvatolCVExceptionExpresserJavaFX;
import edu.oregonstate.eecs.iis.avatolcv.javafxui.AvatolCVJavaFX;
import edu.oregonstate.eecs.iis.avatolcv.normalized.NormalizedKey;
import edu.oregonstate.eecs.iis.avatolcv.normalized.NormalizedValue;
import edu.oregonstate.eecs.iis.avatolcv.results.ResultsTable;
import edu.oregonstate.eecs.iis.avatolcv.results.ResultsTable2;
import edu.oregonstate.eecs.iis.avatolcv.results.SortableRow;
import edu.oregonstate.eecs.iis.avatolcv.scoring.HoldoutInfoFile;
import edu.oregonstate.eecs.iis.avatolcv.scoring.ScoresInfoFile;
import edu.oregonstate.eecs.iis.avatolcv.scoring.ScoringInfoFile;
import edu.oregonstate.eecs.iis.avatolcv.session.DatasetInfo;
import edu.oregonstate.eecs.iis.avatolcv.session.RunSummary;
import edu.oregonstate.eecs.iis.avatolcv.util.ClassicSplitter;

public class ResultsReview2 {
    public static final String COLNAME_IMAGE = "image";
    public static final String COLNAME_TRUTH = "truth";
    public static final String COLNAME_SCORE = "score";
    public static final String COLNAME_CONFIDENCE = "confidence";
    public static final String COLNAME_NAME = "name";
    public static final String COLNAME_TRAIN_TEST = "";
	public Slider thresholdSlider = null;
    public Accordion runDetailsAccordion = null;
    public ScrollPane runDetailsScrollPane = null;
    public ChoiceBox<String> runSelectChoiceBox = null;
    public Tab scoredImagesTab = null;
    public Tab trainingImagesTab = null;
    public Label runIDValue = null;
    public Label datasetValue = null;
    public Label scoringConcernValue = null;
    public Label dataSourceValue = null;
    public Label scoringAlgorithmValue = null;
    public GridPane overviewGridPane = null;
    public GridPane scoredImagesGridPane = null;
    public GridPane trainingImagesGridPane = null;
    public VBox trainingImagesVBox = null;
    public Button saveResultsButton = null;
    public Button undoSaveButton = null;
    public ProgressBar uploadProgress = null;
    private Stage mainWindow = null;
    private Scene scene = null;
    private String runID = null;
    private String runName = null;
    private String scoringMode = null;
    private RunSummary runSummary = null;
    private AvatolCVJavaFX mainScreen = null;
    private ResultsTable2 resultsTable2 = null;
    private ResultsTable2 trainingTable = null;
    private String currentThresholdString  = "?";
    private TrainingInfoFile tif = null;
    private ScoresInfoFile sif = null;
    private UploadSession uploadSession = null;
    private DataSource dataSource = null;
    private Hashtable<String,Label> scoreLabelForImageIDHash = null;
    public ResultsReview2(){
    }
    public void init(AvatolCVJavaFX mainScreen, Stage mainWindow, String runName) throws AvatolCVException {
        
        this.mainWindow = mainWindow;
        this.mainScreen = mainScreen;
        this.runName = runName;
        this.runID = RunSummary.getRunIDFromRunSummaryName(runName);
        if (this.runID == null || "".equals(this.runID)){
        	throw new AvatolCVException("null runID cannot be rendered in results viewer");
        }
        
        initUI();
        enableUndoUploadButtonIfAppropriate();
    }
    private void enableUndoUploadButtonIfAppropriate(){
        int uploadSessionNumber = this.uploadSession.getUploadSessionNumber();
        if (uploadSessionNumber == 0){
            this.undoSaveButton.setDisable(true);
            this.undoSaveButton.setText("Undo Upload");
        }
        else {
            this.undoSaveButton.setDisable(false);
            this.undoSaveButton.setText("Undo Upload " + uploadSessionNumber);
        }
    }
    public void initOnAppThread(AvatolCVJavaFX mainScreen, Stage mainWindow, String runName){
    	ApplicationThreadResultsReviewInit atrri = new ApplicationThreadResultsReviewInit(mainScreen, mainWindow, runName);
    	Platform.runLater(atrri);
    }
    public class ApplicationThreadResultsReviewInit implements Runnable {
    	AvatolCVJavaFX mainScreen = null;
    	Stage mainWindow = null;
    	String runName = null;
    	public ApplicationThreadResultsReviewInit(AvatolCVJavaFX mainScreen, Stage mainWindow, String runName){
    		this.mainScreen = mainScreen;
    		this.mainWindow = mainWindow;
    		this.runName = runName;
    	}
		@Override
		public void run() {
			try {
				init(mainScreen, mainWindow, runName);
			}
			catch(AvatolCVException ace){
				AvatolCVExceptionExpresserJavaFX.instance.showException(ace, "problem starting ResultsReview: " + ace.getMessage());
			}
		}
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
            initializePriorRunChoices();
            runSelectChoiceBox.getSelectionModel().selectedIndexProperty().addListener(new RunChoiceChangeListener(runSelectChoiceBox, this.mainScreen, this.mainWindow));
            setRunDetails(this.runName);
            this.uploadSession = new UploadSession();
            setScoredImagesInfo(this.runID, scoringConcernValue.getText());
            //runDetailsAccordion.requestLayout();
            setupSlider();
            //scoredImagesGridPane.setStyle("-fx-background-color:yellow;");
            
        }
        catch(Exception e){
            throw new AvatolCVException(e.getMessage(),e);
        }
    }
    
    public class RunChoiceChangeListener implements ChangeListener<Number> {
        private ChoiceBox<String> cb;
        private AvatolCVJavaFX mainScreen;
        private Stage mainWindow;
        public RunChoiceChangeListener(ChoiceBox<String> cb,AvatolCVJavaFX mainScreen, Stage mainWindow){
            this.cb = cb;
            this.mainScreen = mainScreen;
            this.mainWindow = mainWindow;
        }
        @Override
        public void changed(ObservableValue ov, Number value, Number newValue) {
            String newRunID = AvatolCVConstants.UNDETERMINED;
            try {
                newRunID =(String)cb.getItems().get((Integer)newValue);
                init(mainScreen, mainWindow, newRunID);
            }
            catch(Exception e){
                AvatolCVExceptionExpresserJavaFX.instance.showException(e, "Problem changing to results for runID " + newRunID + " " + e.getMessage());
            }
        }
    }
    private void initializePriorRunChoices() throws AvatolCVException {
        List<String> names = AvatolCVFileSystem.getSessionFilenames();
        Collections.sort(names);
        Collections.reverse(names);
        for (String name : names){
            runSelectChoiceBox.getItems().add(name);
        }
        String currentRunName = "";
        for (String name : names){
            if (name.startsWith(this.runID)){
                currentRunName = name;
            }
        }
        runSelectChoiceBox.setValue(currentRunName);
        runSelectChoiceBox.requestLayout();
    }
    private boolean isEvaluationMode(){
    	if (RunSummary.SCORING_MODE_VALUE_EVALUATION_MODE.equals(this.scoringMode)){
    		return true;
    	}
    	return false;
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
    public void disableNodes(List<Node> nodes, boolean disable){
        for (Node n : nodes){
            if (null != n){
                n.setDisable(disable);
            }
        }
    }
    private void disableAllUnderThreshold(String threshold){
        currentThresholdString = threshold;
    	List<String> imageIDs = resultsTable2.getImageIDsInCurrentOrder();
    	
    	for (String imageID : imageIDs){
    	    List<Node> nodesInRow = new ArrayList<Node>();
    	    Node confNode = resultsTable2.getWidget(imageID, COLNAME_CONFIDENCE);
    	    nodesInRow.add(confNode);
            Node nameNode = resultsTable2.getWidget(imageID, COLNAME_NAME);
            nodesInRow.add(nameNode);
            Node scoreNode = resultsTable2.getWidget(imageID, COLNAME_SCORE);
            nodesInRow.add(scoreNode);
    	    if (isEvaluationMode()){
    	        Node truthNode = resultsTable2.getWidget(imageID, COLNAME_TRUTH);
                nodesInRow.add(truthNode);
    	    }
    	    if (this.runSummary.hasTrainTestConcern()){
    	        Node ttNode = resultsTable2.getWidget(imageID, COLNAME_TRAIN_TEST);
                nodesInRow.add(ttNode);
    	    }
    	    String confString = resultsTable2.getValue(imageID, COLNAME_CONFIDENCE);
    	    
    	    Double confDouble = new Double(confString);
            Double threshDouble = new Double(threshold);
            if (confDouble.doubleValue() < threshDouble.doubleValue()){
                disableNodes(nodesInRow, true);
            }
            else {
                disableNodes(nodesInRow, false);
            }
    	}
    }
    private void addEventhandlerForImageClick(ImageView iv, ResultsTable2 rt, String imageID, GridPane gp){
        iv.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (rt.isLargeImageShown(imageID)){
                    hideLargeImage(imageID);
                }
                else {
                    int targetRowIndex = rt.getTargetRowForLargeImage(imageID);
                    showLargeImage(rt.getValue(imageID, COLNAME_IMAGE),targetRowIndex);
                }
                event.consume();
            }
            private void showLargeImage(String thumbnailPath, int targetRowIndex){
                try {
                    String largeImagePath = AvatolCVFileSystem.getLargeImagePathForThumbnailPath(thumbnailPath);
                    //System.out.println("put big image " + largeImagePath + " at index " + targetIndex);
                    Image image = new Image("file:"+largeImagePath);
                    ImageView iv = new ImageView(image);
                    iv.setPreserveRatio(true);
                    iv.setFitWidth(600);
                    rt.addLargeImage(imageID, iv);
                    gp.add(iv, 0, targetRowIndex, 5, 1);
                }
                catch(Exception e){
                    AvatolCVExceptionExpresserJavaFX.instance.showException(e, "problem trying to show image");
                }
            }
            private void hideLargeImage(String imageID){
                ImageView iv = (ImageView)rt.forgetLargeImageObject(imageID);
                gp.getChildren().remove(iv);
            }
       });
    }
   
    private void addEventhandlerForColumnSort(Label columnHeader, ResultsTable2 rt, GridPane gp, List<String> colNames){
        columnHeader.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                String colName = columnHeader.getText();
                try {
                    rt.sortOnColumn(colName);
                    renderTable(rt, gp, colNames);
                }
                catch(AvatolCVException ace){
                    System.out.println("could not sort column " + ace.getMessage());
                }
                event.consume();
            }
       });
    }
    public List<String> getActiveScoreColumns(){
        List<String> result = new ArrayList<String>();
        result.add(COLNAME_IMAGE);
        if (isEvaluationMode()){
            result.add(COLNAME_TRUTH); 
        }
        result.add(COLNAME_SCORE);
        result.add(COLNAME_CONFIDENCE);
        if (this.runSummary.hasTrainTestConcern()){
            result.add(COLNAME_TRAIN_TEST);
        }
        result.add(COLNAME_NAME);
        return result;
    }
    public List<String> getTrainingColumns(){
        List<String> result = new ArrayList<String>();
        result.add(COLNAME_IMAGE);
        result.add(COLNAME_SCORE);
        result.add(COLNAME_NAME);
        return result;
    }
    
    private void renderTable(ResultsTable2 rt, GridPane gp, List<String> colNames){
        gp.getChildren().clear();
        int headerColumnIndex = 0;
        for (String colName : colNames){
            Label label = new Label(colName);
            label.getStyleClass().add("columnHeader");
            GridPane.setHalignment(label, HPos.CENTER);
            gp.add(label, headerColumnIndex++, 0);
            addEventhandlerForColumnSort(label, rt, gp, colNames);
        }
        List<String> imageIDs = rt.getImageIDsInCurrentOrder();
        int rowIndex = 1;
        for (String imageID: imageIDs){
            int columnIndex = 0;
            for (String colName : colNames){
                Node widget = rt.getWidget(imageID, colName);
                gp.add(widget, columnIndex++, rowIndex);
            }
            rowIndex+=2;
        }
        //scoredImagesGridPane.setGridLinesVisible(true);
        ensureConstraintsForGridPane(gp);
    }
    private void ensureConstraintsForGridPane(GridPane gp){
        ObservableList<ColumnConstraints> colConstraints = gp.getColumnConstraints();
        for (ColumnConstraints cc : colConstraints){
            cc.setHgrow(Priority.SOMETIMES);
        }
        ObservableList<RowConstraints> rowConstraints = gp.getRowConstraints();
        for (RowConstraints rc : rowConstraints){
            rc.setVgrow(Priority.NEVER);
        }
    }
    private void setScoredImagesInfo(String runID, String scoringConcernName) throws AvatolCVException {
    	scoreLabelForImageIDHash = new Hashtable<String, Label>();
    	scoredImagesTab.setText(scoringConcernName + " - SCORED images");
    	trainingImagesTab.setText(scoringConcernName + " - TRAINING images");

    	//scoredImagesGridPane.getChildren().clear();
    	//addScoredImagesHeader(scoredImagesGridPane);
    	
    	String scoringFilePath = AvatolCVFileSystem.getScoringFilePath(runID, scoringConcernName);
        ScoringInfoFile scoringInfoFile = new ScoringInfoFile(scoringFilePath);
        
    	String scoreFilePath = AvatolCVFileSystem.getScoreFilePath(runID, scoringConcernName);
    	sif = new ScoresInfoFile(scoreFilePath);
    	
    	String trainingFilePath = AvatolCVFileSystem.getTrainingFilePath(runID, scoringConcernName);
    	tif = new TrainingInfoFile(trainingFilePath);
    	
    	HoldoutInfoFile hif = null;
    	if (isEvaluationMode()){
    	    String holdoutFilePath = AvatolCVFileSystem.getHoldoutFilePath(runID, scoringConcernName);
            hif = new HoldoutInfoFile(holdoutFilePath);
    	}
    	resultsTable2 = new ResultsTable2();
    	//rt.addValueForColumn("image1","colA","valA1");
    	//rt.addWidgetForColumn("image1","colA",objA1);
    	//rt.getImageIDsInCurrentOrder()
    	//rt.getValue("image1","colB")
    	//rt.getWidget("image1","colB")
    	List<String> scoringImagePaths = sif.getImagePaths();
    	//int row = 1;
    	for (String path : scoringImagePaths){
    		String normalizedScoringConcernValue = sif.getScoringConcernValueForImagePath(path);
    		String imageID = ImageInfo.getImageIDFromPath(path);
    		
    		resultsTable2.addValueForColumn(imageID, COLNAME_SCORE, normalizedScoringConcernValue);
    		String scoringConcernValue = new NormalizedValue(normalizedScoringConcernValue).getName();
    		Label scoreLabel = new Label(scoringConcernValue);
    		scoreLabel.getStyleClass().add("columnValue");
    		scoreLabelForImageIDHash.put(imageID, scoreLabel);
    		resultsTable2.addWidgetForColumn(imageID, COLNAME_SCORE, scoreLabel);
    		
    		System.out.println("getting confidence for ImageValue path(key) and value: " + path + ";" + scoringConcernValue);
    		String conf = sif.getConfidenceForImageValue(path, normalizedScoringConcernValue);
    		String trimmedScoreConf = limitToTwoDecimalPlaces(conf);
    		resultsTable2.addValueForColumn(imageID, COLNAME_CONFIDENCE, trimmedScoreConf);
    		Label confLabel = new Label(trimmedScoreConf);
    		confLabel.getStyleClass().add("columnValue");
            resultsTable2.addWidgetForColumn(imageID, COLNAME_CONFIDENCE, confLabel);
            
    		String truth = null;
    		if (isEvaluationMode()){
    			truth = hif.getScoringConcernValueForImagePath(path);
    			resultsTable2.addValueForColumn(imageID, COLNAME_TRUTH, truth);
    			Label truthLabel = new Label(truth);
    			truthLabel.getStyleClass().add("columnValue");
                resultsTable2.addWidgetForColumn(imageID, COLNAME_TRUTH, truthLabel);
    		}
    		
        	String origImageName = getTrueImageNameFromImagePathForCookingShow(path);
        	resultsTable2.addValueForColumn(imageID, COLNAME_NAME, origImageName);
        	Label nameLabel = new Label(origImageName);
        	nameLabel.getStyleClass().add("columnValue");
            resultsTable2.addWidgetForColumn(imageID, COLNAME_NAME, nameLabel);
            
        	String thumbnailPathname = getThumbnailPathWithImagePathForCookingShow(path);
        	resultsTable2.addValueForColumn(imageID, COLNAME_IMAGE, thumbnailPathname);
        	Image image = new Image("file:"+thumbnailPathname);
            ImageView iv = new ImageView(image);
            //addEventhandlerForImageClick(iv, sr);
            resultsTable2.addWidgetForColumn(imageID, COLNAME_IMAGE, iv);
            addEventhandlerForImageClick(iv, resultsTable2, imageID,scoredImagesGridPane);
        	
        	if (this.runSummary.hasTrainTestConcern()){
        	    String trainTestConcernValue = scoringInfoFile.getTrainTestConcernValueForImageID(ImageInfo.getImageIDFromPath(path)).getName();
        	    resultsTable2.addValueForColumn(imageID, COLNAME_TRAIN_TEST, trainTestConcernValue);
        	    Label trainTestLabel = new Label(trainTestConcernValue);
        	    trainTestLabel.getStyleClass().add("columnValue");
                resultsTable2.addWidgetForColumn(imageID, COLNAME_TRAIN_TEST, trainTestLabel);
            }
    	}
    	resultsTable2.sortOnColumn(COLNAME_IMAGE);
    	renderTable(resultsTable2, scoredImagesGridPane, getActiveScoreColumns());

    	trainingTable = new ResultsTable2();
    	//addTrainingImagesHeader(trainingImagesGridPane);
    	List<String> imagePaths = tif.getImagePaths();
    	
    	for (String path : imagePaths){
    		//System.out.println("got image " + name);
    		if (!sif.hasImage(path)){
    		    String imageID = ImageInfo.getImageIDFromPath(path);
    		    
    			String normalizedScoringConcernValue = tif.getScoringConcernValueForImagePath(path);
                String value = new NormalizedValue(normalizedScoringConcernValue).getName();
    			trainingTable.addValueForColumn(imageID, COLNAME_SCORE, value);
    			Label valueLabel = new Label(value);
    			valueLabel.getStyleClass().add("columnValue");
    			trainingTable.addWidgetForColumn(imageID, COLNAME_SCORE, valueLabel);
    			
        		String trueName = getTrueImageNameFromImagePathForCookingShow(path);
        		trainingTable.addValueForColumn(imageID, COLNAME_NAME, trueName);
        		Label nameLabel = new Label(trueName);
                nameLabel.getStyleClass().add("columnValue");
                trainingTable.addWidgetForColumn(imageID, COLNAME_NAME, nameLabel);
                
                String thumbnailPathname = AvatolCVFileSystem.getThumbnailPathForImagePath(path);
                trainingTable.addValueForColumn(imageID, COLNAME_IMAGE, thumbnailPathname);
                Image image = new Image("file:"+thumbnailPathname);
                ImageView iv = new ImageView(image);
                //addEventhandlerForImageClick(iv, sr);
                trainingTable.addWidgetForColumn(imageID, COLNAME_IMAGE, iv);
                addEventhandlerForImageClick(iv, trainingTable, imageID, trainingImagesGridPane);
                
        		//addTrainingImageToGridPaneRowForCookingShow(path, trueName, value, trainingImagesGridPane, row++);
    		}
    	}
    	trainingTable.sortOnColumn(COLNAME_IMAGE);
    	renderTable(trainingTable, trainingImagesGridPane, getTrainingColumns());
        
    	trainingImagesGridPane.requestLayout();
        scoredImagesGridPane.requestLayout();
    }
  
    private boolean isImageTallerThanWide(Image image){
    	double h = image.getHeight();
    	double w = image.getWidth();
    	if (h > w){
    		return true;
    	}
    	return false;
    }

    public static String limitToTwoDecimalPlaces(String conf){
        Double confDouble = new Double(conf);
        return String.format("%.2f", confDouble);
    }
   
  
    private String getTrueImageNameFromImagePathForCookingShow(String imagePath) throws AvatolCVException {
        String imageDirPath = AvatolCVFileSystem.getNormalizedImagesLargeDir();
        String imageID = ImageInfo.getImageIDFromPath(imagePath);
        File imageDir = new File(imageDirPath);
        File[] files = imageDir.listFiles();
        for (File f : files){
            String fname = f.getName();
            if (fname.contains(imageID)){
                return fname;
            }
        }
        return null;
    }
  
    
    private String getThumbnailPathWithImagePathForCookingShow(String imagePath) throws AvatolCVException {
    	String thumbnailDirPath = AvatolCVFileSystem.getNormalizedImagesThumbnailDir();
        String imageID = ImageInfo.getImageIDFromPath(imagePath);
    	File thumbnailDir = new File(thumbnailDirPath);
    	File[] files = thumbnailDir.listFiles();
    	for (File f : files){
    		String fname = f.getName();
    		if (fname.contains(imageID)){
    			return f.getAbsolutePath();
    		}
    	}
    	return null;
    }
   

    private void setRunDetails(String runName) throws AvatolCVException {
        RunSummary rs = RunSummary.loadSummary(runName);
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
        scoringMode = rs.getScoringMode();
        
        List<String> values = rs.getScoringConcernValues();
        int row = 7;
        for (String v : values){
            Label label = new Label(v);
            label.getStyleClass().add("summaryValue");
            overviewGridPane.add(label, 1, row++);
        }
        //
        //this.scoreIndex = new ScoreIndex(AvatolCVFileSystem.getScoreIndexPath(rs.getRunID()));
    }
    public void doneWithResultsReview(){
    	this.mainScreen.start(this.mainWindow);
    }
    private DataSource getDataSourceForRun(){
        DataSource ds = null;
        String dataSourceName = this.runSummary.getDataSource();
        if ("bisque".equals(dataSourceName)){
            ds = new BisqueDataSource(); 
        } 
        else if ("morphobank".equals(dataSourceName)){
            ds = new MorphobankDataSource();
        }
        else {
            ds = new FileSystemDataSource();
        }
        return ds;
    }
    public void doUndoSaveResults(){
        Thread t = new Thread(() -> undoSaveResults());
        t.start();
    }
    public void undoSaveResults(){
        try {
            if (null == this.dataSource){
                this.dataSource = getDataSourceForRun();
            }
            boolean authenticated = false;
            if (this.dataSource.isAuthenticated()){
                authenticated = true;
            }
            else {
                authenticated = authenticate();
            }
            if (!authenticated){
                return;
            }
            Platform.runLater(() -> uploadProgress.setProgress(0.0));
            List<UploadEvent> events = this.uploadSession.getEventsForUndo();
            double count = events.size();
            double percentProgressPerEvent = 1 / count;
            int curEvent = 0;
            for (UploadEvent event : events){
                curEvent++;
                if (event.wasNewKey()){
                    // for now, since there's no web service to remove a key (true?), just do the revise
                    this.dataSource.reviseValueForKey(event.getImageID(), event.getKey(), event.getOrigValue());
                }
                else {
                    this.dataSource.reviseValueForKey(event.getImageID(), event.getKey(), event.getOrigValue());
                }
                double percentDone = percentProgressPerEvent * curEvent;
                Platform.runLater(() -> uploadProgress.setProgress(percentDone));
            }
            this.uploadSession.forgetEvents(events);
            
            // check to see if any scoreLabels should still reflect being uploaded
            for (UploadEvent event : events){
                String imageID = event.getImageID();
                Label scoreLabel = scoreLabelForImageIDHash.get(imageID);
                Platform.runLater(() -> scoreLabel.getStyleClass().remove("uploaded"));
            }
            
            Platform.runLater(() -> enableUndoUploadButtonIfAppropriate());
        }
        catch(AvatolCVException ace){
            AvatolCVExceptionExpresserJavaFX.instance.showException(ace, "problem trying to undo save results");
        }
        
    }
    private boolean authenticate() throws AvatolCVException {
        LoginDialog dialog = new LoginDialog();
        dialog.display(this.dataSource.getName());
        String username = dialog.getLogin();
        String password = dialog.getPword();
        return this.dataSource.authenticate(username, password);
    }
    public void doSaveResults(){
        Thread t = new Thread(() -> saveResults());
        t.start();
    }
    public boolean isConfidenceStringLessThanThreshold(String confString){
        Double confDouble = new Double(confString);
        Double threshDouble = new Double(currentThresholdString);
        if (confDouble.doubleValue() < threshDouble.doubleValue()){
            return true;
        }
        return false;
    }
    public void saveResults(){
        try {
            if (null == this.dataSource){
                this.dataSource = getDataSourceForRun();
            }
            boolean authenticated = false;
            if (this.dataSource.isAuthenticated()){
                authenticated = true;
            }
            else {
                authenticated = authenticate();
            }
            if (!authenticated){
                return;
            }
            
            int imageNameIndex = ResultsTable.getIndexOfColumn(ResultsTable.COLNAME_NAME);
            // list all the answers above threshold
            List<String> imageIDs = resultsTable2.getImageIDsInCurrentOrder();
            double rowToUploadCount = 0;
            for (String imageID : imageIDs){
                String confString = resultsTable2.getValue(imageID, COLNAME_CONFIDENCE);
                if (isConfidenceStringLessThanThreshold(confString)){
                    rowToUploadCount++;
                }
            }
           
            Platform.runLater(() -> uploadProgress.setProgress(0.0));
            double percentProgressPerRow = 1 / rowToUploadCount;
            int rowCount = 0;
            uploadSession.nextSession();
            for (String imageID : imageIDs){
                String confString = resultsTable2.getValue(imageID, COLNAME_CONFIDENCE);
                
                //Label scoreLabel = (Label)row.getWidget(ResultsTable.COLNAME_SCORE);
                if (isConfidenceStringLessThanThreshold(confString)){
                	rowCount++;
                    //String value = scoreChoice.getValue();
                    String value = resultsTable2.getValue(imageID, COLNAME_SCORE);
                    String name = resultsTable2.getValue(imageID, COLNAME_NAME);
                    System.out.println(name + "  -  " + value);
       
                    NormalizedKey normCharKey = tif.getNormalizedCharacter();
                    NormalizedKey trainTestConcern = tif.getTrainTestConcernForImageID(imageID);
                    NormalizedValue trainTestConcernValue = tif.getTrainTestConcernValueForImageID(imageID);
                    NormalizedValue newValue = sif.getScoreValueForImageID(imageID);
                    //Need to pass the normalized key and value for this row to Data source and ask if key exists for this image
                    NormalizedValue existingValueForKey = dataSource.getValueForKeyAtDatasourceForImage(normCharKey, imageID, trainTestConcern, trainTestConcernValue);
                    Node scoreLabel = resultsTable2.getWidget(imageID, COLNAME_SCORE);
                    if (null == existingValueForKey){
                        //add score
                        boolean result = dataSource.addKeyValue(imageID, normCharKey, newValue);
                        if (result){
                        	this.uploadSession.addNewKeyValue(imageID, normCharKey, newValue);
                        	Platform.runLater(() -> scoreLabel.getStyleClass().add("uploaded"));
                        }
                        else {
                        	Platform.runLater(() -> dialog("cannot upload to add score for image " + name));
                        }
                        
                    }
                    else {
                        // revise score
                        boolean result = dataSource.reviseValueForKey(imageID, normCharKey, newValue);
                        if (result){
                        	this.uploadSession.reviseValueForKey(imageID, normCharKey, newValue, existingValueForKey);
                        	Platform.runLater(() -> scoreLabel.getStyleClass().add("uploaded"));
                        }
                        else {
                        	Platform.runLater(() -> dialog("cannot upload to revise score for image " + name));
                        }
                    }
                    double percentDone = percentProgressPerRow * rowCount;
                    
                    
                    Platform.runLater(() -> uploadProgress.setProgress(percentDone));
                }
            }
            this.uploadSession.persist();
            Platform.runLater(() -> enableUndoUploadButtonIfAppropriate());
        }
        catch(AvatolCVException ace){
            AvatolCVExceptionExpresserJavaFX.instance.showException(ace, "problem trying to save results");
        }
    }
    private void dialog(String text){
		Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Error Dialog");
        alert.setHeaderText("AvatolCV error");
        alert.setContentText(text);
        alert.showAndWait();
	}
}

