package edu.oregonstate.eecs.iis.avatolcv.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;

import edu.oregonstate.eecs.iis.avatolcv.SessionDataForTaxon;

public class ResultMatrixCell{
	enum ScoreConfidence {
		Unknown,
		GoodEnough,
		NotGoodEnough
	};
	public static final Color backgroundColor = new Color(230,230,255);
	private SessionDataForTaxon sdft = null;
	private boolean isFocus = false;
	private String taxonId = null;
	private String taxonName = null;
	private ScoreConfidence curConfidenceState = ScoreConfidence.Unknown;
	private JLabel taxonNameLabel = null;
	private JLabel scoreConfidenceLabel = null;
	private JLabel stateLabel = null;
	private ResultMatrixColumn rmc = null;
    public ResultMatrixCell(String taxonId, String taxonName, SessionDataForTaxon sdft, ResultMatrixColumn rmc){
    	this.rmc = rmc;
    	this.sdft = sdft;
    	this.taxonId = taxonId;
    	this.taxonName = taxonName;
    	TaxonSelectionListener tsl = new TaxonSelectionListener(this, this.rmc);
    	//this.setLayout(new GridBagLayout());
    	taxonNameLabel = new JLabel(taxonName);
    	taxonNameLabel.setFont(new Font("Sans Serif",Font.PLAIN,16));
    	taxonNameLabel.setBackground(backgroundColor);
    	taxonNameLabel.setOpaque(true);
    	taxonNameLabel.setForeground(Color.black);
    	taxonNameLabel.addMouseListener(tsl);
    	
    	
    	stateLabel = new JLabel(sdft.getBelievedState());
    	stateLabel.setFont(new Font("Sans Serif",Font.PLAIN,16));
    	stateLabel.setBackground(backgroundColor);
    	stateLabel.setOpaque(true);
    	stateLabel.setForeground(Color.black);
    	stateLabel.addMouseListener(tsl);
    	
    	//this.add(taxonNameLabel,getLabelConstraints());
    	Color green = Color.green;
    	scoreConfidenceLabel = new JLabel("" + sdft.getCombinedScoreString(), SwingConstants.CENTER);
    	scoreConfidenceLabel.setFont(new Font("Sans Serif",Font.PLAIN,16));
    	scoreConfidenceLabel.setBackground(green);
    	scoreConfidenceLabel.setOpaque(true);
    	//scoreConfidenceLabel.addMouseListener(tsl);
    	//this.add(scoreConfidencePanel, getConfidencePanelConstraints());
    }
    public void highlightCellForSelection(boolean value){
    	if (value){
    		this.taxonNameLabel.setBackground(Color.green);
        	this.stateLabel.setBackground(Color.green);
    	}
    	else {
    		this.taxonNameLabel.setBackground(backgroundColor);
        	this.stateLabel.setBackground(backgroundColor);
    	}
    }
    public void highlightCellForHover(boolean value){
    	if (!this.isFocusCell()){
    		if (value){
    			this.taxonNameLabel.setBackground(Color.white);
            	this.stateLabel.setBackground(Color.white);
    		}
    		else {
    			this.taxonNameLabel.setBackground(backgroundColor);
        		this.stateLabel.setBackground(backgroundColor);
    		}
    	}
    }
    
    public SessionDataForTaxon getSessionDataForTaxon(){
    	return this.sdft;
    }
    public JLabel getConfidenceLabel(){
    	return this.scoreConfidenceLabel;
    }
    public JLabel getTaxonLabel(){
    	return this.taxonNameLabel;
    }
    public JLabel getStateLabel(){
    	return this.stateLabel;
    }
    public GridBagConstraints getTaxonLabelConstraints(int i){
    	GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = i;
		c.weightx = 0.5;
		//c.weighty = 1.0;
		c.anchor = GridBagConstraints.WEST;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridheight = 1;
		c.gridwidth = 1;
		//c.ipadx = 4;
		//c.ipady = 4;
		c.insets = new Insets(2,4,2,4);
		return c;
    }
    public GridBagConstraints getStateLabelConstraints(int i){
    	GridBagConstraints c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = i;
		c.weightx = 0.3;
		//c.weighty = 1.0;
		c.anchor = GridBagConstraints.WEST;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridheight = 1;
		c.gridwidth = 1;
		//c.ipadx = 4;
		//c.ipady = 4;
		c.insets = new Insets(2,4,2,4);
		return c;
    }
    public GridBagConstraints getConfidenceLabelConstraints(int i){
    	GridBagConstraints c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = i;
		c.weightx = 0.2;
		//c.weighty = 1.0;
		c.anchor = GridBagConstraints.EAST;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridheight = 1;
		c.gridwidth = 1;
		//c.ipadx = 4;
		//c.ipady = 4;
		c.insets = new Insets(2,4,2,4);
		return c;
    }
    public boolean isFocusCell(){
    	return this.isFocus;
    }
    public void setFocus(boolean value){
    	this.isFocus = value;
    	if (value){
    		// show the imageBrowser for this taxon
    		ImageBrowser.switchToBrowser(this.sdft.getImageBrowser());
    		System.out.println("focus on cell " + this.taxonName);
    		highlightCellForSelection(true);
    	}
    	else {
    		System.out.println("unfocus cell " + this.taxonName);
    		highlightCellForSelection(false);
    	}
    }
    public String getCellScoreString(){
    	return this.sdft.getCombinedScoreString();
    }
    public double getCellScore(){
    	return this.sdft.getCombinedScore();
    }
    public ScoreConfidence getScoreConfidence(){
    	return this.curConfidenceState;
    }
    public void adjustToNewThreshold(double threshold){
    	if (getCellScore() == -1){
    		this.curConfidenceState = ScoreConfidence.Unknown;
    	}
    	else if (threshold <= getCellScore() * 100){
    		this.curConfidenceState = ScoreConfidence.GoodEnough;
    		this.scoreConfidenceLabel.setBackground(Color.green);
    	}
    	else {
    		this.curConfidenceState = ScoreConfidence.NotGoodEnough;
    		this.scoreConfidenceLabel.setBackground(Color.red);
    	}
    }
    public String getName(){
    	return this.taxonName;
    }
    public boolean hasKnownScore(){
    	if (getCellScore() == -1){
    		return false;
    	}
    	return true;
    }
}
