package edu.oregonstate.eecs.iis.avatolcv.ui.javafx;

import java.io.IOException;
import java.util.List;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.oregonstate.eecs.iis.avatolcv.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.core.ImageInfo;
import edu.oregonstate.eecs.iis.avatolcv.core.ImageWithInfo;
import edu.oregonstate.eecs.iis.avatolcv.core.StepController;
import edu.oregonstate.eecs.iis.avatolcv.javafxui.AvatolCVExceptionExpresserJavaFX;
import edu.oregonstate.eecs.iis.avatolcv.steps.ExclusionQualityStep;

public class ExclusionQualityStepController  implements StepController {
	private static final double DEFAULT_LARGE_IMAGE_HEIGHT = 300;
	public ImageView largeImageView;
    public GridPane excludeImageGrid;
    public HBox excludeImageSequence;
    public VBox imageVBox;
    private AnchorPane navigationShellContentPane = null;
    protected ExclusionQualityStep step;
    protected String fxmlDocName;
    protected List<ImageInfo> thumbnailImages = null;
    private static final Logger logger = LogManager.getLogger(ExclusionQualityStepController.class);

    public ExclusionQualityStepController(ExclusionQualityStep step, String fxmlDocName, AnchorPane navigationShellContentPane){
        this.step = step;
        this.fxmlDocName = fxmlDocName;
        this.navigationShellContentPane = navigationShellContentPane;
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
    		this.step.loadImages();
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
    public void renderExclusionStateOfImageView(ImageView iv, ImageInfo ii) throws AvatolCVException {
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
        		ii.excludeForReason(ImageInfo.EXCLUSION_REASON_IMAGE_QUALITY, false);
        	}
        	renderExclusionStateOfImageView(source, ii);
        	renderExclusionStateOfImageView(largeImageView, ii);
    	}
    	catch(Exception ex){
    	    AvatolCVExceptionExpresserJavaFX.instance.showException(ex, "AvatolCV error while trying to save exclusion state");
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
    	largeImageView.setPreserveRatio(true);
    	largeImageView.setFitHeight(DEFAULT_LARGE_IMAGE_HEIGHT);
    	if (false){
    		double w = largeImage.getWidth();
        	double h = largeImage.getHeight();
        	//double targetHeight = DEFAULT_LARGE_IMAGE_HEIGHT;
        	double maxWidth = this.navigationShellContentPane.getWidth();
        	
        	boolean fitToHeight = true;
        	double heightRatioToDefault = h / DEFAULT_LARGE_IMAGE_HEIGHT;
        	double howWideTheImageWouldBeWithDefault = w / heightRatioToDefault;
        	if (howWideTheImageWouldBeWithDefault > maxWidth){
        		fitToHeight = false;
        	}
        	largeImageView.setPreserveRatio(true);
        	if (fitToHeight == false){
        		largeImageView.setFitWidth(maxWidth);
        	}
        	else {
        		largeImageView.setFitHeight(DEFAULT_LARGE_IMAGE_HEIGHT);
        	}
    	}
    	
    	
    	renderExclusionStateOfImageView(largeImageView, ii);
    }
    
   
    


	@Override
	public boolean delayEnableNavButtons() {
		return false;
	}
	@Override
	public void executeFollowUpDataLoadPhase() throws AvatolCVException {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void configureUIForFollowUpDataLoadPhase() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public boolean isFollowUpDataLoadPhaseComplete() {
		// TODO Auto-generated method stub
		return false;
	}
}
