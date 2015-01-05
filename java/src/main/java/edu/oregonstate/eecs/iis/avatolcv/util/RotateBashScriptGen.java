package edu.oregonstate.eecs.iis.avatolcv.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RotateBashScriptGen {
	private static final String FILESEP = System.getProperty("file.separator");
	private static final String NL = System.getProperty("line.separator");
	public static void main(String[] args) {
		
		// TODO Auto-generated method stub
		String path = "C:\\avatol\\git\\avatol_cv\\matrix_downloads\\BAT2\\imagesFlipped.txt";
		RotateBashScriptGen rbsg = new RotateBashScriptGen(path);
	}
	public String getImageFilename(String mediaIdCapM){
		String result = null;
		String mediaPath = "C:\\avatol\\git\\avatol_cv\\matrix_downloads\\BAT2\\media";
		File mediaDirFile = new File(mediaPath);
		File[] files = mediaDirFile.listFiles();
		for (File f : files){
			String name = f.getName();
			if (name.startsWith(mediaIdCapM)){
				result = name;
			}
		}
		return result;
	}
	public RotateBashScriptGen(String imagesToRotateListpath){
		String outputPath = "C:\\avatol\\git\\avatol_cv\\matrix_downloads\\BAT2\\rotate.sh";
		
		try {
			List<String> filenames = new ArrayList<String>();
			BufferedReader reader = new BufferedReader(new FileReader(imagesToRotateListpath));
    		String line = null;
    		while (null != (line = reader.readLine())){
    			String imageIdCapM = line;
    			String imageId = imageIdCapM.replace("M", "m");
    			String imageFilename = getImageFilename(imageIdCapM);
    			if (imageFilename == null){
    				System.out.println("could not find filename for imageId " + imageId);
    			}
    			else {
    				filenames.add(imageFilename);
    			}
    		}
			BufferedWriter writer = new BufferedWriter(new FileWriter(outputPath));
			writer.write("#!/bin/bash" + NL);
			for (String name : filenames){
				writer.write("convert media/" + name + " -rotate 180 mediaRotated20140102/" + name + NL);
			}
			writer.close();
			reader.close();
		}
		catch(IOException ioe){
			
		}
	}
}
