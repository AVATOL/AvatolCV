package edu.oregonstate.eecs.iis.avatolcv.ui.javafx;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import edu.oregonstate.eecs.iis.avatolcv.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.AvatolCVFileSystem;
import edu.oregonstate.eecs.iis.avatolcv.javafxui.AvatolCVExceptionExpresserJavaFX;
import edu.oregonstate.eecs.iis.avatolcv.javafxui.AvatolCVJavaFX;
import edu.oregonstate.eecs.iis.avatolcv.tools.CopyDatasetTab;
import edu.oregonstate.eecs.iis.avatolcv.tools.CopyDatasetTask;

public class ToolsPanel implements CopyDatasetTab {
	private static FXMLLoader loader = null;
	private static Scene toolsScene = null;
	private Stage mainWindow = null;
    private Scene originScene = null;
    private AvatolCVJavaFX mainScreen = null;
    public ChoiceBox<String> existingDatasetsChoiceBox = null;
    public TextField newDatasetTextField = null;
    public TextArea copyTextArea = null;
    public Button copyButton = null;
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
                loadCopyDatasetTab();
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
	
	private void loadCopyDatasetTab() throws AvatolCVException {
		String sessionDirPath = AvatolCVFileSystem.getSessionsRoot();
		File sessionsDirFile = new File(sessionDirPath);
		File[] files = sessionsDirFile.listFiles();
		for (File file : files){
			if (CopyDatasetTask.isValidCopySource(file)){
				existingDatasetsChoiceBox.getItems().add(file.getName());
			}
		}
		existingDatasetsChoiceBox.setValue(existingDatasetsChoiceBox.getItems().get(0));
		existingDatasetsChoiceBox.requestLayout();
	}
	private void dialog(String text){
		Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Error Dialog");
        alert.setHeaderText("AvatolCV tools error");
        alert.setContentText(text);
        alert.showAndWait();
	}
	
	public void doCopy() throws AvatolCVException {
		String newDataset = newDatasetTextField.getText();
		String sourceDatasetName = existingDatasetsChoiceBox.getValue();
		if ("".equals(newDatasetTextField)){
			dialog("Please specify a new dataset name");
			return;
		}
		if (CopyDatasetTask.isNewDatasetAlreadyExist(newDataset)){
			dialog("Dataset "+ newDataset + " already exists.  Specify a different name");
			return;
		}
		Task<Boolean> task = new CopyDatasetTask( sourceDatasetName,  newDataset, this);
        new Thread(task).start();
	}
	@Override
	public void disableCopyButton() {
		Platform.runLater(() -> copyButton.setDisable(true));
	}
	@Override
	public void enableCopyButton() {
		Platform.runLater(() -> copyButton.setDisable(false));
	}
	@Override
	public void appendText(String s) {
		Platform.runLater(() -> copyTextArea.appendText(s));
	}
	
    
}
