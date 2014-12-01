package edu.oregonstate.eecs.iis.avatolcv;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edu.oregonstate.eecs.iis.avatolcv.mb.MorphobankSDDFile;

public class SummaryFile {
	private static final String DELIM = ",";
	public static final String SUMMARY_FILENAME = "summary.txt";
	public static final String CHARACTER_PREFIX = "character";
	public static final String MEDIA_PREFIX = "media";
	public static final String TAXON_PREFIX = "taxon";
	public static final String VIEW_PREFIX = "view";
	
	private static final String NL = System.getProperty("line.separator");
	private static final String FILESEP = System.getProperty("file.separator");
    private String path;
    private List<String> entries = new ArrayList<String>();
    private MorphobankSDDFile sddFile = null;
    public SummaryFile(String path, MorphobankSDDFile sddFile){
    	this.path = path;
    	this.sddFile = sddFile;
    }
    public void addTaxonEntry(String id, String name){
    	addEntry(TAXON_PREFIX, id, name);
    }
    public void addCharacterEntry(String id, String name){
    	addEntry(CHARACTER_PREFIX, id, name);
    }
    public void addMediaEntry(String id, String name){
    	addEntry(MEDIA_PREFIX, id, name);
    }
    public void addViewEntry(String id, String name){
    	addEntry(VIEW_PREFIX, id, name);
    }
    public void addEntry(String prefix, String id, String name){
    	String newLine = prefix + DELIM + id + DELIM + name;
    	if (!entries.contains(newLine)){
    		entries.add(newLine);
    	}
    }
    public void persist() throws AvatolCVException {
    	Collections.sort(entries);
    	File f = new File(this.path);
    	if (f.exists()){
    		f.delete();
    	}
    	try {
    		BufferedWriter writer = new BufferedWriter(new FileWriter(this.path));
        	for (String s : entries){
    			writer.write(s + NL);
    		}
    		writer.close();
    	}
    	catch(IOException ioe){
    		throw new AvatolCVException("problem persisting summary file " + this.path + " " + ioe.getMessage());
    	}
    }

    public void filter(List<String> charIds, String viewId, String newInputDir) throws AvatolCVException {
    	List<String> filteredList = new ArrayList<String>();
    	String path = newInputDir + FILESEP + SUMMARY_FILENAME;
    	File f = new File(path);
    	if (f.exists()){
    		f.delete();
    	}
    	try{
    		for (String entry : this.entries){
        		String[] parts = entry.split(DELIM);
        		if (parts[0].equals(CHARACTER_PREFIX)){
        			String charId = parts[1];
        			if (charIds.contains(charId)){
        				filteredList.add(entry);
        			}
        		}
        		else if (parts[0].equals(MEDIA_PREFIX)){
        			String mediaId = parts[1];
        			if (this.sddFile.isMediaOfView(mediaId, viewId)){
        				filteredList.add(entry);
        			}
        		}
     
        		else if (parts[0].equals(VIEW_PREFIX)){
        			if (parts[1].equals(viewId)){
        				filteredList.add(entry);
        			}
        		}
        		else {
        			// just pass it through
        			filteredList.add(entry);
        		}
        	}

    		BufferedWriter writer = new BufferedWriter(new FileWriter(path));
    		Collections.sort(filteredList);
    		for (String s : filteredList){
    			writer.write(s + NL);
    		}
    		writer.close();
    	}
    	catch(IOException ioe){
    		throw new AvatolCVException("problem creating filtered summary file " + path + " " + ioe.getMessage());
    	}
    	
    }
}
