package edu.oregonstate.eecs.iis.avatolcv.steps;

import java.io.File;

import edu.oregonstate.eecs.iis.avatolcv.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.AvatolCVFileSystem;
import edu.oregonstate.eecs.iis.avatolcv.algorithm.AlgorithmLauncher;
import edu.oregonstate.eecs.iis.avatolcv.algorithm.AlgorithmSequence;
import edu.oregonstate.eecs.iis.avatolcv.algorithm.OutputMonitor;
import edu.oregonstate.eecs.iis.avatolcv.algorithm.RunConfigFile;
import edu.oregonstate.eecs.iis.avatolcv.algorithm.OrientationAlgorithm;
import edu.oregonstate.eecs.iis.avatolcv.session.SessionInfo;

public class OrientationRunStep implements Step {
    private SessionInfo sessionInfo = null;
    private AlgorithmLauncher launcher = null;
    private RunConfigFile runConfigFile = null;
    public OrientationRunStep(SessionInfo sessionInfo){
        this.sessionInfo = sessionInfo;
    }
    public String getSelectedOrientationAlgorithm(){
        return this.sessionInfo.getOrientationAlgName();
    }
    @Override
    public void init() throws AvatolCVException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void consumeProvidedData() throws AvatolCVException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public boolean hasFollowUpDataLoadPhase() {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean skipRunConfigForOrientation() throws AvatolCVException {
        String path = AvatolCVFileSystem.getDatasetDir() + "skipRunConfigForOrientationON.txt";
        File f = new File(path);
        if (f.exists()){
            return true;
        }
        return false;
    }
    public RunConfigFile getRunConfigFile(){
        return this.runConfigFile;
    }
    public void runOrientation(OutputMonitor controller, String processName, boolean useRunConfig) throws AvatolCVException {
        OrientationAlgorithm sa  = sessionInfo.getSelectedOrientationAlgorithm();
        AlgorithmSequence algSequence = sessionInfo.getAlgorithmSequence();
        algSequence.enableOrientation();
        String runConfigPath = null;
        
        if (useRunConfig) {
        	this.runConfigFile = new RunConfigFile(sa, algSequence, null);
            runConfigPath = this.runConfigFile.getRunConfigPath();
            File runConfigFile = new File(runConfigPath);
            if (!runConfigFile.exists()){
                throw new AvatolCVException("runConfigFile path does not exist."); 
            }
            this.launcher = new AlgorithmLauncher(sa, runConfigPath, true);
        }
        else {
            this.launcher = new AlgorithmLauncher(sa, runConfigPath, false);
        }
        //String statusPath = rcf.getAlgorithmStatusPath();
        //ProcessMonitor monitor = new ProcessMonitor(launcher, controller, statusPath);
        //Thread t = new Thread(monitor);
        //t.run();
        this.launcher.launch(controller);
    }
    public void cancelOrientation(){
        this.launcher.cancel();
    }
    /*
    public class ProcessMonitor implements Runnable {
        private AlgorithmLauncher launcher = null;
        private String statusPath = null;
        private OutputMonitor om = null;
        public ProcessMonitor(AlgorithmLauncher launcher, OutputMonitor om, String statusPath){
            this.launcher = launcher;
            this.statusPath = statusPath;
            this.om = om;
        }
        @Override
        public void run() {
            while (launcher.isProcessRunning()){
                try {
                    Thread.sleep(1000);
                }
                catch(Exception e){
                    
                }
                String status = getStatus(statusPath);
                //System.out.println(NL + "========================" + NL + "STATUS is " + status + NL + "========================" + NL);
                this.pp.setMessage("", status);
            }
        }
    }
    */
    
    @Override
    public boolean isEnabledByPriorAnswers() {
        if (this.sessionInfo.isOrientationAlgChosen()){
            return true;
        }
        else {
            return false;
        }
    }
    @Override
	public boolean shouldRenderIfBackingIntoIt() {
		return false;
	}
    
}
