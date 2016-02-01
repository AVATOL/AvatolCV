package edu.oregonstate.eecs.iis.avatolcv.ui.javafx;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import edu.oregonstate.eecs.iis.avatolcv.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.javafxui.AvatolCVJavaFX;
import edu.oregonstate.eecs.iis.avatolcv.session.RunSummary;
import edu.oregonstate.eecs.iis.avatolcv.ui.javafx.ResultsReview.RunChoiceChangeListener;

public class ToolsPanel {
	private Stage mainWindow = null;
    private Scene scene = null;
    private Scene originScene = null;
    private AvatolCVJavaFX mainScreen = null;
	public ToolsPanel(AvatolCVJavaFX mainScreen, Stage mainWindow) throws AvatolCVException {
        this.mainWindow = mainWindow;
        this.mainScreen = mainScreen;
        initUI();
    }
	public void initUI() throws AvatolCVException {
        try {
            FXMLLoader loader = new FXMLLoader(JavaFXStepSequencer.class.getResource("ToolsPanel.fxml"));
            loader.setController(this);
            Parent resultsReview = loader.load();
            this.originScene = this.mainWindow.getScene();
            this.scene = new Scene(resultsReview, AvatolCVJavaFX.MAIN_WINDOW_WIDTH, AvatolCVJavaFX.MAIN_WINDOW_HEIGHT);
            this.mainWindow.setScene(scene);
            
        }
        catch(Exception e){
            throw new AvatolCVException(e.getMessage(),e);
        }
    }
	public void hideToolsPanel(){
		this.mainWindow.setScene(this.originScene);
	}
}
