package edu.oregonstate.eecs.iis.avatolcv.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class SwitchPlatform {

	private static final String FILESEP = System.getProperty("file.separator");
	private static final String NL = System.getProperty("line.separator");
	public static void main(String[] args) {
		
		String path = "C:\\avatol\\git\\avatol_cv\\matrix_downloads\\BATUnix";
		String inputDirPath = "C:\\avatol\\git\\avatol_cv\\matrix_downloads\\BAT\\input";
		String outputDirPath = "C:\\avatol\\git\\avatol_cv\\matrix_downloads\\BAT\\output";
		String exampleFile = "C:\\avatol\\git\\avatol_cv\\scoredSetMetadata\\1420476645_DPM_BAT.txt";
		String ssmPath = "C:\\avatol\\git\\avatol_cv\\scoredSetMetadata";

		int hexOA = 0xA; 
		String oa = "" + (char)hexOA ;
		int hexOD = 0xD;
		String od = "" + (char)hexOD ;
		String crlf = od+oa;
		try {
			if (fileHasForeSlashes(exampleFile)){
				SwitchPlatform sp = new SwitchPlatform(path, od, crlf);
			}
			else {
				SwitchPlatform sp = new SwitchPlatform(path, crlf, od);
			}
		}
		catch(IOException ioe){
			
		}
		

	}
	public static boolean fileHasForeSlashes(String path){
		boolean result = false;
		try {
			BufferedReader reader = new BufferedReader(new FileReader(path));
			String line = null;
			while (null != (line = reader.readLine())){
				if (line.contains("/")){
					result = true;
				}
			}
			reader.close();
			return result;
		}
		catch(Exception e){
			e.printStackTrace();
			System.out.println(e.getMessage());
		}
		return result;
	}
	public SwitchPlatform(String path, String oldEnding, String newEnding) throws IOException {
		File dir = new File(path);
		File[] files = dir.listFiles();
		for (File f : files){
			if (f.isDirectory()){
				String thisPath = f.getAbsolutePath();
				SwitchPlatform ss = new SwitchPlatform(thisPath, oldEnding, newEnding);
			}
			else if (f.getName().contains(".txt")){
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
					writer.write(newLine + newEnding);
				}
				reader.close();
				writer.close();
				backupFile = new File(backupPath);
				backupFile.delete();
			}
		}
	}
}
