package edu.oregonstate.eecs.iis.avatolcv.algorithm;

import java.io.File;

import edu.oregonstate.eecs.iis.avatolcv.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.Platform;
import edu.oregonstate.eecs.iis.avatolcv.algorithm.AlgorithmModules.AlgType;

/**
 * 
 * @author admin-jed
 *
 *Class for launching all segmentation, orientation, and scoring algs present in the module system
 */
public class AlgorithmLauncher {
	private static final String FILESEP = System.getProperty("file.separator");
    private static final String NL = System.getProperty("line.separator");
	public static void main(String[] args){
	    if (args.length < 2){
	        usage();
	        System.exit(0);
	    }
	    
	    
	    String algPropertiesPath = args[0];
	    try {
	        AlgorithmProperties algorithmProperties = new AlgorithmProperties(algPropertiesPath);
	    }
	    catch(AvatolCVException ex){
	        System.out.println(ex.getMessage() + NL);
	        System.out.println("problem : " + algPropertiesPath + " is not a valid algProperties file"+ NL);
	        System.exit(0);
	    }
	    String runConfigFilePath = args[1];
	    File f = new File(runConfigFilePath);
	    if (!f.exists()){
	        System.out.println("problem : " + runConfigFilePath + " does not exist"+ NL);
            System.exit(0);
	    }
	    
	    AlgorithmLauncher launcher = new AlgorithmLauncher(algPropertiesPath, runConfigFilePath);
		
	}
	public AlgorithmLauncher(String algPropertiesPath, String runConfigPath){
	    try {
            AlgorithmProperties algorithmProperties = new AlgorithmProperties(algPropertiesPath);
            String launchFile = algorithmProperties.getLaunchFile();
            String algDir = algorithmProperties.getParentDir();
            String commandLine = "";
            if (Platform.isWindows()){
                commandLine = launchFile + " " + runConfigPath;
            }
            else {
                commandLine = "./" + launchFile + " " + runConfigPath;
            }
            
            CommandLineInvoker invoker = new CommandLineInvoker(algDir);
            String stdoutPath = algDir + FILESEP + "stdoutLog.txt";
            invoker.runCommandLine(commandLine, stdoutPath);
	    }
	    catch(AvatolCVException e){
	        e.printStackTrace();
            System.out.println(e.getMessage());
	    }
		
	}
	public static void usage(){
	    System.out.println("usage:" + NL);
        System.out.println("java -jar algLauncher.jar <algPropertiesPath> <runConfigPath>" + NL); 
	}
	/*
	public static String getModulesRootFromAlgPropsPath(String algPropertiesPath) throws AvatolCVException {
	    File f = new File(algPropertiesPath);
	    File parentFile = f.getParentFile();
	    while (!parentFile.getName().equals("modules")){
	        parentFile = parentFile.getParentFile();
	        if (null == parentFile){
	            throw new AvatolCVException("algorithm properties file path " + algPropertiesPath + " is not underneath 'modules' dir");
	        }
	    }
	    return parentFile.getAbsolutePath();
	}
	*/
}
