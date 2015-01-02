package edu.oregonstate.eecs.iis.avatolcv.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class ScaleBashScriptGen {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String NL = "\n";
		String mediaDir = "C:\\avatol\\git\\avatol_cv\\matrix_downloads\\BAT2\\media";
		File mediaDirFile = new File(mediaDir);
		File[] files = mediaDirFile.listFiles();
		String outputPath = "C:\\avatol\\git\\avatol_cv\\matrix_downloads\\BAT2\\scaledown.sh";
		
		try {

			BufferedWriter writer = new BufferedWriter(new FileWriter(outputPath));
			writer.write("#!/bin/bash" + NL);
			for (File f : files){
				String name = f.getName();
				writer.write("convert media/" + name + " -resize 780 mediaScaled/" + name + NL);
			}
			writer.close();
		}
		catch(IOException ioe){
			
		}
	}

}
