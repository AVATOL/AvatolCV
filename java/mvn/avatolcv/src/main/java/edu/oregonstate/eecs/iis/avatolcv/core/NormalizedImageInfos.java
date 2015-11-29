package edu.oregonstate.eecs.iis.avatolcv.core;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Properties;

import edu.oregonstate.eecs.iis.avatolcv.AvatolCVException;

public class NormalizedImageInfos {
	private static final String FILESEP = System.getProperty("file.separator");
	private static final String NL = System.getProperty("line.separator");
	private String root = null;
	private Hashtable<String, NormalizedImageInfo> niiHash = new Hashtable<String, NormalizedImageInfo>();
	private List<String> niiAllPresent = new ArrayList<String>();
	private List<String> niiSession = new ArrayList<String>();
	public NormalizedImageInfos(String root) throws AvatolCVException {
		this.root = root;
		File dirFile = new File(this.root);
		// load the existing files
		File[] files = dirFile.listFiles();
		for (File f : files){
			NormalizedImageInfo nii = new NormalizedImageInfo(f.getAbsolutePath());
			niiHash.put(f.getName(), nii);
			System.out.println("+ adding " + f.getName());
			niiAllPresent.add(f.getName());
		}
	}
	public void ensureAllKeysPresentInAllImageInfos() throws AvatolCVException {
		List<String> scorableKeys = getScorableKeys();
		for (String name : niiAllPresent){
			NormalizedImageInfo nii = niiHash.get(name);
			for (String key : scorableKeys){
				if (!nii.hasKey(key)){
					nii.addUnscoredKey(key);
				}
			}
		}
	}
	public List<String> getScorableKeys(){
		List<String> scorableKeys = new ArrayList<String>();
		for (String name : niiAllPresent){
			NormalizedImageInfo nii = niiHash.get(name);
			List<String> keys = nii.getKeys();
			for (String key : keys){
				if (key.startsWith(NormalizedImageInfo.KEY_TIMESTAMP)){
					//skip
				}
				else if (key.startsWith(NormalizedImageInfo.KEY_IMAGE_NAME)){
					// skip
				}
				else if (key.startsWith(NormalizedImageInfo.KEY_ANNOTATION)){
					// skip
				}
				else if (key.startsWith(NormalizedImageInfo.PREFIX)){
					// skip
				}
				else {
					if (!scorableKeys.contains(key)){
						scorableKeys.add(key);
					}
				}
			}
		}
		return scorableKeys;
	}
	
	public List<NormalizedImageInfo> getNormalizedImageInfosForSession(){
		List<NormalizedImageInfo> result = new ArrayList<NormalizedImageInfo>();
		for (String s : niiSession){
			result.add(niiHash.get(s));
		}
		return result;
	}
	public List<NormalizedImageInfo> getSessionNIIsForKeyValue(String key, String value){
		List<NormalizedImageInfo> niis = new ArrayList<NormalizedImageInfo>();
		for (String name : niiSession){
			NormalizedImageInfo nii = niiHash.get(name);
			if (nii.hasKey(key)){
				if (nii.getValueForKey(key).equals(value)){
					niis.add(nii);
				}
			}
		}
		return niis;
	}
	public void flush(){
		niiAllPresent.clear();
		niiSession.clear();
		niiHash.clear();
		File dirFile = new File(this.root);
		File[] files = dirFile.listFiles();
		for (File f : files){
			f.delete();
		}
	}
	public Object getTotalCount() {
		return niiAllPresent.size();
	}
	public Object getSessionCount() {
		return niiSession.size();
	}
	public boolean doesMatchingNormalizedImageFileExistAtPath(List<String> lines, String mediaID, String path) throws AvatolCVException {
		File f = new File(path);
		if (!f.exists()){
			return false;
		}
		NormalizedImageInfo niiMatchCandidate = new NormalizedImageInfo(lines, mediaID, path);
		NormalizedImageInfo niiExisting = new NormalizedImageInfo(path);
		return niiMatchCandidate.equals(niiExisting);
	}
	
	public String createNormalizedImageInfoFromLines(String mediaID, List<String> lines) throws AvatolCVException {
		File dirFile = new File(this.root);
		List<String> matchingNumbersForMediaID = new ArrayList<String>();
		// look through the files
		File[] files = dirFile.listFiles();
		for (File f : files){
			String fName = f.getName();
			String[] parts = fName.split("\\.");
			String rootName = parts[0];
			String[] rootParts = rootName.split("_");
			if (rootParts[0].equals(mediaID)){
				//media ID matches, note number suffix
				matchingNumbersForMediaID.add(rootParts[1]);
				if (doesMatchingNormalizedImageFileExistAtPath(lines, mediaID, f.getAbsolutePath())){
					return f.getAbsolutePath();
				}
			}
		}
		//none of the files matched, check for the first unused number suffix and store with that
		String newSuffix = getFirstUnusedSuffix(matchingNumbersForMediaID);
		String newFilename = mediaID + "_" + newSuffix + ".txt";
		String newPath = this.root + FILESEP + newFilename;
		// then load it up into the NII object form 
		NormalizedImageInfo nii = new NormalizedImageInfo(lines, mediaID, newPath);
		nii.persist();
		niiHash.put(newFilename, nii);
		niiAllPresent.add(newFilename);
		System.out.println("$$ adding " + newFilename);
		return newPath;
	}
	public NormalizedImageInfo getNormalizedImageInfoForSessionWithName(String name) throws AvatolCVException {
		if (!(niiSession.contains(name))){
			throw new AvatolCVException("No NormalizedImageInfo named " + name + " present in current session");
		}
		NormalizedImageInfo nii = niiHash.get(name);
		if (null == nii){
			throw new AvatolCVException("No NormalizedImageInfo named " + name + " known in niiHash.");
		}
		return nii;
	}
	public void focusToSession(List<String> filenames) throws AvatolCVException {
		this.niiSession.clear();
		for (String s : niiAllPresent){
			if (s.equals("00-3HPPsgoaeBaq2rDrGPnvhn_2.txt")){
				System.out.println("%%% " + s);
			}
		}
		for (String name : filenames){
			
			if (!niiAllPresent.contains(name)){
				throw new AvatolCVException("given filename " + name + " not present.");
			}
			this.niiSession.add(name);
		}
	}
	public static String getFirstUnusedSuffix(List<String> suffixList){
		int i = 1;
		while (true){
			String currentNumber = "" + i;
			if (suffixList.contains(currentNumber)){
				i++;
			}
			else {
				return currentNumber;
			}
		}
	}
}
