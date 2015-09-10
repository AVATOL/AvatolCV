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
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.oregonstate.eecs.iis.avatolcv.AvatolCVFileSystem;
import edu.oregonstate.eecs.iis.avatolcv.core.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.core.AvatolCVExceptionExpresser;
import edu.oregonstate.eecs.iis.avatolcv.core.SessionInfo;
import edu.oregonstate.eecs.iis.avatolcv.core.StepController;
import edu.oregonstate.eecs.iis.avatolcv.core.Step;
import edu.oregonstate.eecs.iis.avatolcv.core.StepSequence;
import edu.oregonstate.eecs.iis.avatolcv.javafxui.AvatolCVJavaFXMB;
import edu.oregonstate.eecs.iis.avatolcv.steps.DataSourceStep;
import edu.oregonstate.eecs.iis.avatolcv.steps.DatasetChoiceStep;
import edu.oregonstate.eecs.iis.avatolcv.steps.ImagePullStep;
import edu.oregonstate.eecs.iis.avatolcv.steps.LoginStep;
import edu.oregonstate.eecs.iis.avatolcv.steps.SummaryFilterStep;
import edu.oregonstate.eecs.iis.avatolcv.steps.ScoringConcernStep;
import edu.oregonstate.eecs.iis.avatolcv.steps.SessionFocusStep;

public class JavaFXStepSequencer  {
   
	public Button nextButton;
	public Button backButton;
	public VBox stepList;
    private SessionInfo sessionInfo = null;
    private StepSequence ss = null;
    private Stage mainWindow = null;
    private Scene scene = null;
    private AvatolCVExceptionExpresser exceptionExpresser = null;
    private Hashtable<Step,Label> labelForStepHash = new Hashtable<Step,Label>();
  
    private Hashtable<Step,StepController> controllerForStep = new Hashtable<Step,StepController>();
    public JavaFXStepSequencer(AvatolCVExceptionExpresser exceptionExpresser){
        this.exceptionExpresser = exceptionExpresser;
    }
    public void init(String avatolCVRootDir, Stage mainWindow) throws AvatolCVException {
        this.mainWindow = mainWindow;
    	initUI();

        //MorphobankWSClient client = new MorphobankWSClientImpl();
        sessionInfo = new SessionInfo(exceptionExpresser);
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
        
        DatasetChoiceStep matrixStep = new DatasetChoiceStep(sessionInfo);
        ss.appendStep(matrixStep);
        DatasetChoiceStepController matrixController = new DatasetChoiceStepController(matrixStep, "DatasetChoiceStep.fxml");
        controllerForStep.put(matrixStep, matrixController);
        addLabelForStep(matrixStep,"Select Matrix");

        SessionFocusStep focusStep = new SessionFocusStep(sessionInfo);
        ss.appendStep(focusStep);
        SessionFocusStepController focusController = new SessionFocusStepController(focusStep, "SessionFocusStep.fxml");
        controllerForStep.put(focusStep, focusController);
        addLabelForStep(focusStep,"Select Scoring Approach");
             
        ScoringConcernStep scoringConcernStep = new ScoringConcernStep(sessionInfo);
        ss.appendStep(scoringConcernStep);
        ScoringConcernStepController scoringConcernStepController = new ScoringConcernStepController(scoringConcernStep, "ScoringConcernStep.fxml");
        controllerForStep.put(scoringConcernStep, scoringConcernStepController);
        addLabelForStep(scoringConcernStep,"Select Item To Score");
        
        SummaryFilterStep summaryFilterStep = new SummaryFilterStep(sessionInfo);
        ss.appendStep(summaryFilterStep);
        SummaryFilterStepController summaryFilterStepController = new SummaryFilterStepController(summaryFilterStep, "SummaryFilterStep.fxml");
        controllerForStep.put(summaryFilterStep, summaryFilterStepController);
        addLabelForStep(summaryFilterStep,"Summary/Filter");
        
        ImagePullStep imagePullStep = new ImagePullStep(sessionInfo);
        ss.appendStep(imagePullStep);
        ImagePullStepController imagePullController = new ImagePullStepController(imagePullStep, "ImagePullStep.fxml");
        controllerForStep.put(imagePullStep, imagePullController);
        addLabelForStep(imagePullStep,"Load Images");
        /*
        MBExclusionQualityStep exclusionQualityStep = new MBExclusionQualityStep(null, client, sessionData);
        ss.appendStep(exclusionQualityStep);
        MBExclusionQualityStepController qualityStepController = new MBExclusionQualityStepController(exclusionQualityStep, "MBExclusionQualityStepTile.fxml");
        controllerForStep.put(exclusionQualityStep, qualityStepController);
        addLabelForStep(exclusionQualityStep,"Image Quality");
        //Step exclusionStep = new MBExclusionPropertyStep(null, sessionData);
        //ss.appendStep(exclusionStep);
        
        
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
        ScoringAlgorithms scoringAlgorithms = new ScoringAlgorithms();
        MBCharQuestionsStep charQuestionsStep = new MBCharQuestionsStep(null, scoringAlgorithms, sessionData);
        ss.appendStep(charQuestionsStep);
        MBCharQuestionsController charQuestionsStepController = new MBCharQuestionsController(charQuestionsStep, "MBCharQuestionsStep.fxml");
        controllerForStep.put(charQuestionsStep, charQuestionsStepController);
        Label loginLabel = new Label("Login");
        stepList.getChildren().add(loginLabel);
        */
        
  /*      
        MBTrainingDataPullStep trainingDataPullStep = new MBTrainingDataPullStep(null, client, sessionData);
        ss.appendStep(trainingDataPullStep);
        MBTrainingDataPullStepController trainingDataPullStepController = new MBTrainingDataPullStepController(this, trainingDataPullStep, "MBTrainingDataPullStep.fxml");
        controllerForStep.put(trainingDataPullStep, trainingDataPullStepController);
        addLabelForStep(trainingDataPullStep,"Pull Training Data");
       
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
    private void activateCurrentStep() throws AvatolCVException {
    	reRenderStepList();
        Step step = ss.getCurrentStep();
        StepController controller = controllerForStep.get(step);
        Node contentNode = controller.getContentNode();
        AnchorPane anchorPane = (AnchorPane)scene.lookup("#navigationShellContentPane");
        ObservableList<Node> children = anchorPane.getChildren();

        anchorPane.getChildren().clear();
        children = anchorPane.getChildren();
        anchorPane.getChildren().add(contentNode);
        AnchorPane.setBottomAnchor(contentNode, 0.0);
        AnchorPane.setTopAnchor(contentNode, 0.0);
        AnchorPane.setLeftAnchor(contentNode, 0.0);
        AnchorPane.setRightAnchor(contentNode, 0.0);
       
        if (!controller.delayEnableNavButtons()){
        	enableNavButtons();
        }
    }
    public void enableNavButtons(){
    	nextButton.setDisable(false);
    	backButton.setDisable(false);
    }
    public void initUI() throws AvatolCVException {
        try {
            FXMLLoader loader = new FXMLLoader(JavaFXStepSequencer.class.getResource("navigationShellNoSplit.fxml"));
            loader.setController(this);
            Parent navShell = loader.load();
            
            this.scene = new Scene(navShell, AvatolCVJavaFXMB.MAIN_WINDOW_WIDTH, AvatolCVJavaFXMB.MAIN_WINDOW_HEIGHT);
            this.mainWindow.setScene(scene);
        }
        catch(Exception e){
            throw new AvatolCVException(e.getMessage(),e);
        }
    }
    public void previousStep(){
    	
    }
    /*
     * nextStep called from the button on the javaFX ui thread (application thread)
     */
    public void nextStep(){
    	nextButton.setDisable(true);
    	backButton.setDisable(true);
    	
    	Step step = ss.getCurrentStep();
        StepController controller = controllerForStep.get(step);
        if (step.hasDataLoadPhase()){
            controller.configureUIForDataLoadPhase();
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
     * (runs in background thread)
     */
    public void requestNextStep() throws AvatolCVException {
    	Step step = ss.getCurrentStep();
    	StepController controller = controllerForStep.get(step);
    	
    	boolean success = controller.consumeUIData();
    	if (success){
    	    if (step.hasDataLoadPhase()){
                controller.executeDataLoadPhase();
                while (!controller.isDataLoadPhaseComplete()){
                    try {
                        Thread.sleep(500);
                    }
                    catch(InterruptedException e){ }
                }
            }
    		ss.next();
    		// task of loading UI for next step is put back on the javaFX UI thread
    		CurrentStepRunner stepRunner = new CurrentStepRunner();
    		Platform.runLater(stepRunner);
    	}
    	else {
    		controller.clearUIFields();
    	}
    }
    public class CurrentStepRunner implements Runnable{

		@Override
		public void run() {
			try {
    			activateCurrentStep();
    		}
    		catch (AvatolCVException ace){
    		    SessionInfo.exceptionExpresser.showException(ace, "An error was encountered while trying to move to next screen.");
    		}
			
		}
    	
    }
}
