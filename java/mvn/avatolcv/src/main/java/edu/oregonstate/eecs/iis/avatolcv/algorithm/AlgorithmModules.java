package edu.oregonstate.eecs.iis.avatolcv.algorithm;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;

import edu.oregonstate.eecs.iis.avatolcv.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.AvatolCVExceptionExpresser;
import edu.oregonstate.eecs.iis.avatolcv.AvatolCVFileSystem;
import edu.oregonstate.eecs.iis.avatolcv.Platform;
import edu.oregonstate.eecs.iis.avatolcv.algorithm.ScoringAlgorithm.ScoringScope;
import edu.oregonstate.eecs.iis.avatolcv.algorithm.ScoringAlgorithm.ScoringSessionFocus;

/**
 * 
 * @author admin-jed
 *
 * Encapsulates awareness of and "loading"(pulling into a runnable state) all the algorithms present in the modules dir.
 */
public class AlgorithmModules {
	public enum AlgType {
		SEGMENTATION,
		ORIENTATION,
		SCORING
	}
	private static final String FILESEP = System.getProperty("file.separator");
	//private Hashtable<String, Algorithm> propsForNameHashSegmentation = new Hashtable<String, Algorithm>();
	//private Hashtable<String, Algorithm> propsForNameHashOrientation = new Hashtable<String, Algorithm>();
	//private Hashtable<String, Algorithm> propsForNameHashScoring = new Hashtable<String, Algorithm>();
	//private List<String> algNamesSegmentation = new ArrayList<String>();
	//private List<String> algNamesOrientation = new ArrayList<String>();
	//private List<String> algNamesScoring = new ArrayList<String>();
	private List<Algorithm> segAlgs = new ArrayList<Algorithm>();
    private List<Algorithm> orientAlgs = new ArrayList<Algorithm>();
    private List<Algorithm> scoringAlgs = new ArrayList<Algorithm>();
    public static AlgorithmModules instance = null;
    
    public static void init() throws AvatolCVException {
        instance = new AlgorithmModules(AvatolCVFileSystem.getModulesDir());
    }
	//private ScoringAlgorithms scoringAlgorithms = null;
	public AlgorithmModules(String moduleRootDir) throws AvatolCVException {
		File moduleRootFile = new File(moduleRootDir);
		if (!moduleRootFile.exists()){
			throw new AvatolCVException("moduleRootPath " + moduleRootDir + " does not exist.");
		}
		String segmentationAlgPath = moduleRootDir + FILESEP + "segmentation";
		String orientationAlgPath = moduleRootDir + FILESEP + "orientation";
		String scoringAlgPath = moduleRootDir + FILESEP + "scoring";
		String algsSetsPath = moduleRootDir + FILESEP + "algSets";
		
		String propertiesFileName = getPropertiesFilename();
		File segDir = new File(segmentationAlgPath);
		if (segDir.exists()){
			loadAlgs(segDir, propertiesFileName);
		}
		File orientDir = new File(orientationAlgPath);
		if (orientDir.exists()){
			loadAlgs(orientDir,propertiesFileName);
		}
		File scoringDir = new File(scoringAlgPath);
		if (scoringDir.exists()){
			loadAlgs(scoringDir, propertiesFileName);
		}
		/*
		File algsSetsDir = new File(algsSetsPath);
		if (algsSetsDir.exists()){
			loadAlgSets(algsSetsDir);
		}
		*/
	}
	public List<String> getAlgNamesForScoringFocus(ScoringAlgorithm.ScoringSessionFocus focus) throws AvatolCVException {
	    List<String> names = new ArrayList<String>();
        for (Algorithm alg : scoringAlgs){
            ScoringAlgorithm sa = (ScoringAlgorithm)alg;
            if (sa.hasFocus(focus)){
                names.add(alg.getAlgName());
            }
        }
        Collections.sort(names);;
        return names;
    }
	public Algorithm getAlgWithName(String name, List<Algorithm> algs, AlgType type) throws AvatolCVException {
	    for (Algorithm alg : algs){
	        if (alg.getAlgName().equals(name)){
	            return alg;
	        }
	    }
	    throw new AvatolCVException("no algorithm named " + name + " of type " + type + " found.");
	}
	public String getAlgDescription(String name, AlgType type) throws AvatolCVException {
	    Algorithm alg = null;
	    if (type == AlgType.SEGMENTATION){
	        alg = getAlgWithName(name, segAlgs, AlgType.SEGMENTATION);
	        
	    }
	    else if (type == AlgType.ORIENTATION){
	        alg = getAlgWithName(name, orientAlgs, AlgType.ORIENTATION);
	    }
	    else {
	        // must be scoring
	        alg = getAlgWithName(name, scoringAlgs, AlgType.SCORING);
	    }
	    if (null == alg){
	        throw new AvatolCVException("no properties found for alg name " + name);
	    }
	    String description = alg.getAlgDescription();
	    return description;
	}
	/*
	private void loadAlgSets(File parentDir) throws AvatolCVException {
		File[] algSetDirs = parentDir.listFiles();
		for (File f : algSetDirs){
			loadAlgSet(f);
		}
	}
	private void loadAlgSet(File algDirSet) throws AvatolCVException {
		File[] files = algDirSet.listFiles();
		String prefixToMatch = getPropertiesFilenamePrefix();
		for (File f : files){
			String name = f.getName();
			if (name.startsWith(prefixToMatch)){
				// this is an AlgorithmProperties file
				String configFilePath = f.getAbsolutePath();
				List<String> lines = loadProps(configFilePath);
				Algorithm algProps = new Algorithm(lines,configFilePath);
				//String[] nameParts = name.split("\\.");
				//String nameRoot = nameParts[0];
				//String[] nameRootParts = nameRoot.split("_");
				//String algName = nameRootParts[1];
				//algProps.setAlgName(algName);
				String algName = algProps.getAlgName();
				String algTypeString = algProps.getAlgType();
				if (algTypeString.equals(Algorithm.PROPERTY_ALG_TYPE_VALUE_ORIENTATION)){
					propsForNameHashOrientation.put(algName, algProps);

				}
				else if (algTypeString.equals(Algorithm.PROPERTY_ALG_TYPE_VALUE_SCORING)){
					propsForNameHashScoring.put(algName, algProps);
					algNamesScoring.add(algName);
				}
				else {
					// AlgorithmProperties.PROPERTY_ALG_TYPE_VALUE_SEGMENTATION
					propsForNameHashSegmentation.put(algName, algProps);

				}
			}
		}
	}

	*/
	private String getPropertiesFilename(){
		String result = null;
		if (Platform.isWindows()){
			result = "algPropertiesWindows.txt";
		}
		else {
			result ="algPropertiesMac.txt";
		}
		return result;
	}
	private String getPropertiesFilenamePrefix(){
		String result = null;
		if (Platform.isWindows()){
			result = "algPropertiesWindows";
		}
		else {
			result ="algPropertiesMac";
		}
		return result;
	}
	private void loadAlg(File algDir, String propsFilename) throws AvatolCVException {
		String configFilePath = null;
		configFilePath = algDir.getAbsolutePath() + FILESEP + propsFilename;
		File propsFile = new File(configFilePath);
		if (propsFile.exists()){
		    List<String> propStrings = loadProps(configFilePath);
		    String algType = getAlgTypeFromProps(propStrings);
		    if (null == algType){
		        throw new AvatolCVException(configFilePath + " contains no line with key " + Algorithm.PROPERTY_ALG_TYPE);
		    }
		    if (algType.equals(Algorithm.PROPERTY_ALG_TYPE_VALUE_SCORING)){
		        scoringAlgs.add(new ScoringAlgorithm(propStrings,configFilePath));
		    }
		    else if (algType.equals(Algorithm.PROPERTY_ALG_TYPE_VALUE_ORIENTATION)){
		        orientAlgs.add(new OrientationAlgorithm(propStrings,configFilePath));
		    }
		    else if (algType.equals(Algorithm.PROPERTY_ALG_TYPE_VALUE_SEGMENTATION)){
		        segAlgs.add(new SegmentationAlgorithm(propStrings,configFilePath));
		    }
		    else {
		        throw new AvatolCVException(configFilePath + " contains unknown algorithm type " + algType);
		    }
		}
		else {
		    throw new AvatolCVException("no properties file exists in algorithm dir " + algDir.getName());
		}
		
	}
	public static String getAlgTypeFromProps(List<String> strings){
	    for (String s : strings){
	        if (s.startsWith(Algorithm.PROPERTY_ALG_TYPE)){
	            String[] parts = s.split("=");
	            String algType = parts[1];
	            return algType;
	        }
	    }
	    return null;
	}
	private List<String> loadProps(String path) throws AvatolCVException {
	    try {
	        List<String> lines = Files.readAllLines(Paths.get(path), Charset.defaultCharset());
	        return lines;
	    }
	    catch(IOException ioe){
	        throw new AvatolCVException("Problem loading algorithm properties file " + path + " : " + ioe.getMessage(), ioe);
	    }
	}
	private void loadAlgs(File parentDir, String propsFilename) throws AvatolCVException {
		File[] algDirs = parentDir.listFiles();
		for (File f : algDirs){
			if (!(f.getName().equals(".") || f.getName().equals(".."))){
				loadAlg(f, propsFilename);
			}
		}
	}
	public List<String> getSegmentationAlgNames()  throws AvatolCVException{
	    return getNameList(segAlgs);
	}
	public List<String> getNameList(List<Algorithm> algs) throws AvatolCVException {
	    List<String> names = new ArrayList<String>();
	    for (Algorithm alg : algs){
	        names.add(alg.getAlgName());
	    }
	    Collections.sort(names);
	    return names;
	}
	public ScoringAlgorithm getScoringAlgorithm(String name) throws AvatolCVException {
	    for (Algorithm alg : scoringAlgs){
	        if (name.equals(alg.getAlgName())){
	            return (ScoringAlgorithm)alg;
	        }
	    }
	    throw new AvatolCVException("No scoring algorithm found for name " + name);
	}
}
