package edu.oregonstate.eecs.iis.avatolcv.ui.javafx;

import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Accordion;
import javafx.scene.control.TitledPane;
import javafx.stage.Stage;
import edu.oregonstate.eecs.iis.avatolcv.AvatolCVFileSystem;
import edu.oregonstate.eecs.iis.avatolcv.core.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.core.AvatolCVExceptionExpresser;
import edu.oregonstate.eecs.iis.avatolcv.javafxui.AvatolCVJavaFXMB;

public class ResultsReview {
    public Accordion runDetailsAccordion = null;
    private AvatolCVExceptionExpresser exceptionExpresser = null;
    private Stage mainWindow = null;
    private Scene scene = null;
    private AvatolCVFileSystem afs = null;
    
    public ResultsReview(AvatolCVExceptionExpresser exceptionExpresser){
        this.exceptionExpresser = exceptionExpresser;
    }
    public void init(String avatolCVRootDir, Stage mainWindow) throws AvatolCVException {
        this.mainWindow = mainWindow;
        afs = new AvatolCVFileSystem(avatolCVRootDir);
        initUI();
    }
    public void initUI() throws AvatolCVException {
        try {
            FXMLLoader loader = new FXMLLoader(JavaFXStepSequencer.class.getResource("ResultsReview.fxml"));
            loader.setController(this);
            Parent resultsReview = loader.load();
            
            this.scene = new Scene(resultsReview, AvatolCVJavaFXMB.MAIN_WINDOW_WIDTH, AvatolCVJavaFXMB.MAIN_WINDOW_HEIGHT);
            this.mainWindow.setScene(scene);
            runDetailsAccordion.getPanes().remove(2);
            runDetailsAccordion.setExpandedPane(runDetailsAccordion.getPanes().get(0));
        }
        catch(Exception e){
            throw new AvatolCVException(e.getMessage(),e);
        }
    }
}
