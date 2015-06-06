package edu.oregonstate.eecs.iis.avatolcv.javafxui;

import java.util.ArrayList;
import java.util.List;

import edu.oregonstate.eecs.iis.avatolcv.core.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.morphobank.MorphobankSessionJavaFX;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public class AvatolCVJavaFX extends Application {
    public static final int MAIN_WINDOW_WIDTH = 800;
    public static final int MAIN_WINDOW_HEIGHT = 600;
    public static final String FILESEP = System.getProperty("file.separator");
    public ComboBox<String> presenceAbsenceAlgChooser = null;
    private static Scene scene;
    private static String rootDir = null;
    private static String startError = "";
    //public Button navigationNextButton;
    //public Button navigationBackButton;
    public RadioButton radioNewSession;
    public RadioButton radioResumeSession;
    public RadioButton radioReviewResults;
    public RadioButton radioTutorial;
    
    Stage mainWindow = null;
    private static final Logger logger = LogManager.getLogger(AvatolCVJavaFX.class);
	public static void main(String[] args){
	    String currentDir = System.getProperty("user.dir");
	    try {
	        rootDir = findRoot(currentDir);
	    }
	    catch(AvatolCVException e){
	        startError = "Error running avatolCV - could not locate avatol_cv directory under installation area.";
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
	            stage.setScene(scene);
	            stage.show();
		    }
		    else {
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
		}
	}
	

	public void launchSession(){
	    System.out.println("called this");
	    try {
	       
	        if (radioNewSession.isSelected()){
	            MorphobankSessionJavaFX mbsession = new MorphobankSessionJavaFX();
	            mbsession.init(rootDir, mainWindow);
	            
	        }
	        else if (radioResumeSession.isSelected()){
	            
	        }
	        else if (radioReviewResults.isSelected()){
	            
	        }
	        else {
	            // must have selected tutorial
	            
	        }
	    } 
	    catch(Exception e){
	        //TODO - dialog box to show this error
	        System.out.println(e.getMessage());
	        e.printStackTrace();
	    }
	}
	public static String findRoot(String currentDir) throws AvatolCVException {
	    String origCurrentDir = currentDir;
        String splitDelim = "/";
	    String OS = System.getProperty("os.name").toLowerCase();
	    if (OS.indexOf("win") >= 0){
	        splitDelim = "\\\\";
	    }
	    
	    boolean searching = true;
	    while (searching && currentDir.length() > 0){
	        String[] parts = currentDir.split(splitDelim);
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
