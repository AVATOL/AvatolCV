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
	private List<String> knownKeyVals = new ArrayList<String>();
	private Hashtable<String, FilterItem> pairForKeyHash = new Hashtable<String, FilterItem>();
	private String sessionDir = null;
	public DataFilter(String sessionDir){
		this.sessionDir = sessionDir;
	}
	public List<FilterItem> getItems(){
		List<FilterItem> result = new ArrayList<FilterItem>();
		for (String key : knownKeyVals){
			FilterItem fi = pairForKeyHash.get(key);
			result.add(fi);
		}
		Collections.sort(result);
		return result;
	}
	public FilterItem addFilterItem(NormalizedKey key, NormalizedValue value, boolean isEditable) throws AvatolCVException {
		String keyValString = key.toString() + "=" + value.toString();
		if (knownKeyVals.contains(keyValString)){
			FilterItem existingItem = pairForKeyHash.get(keyValString);
			return existingItem;
		}
		FilterItem fi = new FilterItem(key, value, isEditable);

		knownKeyVals.add(keyValString);
		pairForKeyHash.put(keyValString, fi);
		persist();
		return fi;
	}
	public class FilterItem implements Comparable<FilterItem> {
		private NormalizedKey key;
		private NormalizedValue value;
		private boolean isSelected = false;
		private boolean isEditable = true;
		public FilterItem(NormalizedKey key, NormalizedValue value, boolean isEditable){
			this.key = key;
			this.value = value;
			this.isEditable = isEditable;
		}
		public NormalizedKey getKey(){
		    return this.key;
		}
		public void setSelected(boolean value){
			this.isSelected = value;
		}
		public boolean isEditable(){
		    return this.isEditable;
		}
		public NormalizedValue getValue(){
			return this.value;
		}
		public boolean isSelected(){
			return this.isSelected;
		}
		@Override
		public int compareTo(FilterItem other) {
	        NormalizedKey otherKey = other.getKey();
	        return key.compareTo(otherKey);
		}
	}
	/*public void enablePropertyValue(String name, String value) throws AvatolCVException {
		String id = name + "_" + value;
		if (!knownProperties.contains(id)){
			throw new AvatolCVException("Filter cannot enable unknown property and value : " + name + " , " + value);
		}
		FilterItem p = pairForKeyHash.get(id);
		p.setSelected(true);
		persist();
	}*/
	/*public void disablePropertyValue(String name, String value) throws AvatolCVException {
		String id = name + "_" + value;
		if (!knownProperties.contains(id)){
			throw new AvatolCVException("Filter cannot disable unknown property and value : " + name + " , " + value);
		}
		FilterItem p = pairForKeyHash.get(id);
		p.setSelected(false);
		persist();
	}*/
	public String getFilterFilePath(){
		return this.sessionDir + FILESEP + FILTER_FILE_NAME;
	}
	public void persist() throws AvatolCVException {
		String path = getFilterFilePath();
		List<FilterItem> items = getItems();
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(path));
			for (FilterItem item : items){
				NormalizedKey key = item.getKey();
				NormalizedValue value = item.getValue();
				String isEnabled = "" + item.isSelected();
				String isEditable = "" + item.isEditable();
				writer.write(key + "," + value + "," + isEnabled + "," + isEditable + NL);
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
				String key = parts[0];
				String value = parts[1];
				String isSelectedString = parts[2];
                String isEditableString = parts[3];
                Boolean isEditableBoolean = new Boolean (isEditableString);
                boolean isEditable = isEditableBoolean.booleanValue();
				FilterItem fi = addFilterItem(new NormalizedKey(key), new NormalizedValue(value), isEditable);
				Boolean b = new Boolean(isSelectedString);
				boolean isSelected = b.booleanValue();
				fi.setSelected(isSelected);
			}
			reader.close();
			return true;
		}
		catch(IOException ioe){
			throw new AvatolCVException("problem loading filter information to path " + path);
		}
	}
}
