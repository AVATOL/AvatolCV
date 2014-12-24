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

public class ImageBrowser extends JPanel {
	private static JPanel imageBrowserHostPanel = new JPanel();
	private ImageNavigator trainingImageNavigator = null;
	private ImageNavigator scoredImageNavigator = null;
	private ImageNavigator unscoredImageNavigator = null;
	static {
		imageBrowserHostPanel.setLayout(new GridBagLayout());
		imageBrowserHostPanel.setBackground(Color.red);
	}
	private static ImageBrowser previouslyHostedImageBrowser = null;
	private ImageSetSupplier imageSetSupplier = null;
    public ImageBrowser(ImageSetSupplier imageSetSupplier) throws AvatolCVException {
    	this.imageSetSupplier = imageSetSupplier;
    	populateImageBrowser(imageSetSupplier);
    	this.setLayout(new GridBagLayout());
    }
    public void populateImageBrowser(ImageSetSupplier imageSetSupplier) throws AvatolCVException {
    	this.trainingImageNavigator = new ImageNavigator(imageSetSupplier.getTrainingImageSet());
    	this.scoredImageNavigator = new ImageNavigator(imageSetSupplier.getScoredImageSet());
    	this.unscoredImageNavigator = new ImageNavigator(imageSetSupplier.getUnscoredImageSet());
    	JTabbedPane tp = getTabbedPane(trainingImageNavigator, scoredImageNavigator, unscoredImageNavigator);
    	this.add(tp, getUseAllSpaceConstraints());
    }
    public JTabbedPane getTabbedPane(JPanel trainingPanel, JPanel scoredPanel, JPanel unscoredPanel){
    	JTabbedPane tabbedPane = new JTabbedPane();
    	tabbedPane.setPreferredSize(new Dimension(548,548));
    	tabbedPane.addTab("Training images", null, trainingPanel,
    	                  "Show training examples for selected taxon");
    	tabbedPane.setMnemonicAt(0, KeyEvent.VK_T);

    	tabbedPane.addTab("Scored Images", null, scoredPanel,
    			           "Show scored images for selected taxon");
    	tabbedPane.setMnemonicAt(1, KeyEvent.VK_S);

    	tabbedPane.addTab("Unscored Images", null, unscoredPanel,
    	                  "Show images that could not be scored");
    	tabbedPane.setMnemonicAt(2, KeyEvent.VK_U);
    	return tabbedPane;
    }
    public static JPanel getImageBrowserHostPanel(){
    	return imageBrowserHostPanel;
    }
    public void unloadImages(){
    	this.trainingImageNavigator.unloadImages();
    	this.scoredImageNavigator.unloadImages();
    	this.unscoredImageNavigator.unloadImages();
    }

    public void loadImages() throws AvatolCVException {
    	this.trainingImageNavigator.loadImages();
    	this.scoredImageNavigator.loadImages();
    	this.unscoredImageNavigator.loadImages();
    }
    public static void hostImageBrowser(ImageBrowser ib) throws AvatolCVException {
    	if (previouslyHostedImageBrowser != null){
    		previouslyHostedImageBrowser.unloadImages();
    		imageBrowserHostPanel.remove(previouslyHostedImageBrowser);
    	}
    	
    	imageBrowserHostPanel.add(ib,getUseAllSpaceConstraints());
    	previouslyHostedImageBrowser = ib;
    	ib.loadImages();
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
    
}
