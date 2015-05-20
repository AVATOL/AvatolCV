package edu.oregonstate.eecs.iis.avatolcv.ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class JavaFXTestUI extends Application {
    private static Scene scene;
    public Button navigationNextButton;
    public Button navigationBackButton;
	public static void main(String[] args){
		launch(args);
	}
	@Override
	public void start(Stage s)  {
		try {
			Parent root = FXMLLoader.load(getClass().getResource("navigationShell.fxml"));
			s.setTitle("Test Application");
			scene = new Scene(root, 700, 400);
			
			s.setScene(scene);
			Pane contentPane = (Pane)scene.lookup("#contentPane");
			Node content = FXMLLoader.load(getClass().getResource("content1.fxml"));
			contentPane.getChildren().add(content);
			s.show();
		}
		catch(Exception e){
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}
	public void setContent2() throws Exception {

		Pane contentPane = (Pane)scene.lookup("#contentPane");
		Node content = FXMLLoader.load(getClass().getResource("content2.fxml"));
		contentPane.getChildren().clear();
		contentPane.getChildren().add(content);
		//navigationNextButton.setVisible(false);
	}

	public void setContent1() throws Exception {

		Pane contentPane = (Pane)scene.lookup("#contentPane");
		Node content = FXMLLoader.load(getClass().getResource("content1.fxml"));
		contentPane.getChildren().clear();
		contentPane.getChildren().add(content);
		//navigationNextButton.setVisible(false);
	}
}
