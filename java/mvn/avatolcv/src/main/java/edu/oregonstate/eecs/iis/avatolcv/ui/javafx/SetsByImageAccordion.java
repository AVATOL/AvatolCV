package edu.oregonstate.eecs.iis.avatolcv.ui.javafx;

import java.util.List;

import edu.oregonstate.eecs.iis.avatolcv.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.AvatolCVFileSystem;
import edu.oregonstate.eecs.iis.avatolcv.core.ImageInfo;
import edu.oregonstate.eecs.iis.avatolcv.core.ImageWithInfo;
import edu.oregonstate.eecs.iis.avatolcv.scoring.ModalImageInfo;
import edu.oregonstate.eecs.iis.avatolcv.scoring.ScoringSet;
import edu.oregonstate.eecs.iis.avatolcv.session.ImagesForStep;
import javafx.scene.control.Accordion;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TitledPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;

public class SetsByImageAccordion extends Accordion {
    private ImagesForStep imageAccessor = null;

    public SetsByImageAccordion(List<ScoringSet> sets, boolean allowUserChanges) throws AvatolCVException {
    	String pathOfLargeImages = AvatolCVFileSystem.getNormalizedImagesLargeDir();
        String pathOfThumbnailImages = AvatolCVFileSystem.getNormalizedImagesThumbnailDir();
        this.imageAccessor = new ImagesForStep(pathOfLargeImages, pathOfThumbnailImages);
        for (ScoringSet ss : sets){
            TitledPane tp = new TitledPane();
            //tp.setAnimated(false);
            tp.setText(ss.getScoringConcernName());
            GridPane gp = loadGridPaneWithSetByImage(ss, allowUserChanges);
            ScrollPane sp = new ScrollPane();
            sp.setContent(gp);
            tp.setContent(sp);
            this.getPanes().add(tp);
        }
        this.setExpandedPane(this.getPanes().get(0));
    }
    public GridPane loadGridPaneWithSetByImage(ScoringSet ss, boolean allowUserChanges) throws AvatolCVException { 
        GridPane gp = new GridPane();
        System.out.println("Laying out gp for " + ss.getScoringConcernName());
        gp.setHgap(4);
        gp.setVgap(4);
        //DropShadow dsTraining = new DropShadow( 20, Color.AQUA );
        //DropShadow dsScoring = new DropShadow( 20, Color.TOMATO );
        List<ModalImageInfo> trainingImages = ss.getImagesToTrainOn();
        List<ModalImageInfo> scoringImages = ss.getImagesToScore();
        int row = 0;
        int column = 0;
        for (ModalImageInfo mii : trainingImages){
            System.out.println("training " + mii.getNormalizedImageInfo().getNiiString());
            String imageId = mii.getNormalizedImageInfo().getImageID();
            ImageView iv = getImageViewForImageID(imageId);
            Label label = new Label();
            label.setGraphic(iv);
            label.setStyle("-fx-border-color: #80808FF;-fx-border-width:4px");
            gp.add(label, column++, row);
            if (column == 10){
                column = 0;
                row++;
            }
        }
        for (ModalImageInfo mii : scoringImages){
            System.out.println("test " + mii.getNormalizedImageInfo().getNiiString());
            String imageId = mii.getNormalizedImageInfo().getImageID();
            ImageView iv = getImageViewForImageID(imageId);
            Label label = new Label();
            label.setGraphic(iv);
            label.setStyle("-fx-border-color: #80FF80;-fx-border-width:4px");
            gp.add(label, column++, row);
            if (column == 10){
                column = 0;
                row++;
            }
        }
        return gp;
    }
    public ImageView getImageViewForImageID(String imageID) throws AvatolCVException {
        ImageInfo ii = this.imageAccessor.getThumbnailImageForID(imageID);
        ImageView iv = new ImageView();
        iv.setPreserveRatio(true);
        ImageWithInfo imageWithInfo = new ImageWithInfo("file:" + ii.getFilepath(), ii);
        iv.setImage(imageWithInfo);
        iv.setFitHeight(80);
        return iv;
    }
}
