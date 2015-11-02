package edu.oregonstate.eecs.iis.avatolcv.algorithm;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import edu.oregonstate.eecs.iis.avatolcv.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.Platform;

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
	    String runConfigFilePath = args[1];
	    AlgorithmLauncher launcher = new AlgorithmLauncher(algPropertiesPath, runConfigFilePath);
	}
	public AlgorithmLauncher(String algPropertiesPath, String runConfigPath){
		try {
	        AlgorithmProperties algorithmProperties = new AlgorithmProperties(algPropertiesPath);
	    }
	    catch(AvatolCVException ex){
	        System.out.println(ex.getMessage() + NL);
	        System.out.println("problem : " + algPropertiesPath + " is not a valid algProperties file"+ NL);
	        System.exit(0);
	    }
		File f = new File(runConfigPath);
	    if (!f.exists()){
	        System.out.println("problem : " + runConfigPath + " does not exist"+ NL);
            System.exit(0);
	    }
	    try {
            AlgorithmProperties algorithmProperties = new AlgorithmProperties(algPropertiesPath);
            String launchFile = algorithmProperties.getLaunchFile();
            String algDir = algorithmProperties.getParentDir();
            String launchFilePath = algDir + FILESEP + launchFile;
            /*
             * if (Platform.isWindows()){
    	 fullCommandLine = "\"cd " + this.dirToRunIn + "\", " + " \"&\", " + commandLine ;
     }
     else {
    	 fullCommandLine = "cd " + this.dirToRunIn + ";" + commandLine;
     }
             */
            List<String> commands = new ArrayList<String>();
            if (Platform.isWindows()){
            	commands.add("cmd.exe");
            	commands.add("/C");
            	//commands.add("cd " + algDir);
            	//commands.add("&");
            	commands.add(launchFilePath + " " + runConfigPath);
            }
            else {
            	commands.add("/bin/bash");
            	commands.add("-c");
            	//commands.add("cd " + algDir);
            	//commands.add(" ; ");
            	//commands.add("./" + launchFile + " " + runConfigPath);
            	commands.add(launchFilePath + " " + runConfigPath);
            }
            
            CommandLineInvoker invoker = new CommandLineInvoker(algDir);
            String stdoutPath = algDir + FILESEP + "stdoutLog.txt";
            invoker.runCommandLine(commands, stdoutPath);
	    }
	    catch(AvatolCVException e){
	        e.printStackTrace();
            System.out.println(NL + NL + e.getMessage());
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
