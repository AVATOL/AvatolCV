package edu.oregonstate.eecs.iis.avatolcv.morphobank.javafx;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
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
import javafx.scene.control.RadioButton;
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
    public RadioButton radioFlipHorizontal;
    public RadioButton radioFlipVertical;
    public RadioButton radioExclude;
    
    //private Hashtable<ImageView, ImageInfo> imageInfoForImageViewHash = new Hashtable<ImageView, ImageInfo>();
	private Hashtable<ImageView, RotateTransition> rotaterHashYAxis = new Hashtable<ImageView, RotateTransition>();
    private Hashtable<ImageView, RotateTransition> rotaterHashXAxis = new Hashtable<ImageView, RotateTransition>();
    private Hashtable<String, ImageView> imageViewForImageIDHash = new Hashtable<String, ImageView>();
    private static final Logger logger = LogManager.getLogger(MBExclusionOrientationStepController.class);

    public MBExclusionOrientationStepController(MBExclusionQualityStep step, String fxmlDocName){
    	super(step,fxmlDocName);
    	
    }
   
    private RotateTransition createYAxisRotator(Node card) {
        RotateTransition rotator = new RotateTransition(Duration.millis(500), card);
        rotator.setAxis(Rotate.Y_AXIS);
        rotator.setFromAngle(0);
        rotator.setToAngle(180);
        rotator.setInterpolator(Interpolator.LINEAR);
        rotator.setCycleCount(1);
        return rotator;
    }
    private RotateTransition createXAxisRotator(Node card) {
        RotateTransition rotator = new RotateTransition(Duration.millis(500), card);
        rotator.setAxis(Rotate.X_AXIS);
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
                    rotateImageToMatchPriorRotationState(iv,ii);
                    //iv.setScaleX(-1);
                    RotateTransition rotatorXAxis = createXAxisRotator(iv);
                    RotateTransition rotatorYAxis = createYAxisRotator(iv);
                    rotaterHashXAxis.put(iv,  rotatorXAxis);
                    rotaterHashYAxis.put(iv,  rotatorYAxis);
                    imageViewForImageIDHash.put(ii.getID(),iv);
                    renderExclusionStateOfImageView(iv,ii);
                    ////iv.setOnMouseEntered(this::showCurrentImageLarge);
                    //iv.setOnMouseClicked(this::excludeOrUnexcludeImage);
                    iv.setOnMouseClicked(this::handleImageRequest);
                    excludeImageGrid.add(iv,curCol, curRow);
                    curRow += 1;
                    if (curRow > 4){
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
    public void rotateImageToMatchPriorRotationState(ImageView iv, ImageInfo ii){
    	if (this.step.isRotatedHorizontally(ii)){
			iv.setScaleX(-1);
		}
		if (this.step.isRotatedVertically(ii)){
			iv.setScaleY(-1);
		}
    	
    }
    public void handleImageRequest(MouseEvent e){
        ImageView iv = (ImageView)e.getSource();
        Image image = iv.getImage();
        ImageWithInfo imageWithInfo = (ImageWithInfo)image;
        ImageInfo ii = imageWithInfo.getImageInfo();
        if (radioExclude.isSelected()){
            excludeOrUnexcludeImage(ii,iv);
        }
        else if (radioFlipVertical.isSelected()){
            flipVertically(ii, iv);
        }
        else {
            flipHorizontally(ii, iv);
        }
    }
    public void flipVertically(ImageInfo ii, ImageView iv){
    	System.out.println("rotate vert");
        //RotateTransition rotater = rotaterHashXAxis.get(iv);
        //rotater.play();
        iv.setScaleY(-iv.getScaleY());
        try {
            this.step.rotateVertically(ii);
        }
        catch(AvatolCVException ace){
            logger.error("AvatolCV error while trying to save rotation state");
            logger.error(ace.getMessage());
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error Dialog");
            alert.setHeaderText("AvatolCV error while trying to save rotation state");
            alert.setContentText(ace.getMessage());
            alert.showAndWait();
        }
    }
    
    public void flipHorizontally(ImageInfo ii, ImageView iv){
    	System.out.println("rotate horiz");
        //RotateTransition rotater = rotaterHashYAxis.get(iv);
        //rotater.play();
        iv.setScaleX(-iv.getScaleX());
        try {
            this.step.rotateHorizontally(ii);
        }
        catch(AvatolCVException ace){
            logger.error("AvatolCV error while trying to save rotation state");
            logger.error(ace.getMessage());
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error Dialog");
            alert.setHeaderText("AvatolCV error while trying to save rotation state");
            alert.setContentText(ace.getMessage());
            alert.showAndWait();
        }
    }
    
    
    public BufferedImage flipBufferedImageVertical(BufferedImage src){
        AffineTransform tx=AffineTransform.getScaleInstance(-1.0,1.0);  //scaling
        tx.translate(-src.getWidth(),0);  //translating
        AffineTransformOp tr=new AffineTransformOp(tx,null);  //transforming
        return tr.filter(src, null);  //filtering
    }
       
    public BufferedImage flipBufferedImageHorizontal(BufferedImage src){
        AffineTransform tx=AffineTransform.getScaleInstance(1.0,-1.0);  //scaling
        tx.translate(0,-src.getHeight());  //translating
        AffineTransformOp tr=new AffineTransformOp(tx,null);  //transforming
        return tr.filter(src, null);  //filtering
    }
    public void excludeOrUnexcludeImage(ImageInfo ii, ImageView iv){
    	try {
        	if (ii.isExcluded()){
        		ii.undoExclude();
        	}
        	else {
        		ii.excludeForReason(ImageInfo.EXCLUSION_REASON_ORIENTATION);
        	}
        	renderExclusionStateOfImageView(iv, ii);
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
