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
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import edu.oregonstate.eecs.iis.avatolcv.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.algata.ImageSet;
import edu.oregonstate.eecs.iis.avatolcv.algata.ResultImage;
import edu.oregonstate.eecs.iis.avatolcv.mb.PointAsPercent;

public class ImageNavigator extends JPanel {
	private ImageSet imageSet = null;
	private JPanel imagePanel = null;
	private JScrollPane thumbnailScrollPane = null;
	private JPanel thumbnailPanel = null;
	private JLabel picLabel = null;
	private String taxonName = null;
	private String type = null;
	private List<JLabel> thumbnailLabels = null;
    public ImageNavigator(ImageSet imageSet, String taxonName, String type) {
		this.imageSet = imageSet;
		this.taxonName = taxonName;
		this.type = type;
    	this.setLayout(new GridBagLayout());
    	this.imagePanel = new JPanel();
    	this.imagePanel.setLayout(new GridBagLayout());
    	this.imagePanel.setBackground(Color.white);
    	this.thumbnailScrollPane = new JScrollPane();
    	this.thumbnailScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        this.thumbnailPanel = getThumbnailPanel();
        this.thumbnailPanel.setBackground(Color.white);

		thumbnailScrollPane.setViewportView(this.thumbnailPanel);
        this.add(imagePanel, getImagePanelConstraints());
        this.add(thumbnailScrollPane, getThumbnailPanelConstraints());
    }
    public void loadImages() throws AvatolCVException{
    	String imagePath = "unknown";
		if (this.imageSet.hasData()){
	    	//System.out.println(System.currentTimeMillis() + " Image Navigator " + this.taxonName + " " + this.type + " loading ");
    		imagePath = this.imageSet.getCurrentResultImage().getScaledMediaPath();
    		List<PointAsPercent> annotationPoints = this.imageSet.getCurrentResultImage().getAnnotationCoordinates().getPoints();
    		this.picLabel = getImageAsJLabel(imagePath, annotationPoints);
    		this.imagePanel.add(picLabel,ImageBrowser.getUseAllSpaceConstraints());
    		List<String> thumbnailPaths = imageSet.getAllThumbnailPaths();
    		this.thumbnailLabels = new ArrayList<JLabel>();
            int index = 0;
            for (String thumbnailPath : thumbnailPaths){
            	JLabel thumbnailLabel = getImageAsJLabel(thumbnailPath,annotationPoints);
            	this.thumbnailLabels.add(thumbnailLabel);
            	this.thumbnailPanel.add(thumbnailLabel, getThumbnailConstraints(index++));
            }
        	//System.out.println(System.currentTimeMillis() + " Image Navigator " + this.taxonName + " " + this.type + " loaded ");
		}
    	
    }
    public int getPointPixelRadius(BufferedImage image){
    	int width = image.getWidth();
		int height = image.getHeight();
		if (width < 100 || height < 100){
			return 3;
		}
		return 5;
    }
    public int getPixelDistanceForPercentCoordinate(int imageDimension, double coord){
    	int pixels = (int)(imageDimension * coord)/100;
    	return pixels;
    }
    public JLabel getImageAsJLabel(String imagePath, List<PointAsPercent> annotationPoints) throws AvatolCVException {
    	try {
    		BufferedImage image = ImageIO.read(new File(imagePath));
    		int radius = getPointPixelRadius(image);
    		int width = image.getWidth();
    		int height = image.getHeight();
    		//Graphics g2d = image.getGraphics();
    		
    		ImageIcon icon = new ImageIcon(image);
    		Graphics g2d = icon.getImage().getGraphics();
    		g2d.setColor(Color.red);
    		for (PointAsPercent pap : annotationPoints){
    			double x = pap.getX();
    			int xPixel = getPixelDistanceForPercentCoordinate(width, x);
    			double y = pap.getY();
    			int yPixel = getPixelDistanceForPercentCoordinate(height, y);
    			g2d.fillOval(xPixel, yPixel, radius, radius); 
    		}  
    		JLabel result = new JLabel(new ImageIcon(image));
    		return result;
    	}
    	catch(IOException ioe){
    		ioe.printStackTrace();
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
    	System.out.println(System.currentTimeMillis() + " Image Navigator resize image begin");
        BufferedImage bi = new BufferedImage(width, height, BufferedImage.TRANSLUCENT);
        Graphics2D g2d = (Graphics2D) bi.createGraphics();
        g2d.addRenderingHints(new RenderingHints(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY));
        g2d.drawImage(image, 0, 0, width, height, null);
        g2d.dispose();

    	System.out.println(System.currentTimeMillis() + " Image Navigator resize image done");
        return bi;
    }
    public void unloadImages(){
    	System.out.println(System.currentTimeMillis() + " Image Navigator " + this.taxonName + " " + this.type + " trying to unload ");
    	if (this.imageSet.hasData()){
    		this.imagePanel.remove(this.picLabel);
        	this.picLabel = null;
        	//System.out.println(System.currentTimeMillis() + " calling gc ");
        	//System.gc();
        	//System.out.println( System.currentTimeMillis() + " called gc ");
        	if (null != this.thumbnailLabels){
        		for (JLabel label : this.thumbnailLabels){
        			this.thumbnailPanel.remove(label);
        		}
        		this.thumbnailLabels = null;
        	}
    	}
    }
    public JPanel getThumbnailPanel(){
    	JPanel p = new JPanel();
    	p.setLayout(new GridBagLayout());
    	return p;
    }


    public GridBagConstraints getThumbnailConstraints(int x){
    	GridBagConstraints c = new GridBagConstraints();
		c.gridx = x;
		c.gridy = 0;
		c.weightx = 1.0;
		c.weighty = 0.0;
		c.anchor = GridBagConstraints.CENTER;
		c.fill = GridBagConstraints.BOTH;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(0,2,0,2);
		return c;
    }

    public GridBagConstraints getImagePanelConstraints(){
    	GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1.0;
		c.weighty = 0.0;
		c.anchor = GridBagConstraints.NORTH;
		c.fill = GridBagConstraints.BOTH;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(2,4,2,4);
		return c;
    }
    public GridBagConstraints getThumbnailPanelConstraints(){
    	GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 1;
		c.weightx = 1.0;
		c.weighty = 1.0;
		c.anchor = GridBagConstraints.SOUTH;
		c.fill = GridBagConstraints.BOTH;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(2,4,2,4);
		return c;
    }
}
