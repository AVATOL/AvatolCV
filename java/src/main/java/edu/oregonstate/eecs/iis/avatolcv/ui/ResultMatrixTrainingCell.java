package edu.oregonstate.eecs.iis.avatolcv.ui;

import java.awt.Color;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.SwingConstants;

import edu.oregonstate.eecs.iis.avatolcv.SessionDataForTaxon;
import edu.oregonstate.eecs.iis.avatolcv.ui.ResultMatrixCell.ScoreConfidence;

public class ResultMatrixTrainingCell extends ResultMatrixCell {
    public ResultMatrixTrainingCell(String taxonId, String taxonName, String trueScore, SessionDataForTaxon sdft, ResultMatrixColumn rmc){
    	super( taxonId,  taxonName,  trueScore,  sdft,  rmc);
    }
    public void init(){
    	TaxonSelectionListener tsl = new TaxonSelectionListener(this, this.rmc);
    	//this.setLayout(new GridBagLayout());
    	taxonNameLabel = new JLabel(" " + taxonName);
    	taxonNameLabel.setFont(textFont);
    	taxonNameLabel.setBackground(backgroundColor);
    	taxonNameLabel.setOpaque(true);
    	taxonNameLabel.setForeground(Color.GRAY);
    	taxonNameLabel.addMouseListener(tsl);
    	
    	
    	stateLabel = new JLabel(" ", SwingConstants.CENTER);
        stateLabel.setFont(textFont);
    	stateLabel.setBackground(backgroundColor);
    	stateLabel.setOpaque(true);
    	stateLabel.setForeground(Color.black);
    	stateLabel.addMouseListener(tsl);
    	
    	trueScoreLabel = new JLabel(" " + abbreviate(trueScore), SwingConstants.CENTER);
    	trueScoreLabel.setFont(textFont);
    	trueScoreLabel.setBackground(backgroundColor);
    	trueScoreLabel.setOpaque(true);
    	trueScoreLabel.setForeground(Color.GRAY);
    	trueScoreLabel.addMouseListener(tsl);
    	
    	//this.add(taxonNameLabel,getLabelConstraints());
    	scoreConfidenceLabel = new JLabel(" " , SwingConstants.CENTER);
    	scoreConfidenceLabel.setFont(textFont);
    	scoreConfidenceLabel.setBackground(backgroundColor);
    	scoreConfidenceLabel.setOpaque(true);
    	//scoreConfidenceLabel.addMouseListener(tsl);
    	//this.add(scoreConfidencePanel, getConfidencePanelConstraints());
    }
    public void adjustToNewThreshold(double threshold){
    	// do nothing for this type of cell
    }
}
