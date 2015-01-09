package edu.oregonstate.eecs.iis.avatolcv.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class SlashSwitch{
	private static final String FILESEP = System.getProperty("file.separator");
	private static final String NL = System.getProperty("line.separator");
	public static void main(String[] args) {
		
		String inputDirPath = "C:\\avatol\\git\\avatol_cv\\matrix_downloads\\BAT\\input";
		String outputDirPath = "C:\\avatol\\git\\avatol_cv\\matrix_downloads\\BAT\\output";
		String ssmPath = "C:\\avatol\\git\\avatol_cv\\scoredSetMetadata";
		try {
			//SlashSwitch ss1 = new SlashSwitch(inputDirPath);
			SlashSwitch ss2 = new SlashSwitch(outputDirPath);
			// ss3 = new SlashSwitch(ssmPath);
		}
		catch(IOException ioe){
			
		}
		

	}
	public SlashSwitch(String path) throws IOException {
		File dir = new File(path);
		File[] files = dir.listFiles();
		for (File f : files){
			if (f.isDirectory()){
				String thisPath = f.getAbsolutePath();
				SlashSwitch ss = new SlashSwitch(thisPath);
			}
			else {
				String parent = f.getParent();
				String name = f.getName();
				String backupName = name.replaceFirst(".txt", ".bku");
				String backupPath = parent + FILESEP + backupName;
				File backupFile = new File(backupPath);
				f.renameTo(backupFile);
				BufferedReader reader = new BufferedReader(new FileReader(backupPath));
				BufferedWriter writer = new BufferedWriter(new FileWriter(f.getAbsolutePath()));
				String line = null;
				while (null != (line = reader.readLine())){
					String newLine = "";
					if (line.indexOf("\\") != -1){
						newLine = line.replaceAll("\\\\", "/");
					}
					else if (line.indexOf("/") != -1){
						newLine = line.replaceAll("/", "\\\\");
					}
					else {
						newLine = line;
					}
					writer.write(newLine + NL);
				}
				reader.close();
				writer.close();
				backupFile = new File(backupPath);
				backupFile.delete();
			}
		}
	}

}
