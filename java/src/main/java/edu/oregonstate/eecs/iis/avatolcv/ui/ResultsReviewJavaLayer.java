package edu.oregonstate.eecs.iis.avatolcv.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeListener;

import edu.oregonstate.eecs.iis.avatolcv.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.ScoredSetMetadata;
import edu.oregonstate.eecs.iis.avatolcv.SessionDataForTaxa;
import edu.oregonstate.eecs.iis.avatolcv.SessionDataForTaxon;

public class ResultsReviewJavaLayer  {
	private SessionDataForTaxa sessionDataForTaxa = null;
	private JPanel matrixColumnPanel = null;
	private JSlider confidenceSlider = null;
	private JLabel confidenceLabel = null;
	//private ScoredSetMetadata ssm = null;
	public ResultsReviewJavaLayer(SessionDataForTaxa sessionDataForTaxa) throws AvatolCVException {
		//this.ssm = ssm;
		this.sessionDataForTaxa = sessionDataForTaxa;
		this.matrixColumnPanel = getMatrixColumnPanel(sessionDataForTaxa);
		this.confidenceSlider = getConfidenceSlider();
		this.confidenceLabel = getConfidenceLabel();
	}
	public JLabel getConfidenceLabel(){
		JLabel label = new JLabel("confidence threshold",SwingConstants.CENTER);
		label.setFont(new Font("Sans Serif",Font.PLAIN,16));
		label.setBackground(Color.white);
		label.setOpaque(true);
		return label;
	}
	public JSlider getConfidenceSlider(){
		JSlider slider = new JSlider(JSlider.HORIZONTAL,
                0, 100, 100);
		slider.setMajorTickSpacing(20);
		slider.setMinorTickSpacing(10);
		slider.setPaintTicks(true);
		slider.setPaintLabels(true);
		slider.setBackground(Color.white);
		return slider;
	}
	public JPanel getMatrixColumnPanel(SessionDataForTaxa ssdt) throws AvatolCVException {
		JPanel p = new JPanel();
		//JScrollPane scrollPane = new JScrollPane();
		//scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		//scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		//scrollPane.setViewportView(p);
		p.setLayout(new GridBagLayout());
		Color backgroundColor = new Color(230,230,255);
		p.setBackground(backgroundColor);
		int count = ssdt.getTaxonCount();
		for (int i = 0; i < count; i++){
			SessionDataForTaxon taxonData = ssdt.getSessionDataForTaxonAtIndex(i);
			String taxonName = taxonData.getTaxonName();
			JLabel label = new JLabel(taxonName);
			label.setFont(new Font("Sans Serif",Font.PLAIN,16));
			label.setBackground(backgroundColor);
			label.setOpaque(true);
			label.setForeground(Color.black);
			GridBagConstraints c = new GridBagConstraints();
			c.gridx = 0;
			c.gridy = i;
			c.weightx = 1.0;
			//c.weighty = 1.0;
			c.anchor = GridBagConstraints.NORTH;
			c.fill = GridBagConstraints.HORIZONTAL;
			c.gridheight = 1;
			c.gridwidth = 1;
			//c.ipadx = 4;
			//c.ipady = 4;
			c.insets = new Insets(2,4,2,4);
			p.add(label, c);
		}
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = count;
		c.weightx = 1.0;
		c.weighty = 1.0;
		c.anchor = GridBagConstraints.NORTH;
		c.fill = GridBagConstraints.BOTH;
		c.gridheight = 1;
		c.gridwidth = 1;
		//c.ipadx = 4;
		//c.ipady = 4;
		c.insets = new Insets(2,4,2,4);
		JPanel spacerPanel = new JPanel();
		spacerPanel.setBackground(backgroundColor);
		p.add(spacerPanel, c);
		return p;
	}
	public JPanel getMatrixColumnPanel(){
		return this.matrixColumnPanel;
	}
}
