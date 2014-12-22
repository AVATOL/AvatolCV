package edu.oregonstate.eecs.iis.avatolcv.util;

import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

public class AnnotationToPercent {

	//public AnnotationToPercent
	public void foo(){
		try {
			File resourceFile = new File("foo");
			ImageInputStream in = ImageIO.createImageInputStream(resourceFile);
			try {
			    final Iterator<ImageReader> readers = ImageIO.getImageReaders(in);
			    if (readers.hasNext()) {
			        ImageReader reader = readers.next();
			        try {
			            reader.setInput(in);
			            int width = reader.getWidth(0);
			            int height = reader.getHeight(0);
			        } finally {
			            reader.dispose();
			        }
			    }
			} finally {
			    if (in != null) in.close();
			}
		}
		catch(IOException ioe){
			
		}
		
	}
}
