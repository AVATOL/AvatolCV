package edu.oregonstate.eecs.iis.avatolcv.core;

import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import edu.oregonstate.eecs.iis.avatolcv.Platform;
import edu.oregonstate.eecs.iis.avatolcv.core.ScoringAlgorithms.ScoringSessionFocus;

public class AlgorithmModules {
	private enum AlgType {
		SEGMENTATION,
		ORIENTATION,
		SCORING
	}
	private static final String FILESEP = System.getProperty("file.separator");
	private Hashtable<String, AlgorithmProperties> propsForNameHashSegmentation = new Hashtable<String, AlgorithmProperties>();
	private Hashtable<String, AlgorithmProperties> propsForNameHashOrientation = new Hashtable<String, AlgorithmProperties>();
	private Hashtable<String, AlgorithmProperties> propsForNameHashScoring = new Hashtable<String, AlgorithmProperties>();
	private List<String> algNamesSegmentation = new ArrayList<String>();
	private List<String> algNamesOrientation = new ArrayList<String>();
	private List<String> algNamesScoring = new ArrayList<String>();

	private ScoringAlgorithms scoringAlgorithms = null;
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
			loadAlgs(segDir, AlgType.SEGMENTATION,propertiesFileName, propsForNameHashSegmentation, algNamesSegmentation);
		}
		File orientDir = new File(orientationAlgPath);
		if (orientDir.exists()){
			loadAlgs(orientDir, AlgType.ORIENTATION,propertiesFileName, propsForNameHashOrientation, algNamesOrientation);
		}
		File scoringDir = new File(scoringAlgPath);
		if (scoringDir.exists()){
			loadAlgs(scoringDir, AlgType.SCORING, propertiesFileName, propsForNameHashScoring, algNamesScoring);
		}
		File algsSetsDir = new File(algsSetsPath);
		if (algsSetsDir.exists()){
			loadAlgSets(algsSetsDir);
		}
		this.scoringAlgorithms = new ScoringAlgorithms();
		configureScoringAlgorithms();
	}
	
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
				AlgorithmProperties algProps = new AlgorithmProperties(configFilePath);
				String[] nameParts = name.split("\\.");
				String nameRoot = nameParts[0];
				String[] nameRootParts = nameRoot.split("_");
				String algName = nameRootParts[1];
				algProps.setAlgName(algName);

				String algTypeString = algProps.getAlgType();
				if (algTypeString.equals(AlgorithmProperties.PROPERTY_ALG_TYPE_VALUE_ORIENTATION)){
					propsForNameHashOrientation.put(algName, algProps);

				}
				else if (algTypeString.equals(AlgorithmProperties.PROPERTY_ALG_TYPE_VALUE_SCORING)){
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
	public ScoringAlgorithms getScoringAlgorithms(){
		return this.scoringAlgorithms;
	}
	private void configureScoringAlgorithms() throws AvatolCVException {
		 for (String name : algNamesScoring){
			   	AlgorithmProperties ap = propsForNameHashScoring.get(name);
	        	// presence of scope and focus strings
	        	ScoringAlgorithms.ScoringSessionFocus focus = getFocusFromProperties(name, ap);
	        	ScoringAlgorithms.ScoringScope scope = getScopeFromProperties(name, ap);
	        	String algLaunchString = getLaunchStringFromProperties(name, ap);
	        	ScoringAlgorithms.LaunchThrough launchThrough = getLaunchThroughValueFromProperties(name, ap);
	        	this.scoringAlgorithms.addAlgorithm(name, focus, scope, launchThrough, algLaunchString, true, ap.getParentDir());
	      }
	}
	private ScoringAlgorithms.LaunchThrough getLaunchThroughValueFromProperties(String name, AlgorithmProperties ap) throws AvatolCVException {
		ScoringAlgorithms.LaunchThrough launchThrough = null;
		if (ap.getLaunchFileLanguage().equals(AlgorithmProperties.PROPERTY_LAUNCH_FILE_LANGUAGE_MATLAB)){
    		launchThrough = ScoringAlgorithms.LaunchThrough.MATLAB;
    	}
    	else {
    		launchThrough = ScoringAlgorithms.LaunchThrough.OTHER;
    	}
		return launchThrough;
	}
	private String getLaunchStringFromProperties(String name, AlgorithmProperties ap) throws AvatolCVException {
		String algLaunchString = null;
		try {
			algLaunchString = ap.getLaunchFile();
		}
		catch(Exception e){
			throw new AvatolCVException("Scoring algorithm " + name + " needs " + AlgorithmProperties.PROPERTY_LAUNCH_FILE + " property defined as per README");
		}
		return algLaunchString;
	}
	private ScoringAlgorithms.ScoringScope getScopeFromProperties(String name, AlgorithmProperties ap) throws AvatolCVException {
    	String scoringScopeString = null;
    	try {
    		scoringScopeString = ap.getProperty("scoringScope");
    	}
    	catch(Exception e){
    		throw new AvatolCVException("Scoring algorithm " + name + " needs scoringScope property defined as per README");
    	}
    	ScoringAlgorithms.ScoringScope scope = null;
    	try {
    	    scope = ScoringAlgorithms.ScoringScope.valueOf(scoringScopeString);
    	}
    	catch(Exception e){
    		throw new AvatolCVException("invalid scoringScope value for scoring algorithm " + name + ": " + scoringScopeString);
    	}
    	return scope;
	}
	private ScoringAlgorithms.ScoringSessionFocus getFocusFromProperties(String name, AlgorithmProperties ap) throws AvatolCVException {
    	String scoringFocusString = null;
    	try {
    		scoringFocusString = ap.getProperty("scoringFocus");
    	}
    	catch(Exception e){
    		throw new AvatolCVException("Scoring algorithm " + name + " needs scoringFocus property defined as per README");
    	}
    	ScoringAlgorithms.ScoringSessionFocus focus = null;
    	try {
    	    focus = ScoringAlgorithms.ScoringSessionFocus.valueOf(scoringFocusString);
    	}
    	catch(Exception e){
    		throw new AvatolCVException("invalid scoringFocus value for scoring algorithm " + name + ": " + scoringFocusString);
    	}
    	return focus;
	}
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
	private String loadAlg(File algDir, AlgType algType, String propsFilename, Hashtable<String, AlgorithmProperties> propsForNameHash) throws AvatolCVException {
		String algName = algDir.getName();
		String configFilePath = null;
		configFilePath = algDir.getAbsolutePath() + FILESEP + propsFilename;
		
		
		File propsFile = new File(configFilePath);
		if (!propsFile.exists()){
			throw new AvatolCVException("no properties file exists for algorithm " + algName + " of type " + algType);
		}
		AlgorithmProperties algProps = new AlgorithmProperties(propsFile.getAbsolutePath());
		propsForNameHash.put(algName, algProps);
		algProps.setAlgName(algName);
		return algName;
	}
	private void loadAlgs(File parentDir, AlgType algType, String propsFilename, Hashtable<String, AlgorithmProperties> propsForNameHash, List<String> algNames) throws AvatolCVException {
		File[] algDirs = parentDir.listFiles();
		for (File f : algDirs){
			if (!(f.getName().equals(".") || f.getName().equals(".."))){
				String algName = loadAlg(f, algType, propsFilename, propsForNameHash);
				algNames.add(algName);
			}
		}
	}
}
