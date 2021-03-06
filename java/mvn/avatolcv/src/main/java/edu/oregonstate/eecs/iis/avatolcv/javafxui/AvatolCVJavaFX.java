package edu.oregonstate.eecs.iis.avatolcv.javafxui;


import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edu.oregonstate.eecs.iis.avatolcv.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.AvatolCVFileSystem;
import edu.oregonstate.eecs.iis.avatolcv.algorithm.AlgorithmModules;
import edu.oregonstate.eecs.iis.avatolcv.core.Defaults;
import edu.oregonstate.eecs.iis.avatolcv.ui.javafx.JavaFXStepSequencer;
import edu.oregonstate.eecs.iis.avatolcv.ui.javafx.ResultsReviewSortable;
import edu.oregonstate.eecs.iis.avatolcv.ui.javafx.ToolsPanel;
import edu.oregonstate.eecs.iis.avatolcv.util.ClassicSplitter;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public class AvatolCVJavaFX extends Application {
    public static final int MAIN_WINDOW_WIDTH = 1000;
    //public static final int MAIN_WINDOW_WIDTH = 900;
    public static final int MAIN_WINDOW_HEIGHT = 600;
    public static final String FILESEP = System.getProperty("file.separator");
    public ComboBox<String> presenceAbsenceAlgChooser = null;
    private static Scene scene;
    private static String avatolCVRootDir = null;
    private static String startError = "";
    public RadioButton radioNewSession;
    //public RadioButton radioResumeSession;
    public RadioButton radioReviewResults;
    public RadioButton radioTutorial;
    public ChoiceBox<String> priorSessionSelector;
    //public ChoiceBox<String> sessionResumeChooser;
    public GridPane frontPageGridPane;
    //public static AvatolCVExceptionExpresser exceptionExpresser = new AvatolCVExceptionExpresserJavaFX();
    
    Stage mainWindow = null;
    private static final Logger logger = LogManager.getLogger(AvatolCVJavaFX.class);
    public static void main(String[] args){
        String currentDir = System.getProperty("user.dir");
        try {
            avatolCVRootDir = findAvatolCVRoot(currentDir);
            AvatolCVFileSystem.setRootDir(avatolCVRootDir);
        }
        catch(AvatolCVException e){
            startError = "Error running avatolCV - could not locate avatol_cv directory under installation area: " + e.getMessage();
        }
        try {
            Defaults.init(avatolCVRootDir);
        }
        catch(AvatolCVException e){
            startError = "Problem loading defaults: " + e.getMessage();
            logger.fatal("Problem loading defaults: " + e.getMessage());
        }
        if ("".equals(startError)){
            try {
                AlgorithmModules.init();
            }
            catch(AvatolCVException e){
                startError = "Error running avatolCV - problem found initializing algorithm modules: " + e.getMessage();
            }
        }
        launch(args);
    }
    @Override
    public void start(Stage stage)  {
        logger.info("Starting AvatolCV.");
        try {
            if (startError.equals("")){
                this.mainWindow = stage;
                FXMLLoader loader = new FXMLLoader(getClass().getResource("avatolCvHome.fxml"));
                loader.setController(this);
                Parent root = loader.load();
                stage.setTitle("AvatolCV");
                
               
                scene = new Scene(root, MAIN_WINDOW_WIDTH, MAIN_WINDOW_HEIGHT);
                // hide these two for now
                //frontPageGridPane.getChildren().remove(radioResumeSession);
                //frontPageGridPane.getChildren().remove(sessionResumeChooser);
                
                initializePriorRunChoices(scene);
                stage.setScene(scene);
                stage.show();
            }
            else {
                logger.info("Error :" + startError);
                Alert alert = new Alert(AlertType.ERROR);
                alert.setTitle("Error Dialog");
                alert.setHeaderText("AvatolCV error on launch");
                alert.setContentText(startError);
                alert.showAndWait();
            }
            
        }
        catch(Exception e){
            System.out.println(e.getMessage());
            e.printStackTrace();
            AvatolCVExceptionExpresserJavaFX.instance.showException(e, "Problem during launch");
        }
    }
    public void showToolsPanel(){
    	try {
    		ToolsPanel toolsPanel = new ToolsPanel(this, mainWindow);
    	}
    	catch(AvatolCVException ace){
    		AvatolCVExceptionExpresserJavaFX.instance.showException(ace, "Problem showing ToolsPanel");
    	}
    }
    private void initializePriorRunChoices(Scene scene) throws AvatolCVException {
        List<String> names = AvatolCVFileSystem.getSessionFilenames();
        Collections.sort(names);
        Collections.reverse(names);
        for (String name : names){
            priorSessionSelector.getItems().add(name);
        }
        if (names.size() > 0){
        	priorSessionSelector.setValue(names.get(0));
        }
        priorSessionSelector.requestLayout();
    }
    public void launchSession(){
        try {
           
            if (radioNewSession.isSelected()){
                JavaFXStepSequencer session = new JavaFXStepSequencer(this);
                session.init(avatolCVRootDir, mainWindow);
                
            }
            //else if (radioResumeSession.isSelected()){
            //    
            //}
            else if (radioReviewResults.isSelected()){
                ResultsReviewSortable rr = new ResultsReviewSortable();
                String runName = (String)priorSessionSelector.getValue();
                //String runID = RunSummary.getRunIDFromRunSummaryFilename(runChoice);
                rr.init(this, mainWindow, runName);
            }
            else {
                // must have selected tutorial
            }    
        } 
        catch(AvatolCVException e){
            AvatolCVExceptionExpresserJavaFX.instance.showException(e, "Problem initializing session");
        }
    }
    public static String findAvatolCVRoot(String currentDir) throws AvatolCVException {
        String origCurrentDir = currentDir;
        char splitDelim = '/';
        String OS = System.getProperty("os.name").toLowerCase();
        if (OS.indexOf("win") >= 0){
            splitDelim = '\\';
        }
        
        boolean searching = true;
        while (searching && currentDir.length() > 0){
            String[] parts = ClassicSplitter.splitt(currentDir,splitDelim);
            int count = parts.length;
            String trailingString = parts[count -1];
            if (trailingString.equals("avatol_cv")){
                return currentDir;
            }
            else {
                if (parts.length == 1){
                    throw new AvatolCVException("could not locate avatol_cv directory by climbing from current dir " + origCurrentDir);
                }
                else {
                    currentDir = currentDir.replace(FILESEP + trailingString, "");
                }
            }
        }
        throw new AvatolCVException("could not locate avatol_cv directory by climbing from current dir " + origCurrentDir);
    }
}
