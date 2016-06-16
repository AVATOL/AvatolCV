package edu.oregonstate.eecs.iis.avatolcv.ui.javafx;

import java.util.Collections;
import java.util.Hashtable;
import java.util.List;

import edu.oregonstate.eecs.iis.avatolcv.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.core.ImageInfo;
import edu.oregonstate.eecs.iis.avatolcv.normalized.NormalizedImageInfo;
import edu.oregonstate.eecs.iis.avatolcv.normalized.NormalizedKey;
import edu.oregonstate.eecs.iis.avatolcv.normalized.NormalizedValue;
import edu.oregonstate.eecs.iis.avatolcv.session.DataIssue;
import edu.oregonstate.eecs.iis.avatolcv.session.SessionInfo;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;

public class JavaFXUtils {
	public static void dialog(String text){
		Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Error Dialog");
        alert.setHeaderText("AvatolCV error");
        alert.setContentText(text);
        alert.showAndWait();
	}
	public static Label getIssueText(DataIssue di, int issueNumber){
		Label label = new Label();
    	label.setText("" + di.getIssueText(issueNumber));
    	label.getStyleClass().add("issueText");
    	label.setPadding(new Insets(0,0,0,10));
    	//ta.setWrapText(true);
    	//ta.setPrefRowCount(3);
    	return label;
    }
	/*
	public static AnchorPane getIssueText(DataIssue di, int issueNumber){
		AnchorPane ap = new AnchorPane();
    	TextArea ta = new TextArea();
    	ap.getChildren().add(label);
    	AnchorPane.setBottomAnchor(ta, 0.0);
    	AnchorPane.setTopAnchor(ta, 0.0);
    	AnchorPane.setRightAnchor(ta, 0.0);
    	AnchorPane.setLeftAnchor(ta, 0.0);
    	ta.setCache(false);// blurriness bug
    	ta.setText("" + di.getIssueText(issueNumber));
    	ta.setStyle("-fx-border-color: black;");
    	ta.setWrapText(true);
    	ta.setPrefRowCount(3);
    	return ap;
    }
    */

    public static void populateDataInPlay(SessionInfo sessionInfo) throws AvatolCVException {
        if (!sessionInfo.getDataSource().getName().equals("morphobank")){
            return;
        }
        GridPane gp = JavaFXStepSequencer.gridPaneDataInPlaySingleton;
        gp.getChildren().clear();
        //gp.setGridLinesVisible(true);
        gp.setHgap(12.0);
        gp.setVgap(0.0);
        List<NormalizedKey> charKeys = sessionInfo.getChosenScoringConcernKeys();
        Collections.sort(charKeys);
        NormalizedKey taxon = sessionInfo.getMandatoryTrainTestConcern();
        List<NormalizedValue> taxonValues = sessionInfo.getNormalizedImageInfos().getValuesForKey(taxon);
        Collections.sort(taxonValues);
        List<NormalizedImageInfo> niis = sessionInfo.getNormalizedImageInfos().getNormalizedImageInfosForSessionWithExcluded();
        
        // make headers
        Label taxonHeaderLabel = new Label("taxon");
        decorateHeaderLabel(taxonHeaderLabel);
        gp.add(taxonHeaderLabel, 0,0);
        int charIndex = 1;
        for (NormalizedKey key : charKeys){
            Label charLabel = new Label(key.getName());
            decorateHeaderLabel(charLabel);
            gp.add(charLabel, charIndex++, 0);
        }
        int taxonIndex = 1;
        for (NormalizedValue value : taxonValues){
            Label taxonLabel = new Label(value.getName());
            decorateHeaderLabel(taxonLabel);
            gp.add(taxonLabel, 0, taxonIndex++);
        }
        Hashtable<String,VBox> vboxHash = new Hashtable<String, VBox>();
        for (int i = 0; i < charKeys.size(); i++){
            for (int j = 0; j < taxonValues.size(); j++){
                VBox vbox = new VBox();
                vbox.getStyleClass().add("inPlayCell");
                //Label d1 = new Label(charKeys.get(i).getName());
                //Label d2 = new Label(taxonValues.get(j).getName());
                //vbox.getChildren().add(d1);
                //vbox.getChildren().add(d2);
                int gpRow = j + 1;
                int gpCol = i + 1;
                gp.add(vbox, gpCol, gpRow);
                String lookupKey = getKey(charKeys.get(i),taxonValues.get(j));
                vboxHash.put(lookupKey, vbox);
            }
        }
        for (NormalizedKey charKey : charKeys){
            for (NormalizedImageInfo nii : niis){
                if (nii.hasKey(charKey)){
                    NormalizedValue nval = nii.getValueForKey(taxon);
                    String hashKey = getKey(charKey, nval);
                    VBox vbox = vboxHash.get(hashKey);
                    if (vbox == null){
                        System.out.println("could not find vbox for key " + hashKey);
                    }
                    else {
                        vbox.getChildren().add(getLabelForStateOfNii(nii, charKey));
                    }
                }
            }
        }
    }
    public static Label getLabelForStateOfNii(NormalizedImageInfo nii, NormalizedKey charKey) throws AvatolCVException {
        if (nii.isExcluded()){
            String exclusionReason = ImageInfo.getExclusionReason(nii.getImageID());
            Label label = new Label(exclusionReason);
            decorateCellLabel(label);
            label.setDisable(true);
            return label;
        }
        else if (nii.isExcludedByValueForKey(charKey)){
            String exclusionReason = nii.getValueForKey(charKey).getName();
            Label label = new Label(exclusionReason);
            decorateCellLabel(label);
            label.setDisable(true);
            return label;
        }
        else if (nii.hasValueForKey(charKey)){
            if (nii.getAnnotationCoordinates().equals("")){
                Label label = new Label("X");
                decorateCellLabel(label);
                label.getStyleClass().add("inPlayCellYellow");
                return label;
            }
            else {
                Label label = new Label("S");
                decorateCellLabel(label);
                label.getStyleClass().add("inPlayCellGreen");
                return label;
            }
        }
        else {
            Label label = new Label("?");
            decorateCellLabel(label);
            label.getStyleClass().add("inPlayCellWhite");
            return label;
        }
        
    }
    public static String getKey(NormalizedKey charKey, NormalizedValue taxonVal){
        return taxonVal.getName() + charKey.getName();
    }
    public static void decorateHeaderLabel(Label l){
        l.getStyleClass().add("resultSummaryHeading");
        l.setAlignment(Pos.CENTER);
        l.setPadding(new Insets(4,4,4,4));
    }
    public static void decorateCellLabel(Label l){
        l.getStyleClass().add("inPlayNiiInfo");
        l.setAlignment(Pos.CENTER);
        l.setMaxWidth(Double.MAX_VALUE);
        l.setMaxHeight(Double.MAX_VALUE);
        l.setPrefHeight(27);
    }
	public static void populateIssues(List<DataIssue> dataIssues){
		VBox vBox = JavaFXStepSequencer.vBoxDataIssuesSingleton;
		vBox.getChildren().clear();
		for (int i = 0; i < dataIssues.size() ; i++){
	    	DataIssue di = dataIssues.get(i);
	    	Label label = JavaFXUtils.getIssueText(di, i+1);
	    	vBox.getChildren().add(label);
	    }
		String s = dataIssues.size() + " issues detected in the data - see Issues pane";
		if (dataIssues.size() == 0){
			if (JavaFXStepSequencer.issueCountLabelSingleton.getStyleClass().contains("issueAlert")){
				JavaFXStepSequencer.issueCountLabelSingleton.getStyleClass().remove("issueAlert");
			}
		}
		else {
			if (!JavaFXStepSequencer.issueCountLabelSingleton.getStyleClass().contains("issueAlert")){
				JavaFXStepSequencer.issueCountLabelSingleton.getStyleClass().add("issueAlert");
			}
		}
		JavaFXStepSequencer.issueCountLabelSingleton.setText(s);
	}
	public static void clearIssues(VBox vBox){
		vBox.getChildren().clear();
		JavaFXStepSequencer.issueCountLabelSingleton.setText("");
		if (JavaFXStepSequencer.issueCountLabelSingleton.getStyleClass().contains("issueAlert")){
			JavaFXStepSequencer.issueCountLabelSingleton.getStyleClass().remove("issueAlert");
		}
	}
	public static void clearDataInPlay(){
	    GridPane gp = JavaFXStepSequencer.gridPaneDataInPlaySingleton;
	    gp.getChildren().clear();
	}
}
