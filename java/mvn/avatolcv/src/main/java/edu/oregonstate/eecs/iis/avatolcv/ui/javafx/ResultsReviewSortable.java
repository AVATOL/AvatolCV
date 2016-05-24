package edu.oregonstate.eecs.iis.avatolcv.ui.javafx;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import edu.oregonstate.eecs.iis.avatolcv.AvatolCVConstants;
import edu.oregonstate.eecs.iis.avatolcv.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.AvatolCVFileSystem;
import edu.oregonstate.eecs.iis.avatolcv.core.ImageInfo;
import edu.oregonstate.eecs.iis.avatolcv.core.TrainingInfoFile;
import edu.oregonstate.eecs.iis.avatolcv.datasource.BisqueDataSource;
import edu.oregonstate.eecs.iis.avatolcv.datasource.DataSource;
import edu.oregonstate.eecs.iis.avatolcv.datasource.DataSourceUtils;
import edu.oregonstate.eecs.iis.avatolcv.datasource.FileSystemDataSource;
import edu.oregonstate.eecs.iis.avatolcv.datasource.MorphobankDataSource;
import edu.oregonstate.eecs.iis.avatolcv.datasource.PointAnnotations;
import edu.oregonstate.eecs.iis.avatolcv.datasource.PointAnnotations.Annotation;
import edu.oregonstate.eecs.iis.avatolcv.datasource.UploadSession;
import edu.oregonstate.eecs.iis.avatolcv.datasource.UploadSession.UploadEvent;
import edu.oregonstate.eecs.iis.avatolcv.javafxui.AvatolCVExceptionExpresserJavaFX;
import edu.oregonstate.eecs.iis.avatolcv.javafxui.AvatolCVJavaFX;
import edu.oregonstate.eecs.iis.avatolcv.javafxui.FXUtilities;
import edu.oregonstate.eecs.iis.avatolcv.normalized.NormalizedKey;
import edu.oregonstate.eecs.iis.avatolcv.normalized.NormalizedValue;
import edu.oregonstate.eecs.iis.avatolcv.normalized.PointAsPercent;
import edu.oregonstate.eecs.iis.avatolcv.results.ResultsTableSortable;
import edu.oregonstate.eecs.iis.avatolcv.results.ResultsUtils;
import edu.oregonstate.eecs.iis.avatolcv.results.ScoreItem;
import edu.oregonstate.eecs.iis.avatolcv.results.ScoreItem.ScoringFate;
import edu.oregonstate.eecs.iis.avatolcv.results.ScoringProvenanceInfo;
import edu.oregonstate.eecs.iis.avatolcv.results.UploadVoter;
import edu.oregonstate.eecs.iis.avatolcv.scoring.HoldoutInfoFile;
import edu.oregonstate.eecs.iis.avatolcv.scoring.ScoresInfoFile;
import edu.oregonstate.eecs.iis.avatolcv.scoring.ScoringInfoFile;
import edu.oregonstate.eecs.iis.avatolcv.session.DatasetInfo;
import edu.oregonstate.eecs.iis.avatolcv.session.ProgressPresenter;
import edu.oregonstate.eecs.iis.avatolcv.session.RunSummary;
import edu.oregonstate.eecs.iis.avatolcv.steps.ScoringRunStep;
import edu.oregonstate.eecs.iis.avatolcv.util.ClassicSplitter;

public class ResultsReviewSortable implements ProgressPresenter {
    public static final String STATUS_UNSAVED = "no";
    public static final String COLNAME_IMAGE = "image";
    public static final String COLNAME_TRUTH = "truth";
    public static final String COLNAME_SCORE = "score";
    public static final String COLNAME_CONFIDENCE = "confidence";
    public static final String COLNAME_NAME = "name";
    public static final String COLNAME_SAVE_STATUS = "saved?";
    private static final String UPLOADED_CSS_STYLE = "uploaded";
    //public static final String COLNAME_TRAIN_TEST = "";
    //private static final int CROSSHAIR_RADIUS_THUMBNAIL = 4;
    //private static final int CROSSHAIR_RADIUS_LARGE = 20;
    private static final int LARGE_IMAGE_FIT_WIDTH = 600;
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
    private ResultsTableSortable resultsTable2 = null;
    private ResultsTableSortable trainingTable = null;
    private String currentThresholdString  = AvatolCVConstants.UNDETERMINED;
    private TrainingInfoFile tif = null;
    private ScoresInfoFile sif = null;
    private UploadSession uploadSession = null;
    private DataSource dataSource = null;
    private Hashtable<String,Label> scoreLabelForImageIDHash = null;
    private Hashtable<String,Label> saveStatusLabelForImageIDHash = null;
    private String username = null;
    private String password = null;
    ScoringInfoFile scoringInfoFile = null;
    private static final Logger logger = LogManager.getLogger(ResultsReviewSortable.class);

    public ResultsReviewSortable(){
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
            runSelectChoiceBox.getSelectionModel().select(this.runName);
            runSelectChoiceBox.getSelectionModel().selectedIndexProperty().addListener(new RunChoiceChangeListener(runSelectChoiceBox, this.mainScreen, this.mainWindow));
            setRunDetails(this.runName);
            this.uploadSession = new UploadSession(this.runName);
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
                cb.getSelectionModel().select(newRunID);
                uploadSession = new UploadSession(newRunID);
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
    private void setupSlider() throws AvatolCVException{
    	double initValue = thresholdSlider.getValue();
    	adjustConfidencesToThreshold(initValue);
    	thresholdSlider.valueProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<? extends Number> ov,
                Number old_val, Number new_val) {
                    try {
                        adjustConfidencesToThreshold(new_val.doubleValue());
                    }
            	    catch(AvatolCVException ace){
            	        System.out.println("could not setup slider " + ace.getMessage());
            	    }
            }
        });
    }
    public void adjustConfidencesToThreshold(double value) throws AvatolCVException{
	    double newValPercent = value/100;
        String newValString = "" + newValPercent;
        String twoDecimalString = ResultsUtils.limitToTwoDecimalPlaces(newValString);
	    disableAllUnderThreshold(twoDecimalString);
    }
    public void disableNodes(List<Node> nodes, boolean disable){
        for (Node n : nodes){
            if (null != n){
                n.setDisable(disable);
            }
        }
    }
    private void disableAllUnderThreshold(String threshold) throws AvatolCVException {
        currentThresholdString = threshold;
    	List<String> imageIDs = resultsTable2.getImageIDsInCurrentOrder();
    	
    	for (String imageID : imageIDs){
    	    List<Node> nodesInRow = new ArrayList<Node>();
    	    Node confNode = (Node)resultsTable2.getWidget(imageID, COLNAME_CONFIDENCE);
    	    nodesInRow.add(confNode);
            Node nameNode = (Node)resultsTable2.getWidget(imageID, COLNAME_NAME);
            nodesInRow.add(nameNode);
            Node scoreNode = (Node)resultsTable2.getWidget(imageID, COLNAME_SCORE);
            nodesInRow.add(scoreNode);
            Node saveStatusNode = (Node)resultsTable2.getWidget(imageID, COLNAME_SAVE_STATUS);
            nodesInRow.add(saveStatusNode);
    	    if (isEvaluationMode()){
    	        Node truthNode = (Node)resultsTable2.getWidget(imageID, COLNAME_TRUTH);
                nodesInRow.add(truthNode);
    	    }
    	    if (this.runSummary.hasTrainTestConcern()){
    	        Node ttNode = (Node)resultsTable2.getWidget(imageID, getTrainTestHeader());
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
    private void addEventhandlerForImageClick(ImageView iv, ResultsTableSortable rt, String imageID, GridPane gp, PointAnnotations pointAnnotations){
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
                    iv.setFitWidth(LARGE_IMAGE_FIT_WIDTH);
                    int boundWidth = (int)iv.getBoundsInParent().getWidth();
                    int boundHeight = (int)iv.getBoundsInParent().getHeight();
                    //System.out.println("boundWidth " + boundWidth + " boundHeight " + boundHeight);
                    AnchorPane ap = new AnchorPane();
                    ap.getChildren().add(iv);
                    drawCoordinates(ap, pointAnnotations, boundWidth, boundHeight);
                    gp.add(ap, 0, targetRowIndex, 5, 1);
                    rt.addLargeImage(imageID, ap);
                }
                catch(Exception e){
                    AvatolCVExceptionExpresserJavaFX.instance.showException(e, "problem trying to show image");
                }
            }
            private void hideLargeImage(String imageID){
                AnchorPane ap = (AnchorPane)rt.forgetLargeImageObject(imageID);
                gp.getChildren().remove(ap);
            }
       });
    }
    public void drawCoordinates(AnchorPane ap, PointAnnotations pointAnnotations, int imageWidthInPixels, int imageHeightInPixels){
    	if (pointAnnotations == null){
    		// ignore
    	}
    	List<Annotation> annotations = pointAnnotations.getAnnotations();
    	for (Annotation annotation : annotations){
    	    if (annotation.getType() == PointAnnotations.AnnotationType.NONE){
                // ignore
            }
            else if (annotation.getType() == PointAnnotations.AnnotationType.POINT){
                drawPointAnnotation(ap, annotation, imageWidthInPixels, imageHeightInPixels);
            }
            else if (annotation.getType() == PointAnnotations.AnnotationType.BOX){
                System.out.println("ERROR - BOX annotation rendering not yet implemented");
            }
            else if (annotation.getType() == PointAnnotations.AnnotationType.POLYGON) {
                System.out.println("ERROR - POLYGON annotation rendering not yet implemented");
            }
            else {
                System.out.println("ERROR Unknown AnnotationCoordinates.AnnotationType encountered - expected BOX, POINT, or POLYGON");
            }
    	} 
    }
    public void drawPointAnnotation(AnchorPane ap, Annotation annotation, int imageWidthInPixels, int imageHeightInPixels){
    	List<PointAsPercent> pointsAsPercent = annotation.getPoints();
    	PointAsPercent pap = pointsAsPercent.get(0);
    	//System.out.println("x " + " y " + " xPixel " + " yPixel " + pap.getYPixel(imageHeightInPixels)
    	List<Line> lines = getCrosshairs(pap, imageWidthInPixels, imageHeightInPixels);
    	for (Line line : lines){
    		ap.getChildren().add(line);
    	}
    }
    public static List<Line> getCrosshairs(PointAsPercent pap,int imageWidthInPixels, int imageHeightInPixels){
    	List<Line> lines = new ArrayList<Line>();
    	int x = pap.getXPixel(imageWidthInPixels);
    	int y = pap.getYPixel(imageHeightInPixels);
    	// horizontal line
    	int crosshairRadius = (int)((double)(imageWidthInPixels/20.0));
    	int x1a = x - crosshairRadius;
    	if (x1a < 0){
    		x1a = 0;
    	}
    	int x1b = x + crosshairRadius;
    	int y1a = y;
    	int y1b = y;
    	Line line1 = new Line(x1a,y1a,x1b,y1b);
    	styleCrosshairLine(line1);
    	lines.add(line1);
    	// vertical line
    	int x2a = x;
    	int x2b = x;
    	int y2a = y - crosshairRadius;
    	if (y2a < 0){
    		y2a = 0;
    	}
    	int y2b = y + crosshairRadius;
    	Line line2 = new Line(x2a, y2a, x2b, y2b);
    	styleCrosshairLine(line2);
    	lines.add(line2);
    	return lines;
    	
    }
    public static void styleCrosshairLine(Line line){
    	line.setFill(null);
        line.setStroke(Color.YELLOW);
        line.setStrokeWidth(1);
    	
    }
   
    private void addEventhandlerForColumnSort(Label columnHeader, ResultsTableSortable rt, GridPane gp, List<String> colNames){
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
    public List<String> getActiveScoreColumns() throws AvatolCVException {
        List<String> result = new ArrayList<String>();
        result.add(COLNAME_IMAGE);
        if (isEvaluationMode()){
            result.add(COLNAME_TRUTH); 
        }
        result.add(COLNAME_SCORE);
        result.add(COLNAME_SAVE_STATUS);
        result.add(COLNAME_CONFIDENCE);
        if (this.runSummary.hasTrainTestConcern()){
            result.add(getTrainTestHeader());
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
    
    private void renderTable(ResultsTableSortable rt, GridPane gp, List<String> colNames){
        gp.getChildren().clear();
        int headerColumnIndex = 0;
        for (String colName : colNames){
            Label label = new Label(colName);
            label.getStyleClass().add("columnHeader");
            label.setMaxWidth(Double.MAX_VALUE);
            label.setTextAlignment(TextAlignment.CENTER);
            GridPane.setHalignment(label, HPos.CENTER);
            gp.add(label, headerColumnIndex++, 0);
            addEventhandlerForColumnSort(label, rt, gp, colNames);
        }
        List<String> imageIDs = rt.getImageIDsInCurrentOrder();
        int rowIndex = 1;
        for (String imageID: imageIDs){
            int columnIndex = 0;
            for (String colName : colNames){
                Node widget = (Node)rt.getWidget(imageID, colName);
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
            cc.setHgrow(Priority.NEVER);
        }
        ObservableList<RowConstraints> rowConstraints = gp.getRowConstraints();
        for (RowConstraints rc : rowConstraints){
            rc.setVgrow(Priority.NEVER);
        }
    }
    private void setScoredImagesInfo(String runID, String scoringConcernName) throws AvatolCVException {
    	scoreLabelForImageIDHash = new Hashtable<String, Label>();
    	saveStatusLabelForImageIDHash = new Hashtable<String, Label>();
    	scoredImagesTab.setText(scoringConcernName + " - SCORED images");
    	trainingImagesTab.setText(scoringConcernName + " - TRAINING images");

    	//scoredImagesGridPane.getChildren().clear();
    	//addScoredImagesHeader(scoredImagesGridPane);
    	
    	String scoringFilePath = AvatolCVFileSystem.getScoringFilePath(runID, scoringConcernName);
        scoringInfoFile = new ScoringInfoFile(scoringFilePath);
    	String scoreFilePath = AvatolCVFileSystem.getScoreFilePath(runID, scoringConcernName);
    	sif = new ScoresInfoFile(scoreFilePath);
    	
    	String trainingFilePath = AvatolCVFileSystem.getTrainingFilePath(runID, scoringConcernName);
    	tif = new TrainingInfoFile(trainingFilePath);
    	
    	HoldoutInfoFile hif = null;
    	if (isEvaluationMode()){
    	    String holdoutFilePath = AvatolCVFileSystem.getHoldoutFilePath(runID, scoringConcernName);
            hif = new HoldoutInfoFile(holdoutFilePath);
    	}
    	resultsTable2 = new ResultsTableSortable();
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
    		
    		Label saveStatusLabel = new Label(STATUS_UNSAVED);
    		saveStatusLabel.getStyleClass().add("columnValue");
            saveStatusLabelForImageIDHash.put(imageID, saveStatusLabel);
            resultsTable2.addWidgetForColumn(imageID, COLNAME_SAVE_STATUS, saveStatusLabel);
    		
    		//System.out.println("getting confidence for ImageValue path(key) and value: " + path + ";" + scoringConcernValue);
    		String conf = sif.getConfidenceForImageValue(path, normalizedScoringConcernValue);
    		String trimmedScoreConf = ResultsUtils.limitToTwoDecimalPlaces(conf);
    		resultsTable2.addValueForColumn(imageID, COLNAME_CONFIDENCE, trimmedScoreConf);
    		Label confLabel = new Label(trimmedScoreConf);
    		confLabel.getStyleClass().add("columnValue");
            resultsTable2.addWidgetForColumn(imageID, COLNAME_CONFIDENCE, confLabel);
            
    		String normalizedTruthString = null;
    		if (isEvaluationMode()){
    		    normalizedTruthString = hif.getScoringConcernValueForImagePath(path);
    			String truth = new NormalizedValue(normalizedTruthString).getName();
    			resultsTable2.addValueForColumn(imageID, COLNAME_TRUTH, truth);
    			Label truthLabel = new Label(truth);
    			truthLabel.getStyleClass().add("columnValue");
                resultsTable2.addWidgetForColumn(imageID, COLNAME_TRUTH, truthLabel);
    		}
    		
        	String origImageNameWithID = ResultsUtils.getTrueImageNameFromImagePath(path);
        	String[] parts = ClassicSplitter.splitt(origImageNameWithID,'_');
        	String origImageName = parts[1];
        	if ("".equals(origImageName)){
        	    origImageName = parts[0];
        	}
        	//String idPrefix = imageID + "_";
        	//String origImageName = origImageNameWithID.replaceAll(idPrefix, "");
        	resultsTable2.addValueForColumn(imageID, COLNAME_NAME, origImageName);
        	Label nameLabel = new Label(origImageName);
        	nameLabel.getStyleClass().add("columnValue");
            resultsTable2.addWidgetForColumn(imageID, COLNAME_NAME, nameLabel);
            
        	String thumbnailPathname = ResultsUtils.getThumbnailPathWithImagePath(path);
        	resultsTable2.addValueForColumn(imageID, COLNAME_IMAGE, thumbnailPathname);
        	Image image = new Image("file:"+thumbnailPathname);
            ImageView iv = new ImageView(image);
            int boundWidth = (int)iv.getBoundsInParent().getWidth();
            int boundHeight = (int)iv.getBoundsInParent().getHeight();
            //System.out.println("boundWidth " + boundWidth + " boundHeight " + boundHeight);
            AnchorPane apForThumbnail = new AnchorPane();
            apForThumbnail.getChildren().add(iv);
            PointAnnotations pointAnnotations = sif.getAnnotationCoordinates(path);
            drawCoordinates(apForThumbnail, pointAnnotations, boundWidth, boundHeight);
            //addEventhandlerForImageClick(iv, sr);
            resultsTable2.addWidgetForColumn(imageID, COLNAME_IMAGE, apForThumbnail);
            addEventhandlerForImageClick(iv, resultsTable2, imageID,scoredImagesGridPane, pointAnnotations);
        	
        	if (this.runSummary.hasTrainTestConcern()){
        	    String trainTestConcernValue = scoringInfoFile.getTrainTestConcernValueForImageID(ImageInfo.getImageIDFromPath(path)).getName();
        	    resultsTable2.addValueForColumn(imageID, getTrainTestHeader(), trainTestConcernValue);
        	    Label trainTestLabel = new Label(trainTestConcernValue);
        	    trainTestLabel.getStyleClass().add("columnValue");
                resultsTable2.addWidgetForColumn(imageID, getTrainTestHeader(), trainTestLabel);
            }
    	}
    	resultsTable2.sortOnColumn(COLNAME_IMAGE);
    	renderTable(resultsTable2, scoredImagesGridPane, getActiveScoreColumns());

    	trainingTable = new ResultsTableSortable();
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
    			
        		String trueNameWithSuffix = ResultsUtils.getTrueImageNameFromImagePath(path);
        		String[] parts = ClassicSplitter.splitt(trueNameWithSuffix,'_');
                String origImageName = parts[1];
                if ("".equals(origImageName)){
                    origImageName = parts[0];
                }
                //String idPrefix = imageID + "_";
                //String origImageName = origImageNameWithID.replaceAll(idPrefix, "");
        		trainingTable.addValueForColumn(imageID, COLNAME_NAME, origImageName);
        		Label nameLabel = new Label(origImageName);
                nameLabel.getStyleClass().add("columnValue");
                trainingTable.addWidgetForColumn(imageID, COLNAME_NAME, nameLabel);
                
                String thumbnailPathname = AvatolCVFileSystem.getThumbnailPathForImagePath(path);
                trainingTable.addValueForColumn(imageID, COLNAME_IMAGE, thumbnailPathname);
                Image image = new Image("file:"+thumbnailPathname);
                ImageView iv = new ImageView(image);
                int boundWidth = (int)iv.getBoundsInParent().getWidth();
                int boundHeight = (int)iv.getBoundsInParent().getHeight();
                //System.out.println("boundWidth " + boundWidth + " boundHeight " + boundHeight);
                AnchorPane apForThumbnail = new AnchorPane();
                apForThumbnail.getChildren().add(iv);
                //addEventhandlerForImageClick(iv, sr);
                PointAnnotations pointAnnotations = tif.getAnnotationCoordinates(path);
                drawCoordinates(apForThumbnail, pointAnnotations, boundWidth, boundHeight);
                trainingTable.addWidgetForColumn(imageID, COLNAME_IMAGE, apForThumbnail);
                addEventhandlerForImageClick(iv, trainingTable, imageID, trainingImagesGridPane, pointAnnotations);
                
        		//addTrainingImageToGridPaneRowForCookingShow(path, trueName, value, trainingImagesGridPane, row++);
    		}
    	}
    	trainingTable.sortOnColumn(COLNAME_IMAGE);
    	renderTable(trainingTable, trainingImagesGridPane, getTrainingColumns());
        
    	trainingImagesGridPane.requestLayout();
        scoredImagesGridPane.requestLayout();
    }
  
    public String getTrainTestHeader() throws AvatolCVException {
        String ttKeyString = this.runSummary.getTrainTestConcern();
        String ttHeaderString = new NormalizedKey(ttKeyString).getName();
        return ttHeaderString;
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
    
    public void doUndoSaveResults(){
        Thread t = new Thread(() -> undoSaveResults());
        t.start();
    }
    private static List<String> getIdListFromListString(String idListString){
    	List<String> result = new ArrayList<String>();
    	String[] ids = idListString.split(UploadSession.STRING_LIST_DELIM);
    	
    	for (String id : ids){
    		result.add(id);
    	}
    	return result;
    }
    public void userFeedbackForUndo(List<String> ids){
    	for (String id : ids){
			Label scoreLabel = scoreLabelForImageIDHash.get(id);
            Platform.runLater(() -> scoreLabel.getStyleClass().remove(UPLOADED_CSS_STYLE));
            Label uploadStatusLabel = saveStatusLabelForImageIDHash.get(id);
            Platform.runLater(() -> uploadStatusLabel.setText(STATUS_UNSAVED));
            Platform.runLater(() -> uploadStatusLabel.getStyleClass().remove(UPLOADED_CSS_STYLE));
		}
    }
    public void userFeedbackForUndoAbstain(List<String> ids){
    	for (String id : ids){
			Label uploadStatusLabel = saveStatusLabelForImageIDHash.get(id);
            Platform.runLater(() -> uploadStatusLabel.setText(STATUS_UNSAVED));
		}
    }
    public void userFeedbackForUndo(String id){
		Label scoreLabel = scoreLabelForImageIDHash.get(id);
        Platform.runLater(() -> scoreLabel.getStyleClass().remove(UPLOADED_CSS_STYLE));
        Label uploadStatusLabel = saveStatusLabelForImageIDHash.get(id);
        Platform.runLater(() -> uploadStatusLabel.setText(STATUS_UNSAVED));
    }

    public void userFeedbackForUndoAbstain(String id){
	    Label uploadStatusLabel = saveStatusLabelForImageIDHash.get(id);
        Platform.runLater(() -> uploadStatusLabel.setText(STATUS_UNSAVED));
    }
    private void undoNew(UploadEvent event) throws AvatolCVException {
    	String imageIDString = event.getImageID();
    	if (imageIDString.contains(UploadSession.STRING_LIST_DELIM)){
    		List<String> ids = getIdListFromListString(imageIDString);
    		this.dataSource.deleteScoreForKey(ids.get(0), event.getKey(), event.getTrainTestConcern(), event.getTrainTestConcernValue());
    		userFeedbackForUndo(ids);
    	}
    	else {
    		this.dataSource.deleteScoreForKey(event.getImageID(), event.getKey(), event.getTrainTestConcern(), event.getTrainTestConcernValue());
    		userFeedbackForUndo(event.getImageID());
    	}
        logger.info("upload UNDO : " + event.getImageID() + " " + event.getKey().getName() + " " + event.getOrigValue().getName() + " - DELETING NEW VALUE ");
    }
    private void undoRevise(UploadEvent event) throws AvatolCVException {
    	String imageIDString = event.getImageID();
    	if (imageIDString.contains(UploadSession.STRING_LIST_DELIM)){
    		List<String> ids = getIdListFromListString(imageIDString);
    		this.dataSource.reviseValueForKey("",ids.get(0), event.getKey(), event.getOrigValue(), event.getTrainTestConcern(), event.getTrainTestConcernValue());
    		userFeedbackForUndo(ids);
    	}
    	else {
    		this.dataSource.reviseValueForKey("",event.getImageID(), event.getKey(), event.getOrigValue(), event.getTrainTestConcern(), event.getTrainTestConcernValue());
    		userFeedbackForUndo(event.getImageID());
    	}
    	logger.info("upload UNDO : " + event.getImageID() + " " + event.getKey().getName() + " " + event.getOrigValue().getName() + " - REVERTING TO PRIOR VALUE ");
    }
    private void undoAbstain(UploadEvent event) throws AvatolCVException {
    	String imageIDString = event.getImageID();
    	if (imageIDString.contains(UploadSession.STRING_LIST_DELIM)){
    		List<String> ids = getIdListFromListString(imageIDString);
    		userFeedbackForUndoAbstain(ids);
    	}
    	else {
    		userFeedbackForUndoAbstain(event.getImageID());
    	}
    	logger.info("upload UNDO : " + event.getImageID() + " " + event.getKey().getName() + " " + event.getOrigValue().getName() + " - REVERTING ABSTAIN ");
    }
    public void undoSaveResults(){
        try {
            setDataSource();
            if (!verifyAuthentication()){
                return;
            }
            prepForUpload();
            Platform.runLater(() -> uploadProgress.setProgress(0.0));
            List<UploadEvent> events = this.uploadSession.getEventsForUndo();
            double count = events.size();
            double percentProgressPerEvent = 1 / count;
            int curEvent = 0;
            for (UploadEvent event : events){
                curEvent++;
                if (event.getType() == UploadSession.TYPE_NEW){
                	undoNew(event);
                }
                else if (event.getType() == UploadSession.TYPE_REVISE){
                	undoRevise(event);
                }
                else if (event.getType() == UploadSession.TYPE_ABSTAIN_TIE){
                    undoAbstain(event);
                }
                else {
                    //event.getType() == UploadSession.TYPE_ABSTAIN_VALUE_SAME
                	 undoAbstain(event);
                }
                double percentDone = percentProgressPerEvent * curEvent;
                Platform.runLater(() -> uploadProgress.setProgress(percentDone));
            }
            this.uploadSession.forgetEvents(events);
            
          
            
            Platform.runLater(() -> enableUndoUploadButtonIfAppropriate());
        }
        catch(AvatolCVException ace){
            AvatolCVExceptionExpresserJavaFX.instance.showException(ace, "problem trying to undo save results");
        }
        
    }
    public class AuthenticateRunnable implements Runnable {
        @Override
        public void run() {
            LoginDialog dialog = new LoginDialog();
            dialog.display(dataSource.getName(), dataSource.getDefaultUsername(), dataSource.getDefaultPassword());
            username = dialog.getLogin();
            password = dialog.getPword();
        }
    }
    private boolean authenticate() throws AvatolCVException {
        AuthenticateRunnable ar = new AuthenticateRunnable();
        try {
            FXUtilities.runAndWait(ar);
        }
        catch(ExecutionException e){
            e.printStackTrace();
        } 
        catch (InterruptedException e) {
            e.printStackTrace();
        }
        return this.dataSource.authenticate(username, password);
    }
    public void doSaveResults(){
        Thread t = new Thread(() -> saveResults());
        t.start();
    }
    
    public void setDataSource() throws AvatolCVException {
        if (null == this.dataSource){
            this.dataSource = DataSourceUtils.getDataSourceForRun(this.runSummary.getDataSource());
            DatasetInfo di = new DatasetInfo();
            di.setName(this.runSummary.getDataset());
            di.setID(this.runSummary.getDatasetID());
            this.dataSource.setChosenDataset(di);
        }
    }
    public boolean verifyAuthentication() throws AvatolCVException {
        boolean authenticated = false;
        if (this.dataSource.isAuthenticated()){
            authenticated = true;
        }
        else {
            authenticated = authenticate();
        }
        return authenticated;
    }
    private void prepForUpload() throws AvatolCVException {
        List<String> imageIDs = resultsTable2.getImageIDsInCurrentOrder();
        List<String> charIDs = new ArrayList<String>();
        List<String> ttConcernValueIDs = new ArrayList<String>();
        for (String imageID : imageIDs){
            NormalizedKey normCharKey = tif.getNormalizedCharacter();
            NormalizedValue trainTestConcernValue = scoringInfoFile.getTrainTestConcernValueForImageID(imageID);
            String charID = normCharKey.getID();
            String ttConcernValueID = trainTestConcernValue.getID();
            if (!charIDs.contains(charID)){
                charIDs.add(charID);
            }
            if (!ttConcernValueIDs.contains(ttConcernValueID)){
                ttConcernValueIDs.add(ttConcernValueID);
            }
        }
        this.dataSource.prepForUpload(charIDs, ttConcernValueIDs);
    }
    public void saveResults(){
        try {
            setDataSource();
            if (!verifyAuthentication()){
                return;
            }
            prepForUpload();
            if (this.dataSource.groupByTrainTestConcernValueAndVoteForUpload()){
                voteThenUpload();
            }
            else {
                uploadScoresForAllImages();
            }
        }   
        catch(AvatolCVException ace){
            AvatolCVExceptionExpresserJavaFX.instance.showException(ace, "problem trying to save results");
        }
    }
    private List<Label> getScoreLabelsForImageIds(List<String> imageIDs){
    	List<Label> nodes = new ArrayList<Label>();
    	for (String imageID : imageIDs){
    		Label scoreLabel = (Label)resultsTable2.getWidget(imageID, COLNAME_SCORE);
    		nodes.add(scoreLabel);
    	}
    	return nodes;
    }
    private List<Label> getSaveStatusLabelsForImageIds(List<String> imageIDs){
        List<Label> nodes = new ArrayList<Label>();
        for (String imageID : imageIDs){
            Label saveStatusLabel = (Label)resultsTable2.getWidget(imageID, COLNAME_SAVE_STATUS);
            nodes.add(saveStatusLabel);
        }
        return nodes;
    }
  
    
    private List<ScoreItem> collectScoreItems(List<String> imageIDs) throws AvatolCVException {
        List<ScoreItem> scoreItems = new ArrayList<ScoreItem>();
        int imageCount = imageIDs.size();
        // divide by two since using half the progress bar for this bit
        double percentProgressPerImage = 0.5 / imageCount;
        int curImage = 0;
        for (String imageID : imageIDs){
            String confString = resultsTable2.getValue(imageID, COLNAME_CONFIDENCE);
            
            if (!ResultsUtils.isConfidenceStringLessThanThreshold(confString, currentThresholdString)){
                //String value = scoreChoice.getValue();
                String value = resultsTable2.getValue(imageID, COLNAME_SCORE);
                String name = resultsTable2.getValue(imageID, COLNAME_NAME);
                //System.out.println(name + "  -  " + value);
   
                NormalizedKey normCharKey = tif.getNormalizedCharacter();
                NormalizedKey trainTestConcern = scoringInfoFile.getTrainTestConcernForImageID(imageID);
                NormalizedValue trainTestConcernValue = scoringInfoFile.getTrainTestConcernValueForImageID(imageID);
                NormalizedValue newValue = sif.getScoreValueForImageID(imageID);
                //Need to pass the normalized key and value for this row to Data source and ask if key exists for this image
                NormalizedValue existingValueForKey = dataSource.getValueForKeyAtDatasourceForImage(normCharKey, imageID, trainTestConcern, trainTestConcernValue);
                ScoreItem si = new ScoreItem(imageID, normCharKey, trainTestConcern, trainTestConcernValue, newValue, existingValueForKey, confString);
                scoreItems.add(si);
            }
            curImage++;
            double percentDone = percentProgressPerImage * curImage;
            System.out.println("percentDone 0 - 0.5 : " + percentDone + " curImage " + curImage + " of total images " + imageCount);
            Platform.runLater(() -> uploadProgress.setProgress(percentDone));
        }
        return scoreItems;
    }
    private void userFeedbackForUpload(String logMessage, List<String> ids, List<Label> scoreLabels, List<Label> saveStatusLabels, ScoringFate scoringFate, NormalizedValue newScore){
        for (String id : ids){
            logger.info(logMessage + id);
        }
        String styleString = getStyleForScoringFate(scoringFate);
        if (null != styleString){
            for (Label label : scoreLabels){
                Platform.runLater(() -> label.getStyleClass().add(styleString));
            }
        }
        for (Label label : saveStatusLabels){
            Platform.runLater(() -> label.setText(getTextForScoringFate(scoringFate, newScore.getName())));
            if (scoringFate == ScoringFate.REVISE_VALUE || scoringFate == ScoringFate.SET_NEW_VALUE){
                Platform.runLater(() -> label.getStyleClass().add(styleString));
            }
        }
    }
    private void userFeedbackForUpload(String logMessage, String id,Label scoreLabel, Label saveStatusLabel, ScoringFate scoringFate, NormalizedValue newScore){
        logger.info(logMessage + id);
        String styleString = getStyleForScoringFate(scoringFate);
        if (null != styleString){
            Platform.runLater(() -> scoreLabel.getStyleClass().add(styleString));
        }

        Platform.runLater(() -> saveStatusLabel.setText(getTextForScoringFate(scoringFate, newScore.getName())));
        if (scoringFate == ScoringFate.REVISE_VALUE || scoringFate == ScoringFate.SET_NEW_VALUE){
            Platform.runLater(() -> saveStatusLabel.getStyleClass().add(styleString));
        }
       
    }
    private String getStyleForScoringFate(ScoringFate fate){
        if (fate == ScoringFate.ABSTAIN_FROM_CHANGING_SCORE_NEW_VALUE_SAME){
            return null;
        }
        else if (fate == ScoringFate.ABSTAIN_FROM_CHANGING_SCORE_VOTE_TIE){
            return null;
        }
        else if (fate == ScoringFate.REVISE_VALUE){
            return UPLOADED_CSS_STYLE;
        }
        else {
            //fate == ScoringFate.SET_NEW_VALUE){
            return UPLOADED_CSS_STYLE;
        }
    }
    private String getTextForScoringFate(ScoringFate fate, String newScore){
        if (fate == ScoringFate.ABSTAIN_FROM_CHANGING_SCORE_NEW_VALUE_SAME){
            return "no, score wouldn't change";
        }
        else if (fate == ScoringFate.ABSTAIN_FROM_CHANGING_SCORE_VOTE_TIE){
            return "no, scores were tied";
        }
        else if (fate == ScoringFate.REVISE_VALUE){
            return "changed to " + newScore;
        }
        else {
            //fate == ScoringFate.SET_NEW_VALUE){
            return "set to " + newScore;
        }
    }
    private void voteThenUpload() throws AvatolCVException {
        UploadVoter vu = new UploadVoter(this.dataSource, this, this.uploadSession);
        List<String> imageIDs = resultsTable2.getImageIDsInCurrentOrder();
        
        // use 50% of progress bar on populateVoter
        Platform.runLater(() -> uploadProgress.setProgress(0.0));
        List<ScoreItem> scoreItems = collectScoreItems(imageIDs);
        vu.addScores(scoreItems);
        ScoringProvenanceInfo spi = new ScoringProvenanceInfo();
        vu.vote(spi);
        List<ScoreItem> voteWinners = vu.getVoteWinners();
        int toUploadCount = voteWinners.size();
        
        Platform.runLater(() -> uploadProgress.setProgress(0.5));
        // use the other 50% of progress bar on uploading
        double percentProgressPerRow = 0.5 / toUploadCount;
        uploadSession.nextSession();
        int uploadCount = 0;
        for (ScoreItem si : voteWinners){
            String imageID = si.getImageID();
            NormalizedKey normCharKey = si.getNormCharKey();
            NormalizedValue newValue = si.getNewValue();
            NormalizedKey trainTestConcern = si.getTrainTestConcern();
            NormalizedValue trainTestConcernValue = si.getTrainTestConcernValue();
            NormalizedValue existingValueForKey = si.getExistingValueForKey();
        	List<String> ids = si.getImageIDsRepresentedByWinner();
        	
        	logger.info("################################################################");
            logger.info(" trainTestConcernValue: " + trainTestConcernValue + " newValue: " + newValue + " existingValueForKey: " + existingValueForKey);
            logger.info("################################################################");
            List<Label> scoreLabelsForWinner = getScoreLabelsForImageIds(si.getImageIDsRepresentedByWinner());
            List<Label> saveStatusLabelsForWinner = getSaveStatusLabelsForImageIds(si.getImageIDsRepresentedByWinner());
            String provenanceString = spi.toString();
            if (si.getScoringFate() == ScoringFate.ABSTAIN_FROM_CHANGING_SCORE_NEW_VALUE_SAME){
                this.uploadSession.abstainSinceValueSame(si.getImageIDsRepresentedByWinner(), normCharKey, newValue, existingValueForKey, trainTestConcern, trainTestConcernValue);
                userFeedbackForUpload("UPLOAD - new value same as old, SKIPPING UPLOAD", ids, scoreLabelsForWinner,saveStatusLabelsForWinner, si.getScoringFate(),newValue);
            }
            else if (si.getScoringFate() == ScoringFate.ABSTAIN_FROM_CHANGING_SCORE_VOTE_TIE){
                this.uploadSession.abstainSinceTieVote(si.getImageIDsRepresentedByWinner(), normCharKey, newValue, existingValueForKey, trainTestConcern, trainTestConcernValue);
                userFeedbackForUpload("UPLOAD - ambiguous scoring - voting tied, SKIPPING UPLOAD", ids, scoreLabelsForWinner,saveStatusLabelsForWinner, si.getScoringFate(),newValue);
            }
            else if (si.getScoringFate() == ScoringFate.REVISE_VALUE){
                
            	boolean result = dataSource.reviseValueForKey(provenanceString,imageID, normCharKey, newValue,trainTestConcern,trainTestConcernValue);
                if (result){
                	this.uploadSession.reviseValueForKey(si.getImageIDsRepresentedByWinner(), normCharKey, newValue, existingValueForKey, trainTestConcern, trainTestConcernValue);
                	userFeedbackForUpload("UPLOAD - REVISING value from " + si.getExistingValueForKey() + " to " +  si.getNewValue(), ids, scoreLabelsForWinner,saveStatusLabelsForWinner, si.getScoringFate(),newValue);
                }
            }
            else {
            	// must be ScoringFate.SET_NEW_VALUE
            	boolean result = dataSource.addKeyValue(provenanceString,imageID, normCharKey, newValue,trainTestConcern,trainTestConcernValue);
                if (result){
                	this.uploadSession.addNewKeyValue(si.getImageIDsRepresentedByWinner(), normCharKey, newValue, trainTestConcern, trainTestConcernValue);
                	userFeedbackForUpload("UPLOAD - NEW value from " + si.getExistingValueForKey() + " to " +  si.getNewValue(), ids, scoreLabelsForWinner,saveStatusLabelsForWinner, si.getScoringFate(),newValue);
                }
                else {
                	Platform.runLater(() -> JavaFXUtils.dialog("cannot upload new score for " + si.getTrainTestConcernValue()));
                }
            }
            uploadCount++;
            
            double percentDone = 0.5 + percentProgressPerRow * uploadCount;
            System.out.println("percentDone 0.5 - 1.0 : " + percentDone);
            Platform.runLater(() -> uploadProgress.setProgress(percentDone));	
        }
        this.uploadSession.persist();
        Platform.runLater(() -> enableUndoUploadButtonIfAppropriate());
    }
    private void uploadScoresForAllImages() throws AvatolCVException {
        // list all the answers above threshold
        List<String> imageIDs = resultsTable2.getImageIDsInCurrentOrder();
        ScoringProvenanceInfo spi = new ScoringProvenanceInfo();
        Platform.runLater(() -> uploadProgress.setProgress(0.0));
        List<ScoreItem> scoreItems = collectScoreItems(imageIDs);
        // for voteThenUpload, calling deduce happens in the Voter so that ties can be noted.  Since we're not voting, all muxt be assessed. 
        for (ScoreItem si : scoreItems){
            si.deduceScoringFate();
        }
        double rowToUploadCount = scoreItems.size();
        
        Platform.runLater(() -> uploadProgress.setProgress(0.5));
        double percentProgressPerRow = 1 / rowToUploadCount;
        int rowCount = 0;
        uploadSession.nextSession();
        for (ScoreItem si : scoreItems){
            String imageID = si.getImageID();
            NormalizedKey normCharKey = si.getNormCharKey();
            NormalizedValue newValue = si.getNewValue();
            NormalizedKey trainTestConcern = si.getTrainTestConcern();
            NormalizedValue trainTestConcernValue = si.getTrainTestConcernValue();
            NormalizedValue existingValueForKey = si.getExistingValueForKey();
            
            logger.info("################################################################");
            logger.info("normCharKey: " + normCharKey + " trainTestConcernValue: " + trainTestConcernValue + " newValue: " + newValue + " existingValueForKey: " + existingValueForKey);
            logger.info("################################################################");
            Label scoreLabel = (Label)resultsTable2.getWidget(imageID, COLNAME_SCORE);
            Label saveStatusLabel = (Label)resultsTable2.getWidget(imageID, COLNAME_SAVE_STATUS);
            String provenanceString = spi.toString();
            if (si.getScoringFate() == ScoringFate.ABSTAIN_FROM_CHANGING_SCORE_NEW_VALUE_SAME){
                this.uploadSession.abstainSinceValueSame(imageID, normCharKey, newValue, existingValueForKey, trainTestConcern, trainTestConcernValue);
                userFeedbackForUpload("UPLOAD - new value same as old, SKIPPING UPLOAD", imageID, scoreLabel,saveStatusLabel, si.getScoringFate(),newValue);
            }
            // TIE WON'T EVER HAPPEN IF WE ARE UPLOADING ALL AND NOT VOTING
            else if (si.getScoringFate() == ScoringFate.ABSTAIN_FROM_CHANGING_SCORE_VOTE_TIE){
                throw new AvatolCVException("During upload, found score that claimed to be a tie, but we aren't in a voting context.");
                //this.uploadSession.abstainSinceTieVote(imageID, normCharKey, newValue, existingValueForKey, trainTestConcern, trainTestConcernValue);
                ///userFeedbackForUpload("UPLOAD - ambiguous scoring - voting tied, SKIPPING UPLOAD", imageID, scoreLabel,saveStatusLabel, si.getScoringFate(),newValue);
            }
            else if (si.getScoringFate() == ScoringFate.REVISE_VALUE){
                boolean result = dataSource.reviseValueForKey(provenanceString, imageID, normCharKey, newValue,trainTestConcern,trainTestConcernValue);
                if (result){
                    this.uploadSession.reviseValueForKey(imageID, normCharKey, newValue, existingValueForKey, trainTestConcern, trainTestConcernValue);
                    userFeedbackForUpload("UPLOAD - REVISING value from " + si.getExistingValueForKey() + " to " +  si.getNewValue(), imageID, scoreLabel,saveStatusLabel, si.getScoringFate(),newValue);
                }
            }
            else {
                // must be ScoringFate.SET_NEW_VALUE
                boolean result = dataSource.addKeyValue(provenanceString, imageID, normCharKey, newValue,trainTestConcern,trainTestConcernValue);
                if (result){
                    this.uploadSession.addNewKeyValue(imageID, normCharKey, newValue, trainTestConcern, trainTestConcernValue);
                    userFeedbackForUpload("UPLOAD - NEW value from " + si.getExistingValueForKey() + " to " +  si.getNewValue(), imageID, scoreLabel ,saveStatusLabel, si.getScoringFate(),newValue);
                }
                else {
                    Platform.runLater(() -> JavaFXUtils.dialog("cannot upload new score for " + si.getTrainTestConcernValue()));
                }
            }
            rowCount++;
            double percentDone = percentProgressPerRow * rowCount;
            Platform.runLater(() -> uploadProgress.setProgress(percentDone));
        }
        this.uploadSession.persist();
        Platform.runLater(() -> enableUndoUploadButtonIfAppropriate());
   
    }
    
    @Override
    public void updateProgress(String processName, double percentDone) {
        Platform.runLater(() -> uploadProgress.setProgress(percentDone));
    }
    @Override
    public void setMessage(String processName, String m) {
        // not needed for this implementation
    }
}
/*
 * private void runAndWait(Runnable runnable) throws InterruptedException, ExecutionException {
FutureTask future = new FutureTask(runnable, null);
Platform.runLater(future);
future.get();
}
 */

