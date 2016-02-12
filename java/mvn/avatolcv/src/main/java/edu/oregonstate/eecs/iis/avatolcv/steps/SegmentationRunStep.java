package edu.oregonstate.eecs.iis.avatolcv.steps;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import edu.oregonstate.eecs.iis.avatolcv.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.AvatolCVFileSystem;
import edu.oregonstate.eecs.iis.avatolcv.algorithm.AlgorithmLauncher;
import edu.oregonstate.eecs.iis.avatolcv.algorithm.AlgorithmSequence;
import edu.oregonstate.eecs.iis.avatolcv.algorithm.RunConfigFile;
import edu.oregonstate.eecs.iis.avatolcv.algorithm.SegmentationAlgorithm;
import edu.oregonstate.eecs.iis.avatolcv.session.SessionInfo;
import edu.oregonstate.eecs.iis.avatolcv.ui.javafx.SegmentationRunStepController;

public class SegmentationRunStep implements Step {
    private static final String NL = System.getProperty("line.separator");
    private static final String FILESEP = System.getProperty("file.separator");
    private SessionInfo sessionInfo = null;
    private AlgorithmLauncher launcher = null;
    private RunConfigFile runConfigFile = null;
    public SegmentationRunStep(SessionInfo sessionInfo){
        this.sessionInfo = sessionInfo;
    }
   
    @Override
    public void init() throws AvatolCVException {
        // TODO Auto-generated method stub
        
    }
    public SessionInfo getSessionInfo(){
        return this.sessionInfo;
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
    
    public String getTestImagesFilePath() throws AvatolCVException {
        return  AvatolCVFileSystem.getSessionDir() + FILESEP + "testImagesFile_segmentation.txt";
    }
    public String getScoredImagesDirPath() throws AvatolCVException{
        return AvatolCVFileSystem.getSessionDir() + FILESEP + "segmentedData";
    }
    public RunConfigFile getRunConfigFile(){
        return this.runConfigFile;
    }
    public void runSegmentation(SegmentationRunStepController controller, String processName, boolean useRunConfig) throws AvatolCVException {
        SegmentationAlgorithm sa  = sessionInfo.getSelectedSegmentationAlgorithm();
        AlgorithmSequence algSequence = sessionInfo.getAlgorithmSequence();
        algSequence.enableSegmentation();
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
    public void cancelSegmentation(){
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
        if (this.sessionInfo.isSegmentationAlgChosen()){
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
