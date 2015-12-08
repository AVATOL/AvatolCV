package edu.oregonstate.eecs.iis.avatolcv.steps;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import edu.oregonstate.eecs.iis.avatolcv.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.algorithm.AlgorithmLauncher;
import edu.oregonstate.eecs.iis.avatolcv.algorithm.AlgorithmModules;
import edu.oregonstate.eecs.iis.avatolcv.algorithm.AlgorithmSequence;
import edu.oregonstate.eecs.iis.avatolcv.algorithm.CommandLineInvoker;
import edu.oregonstate.eecs.iis.avatolcv.algorithm.OutputMonitor;
import edu.oregonstate.eecs.iis.avatolcv.algorithm.RunConfigFile;
import edu.oregonstate.eecs.iis.avatolcv.algorithm.SegmentationAlgorithm;
import edu.oregonstate.eecs.iis.avatolcv.core.ProgressPresenter;
import edu.oregonstate.eecs.iis.avatolcv.core.SessionInfo;
import edu.oregonstate.eecs.iis.avatolcv.ui.javafx.SegmentationRunStepController;

public class SegmentationRunStep implements Step {
    private static final String NL = System.getProperty("line.separator");
    private SessionInfo sessionInfo = null;
    private AlgorithmLauncher launcher = null;
    public SegmentationRunStep(SessionInfo sessionInfo){
        this.sessionInfo = sessionInfo;
    }
    public String getSelectedSegmentationAlgorithm(){
        return this.sessionInfo.getSegmentationAlgName();
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

    public void runSegmentation(SegmentationRunStepController controller, String processName) throws AvatolCVException {
        SegmentationAlgorithm sa  = sessionInfo.getSelectedSegmentationAlgorithm();
        AlgorithmSequence algSequence = sessionInfo.getAlgorithmSequence();
        algSequence.enableSegmentation();
        RunConfigFile rcf = new RunConfigFile(sa, algSequence);
        String runConfigPath = rcf.getRunConfigPath();
        File runConfigFile = new File(runConfigPath);
        if (!runConfigFile.exists()){
            throw new AvatolCVException("runConfigFile path does not exist."); 
        }
        this.launcher = new AlgorithmLauncher(sa, runConfigPath);
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
    private String getStatus(String path) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(path));
            String line = reader.readLine();
            reader.close();
            return line;
        }
        catch(IOException ioe){
            return "algorithm run status unavailable";
        }
    }
    @Override
    public boolean isEnabledByPriorAnswers() {
        if (this.sessionInfo.isSegmentationAlgChosen()){
            return true;
        }
        else {
            return false;
        }
        
    }
}
