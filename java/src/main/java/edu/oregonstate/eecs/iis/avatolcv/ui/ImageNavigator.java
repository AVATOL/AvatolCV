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
import javax.swing.border.LineBorder;

import edu.oregonstate.eecs.iis.avatolcv.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.algata.ImageSet;
import edu.oregonstate.eecs.iis.avatolcv.algata.ResultImage;
import edu.oregonstate.eecs.iis.avatolcv.mb.PointAsPercent;

public class ImageNavigator extends JPanel {
	/**
	 * 
	 */
	public static final int THUMBNAIL_BORDER_WIDTH = 3;
	private static final long serialVersionUID = 1L;
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
    public boolean isResultImageSelected(ResultImage ri) throws AvatolCVException {
    	if (this.imageSet.isCurrentResultImage(ri)){
    		return true;
    	}
    	return false;
    }
    public ImageSet getImageSet(){
    	return this.imageSet;
    }
    public void loadImages() throws AvatolCVException{
    	if (this.imageSet.hasData()){
	    	//System.out.println(System.currentTimeMillis() + " Image Navigator " + this.taxonName + " " + this.type + " loading ");
			ResultImage ri = this.imageSet.getCurrentResultImage();
			loadMainImage(ri);
    		loadThumbnails();
        	//System.out.println(System.currentTimeMillis() + " Image Navigator " + this.taxonName + " " + this.type + " loaded ");
		}
    }
    public void loadMainImage(ResultImage ri) throws AvatolCVException {
    	List<PointAsPercent> annotationPoints = ri.getAnnotationCoordinates().getPoints();
    	String imagePath = ri.getMediaPath();
		this.picLabel = getImageAsJLabel(imagePath, annotationPoints);
		this.imagePanel.add(picLabel,ImageBrowser.getUseLateralSpaceConstraints());
    }
    public void loadThumbnails() throws AvatolCVException {
    	List<ResultImage> resultImages = imageSet.getResultImages();
    	this.thumbnailLabels = new ArrayList<JLabel>();
    	int index = 0;
    	for (ResultImage ri : resultImages){
    		String thumbnailPath = ri.getThumbnailMediaPath();
    		JLabel thumbnailLabel = null;
    		if (ri.hasAnnotationCoordinates()){
    			List<PointAsPercent> annotationPoints = ri.getAnnotationCoordinates().getPoints();
        		thumbnailLabel = getImageAsJLabel(thumbnailPath,annotationPoints);
    		}
    		else {
    			thumbnailLabel = getImageAsJLabel(thumbnailPath);
    		}
    		thumbnailLabel.addMouseListener(new ThumbnailClickListener(thumbnailLabel, ri,this));
        	this.thumbnailLabels.add(thumbnailLabel);
        	this.thumbnailPanel.add(thumbnailLabel, getThumbnailConstraints(index++));
    	}
    	highlightThumbnail(this.thumbnailLabels.get(0));
    }
    public void highlightThumbnail(JLabel thumbnailLabel){
    	// clear them all...
    	for (JLabel label : this.thumbnailLabels){
    		label.setBorder(new LineBorder(Color.white, THUMBNAIL_BORDER_WIDTH));
    	}
    	// then highlight the one desired
    	thumbnailLabel.setBorder(new LineBorder(Color.green, THUMBNAIL_BORDER_WIDTH));
    }
    public void unloadMainImage(){
    	if (this.imageSet.hasData() && this.picLabel != null){
    		this.imagePanel.remove(this.picLabel);
        	this.picLabel = null;
    	}
    }
    public void unloadThumbnails(){
    	if (this.imageSet.hasData() && this.picLabel != null){
    		if (null != this.thumbnailLabels){
        		for (JLabel label : this.thumbnailLabels){
        			this.thumbnailPanel.remove(label);
        		}
        		this.thumbnailLabels = null;
        	}
    	}
    }
    public void unloadAllImages(){
    	unloadMainImage();
    	unloadThumbnails();
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
    public JLabel getImageAsJLabel(String imagePath) throws AvatolCVException {
    	try {
    		BufferedImage image = ImageIO.read(new File(imagePath));
    		JLabel result = new JLabel(new ImageIcon(image));
    		return result;
    	}
    	catch(IOException ioe){
    		ioe.printStackTrace();
    		throw new AvatolCVException("problem loading images for image set " + imagePath);
    	}
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
		c.weighty = 1.0;
		c.anchor = GridBagConstraints.CENTER;
		c.fill = GridBagConstraints.BOTH;
		c.gridheight = 1;
		c.gridwidth = 1;
		//c.insets = new Insets(0,2,0,2);
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
		//c.insets = new Insets(2,4,2,4);
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
		//c.insets = new Insets(2,4,2,4);
		return c;
    }
}
