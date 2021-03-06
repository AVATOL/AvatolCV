package edu.oregonstate.eecs.iis.avatolcv.ui.javafx;

import java.util.Hashtable;
import java.util.List;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Accordion;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TableView;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.oregonstate.eecs.iis.avatolcv.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.AvatolCVFileSystem;
import edu.oregonstate.eecs.iis.avatolcv.javafxui.AvatolCVExceptionExpresserJavaFX;
import edu.oregonstate.eecs.iis.avatolcv.javafxui.AvatolCVJavaFX;
import edu.oregonstate.eecs.iis.avatolcv.session.SessionInfo;
import edu.oregonstate.eecs.iis.avatolcv.session.StepController;
import edu.oregonstate.eecs.iis.avatolcv.session.StepSequence;
import edu.oregonstate.eecs.iis.avatolcv.steps.DataSourceStep;
import edu.oregonstate.eecs.iis.avatolcv.steps.DatasetChoiceStep;
import edu.oregonstate.eecs.iis.avatolcv.steps.ExclusionQualityStep;
import edu.oregonstate.eecs.iis.avatolcv.steps.ImagePullStep;
import edu.oregonstate.eecs.iis.avatolcv.steps.LoginStep;
import edu.oregonstate.eecs.iis.avatolcv.steps.OrientationConfigurationStep;
import edu.oregonstate.eecs.iis.avatolcv.steps.OrientationRunStep;
import edu.oregonstate.eecs.iis.avatolcv.steps.ScoringConfigurationStep;
import edu.oregonstate.eecs.iis.avatolcv.steps.ScoringModeStep;
import edu.oregonstate.eecs.iis.avatolcv.steps.ScoringRunStep;
import edu.oregonstate.eecs.iis.avatolcv.steps.SegmentationConfigurationStep;
import edu.oregonstate.eecs.iis.avatolcv.steps.SegmentationRunStep;
import edu.oregonstate.eecs.iis.avatolcv.steps.Step;
import edu.oregonstate.eecs.iis.avatolcv.steps.SummaryFilterStep;
import edu.oregonstate.eecs.iis.avatolcv.steps.ScoringConcernStep;
import edu.oregonstate.eecs.iis.avatolcv.steps.SessionFocusStep;

public class JavaFXStepSequencer  {
   
	public Button nextButton;
	public Button backButton;
	public Button cancelSessionButton;
	public Label issueCountLabel;
	public static Label issueCountLabelSingleton;
	public VBox stepList;
    private SessionInfo sessionInfo = null;
    private StepSequence ss = null;
    private Stage mainWindow = null;
    private Scene scene = null;
    public Label labelScoringGoalValue;
    public VBox vBoxDataIssues;
    public GridPane gridPaneDataInPlay;
    public static GridPane gridPaneDataInPlaySingleton = null;
    //public TableView<InPlayCell> tableViewDataInPlay;
    public Accordion sessionAccordion;
    public static VBox vBoxDataIssuesSingleton = null;
    //public static TableView<InPlayCell> tableViewDataInPlaySingleton = null;
    public TitledPane titlePaneSession;
    public TitledPane titlePaneIssues;
    //public TitledPane titlePaneDataInPlay;
    public AnchorPane anchorPaneIssues;
    private Hashtable<Step,Label> labelForStepHash = new Hashtable<Step,Label>();
    private AvatolCVJavaFX mainScreen = null;
    private Hashtable<Step,StepController> controllerForStep = new Hashtable<Step,StepController>();
    private static final Logger logger = LogManager.getLogger(JavaFXStepSequencer.class);
    public JavaFXStepSequencer(AvatolCVJavaFX mainScreen){
        this.mainScreen = mainScreen;
        AvatolCVFileSystem.flushPriorSettings();
    }
    public void init(String avatolCVRootDir, Stage mainWindow) throws AvatolCVException {
        this.mainWindow = mainWindow;
    	initUI();

        //MorphobankWSClient client = new MorphobankWSClientImpl();
        sessionInfo = new SessionInfo();
        ss = new StepSequence();
        
        DataSourceStep dataSourceStep = new DataSourceStep(sessionInfo);
        ss.appendStep(dataSourceStep);
        DataSourceStepController dataSourceStepController = new DataSourceStepController(dataSourceStep, sessionInfo, "DataSourceStep.fxml");
        controllerForStep.put(dataSourceStep, dataSourceStepController);
        addLabelForStep(dataSourceStep,"Select Data Source");
        
        
        LoginStep loginStep = new LoginStep(sessionInfo);
        ss.appendStep(loginStep);
        LoginStepController loginController = new LoginStepController(loginStep,sessionInfo,"LoginStep.fxml");
        controllerForStep.put(loginStep, loginController);
        addLabelForStep(loginStep,"Login");
        dataSourceStep.setNextAnswerableInSeries(loginStep);
        
        DatasetChoiceStep datasetStep = new DatasetChoiceStep(sessionInfo);
        ss.appendStep(datasetStep);
        DatasetChoiceStepController matrixController = new DatasetChoiceStepController(datasetStep, "DatasetChoiceStep.fxml");
        controllerForStep.put(datasetStep, matrixController);
        addLabelForStep(datasetStep,"Select Dataset");
        loginStep.setNextAnswerableInSeries(datasetStep);

        SessionFocusStep focusStep = new SessionFocusStep(sessionInfo);
        ss.appendStep(focusStep);
        SessionFocusStepController focusController = new SessionFocusStepController(focusStep, "SessionFocusStep.fxml");
        controllerForStep.put(focusStep, focusController);
        addLabelForStep(focusStep,"Select Scoring Approach");
        datasetStep.setNextAnswerableInSeries(focusStep);
             
        ScoringConcernStep scoringConcernStep = new ScoringConcernStep(sessionInfo);
        ss.appendStep(scoringConcernStep);
        ScoringConcernStepController scoringConcernStepController = new ScoringConcernStepController(scoringConcernStep, "ScoringConcernStep.fxml");
        controllerForStep.put(scoringConcernStep, scoringConcernStepController);
        addLabelForStep(scoringConcernStep,"Select Item To Score");
        focusStep.setNextAnswerableInSeries(scoringConcernStep);
        
        
        SummaryFilterStep summaryFilterStep = new SummaryFilterStep(sessionInfo);
        ss.appendStep(summaryFilterStep);
        SummaryFilterStepController summaryFilterStepController = new SummaryFilterStepController(summaryFilterStep, "SummaryFilterStep.fxml");
        controllerForStep.put(summaryFilterStep, summaryFilterStepController);
        addLabelForStep(summaryFilterStep,"Filter");
        scoringConcernStep.setNextAnswerableInSeries(summaryFilterStep);
        
        ImagePullStep imagePullStep = new ImagePullStep(sessionInfo);
        ss.appendStep(imagePullStep);
        ImagePullStepController imagePullController = new ImagePullStepController(this, imagePullStep, "ImagePullStep.fxml");
        controllerForStep.put(imagePullStep, imagePullController);
        addLabelForStep(imagePullStep,"Load Images");
        summaryFilterStep.setNextAnswerableInSeries(imagePullStep);
//scoringConcernStep.setNextAnswerableInSeries(imagePullStep);
        
        ExclusionQualityStep exclusionQualityStep = new ExclusionQualityStep(sessionInfo);
        ss.appendStep(exclusionQualityStep);
        AnchorPane navigationShellContentPane = (AnchorPane)scene.lookup("#navigationShellContentPane");
        //ExclusionQualityStepController qualityStepController = new ExclusionQualityStepController(exclusionQualityStep, "ExclusionQualityStepTile.fxml", navigationShellContentPane);
        //ExclusionQualityStepController qualityStepController = new ExclusionQualityStepController(exclusionQualityStep, "ExclusionQualityStep.fxml", navigationShellContentPane);
        ExclusionQualityStepController qualityStepController = new ExclusionQualityStepController(exclusionQualityStep, "ExclusionQualityStepSimple.fxml", navigationShellContentPane);
        controllerForStep.put(exclusionQualityStep, qualityStepController);
        addLabelForStep(exclusionQualityStep,"Image Quality");
        imagePullStep.setNextAnswerableInSeries(exclusionQualityStep);

        //
        // Segmentation
        //
        
        SegmentationConfigurationStep segConfigStep = new SegmentationConfigurationStep(sessionInfo);
        ss.appendStep(segConfigStep);
        SegmentationConfigurationStepController segConfigStepController = new SegmentationConfigurationStepController(segConfigStep, "SegmentationConfigurationStep.fxml");
        controllerForStep.put(segConfigStep, segConfigStepController);
        addLabelForStep(segConfigStep,"Configure Segmentation");
        exclusionQualityStep.setNextAnswerableInSeries(segConfigStep);
        
        
        SegmentationRunStep segRunStep = new SegmentationRunStep(sessionInfo);
        ss.appendStep(segRunStep);
        SegmentationRunStepController segRunStepController = new SegmentationRunStepController(this, segRunStep, "SegmentationRunStep.fxml");
        controllerForStep.put(segRunStep, segRunStepController);
        addLabelForStep(segRunStep,"Run Segmentation");
        
        //
        // Orientation
        //
        
        OrientationConfigurationStep orientConfigStep = new OrientationConfigurationStep(sessionInfo);
        ss.appendStep(orientConfigStep);
        OrientationConfigurationStepController orientConfigStepController = new OrientationConfigurationStepController(orientConfigStep, "OrientationConfigurationStep.fxml");
        controllerForStep.put(orientConfigStep, orientConfigStepController);
        addLabelForStep(orientConfigStep,"Configure Orientation");
        segConfigStep.setNextAnswerableInSeries(orientConfigStep);
        
        
        OrientationRunStep orientRunStep = new OrientationRunStep(sessionInfo);
        ss.appendStep(orientRunStep);
        OrientationRunStepController orientRunStepController = new OrientationRunStepController(this, orientRunStep, "SegmentationRunStep.fxml");
        controllerForStep.put(orientRunStep, orientRunStepController);
        addLabelForStep(orientRunStep,"Run Orientation");
        
        //segConfigStep.setNextAnswerableInSeries(segRunStep);
        /*
        ScoringModeStep scoringModeStep = new ScoringModeStep(sessionInfo);
        ss.appendStep(scoringModeStep);
        ScoringModeStepController scoringModeStepController = new ScoringModeStepController(scoringModeStep, "ScoringModeStep.fxml");
        controllerForStep.put(scoringModeStep, scoringModeStepController);
        addLabelForStep(scoringModeStep,"Eval/Score");
        orientConfigStep.setNextAnswerableInSeries(scoringModeStep);
        */
        ScoringConfigurationStep scoringConfigStep = new ScoringConfigurationStep(sessionInfo);
        ss.appendStep(scoringConfigStep);
        ScoringConfigurationStepController scoringConfigStepController = new ScoringConfigurationStepController(scoringConfigStep, "ScoringConfigurationStep.fxml");
        controllerForStep.put(scoringConfigStep, scoringConfigStepController);
        addLabelForStep(scoringConfigStep,"Configure Scoring");
        orientConfigStep.setNextAnswerableInSeries(scoringConfigStep);
        
        ScoringRunStep scoringRunStep = new ScoringRunStep(sessionInfo);
        ss.appendStep(scoringRunStep);
        ScoringRunStepController scoringRunStepController = new ScoringRunStepController(this, scoringRunStep, "ScoringRunStep.fxml");
        controllerForStep.put(scoringRunStep, scoringRunStepController);
        addLabelForStep(scoringRunStep,"Run Scoring");
        
        // THIS WAS SINISAS 20151112 DEMO
        /*
        SegmentationResultsStep segResultsStep = new SegmentationResultsStep();
        ss.appendStep(segResultsStep);
        SegmentationResultsStepController segResultsStepController = new SegmentationResultsStepController(segResultsStep, "SegmentationResultsStep.fxml");
        controllerForStep.put(segResultsStep, segResultsStepController);
        addLabelForStep(segResultsStep,"Demo Segmentation Results");
        segConfigStep.setNextAnswerableInSeries(segResultsStep);
        
        */
        
        /*
        //Step charQuestionsStep = new CharQuestionsStep(null, sessionData);
        //ss.appendStep(charQuestionsStep);   
       
        // Turns out that the Step  orientation exclusion can reuse the quality step  - same mechanisms and data flow
        MBExclusionQualityStep exclusionOrientationStep = new MBExclusionQualityStep(null, client, sessionData);
        ss.appendStep(exclusionOrientationStep);
        MBExclusionOrientationStepController orientationStepController = new MBExclusionOrientationStepController(exclusionOrientationStep, "MBExclusionOrientationStepTile.fxml");
        controllerForStep.put(exclusionOrientationStep, orientationStepController);
        addLabelForStep(exclusionOrientationStep,"Orientation");
   */
 
        
  /*      
     
       
        MBTrainingExampleCheckStep scoringTrainingExampleCheckStep = new MBTrainingExampleCheckStep(null, sessionData, client);
        ss.appendStep(scoringTrainingExampleCheckStep);
        MBTrainingExampleCheckStepController trainingExampleCheckStepController = new MBTrainingExampleCheckStepController(scoringTrainingExampleCheckStep, "MBTrainingExampleCheckStep.fxml");
        controllerForStep.put(scoringTrainingExampleCheckStep, trainingExampleCheckStepController);
        addLabelForStep(scoringTrainingExampleCheckStep,"Train vs Test");
   */
        activateCurrentStep();
    }
    
    private void addLabelForStep(Step step, String text){
    	Label label = new Label(text);
    	label.getStyleClass().add("stepListLabel");
        labelForStepHash.put(step, label);
    	stepList.getChildren().add(label);
    }
    private void reRenderStepList(){
    	List<Step> steps = ss.getAllSteps();
    	boolean seekingActiveStep = true;
    	for (Step step : steps){
    		Label label = labelForStepHash.get(step);
    		if (null != label){
    			label.getStyleClass().clear();
    			if (seekingActiveStep){
    				if (step == ss.getCurrentStep()){
    					// bold
    					label.getStyleClass().add("stepListLabelBold");
    					seekingActiveStep = false;
    				}
    				else {
    					// regular
    					label.getStyleClass().add("stepListLabel");
    				}
    			}
    			else {
    				label.getStyleClass().add("stepListLabelGrey");
    			}
    		}
    	}
    }
    private void reRenderScoringGoal(){
    	labelScoringGoalValue.getStyleClass().add("stepListLabel");
    	if (sessionInfo.isScoringGoalEvalAlg()){
    		labelScoringGoalValue.setText("eval algorithm");
    	}
    	else {
        	labelScoringGoalValue.setText("score images");
    	}
    }
    /*
     * runs in the application thread so UI adjustments can fly
     */
    private void activateCurrentStep() throws AvatolCVException {
    	reRenderStepList();
    	reRenderScoringGoal();
        Step step = ss.getCurrentStep();
        
        StepController controller = controllerForStep.get(step);
        Node contentNode = controller.getContentNode();
        AnchorPane anchorPane = (AnchorPane)scene.lookup("#navigationShellContentPane");
        //ObservableList<Node> children = anchorPane.getChildren();

        anchorPane.getChildren().clear();
        //children = anchorPane.getChildren();
        anchorPane.getChildren().add(contentNode);
        AnchorPane.setBottomAnchor(contentNode, 0.0);
        AnchorPane.setTopAnchor(contentNode, 0.0);
        AnchorPane.setLeftAnchor(contentNode, 0.0);
        AnchorPane.setRightAnchor(contentNode, 0.0);
       
        if (!controller.delayEnableNavButtons()){
        	enableNavButtons();
        }
        if (!ss.canBackUp()){
        	backButton.setVisible(false);
        	backButton.setDisable(true);
        }
        else {
        	backButton.setVisible(true);
        	backButton.setDisable(false);
        }
    }
    public void enableNavButtons(){
    	nextButton.setDisable(false);
    	backButton.setDisable(false);
    }

    public void enableBackButton(){
        backButton.setDisable(false);
    }
    public void disableNavButtons(){
        nextButton.setDisable(true);
        backButton.setDisable(true);
    }
    public void initUI() throws AvatolCVException {
        try {
            FXMLLoader loader = new FXMLLoader(JavaFXStepSequencer.class.getResource("navigationShellNoSplit.fxml"));
            loader.setController(this);
            Parent navShell = loader.load();
            
            this.scene = new Scene(navShell, AvatolCVJavaFX.MAIN_WINDOW_WIDTH, AvatolCVJavaFX.MAIN_WINDOW_HEIGHT);
            scene.getStylesheets().add("../css/javafx.css");
            vBoxDataIssuesSingleton = vBoxDataIssues;
            issueCountLabelSingleton = issueCountLabel;
            gridPaneDataInPlaySingleton = gridPaneDataInPlay;
            //tableViewDataInPlaySingleton = tableViewDataInPlay;
            sessionAccordion.setExpandedPane(titlePaneSession);
            //anchorPaneIssues.get
            this.mainWindow.setScene(scene);
        }
        catch(Exception e){
            throw new AvatolCVException(e.getMessage(),e);
        }
    }
    /*
     * prevStep called from the button on the javaFX ui thread (application thread)
     */
    public void previousStep(){
        logger.info("");
        logger.info("BUTTON - BACK");
        disableNavButtons();
    	// delegate data consumption to background thread
    	PrevStepTask task = new PrevStepTask();
    	new Thread(task).start();
    }
    /*
     * nextStep called from the button on the javaFX ui thread (application thread)
     */
    public void nextStep(){
        logger.info("");
        logger.info("BUTTON - NEXT");
        disableNavButtons();
    	
    	Step step = ss.getCurrentStep();
        StepController controller = controllerForStep.get(step);
        if (step.hasFollowUpDataLoadPhase()){
            controller.configureUIForFollowUpDataLoadPhase();
        }
    	// delegate data consumption to background thread
    	NextStepTask task = new NextStepTask();
    	new Thread(task).start();
    }

    public class NextStepTask extends Task<Boolean> {
        private final Logger logger = LogManager.getLogger(NextStepTask.class);
        @Override
        protected Boolean call() throws Exception {
            requestNextStep();
            return new Boolean(true);
            
        }
       
    }

    /*
     * runs as a worker thread
     */
    public class PrevStepTask extends Task<Boolean> {
        private final Logger logger = LogManager.getLogger(PrevStepTask.class);
        @Override
        protected Boolean call() throws Exception {
            requestPreviousStep();
            return new Boolean(true);
            
        }
       
    }
    
    /*
     * runs on the worker thread but passes the currentStepRunner back to Application Thread as UI adjustments needed
     */
    public void requestPreviousStep() throws AvatolCVException {
		ss.prev();
		Step step = ss.getCurrentStep();
		while (!step.shouldRenderIfBackingIntoIt()){
			ss.prev();
			step = ss.getCurrentStep();
		}
    	CurrentStepRunner stepRunner = new CurrentStepRunner();
		Platform.runLater(stepRunner);
    }
    /*
     * (runs in background thread)
     */
    public void requestNextStep() throws AvatolCVException {
    	boolean showingResults = false;
    	Step step = ss.getCurrentStep();
    	StepController controller = controllerForStep.get(step);
    	
    	boolean success = controller.consumeUIData();
    	if (success){
    	    if (step.hasFollowUpDataLoadPhase()){
                controller.executeFollowUpDataLoadPhase();
                while (!controller.isFollowUpDataLoadPhaseComplete()){
                    try {
                        Thread.sleep(500);
                    }
                    catch(InterruptedException e){ }
                }
            }
    	    boolean seekingNext = true;
    	    while (seekingNext){
    	    	if (ss.hasMoreScreens()){
    	    		ss.next();
        	        if (ss.getCurrentStep().isEnabledByPriorAnswers()){
        	            seekingNext = false;
        	        }
    	    	}
    	    	else {
    	    		ResultsReviewSortable rr = new ResultsReviewSortable();
                    String runName = sessionInfo.getSessionName();
                    rr.initOnAppThread(this.mainScreen, mainWindow, runName);
                    seekingNext = false;
                    showingResults = true;
    	    	}
    	        
    	    }
    		if (!showingResults){
    			// task of loading UI for next step is put back on the javaFX UI thread
        		CurrentStepRunner stepRunner = new CurrentStepRunner();
        		Platform.runLater(stepRunner);
    		}
    	}
    	else {
    		enableNavButtons();
    		controller.clearUIFields();
    	}
    }
   
    public class CurrentStepRunner implements Runnable{
		@Override
		public void run() {
			try {
    			activateCurrentStep();
    		}
    		catch (Exception e){
    		    AvatolCVExceptionExpresserJavaFX.instance.showException(e, "An error was encountered while trying to move to next screen: " + e.getMessage());
    		}
		}
    }
    public void cancelSession(){
    	this.mainScreen.start(this.mainWindow);
    }
}
