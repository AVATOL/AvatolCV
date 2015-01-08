package edu.oregonstate.eecs.iis.avatolcv.ui;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;

public class StatusPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JLabel status = new JLabel();
	private JProgressBar progressBar = new JProgressBar();
	public StatusPanel(){
		
		
		//this.setBackground(Color.blue);
		//this.setLayout(new GridBagLayout());
		//this.add(status,getStatusConstraints());
		//this.add(progressBar, getProgressConstraints());
	}
	public void setLabel(JLabel label){
		this.status = label;
		this.status.setFont(ResultMatrixCell.textFont);
		this.status.setOpaque(true);
		//this.status.setBackground(Color.white);
	}
	public void setProgressBar(JProgressBar progressBar){
		this.progressBar = progressBar;
		this.progressBar.setOrientation(JProgressBar.HORIZONTAL);
	}
	public void setMessage(String message){
		String hackPaddedMessage = "  " + message;
		this.status.setText(hackPaddedMessage);
	}
	public void setProgress(int value){
		SwingUtilities.invokeLater(new ProgressBarUpdater(progressBar, value));
	}
	/*
	public static GridBagConstraints getStatusConstraints(){
    	GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 0.5;
		c.weighty = 1.0;
	    c.anchor = GridBagConstraints.WEST;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(2,4,2,4);
		return c;
    }

	public static GridBagConstraints getProgressConstraints(){
    	GridBagConstraints c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = 0;
		c.weightx = 0.5;
		c.weighty = 1.0;
	    c.anchor = GridBagConstraints.EAST;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(2,4,2,4);
		return c;
    }
    */
}
