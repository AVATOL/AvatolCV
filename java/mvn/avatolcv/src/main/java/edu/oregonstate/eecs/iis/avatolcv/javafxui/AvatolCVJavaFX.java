package edu.oregonstate.eecs.iis.avatolcv.javafxui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;


public class AvatolCVJavaFX extends Application {
    private static Scene scene;
    //public Button navigationNextButton;
    //public Button navigationBackButton;
    ToggleGroup sessionChoice;
	public static void main(String[] args){
		launch(args);
	}
	@Override
	public void start(Stage s)  {
		try {
			Parent root = FXMLLoader.load(getClass().getResource("avatolCvHome.fxml"));
			s.setTitle("AvatolCV");
			scene = new Scene(root, 800, 600);
			scene.getStylesheets().add("javafx.css");
			s.setScene(scene);
			s.show();
		}
		catch(Exception e){
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}
	

}
