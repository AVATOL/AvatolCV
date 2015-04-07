package edu.oregonstate.eecs.iis.avatolcv.ui;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import edu.oregonstate.eecs.iis.avatolcv.mb.Taxon;

public class TaxonTrainingSelector extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
    private JScrollPane scrollPane = new JScrollPane();
    private JPanel contentPanel = new JPanel();
    private List<String> taxonNames = new ArrayList<String>();
    private Hashtable<String, Taxon> taxonsByName = new Hashtable<String, Taxon>();
    private Hashtable<String, JCheckBox> checkBoxesByName = new Hashtable<String, JCheckBox>();
	public TaxonTrainingSelector(List<Taxon> taxa){
		this.scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		this.scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		this.setLayout(new GridBagLayout());
		this.contentPanel.setLayout(new GridBagLayout());
		this.contentPanel.setBackground(Color.white);
		int index = 0;
		for (Taxon t : taxa){
			String name = t.getName();
			System.out.println("taxon name : " + name + " id: " + t.getId());
			taxonNames.add(name);
			taxonsByName.put(name,t);
			JCheckBox c = new JCheckBox(name);
			c.setFont(ResultMatrixCell.textFont);
			c.setBackground(Color.white);
			checkBoxesByName.put(name, c);
			c.setSelected(true);
			this.contentPanel.add(c,getEntryConstraints(index));
			index++;
		}
		JLabel spacerLabel = new JLabel(" ");
		this.contentPanel.add(spacerLabel, getSpacerConstraints(index));
		this.scrollPane.setViewportView(this.contentPanel);
		this.add(this.scrollPane, getUseAllSpaceConstraints());
	}
	public boolean isAllTaxaSelected(){
		boolean allSelected = true;
		for (String s : taxonNames){
			JCheckBox c = checkBoxesByName.get(s);
			if (!c.isSelected()){
				allSelected = false;
			}
		}
		return allSelected;
	}
	public List<Taxon> getSelectedTaxa(){
		List<Taxon> result = new ArrayList<Taxon>();
		for (String s : taxonNames){
			JCheckBox c = checkBoxesByName.get(s);
			if (c.isSelected()){
				Taxon t = taxonsByName.get(s);
				result.add(t);
			}
		}
		return result;
	}
	public List<Taxon> getUnselectedTaxa(){
		List<Taxon> result = new ArrayList<Taxon>();
		for (String s : taxonNames){
			JCheckBox c = checkBoxesByName.get(s);
			if (!c.isSelected()){
				Taxon t = taxonsByName.get(s);
				result.add(t);
			}
		}
		return result;
	}
	public static GridBagConstraints getUseAllSpaceConstraints(){
    	GridBagConstraints c = new GridBagConstraints();
    	c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1.0;
		c.weighty = 1.0;
		c.anchor = GridBagConstraints.NORTHWEST;
		c.fill = GridBagConstraints.BOTH;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(4,4,4,4);
		return c;
    }

	public static GridBagConstraints getEntryConstraints(int index){
    	GridBagConstraints c = new GridBagConstraints();
    	c.gridx = 0;
		c.gridy = index;
		c.weightx = 1.0;
		c.weighty = 0.0;
		c.anchor = GridBagConstraints.NORTHWEST;
		c.fill = GridBagConstraints.BOTH;
		c.gridheight = 1;
		c.gridwidth = 1;
		return c;
    }
	public static GridBagConstraints getSpacerConstraints(int index){
    	GridBagConstraints c = new GridBagConstraints();
    	c.gridx = 0;
		c.gridy = index;
		c.weightx = 1.0;
		c.weighty = 1.0;
		c.anchor = GridBagConstraints.NORTHWEST;
		c.fill = GridBagConstraints.BOTH;
		c.gridheight = 1;
		c.gridwidth = 1;
		return c;
    }
}
