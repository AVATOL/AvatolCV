package edu.oregonstate.eecs.iis.avatolcv.core;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;

import edu.oregonstate.eecs.iis.avatolcv.AvatolCVException;

public class DataFilter {
	private static final String FILTER_FILE_NAME = "filter.txt";
	private static final String FILESEP = System.getProperty("file.separator");
	private static final String NL = System.getProperty("line.separator");
	private List<String> knownProperties = new ArrayList<String>();
	private Hashtable<String, Pair> pairForKeyHash = new Hashtable<String, Pair>();
	private String sessionDir = null;
	public DataFilter(String sessionDir){
		this.sessionDir = sessionDir;
	}
	public List<Pair> getItems(){
		List<Pair> result = new ArrayList<Pair>();
		for (String id : knownProperties){
			Pair p = pairForKeyHash.get(id);
			result.add(p);
		}
		Collections.sort(result);
		return result;
	}
	public Pair addPropertyValue(String name, String value, String valueId, boolean isEditable) throws AvatolCVException {
		Pair p = new Pair(name, value, valueId, isEditable);
		String id = p.getID();
		if (knownProperties.contains(id)){
			Pair existingPair = pairForKeyHash.get(id);
			return existingPair;
			//throw new AvatolCVException("Filter already has name value combination: name " + name + " value: " + value);
		}
		knownProperties.add(id);
		pairForKeyHash.put(id, p);
		persist();
		return p;
	}
	public class Pair implements Comparable<Pair> {
		private String name;
		private String value;
		private boolean isSelected = true;
		private boolean isEditable = true;
		private String valueId = null;
		public Pair(String name, String value, String valueId, boolean isEditable){
			this.name = name;
			this.value = value;
			this.valueId = valueId;
			this.isEditable = isEditable;
		}
		public String getValueID(){
		    return this.valueId;
		}
		public void setSelected(boolean value){
			this.isSelected = value;
		}
		public String getID(){
			return name + "_" + value;
		}
		public boolean isEditable(){
		    return this.isEditable;
		}
		public String getName(){
			return this.name;
		}
		public String getValue(){
			return this.value;
		}
		public boolean isSelected(){
			return this.isSelected;
		}
		@Override
		public int compareTo(Pair other) {
	        String otherID = other.getID();
	        String thisID = this.getID();
	        return thisID.compareTo(otherID);
		}
	}
	public void enablePropertyValue(String name, String value) throws AvatolCVException {
		String id = name + "_" + value;
		if (!knownProperties.contains(id)){
			throw new AvatolCVException("Filter cannot enable unknown property and value : " + name + " , " + value);
		}
		Pair p = pairForKeyHash.get(id);
		p.setSelected(true);
		persist();
	}
	public void disablePropertyValue(String name, String value) throws AvatolCVException {
		String id = name + "_" + value;
		if (!knownProperties.contains(id)){
			throw new AvatolCVException("Filter cannot disable unknown property and value : " + name + " , " + value);
		}
		Pair p = pairForKeyHash.get(id);
		p.setSelected(false);
		persist();
	}
	public String getFilterFilePath(){
		return this.sessionDir + FILESEP + FILTER_FILE_NAME;
	}
	public void persist() throws AvatolCVException {
		String path = getFilterFilePath();
		List<Pair> items = getItems();
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(path));
			for (Pair item : items){
				String name = item.getName();
				String value = item.getValue();
				String isEnabled = "" + item.isSelected();
				String isEditable = "" + item.isEditable();
				String valueId = item.getValueID();
				writer.write(name + "," + value + "," + valueId + "," + isEnabled + "," + isEditable + NL);
			}
			writer.close();
		}
		catch(IOException ioe){
			throw new AvatolCVException("problem persisting filter information to path " + path);
		}
	}
	public boolean load() throws AvatolCVException {
		String path = getFilterFilePath();
		File f = new File(path);
		if (!f.exists()){
			return false;
		}
		try {
			BufferedReader reader = new BufferedReader(new FileReader(path));
			String line = null;
			while (null != (line = reader.readLine())){
				String[] parts = line.split(",");
				String name = parts[0];
				String value = parts[1];
                String valueId = parts[2];
				String isSelectedString = parts[3];
                String isEditableString = parts[4];
                Boolean isEditableBoolean = new Boolean (isEditableString);
                boolean isEditable = isEditableBoolean.booleanValue();
				Pair p = addPropertyValue(name, value, valueId, isEditable);
				Boolean b = new Boolean(isSelectedString);
				boolean isSelected = b.booleanValue();
				p.setSelected(isSelected);
			}
			reader.close();
			return true;
		}
		catch(IOException ioe){
			throw new AvatolCVException("problem loading filter information to path " + path);
		}
	}
}
