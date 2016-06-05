package edu.oregonstate.eecs.iis.avatolcv.steps;

import java.io.File;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.oregonstate.eecs.iis.avatolcv.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.AvatolCVFileSystem;
import edu.oregonstate.eecs.iis.avatolcv.algorithm.AlgorithmLauncher;
import edu.oregonstate.eecs.iis.avatolcv.algorithm.AlgorithmSequence;
import edu.oregonstate.eecs.iis.avatolcv.algorithm.OutputMonitor;
import edu.oregonstate.eecs.iis.avatolcv.algorithm.RunConfigFile;
import edu.oregonstate.eecs.iis.avatolcv.algorithm.OrientationAlgorithm;
import edu.oregonstate.eecs.iis.avatolcv.session.DataIssue;
import edu.oregonstate.eecs.iis.avatolcv.session.SessionInfo;

public class OrientationRunStep implements Step {
    private SessionInfo sessionInfo = null;
    private AlgorithmLauncher launcher = null;
    private RunConfigFile orientRunConfig = null;
    private static final Logger logger = LogManager.getLogger(OrientationRunStep.class);

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
            logger.info("skipping runConfigForOrientation due to presence of " + path);
            return true;
        }
        return false;
    }
    public RunConfigFile getRunConfigFile(){
        return this.orientRunConfig;
    }
    public void runOrientation(OutputMonitor controller, String processName, boolean useRunConfig) throws AvatolCVException {
        OrientationAlgorithm sa  = sessionInfo.getSelectedOrientationAlgorithm();
        AlgorithmSequence algSequence = sessionInfo.getAlgorithmSequence();
        algSequence.enableOrientation();
        String runConfigPath = null;
        logger.info("running orientation algorithm ");
        if (useRunConfig) {
        	this.orientRunConfig = new RunConfigFile(sa, algSequence, null);
            runConfigPath = this.orientRunConfig.getRunConfigPath();
            File runConfigFile = new File(runConfigPath);
            if (!runConfigFile.exists()){
                throw new AvatolCVException("runConfigFile path does not exist."); 
            }
            logger.info("using runConfig file " + runConfigPath);
            logger.info("runConfigFile has: " + orientRunConfig.getPropertiesAsString());
            this.launcher = new AlgorithmLauncher(sa, runConfigPath, true);
        }
        else {
            this.launcher = new AlgorithmLauncher(sa, runConfigPath, false);
        }
        this.launcher.launch(controller);
    }
    public void cancelOrientation(){
        this.launcher.cancel();
        logger.info("cancelled orientation algorithm run");
    }
    
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
	@Override
	public List<DataIssue> getDataIssues() {
		return null;
	}
    
}
