package edu.oregonstate.eecs.iis.avatolcv.morphobank;

import java.io.IOException;
import java.util.Hashtable;

import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import edu.oregonstate.eecs.iis.avatolcv.AvatolCVFileSystem;
import edu.oregonstate.eecs.iis.avatolcv.core.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.core.ScoringAlgorithms;
import edu.oregonstate.eecs.iis.avatolcv.core.Step;
import edu.oregonstate.eecs.iis.avatolcv.core.StepSequence;
import edu.oregonstate.eecs.iis.avatolcv.generic.CharQuestionsStep;
import edu.oregonstate.eecs.iis.avatolcv.javafxui.AvatolCVJavaFX;
import edu.oregonstate.eecs.iis.avatolcv.morphobank.charscore.MBTrainingExampleCheckStep;
import edu.oregonstate.eecs.iis.avatolcv.morphobank.javafx.MBCharChoiceStepController;
import edu.oregonstate.eecs.iis.avatolcv.morphobank.javafx.MBCharQuestionsController;
import edu.oregonstate.eecs.iis.avatolcv.morphobank.javafx.MBExclusionQualityStepController;
import edu.oregonstate.eecs.iis.avatolcv.morphobank.javafx.MBImagePullStepController;
import edu.oregonstate.eecs.iis.avatolcv.morphobank.javafx.MBLoginStepController;
import edu.oregonstate.eecs.iis.avatolcv.morphobank.javafx.MBMatrixChoiceStepController;
import edu.oregonstate.eecs.iis.avatolcv.morphobank.javafx.MBTrainingExampleCheckStepController;
import edu.oregonstate.eecs.iis.avatolcv.morphobank.javafx.MBViewChoiceStepController;
import edu.oregonstate.eecs.iis.avatolcv.morphobank.javafx.StepController;
import edu.oregonstate.eecs.iis.avatolcv.segmentation.SegmentationStep;
import edu.oregonstate.eecs.iis.avatolcv.ws.MorphobankWSClient;
import edu.oregonstate.eecs.iis.avatolcv.ws.MorphobankWSClientImpl;

public class MorphobankSessionJavaFX {
    private String avatolCVRootDir = null;
    private MBSessionData sessionData = null;
    private AvatolCVFileSystem afs = null;
    private StepSequence ss = null;
    private Stage mainWindow = null;
    private Scene scene = null;
  
    private Hashtable<Step,StepController> controllerForStep = new Hashtable<Step,StepController>();
    public void init(String avatolCVRootDir, Stage mainWindow) throws AvatolCVException {
        this.avatolCVRootDir = avatolCVRootDir;
        this.mainWindow = mainWindow;
        MorphobankWSClient client = new MorphobankWSClientImpl();
        afs = new AvatolCVFileSystem(avatolCVRootDir);
        sessionData = new MBSessionData(avatolCVRootDir);
        ss = new StepSequence();
        
        MBLoginStep loginStep = new MBLoginStep(null, client);
        ss.appendStep(loginStep);
        MBLoginStepController loginController = new MBLoginStepController(loginStep,"MBLoginStep.fxml");
        controllerForStep.put(loginStep, loginController);
        
        
        MBMatrixChoiceStep matrixStep = new MBMatrixChoiceStep(null, client, sessionData);
        ss.appendStep(matrixStep);
        MBMatrixChoiceStepController matrixController = new MBMatrixChoiceStepController(matrixStep, "MBMatrixChoiceStep.fxml");
        controllerForStep.put(matrixStep, matrixController);
        
        MBCharChoiceStep charChoiceStep = new MBCharChoiceStep(null, client, sessionData);
        ss.appendStep(charChoiceStep);
        MBCharChoiceStepController charChoiceController = new MBCharChoiceStepController(charChoiceStep, "MBCharChoiceStep.fxml");
        controllerForStep.put(charChoiceStep, charChoiceController);
        
        MBViewChoiceStep viewChoiceStep = new MBViewChoiceStep(client, sessionData);
        ss.appendStep(viewChoiceStep);
        MBViewChoiceStepController viewChoiceController = new MBViewChoiceStepController(viewChoiceStep, "MBViewChoiceStep.fxml");
        controllerForStep.put(viewChoiceStep, viewChoiceController);
        
        
        MBImagePullStep imagePullStep = new MBImagePullStep(null, client, sessionData);
        ss.appendStep(imagePullStep);
        MBImagePullStepController imagePullController = new MBImagePullStepController(this, imagePullStep, "MBImagePullStep.fxml");
        controllerForStep.put(imagePullStep, imagePullController);
        
        MBExclusionQualityStep exclusionQualityStep = new MBExclusionQualityStep(null, client, sessionData);
        ss.appendStep(exclusionQualityStep);
        MBExclusionQualityStepController qualityStepController = new MBExclusionQualityStepController(exclusionQualityStep, "MBExclusionQualityStepTile.fxml");
        controllerForStep.put(exclusionQualityStep, qualityStepController);
        
        //Step exclusionStep = new MBExclusionPropertyStep(null, sessionData);
        //ss.appendStep(exclusionStep);
        
        
        //Step charQuestionsStep = new CharQuestionsStep(null, sessionData);
        //ss.appendStep(charQuestionsStep);   
        
        // Turns out that the Step and Step controller for orientation exclusion can reuse the quality step ones - same mechanisms and data flow
        MBExclusionQualityStep exclusionOrientationStep = new MBExclusionQualityStep(null, client, sessionData);
        ss.appendStep(exclusionOrientationStep);
        MBExclusionQualityStepController orientationStepController = new MBExclusionQualityStepController(exclusionOrientationStep, "MBExclusionOrientationStep.fxml");
        controllerForStep.put(exclusionOrientationStep, orientationStepController);
        
        ScoringAlgorithms scoringAlgorithms = new ScoringAlgorithms();
        MBCharQuestionsStep charQuestionsStep = new MBCharQuestionsStep(null, scoringAlgorithms, sessionData);
        ss.appendStep(charQuestionsStep);
        MBCharQuestionsController charQuestionsStepController = new MBCharQuestionsController(charQuestionsStep, "MBCharQuestionsStep.fxml");
        controllerForStep.put(charQuestionsStep, charQuestionsStepController);
        
        MBTrainingExampleCheckStep scoringTrainingExampleCheckStep = new MBTrainingExampleCheckStep(null, sessionData, client);
        ss.appendStep(scoringTrainingExampleCheckStep);
        MBTrainingExampleCheckStepController trainingExampleCheckStepController = new MBTrainingExampleCheckStepController(scoringTrainingExampleCheckStep, "MBTrainingExampleCheckStep.fxml");
        controllerForStep.put(scoringTrainingExampleCheckStep, trainingExampleCheckStepController);
        
        initUI();
        activateCurrentStep();
    }
    private void activateCurrentStep() throws AvatolCVException {
        Step step = ss.getCurrentStep();
        StepController controller = controllerForStep.get(step);
        Node contentNode = controller.getContentNode();
        AnchorPane anchorPane = (AnchorPane)scene.lookup("#navigationShellContentPane");
        anchorPane.getChildren().clear();
        anchorPane.getChildren().add(contentNode);
        AnchorPane.setBottomAnchor(contentNode, 0.0);
        AnchorPane.setTopAnchor(contentNode, 0.0);
        AnchorPane.setLeftAnchor(contentNode, 0.0);
        AnchorPane.setRightAnchor(contentNode, 0.0);
        if (controller.hasActionToAutoStart()){
            controller.startAction();
        }
    }
    public void initUI() throws AvatolCVException {
        try {
            FXMLLoader loader = new FXMLLoader(MorphobankSessionJavaFX.class.getResource("navigationShellNoSplit.fxml"));
            loader.setController(this);
            Parent navShell = loader.load();
            
            this.scene = new Scene(navShell, mainWindow.getWidth(), mainWindow.getHeight());
            this.mainWindow.setScene(scene);
        }
        catch(Exception e){
            throw new AvatolCVException(e.getMessage(),e);
        }
    }
    public void nextStep(){
    	Step step = ss.getCurrentStep();
    	StepController controller = controllerForStep.get(step);
    	boolean success = controller.consumeUIData();
    	if (success){
    		ss.next();
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
    	else {
    		controller.clearUIFields();
    	}
    	
    }
    
}
