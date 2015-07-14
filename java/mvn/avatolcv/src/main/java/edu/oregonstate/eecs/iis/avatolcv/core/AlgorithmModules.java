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
		String segmentationAlgDir = moduleRootDir + FILESEP + "segmentation";
		String orientationAlgDir = moduleRootDir + FILESEP + "orientation";
		String scoringAlgDir = moduleRootDir + FILESEP + "scoring";
		
		String propertiesFileName = getPropertiesFilename();
		File segFile = new File(segmentationAlgDir);
		if (segFile.exists()){
			loadAlgs(segFile, AlgType.SEGMENTATION,propertiesFileName, propsForNameHashSegmentation, algNamesSegmentation);
		}
		File orientFile = new File(orientationAlgDir);
		if (orientFile.exists()){
			loadAlgs(orientFile, AlgType.ORIENTATION,propertiesFileName, propsForNameHashOrientation, algNamesOrientation);
		}
		File scoringFile = new File(scoringAlgDir);
		if (scoringFile.exists()){
			loadAlgs(scoringFile, AlgType.SCORING, propertiesFileName, propsForNameHashScoring, algNamesScoring);
		}
		configureScoringAlgorithms();
		this.scoringAlgorithms = new ScoringAlgorithms();
	}
	private void configureScoringAlgorithms(){
		 for (String name : algNamesScoring){
	        	AlgorithmProperties ap = propsForNameHashScoring.get(name);
	        	// presence of scope and focus strings
	        	String scoringScopeString = null;
	        	String scoringFocusString = null;
	        	try {
	        		scoringScopeString = ap.getProperty("scoringScope");
	        		scoringFocusString = ap.getProperty("scoringFocus");
	        	}
	        	catch(Exception e){
	        		throw new AvatolCVException("Scoring algorithm " + name + " needs both scoringScope and scoringFocus properties defined as per README");
	        	}
	        	// correctness of scoring and focus strings
	        	ScoringAlgorithms.ScoringSessionFocus focus = null;
	        	ScoringAlgorithms.ScoringScope scope = null;
	        	try {
	        	    focus = ScoringAlgorithms.ScoringSessionFocus.valueOf(scoringFocusString);
	        	}
	        	catch(Exception e){
	        		throw new AvatolCVException("invalid scoringFocus value for scoring algorithm " + name + ": " + scoringFocusString);
	        	}
	        	try {
	        	    scope = ScoringAlgorithms.ScoringScope.valueOf(scoringScopeString);
	        	}
	        	catch(Exception e){
	        		throw new AvatolCVException("invalid scoringScope value for scoring algorithm " + name + ": " + scoringScopeString);
	        	}
	        	// launch string
	        	String algLaunchString = ap.getLaunchFile();
	        	ScoringAlgorithms.LaunchThrough launchThrough = null;
	        	if (algLaunchString.endsWith(".m")){
	        		launchThrough = ScoringAlgorithms.LaunchThrough.MATLAB;
	        		algLaunchString 
	        		LEFT OFF HERE - need to decide how to handle matlab - I'm just calling the matlab function, not the filename so I need to say "if matlab, give function name"
	        		but, how/when does matlab get informed of the paths it needs to find everything?  Could dig it all out myself? How did it work at deploy time? check Mac!
	        	}
	        	else {
	        		launchThrough = ScoringAlgorithms.LaunchThrough.OTHER;
	        	}
	        	this.scoringAlgorithms.addAlgorithm(name, focus, scope, launchThrough, , true);
	        }
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
