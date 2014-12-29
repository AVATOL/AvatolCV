package edu.oregonstate.eecs.iis.avatolcv.images;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import edu.oregonstate.eecs.iis.avatolcv.AvatolCVException;

public class ImageScaler {
	public static final String SCALED_IMAGE_DIR = "scaledImages";
	public static final String THUMBNAIL_DIR = "thumbnails";
	private static final String FILESEP = System.getProperty("file.separator");
	private String bundleRoot = null;
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ImageScaler is = new ImageScaler("C:\\avatol\\git\\avatol_cv\\matrix_downloads\\BAT");
		try {
			is.scaleToSizeInRange(THUMBNAIL_DIR, 80,80);
			//is.scaleToSizeInRange(SCALED_IMAGE_DIR, 550,550);
		}
		catch(Exception e){
			e.printStackTrace();
			System.out.println(e.getMessage());
		}
	}
	public ImageScaler(String bundleRoot){
		this.bundleRoot = bundleRoot;
	}
	public void scaleToSizeInRange(String targetDirName, int maxWidth, int maxHeight) throws AvatolCVException {
		try {
			String mediaDir = bundleRoot + FILESEP + "media";
			String targetDir = bundleRoot + FILESEP + targetDirName;
		    File targetDirFile = new File(targetDir);
		    targetDirFile.mkdirs();
			File mediaDirFile = new File(mediaDir);
			File[] files = mediaDirFile.listFiles();
			int count = 0;
			for (File f : files){
				if (f.getName().startsWith("M")){
					count++;
					System.out.println(count + " scaling image " + f.getName() + " to " + maxWidth + "," + maxHeight);
					BufferedImage bi = ImageIO.read(f);
					BufferedImage newImage = scaleImage(bi, maxWidth, maxHeight);
					persistImage(newImage, targetDir, f.getName());
				}
			}
		}
		catch( IOException ioe){
			throw new AvatolCVException("problem scaling image " + ioe.getMessage(), ioe);
		}
		
	}
	public static void persistImage(BufferedImage image, String targetDir, String filename) throws AvatolCVException {
		String targetFilePath = targetDir + FILESEP + filename;
		File f = new File(targetFilePath);
		if (f.exists()){
			f.delete();
		}
		try {
			ImageIO.write(image,"png",f);
		}
		catch(IOException ioe){
			throw new AvatolCVException("problem persisting image " + targetFilePath);
		}
	}
	public static BufferedImage scaleImage(BufferedImage image, int maxWidth, int maxHeight){
    	int height = image.getHeight();
		int width = image.getWidth();
		int newWidth = 0;
		int newHeight = 0;
		if (height <= maxHeight && width <= maxWidth){
			return image;
		}
		if (width >= height){
			newWidth = maxWidth;
			newHeight = height / ( width / maxWidth );
			
		}
		else {
			newHeight = maxHeight;
			newWidth = width / (height / maxHeight);
		}
		BufferedImage resizedImage=resize(image,newWidth,newHeight);
		return resizedImage;
    }
	public static BufferedImage resize(BufferedImage image, int width, int height) {
    	//System.out.println(System.currentTimeMillis() + " Image Navigator resize image begin");
        BufferedImage bi = new BufferedImage(width, height, BufferedImage.TRANSLUCENT);
        Graphics2D g2d = (Graphics2D) bi.createGraphics();
        g2d.addRenderingHints(new RenderingHints(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY));
        g2d.drawImage(image, 0, 0, width, height, null);
        g2d.dispose();

    	//System.out.println(System.currentTimeMillis() + " Image Navigator resize image done");
        return bi;
    }

}
