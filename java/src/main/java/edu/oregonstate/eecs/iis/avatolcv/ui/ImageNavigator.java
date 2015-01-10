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
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.LineBorder;

import edu.oregonstate.eecs.iis.avatolcv.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.algata.AnnotatedItem;
import edu.oregonstate.eecs.iis.avatolcv.algata.ImageSet;
import edu.oregonstate.eecs.iis.avatolcv.algata.ResultImage;
import edu.oregonstate.eecs.iis.avatolcv.mb.Annotation;
import edu.oregonstate.eecs.iis.avatolcv.mb.AnnotationCoordinates;
import edu.oregonstate.eecs.iis.avatolcv.mb.Annotations;
import edu.oregonstate.eecs.iis.avatolcv.mb.MorphobankBundle;
import edu.oregonstate.eecs.iis.avatolcv.mb.MorphobankData;
import edu.oregonstate.eecs.iis.avatolcv.mb.MorphobankDataException;
import edu.oregonstate.eecs.iis.avatolcv.mb.PointAsPercent;

public class ImageNavigator extends JPanel {
	/**
	 * 
	 */
	public enum DataType {
		training,
		scored,
		unscored
	}
	
	public static final Color thumbnailHoverColor = new Color(200,200,255);
	public static final Color thumbNailSelectionColor = new Color(100,100,255);
	public static final int THUMBNAIL_BORDER_WIDTH = 3;
	private static final int CROSS_HALF_LENGTH = 15;
	private static final int THUMBNAIL_CROSS_HALF_LENGTH = 3;
	private static final long serialVersionUID = 1L;
	private ImageSet imageSet = null;
	private JPanel imagePanel = null;
	private JLabel imageInfoLabel = null;
	private JScrollPane thumbnailScrollPane = null;
	private JPanel thumbnailPanel = null;
	private JLabel picLabel = null;
	private String taxonName = null;
	private DataType type = null;
	private List<JLabel> thumbnailLabels = null;
	private static String infoSpacer = "     ";
	private MorphobankBundle mb = null;
    public ImageNavigator(ImageSet imageSet, String taxonName, DataType type, MorphobankBundle mb) {
    	this.mb = mb;
		this.imageSet = imageSet;
		this.taxonName = taxonName;
		this.type = type;
    	this.setLayout(new GridBagLayout());
    	this.imagePanel = new JPanel();
    	this.imagePanel.setLayout(new GridBagLayout());
    	this.imagePanel.setBackground(Color.white);
    	this.imageInfoLabel = new JLabel("",SwingConstants.CENTER);
    	this.imageInfoLabel.setOpaque(true);
    	this.imageInfoLabel.setFont(ResultMatrixCell.textFont);
    	this.thumbnailScrollPane = new JScrollPane();
    	this.thumbnailScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        this.thumbnailPanel = getThumbnailPanel();
        this.thumbnailPanel.setBackground(Color.white);

		thumbnailScrollPane.setViewportView(this.thumbnailPanel);
        this.add(imagePanel, getImagePanelConstraints());
        this.add(imageInfoLabel, getImageInfoLabelConstraints());
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
    public void loadImages() throws AvatolCVException, MorphobankDataException {
    	if (this.imageSet.hasData()){
	    	//System.out.println(System.currentTimeMillis() + " Image Navigator " + this.taxonName + " " + this.type + " loading ");
			ResultImage ri = this.imageSet.getCurrentResultImage();
			loadMainImage(ri);
    		loadThumbnails();
        	//System.out.println(System.currentTimeMillis() + " Image Navigator " + this.taxonName + " " + this.type + " loaded ");
		}
    }
    public String getTrainingImageInfo(ResultImage ri){
    	/*
    	 * need to load the original annotation for this media character combo 
    	 */
    	
    	String info = "character: " + ri.getCharacterName().toUpperCase() + infoSpacer + " state: " + ri.getCharacterStateName().toUpperCase();
    	return info;
    }
    public String getScoredImageInfo(ResultImage ri, String humanLabel){
    	String info = "character: " + ri.getCharacterName().toUpperCase() + 
    			infoSpacer + " state: " + ri.getCharacterStateName().toUpperCase() + 
    			infoSpacer + " confidence: " + ri.getConfidence().toUpperCase().substring(0, 4)  + 
    			infoSpacer + " human score: " + humanLabel;
    	return info;
    }
    public String getUnscoredImageInfo(ResultImage ri){
    	String info = "character: " + ri.getCharacterName().toUpperCase() + infoSpacer + "not scored";
    	return info;
    }
    public void loadMainImage(ResultImage ri) throws AvatolCVException, MorphobankDataException  {
    	int halfLength = CROSS_HALF_LENGTH;
    	String imagePath = ri.getMediaPath();
    	if (ri.hasAnnotationCoordinates()){
    		List<PointAsPercent> annotationPoints = ri.getAnnotationCoordinates().getPoints();
        	if (this.type == DataType.training){
            	this.imageInfoLabel.setText(getTrainingImageInfo(ri));
        		this.picLabel = getImageAsJLabel(imagePath, annotationPoints, halfLength);
        	}
        	else if (this.type == DataType.scored){
        		String annotationPath = Annotations.getAnnotationFilePathname(this.mb.getRootDir(), ri.getCharacterId(), ri.getMediaId());
        		List<Annotation> humanAnnotations = Annotations.loadAnnotations(annotationPath, ri.getMediaId());
        		//String humanLabel = humanAnnotations.get(0).getCharStateText();
        		String humanLabel = ri.getHumanLabel();
        		this.imageInfoLabel.setText(getScoredImageInfo(ri, humanLabel));
        		this.picLabel = getImageAsJLabel(imagePath, annotationPoints, humanAnnotations, halfLength);
        	}
        	else {
        		//(this.type == DataType.unscored)
        		this.imageInfoLabel.setText(getUnscoredImageInfo(ri));
        		this.picLabel = getImageAsJLabel(imagePath, annotationPoints, halfLength);
        	}
    		this.imagePanel.add(picLabel,ImageBrowser.getUseLateralSpaceConstraints());
    	}
    	else {
    		this.picLabel = getImageAsJLabel(imagePath);
    	}
    	
    }
    
    public void loadThumbnails() throws AvatolCVException, MorphobankDataException {
    	int halfLength = THUMBNAIL_CROSS_HALF_LENGTH;
    	List<ResultImage> resultImages = imageSet.getResultImages();
    	this.thumbnailLabels = new ArrayList<JLabel>();
    	int index = 0;
    	for (ResultImage ri : resultImages){
    		String thumbnailPath = ri.getThumbnailMediaPath();
    		JLabel thumbnailLabel = null;
    		if (ri.hasAnnotationCoordinates()){
    	    	JLabel curLabel = null;
    			List<PointAsPercent> annotationPoints = ri.getAnnotationCoordinates().getPoints();
    			if (this.type == DataType.training){
    				thumbnailLabel = getImageAsJLabel(thumbnailPath, annotationPoints, halfLength);
            	}
            	else if (this.type == DataType.scored){
            		//List<Annotation> annotations = loadAnnotations(String path, String mediaId)
            		String annotationPath = Annotations.getAnnotationFilePathname(this.mb.getRootDir(), ri.getCharacterId(), ri.getMediaId());
            		List<Annotation> humanAnnotations = Annotations.loadAnnotations(annotationPath, ri.getMediaId());
            		thumbnailLabel = getImageAsJLabel(thumbnailPath, annotationPoints, humanAnnotations, halfLength);
            	}
            	else {
            		//(this.type == DataType.unscored)
            		thumbnailLabel = getImageAsJLabel(thumbnailPath, annotationPoints, halfLength);
            	}
    		}
    		else {
    			thumbnailLabel = getImageAsJLabel(thumbnailPath);
    		}
    		thumbnailLabel.addMouseListener(new ThumbnailClickListener(thumbnailLabel, ri,this));
    		thumbnailLabel.setToolTipText(ri.getMediaId());
        	this.thumbnailLabels.add(thumbnailLabel);
        	this.thumbnailPanel.add(thumbnailLabel, getThumbnailConstraints(index++));
    	}
    	highlightThumbnail(this.thumbnailLabels.get(0));
    }
    public void highlightThumbnail(JLabel thumbnailLabel){
    	// clear them all...
    	for (JLabel label : this.thumbnailLabels){
    		SwingUtilities.invokeLater(new ThumbnailBorderUpdater(Color.white, label));
    		//label.setBorder(new LineBorder(Color.white, THUMBNAIL_BORDER_WIDTH));
    	}
    	// then highlight the one desired
    	SwingUtilities.invokeLater(new ThumbnailBorderUpdater(thumbNailSelectionColor, thumbnailLabel));
    	//thumbnailLabel.setBorder(new LineBorder(ResultMatrixCell.entrySelectionColor, THUMBNAIL_BORDER_WIDTH));
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

    public JLabel getImageAsJLabel(String imagePath, List<PointAsPercent> annotationPoints, int halfLength) throws AvatolCVException {
    	try {
    		BufferedImage image = ImageIO.read(new File(imagePath));
    		int width = image.getWidth();
    		int height = image.getHeight();
    		//Graphics g2d = image.getGraphics();
    		
    		ImageIcon icon = new ImageIcon(image);
    		Graphics g2d = icon.getImage().getGraphics();
    		g2d.setColor(Color.yellow);
    		for (PointAsPercent pap : annotationPoints){
    			double x = pap.getX();
    			int xPixel = getPixelDistanceForPercentCoordinate(width, x);
    			double y = pap.getY();
    			int yPixel = getPixelDistanceForPercentCoordinate(height, y);
    			//g2d.fillOval(xPixel, yPixel, radius, radius); 
    			drawCrossAtPoint(g2d,xPixel,yPixel,width,height, halfLength);
    		}  
    		JLabel result = new JLabel(new ImageIcon(image));
    		return result;
    	}
    	catch(IOException ioe){
    		ioe.printStackTrace();
    		throw new AvatolCVException("problem loading images for image set " + imagePath);
    	}
    }

    public JLabel getImageAsJLabel(String imagePath, List<PointAsPercent> annotationPoints, List<Annotation> humanAnnotations, int halfLength) throws AvatolCVException {
    	try {
    		BufferedImage image = ImageIO.read(new File(imagePath));
    		int width = image.getWidth();
    		int height = image.getHeight();
    		
    		ImageIcon icon = new ImageIcon(image);
    		Graphics2D g2d = (Graphics2D)(icon.getImage().getGraphics());
    		g2d.setColor(Color.yellow);
    		
    		for (Annotation annot : humanAnnotations){
    			String coordinateList = annot.getCoordinateList();
    			AnnotationCoordinates coords = AnnotatedItem.parseAnnotationLine(coordinateList);
    			List<PointAsPercent> humanAnnotationPoints = coords.getPoints();
    			for (PointAsPercent pap : humanAnnotationPoints){
        			double x = pap.getX();
        			int xPixel = getPixelDistanceForPercentCoordinate(width, x);
        			double y = pap.getY();
        			int yPixel = getPixelDistanceForPercentCoordinate(height, y);
        			drawCrossAtPoint(g2d,xPixel,yPixel,width,height, halfLength);
        			//g2d.fillOval(xPixel, yPixel, radius, radius); 
        			
        		}  
    		}
    		g2d.setColor(Color.yellow);
    	    g2d.addRenderingHints(new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON));
    		for (PointAsPercent pap : annotationPoints){
    			double x = pap.getX();
    			int xPixel = getPixelDistanceForPercentCoordinate(width, x);
    			double y = pap.getY();
    			int yPixel = getPixelDistanceForPercentCoordinate(height, y);
    			drawXAtPoint(g2d,xPixel,yPixel,width,height, halfLength);
    		    drawCircleCenteredAtPoint(g2d,xPixel, yPixel, halfLength); 
    		}  
    		JLabel result = new JLabel(new ImageIcon(image));
    		return result;
    	}
    	catch(IOException ioe){
    		ioe.printStackTrace();
    		throw new AvatolCVException("problem loading images for image set " + imagePath);
    	}
    }

    public void drawCircleCenteredAtPoint(Graphics2D graphics, int x, int y, int halfLength){
    	int drawingOriginX = x - halfLength;
		int drawingOriginY = y - halfLength;
		graphics.drawOval(drawingOriginX, drawingOriginY, 2*halfLength, 2*halfLength); 
    }
    public void drawCrossAtPoint(Graphics graphics, int x, int y, int width, int height, int halfLength){
    	int verticalLineX1 = x;
		int verticalLineX2 = x;
		int verticalLineY1 = -1;
		int verticalLineY2 = -1;
		if (y < halfLength){
			verticalLineY1 = 0;
			verticalLineY2 = y + halfLength;
		} 
		else if (y > height - halfLength){
			verticalLineY1 = y - halfLength;
			verticalLineY2 = height;
		}
		else {
			verticalLineY1 = y - halfLength;
			verticalLineY2 = y + halfLength;
		}
		
		graphics.drawLine(verticalLineX1, verticalLineY1, verticalLineX2, verticalLineY2);
    	int horizontalLineX1 = -1;
		int horizontalLineX2 = -1;
		int horizontalLineY1 = y;
		int horizontalLineY2 = y;
		if (x < halfLength){
			horizontalLineX1 = 0;
			horizontalLineX2 = x + halfLength;
		} 
		else if (x > width - halfLength){
			horizontalLineX1 = x - halfLength;
			horizontalLineX2 = width;
		}
		else {
			horizontalLineX1 = x - halfLength;
			horizontalLineX2 = x + halfLength;
		}
		graphics.drawLine(horizontalLineX1, horizontalLineY1, horizontalLineX2, horizontalLineY2);
    }

    public void drawXAtPoint(Graphics graphics, int x, int y, int width, int height, int halfLength){
    	int distance = (int)(Math.cos(Math.toRadians(45)) * halfLength);
    	int nwseLineX1 = x - distance;
		int nwseLineX2 = x + distance;
		int nwseLineY1 = y - distance;
		int nwseLineY2 = y + distance;
		
		graphics.drawLine(nwseLineX1, nwseLineY1, nwseLineX2, nwseLineY2);
    	int neswLineX1 = x + distance;
		int neswLineX2 = x - distance;
		int neswLineY1 = y - distance;
		int neswLineY2 = y + distance;
		
		graphics.drawLine(neswLineX1, neswLineY1, neswLineX2, neswLineY2);
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
    public GridBagConstraints getImageInfoLabelConstraints(){
    	GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 1;
		c.weightx = 0.0;
		c.weighty = 0.05;
		c.anchor = GridBagConstraints.CENTER;
		c.fill = GridBagConstraints.BOTH;
		c.gridheight = 1;
		c.gridwidth = 1;
		//c.insets = new Insets(2,4,2,4);
		return c;
    }
    public GridBagConstraints getThumbnailPanelConstraints(){
    	GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 2;
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
