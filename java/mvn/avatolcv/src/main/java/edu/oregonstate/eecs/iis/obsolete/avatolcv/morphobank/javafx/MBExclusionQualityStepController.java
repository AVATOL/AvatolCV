package edu.oregonstate.eecs.iis.obsolete.avatolcv.morphobank.javafx;

import java.io.IOException;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
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
import edu.oregonstate.eecs.iis.avatolcv.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.core.ImageInfo;
import edu.oregonstate.eecs.iis.avatolcv.core.StepController;
import edu.oregonstate.eecs.iis.avatolcv.javafxui.AvatolCVJavaFXMB;
import edu.oregonstate.eecs.iis.obsolete.avatolcv.morphobank.MBExclusionQualityStep;

public class MBExclusionQualityStepController implements StepController {
	public ImageView largeImageView;
    public GridPane excludeImageGrid;
    public HBox excludeImageSequence;
    public VBox imageVBox;
    protected MBExclusionQualityStep step;
    protected String fxmlDocName;
    protected List<ImageInfo> thumbnailImages = null;
    private static final Logger logger = LogManager.getLogger(MBExclusionQualityStepController.class);

    public MBExclusionQualityStepController(MBExclusionQualityStep step, String fxmlDocName){
        this.step = step;
        this.fxmlDocName = fxmlDocName;
    }
    @Override
    public boolean consumeUIData() {
    	/*
       for (ImageInfo ii : this.images){
           String id = ii.getID();
           CheckBox cb = checkboxForImageIdHash.get(id);
           if (null != cb){
               if (cb.isSelected()){
                   ii.setExclusionReason(ImageInfo.EXCLUSION_REASON_IMAGE_QUALITY);
               }
           }
       }
       this.step.acceptExclusions();
       */
       return true;
    }

    @Override
    public void clearUIFields() {
        //selectedView.setValue(viewNames.get(0));
    }
    
    
    @Override
    public Node getContentNode() throws AvatolCVException {
    	try {
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
 	        	// show it if it's not excluded or it is, but due ot a previous image quality pass
 	            if (!(ii.isExcluded()) || (ii.isExcluded() && ImageInfo.EXCLUSION_REASON_IMAGE_QUALITY.equals(ii.getExclusionReason()))){
 	                if ((curRow == 0) && (curCol == 0)){
 	                	showLargeImageForImage(ii);
 	                }
 	                ImageView iv = new ImageView();
 	                iv.setPreserveRatio(true);
 	                ImageWithInfo imageWithInfo = new ImageWithInfo("file:" + ii.getFilepath(), ii);
 	                iv.setImage(imageWithInfo);
 	                iv.setFitHeight(60);
 	                //iv.setScaleX(-1);
 	                
 	                renderExclusionStateOfImageView(iv,ii);
 	                iv.setOnMouseEntered(this::showCurrentImageLarge);
 	                iv.setOnMouseClicked(this::excludeOrUnexcludeImage);
 	                excludeImageGrid.add(iv,curCol, curRow);
 	                curRow += 1;
 	                if (curRow > 2){
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
    public void renderExclusionStateOfImageView(ImageView iv, ImageInfo ii){
    	if (ii.isExcluded()){
    		iv.setOpacity(0.4);
    	}
    	else {
    		iv.setOpacity(1.0);
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
        		ii.excludeForReason(ImageInfo.EXCLUSION_REASON_IMAGE_QUALITY);
        	}
        	renderExclusionStateOfImageView(source, ii);
        	renderExclusionStateOfImageView(largeImageView, ii);
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

    public void showCurrentImageLarge(MouseEvent e){
    	ImageView source = (ImageView)e.getSource();
    	ImageWithInfo sourceImage = (ImageWithInfo)source.getImage();
    	ImageInfo ii = sourceImage.getImageInfo();
    	try {
    		showLargeImageForImage(ii);
    	}
    	catch(AvatolCVException ex){
    		// just don't display the image
    	}
    }
    public void showLargeImageForImage(ImageInfo ii) throws AvatolCVException {
    	ImageInfo large = this.step.getLargeImageForImage(ii);
    	Image largeImage = new Image("file:" + large.getFilepath());
    	largeImageView.setImage(largeImage);
    	largeImageView.setFitHeight(300);
    	renderExclusionStateOfImageView(largeImageView, ii);
    }
    
   
    public class ImageWithInfo extends Image{
    	private ImageInfo ii = null;

		public ImageWithInfo(String arg0, ImageInfo ii) {
			super(arg0);
			this.ii = ii;
		}
    	public ImageInfo getImageInfo(){
    		return this.ii;
    	}
    }


	@Override
	public boolean delayEnableNavButtons() {
		return false;
	}
}
