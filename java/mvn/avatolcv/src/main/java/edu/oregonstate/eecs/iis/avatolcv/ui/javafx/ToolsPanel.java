package edu.oregonstate.eecs.iis.avatolcv.ui.javafx;

import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import edu.oregonstate.eecs.iis.avatolcv.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.AvatolCVFileSystem;
import edu.oregonstate.eecs.iis.avatolcv.javafxui.AvatolCVExceptionExpresserJavaFX;
import edu.oregonstate.eecs.iis.avatolcv.javafxui.AvatolCVJavaFX;
import edu.oregonstate.eecs.iis.avatolcv.normalized.NormalizedKey;
import edu.oregonstate.eecs.iis.avatolcv.tools.CopyDatasetTab;
import edu.oregonstate.eecs.iis.avatolcv.tools.CopyDatasetTask;
import edu.oregonstate.eecs.iis.avatolcv.tools.DatasetEditor;

public class ToolsPanel implements CopyDatasetTab {
	private static FXMLLoader loader = null;
	private static Scene toolsScene = null;
	private Stage mainWindow = null;
    private Scene originScene = null;
    private AvatolCVJavaFX mainScreen = null;
    // dataset copy
    public ChoiceBox<String> copyDatasetChoiceBox = null;
    public TextField newDatasetTextField = null;
    public TextArea copyTextArea = null;
    public Button copyButton = null;
    // dataset edit
    public ChoiceBox<String> editDatasetChoiceBox = null;
    public GridPane editDatasetGridPane = null;
    private DatasetEditor datasetEditor = new DatasetEditor();
    private Hashtable<String, List<Label>> propLabelsForNiiFilenameHashForEditor = null;
    private ArrayList<String>              niiFilenamesForEditor             = null;
    private List<Label>                    niiFilenameLabelsForEditor        = null;
    private String currentDatasetForEdit = null;
	//private Hashtable<String, String>      niiPathnameForImageNameHash     = null;

    //
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
                loadEditDatasetTab();
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
				copyDatasetChoiceBox.getItems().add(file.getName());
			}
		}
		copyDatasetChoiceBox.setValue(copyDatasetChoiceBox.getItems().get(0));
		copyDatasetChoiceBox.requestLayout();
	}
	private void loadEditDatasetTab() throws AvatolCVException {
		String sessionDirPath = AvatolCVFileSystem.getSessionsRoot();
		File sessionsDirFile = new File(sessionDirPath);
		File[] files = sessionsDirFile.listFiles();
		editDatasetChoiceBox.getItems().add(" ");
		for (File file : files){
		    if (file.isDirectory()){
		        if (DatasetEditor.isLocalDataset(file)){
	                editDatasetChoiceBox.getItems().add(file.getName());
	            }
		    }
		}
		editDatasetChoiceBox.setValue(" ");
		editDatasetChoiceBox.getSelectionModel().selectedIndexProperty().addListener(new DatasetEditChangeListener(editDatasetChoiceBox, this.datasetEditor));

		editDatasetChoiceBox.requestLayout();
	}
	public class DatasetEditChangeListener implements ChangeListener<Number> {
        private ChoiceBox<String> cb;
        private DatasetEditor editor = null;
        public DatasetEditChangeListener(ChoiceBox<String> cb, DatasetEditor editor){
            this.cb = cb;
            this.editor = editor;
        }
        @Override
        public void changed(ObservableValue ov, Number value, Number newValue) {
        	String datasetName = null;
             try {
                datasetName = (String)cb.getItems().get((Integer)newValue);
                if (" ".equals(datasetName)){
                	clearEditor();
                }
                else {
                	loadEditor(datasetName);
                }
                
            }
            catch(Exception e){
                AvatolCVExceptionExpresserJavaFX.instance.showException(e, "Problem loading editor with dataset " + datasetName + " " + e.getMessage());
            }
        }
    }
	private void decorateHeaderLabel(Label l){
		l.getStyleClass().add("columnHeader");
		GridPane.setHalignment(l, HPos.CENTER);
		l.setMaxWidth(Double.MAX_VALUE);
	}
	public void saveDatasetEdits() {
		try {
			datasetEditor.saveEdits();
			loadEditor(currentDatasetForEdit);
		}
		catch(AvatolCVException ace){
			AvatolCVExceptionExpresserJavaFX.instance.showException(ace, "Problem saving dataset edits " + ace.getMessage());
		}
	}
	public void clearDatasetEdits(){
		datasetEditor.clearEdits();
		for (String imageName : niiFilenamesForEditor){
			List<Label> propLabels = propLabelsForNiiFilenameHashForEditor.get(imageName);
			for (Label l : propLabels){
				clearLabel(l);
			}
		}
		for (Label l : niiFilenameLabelsForEditor){
			clearLabel(l);
		}
	}
	private void clearLabel(Label l){
	    l.setStyle("-fx-background-color:white;");
	}
	private void selectLabel(Label l){
	    l.setStyle("-fx-background-color:red;");
	}
	public void loadEditor(String datasetName) throws AvatolCVException {
		propLabelsForNiiFilenameHashForEditor = new Hashtable<String, List<Label>>();
    	niiFilenamesForEditor             = new ArrayList<String>();
    	niiFilenameLabelsForEditor        = new ArrayList<Label>();
    	currentDatasetForEdit = datasetName;
    	//niiPathnameForImageNameHash     = new Hashtable<String, String>();
		GridPane gp = editDatasetGridPane;
		gp.getChildren().clear();
		datasetEditor.loadDataset(datasetName);
		List<NormalizedKey> propKeys = datasetEditor.getPropKeys();
		List<String> niiFilenames = datasetEditor.getNiiFilenames();
		int row = 0;
		Label imageColumnLabel = new Label("image name");
		decorateHeaderLabel(imageColumnLabel);
		gp.add(imageColumnLabel, 0, row);
		int propNameColumnIndex = 1;
		for (NormalizedKey key : propKeys){
			Label propNameLabel = new Label(key.getName());
			decorateHeaderLabel(propNameLabel);
			gp.add(propNameLabel, propNameColumnIndex++, row);
		}
		row++;
		for (String niiFilename : niiFilenames){
			int column = 0;
			String imageName = datasetEditor.getImageNameForNiiFilename(niiFilename);
			//niiPathnameForImageNameHash.put(imageName, niiPathname);
			List<Label> propLabelList = new ArrayList<Label>();
			propLabelsForNiiFilenameHashForEditor.put(niiFilename, propLabelList);
			Label label = new Label(imageName);
			niiFilenameLabelsForEditor.add(label);
			niiFilenamesForEditor.add(niiFilename);
			label.setOnMouseClicked(new ImageLabelToggle(niiFilename));
			//imageNameforLabelHash.put(label, imageName);
			clearLabel(label);
			label.setPadding(new Insets(4,4,4,10));
			label.setMaxWidth(Double.MAX_VALUE);
			gp.add(label, column++, row);
			for (NormalizedKey propKey : propKeys){
				String propValue = datasetEditor.getValueForProperty(niiFilename,propKey);
				System.out.println(" niiName + " + niiFilename + " propKey " + propKey + " propValue " + propValue);
				if (propValue.equals("")){
					propValue = "?";
				}
				Label propLabel = new Label(propValue);
				propLabelList.add(propLabel);
				clearLabel(propLabel);
				propLabel.setMaxWidth(Double.MAX_VALUE);
				propLabel.setPadding(new Insets(4,4,4,10));
				propLabel.setOnMouseClicked(new PropLabelToggle(niiFilename, propKey));
				gp.add(propLabel, column++, row);
			}
			row++;
		}
	}
	public class PropLabelToggle implements EventHandler<MouseEvent> {
		private String imageName = null;
		private NormalizedKey propKey = null;
		public PropLabelToggle(String imageName, NormalizedKey propKey){
			this.imageName = imageName;
			this.propKey = propKey;
		}
		@Override
		public void handle(MouseEvent mouseEvent) {
			if(mouseEvent.getButton().equals(MouseButton.PRIMARY)){
	            datasetEditor.toggleEditForPropkey(imageName, propKey);
	            Label source = (Label)mouseEvent.getSource();
	            if (datasetEditor.isPropertyMarkedForDelete(imageName, propKey)){
	            	selectLabel(source);
	            }
	            else {
	            	clearLabel(source);
	            }
	        }
		}
	}
	public class ImageLabelToggle implements EventHandler<MouseEvent> {
		private String niiFilename = null;
		public ImageLabelToggle(String niiFilename){
			this.niiFilename = niiFilename;
		}
		@Override
		public void handle(MouseEvent mouseEvent) {
			if(mouseEvent.getButton().equals(MouseButton.PRIMARY)){
	            datasetEditor.toggleEditForNiiFilename(niiFilename);
	            Label source = (Label)mouseEvent.getSource();
	            List<Label> propLabels = propLabelsForNiiFilenameHashForEditor.get(niiFilename);
	            if (datasetEditor.isNiiFilenameMarkedForDelete(niiFilename)){
	            	selectLabel(source);
	            	for (Label l : propLabels){
	            		selectLabel(l);
	            	}
	            }
	            else {
	            	source.setStyle("-fx-background-color:white;");
	            	for (Label l : propLabels){
	            		clearLabel(l);
	            	}
	            }
	        }
		}
	}
	public void clearEditor(){
		editDatasetGridPane.getChildren().clear();
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
		String sourceDatasetName = copyDatasetChoiceBox.getValue();
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
