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
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.SwingConstants;

import edu.oregonstate.eecs.iis.avatolcv.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.SessionDataForTaxa;
import edu.oregonstate.eecs.iis.avatolcv.SessionDataForTaxon;
import edu.oregonstate.eecs.iis.avatolcv.mb.MorphobankBundle;

public class ResultMatrixColumn extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static JPanel containerPanel = new JPanel();
	private SessionDataForTaxa sdft = null;
	private List<ResultMatrixCell> cells = new ArrayList<ResultMatrixCell>();
	private ResultMatrixCell focusCell = null;
	private JavaUI javaUI = null;
	private Hashtable<String, ResultMatrixCell> cellsForTaxonName = new Hashtable<String, ResultMatrixCell>();
    public ResultMatrixColumn(SessionDataForTaxa sdft, JavaUI javaUI) throws AvatolCVException {
    	System.out.println("makeing new RMC");
    	this.javaUI = javaUI;
		this.setLayout(new GridBagLayout());
		this.setBackground(ResultMatrixCell.backgroundColor);
    	this.sdft = sdft;
    	int count = sdft.getTaxonCount();
    	JLabel taxonColumnTitle = new JLabel("Taxon", SwingConstants.CENTER);
		JLabel stateColumnTitle = new JLabel("Char State", SwingConstants.CENTER);
		JLabel confidenceColumnTitle = new JLabel("Conf", SwingConstants.CENTER);
		decorateColumnTitleLabel(taxonColumnTitle);
		decorateColumnTitleLabel(stateColumnTitle);
		decorateColumnTitleLabel(confidenceColumnTitle);
		this.add(taxonColumnTitle, ResultMatrixCell.getTaxonLabelConstraints(0, true));
		this.add(stateColumnTitle, ResultMatrixCell.getStateLabelConstraints(0, true));
		this.add(confidenceColumnTitle, ResultMatrixCell.getConfidenceLabelConstraints(0, true));
    	for (int i = 0; i < count; i++){
    		SessionDataForTaxon sd = sdft.getSessionDataForTaxonAtIndex(i);
    		String taxonId = sd.getTaxonId();
    		String taxonName = this.sdft.getParentBundle().getTaxonNameForId(taxonId);
    		ResultMatrixCell cell = new ResultMatrixCell(taxonId, taxonName, sd, this);
    		this.cellsForTaxonName.put(taxonName, cell);
    		this.cells.add(cell);
    		JLabel curTaxonLabel = cell.getTaxonLabel();
    		JLabel curStateLabel = cell.getStateLabel();
    		JLabel curQualityPanel = cell.getConfidenceLabel();
    		int trueConstraintsIndex = i+1;
    		this.add(curTaxonLabel, ResultMatrixCell.getTaxonLabelConstraints(trueConstraintsIndex, false));
    		this.add(curStateLabel, ResultMatrixCell.getStateLabelConstraints(trueConstraintsIndex, false));
    		this.add(curQualityPanel, ResultMatrixCell.getConfidenceLabelConstraints(trueConstraintsIndex, false));
    	}
    	JPanel spacerPanel = new JPanel();
		spacerPanel.setBackground(ResultMatrixCell.backgroundColor);
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = count+1;
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
		configureContainerPanel();
    }
    public GridBagConstraints getContainerConstraintsForRMC(){
    	GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1.0;
		c.weighty = 1.0;
		c.anchor = GridBagConstraints.NORTH;
		c.fill = GridBagConstraints.BOTH;
		c.gridheight = 1;
		c.gridwidth = 1;
		//c.ipadx = 4;
		//c.ipady = 4;
		//c.insets = new Insets(2,4,2,4);
		return c;
    }

    public GridBagConstraints getContainerConstraintsForSliderLabel(){
    	GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 1;
		c.weightx = 1.0;
		c.weighty = 0.0;
		c.anchor = GridBagConstraints.NORTH;
		c.fill = GridBagConstraints.BOTH;
		c.gridheight = 1;
		c.gridwidth = 1;
		//c.ipadx = 4;
		//c.ipady = 4;
		c.insets = new Insets(8,0,4,0);
		return c;
    }

    public GridBagConstraints getContainerConstraintsForSlider(){
    	GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 2;
		c.weightx = 1.0;
		c.weighty = 0.0;
		c.anchor = GridBagConstraints.NORTH;
		c.fill = GridBagConstraints.BOTH;
		c.gridheight = 1;
		c.gridwidth = 1;
		//c.ipadx = 4;
		//c.ipady = 4;
		//c.insets = new Insets(2,4,2,4);
		return c;
    }
    public void configureContainerPanel(){
    	ResultMatrixColumn.containerPanel.removeAll();
    	ResultMatrixColumn.containerPanel.setLayout(new GridBagLayout());
    	ResultMatrixColumn.containerPanel.setBackground(Color.white);
    	JScrollPane scrollPane = new JScrollPane();
    	scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    	scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
    	scrollPane.setViewportView(this);
    	ResultMatrixColumn.containerPanel.add(scrollPane, getContainerConstraintsForRMC());
    	ResultMatrixColumn.containerPanel.add(getConfidenceLabel(), getContainerConstraintsForSliderLabel());
    	ResultMatrixColumn.containerPanel.add(getConfidenceSlider(), getContainerConstraintsForSlider());
    }
    public JPanel getContainingPanel(){
    	return ResultMatrixColumn.containerPanel;
    }
    public void decorateColumnTitleLabel(JLabel label){
    	label.setFont(new Font("Sans Serif",Font.BOLD,16));
    	label.setHorizontalTextPosition(SwingConstants.CENTER);
    	label.setBackground(ResultMatrixCell.titleRowColor);
    	label.setOpaque(true);
    	label.setForeground(Color.black);
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
		slider.setValue(80);
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
