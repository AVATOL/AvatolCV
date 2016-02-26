package edu.oregonstate.eecs.iis.avatolcv.algorithm;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;

import edu.oregonstate.eecs.iis.avatolcv.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.AvatolCVFileSystem;
import edu.oregonstate.eecs.iis.avatolcv.normalized.NormalizedImageInfo;
import edu.oregonstate.eecs.iis.avatolcv.results.OutputImageSorter;
import edu.oregonstate.eecs.iis.avatolcv.scoring.ModalImageInfo;
import edu.oregonstate.eecs.iis.avatolcv.util.ClassicSplitter;

/*
 *  segmentationOutputDir=<path of dir where output goes>
    avatolCVStatusFile=<path to file to write status to> (avatolCV will poll that file)
    
AvatolCV generates this line and the associated files due to inputRequired line in algProperties file:
    testImagesFile=<someAbsolutePath>/testImagesFile.txt
    
AvatolCV generates this line and the associated files entries(if any) due to inputOptional line in algProperties file:
    userProvidedGroundTruthImagesFile=<someAbsolutePath>/userProvidedGroundTruthImagesFile.txt
    userProvidedTrainImagesFile=<someAbsolutePath>/userProvidedTrainImagesFile.txt   
 */
public class RunConfigFile {
    private static final String FILESEP = System.getProperty("file.separator");
    private static final String NL = System.getProperty("line.separator");
    private String pathOfSessionInputFiles = null;
    private String pathOfUserProvidedFiles = null;
    List<String> dependencyEntries = new ArrayList<String>();
    List<String> inputRequiredEntries = new ArrayList<String>();
    List<String> inputOptionalEntries = new ArrayList<String>();
    private String path = null;
    private Algorithm alg = null;
    private AlgorithmSequence algSequence = null;
    private String algStatusPath = null;
    private List<ModalImageInfo> scoringImages = null;
    private List<String> inputImageIDs = new ArrayList<String>();
    private OutputImageSorter sorter = null;
    private boolean sorterLoaded = false;
    
    public RunConfigFile(Algorithm alg, AlgorithmSequence algSequence, List<ModalImageInfo> scoringImages) throws AvatolCVException {
        this.alg = alg;
        this.algSequence = algSequence;
        this.scoringImages = scoringImages;
        this.pathOfSessionInputFiles = algSequence.getInputDir();
        System.out.println("RunConfigFile sets pathOfSessionInputFiles as " + this.pathOfSessionInputFiles);
        handleDependencies(alg);
        handleOptionalInputs(alg);
        
       
        handleRequiredInputs(alg, scoringImages);
        
        File f = new File(this.pathOfSessionInputFiles);
        if (!f.exists()){
            throw new AvatolCVException("pathOfSessionInputFiles does not exist " + pathOfSessionInputFiles);
        }
        this.pathOfUserProvidedFiles = algSequence.getSupplementalInputDir();
        persist();
    }
    private List<String> getPathnamesFromFile(String path) throws AvatolCVException {
    	List<String> pathnames = new ArrayList<String>();
    	try {
        	BufferedReader reader = new BufferedReader(new FileReader(path));
        	String line = null;
        	while (null != (line = reader.readLine())){
        		pathnames.add(line);
        	}
        	reader.close();
        	return pathnames;
        }
        catch(IOException ioe){
        	throw new AvatolCVException("problem reading pathnames from file requiredInputs file " + path);
        }
    }
    
    public void loadAndSortInputandOutputImagePaths() throws AvatolCVException {
    	sorter = new OutputImageSorter();
    	List<AlgorithmInputRequired> airs = alg.getRequiredInputs();
        for (AlgorithmInputRequired air : airs){
        	String suffix = air.getSuffix();
            String path = getFileListPathnameForKey(air.getKey(), this.algSequence);
            List<String> pathnames = getPathnamesFromFile(path);
            sorter.addInputSuffixAndPaths(suffix, pathnames);
        }
        List<AlgorithmOutput> aos = alg.getOutputs();
        String outputDir = this.algSequence.getOutputDir();
        for (AlgorithmOutput ao : aos){
        	String suffix = ao.getSuffix();
            List<String> pathnames = getPathnamesFromDirWithSuffix(outputDir, suffix);
            sorter.addOutputSuffixAndPaths(suffix, pathnames);
        }
        sorter.sort();
    }
    public List<String> getPathnamesFromDirWithSuffix(String dir, String suffix) throws AvatolCVException {
    	List<String> result = new ArrayList<String>();
    	File f = new File(dir);
    	if (!f.isDirectory()){
    		throw new AvatolCVException("given algorithm output directory does not exist: " + dir);
    	}
    	File[] files = f.listFiles();
    	for (File file : files){
    		String name = file.getName();
    		String[] parts = ClassicSplitter.splitt(name, '.');
    		String namePart = parts[0];
    		String[] fragments = ClassicSplitter.splitt(namePart, '_');
    		int count = fragments.length;
    		String suffixCandidate = fragments[count -1];
    		if (suffix.startsWith("_")){
    			suffixCandidate = "_" + suffixCandidate;
    		}
    		if (suffixCandidate.equals(suffix)){
    			result.add(file.getAbsolutePath());
    		}
    	}
    	return result;
    }
    public String getAlgorithmStatusPath(){
        return this.algStatusPath;
    }
    
    private void persist() throws AvatolCVException {
    	String algType = this.alg.getAlgType();
    	//String sessionDir = AvatolCVFileSystem.getSessionDir();
    	this.path = AvatolCVFileSystem.getSessionDir() + FILESEP + "runConfig_" + algType + ".txt";
    	
    	try {
    		BufferedWriter writer = new BufferedWriter(new FileWriter(this.path));
    		String outputDirKey = this.alg.getAlgType() + "OutputDir";
    		String outputDirPath = this.algSequence.getOutputDir();
    		File outputDirFile = new File(outputDirPath);
    		outputDirFile.mkdirs();
    		writer.write(outputDirKey + "=" + outputDirPath + NL);
    		String algTypeScoringName = AlgorithmModules.AlgType.SCORING.name().toLowerCase();
    		if (algType.equals(algTypeScoringName)){
    		    writer.write("trainingDataDir=" + AvatolCVFileSystem.getTrainingDataDirForScoring() + NL);
    		}
    		for (String dependency : dependencyEntries){
    			writer.write(dependency + NL);
    		}
    		for (String required : inputRequiredEntries){
    			writer.write(required + NL);
    		}
    		for (String optional : inputOptionalEntries){
                writer.write(optional + NL);
            }
    		writer.close();
    	}
    	catch(IOException ioe){
    		throw new AvatolCVException("problem writing runConfig file " + this.path);
    	}
    }
    public String getRunConfigPath(){
        return this.path;
    }
    public static String getFileListPathnameForKey(String key, AlgorithmSequence algSequence) throws AvatolCVException {
        return AvatolCVFileSystem.getSessionDir() + FILESEP + key + "_" + algSequence.getCurrentStage() + ".txt";
    }
  //  public static String getFileListPathnameForKeyForExternallyProvidedData(String key, AlgorithmSequence algSequence) throws AvatolCVException {
 //       return AvatolCVFileSystem.getSessionDir() + FILESEP + key + "_" + algSequence.getCurrentStage() + ".txt";
  //  }
    //
    // dependencies
    //
    public void handleDependencies(Algorithm alg){
        List<AlgorithmDependency> dependencies = alg.getDependencies();
        for (AlgorithmDependency ad : dependencies){
            dependencyEntries.add(generateEntryForDependency(ad));
        }
    }
    public static String generateEntryForDependency(AlgorithmDependency ad){
        return ad.getKey() + "=" + ad.getPath();
    }
    
    //
    // required inputs
    //
   
    public void handleRequiredInputs(Algorithm alg, List<ModalImageInfo> scoringList) throws AvatolCVException {
        String algType = alg.getAlgType();
        System.out.println("algType chosen is " + algType);
        List<AlgorithmInputRequired> requiredInputs = alg.getRequiredInputs();
        // first create the entries for the file
        for (AlgorithmInputRequired air : requiredInputs){
            inputRequiredEntries.add(generateEntryForRequiredInput(air, this.algSequence));
        }
      
        Hashtable<AlgorithmInput, List<String>> pathListHash = new Hashtable<AlgorithmInput, List<String>>();
        
        // make a list of the base class type for sorting
        List<AlgorithmInput> inputs = new ArrayList<AlgorithmInput>();
        inputs.addAll(requiredInputs);
        verifyUniqueSuffixes(inputs);
        
        File dir = new File(this.pathOfSessionInputFiles);
        File[] files = dir.listFiles();
        List<String> allPathsFromDir = new ArrayList<String>();
        for (File f : files){
        	if (!f.getName().startsWith(".")){
        		allPathsFromDir.add(f.getAbsolutePath());
        	}
        }
        
        suffixFileSort(inputs, pathListHash, allPathsFromDir, this.pathOfSessionInputFiles);
        
        /*
         * FOREACH inputRequiredForTest, need to have the following happen:
			from the dir that is output from previous stage
			they need to match the id of the files in the scoringList
			they need to match the iRFT suffix
			AND they need to match the TYPE of output from prior stage (if there was a prior stage)  We can 
			ignore this for now as out limited implementation won't experience malfunction because of this
         * now filter on what images are actually in play for this scoring run
         */
        if (null!= scoringList){
        	for (AlgorithmInput air : requiredInputs){
            	List<String> desiredFiles = new ArrayList<String>();
            	List<String> candidatesWithMatchedSuffix = pathListHash.get(air);
            	int count = 0;
            	for (ModalImageInfo mii : scoringList){
                	String imageID = mii.getNormalizedImageInfo().getImageID();
                	String pathWithMatchingImageID = getPathWithMatchingImageID(candidatesWithMatchedSuffix, imageID);
                	if (null == pathWithMatchingImageID){
                		System.out.println("WARNING - Cannot find file to score with imageID " + imageID + " and reqiured suffix " + air.getSuffix() + " in "  + this.pathOfSessionInputFiles);
                	}
                	else {
                		desiredFiles.add(pathWithMatchingImageID);
                	}
                }
            	// replace the candidate list with the desired list
            	pathListHash.put(air,desiredFiles);
            }
        }
        
        
        // generate a file list for each requiredInput
        for (AlgorithmInput air : requiredInputs){
            String path = getFileListPathnameForKey(air.getKey(), this.algSequence);
            generateFileList(path, pathListHash.get(air));
        }
    }
    public static String getPathWithMatchingImageID(List<String> paths, String imageID){
    	for (String path : paths){
    		String thisID = NormalizedImageInfo.getImageIDFromPath(path);
    		if (thisID.equals(imageID)){
    			return path;
    		}
    	}
    	return null;
    }
    public static void verifyUniqueSuffixes(List<AlgorithmInput> inputs) throws AvatolCVException {
        List<String> suffixList = new ArrayList<String>();
        for (AlgorithmInput ai : inputs){
            if (suffixList.contains(ai.getSuffix())){
                throw new AvatolCVException("two input types have the same suffix.  They must be unique");
            }
            else {
                suffixList.add(ai.getSuffix());
            }
        }
    }
    
    public static void suffixFileSort(List<AlgorithmInput> inputs,  Hashtable<AlgorithmInput, List<String>> pathListHash, List<String> allPathsFromDir, String pathOfInputFiles) throws AvatolCVException {
        System.out.println("suffixFileSort called with pathOfInputFiles as " + pathOfInputFiles);
        if (allPathsFromDir.isEmpty()){
            throw new AvatolCVException("No input data present at " + pathOfInputFiles);
        }
        // next divide the required inputs into two lists, those with suffixes and those without
        List<AlgorithmInput> withSuffixList = new ArrayList<AlgorithmInput>();
        List<AlgorithmInput> withoutSuffixList = new ArrayList<AlgorithmInput>();
        for (AlgorithmInput air : inputs){
            if (air.hasSuffix()){
                withSuffixList.add(air);
            }
            else {
                withoutSuffixList.add(air);
            }
        }
        // if more than one lacks a suffix, throw an exception as there is no way to tell which are which in the one dir
        if (withoutSuffixList.size() > 1){
            throw new AvatolCVException("More than one input has no identifying suffix.  Since all input files come from the same directory - no way to tell these sets apart at " + pathOfInputFiles);
        }
        // make a list of pathnames for each requiredInput
        
        for (AlgorithmInput air : inputs){
            List<String> listOfPathnames = new ArrayList<String>();
            pathListHash.put(air, listOfPathnames);
        }
        
        // go through the path list, put each file into the correct bin for each suffix 
        
        // first pull out for each one that matches suffix
        for (AlgorithmInput air : withSuffixList){
            List<String> pathListForInput = pathListHash.get(air);
            
            for (String path : allPathsFromDir){
            	String suffix = air.getSuffix();
                if (pathHasSuffix(path,suffix)){
                	pathListForInput.add(path);
                }
            }
            Collections.sort(pathListForInput);
            for (String s : pathListForInput){
                if (allPathsFromDir.contains(s)){
                    allPathsFromDir.remove(s);
                }
            }
        }
        // look for case where no matches for desired suffix were found
        for (AlgorithmInput air : withSuffixList){
            List<String> pathListForInput = pathListHash.get(air);
            if (pathListForInput.size() == 0){
                throw new AvatolCVException("No files with required suffix " + air.getSuffix() + " found at " + pathOfInputFiles);
            }
        }
        // put the rest in the list for no suffix
        if (!withoutSuffixList.isEmpty()){
            List<String> noSuffixPathList = pathListHash.get(withoutSuffixList.get(0));
            for (String path : allPathsFromDir){
                noSuffixPathList.add(path);
            }
            Collections.sort(noSuffixPathList);
            // look for case where all the input files matched specified suffixes, leaving none to match the "no suffix" * declaration
            if (noSuffixPathList.isEmpty()){
            	throw new AvatolCVException("all the input files matched specified suffixes, leaving none to match the 'no suffix' * declaration at " + pathOfInputFiles);
            }
        }
    }
    public static boolean pathHasSuffix(String path, String suffix){
        String[] parts = ClassicSplitter.splitt(path,'.');
        String fileDescriptor = parts[parts.length - 1];
        String root = path.replace("." + fileDescriptor, "");
        return root.endsWith(suffix);
    }
    public static String generateEntryForRequiredInput(AlgorithmInputRequired air, AlgorithmSequence algSequence) throws AvatolCVException {
        String key = air.getKey();
        return key + "=" + getFileListPathnameForKey(key, algSequence);
    }
   
    //
    // optional inputs
    //
    // for now, just generate empty file lists so this info can flow through the interface, but won't have effect
   
    public void handleOptionalInputs(Algorithm alg) throws AvatolCVException {
        String algType = alg.getAlgType();
        List<AlgorithmInputOptional> optionalInputs = alg.getOptionalInputs();
        // first create the entries for the file
        for (AlgorithmInputOptional air : optionalInputs){
            inputOptionalEntries.add(generateEntryForOptionalInput(air, this.algSequence));
        }
      
        Hashtable<AlgorithmInput, List<String>> pathListHash = new Hashtable<AlgorithmInput, List<String>>();
        
        // make a list of the base class type for sorting
        List<AlgorithmInput> inputs = new ArrayList<AlgorithmInput>();
        inputs.addAll(optionalInputs);
        verifyUniqueSuffixes(inputs);
        
        File dir = new File(algSequence.getSupplementalInputDir());
        File[] files = dir.listFiles();
        List<String> allPathsFromDir = new ArrayList<String>();
        if (files.length > 0){
            for (File f : files){
            	if (f.getName().startsWith(".")){
            		allPathsFromDir.add(f.getAbsolutePath());
            	}
            }
        }
        
        if (!allPathsFromDir.isEmpty()){
            suffixFileSort(inputs, pathListHash, allPathsFromDir, algSequence.getSupplementalInputDir());
        }
        
        // generate a file list for each requiredInput
        for (AlgorithmInput air : optionalInputs){
            String path = getFileListPathnameForKey(air.getKey(), this.algSequence);
            generateFileList(path, pathListHash.get(air));
        }
    }
  
    

    public static String generateEntryForOptionalInput(AlgorithmInputOptional aio, AlgorithmSequence algSequence) throws AvatolCVException {
        String key = aio.getKey();
        return key + "=" + getFileListPathnameForKey(key, algSequence);
    }
   
  
    public void generateFileList(String pathOfFileToCreate, List<String> paths) throws AvatolCVException {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(pathOfFileToCreate));
            if (null != paths){
                if (!paths.isEmpty()){
                    for (String path : paths){
                        writer.write(path + NL);
                    }
                }
            }
            writer.close();
        }
        catch(IOException ioe){
            throw new AvatolCVException("problem creating runConfigFile at " + pathOfFileToCreate);
        }
        
    }
    private List<String> getImageIdsFromFile(String path) throws AvatolCVException {
        List<String> imageIDs = new ArrayList<String>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(path));
            String line = null;
            while (null != (line = reader.readLine())){
            	File f = new File(line);
            	String filename = f.getName();
                String[] parts = ClassicSplitter.splitt(filename,  '_');
                String imageID = parts[0];
                if (!imageIDs.contains(imageID)){
                    imageIDs.add(imageID);
                }
            }
            reader.close();
            return imageIDs;
        }
        catch(IOException ioe){
            throw new AvatolCVException("problem loading path " + path + " to extract imageIDs");
        }
    }
    public List<String> getInputImageIDs() throws AvatolCVException {
        List<AlgorithmInputRequired> airs = alg.getRequiredInputs();
        List<String> imageIDs = new ArrayList<String>();
        for (AlgorithmInputRequired air : airs){
            String path = getFileListPathnameForKey(air.getKey(), this.algSequence);
            List<String> imageIDsFromFile = getImageIdsFromFile(path);
            for (String imageID : imageIDsFromFile){
                if (!imageIDs.contains(imageID)){
                    imageIDs.add(imageID);
                }
            }
        }
        return imageIDs;
    }
    public List<String> getInputImagePathnamesForImageID(String imageID) throws AvatolCVException {
    	if (!sorterLoaded){
    		loadAndSortInputandOutputImagePaths();
    		sorterLoaded = true;
    	}
        return sorter.getInputPathsForImageID(imageID);
    }
    public List<String> getOutputImagePathnamesForImageID(String imageID) throws AvatolCVException{
    	if (!sorterLoaded){
    		loadAndSortInputandOutputImagePaths();
    		sorterLoaded = true;
    	}
    	return sorter.getOutputPathsForImageID(imageID);
    }
    public List<String> getInputSuffixList()  throws AvatolCVException{
    	if (!sorterLoaded){
    		loadAndSortInputandOutputImagePaths();
    		sorterLoaded = true;
    	}
    	return sorter.getInputSuffixList();
    }
    public List<String> getOutputSuffixList()  throws AvatolCVException{
    	if (!sorterLoaded){
    		loadAndSortInputandOutputImagePaths();
    		sorterLoaded = true;
    	}
    	return sorter.getOutputSuffixList();
    }
    //public static List<String> getListOfImageIDs(List<String> inputFileListPathnames){
     //   List<String> result = new ArrayList<String>();
    //    return result;
    //}
}
