package edu.oregonstate.eecs.iis.avatolcv.tools;

import java.io.File;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import edu.oregonstate.eecs.iis.avatolcv.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.AvatolCVFileSystem;
import edu.oregonstate.eecs.iis.avatolcv.normalized.NormalizedImageInfo;
import edu.oregonstate.eecs.iis.avatolcv.normalized.NormalizedImageInfos;
import edu.oregonstate.eecs.iis.avatolcv.normalized.NormalizedKey;
import edu.oregonstate.eecs.iis.avatolcv.normalized.NormalizedValue;

public class DatasetEditor {
    private static final String FILESEP = System.getProperty("file.separator");
    // normalized image info names
    private List<String> niiFilenames = null;
    private NormalizedImageInfos imageInfos = null;
    List<NormalizedKey> allKeys = null;
    private Hashtable<String, NormalizedImageInfo> niiForNiiFilenameHash = null;
    private Hashtable<String, List<NormalizedKey>> keysToDeleteValuesForPerNiiFilenameHash = null;
    private List<String> niiFilenamesToDeleteList = null;
	private Hashtable<String, String> niiPathnameForNiiFilenameHash = null;

	public void clear(){
		
	}
	public static NormalizedImageInfos getNormalizedImageInfosForDataset(String datasetName) throws AvatolCVException {
		String datasetPath = AvatolCVFileSystem.getSessionsRoot() + FILESEP + datasetName;
		String imageInfoPath = datasetPath + FILESEP + "normalized" + FILESEP + "imageInfo";
		NormalizedImageInfos niis = new NormalizedImageInfos(imageInfoPath);
		return niis;
	}
	
	public void loadDataset(String datasetName) throws AvatolCVException {
		niiForNiiFilenameHash = new Hashtable<String, NormalizedImageInfo>();
		keysToDeleteValuesForPerNiiFilenameHash = new Hashtable<String, List<NormalizedKey>>();
    	niiPathnameForNiiFilenameHash     = new Hashtable<String, String>();
		niiFilenamesToDeleteList = new ArrayList<String>();
		niiFilenames = new ArrayList<String>();
		imageInfos = getNormalizedImageInfosForDataset(datasetName);
		allKeys = getAllKeys(imageInfos);
		List<NormalizedImageInfo> niis = imageInfos.getNormalizedImageInfosForDataset();
		for (NormalizedImageInfo nii : niis){
			String niiFilename = nii.getNiiFilename();  
			niiFilenames.add(niiFilename);
			niiForNiiFilenameHash.put(niiFilename,  nii);
	    	niiPathnameForNiiFilenameHash.put(niiFilename, nii.getPath());
		}
	}
	public List<NormalizedKey> getAllKeys(NormalizedImageInfos imageInfos) throws AvatolCVException {
		List<NormalizedKey> result = new ArrayList<NormalizedKey>();
		List<NormalizedImageInfo> niis = imageInfos.getNormalizedImageInfosForDataset();
		for (NormalizedImageInfo nii : niis){
			List<NormalizedKey> keys = nii.getKeys();
			for (NormalizedKey key : keys){
				if (!result.contains(key)){
					result.add(key);
				}
			}
		}
		return result;
	}
	public static boolean isLocalDataset(File f){
		File[] files = f.listFiles();
		for (File file : files){
			if (file.getName().equals("morphobank")){
				return false;
			}
		}
		for (File file : files){
			if (file.getName().equals("bisque")){
				return false;
			}
		}
		return true;
	}
	public List<NormalizedKey> getPropKeys(){
		List<NormalizedKey> result = new ArrayList<NormalizedKey>();
		result.addAll(allKeys);
		return result;
	}
	public List<String> getNiiFilenames(){
		List<String> result = new ArrayList<String>();
		result.addAll(niiFilenames);
		return result;
	}
	public String getValueForProperty(String niiFilename, NormalizedKey propKey) throws AvatolCVException {
		NormalizedImageInfo nii = niiForNiiFilenameHash.get(niiFilename);
		if (null == nii){
			throw new AvatolCVException("no NormalizedImageInfo found with filename " + niiFilename);
		}
		NormalizedValue nv = nii.getValueForKey(propKey);
		if (null == nv){
			return "";
		}
		return nv.getName();
	}
	public String getImageNameForNiiFilename(String niiFilename)  throws AvatolCVException {
		NormalizedImageInfo nii = niiForNiiFilenameHash.get(niiFilename);
		if (null == nii){
			throw new AvatolCVException("no NormalizedImageInfo found with filename " + niiFilename);
		}
		String imageName = nii.getImageName();
		return imageName;
	}
	
	public String getNiiPathnameForNiiFilename(String niiFilename)  throws AvatolCVException {
		NormalizedImageInfo nii = niiForNiiFilenameHash.get(niiFilename);
		if (null == nii){
			throw new AvatolCVException("no NormalizedImageInfo found with filename " + niiFilename);
		}
		return nii.getPath();
	}
	public boolean isNiiFilenameMarkedForDelete(String niiFilename){
		if (niiFilenamesToDeleteList.contains(niiFilename)){
			return true;
		}
		return false;
	}
	public boolean isPropertyMarkedForDelete(String niiFilename, NormalizedKey propKey){
		List<NormalizedKey> keysForNiiFilename = keysToDeleteValuesForPerNiiFilenameHash.get(niiFilename);
		if (null == keysForNiiFilename){
			return false;
		}
		if (keysForNiiFilename.contains(propKey)){
			return true;
		}
		return false;
	}
	public void toggleEditForPropkey(String niiFilename, NormalizedKey propKey){
		List<NormalizedKey> keysForImage = keysToDeleteValuesForPerNiiFilenameHash.get(niiFilename);
		if (null == keysForImage){
			keysForImage = new ArrayList<NormalizedKey>();
			keysToDeleteValuesForPerNiiFilenameHash.put(niiFilename, keysForImage);
		}
		if (keysForImage.contains(propKey)){
			keysForImage.remove(propKey);
			System.out.println("cancel delete for " + niiFilename + " " + propKey.getName());
		}
		else {
			keysForImage.add(propKey);
			System.out.println("DELETE for " + niiFilename + " " + propKey.getName());
		}
	}
	
	public void toggleEditForNiiFilename(String niiFilename){
		if (niiFilenamesToDeleteList.contains(niiFilename)){
			niiFilenamesToDeleteList.remove(niiFilename);
			System.out.println("cancel delete for " + niiFilename);
		}
		else {
			niiFilenamesToDeleteList.add(niiFilename);
			System.out.println("DELETE for " + niiFilename );
			List<NormalizedKey> keysToDelete = keysToDeleteValuesForPerNiiFilenameHash.get(niiFilename);
			if (null != keysToDelete){
				keysToDelete.clear();
			}
		}
	}
	public void clearEdits(){
		niiFilenamesToDeleteList.clear();
		Enumeration<String> niiFilenameEnum = keysToDeleteValuesForPerNiiFilenameHash.keys();
		while (niiFilenameEnum.hasMoreElements()){
			String niiFilename = niiFilenameEnum.nextElement();
			List<NormalizedKey> keysForImage = keysToDeleteValuesForPerNiiFilenameHash.get(niiFilename);
			if (null != keysForImage){
				keysForImage.clear();
			}
		}
	}
	private void deleteNiiFile(NormalizedImageInfo nii){
		String path = nii.getPath();
		File f = new File(path);
		f.delete();
	}
	public void saveEdits() throws AvatolCVException {
		Enumeration<String> niiFilenamesEnum = niiForNiiFilenameHash.keys();
		while (niiFilenamesEnum.hasMoreElements()){
			String niiFilename = niiFilenamesEnum.nextElement();
			if (niiFilename.endsWith("5EA_1.txt")){
				int foo = 3;
				int bar = foo;
			//	problem is that imageName is the name of the original image, not the name of the niiFilename - need another lookup for that.
			}
			NormalizedImageInfo nii = niiForNiiFilenameHash.get(niiFilename);
			if (niiFilenamesToDeleteList.contains(niiFilename)){
				deleteNiiFile(nii);
			}
			else {
				List<NormalizedKey> keysForImage = keysToDeleteValuesForPerNiiFilenameHash.get(niiFilename);
				boolean madeEdit = false;
				if (null != keysForImage){
					for (NormalizedKey key : keysForImage){
						if (nii.hasKey(key)){
							nii.forgetValue(key);
							madeEdit = true;
						}
					}
				}
				if (madeEdit){
					deleteNiiFile(nii);
					nii.persist();
				}
			}
		}
	}
}
