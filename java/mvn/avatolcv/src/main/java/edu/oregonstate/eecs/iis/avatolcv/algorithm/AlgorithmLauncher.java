package edu.oregonstate.eecs.iis.avatolcv.algorithm;

import edu.oregonstate.eecs.iis.avatolcv.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.algorithm.AlgorithmModules.AlgType;

/**
 * 
 * @author admin-jed
 *
 *Class for launching all segmentation, orientation, and scoring algs present in the module system
 */
public class AlgorithmLauncher {
	private static final String FILESEP = System.getProperty("file.separator");
	public static void main(String[] args){
		try {
			//AlgorithmModules modules = new AlgorithmModules("C:\\avatol\\git\\modules");
			AlgorithmModules modules = new AlgorithmModules("/Users/jedirvine/av/avatol_cv/modules");
			AlgorithmLauncher launcher = new AlgorithmLauncher();
			launcher.launch("yaoSeg", AlgorithmModules.AlgType.SEGMENTATION,modules);
		}
		catch(AvatolCVException e){
			e.printStackTrace();
			System.out.println(e.getMessage());
		}
	}
	public AlgorithmLauncher(){
		
	}
	public void launch(String algName, AlgorithmModules.AlgType algType, AlgorithmModules modules) throws AvatolCVException {
		AlgorithmProperties props = modules.getAlgPropertiesForAlgName(algName, algType);
		String launchFile = props.getLaunchFile();
		String parentDir = props.getParentDir();
		String commandLine = "./" + launchFile + " segRunConfigWorks1.txt";
		CommandLineInvoker invoker = new CommandLineInvoker(parentDir);
		String stdoutPath = parentDir + FILESEP + "stdoutLog.txt";
		invoker.runCommandLine(commandLine, stdoutPath);
	}
}
