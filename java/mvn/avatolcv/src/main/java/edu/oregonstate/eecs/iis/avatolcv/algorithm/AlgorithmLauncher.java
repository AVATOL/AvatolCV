package edu.oregonstate.eecs.iis.avatolcv.algorithm;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
    private Algorithm algorithm = null;
    private String runConfigPath = null;
    private CommandLineInvoker invoker = null;
	public static void main(String[] args){
	    if (args.length < 2){
	        usage();
	        System.exit(0);
	    }
	    String algPropertiesPath = args[0];
	    String runConfigFilePath = args[1];
	    AlgorithmLauncher launcher = new AlgorithmLauncher(algPropertiesPath, runConfigFilePath);
	    launcher.launch(null);
	}
	public class MyOutputMonitor implements OutputMonitor {

        @Override
        public void acceptOutput(String s) {
           System.out.println(s);
        }
	    
	}
	public void cancel(){
	    invoker.cancel();
	}
	public AlgorithmLauncher(Algorithm algorithm, String runConfigPath){
	    this.algorithm = algorithm;
	    verifyRunConfigPath(runConfigPath);
	}
	public void verifyRunConfigPath(String path){
	    File f = new File(path);
        if (!f.exists()){
            System.out.println("problem : " + path + " does not exist"+ NL);
            System.exit(0);
        }
        this.runConfigPath = path;
	}
	public AlgorithmLauncher(String algPropertiesPath, String runConfigPath){
		try {
		    Path path = Paths.get(algPropertiesPath);
		    List<String> lines = Files.readAllLines(path, Charset.defaultCharset());
	        this.algorithm = new Algorithm(lines,algPropertiesPath);
	    }
	    catch(Exception ex){
	        System.out.println(ex.getMessage() + NL);
	        System.out.println("problem : " + algPropertiesPath + " is not a valid algProperties file"+ NL);
	        System.exit(0);
	    }
		 verifyRunConfigPath(runConfigPath);
	}
	public boolean isProcessRunning(){
	    if (null == this.invoker){
	        return true; // hasn't yet started
	    }
	    return this.invoker.isProcessRunning();
	}
	public void launch(OutputMonitor outputMonitor){
	    if (null == outputMonitor){
	        outputMonitor = new MyOutputMonitor();
	    }
	    try {
	        
            String launchFile = this.algorithm.getLaunchFile();
            String algDir = this.algorithm.getParentDir();
            String launchFilePath = algDir + FILESEP + launchFile;


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
            
            this.invoker = new CommandLineInvoker();
            String stdoutPath = algDir + FILESEP + "stdoutLog.txt";
            //this.invoker.runCommandLine(commands, stdoutPath);
            this.invoker.runCommandLine(commands, outputMonitor);
	    }
	   
	    catch(Exception e){
	        e.printStackTrace();
            System.out.println(NL + NL + e.getMessage());
	    }
		
	}
	public static void usage(){
	    System.out.println("usage:" + NL);
        System.out.println("java -jar algLauncher.jar <algPropertiesPath> <runConfigPath>" + NL); 
	}
}
