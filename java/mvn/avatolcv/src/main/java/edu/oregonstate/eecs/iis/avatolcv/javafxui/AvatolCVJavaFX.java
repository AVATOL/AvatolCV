package edu.oregonstate.eecs.iis.avatolcv.javafxui;

import edu.oregonstate.eecs.iis.avatolcv.core.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.morphobank.MorphobankSessionJavaFX;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;


public class AvatolCVJavaFX extends Application {
    public static final int MAIN_WINDOW_WIDTH = 800;
    public static final int MAIN_WINDOW_HEIGHT = 600;
    public static final String FILESEP = System.getProperty("file.separator");
    private static Scene scene;
    private static String rootDir = null;
    private static String startError = "";
    //public Button navigationNextButton;
    //public Button navigationBackButton;
    public RadioButton radioMBSession;
    public RadioButton radioBisqueSession;
    public RadioButton radioResumeSession;
    public RadioButton radioTutorialSession;
    Stage mainWindow = null;
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
		try {
		    if (startError.equals("")){
		        this.mainWindow = stage;
	            Parent root = FXMLLoader.load(getClass().getResource("avatolCvHome.fxml"));
	            stage.setTitle("AvatolCV");
	            scene = new Scene(root, MAIN_WINDOW_WIDTH, MAIN_WINDOW_HEIGHT);
	            scene.getStylesheets().add("javafx.css");
	            stage.setScene(scene);
	            stage.show();
		    }
		    else {
		        // TODO add dialog here
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
	       
	        if (radioMBSession.isSelected()){
	            MorphobankSessionJavaFX mbsession = new MorphobankSessionJavaFX(rootDir, mainWindow);
	        }
	        else if (radioBisqueSession.isSelected()){
	            
	        }
	        else if (radioResumeSession.isSelected()){
	            
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
