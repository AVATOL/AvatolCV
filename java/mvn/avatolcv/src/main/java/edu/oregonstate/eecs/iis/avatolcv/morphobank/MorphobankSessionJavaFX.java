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
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import edu.oregonstate.eecs.iis.avatolcv.AvatolCVFileSystem;
import edu.oregonstate.eecs.iis.avatolcv.core.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.core.Step;
import edu.oregonstate.eecs.iis.avatolcv.core.StepSequence;
import edu.oregonstate.eecs.iis.avatolcv.generic.CharQuestionsStep;
import edu.oregonstate.eecs.iis.avatolcv.javafxui.AvatolCVJavaFX;
import edu.oregonstate.eecs.iis.avatolcv.morphobank.javafx.MBLoginStepController;
import edu.oregonstate.eecs.iis.avatolcv.morphobank.javafx.MBMatrixChoiceStepController;
import edu.oregonstate.eecs.iis.avatolcv.morphobank.javafx.StepController;
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
        
        Step charChoiceStep = new MBCharChoiceStep(null, client, sessionData);
        ss.appendStep(charChoiceStep);
        Step viewChoiceStep = new MBViewChoiceStep(null, client, sessionData);
        ss.appendStep(viewChoiceStep);
        Step imagePullStep = new MBImagePullStep(null, client, sessionData);
        ss.appendStep(imagePullStep);
        Step exclusionCoachingStep = new MBExclusionQualityStep(null, client);
        ss.appendStep(exclusionCoachingStep);
        Step exclusionStep = new MBExclusionPropertyStep(null, sessionData);
        ss.appendStep(exclusionStep);
        Step charQuestionsStep = new CharQuestionsStep(null, sessionData);
        ss.appendStep(charQuestionsStep);   
        initUI();
        activateCurrentStep();
    }
    private void activateCurrentStep() throws AvatolCVException {
        Step step = ss.getCurrentStep();
        StepController controller = controllerForStep.get(step);
        Node contentNode = controller.getContentNode();
        ScrollPane scrollPane = (ScrollPane)scene.lookup("#navigationShellContentPane");
        scrollPane.setContent(contentNode);
    }
    public void initUI() throws AvatolCVException {
        try {
            FXMLLoader loader = new FXMLLoader(MorphobankSessionJavaFX.class.getResource("navigationShell.fxml"));
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
