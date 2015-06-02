package edu.oregonstate.eecs.iis.avatolcv.morphobank.javafx;

import java.io.IOException;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;

import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import edu.oregonstate.eecs.iis.avatolcv.core.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.core.ImageInfo;
import edu.oregonstate.eecs.iis.avatolcv.morphobank.MBExclusionQualityStep;

public class MBExclusionQualityStepController implements StepController {
	public ImageView largeImageView;
    public GridPane excludeImageGrid;
    public HBox excludeImageSequence;
    public VBox imageVBox;
    private MBExclusionQualityStep step;
    private String fxmlDocName;
    private List<String> viewNames = null;
    private Hashtable<String,CheckBox> checkboxForImageIdHash = null;
    private List<ImageInfo> thumbnailImages = null;
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
                if (!(ii.isExcluded())){
                    if ((curRow == 0) && (curCol == 0)){
                    	showLargeImageForImage(ii);
                    }
                    ImageView iv = new ImageView();
                    iv.setPreserveRatio(true);
                    ImageWithInfo imageWithInfo = new ImageWithInfo("file:" + ii.getFilepath(), ii);
                    iv.setImage(imageWithInfo);
                    iv.setFitHeight(60);
                    iv.setOnMouseEntered(this::showCurrentImageLarge);
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
   
    public void showCurrentImageLarge(MouseEvent e){
    	ImageView source = (ImageView)e.getSource();
    	ImageWithInfo sourceImage = (ImageWithInfo)source.getImage();
    	ImageInfo ii = sourceImage.getImageInfo();
    	try {
    		showLargeImageForImage(ii);
    	}
    	catch(AvatolCVException ex){
    		// just don'd distplay the image
    	}
    }
    public void showLargeImageForImage(ImageInfo ii) throws AvatolCVException {
    	ImageInfo large = this.step.getLargeImageForImage(ii);
    	Image largeImage = new Image("file:" + large.getFilepath());
    	largeImageView.setImage(largeImage);
    	largeImageView.setFitHeight(300);
    }
    public Node getContentNodeOld() throws AvatolCVException {
        try {
            checkboxForImageIdHash = new Hashtable<String,CheckBox>();
            System.out.println("trying to load " +  this.fxmlDocName);
            FXMLLoader loader = new FXMLLoader(this.getClass().getResource(this.fxmlDocName));
            loader.setController(this);
            Node content = loader.load();
            excludeImageSequence.getChildren().clear();
            this.thumbnailImages = this.step.getImagesThumbnail();
            for (ImageInfo ii : thumbnailImages){
                if (!(ii.isExcluded())){
                    VBox vbox = new VBox();
                    vbox.setAlignment(Pos.CENTER);
                    CheckBox checkBox = new CheckBox("reject this image");
                    checkboxForImageIdHash.put(ii.getID(),checkBox);
                    checkBox.setSelected(false);
                    checkBox.setPadding(new Insets(4,4,4,4));
                    ImageView iv = new ImageView();
                    iv.setPreserveRatio(true);
                    //iv.setFitHeight(80);
                    Image image = new Image("file:" + ii.getFilepath());
                    iv.setImage(image);
                    vbox.getChildren().addAll(checkBox, iv);
                    excludeImageSequence.getChildren().add(vbox);
                    //hbox.setStyle("-fx-padding: 8;");
                }
            }
            content.autosize();
            return content;
        }
        catch(IOException ioe){
            throw new AvatolCVException("problem loading ui " + fxmlDocName + " for controller " + this.getClass().getName());
        } 
    }
    @Override
    public boolean hasActionToAutoStart() {
        return false;
    }
    @Override
    public void startAction() {
        // NA
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
}
