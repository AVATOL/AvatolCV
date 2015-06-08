package edu.oregonstate.eecs.iis.avatolcv.morphobank.javafx;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.animation.Interpolator;
import javafx.animation.RotateTransition;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;
import edu.oregonstate.eecs.iis.avatolcv.core.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.core.ImageInfo;
import edu.oregonstate.eecs.iis.avatolcv.javafxui.AvatolCVJavaFX;
import edu.oregonstate.eecs.iis.avatolcv.morphobank.MBExclusionQualityStep;

public class MBExclusionOrientationStepController extends MBExclusionQualityStepController {

	private Hashtable<ImageView, RotateTransition> rotaterHash = new Hashtable<ImageView, RotateTransition>();
    private static final Logger logger = LogManager.getLogger(MBExclusionOrientationStepController.class);

    public MBExclusionOrientationStepController(MBExclusionQualityStep step, String fxmlDocName){
    	super(step,fxmlDocName);
    	
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
    @Override
    public Node getContentNode() throws AvatolCVException {
        try {
            //checkboxForImageIdHash = new Hashtable<String,CheckBox>();
            System.out.println("trying to load " +  this.fxmlDocName);
            FXMLLoader loader = new FXMLLoader(this.getClass().getResource(this.fxmlDocName));
            loader.setController(this);
            Node content = loader.load();
            excludeImageGrid.getChildren().clear();
            //excludeImageGrid.set
            this.thumbnailImages = this.step.getImagesThumbnail();
            int curRow = 0;
            int curCol = 0;
            for (ImageInfo ii : thumbnailImages){
            	// show it if it's not excluded or it is, but due to a previous image orientation pass
                if (!(ii.isExcluded()) || 
                	 (  ii.isExcluded() && ImageInfo.EXCLUSION_REASON_ORIENTATION.equals(ii.getExclusionReason())  )
                	 ){
                   
                    ImageView iv = new ImageView();
                    iv.setPreserveRatio(true);
                    ImageWithInfo imageWithInfo = new ImageWithInfo("file:" + ii.getFilepath(), ii);
                    iv.setImage(imageWithInfo);
                    iv.setFitHeight(80);
                    //iv.setScaleX(-1);
                    RotateTransition rotator = createRotator(iv);
                    rotaterHash.put(iv,  rotator);
                    renderExclusionStateOfImageView(iv,ii);
                    ////iv.setOnMouseEntered(this::showCurrentImageLarge);
                    //iv.setOnMouseClicked(this::excludeOrUnexcludeImage);
                    iv.setOnMouseClicked(this::reverseImage);
                    excludeImageGrid.add(iv,curCol, curRow);
                    curRow += 1;
                    if (curRow > 5){
                        curRow = 0;
                        curCol++;
                    }
                }
            }
            content.autosize();
            return content;
        }
        catch(IOException ioe){
            throw new AvatolCVException("problem loading ui " + fxmlDocName + " for controller " + this.getClass().getName());
        } 
    }
   
    public void reverseImage(MouseEvent e){
    	ImageView iv = (ImageView)e.getSource();
    	RotateTransition rotater = rotaterHash.get(iv);
    	rotater.play();
    	File output = new File("c:\\avatol\\git\\avatol_cv\\reversedImage.jpg");

    	
    	try {
        	ImageIO.write(SwingFXUtils.fromFXImage(iv.snapshot(null, null), null), "jpg", output);
    	}
    	catch(IOException ioe){
    		
    	}


    }
    public void excludeOrUnexcludeImage(MouseEvent e){
    	try {
    		ImageView source = (ImageView)e.getSource();
        	ImageWithInfo sourceImage = (ImageWithInfo)source.getImage();
        	ImageInfo ii = sourceImage.getImageInfo();
        	if (ii.isExcluded()){
        		ii.undoExclude();
        	}
        	else {
        		ii.excludeForReason(ImageInfo.EXCLUSION_REASON_ORIENTATION);
        	}
        	renderExclusionStateOfImageView(source, ii);
    	}
    	catch(AvatolCVException ace){
    		logger.error("AvatolCV error while trying to save exclusion state");
    		logger.error(ace.getMessage());
    		Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error Dialog");
            alert.setHeaderText("AvatolCV error while trying to save exclusion state");
            alert.setContentText(ace.getMessage());
            alert.showAndWait();
    	}
    }

}
