package edu.oregonstate.eecs.iis.avatolcv.ui.javafx;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import edu.oregonstate.eecs.iis.avatolcv.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.javafxui.AvatolCVJavaFX;

public class ToolsPanel {
	private static FXMLLoader loader = null;
	private static Scene toolsScene = null;
	private Stage mainWindow = null;
    private Scene originScene = null;
    private AvatolCVJavaFX mainScreen = null;
	public ToolsPanel(AvatolCVJavaFX mainScreen, Stage mainWindow) throws AvatolCVException {
        this.mainWindow = mainWindow;
        this.mainScreen = mainScreen;
        initUI();
    }
	public void initUI() throws AvatolCVException {
        try {
        	if (null == loader){
        		loader = new FXMLLoader(JavaFXStepSequencer.class.getResource("ToolsPanel.fxml"));
                loader.setController(this);
                Parent p = loader.load();
                toolsScene = new Scene(p, AvatolCVJavaFX.MAIN_WINDOW_WIDTH, AvatolCVJavaFX.MAIN_WINDOW_HEIGHT);
        	}
            this.originScene = this.mainWindow.getScene();
            this.mainWindow.setScene(toolsScene);
        }
        catch(Exception e){
            throw new AvatolCVException(e.getMessage(),e);
        }
    }
	public void hideToolsPanel(){
		this.mainWindow.setScene(this.originScene);
	}
}
