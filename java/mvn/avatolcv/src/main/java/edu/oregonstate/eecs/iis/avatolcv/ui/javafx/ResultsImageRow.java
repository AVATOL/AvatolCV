package edu.oregonstate.eecs.iis.avatolcv.ui.javafx;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import edu.oregonstate.eecs.iis.avatolcv.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.AvatolCVFileSystem;
import edu.oregonstate.eecs.iis.avatolcv.core.ImageInfo;

public class ResultsImageRow {
    private int row = 0;
    private GridPane gp = null;
    
    private Hashtable<String, ImageView> thumbnailForImagePathHash = new Hashtable<String, ImageView>();
    private List<ImageView> smallInputImages  = new ArrayList<ImageView>();
    private List<ImageView> smallOutputImages = new ArrayList<ImageView>();

    private List<ImageView> largeInputImages  = new ArrayList<ImageView>();
    private List<ImageView> largeOutputImages = new ArrayList<ImageView>();
    
    private List<String> inputImagePathnames = null;
    private List<String> outputImagePathnames = null;
    private Label largeArrowLabel = null;
    private HBox largeImageHBox = null;
    /**
     * 
     * @param imageIDs
     * @param scoredImagesDirPath
     * @param row
     * @param gp
     * @throws AvatolCVException
     * 
     * - takes a list of inputImagePathnames and outputImagePathnames
     * - load up thumbnail sized images for them
     * - clicking on any of them opens up large size image for all of them
     */
    public ResultsImageRow(List<String> inputImagePathnames, List<String> outputImagePathnames, int row, GridPane gp) throws AvatolCVException {
        this.row = row;
        this.gp = gp;
        
        this.inputImagePathnames = inputImagePathnames;
        this.outputImagePathnames = outputImagePathnames;
        findThumbnails(inputImagePathnames);
        findSmallInputImages(inputImagePathnames);
        findSmallOutputImages(outputImagePathnames);
        int column = 0;
        for (ImageView iv : smallInputImages){
            gp.add(iv, column++, row);
        }
        gp.add(new Label("rightArrow"), column++, row);
        for (ImageView iv : smallOutputImages){
            gp.add(iv, column++, row);
        }   
    }
    private void findSmallInputImages(List<String> inputImagePathnames){
        for (String imagePath : inputImagePathnames){
            ImageView iv = thumbnailForImagePathHash.get(imagePath);
            if (null == iv){
                Image image = new Image("file:" + imagePath);
                iv = new ImageView(image);
                iv.setPreserveRatio(true);
                iv.setFitHeight((new Double(ImageInfo.IMAGE_THUMBNAIL_WIDTH)).doubleValue());
                addEventhandlerForImageClick(iv, this);
            }
            smallInputImages.add(iv);
        }
    }

    private void findSmallOutputImages(List<String> outputImagePathnames){
        for (String imagePath : outputImagePathnames){
            ImageView iv = thumbnailForImagePathHash.get(imagePath);
            if (null == iv){
                Image image = new Image("file:" + imagePath);
                iv = new ImageView(image);
                iv.setPreserveRatio(true);
                iv.setFitHeight((new Double(ImageInfo.IMAGE_THUMBNAIL_WIDTH)).doubleValue());
            }
            smallOutputImages.add(iv);
        }
    }
    private void findThumbnails(List<String> inputImagePathnames){
        for (String imagePath : inputImagePathnames){
            ImageView iv;
            try {
                String thumbnailPath = AvatolCVFileSystem.getThumbnailPathForLargeImagePath(imagePath);
                Image image = new Image("file:"+thumbnailPath);
                iv = new ImageView(image);
            }
            catch (AvatolCVException ace){
                iv = null;
            }
            thumbnailForImagePathHash.put(imagePath, iv);
        }
    }
    public int getRow(){
        return this.row;
    }
    private void addEventhandlerForImageClick(ImageView iv, ResultsImageRow ir){
        iv.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (ir.isLargeImagesShown()){
                    ir.forgetLargeImages();
                }
                else {
                    ir.showLargeImages();
                }
                event.consume();
            }
       });
    }
    private ImageView getLargeImage(String path){
        Image image = new Image("file:"+path);
        ImageView iv = new ImageView(image);
        iv.setPreserveRatio(true);
        iv.setFitWidth(600);
        return iv;
    }
    private void showLargeImages(){
        int targetRowIndex = getRow() + 1;
        int column = 0;
        largeImageHBox = new HBox();
        for (String inputImagePathname : inputImagePathnames){
            ImageView iv = getLargeImage(inputImagePathname);
            largeImageHBox.getChildren().add(iv);
            column++;
        }
        largeArrowLabel = new Label("rightArrow");
        largeImageHBox.getChildren().add(largeArrowLabel);
        column++;
        for (String outputImagePathname : outputImagePathnames){
            ImageView iv = getLargeImage(outputImagePathname);
            largeImageHBox.getChildren().add(iv);
            column++;
        }
        
        gp.add(largeImageHBox, 0, targetRowIndex, column + 1, 1);
        
    }   
    public void forgetLargeImages(){
        for (ImageView iv : largeInputImages){
            this.largeImageHBox.getChildren().remove(iv);
        }
        largeInputImages.clear();
        if (null != largeArrowLabel){
            this.largeImageHBox.getChildren().remove(largeArrowLabel);
            largeArrowLabel = null;
        }
        for (ImageView iv : largeOutputImages){
            this.largeImageHBox.getChildren().remove(iv);
        }
        largeOutputImages.clear();
        this.gp.getChildren().remove(largeImageHBox);
        largeImageHBox = null;
    }
    
    public boolean isLargeImagesShown() {
        int count = largeInputImages.size() + largeOutputImages.size();
        if (count != 0){
            return true;
        }
        return false;
    }
    
}
