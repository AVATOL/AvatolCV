package edu.oregonstate.eecs.iis.avatolcv.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class FilePrepFor20151111Demo {
	private static final String NL = System.getProperty("line.separator");

	public static void main(String[] args) {
		String imageNamesFile =   "C:\\avatol\\git\\avatol_cv\\modules\\segmentation\\yaoSeg\\listOfImageNamesFromYaoExpLarge.txt";
		String outPath= "C:\\avatol\\git\\avatol_cv\\modules\\segmentation\\yaoSeg\\testImagesFor201511Demo.txt";
		//String pathToRealImages = "C:\\avatol\\git\\avatol_cv\\sessions\\yaoExp\\normalized\\images\\large\\";
		String pathToRealImages = "/Users/jedirvine/av/avatol_cv/sessions/yaoExp/normalized/images/large/";
		try {
			BufferedReader reader = new BufferedReader(new FileReader(imageNamesFile));
			BufferedWriter writer = new BufferedWriter(new FileWriter(outPath));
			
			String line = null;
			while (null != (line = reader.readLine())){
				String outline = pathToRealImages + line + NL;
				writer.write(outline);
			}
			reader.close();
			writer.close();
		}
		catch(IOException ioe){
			
		}
	}

}
