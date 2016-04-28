package edu.oregonstate.eecs.iis.avatolcv.algorithm;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.oregonstate.eecs.iis.avatolcv.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.AvatolCVFileSystem;
import edu.oregonstate.eecs.iis.avatolcv.Platform;
import edu.oregonstate.eecs.iis.avatolcv.util.ClassicSplitter;

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
	private List<Algorithm> segAlgs = new ArrayList<Algorithm>();
    private List<Algorithm> orientAlgs = new ArrayList<Algorithm>();
    private List<Algorithm> scoringAlgs = new ArrayList<Algorithm>();
    private static final Logger logger = LogManager.getLogger(AlgorithmModules.class);
    public static AlgorithmModules instance = null;
    
    public static void init() throws AvatolCVException {
        instance = new AlgorithmModules(AvatolCVFileSystem.getModulesDir());
    }
	public AlgorithmModules(String moduleRootDir) throws AvatolCVException {
		File moduleRootFile = new File(moduleRootDir);
		if (!moduleRootFile.exists()){
			throw new AvatolCVException("moduleRootPath " + moduleRootDir + " does not exist.");
		}
		String segmentationAlgPath = moduleRootDir + FILESEP + AlgType.SEGMENTATION.toString().toLowerCase();
		String orientationAlgPath = moduleRootDir + FILESEP + AlgType.ORIENTATION.toString().toLowerCase();
		String scoringAlgPath = moduleRootDir + FILESEP + AlgType.SCORING.toString().toLowerCase();
		
		String propertiesFileName = getPropertiesFileRootName();
		File segDir = new File(segmentationAlgPath);
		if (segDir.exists()){
			loadAlgsForCategory(segDir, propertiesFileName);
		}
		File orientDir = new File(orientationAlgPath);
		if (orientDir.exists()){
			loadAlgsForCategory(orientDir,propertiesFileName);
		}
		File scoringDir = new File(scoringAlgPath);
		if (scoringDir.exists()){
			loadAlgsForCategory(scoringDir, propertiesFileName);
		}
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
	public Algorithm getAlgWithName(String name, AlgType type) throws AvatolCVException {
	    if (type == AlgorithmModules.AlgType.SEGMENTATION){
	        return getAlgWithName(name, this.segAlgs, type);
	    }
	    else if (type == AlgorithmModules.AlgType.ORIENTATION){
	        return getAlgWithName(name, this.orientAlgs, type);
        }
	    else {
	        // type == AlgorithmModules.AlgType.SCORING 
	        return getAlgWithName(name, this.scoringAlgs, type);
	    }
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
	
	
	private String getPropertiesFileRootName(){
        String result = null;
        if (Platform.isWindows()){
            result = "algPropertiesWindows";
        }
        else {
            result ="algPropertiesMac";
        }
        return result;
    }
	private void loadAlgsFromDir(File algDir, String propsFileRootName) throws AvatolCVException {
	    File[] files = algDir.listFiles();
	    for (File f : files){
	        if (f.getName().startsWith(propsFileRootName)){
	            loadAlg(algDir, f.getName());
	        }
	    }
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
		    logger.info("no properties file exists in algorithm dir " + algDir.getName() + " for this platform.");
		}
		
	}
	public static String getAlgTypeFromProps(List<String> strings){
	    for (String s : strings){
	        if (s.startsWith(Algorithm.PROPERTY_ALG_TYPE)){
	            String[] parts = ClassicSplitter.splitt(s,'=');
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
	private void loadAlgsForCategory(File parentDir, String propsFileRootName) throws AvatolCVException {
		File[] algDirs = parentDir.listFiles();
		for (File f : algDirs){
			if (!(f.getName().equals(".") || f.getName().equals("..") || f.isFile())){
			    loadAlgsFromDir(f, propsFileRootName);
			}
		}
	}
	public List<String> getSegmentationAlgNames()  throws AvatolCVException{
	    return getNameList(segAlgs);
	}
    public List<String> getOrientationAlgNames()  throws AvatolCVException{
        return getNameList(orientAlgs);
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
