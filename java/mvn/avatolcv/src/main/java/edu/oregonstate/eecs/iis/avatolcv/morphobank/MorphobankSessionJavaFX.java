package edu.oregonstate.eecs.iis.avatolcv.morphobank;

import java.io.IOException;

import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import edu.oregonstate.eecs.iis.avatolcv.AvatolCVFileSystem;
import edu.oregonstate.eecs.iis.avatolcv.core.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.core.Step;
import edu.oregonstate.eecs.iis.avatolcv.core.StepSequence;
import edu.oregonstate.eecs.iis.avatolcv.generic.CharQuestionsStep;
import edu.oregonstate.eecs.iis.avatolcv.javafxui.AvatolCVJavaFX;
import edu.oregonstate.eecs.iis.avatolcv.ws.MorphobankWSClient;
import edu.oregonstate.eecs.iis.avatolcv.ws.MorphobankWSClientImpl;

public class MorphobankSessionJavaFX {
    private String avatolCVRootDir = null;
    private MBSessionData sessionData = null;
    private AvatolCVFileSystem afs = null;
    private StepSequence ss = null;
    private Stage mainWindow = null;
    private Scene scene = null;
  
    public void init(String avatolCVRootDir, Stage mainWindow) throws AvatolCVException {
        this.avatolCVRootDir = avatolCVRootDir;
        this.mainWindow = mainWindow;
        MorphobankWSClient client = new MorphobankWSClientImpl();
        afs = new AvatolCVFileSystem(avatolCVRootDir);
        sessionData = new MBSessionData(avatolCVRootDir);
        ss = new StepSequence();
        
        Step loginStep = new MBLoginStep("MBLoginStep.fxml", client);
        ss.appendStep(loginStep);
        Step matrixStep = new MBMatrixChoiceStep(null, client, sessionData);
        ss.appendStep(matrixStep);
        Step charChoiceStep = new MBCharChoiceStep(null, client, sessionData);
        ss.appendStep(charChoiceStep);
        Step viewChoiceStep = new MBViewChoiceStep(null, client, sessionData);
        ss.appendStep(viewChoiceStep);
        Step imagePullStep = new MBImagePullStep(null, client, sessionData);
        ss.appendStep(imagePullStep);
        Step exclusionCoachingStep = new MBExclusionCoachingStep(null, client);
        ss.appendStep(exclusionCoachingStep);
        Step exclusionStep = new MBExclusionStep(null, sessionData);
        ss.appendStep(exclusionStep);
        Step charQuestionsStep = new CharQuestionsStep(null, sessionData);
        ss.appendStep(charQuestionsStep);   
        initUI();
        activateCurrentStep();
    }
    private void activateCurrentStep() throws AvatolCVException {
        Step step = ss.getCurrentStep();
        String fxmlDoc = step.getView();
        try {
            FXMLLoader loader = new FXMLLoader(MorphobankSessionJavaFX.class.getResource(fxmlDoc));
            loader.setController(step);
            Node content = loader.load();
            Pane contentPane = (Pane)scene.lookup("#navigationShellContentPane");
            ObservableList<Node> children = contentPane.getChildren();
            if (null != children){
                contentPane.getChildren().add(content);
            }
        }
        catch(IOException ioe){
            throw new AvatolCVException("problem loading ui " + fxmlDoc + " for step " + step.getClass().getName());
        }
    }
    public void initUI() throws AvatolCVException {
        try {
            FXMLLoader loader = new FXMLLoader(MorphobankSessionJavaFX.class.getResource("navigationShell.fxml"));
            loader.setController(this);
            Parent navShell = loader.load();
            
            this.scene = new Scene(navShell, AvatolCVJavaFX.MAIN_WINDOW_WIDTH, AvatolCVJavaFX.MAIN_WINDOW_HEIGHT);
            this.mainWindow.setScene(scene);
        }
        catch(Exception e){
            throw new AvatolCVException(e.getMessage(),e);
        }
    }
    
}
