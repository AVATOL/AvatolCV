package edu.oregonstate.eecs.iis.avatolcv.algorithm;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
    private static final Logger logger = LogManager.getLogger(AlgorithmLauncher.class);
	public static void main(String[] args){
	    if (args.length < 2){
	        usage();
	        System.exit(0);
	    }
	    String algPropertiesPath = args[0];
	    String runConfigFilePath = args[1];
	    AlgorithmLauncher launcher = null;
	    try {
	        launcher = new AlgorithmLauncher(algPropertiesPath, runConfigFilePath);
	    }
	    catch(AvatolCVException ace){
	        ace.printStackTrace();
	        logger.info(ace.getMessage());
	        System.exit(1);
	    }
	    launcher.launch(null);
	}
	public class MyOutputMonitor implements OutputMonitor {

        @Override
        public void acceptOutput(String s) {
           logger.info(s);
        }
	    
	}
	public void cancel(){
	    invoker.cancel();
	}
	public AlgorithmLauncher(Algorithm algorithm, String runConfigPath, boolean verifyRunConfigPath) throws AvatolCVException {
	    this.algorithm = algorithm;
	    if (verifyRunConfigPath){
	        verifyRunConfigPath(runConfigPath);
	    }
	}
	public void verifyRunConfigPath(String path) throws AvatolCVException{
	    File f = new File(path);
        if (!f.exists()){
            throw new AvatolCVException("problem : " + path + " does not exist"+ NL);
        }
        this.runConfigPath = path;
	}
	public AlgorithmLauncher(String algPropertiesPath, String runConfigPath) throws AvatolCVException {
		try {
		    Path path = Paths.get(algPropertiesPath);
		    List<String> lines = Files.readAllLines(path, Charset.defaultCharset());
	        this.algorithm = new Algorithm(lines,algPropertiesPath);
	    }
	    catch(Exception ex){
	        logger.info(ex.getMessage() + NL);
	        logger.info("problem : " + algPropertiesPath + " is not a valid algProperties file"+ NL);
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
            logger.info("launching algorithm with " + launchFilePath);

            List<String> commands = new ArrayList<String>();
            if (Platform.isWindows()){
            	commands.add("cmd.exe");
            	commands.add("/C");
            	//commands.add("cd " + algDir);
            	//commands.add("&");
            	if (launchFile.endsWith(".py")){
            	    String command = "python \"" + launchFilePath + "\" \"" + runConfigPath + "\"";
            	    commands.add(command);
            	    logger.info("queued command: " + command);
            	}
            	else {
            	    String command = launchFilePath + " " + runConfigPath;
            	    commands.add(command);
            	    logger.info("queued command: " + command);
            	}
            }
            else {
                
            	commands.add("/bin/bash");
            	commands.add("-c");
            	//commands.add("cd " + algDir);
            	//commands.add(" ; ");
            	//commands.add("./" + launchFile + " " + runConfigPath);
            	String escapedLaunchFilePath = launchFilePath.replaceAll(" ", "\\\\ ");
                String escapedArgs = runConfigPath.replaceAll(" ", "\\\\ ");
                if (launchFile.endsWith(".py")){
                    String command = "python " + escapedLaunchFilePath + " " + escapedArgs;
                    commands.add(command);
                    logger.info("queued command: " + command);
                }
                else {
                    String command = escapedLaunchFilePath + " " + escapedArgs;
                    commands.add(command);
                    logger.info("queued command: " + command);
                }
            }
            
            this.invoker = new CommandLineInvoker();
            String stdoutPath = algDir + FILESEP + "stdoutLog.txt";
            //this.invoker.runCommandLine(commands, stdoutPath);
            this.invoker.runCommandLine(commands, outputMonitor);
	    }
	   
	    catch(Exception e){
	        e.printStackTrace();
            logger.info(NL + NL + e.getMessage());
	    }
		
	}
	public static void usage(){
	    System.out.println("usage:" + NL);
        System.out.println("java -jar algLauncher.jar <algPropertiesPath> <runConfigPath>" + NL); 
	}
}
