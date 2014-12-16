package edu.oregonstate.eecs.iis.avatolcv.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public class AnnotationLineCounter {
    private Hashtable<String, Integer> countMap = new Hashtable<String, Integer>();
    private List<String> countsRepresented = new ArrayList<String>();
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String path = "C:\\avatol\\git\\avatol_cv\\matrix_downloads\\BAT\\annotations";
		AnnotationLineCounter alc = new AnnotationLineCounter(path);
	}
	public AnnotationLineCounter(String path){
		File dir = new File(path);
		File[] files = dir.listFiles();
		for (File f : files){
			if (f.getName().startsWith("m") && f.getName().endsWith(".txt")){
				try{
					BufferedReader reader = new BufferedReader(new FileReader(f.getAbsolutePath()));
					String line = null;
					int count = 0;
					while (null != (line = reader.readLine())){
						count++;
					}
					registerCount(count);
					reader.close();
				}
				catch(IOException ioe){
					System.out.println("Problem reading file " + f.getAbsolutePath());
				}
			}
		}
		for (String countRepresented : countsRepresented){
			System.out.println("count " + countRepresented + " " + countMap.get(countRepresented) + " times");
		}
	}
	public void registerCount(int count){
		String countString = "" + count;
		if (!countsRepresented.contains(countString)){
			countsRepresented.add(countString);
		}
		Integer countTally = countMap.get(countString);
		if (null == countTally){
			countTally = new Integer(0);
			countMap.put(countString, countTally);
		}
		Integer newCountTally = new Integer(countTally.intValue() + 1);
		countMap.put(countString, newCountTally);
	}

}
