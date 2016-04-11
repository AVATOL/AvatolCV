package edu.oregonstate.eecs.iis.avatolcv.normalized;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import javax.imageio.ImageIO;

public class ImageConverter {

	public static void convertToJpeg(File inputFile, File outputFile) throws Exception {
		if (!inputFile.exists()) {
			throw new Exception("Input Image Not Found!!!");
		}
		try (InputStream is = new FileInputStream(inputFile)) {
			BufferedImage image = ImageIO.read(is);
			try (OutputStream os = new FileOutputStream(outputFile)) {
				ImageIO.write(image, "jpg", os);
			} catch (Exception exp) {
				exp.printStackTrace();
			}
		} catch (Exception exp) {
			exp.printStackTrace();
		}
	}

	public static void convertToJpeg(InputStream is, File outputFile) throws Exception {
        if (null == is) {
            throw new Exception("null input stream provided to image converter");
        }
        try {
            BufferedImage image = ImageIO.read(is);
            try (OutputStream os = new FileOutputStream(outputFile)) {
                ImageIO.write(image, "jpg", os);
            } catch (Exception exp) {
                exp.printStackTrace();
            }
        } catch (Exception exp) {
            exp.printStackTrace();
        }
    }
	public static void convertToJpeg(String inputFile, String outputFile) throws Exception {
		convertToJpeg(new File(inputFile), new File(outputFile));
	}
	public static void convertToJpeg(InputStream inputStream, String outputFile) throws Exception {
        convertToJpeg(inputStream, new File(outputFile));
    }
}
