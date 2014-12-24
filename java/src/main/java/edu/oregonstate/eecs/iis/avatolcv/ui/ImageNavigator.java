package edu.oregonstate.eecs.iis.avatolcv.ui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import edu.oregonstate.eecs.iis.avatolcv.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.algata.ImageSet;
import edu.oregonstate.eecs.iis.avatolcv.algata.ResultImage;

public class ImageNavigator extends JPanel {
	private ImageSet imageSet = null;
	private JPanel imagePanel = null;
	private JPanel iconPanel = null;
	private JLabel picLabel = null;
	private String taxonName = null;
	private String type = null;
    public ImageNavigator(ImageSet imageSet, String taxonName, String type) {
		this.imageSet = imageSet;
		this.taxonName = taxonName;
		this.type = type;
    	this.setLayout(new GridBagLayout());
    	this.imagePanel = new JPanel();
    	this.imagePanel.setLayout(new GridBagLayout());
    	this.imagePanel.setBackground(Color.yellow);	
        this.iconPanel = getIconPanel();
        this.iconPanel.setBackground(Color.blue);
        this.add(imagePanel, getImagePanelConstraints());
        this.add(iconPanel, getIconPanelConstraints());
    }
    public void loadImages() throws AvatolCVException{
    	String imagePath = "unknown";
    	try {
    		if (this.imageSet.hasData()){
        		imagePath = this.imageSet.getCurrentResultImage().getMediaPath();
        		BufferedImage image = ImageIO.read(new File(imagePath));
        		BufferedImage scaledImage = scaleImage(image);
        		
        		this.picLabel = new JLabel(new ImageIcon(scaledImage));
        		this.imagePanel.add(picLabel,ImageBrowser.getUseAllSpaceConstraints());
    		}
    	}
    	catch(IOException ioe){
    		throw new AvatolCVException("problem loading images for image set " + imagePath);
    	}
    }
    public static BufferedImage scaleImage(BufferedImage image){
    	int height = image.getHeight();
		int width = image.getWidth();
		int newWidth = 0;
		int newHeight = 0;
		if (height <= 520 && width <= 520){
			return image;
		}
		if (width >= height){
			newWidth = 520;
			newHeight = height / ( width / 520 );
			
		}
		else {
			newHeight = 520;
			newWidth = width / (height / 520);
		}
		//BufferedImage image=ImageIO.read(..);
		BufferedImage resizedImage=resize(image,newWidth,newHeight);
		return resizedImage;
    }
    public static BufferedImage resize(BufferedImage image, int width, int height) {
        BufferedImage bi = new BufferedImage(width, height, BufferedImage.TRANSLUCENT);
        Graphics2D g2d = (Graphics2D) bi.createGraphics();
        g2d.addRenderingHints(new RenderingHints(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY));
        g2d.drawImage(image, 0, 0, width, height, null);
        g2d.dispose();
        return bi;
    }
    public void unloadImages(){
    	System.out.println("Image Navigator " + this.taxonName + " " + this.type + " trying to unload");
    	if (this.imageSet.hasData()){
    		this.imagePanel.remove(this.picLabel);
        	this.picLabel = null;
        	System.gc();
    	}
    }
    public JPanel getIconPanel(){
    	JPanel p = new JPanel();
    	p.setLayout(new GridBagLayout());
    	int width = this.imageSet.getCurrentListSize();
    	for (int i = 0; i < width; i ++){
    		JPanel iconPanel = new JPanel();
    		p.add(iconPanel, getIconConstraints(i));
    	}
    	return p;
    }


    public GridBagConstraints getIconConstraints(int x){
    	GridBagConstraints c = new GridBagConstraints();
		c.gridx = x;
		c.gridy = 0;
		c.weightx = 0.0;
		c.weighty = 1.0;
		c.anchor = GridBagConstraints.CENTER;
		c.fill = GridBagConstraints.BOTH;
		c.gridheight = 1;
		c.gridwidth = 1;
		//c.insets = new Insets(2,4,2,4);
		return c;
    }

    public GridBagConstraints getImagePanelConstraints(){
    	GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1.0;
		c.weighty = 0.8;
		c.anchor = GridBagConstraints.NORTH;
		c.fill = GridBagConstraints.BOTH;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(2,4,2,4);
		return c;
    }
    public GridBagConstraints getIconPanelConstraints(){
    	GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 1;
		c.weightx = 1.0;
		c.weighty = 0.2;
		c.anchor = GridBagConstraints.SOUTH;
		c.fill = GridBagConstraints.BOTH;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(2,4,2,4);
		return c;
    }
}
