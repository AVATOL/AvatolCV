package edu.oregonstate.eecs.iis.avatolcv.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.KeyEvent;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;

import edu.oregonstate.eecs.iis.avatolcv.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.algata.ImageSetSupplier;
import edu.oregonstate.eecs.iis.avatolcv.mb.MorphobankBundle;
import edu.oregonstate.eecs.iis.avatolcv.mb.MorphobankDataException;

public class ImageBrowser extends JPanel {
	private static JPanel imageBrowserHostPanel = new JPanel();
	private ImageNavigator trainingImageNavigator = null;
	private ImageNavigator scoredImageNavigator = null;
	private ImageNavigator unscoredImageNavigator = null;
	private String taxonName = null;
	private MorphobankBundle mb = null;
	static {
		imageBrowserHostPanel.setLayout(new GridBagLayout());
		imageBrowserHostPanel.setBackground(Color.white);
	}
	private static ImageBrowser previouslyHostedImageBrowser = null;
	private ImageSetSupplier imageSetSupplier = null;
    public ImageBrowser(ImageSetSupplier imageSetSupplier, String taxonName, MorphobankBundle mb) throws AvatolCVException {
    	this.mb = mb;
    	this.imageSetSupplier = imageSetSupplier;
    	this.taxonName = taxonName;
    	this.setLayout(new GridBagLayout());
    	populateImageBrowser(imageSetSupplier);
    	this.setBackground(Color.white);
    }
    public void populateImageBrowser(ImageSetSupplier imageSetSupplier) throws AvatolCVException {
    	this.trainingImageNavigator = new ImageNavigator(imageSetSupplier.getTrainingImageSet(), this.taxonName, ImageNavigator.DataType.training, this.mb);
    	this.scoredImageNavigator = new ImageNavigator(imageSetSupplier.getScoredImageSet(), this.taxonName, ImageNavigator.DataType.scored, this.mb);
    	this.unscoredImageNavigator = new ImageNavigator(imageSetSupplier.getUnscoredImageSet(), this.taxonName, ImageNavigator.DataType.unscored, this.mb);
    	JTabbedPane tp = getTabbedPane(trainingImageNavigator, scoredImageNavigator, unscoredImageNavigator, imageSetSupplier);
    	this.add(tp, getUseAllSpaceConstraints());
    }
    public JTabbedPane getTabbedPane(JPanel trainingPanel, JPanel scoredPanel, JPanel unscoredPanel, ImageSetSupplier imageSetSupplier){
    	JTabbedPane tabbedPane = new JTabbedPane();
    	tabbedPane.setPreferredSize(new Dimension(800,600));
    	tabbedPane.setFont(ResultMatrixCell.textFont);
    	tabbedPane.addTab(imageSetSupplier.getScoredTabTitle(), null, scoredPanel,
    			           "Show scored images for selected taxon");
    	tabbedPane.setMnemonicAt(0, KeyEvent.VK_S);
    	
    	tabbedPane.addTab(imageSetSupplier.getTrainingTabTitle(), null, trainingPanel,
                "Show training examples for selected taxon");
    	tabbedPane.setMnemonicAt(1, KeyEvent.VK_T);

    	//tabbedPane.addTab(imageSetSupplier.getUnscoredTabTitle(), null, unscoredPanel,
    	//                  "Show images that could not be scored");
    	//tabbedPane.setMnemonicAt(2, KeyEvent.VK_U);
    	tabbedPane.setBackground(Color.white);
    	return tabbedPane;
    }
    public static JPanel getImageBrowserHostPanel(){
    	return imageBrowserHostPanel;
    }
    public void unloadImages(){
    	System.out.println(System.currentTimeMillis() + " unloadImages begin " + this.taxonName);
    	this.trainingImageNavigator.unloadAllImages();
    	this.scoredImageNavigator.unloadAllImages();
    	this.unscoredImageNavigator.unloadAllImages();
    	System.out.println(System.currentTimeMillis() + " unloadImages end " + this.taxonName);
    }

    public void loadImages() throws AvatolCVException, MorphobankDataException  {

    	System.out.println(System.currentTimeMillis() + " loadImages begin " + this.taxonName);
    	this.trainingImageNavigator.loadImages();
    	this.scoredImageNavigator.loadImages();
    	this.unscoredImageNavigator.loadImages();
    	System.out.println(System.currentTimeMillis() + " loadImages end " + this.taxonName);
    	this.scoredImageNavigator.validate();
    }
    public static void hostImageBrowser(ImageBrowser ib) throws AvatolCVException, MorphobankDataException  {
    	if (previouslyHostedImageBrowser != null){
    		previouslyHostedImageBrowser.unloadImages();
    		imageBrowserHostPanel.remove(previouslyHostedImageBrowser);
    	}
    	
    	imageBrowserHostPanel.add(ib,getUseAllSpaceConstraints());
    	previouslyHostedImageBrowser = ib;
    	ib.loadImages();
    	imageBrowserHostPanel.validate();
    }
    public static void switchToBrowser(ImageBrowser ib){
    	ImageBrowserSwitcher ibs = new ImageBrowserSwitcher(ib);
    	SwingUtilities.invokeLater(ibs);
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
		return c;
    }

    public static GridBagConstraints getUseLateralSpaceConstraints(){
    	GridBagConstraints c = new GridBagConstraints();
    	c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1.0;
		c.weighty = 0.0;
		c.anchor = GridBagConstraints.NORTHWEST;
		c.fill = GridBagConstraints.BOTH;
		c.gridheight = 1;
		c.gridwidth = 1;
		return c;
    }
    
}
