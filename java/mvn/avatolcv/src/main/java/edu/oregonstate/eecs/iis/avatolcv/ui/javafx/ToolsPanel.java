package edu.oregonstate.eecs.iis.avatolcv.ui.javafx;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
import edu.oregonstate.eecs.iis.avatolcv.session.ProgressPresenter;
import edu.oregonstate.eecs.iis.avatolcv.steps.DatasetChoiceStep;
import edu.oregonstate.eecs.iis.avatolcv.ui.javafx.DatasetChoiceStepController.ScoringMetadataDownloadTask;
import edu.oregonstate.eecs.iis.avatolcv.ui.javafx.ScoringConcernStepController.RemainingMetadataDownloadTask;

public class ToolsPanel {
	private static FXMLLoader loader = null;
	private static Scene toolsScene = null;
	private Stage mainWindow = null;
    private Scene originScene = null;
    private AvatolCVJavaFX mainScreen = null;
    private static final String NL = System.getProperty("line.separator");
    private static final String FILESEP = System.getProperty("file.separator");
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
	private boolean isFileAValidSessionDir(File candidateSessionDir){
		String imageInfoPath  = candidateSessionDir + FILESEP + "normalized" + FILESEP + "imageInfo";
		String imagesPath     = candidateSessionDir + FILESEP + "normalized" + FILESEP + "images";
		File f1 = new File(imageInfoPath);
		File f2 = new File(imagesPath);
		if (f1.isDirectory() && f2.isDirectory()){
			return true;
		}
		return false;
	}
	private void loadCopyDatasetTab() throws AvatolCVException {
		String sessionDirPath = AvatolCVFileSystem.getSessionsRoot();
		File sessionsDirFile = new File(sessionDirPath);
		File[] files = sessionsDirFile.listFiles();
		for (File file : files){
			if (isFileAValidSessionDir(file)){
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
		if ("".equals(newDatasetTextField)){
			dialog("Please specify a new dataset name");
			return;
		}
		String newDatasetPath = AvatolCVFileSystem.getSessionsRoot() + FILESEP + newDataset;
		File newDatasetDirFile = new File(newDatasetPath);
		if (newDatasetDirFile.exists()){
			dialog("Dataset "+ newDataset + " already exists.  Specify a different name");
			return;
		}
		newDatasetDirFile.mkdir();
		String sourceDatasetName = existingDatasetsChoiceBox.getValue();
		String sourceDatasetDir = AvatolCVFileSystem.getSessionsRoot() + FILESEP + sourceDatasetName;
		String sourceNormalizedDir = sourceDatasetDir + FILESEP + "normalized";
		String destNormalizedDir = newDatasetPath + FILESEP + "normalized";
		Task<Boolean> task = new CopyDatasetTask(sourceNormalizedDir, destNormalizedDir, copyTextArea);
        new Thread(task).start();
		
	}
	public void recursiveCopyDir(String sourceNormalizedDir, String destNormalizedDir) throws AvatolCVException {
		//File newNormalizedDir = new File(destNormalizedDir);
		Path destNormalizedPath = Paths.get(destNormalizedDir);
		Path sourceNormalizedPath = Paths.get(sourceNormalizedDir);
		try {
            Platform.runLater(() -> copyButton.setDisable(true));
            Files.walk(sourceNormalizedPath).forEach(path ->{
                    try {
                    	Platform.runLater(() -> copyTextArea.appendText("copying " + path + NL));
                        Files.copy(path, Paths.get(path.toString().replace(
                        		sourceNormalizedPath.toString(),
                        		destNormalizedPath.toString())));
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
            });
            Platform.runLater(() -> copyTextArea.appendText("copy complete! " + NL));
            Platform.runLater(() -> copyButton.setDisable(false));
        } catch (IOException e1) {
            throw new AvatolCVException("problem copying dataset: " + e1.getMessage());
        }
	}
    public class CopyDatasetTask extends Task<Boolean> {
        private String sourceNormalizedDir;
        private String destNormalizedDir;
        private TextArea textArea;
        
        public CopyDatasetTask(String sourceNormalizedDir, String destNormalizedDir, TextArea textArea){
            this.sourceNormalizedDir = sourceNormalizedDir;
            this.destNormalizedDir = destNormalizedDir;
            this.textArea = textArea;
        }
        @Override
        protected Boolean call() throws Exception {
            try {
            	recursiveCopyDir(sourceNormalizedDir,destNormalizedDir);
                return new Boolean(true);
            }
            catch(AvatolCVException ace){
                AvatolCVExceptionExpresserJavaFX.instance.showException(ace, "AvatolCV error copying dataset");
                return new Boolean(false);
            }
        }
    }
}
