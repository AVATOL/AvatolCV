package edu.oregonstate.eecs.iis.avatolcv.morphobank;

import java.io.IOException;
import java.util.Hashtable;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import edu.oregonstate.eecs.iis.avatolcv.AvatolCVFileSystem;
import edu.oregonstate.eecs.iis.avatolcv.core.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.core.ScoringAlgorithms;
import edu.oregonstate.eecs.iis.avatolcv.core.Step;
import edu.oregonstate.eecs.iis.avatolcv.core.StepController;
import edu.oregonstate.eecs.iis.avatolcv.core.StepSequence;
import edu.oregonstate.eecs.iis.avatolcv.javafxui.AvatolCVJavaFXMB;
import edu.oregonstate.eecs.iis.avatolcv.morphobank.charscore.MBTrainingDataPullStep;
import edu.oregonstate.eecs.iis.avatolcv.morphobank.charscore.MBTrainingExampleCheckStep;
import edu.oregonstate.eecs.iis.avatolcv.morphobank.javafx.DataSourceStepController;
import edu.oregonstate.eecs.iis.avatolcv.morphobank.javafx.MBCharChoiceStepController;
import edu.oregonstate.eecs.iis.avatolcv.morphobank.javafx.MBCharQuestionsController;
import edu.oregonstate.eecs.iis.avatolcv.morphobank.javafx.MBExclusionOrientationStepController;
import edu.oregonstate.eecs.iis.avatolcv.morphobank.javafx.MBExclusionQualityStepController;
import edu.oregonstate.eecs.iis.avatolcv.morphobank.javafx.MBImagePullStepController;
import edu.oregonstate.eecs.iis.avatolcv.morphobank.javafx.MBLoginStepController;
import edu.oregonstate.eecs.iis.avatolcv.morphobank.javafx.MBMatrixChoiceStepController;
import edu.oregonstate.eecs.iis.avatolcv.morphobank.javafx.MBTrainingDataPullStepController;
import edu.oregonstate.eecs.iis.avatolcv.morphobank.javafx.MBTrainingExampleCheckStepController;
import edu.oregonstate.eecs.iis.avatolcv.morphobank.javafx.MBViewChoiceStepController;
import edu.oregonstate.eecs.iis.avatolcv.morphobank.javafx.SessionFocusStepController;
import edu.oregonstate.eecs.iis.avatolcv.morphobank.javafx.MBImagePullStepController.ImageDownloadTask;
import edu.oregonstate.eecs.iis.avatolcv.segmentation.SegmentationStep;
import edu.oregonstate.eecs.iis.avatolcv.steps.CharQuestionsStep;
import edu.oregonstate.eecs.iis.avatolcv.ws.MorphobankWSClient;
import edu.oregonstate.eecs.iis.avatolcv.ws.MorphobankWSClientImpl;

public class MorphobankSessionJavaFX {
	public Button nextButton;
	public Button backButton;
	public VBox stepList;
    private String avatolCVRootDir = null;
    private MBSessionData sessionData = null;
    private AvatolCVFileSystem afs = null;
    private StepSequence ss = null;
    private Stage mainWindow = null;
    private Scene scene = null;
    private Hashtable<Step,Label> labelForStepHash = new Hashtable<Step,Label>();
  
    private Hashtable<Step,StepController> controllerForStep = new Hashtable<Step,StepController>();
    public void init(String avatolCVRootDir, Stage mainWindow) throws AvatolCVException {
        this.avatolCVRootDir = avatolCVRootDir;
        this.mainWindow = mainWindow;
    	initUI();

        MorphobankWSClient client = new MorphobankWSClientImpl();
        afs = new AvatolCVFileSystem(avatolCVRootDir);
        sessionData = new MBSessionData(avatolCVRootDir);
        ss = new StepSequence();
        
        SessionFocusStep focusStep = new SessionFocusStep(sessionData);
        ss.appendStep(focusStep);
        SessionFocusStepController focusController = new SessionFocusStepController(focusStep,"SessionFocusStep.fxml");
        controllerForStep.put(focusStep, focusController);
        addLabelForStep(focusStep,"Scoring Focus");
        
        DataSourceStep dataSourceStep = new DataSourceStep();
        ss.appendStep(dataSourceStep);
        DataSourceStepController dataSourceStepController = new DataSourceStepController(dataSourceStep,"DataSourceStep.fxml");
        controllerForStep.put(dataSourceStep, dataSourceStepController);
        addLabelForStep(dataSourceStep,"Select Data Source");
        
        MBLoginStep loginStep = new MBLoginStep(null, client);
        ss.appendStep(loginStep);
        MBLoginStepController loginController = new MBLoginStepController(loginStep,"MBLoginStep.fxml");
        controllerForStep.put(loginStep, loginController);
        addLabelForStep(loginStep,"Login");
        
        MBMatrixChoiceStep matrixStep = new MBMatrixChoiceStep(null, client, sessionData);
        ss.appendStep(matrixStep);
        MBMatrixChoiceStepController matrixController = new MBMatrixChoiceStepController(matrixStep, "MBMatrixChoiceStep.fxml");
        controllerForStep.put(matrixStep, matrixController);
        addLabelForStep(matrixStep,"Select Matrix");
        
        MBCharChoiceStep charChoiceStep = new MBCharChoiceStep(null, client, sessionData);
        ss.appendStep(charChoiceStep);
        MBCharChoiceStepController charChoiceController = new MBCharChoiceStepController(charChoiceStep, "MBCharChoiceStep.fxml");
        controllerForStep.put(charChoiceStep, charChoiceController);
        addLabelForStep(charChoiceStep,"Select Character");
        
        MBViewChoiceStep viewChoiceStep = new MBViewChoiceStep(client, sessionData);
        ss.appendStep(viewChoiceStep);
        MBViewChoiceStepController viewChoiceController = new MBViewChoiceStepController(viewChoiceStep, "MBViewChoiceStep.fxml");
        controllerForStep.put(viewChoiceStep, viewChoiceController);
        addLabelForStep(viewChoiceStep,"Select View");
        
        MBImagePullStep imagePullStep = new MBImagePullStep(null, client, sessionData);
        ss.appendStep(imagePullStep);
        MBImagePullStepController imagePullController = new MBImagePullStepController(this, imagePullStep, "MBImagePullStep.fxml");
        controllerForStep.put(imagePullStep, imagePullController);
        addLabelForStep(imagePullStep,"Load Images");
        
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
  /*      
        ScoringAlgorithms scoringAlgorithms = new ScoringAlgorithms();
        MBCharQuestionsStep charQuestionsStep = new MBCharQuestionsStep(null, scoringAlgorithms, sessionData);
        ss.appendStep(charQuestionsStep);
        MBCharQuestionsController charQuestionsStepController = new MBCharQuestionsController(charQuestionsStep, "MBCharQuestionsStep.fxml");
        controllerForStep.put(charQuestionsStep, charQuestionsStepController);
        Label loginLabel = new Label("Login");
        stepList.getChildren().add(loginLabel);
        */
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
            FXMLLoader loader = new FXMLLoader(MorphobankSessionJavaFX.class.getResource("navigationShellNoSplit.fxml"));
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
     * nextStep called from the button on the javaFX ui thread
     */
    public void nextStep(){
    	nextButton.setDisable(true);
    	backButton.setDisable(true);
    	// delegate data consumption to the javafx application thread
    	NextStepTask task = new NextStepTask();
    	new Thread(task).start();
    }
    /*
     * this one runs on thejavafx application thread
     */
    public void requestNextStep(){
    	Step step = ss.getCurrentStep();
    	StepController controller = controllerForStep.get(step);
    	boolean success = controller.consumeUIData();
    	if (success){
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
    			Alert alert = new Alert(AlertType.ERROR);
    			alert.setTitle("Error Dialog");
    			alert.setHeaderText("An error was encountered while trying to move to next screen.");
    			alert.setContentText(ace.getMessage());
    			alert.showAndWait();
    		}
			
		}
    	
    }
    public class NextStepTask extends Task<Boolean> {
        private final Logger logger = LogManager.getLogger(NextStepTask.class);
    	
        public NextStepTask(){
            
        }
        @Override
        protected Boolean call() throws Exception {
        	requestNextStep();
        	return new Boolean(true);
        	
        }
       
    }
    
}
