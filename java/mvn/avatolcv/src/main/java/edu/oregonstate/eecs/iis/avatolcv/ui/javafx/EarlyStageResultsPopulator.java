package edu.oregonstate.eecs.iis.avatolcv.ui.javafx;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import edu.oregonstate.eecs.iis.avatolcv.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.algorithm.AlgorithmInput;
import edu.oregonstate.eecs.iis.avatolcv.algorithm.RunConfigFile;

public class EarlyStageResultsPopulator {
	private List<ResultsImageRow> resultsImageRows = new ArrayList<ResultsImageRow>();
    public EarlyStageResultsPopulator(GridPane resultsGridPane, RunConfigFile rcf) throws AvatolCVException {
    	resultsGridPane.getChildren().clear();
        List<String> inputImageIDs = rcf.getInputImageIDs();
        Collections.sort(inputImageIDs);
        int row = 0;
        int column = 0;
        resultsGridPane.getColumnConstraints().clear();
        
        List<String> inSuffixes = rcf.getInputSuffixList();
        for (String inSuffix : inSuffixes){
        	Label headerLabel = null;
        	if (inSuffix.equals(AlgorithmInput.NO_SUFFIX)){
        		headerLabel = new Label("raw image");
        	}
        	else {
        		headerLabel = new Label(inSuffix);
        	}
        	ColumnConstraints col = new ColumnConstraints();
        	//col.setMaxWidth(iv);
        	col.setHgrow(Priority.NEVER);
        	resultsGridPane.getColumnConstraints().add(col);
        	resultsGridPane.add(headerLabel, column++, row);
        }
        Label emptyLabel = new Label(" ");
        ColumnConstraints arrowCol = new ColumnConstraints();
        arrowCol.setHgrow(Priority.NEVER);
        //arrowCol.setPrefWidth(iv.getFitWidth());
        resultsGridPane.getColumnConstraints().add(arrowCol);
        resultsGridPane.add(emptyLabel, column++, row);
        
        List<String> outSuffixes = rcf.getOutputSuffixList();
        for (String outSuffix : outSuffixes){
        	Label headerLabel = new Label(outSuffix);
        	ColumnConstraints col = new ColumnConstraints();
        	col.setHgrow(Priority.NEVER);
        	resultsGridPane.getColumnConstraints().add(col);
        	resultsGridPane.add(headerLabel, column++, row);
        }
        Label expandingSpacerLabel = new Label(" ");
        ColumnConstraints col = new ColumnConstraints();
    	//col.setPrefWidth(Double.MAX_VALUE);
        col.setHgrow(Priority.ALWAYS);
        resultsGridPane.getColumnConstraints().add(col);
        resultsGridPane.add(expandingSpacerLabel, column++, row);
        
        Label filterHeader = new Label("filter");
        ColumnConstraints filterCol = new ColumnConstraints();
        filterCol.setHgrow(Priority.NEVER);
    	resultsGridPane.getColumnConstraints().add(filterCol);
    	resultsGridPane.add(filterHeader, column++, row);
    	
    	row++;
    	
        for (String imageID : inputImageIDs){
            List<String> inputImagePathnames = rcf.getInputImagePathnamesForImageID(imageID);
            List<String> outputImagePathnames = rcf.getOutputImagePathnamesForImageID(imageID);
            ResultsImageRow ir = new ResultsImageRow(inputImagePathnames, outputImagePathnames, row, resultsGridPane, imageID);
            resultsImageRows.add(ir);
            row += 2;
        }
    }
}
