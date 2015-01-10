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
	public static Font textFont= new Font("Sans Serif",Font.PLAIN,16);
	public static Font boldTextFont= new Font("Sans Serif",Font.BOLD,16);
	public static Color entrySelectionColor = new Color(200,200,255);
	public static Color headerColor = Color.blue;
	public static Color aboveThresholdColor = new Color(100,255,100);
	public static Color belowThresholdColor = new Color(255,100,100);
	public static Color backgroundColor = new Color(255,255,255);
	//public static Color titleRowColor = new Color(210,210,255);
	public static Color titleRowColor = Color.white;
	enum ScoreConfidence {
		Unknown,
		GoodEnough,
		NotGoodEnough
	};
	protected SessionDataForTaxon sdft = null;
	protected boolean isFocus = false;
	protected String taxonId = null;
	protected String taxonName = null;
	protected ScoreConfidence curConfidenceState = ScoreConfidence.Unknown;
	protected JLabel taxonNameLabel = null;
	protected JLabel scoreConfidenceLabel = null;
	protected JLabel stateLabel = null;
	protected JLabel trueScoreLabel = null;
	protected ResultMatrixColumn rmc = null;
	protected String trueScore = null;
    public ResultMatrixCell(String taxonId, String taxonName, String trueScore, SessionDataForTaxon sdft, ResultMatrixColumn rmc){
    	this.rmc = rmc;
    	this.sdft = sdft;
    	this.taxonId = taxonId;
    	this.taxonName = taxonName;
    	this.trueScore = trueScore;
    }
    public void init(){
    	TaxonSelectionListener tsl = new TaxonSelectionListener(this, this.rmc);
    	//this.setLayout(new GridBagLayout());
    	taxonNameLabel = new JLabel(" " + taxonName);
    	taxonNameLabel.setFont(textFont);
    	taxonNameLabel.setBackground(backgroundColor);
    	taxonNameLabel.setOpaque(true);
    	taxonNameLabel.setForeground(Color.black);
    	taxonNameLabel.addMouseListener(tsl);
    	
    	String believedState = sdft.getBelievedState();
    	Font matchFont = textFont;
    	if (abbreviate(believedState).equals(abbreviate(trueScore))){
    		matchFont = boldTextFont;
    		System.out.println("ITAL");
    	}
    	
    	stateLabel = new JLabel(" " + abbreviate(believedState), SwingConstants.CENTER);
        stateLabel.setFont(matchFont);
    	stateLabel.setBackground(backgroundColor);
    	stateLabel.setOpaque(true);
    	stateLabel.setForeground(Color.black);
    	stateLabel.addMouseListener(tsl);
    	
    	trueScoreLabel = new JLabel(" " + abbreviate(trueScore), SwingConstants.CENTER);
    	trueScoreLabel.setFont(matchFont);
    	trueScoreLabel.setBackground(backgroundColor);
    	trueScoreLabel.setOpaque(true);
    	trueScoreLabel.setForeground(Color.black);
    	trueScoreLabel.addMouseListener(tsl);
    	
    	//this.add(taxonNameLabel,getLabelConstraints());
    	scoreConfidenceLabel = new JLabel("" + sdft.getCombinedScoreString(), SwingConstants.CENTER);
    	scoreConfidenceLabel.setFont(textFont);
    	scoreConfidenceLabel.setBackground(aboveThresholdColor);
    	scoreConfidenceLabel.setOpaque(true);
    	//scoreConfidenceLabel.addMouseListener(tsl);
    	//this.add(scoreConfidencePanel, getConfidencePanelConstraints());
    }
    public String abbreviate(String s){
    	String x = s.toLowerCase();
    	if (x.contains("present")){
    		return "P";
    	}
    	else if (x.contains("absent")){
    		return "A";
    	}
    	else if (x.equals("na")){
    		return "-";
    	}
    	else {
    		System.out.println("givenString was " + s);
    		return "?";
    	}
    }
    public void highlightCellForSelection(boolean value){
    	if (value){
    		this.taxonNameLabel.setBackground(entrySelectionColor);
        	this.stateLabel.setBackground(entrySelectionColor);
        	this.trueScoreLabel.setBackground(entrySelectionColor);
    	}
    	else {
    		this.taxonNameLabel.setBackground(backgroundColor);
        	this.stateLabel.setBackground(backgroundColor);
        	this.trueScoreLabel.setBackground(backgroundColor);
    	}
    }
    public void highlightCellForHover(boolean value){
    	if (!this.isFocusCell()){
    		if (value){
    			this.taxonNameLabel.setBackground(Color.white);
            	this.stateLabel.setBackground(Color.white);
            	this.trueScoreLabel.setBackground(Color.white);
    		}
    		else {
    			this.taxonNameLabel.setBackground(backgroundColor);
        		this.stateLabel.setBackground(backgroundColor);
            	this.trueScoreLabel.setBackground(backgroundColor);
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
    public JLabel getTrueScoreLabel(){
    	return this.trueScoreLabel;
    }
    public static GridBagConstraints getTaxonLabelConstraints(int i, boolean isHeader){
    	GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = i;
		c.weightx = 0.5;
		//c.weighty = 1.0;
		if (isHeader){
			c.anchor = GridBagConstraints.CENTER;
		}
		else {
			c.anchor = GridBagConstraints.WEST;
		}
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.ipadx = 10;
		//c.ipady = 4;
		//c.insets = new Insets(2,4,2,4);
		return c;
    }
    public static GridBagConstraints getStateLabelConstraints(int i, boolean isHeader){
    	GridBagConstraints c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = i;
		c.weightx = 0.3;
		//c.weighty = 1.0;
		if (isHeader){
			c.anchor = GridBagConstraints.CENTER;
		}
		else {
			c.anchor = GridBagConstraints.CENTER;
		}
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.ipadx = 10;
		//c.ipady = 4;
		//c.insets = new Insets(2,4,2,4);
		return c;
    }
    public static GridBagConstraints getConfidenceLabelConstraints(int i, boolean isHeader){
    	GridBagConstraints c = new GridBagConstraints();
		c.gridx = 3;
		c.gridy = i;
		c.weightx = 0.2;
		//c.weighty = 1.0;
		if (isHeader){
			c.anchor = GridBagConstraints.CENTER;
		}
		else {
			c.anchor = GridBagConstraints.CENTER;
		}
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.ipadx = 15;
		//c.ipady = 4;
		//c.insets = new Insets(2,4,2,4);
		return c;
    }

    public static GridBagConstraints getTrueScoreLabelConstraints(int i, boolean isHeader){
    	GridBagConstraints c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = i;
		c.weightx = 0.2;
		//c.weighty = 1.0;
		if (isHeader){
			c.anchor = GridBagConstraints.CENTER;
		}
		else {
			c.anchor = GridBagConstraints.CENTER;
		}
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.ipadx = 10;
		//c.ipady = 4;
		//c.insets = new Insets(2,4,2,4);
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
    		this.scoreConfidenceLabel.setBackground(aboveThresholdColor);
    	}
    	else {
    		this.curConfidenceState = ScoreConfidence.NotGoodEnough;
    		this.scoreConfidenceLabel.setBackground(belowThresholdColor);
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
