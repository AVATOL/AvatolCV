package edu.oregonstate.eecs.iis.avatolcv.ui;

import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import javafx.animation.*;
import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.*;
import javafx.scene.control.Button;
import javafx.scene.image.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;
import javafx.util.Duration;

public class FlipTest extends Application {
	private ImageView iv;
    @Override
    public void start(Stage stage) throws Exception {
        Node card = createCard();

        stage.setScene(createScene(card));
        stage.show();

        RotateTransition rotator = createRotator(card);
        rotator.play();
    }

    private Scene createScene(Node card) {
        VBox vbox = new VBox();
        Button b = new Button("save");
        b.setOnMouseClicked(this::saveImage);
        vbox.getChildren().addAll(card, b);

        Scene scene = new Scene(vbox, 600, 700, true, SceneAntialiasing.BALANCED);
        //scene.setCamera(new PerspectiveCamera());

        return scene;
    }

    private void saveImage(MouseEvent e){
    	File output = new File("c:\\avatol\\git\\avatol_cv\\reversedImage.jpg");

    	
    	try {
        	ImageIO.write(SwingFXUtils.fromFXImage(iv.snapshot(new SnapshotParameters(), null), null), "jpg", output);
    	}
    	catch(IOException ioe){
    		
    	}
    }
    private Node createCard() {
    	iv = new ImageView(
                new Image(
                		"file:C:\\avatol\\git\\avatol_cv\\sessionData\\AVAToL Computer Vision Matrix\\media\\thumbnail\\283379__thumbnail.jpg"
                    //"http://www.ohmz.net/wp-content/uploads/2012/05/Game-of-Throne-Magic-trading-cards-2.jpg"
                )
            );
        return iv;
    }

    private RotateTransition createRotator(Node card) {
        RotateTransition rotator = new RotateTransition(Duration.millis(500), card);
        rotator.setAxis(Rotate.Y_AXIS);
        rotator.setFromAngle(0);
        rotator.setToAngle(180);
        rotator.setInterpolator(Interpolator.LINEAR);
        rotator.setCycleCount(1);

        return rotator;
    }

    public static void main(String[] args) {
        launch();
    }
}