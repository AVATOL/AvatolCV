package edu.oregonstate.eecs.iis.avatolcv.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Properties;

import edu.oregonstate.eecs.iis.avatolcv.AvatolCVException;

public class NormalizedImageInfos {
	private static final String FILESEP = System.getProperty("file.separator");
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
	public String createNormalizedImageInfoFromProperties(String mediaID, Properties newProps) throws AvatolCVException {
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
				// and load up and compare to see if its already present.  If so, return the path
				Properties p = getPropertiesForPath(f.getAbsolutePath());
				if (p.equals(newProps)){
					// matches existing file
					return f.getAbsolutePath();
				}
			}
		}
		//none of the files matched, check for the first unused number suffix and store with that
		String newSuffix = getFirstUnusedSuffix(matchingNumbersForMediaID);
		String newFilename = mediaID + "_" + newSuffix + ".txt";
		String newPath = this.root + FILESEP + newFilename;
		savePropertiesAtPath(newProps, newPath);
		// then load it up into the NII object form 
		NormalizedImageInfo nii = new NormalizedImageInfo(newPath);
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
	public void savePropertiesAtPath(Properties p, String path) throws AvatolCVException {
		OutputStream output = null;
		try {
			output = new FileOutputStream(path);
			p.store(output, null);
		} 
		catch (IOException ioe) {
			throw new AvatolCVException("io exception storing properties file at path " + path, ioe);
		} 
		finally {
			if (output != null) {
				try {
					output.close();
				} 
				catch (IOException ioe) {
					throw new AvatolCVException("io exception closing properties file stored at path " + path, ioe);
				}
			}

		}
	}
	public Properties getPropertiesForPath(String path) throws AvatolCVException {
		Properties p = new Properties();
		InputStream input = null;
		try {
			input = new FileInputStream(path);
			p.load(input);
			return p;
		} catch (IOException ioe) {
			throw new AvatolCVException("io exception loading properties file from path " + path, ioe);
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException ioe) {
					throw new AvatolCVException("could not close properties file from path " + path, ioe);
				}
			}
		}
	}
}
