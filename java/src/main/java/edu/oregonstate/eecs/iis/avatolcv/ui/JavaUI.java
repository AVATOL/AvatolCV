package edu.oregonstate.eecs.iis.avatolcv.ui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.border.EmptyBorder;

import edu.oregonstate.eecs.iis.avatolcv.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.ScoredSetMetadatas;
import edu.oregonstate.eecs.iis.avatolcv.SessionDataForTaxa;
import edu.oregonstate.eecs.iis.avatolcv.mb.MorphobankBundle;
import edu.oregonstate.eecs.iis.avatolcv.mb.MorphobankData;

public class JavaUI {
     private MorphobankData mbData = null;
     private ScoredSetMetadatas ssms = null;
     private ResultMatrixColumn currentMatrixColumn = null;
     private RunSelector currentRunSelector = null;
     private MorphobankBundle currentMorphobankBundle = null;
     private String currentMatrixName = null;
     private JPanel containingPanel = new JPanel();
     private JPanel imageBrowserHostPanel = new JPanel();
     private JSplitPane splitPane = null;
     private JScrollPane scrollPane = new JScrollPane();
     public JavaUI(MorphobankData mbData){
    	 this.mbData = mbData;
    	 this.containingPanel.setLayout(new GridBagLayout());
    	 this.containingPanel.setBorder(new EmptyBorder(new Insets(2,2,2,2)));
    	 this.scrollPane.setViewportView(this.containingPanel);
    	 this.scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    	 this.scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
     }
     public MorphobankData getMorphobankData(){
    	 return this.mbData;
     }
     public String getCurrentMatrixName(){
    	 return this.currentMatrixName;
     }
     public void setScoredSetMetadatas(ScoredSetMetadatas ssms){
    	 this.ssms = ssms;
    	 this.currentMatrixName = this.ssms.getMatrixNameFromKey(ssms.getCurrentKey());
     }
     public void createComponents(SessionDataForTaxa sdft) throws AvatolCVException {
    	 // we want to reload each time we get to review results in case another session has happened - then we need to load from the new ssms
    	 this.containingPanel.removeAll();
    	 this.currentRunSelector = new RunSelector(this.ssms, this);
    	 this.containingPanel.add(this.currentRunSelector, getConstraintsForRunSelector());
    	 
    	 this.currentMatrixColumn = new ResultMatrixColumn(sdft, this);
    	 
    	 this.imageBrowserHostPanel = ImageBrowser.getImageBrowserHostPanel();
    	 this.splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, this.currentMatrixColumn.getContainingPanel(), this.imageBrowserHostPanel);
    	 this.containingPanel.add(splitPane, getConstraintsForSplitPane());
     }
     public JScrollPane getContainingPanel(){
    	 return this.scrollPane;
     }
     public void setCurrentBundle(MorphobankBundle mb){
    	 this.currentMorphobankBundle = mb;
     }
     public MorphobankBundle getCurrentBundle(){
    	 return this.currentMorphobankBundle;
     }
     
     public GridBagConstraints getConstraintsForRunSelector(){
     	GridBagConstraints c = new GridBagConstraints();
 		c.gridx = 0;
 		c.gridy = 0;
 		c.weightx = 1.0;
 		c.weighty = 0.0;
 		c.anchor = GridBagConstraints.NORTH;
 		c.fill = GridBagConstraints.BOTH;
 		c.gridheight = 1;
 		c.gridwidth = 1;
 		return c;
     }

     public GridBagConstraints getConstraintsForSplitPane(){
     	GridBagConstraints c = new GridBagConstraints();
 		c.gridx = 0;
 		c.gridy = 1;
 		c.weightx = 1.0;
 		c.weighty = 1.0;
 		c.anchor = GridBagConstraints.NORTH;
 		c.fill = GridBagConstraints.BOTH;
 		c.gridheight = 1;
 		c.gridwidth = 1;
 		return c;
     }
}
