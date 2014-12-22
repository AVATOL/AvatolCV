package edu.oregonstate.eecs.iis.avatolcv.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AnnotationReaderUtility {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
        String annotationDirPath = "C:\\avatol\\git\\avatol_cv\\matrix_downloads\\BAT\\annotations";
        AnnotationReaderUtility aru = new AnnotationReaderUtility(annotationDirPath);
	}
	public AnnotationReaderUtility(String dir){
		try {
			File dirFile = new File(dir);
			File[] files = dirFile.listFiles();
			List<String> oddIdList = new ArrayList<String>();
			for (File file : files){
				String path = file.getAbsolutePath();
				System.out.println("reading "  + path);
				BufferedReader reader = new BufferedReader(new FileReader(path));
				String line = null;
				while (null != (line = reader.readLine())){
					String[] parts = line.split(":");
					String stateId = parts[3];
					if (stateId.startsWith("s") && stateId.length() > 1){
						// its a normal stateId
					}
					else {
						if (!oddIdList.contains(stateId)){
							oddIdList.add(stateId);
						}
					}
				}
				reader.close();
			}
			for (String s : oddIdList){
				System.out.println("odd state id : " + s);
			}
		}
		catch(FileNotFoundException fnfe){
			System.out.println("problem reading file");
		}
		catch(IOException ioe){
			System.out.println("problem reading file");
		}
		
	}

}
//29.3839031339031,56.1378205128205:c104521:Parafibula, Fusion to fibula, presence:NPA:NPA