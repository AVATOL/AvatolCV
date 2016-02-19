package edu.oregonstate.eecs.iis.avatolcv.ui.javafx;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
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
    private static final String FILESEP = System.getProperty("file.separator");
    private Hashtable<String, ImageView> thumbnailForImagePathHash = new Hashtable<String, ImageView>();
    private List<ImageView> smallInputImages  = new ArrayList<ImageView>();
    private List<Node> smallOutputImages = new ArrayList<Node>();

    private List<ImageView> largeInputImages  = new ArrayList<ImageView>();
    private List<Node> largeOutputImages = new ArrayList<Node>();
    
    private List<String> inputImagePathnames = null;
    private List<String> outputImagePathnames = null;
    private ImageView largeArrow = null;
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
    public ResultsImageRow(List<String> inputImagePathnames, List<String> outputImagePathnames, int row, GridPane gp, String imageID) throws AvatolCVException {
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
        String arrowImagePath = AvatolCVFileSystem.getImagesDir() + FILESEP + "arrow2.png";
        Image arrowImage = new Image("file:" + arrowImagePath);
        ImageView arrowImageView = new ImageView(arrowImage);
        arrowImageView.setFitHeight(20);
        arrowImageView.setFitWidth(60);
        
        gp.add(arrowImageView, column++, row);
        for (Node n : smallOutputImages){
            gp.add(n, column++, row);
        }   
        CheckBox filterCheckbox = new CheckBox("exclude");
        engageCheckboxFunction(filterCheckbox, imageID);
        gp.add(filterCheckbox, column++, row);
    }
    private void greyOutImage(String imageID){
    	for (ImageView iv : smallInputImages){
    		iv.setOpacity(0.4);
    	}
    	for (Node n : smallOutputImages){
    		if (n instanceof ImageView){
    			((ImageView)n).setOpacity(0.4);
    		}
    	}
    }
    private void unGreyOutImage(String imageID){
    	for (ImageView iv : smallInputImages){
    		iv.setOpacity(1.0);
    	}
    	for (Node n : smallOutputImages){
    		if (n instanceof ImageView){
    			((ImageView)n).setOpacity(1.0);
    		}
    	}
    }
    private void engageCheckboxFunction(CheckBox cb, String imageID){
    	 cb.setOnAction((event) -> {
             boolean selected = cb.isSelected();
             if (selected){
             	try {
             		ImageInfo.excludeForSession("badSegmentation", imageID);
             		greyOutImage(imageID);
             	}
             	catch(AvatolCVException ace){
             		
             	}
             }
             else {
            	 try {
              		ImageInfo.undoExcludeForSession("badSegmentation", imageID);
              		unGreyOutImage(imageID);
              	}
              	catch(AvatolCVException ace){
              		
              	}
             }
         });
    }
    
    private void findSmallInputImages(List<String> inputImagePathnames){
        for (String imagePath : inputImagePathnames){
            ImageView iv = thumbnailForImagePathHash.get(imagePath);
            if (null == iv){
                Image image = new Image("file:" + imagePath);
                iv = new ImageView(image);
                iv.setPreserveRatio(true);
                iv.setFitWidth((new Double(ImageInfo.IMAGE_THUMBNAIL_WIDTH)).doubleValue());
            }
            addEventhandlerForImageClick(iv, this);
            smallInputImages.add(iv);
        }
    }

    private void findSmallOutputImages(List<String> outputImagePathnames){
        for (String imagePath : outputImagePathnames){
        	if (null == imagePath){
        		Label noOutputLabel = new Label("no output");
        		smallOutputImages.add(noOutputLabel);
        	}
        	else {
        		ImageView iv = thumbnailForImagePathHash.get(imagePath);
                if (null == iv){
                    Image image = new Image("file:" + imagePath);
                    iv = new ImageView(image);
                    iv.setPreserveRatio(true);
                    iv.setFitWidth((new Double(ImageInfo.IMAGE_THUMBNAIL_WIDTH)).doubleValue());
                }
                addEventhandlerForImageClick(iv, this);
                smallOutputImages.add(iv);
        	}
        }
    }
    private void findThumbnails(List<String> inputImagePathnames){
        for (String imagePath : inputImagePathnames){
            ImageView iv;
            try {
                String thumbnailPath = AvatolCVFileSystem.getThumbnailPathForLargeImagePath(imagePath);
                if (null == thumbnailPath){
                	iv = null;
                }
                else {
                	Image image = new Image("file:"+thumbnailPath);
                    iv = new ImageView(image);
                }
            }
            catch (AvatolCVException ace){
                iv = null;
            }
            if (null != iv){
            	thumbnailForImagePathHash.put(imagePath, iv);
            }
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
        int columnsForHBoxToSpanCount = 0;
        largeImageHBox = new HBox();
        for (String inputImagePathname : inputImagePathnames){
            ImageView iv = getLargeImage(inputImagePathname);
            largeImageHBox.getChildren().add(iv);
            largeInputImages.add(iv);
            columnsForHBoxToSpanCount++;
        }
        String arrowImagePath = AvatolCVFileSystem.getImagesDir() + FILESEP + "arrow2.png";
        Image image = new Image("file:" + arrowImagePath);
        largeArrow = new ImageView(image);
        largeArrow.setFitHeight(60);
        largeArrow.setFitWidth(100);
        largeImageHBox.getChildren().add(largeArrow);
        columnsForHBoxToSpanCount++;
        for (String outputImagePathname : outputImagePathnames){
            ImageView iv = getLargeImage(outputImagePathname);
            largeImageHBox.getChildren().add(iv);
            largeOutputImages.add(iv);
            columnsForHBoxToSpanCount++;
        }
        
        gp.add(largeImageHBox, 0, targetRowIndex, ++columnsForHBoxToSpanCount, 1);
        System.out.println("columnsForHBoxToSpanCount plus 1 was " + columnsForHBoxToSpanCount);
        gp.requestLayout();
        
    }   
    public void forgetLargeImages(){
        for (ImageView iv : largeInputImages){
            this.largeImageHBox.getChildren().remove(iv);
        }
        largeInputImages.clear();
        if (null != largeArrow){
            this.largeImageHBox.getChildren().remove(largeArrow);
            largeArrow = null;
        }
        for (Node n : largeOutputImages){
            this.largeImageHBox.getChildren().remove(n);
        }
        largeOutputImages.clear();
        this.gp.getChildren().remove(largeImageHBox);
        largeImageHBox = null;
        gp.requestLayout();
    }
    
    public boolean isLargeImagesShown() {
        int count = largeInputImages.size() + largeOutputImages.size();
        if (count != 0){
            return true;
        }
        return false;
    }
    
}
