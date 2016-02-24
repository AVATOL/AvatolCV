package edu.oregonstate.eecs.iis.avatolcv.results;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;

import edu.oregonstate.eecs.iis.avatolcv.algorithm.AlgorithmInput;
import edu.oregonstate.eecs.iis.avatolcv.util.ClassicSplitter;

public class OutputImageSorter {
	private List<String> inputImageIDs = new ArrayList<String>();
	private Hashtable<String, String> pathForInputImageIDPlusSuffixHash = new Hashtable<String, String>();
    List<String> inputImageSuffixes = new ArrayList<String>();
    private Hashtable<String, String> pathForOutputImageIDPlusSuffixHash = new Hashtable<String, String>();
    List<String> outputImageSuffixes = new ArrayList<String>();
    private boolean rawInputSuffixFound = false;
    
	public void addInputSuffixAndPaths(String inputSuffix, List<String> paths){
		if (AlgorithmInput.NO_SUFFIX.equals(inputSuffix)){
    		rawInputSuffixFound = true;
    	}
		else if (!inputImageSuffixes.contains(inputSuffix)){
			inputImageSuffixes.add(inputSuffix);
		}
		
		for (String imagePath : paths){
			File f = new File(imagePath);
			String name = f.getName();
        	String[] parts = ClassicSplitter.splitt(name, '_');
        	String imageID = parts[0];
        	if (!inputImageIDs.contains(imageID)){
        		inputImageIDs.add(imageID);
        	}
        	String key = getKeyForImageIDAndSuffix(imageID, inputSuffix);
        	pathForInputImageIDPlusSuffixHash.put(key, imagePath);
        }
	}
	public void addOutputSuffixAndPaths(String outputSuffix, List<String> paths){
		if (!outputImageSuffixes.contains(outputSuffix)){
			outputImageSuffixes.add(outputSuffix);
		}
		for (String imagePath : paths){
			File f = new File(imagePath);
			String filename = f.getName();
        	String[] parts = ClassicSplitter.splitt(filename, '_');
        	String imageID = parts[0];
        	
        	String key = getKeyForImageIDAndSuffix(imageID, outputSuffix);
        	pathForOutputImageIDPlusSuffixHash.put(key, imagePath);
        }
	}
	private String getKeyForImageIDAndSuffix(String imageID, String suffix){
    	return imageID + suffix;
    }
	public List<String> getImageIDs(){
		List<String> result = new ArrayList<String>();
		result.addAll(inputImageIDs);
		return result;
	}
	public void sort(){
		Collections.sort(inputImageIDs);
		List<String> inputSuffixes = new ArrayList<String>();
		if (rawInputSuffixFound){
        	inputSuffixes.add(AlgorithmInput.NO_SUFFIX);
        }
        Collections.sort(inputImageSuffixes);
        inputSuffixes.addAll(inputImageSuffixes);
        inputImageSuffixes.clear();
        inputImageSuffixes.addAll(inputSuffixes);
        
        Collections.sort(outputImageSuffixes);
	}

	
	public List<String> getInputPathsForImageID(String imageID){
		List<String> result = new ArrayList<String>();
		for (String suffix : inputImageSuffixes){
			String key = getKeyForImageIDAndSuffix(imageID, suffix);
			String path = pathForInputImageIDPlusSuffixHash.get(key);
			result.add(path);
		}
		return result;
	}
	public List<String> getOutputPathsForImageID(String imageID){
		List<String> result = new ArrayList<String>();
		for (String suffix : outputImageSuffixes){
			String key = getKeyForImageIDAndSuffix(imageID, suffix);
			String path = pathForOutputImageIDPlusSuffixHash.get(key);
			result.add(path);
		}
		return result;
	}
	public List<String> getInputSuffixList(){
    	return inputImageSuffixes;
    }
    public List<String> getOutputSuffixList(){
    	return outputImageSuffixes;
    } 
}
