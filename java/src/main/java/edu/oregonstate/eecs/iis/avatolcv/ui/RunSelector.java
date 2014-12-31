package edu.oregonstate.eecs.iis.avatolcv.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import edu.oregonstate.eecs.iis.avatolcv.ScoredSetMetadata;
import edu.oregonstate.eecs.iis.avatolcv.ScoredSetMetadatas;

public class RunSelector extends JPanel {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JLabel algPrompt = new JLabel("algorithm:");
    private JLabel algValue = new JLabel();
    private JLabel charPrompt = new JLabel("character:");
    private JLabel charValue = new JLabel();
    private JLabel matrixPrompt = new JLabel("matrix:");
    private JLabel matrixValue = new JLabel();
    private JLabel viewPrompt = new JLabel("view:");
    private JLabel viewValue = new JLabel();
    private JLabel splitPrompt = new JLabel("split:");
    private JLabel splitValue = new JLabel();
    private JLabel runNumberPrompt = new JLabel("Results for scoring run: ");
    private JLabel runNumberValue = new JLabel();
    private JButton prevButton = new JButton("prev");
    private JButton nextButton = new JButton("next");
    private JLabel spacer = new JLabel("         ");
    private ScoredSetMetadatas ssms = null;
    public RunSelector(ScoredSetMetadatas ssms){
    	this.ssms = ssms;
    	this.setBackground(Color.white);
    	this.setLayout(new GridBagLayout());
    	this.add(runNumberPrompt, getLabelConstraints(0));
    	this.add(runNumberValue, getLabelConstraints(1));
    	this.add(prevButton, getLabelConstraints(2));
    	this.add(nextButton, getLabelConstraints(3));
    	this.add(spacer, getLabelConstraints(4));
    	this.add(matrixPrompt, getLabelConstraints(5));
    	this.add(matrixValue, getLabelConstraints(6));
    	this.add(charPrompt, getLabelConstraints(7));
    	this.add(charValue, getLabelConstraints(8));
    	this.add(algPrompt, getLabelConstraints(9));
    	this.add(algValue, getLabelConstraints(10));
    	this.add(viewPrompt, getLabelConstraints(11));
    	this.add(viewValue, getLabelConstraints(12));
    	this.add(splitPrompt, getLabelConstraints(13));
    	this.add(splitValue, getLabelConstraints(14));
    	String key = ssms.getCurrentKey();
    	ScoredSetMetadata ssm = ssms.getScoredSetMetadataForKey(key);
    	setValues(ssm);
    	decorateLabels();
    }
    
    public void goToNextSession(){
		this.ssms.goToNextSession();
	}
	public void goToPrevSession(){
		this.ssms.goToPrevSession();
	}
	public boolean backButtonNeeded(){
		return this.ssms.backButtonNeeded();
	}
	public boolean nextButtonNeeded(){
		return this.ssms.nextButtonNeeded();
	}
	
    public void setValues(ScoredSetMetadata ssm){
    	this.algValue.setText(ssm.getAlgorithm());
    	System.out.println("character found is " + ssm.getCharacter());
    	this.charValue.setText(ssm.getCharacter());
    	this.matrixValue.setText(ssm.getMatrix());
    	this.viewValue.setText(ssm.getView());
    	this.splitValue.setText(ssm.getSplit());
    	this.runNumberValue.setText(this.ssms.getPositionInList());
    }
    public void decoratePromptLabel(JLabel label){

    	label.setHorizontalTextPosition(SwingConstants.CENTER);
    	label.setFont(new Font("Sans Serif",Font.PLAIN,16));
    	label.setBackground(Color.white);
    	label.setOpaque(true);
    }
    public void decorateValueLabel(JLabel label){
    	label.setHorizontalTextPosition(SwingConstants.CENTER);
    	label.setFont(new Font("Sans Serif",Font.PLAIN,16));
    	label.setBackground(new Color(240,240,240));
    	label.setOpaque(true);
    }
    public void decorateLabels(){
    	decoratePromptLabel(this.algPrompt);
    	decoratePromptLabel(this.charPrompt);
    	decoratePromptLabel(this.matrixPrompt);
    	decoratePromptLabel(this.viewPrompt);
    	decoratePromptLabel(this.splitPrompt);
    	decoratePromptLabel(this.runNumberPrompt);

    	decorateValueLabel(this.algValue);
    	decorateValueLabel(this.charValue);
    	decorateValueLabel(this.matrixValue);
    	decorateValueLabel(this.viewValue);
    	decorateValueLabel(this.splitValue);
    	decorateValueLabel(this.runNumberValue);
    	
    }
    public GridBagConstraints getLabelConstraints(int i){
    	GridBagConstraints c = new GridBagConstraints();
		c.gridx = i;
		c.gridy = 0;
		c.weightx = 0.0;
		c.weighty = 1.0;
		c.anchor = GridBagConstraints.CENTER;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.ipadx = 10;
		c.ipady = 4;
		c.insets = new Insets(2,4,2,4);
		return c;
    }
}
