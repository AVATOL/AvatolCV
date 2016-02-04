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
    private List<String> niiNames = null;
    private NormalizedImageInfos imageInfos = null;
    List<NormalizedKey> allKeys = null;
    private Hashtable<String, NormalizedImageInfo> niiForImagenameHash = null;
    private Hashtable<String, List<NormalizedKey>> keysToDeleteValuesForPerImageHash = null;
    private List<String> imagesToDeleteList = null;
	private Hashtable<String, String> niiPathnameForImageNameHash = null;

	public void clear(){
		
	}
	public static NormalizedImageInfos getNormalizedImageInfosForDataset(String datasetName) throws AvatolCVException {
		String datasetPath = AvatolCVFileSystem.getSessionsRoot() + FILESEP + datasetName;
		String imageInfoPath = datasetPath + FILESEP + "normalized" + FILESEP + "imageInfo";
		NormalizedImageInfos niis = new NormalizedImageInfos(imageInfoPath);
		return niis;
	}
	
	public void loadDataset(String datasetName) throws AvatolCVException {
		niiForImagenameHash = new Hashtable<String, NormalizedImageInfo>();
		keysToDeleteValuesForPerImageHash = new Hashtable<String, List<NormalizedKey>>();
    	niiPathnameForImageNameHash     = new Hashtable<String, String>();
		imagesToDeleteList = new ArrayList<String>();
		niiNames = new ArrayList<String>();
		imageInfos = getNormalizedImageInfosForDataset(datasetName);
		allKeys = getAllKeys(imageInfos);
		List<NormalizedImageInfo> niis = imageInfos.getNormalizedImageInfosForDataset();
		for (NormalizedImageInfo nii : niis){
			//String imageName = nii.getNiiFilename();
			String imageName = nii.getImageName();
			niiNames.add(imageName);
			niiForImagenameHash.put(imageName,  nii);
	    	niiPathnameForImageNameHash.put(imageName, nii.getPath());
		}
	}
	public List<NormalizedKey> getAllKeys(NormalizedImageInfos imageInfos) throws AvatolCVException {
		List<NormalizedKey> result = new ArrayList<NormalizedKey>();
		List<NormalizedImageInfo> niis = imageInfos.getNormalizedImageInfosForDataset();
		for (NormalizedImageInfo nii : niis){
			String imageName = nii.getImageName();
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
	public List<String> getNiiNames(){
		List<String> result = new ArrayList<String>();
		result.addAll(niiNames);
		return result;
	}
	public String getValueForProperty(String niiName, NormalizedKey propKey) throws AvatolCVException {
		NormalizedImageInfo nii = niiForImagenameHash.get(niiName);
		if (null == nii){
			throw new AvatolCVException("no NormalizedImageInfo found with filename " + niiName);
		}
		NormalizedValue nv = nii.getValueForKey(propKey);
		if (null == nv){
			return "";
		}
		return nv.getName();
	}
	public String getImageNameForNiiName(String niiName)  throws AvatolCVException {
		NormalizedImageInfo nii = niiForImagenameHash.get(niiName);
		if (null == nii){
			throw new AvatolCVException("no NormalizedImageInfo found with filename " + niiName);
		}
		String imageName = nii.getImageName();
		return imageName;
	}
	
	public String getNiiPathnameForNiiName(String niiName)  throws AvatolCVException {
		NormalizedImageInfo nii = niiForImagenameHash.get(niiName);
		if (null == nii){
			throw new AvatolCVException("no NormalizedImageInfo found with filename " + niiName);
		}
		return nii.getPath();
	}
	public boolean isImageNameMarkedForDelete(String imageName){
		if (imagesToDeleteList.contains(imageName)){
			return true;
		}
		return false;
	}
	public boolean isPropertyMarkedForDelete(String imageName, NormalizedKey propKey){
		List<NormalizedKey> keysForImage = keysToDeleteValuesForPerImageHash.get(imageName);
		if (null == keysForImage){
			return false;
		}
		if (keysForImage.contains(propKey)){
			return true;
		}
		return false;
	}
	public void toggleEditForPropkey(String imageName, NormalizedKey propKey){
		List<NormalizedKey> keysForImage = keysToDeleteValuesForPerImageHash.get(imageName);
		if (null == keysForImage){
			keysForImage = new ArrayList<NormalizedKey>();
			keysToDeleteValuesForPerImageHash.put(imageName, keysForImage);
		}
		if (keysForImage.contains(propKey)){
			keysForImage.remove(propKey);
			System.out.println("cancel delete for " + imageName + " " + propKey.getName());
		}
		else {
			keysForImage.add(propKey);
			System.out.println("DELETE for " + imageName + " " + propKey.getName());
		}
	}
	
	public void toggleEditForImageName(String imageName){
		if (imagesToDeleteList.contains(imageName)){
			imagesToDeleteList.remove(imageName);
			System.out.println("cancel delete for " + imageName);
		}
		else {
			imagesToDeleteList.add(imageName);
			System.out.println("DELETE for " + imageName );
			List<NormalizedKey> keysToDelete = keysToDeleteValuesForPerImageHash.get(imageName);
			if (null != keysToDelete){
				keysToDelete.clear();
			}
		}
	}
	public void clearEdits(){
		imagesToDeleteList.clear();
		Enumeration<String> imageNameEnum = keysToDeleteValuesForPerImageHash.keys();
		while (imageNameEnum.hasMoreElements()){
			String imageName = imageNameEnum.nextElement();
			List<NormalizedKey> keysForImage = keysToDeleteValuesForPerImageHash.get(imageName);
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
		Enumeration<String> imageNamesEnum = niiForImagenameHash.keys();
		while (imageNamesEnum.hasMoreElements()){
			String imageName = imageNamesEnum.nextElement();
			if (imageName.endsWith("5EA_1.txt")){
				int foo = 3;
				int bar = foo;
			//	problem is that imageName is the name of the original image, not the name of the niiFilename - need another lookup for that.
			}
			NormalizedImageInfo nii = niiForImagenameHash.get(imageName);
			if (imagesToDeleteList.contains(imageName)){
				deleteNiiFile(nii);
			}
			else {
				List<NormalizedKey> keysForImage = keysToDeleteValuesForPerImageHash.get(imageName);
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
