package edu.oregonstate.eecs.iis.avatolcv.normalized;

import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import edu.oregonstate.eecs.iis.avatolcv.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.core.ImageInfo;
import edu.oregonstate.eecs.iis.avatolcv.util.ClassicSplitter;

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
			if (!f.getName().startsWith(".")){
				NormalizedImageInfo nii = new NormalizedImageInfo(f.getAbsolutePath());
				niiHash.put(f.getName(), nii);
				//System.out.println("+ adding " + f.getName());
				niiAllPresent.add(f.getName());
			}
		}
	}
	public boolean arePointCoordinatesRelavent() throws AvatolCVException {
		for (String s : niiAllPresent){
			NormalizedImageInfo nii = niiHash.get(s);
			String coords = nii.getAnnotationCoordinates();
			if ((null != coords) && (!coords.equals(""))){
				return true;
			}
		}
		return false;
	}
	public void ensureAllKeysPresentInAllImageInfos() throws AvatolCVException {
		List<NormalizedKey> scorableKeys = getScorableKeys();
		for (String name : niiAllPresent){
			NormalizedImageInfo nii = niiHash.get(name);
			for (NormalizedKey key : scorableKeys){
				if (!nii.hasKey(key)){
					nii.addUnscoredKey(key);
				}
			}
		}
	}
	public List<NormalizedValue> getValuesForKey(NormalizedKey key){
		List<NormalizedValue> result = new ArrayList<NormalizedValue>();
		for (String name : niiSession){
			NormalizedImageInfo nii = niiHash.get(name);
			NormalizedValue value = nii.getValueForKey(key);
			if (null != value){
				if (!result.contains(value)){
					result.add(value);
				}
			}
		}
		return result;
	}
	public List<NormalizedKey> getScorableKeys(){
		List<NormalizedKey> scorableKeys = new ArrayList<NormalizedKey>();
		for (String name : niiAllPresent){
			NormalizedImageInfo nii = niiHash.get(name);
			List<NormalizedKey> keys = nii.getKeys();
			for (NormalizedKey key : keys){
				if (!scorableKeys.contains(key)){
					scorableKeys.add(key);
				}
			}
		}
		return scorableKeys;
	}
	public List<NormalizedImageInfo> getNormalizedImageInfosForDataset() throws AvatolCVException {
		List<NormalizedImageInfo> result = new ArrayList<NormalizedImageInfo>();
		for (String name: niiAllPresent){
			NormalizedImageInfo nii = niiHash.get(name);
			result.add(nii);
		}
		return result;
	}
	public List<NormalizedImageInfo> getNormalizedImageInfosForSession() throws AvatolCVException {
		List<NormalizedImageInfo> result = new ArrayList<NormalizedImageInfo>();
		for (String s : niiSession){
	    	String imageID = NormalizedImageInfo.getImageIDFromPath(s);
	    	if (!ImageInfo.isExcluded(imageID)){
	    		result.add(niiHash.get(s));
	    	}
		}
		return result;
	}
	public List<NormalizedImageInfo> getNormalizedImageInfosForSessionWithExcluded() throws AvatolCVException {
		List<NormalizedImageInfo> result = new ArrayList<NormalizedImageInfo>();
		for (String s : niiSession){
	    	String imageID = NormalizedImageInfo.getImageIDFromPath(s);
	    	result.add(niiHash.get(s));
		}
		return result;
	}
	public List<NormalizedImageInfo> getSessionNIIsForKeyValue(NormalizedKey key, NormalizedValue value){
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
	public boolean isEveryImageScoredForKey(NormalizedKey key){
	    for (String name : niiSession){
            NormalizedImageInfo nii = niiHash.get(name);
            if (nii.hasKey(key)){
                if (!nii.hasValueForKey(key)){
                    return false;
                }
            }
	    }
        return true;        
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
	public static boolean doesMatchingNormalizedImageFileExistAtPath(List<String> lines, String mediaID, String path) throws AvatolCVException {
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
			if (!f.getName().startsWith(".")){
				String[] parts = ClassicSplitter.splitt(fName,'.');
				String rootName = parts[0];
				String[] rootParts = ClassicSplitter.splitt(rootName,'_');
				if (rootParts[0].equals(mediaID)){
					//System.out.println(mediaID + " found potential match");
					//media ID matches, note number suffix
					matchingNumbersForMediaID.add(rootParts[1]);
					if (doesMatchingNormalizedImageFileExistAtPath(lines, mediaID, f.getAbsolutePath())){
						//System.out.println(mediaID + " found match");
						return f.getAbsolutePath();
					}
					else {
						//System.out.println(mediaID + " did not find match");
					}
				}
			}
			
		}
		//System.out.println(mediaID + "# making new nii");
		//none of the files matched, check for the first unused number suffix and store with that
		String newSuffix = getFirstUnusedSuffix(matchingNumbersForMediaID);
		String newFilename = mediaID + "_" + newSuffix + ".txt";
		String newPath = this.root + FILESEP + newFilename;
		// then load it up into the NII object form 
		NormalizedImageInfo nii = new NormalizedImageInfo(lines, mediaID, newPath);
		nii.persist();
		niiHash.put(newFilename, nii);
		niiAllPresent.add(newFilename);
		//System.out.println("$$ adding " + newFilename);
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
		//for (String s : niiAllPresent){
		//	if (s.equals("00-3HPPsgoaeBaq2rDrGPnvhn_2.txt")){
		//		System.out.println("%%% " + s);
		//	}
		//}
		for (String name : filenames){
			if (!niiAllPresent.contains(name)){
				throw new AvatolCVException("given filename " + name + " not present in niiAllPresent list.");
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
	
	public int getDistinctImageCountForSession() throws AvatolCVException {
	    List<NormalizedImageInfo> niis =  getNormalizedImageInfosForSession();
	    List<String> imageIDs = new ArrayList<String>();
	    for (NormalizedImageInfo nii : niis){
	        String ID = nii.getImageID();
	        if (!imageIDs.contains(ID)){
	            imageIDs.add(ID);
	        }
	    }
	    return imageIDs.size();
	}
}
