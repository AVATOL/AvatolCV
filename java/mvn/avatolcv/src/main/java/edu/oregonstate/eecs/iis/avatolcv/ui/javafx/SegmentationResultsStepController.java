package edu.oregonstate.eecs.iis.avatolcv.ui.javafx;

import java.io.IOException;
import java.util.Hashtable;
import java.util.List;

import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import edu.oregonstate.eecs.iis.avatolcv.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.AvatolCVFileSystem;
import edu.oregonstate.eecs.iis.avatolcv.core.StepController;
import edu.oregonstate.eecs.iis.avatolcv.datasource.ChoiceItem;
import edu.oregonstate.eecs.iis.avatolcv.results.ResultsTable;
import edu.oregonstate.eecs.iis.avatolcv.results.SortableRow;
import edu.oregonstate.eecs.iis.avatolcv.steps.SegmentationResultsStep;

public class SegmentationResultsStepController implements StepController {
	private static final String FILESEP = System.getProperty("file.separator");
	public ImageView maskCroppedImage = null;
	public ImageView origCroppedImage = null;
	private String fxmlDocName = null;
	public SegmentationResultsStepController(
			SegmentationResultsStep segResultsStep, String fxmlDocName) {
		this.fxmlDocName = fxmlDocName;
	}

	@Override
	public boolean consumeUIData() {
		// NA
		return false;
	}

	@Override
	public void clearUIFields() {
		// NA

	}

	@Override
	public Node getContentNode() throws AvatolCVException {
		try {
    		FXMLLoader loader = new FXMLLoader(this.getClass().getResource(this.fxmlDocName));
            loader.setController(this);
            Node content = loader.load();
            String segOutputDir = AvatolCVFileSystem.getModulesDir() + FILESEP + "segmentation" + FILESEP + "yaoSeg" + FILESEP + "segOutput";
            Image maskImage = new Image("file:" + segOutputDir + FILESEP + "bar_croppedMask.jpg");
            Image origImage = new Image("file:" + segOutputDir + FILESEP + "bar_croppedOrig.jpg");
            maskCroppedImage.setImage(maskImage);
            origCroppedImage.setImage(origImage);
            return content;
    	}
    	catch(IOException ioe){
    		throw new AvatolCVException("problem loading ui " + fxmlDocName + " for controller " + this.getClass().getName());
    	}
	}

	@Override
	public boolean delayEnableNavButtons() {
		// NA
		return false;
	}

	@Override
	public void executeFollowUpDataLoadPhase() throws AvatolCVException {
		// NA
	}

	@Override
	public void configureUIForFollowUpDataLoadPhase() {
		// NA
	}

	@Override
	public boolean isFollowUpDataLoadPhaseComplete() {
		// NA
		return false;
	}

}
