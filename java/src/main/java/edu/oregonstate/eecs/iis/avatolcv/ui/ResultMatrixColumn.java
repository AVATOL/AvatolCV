package edu.oregonstate.eecs.iis.avatolcv.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingConstants;

import edu.oregonstate.eecs.iis.avatolcv.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.SessionDataForTaxa;
import edu.oregonstate.eecs.iis.avatolcv.SessionDataForTaxon;
import edu.oregonstate.eecs.iis.avatolcv.mb.MorphobankBundle;

public class ResultMatrixColumn extends JPanel {
	
	private SessionDataForTaxa sdft = null;
	private List<ResultMatrixCell> cells = new ArrayList<ResultMatrixCell>();
	private ResultMatrixCell focusCell = null;
	private int index = 0;
	private Hashtable<String, ResultMatrixCell> cellsForTaxonName = new Hashtable<String, ResultMatrixCell>();
    public ResultMatrixColumn(MorphobankBundle mb, SessionDataForTaxa sdft) throws AvatolCVException {
		this.setLayout(new GridBagLayout());
		this.setBackground(ResultMatrixCell.backgroundColor);
    	this.sdft = sdft;
    	int count = sdft.getTaxonCount();
    	for (int i = 0; i < count; i++){
    		SessionDataForTaxon sd = sdft.getSessionDataForTaxonAtIndex(i);
    		String taxonId = sd.getTaxonId();
    		String taxonName = mb.getTaxonNameForId(taxonId);
    		ResultMatrixCell cell = new ResultMatrixCell(taxonId, taxonName, sd, this);
    		this.cellsForTaxonName.put(taxonName, cell);
    		this.cells.add(cell);
    		JLabel curTaxonLabel = cell.getTaxonLabel();
    		JLabel curStateLabel = cell.getStateLabel();
    		JLabel curQualityPanel = cell.getConfidenceLabel();
    		this.add(curTaxonLabel, cell.getTaxonLabelConstraints(i));
    		this.add(curStateLabel, cell.getStateLabelConstraints(i));
    		this.add(curQualityPanel, cell.getConfidenceLabelConstraints(i));
    	}
    	JPanel spacerPanel = new JPanel();
		spacerPanel.setBackground(ResultMatrixCell.backgroundColor);
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
		this.add(spacerPanel, c);
		focusOnCell(getCellAtIndex(0));
    }
    /*public GridBagConstraints getConstraintsForCell(int i){
    	GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = i;
		c.weightx = 1.0;
		//c.weighty = 1.0;
		//c.anchor = GridBagConstraints.WEST;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridheight = 1;
		c.gridwidth = 1;
		//c.ipadx = 4;
		//c.ipady = 4;
		//c.insets = new Insets(2,4,2,4);
		return c;
    }*/
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
		slider.addChangeListener(new ConfidenceChangeListener(this));
		slider.setValue(90);
		return slider;
	}
    public void adjustToNewThreshold(double threshold){
    	for (ResultMatrixCell cell : this.cells){
    		cell.adjustToNewThreshold(threshold);
    	}
    }
    public List<String> getTaxonNames(){
    	List<String> names = new ArrayList<String>();
    	for (ResultMatrixCell cell: this.cells){
    		names.add(cell.getName());
    	}
    	return names;
    }
    public int getRowCount(){
    	return this.sdft.getTaxonCount();
    }
    
    public ResultMatrixCell getCellAtIndex(int index){
    	return this.cells.get(index);
    }
    public ResultMatrixCell getCellForTaxonName(String taxonName){
    	return this.cellsForTaxonName.get(taxonName);
    }
    
    public void focusOnCell(ResultMatrixCell cell){
    	if (null != this.focusCell){
    		this.focusCell.setFocus(false);
    	}
    	this.focusCell = cell;
    	this.focusCell.setFocus(true);
    }
    public ResultMatrixCell getFocusCell(){
    	return this.focusCell;
    }
    public ImageBrowser getActiveImageBrowser(){
    	ResultMatrixCell focusCell = getFocusCell();
    	return focusCell.getSessionDataForTaxon().getImageBrowser();
    }
}
