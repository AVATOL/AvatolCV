package edu.oregonstate.eecs.iis.avatolcv.steps;

import java.io.File;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.oregonstate.eecs.iis.avatolcv.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.AvatolCVFileSystem;
import edu.oregonstate.eecs.iis.avatolcv.algorithm.AlgorithmLauncher;
import edu.oregonstate.eecs.iis.avatolcv.algorithm.AlgorithmSequence;
import edu.oregonstate.eecs.iis.avatolcv.algorithm.RunConfigFile;
import edu.oregonstate.eecs.iis.avatolcv.algorithm.SegmentationAlgorithm;
import edu.oregonstate.eecs.iis.avatolcv.session.SessionInfo;
import edu.oregonstate.eecs.iis.avatolcv.ui.javafx.SegmentationRunStepController;

public class SegmentationRunStep implements Step {
    private static final String FILESEP = System.getProperty("file.separator");
    private SessionInfo sessionInfo = null;
    private AlgorithmLauncher launcher = null;
    private RunConfigFile segRunConfig = null;
    private static final Logger logger = LogManager.getLogger(SegmentationRunStep.class);

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
        return this.segRunConfig;
    }
    public void runSegmentation(SegmentationRunStepController controller, String processName, boolean useRunConfig) throws AvatolCVException {
        SegmentationAlgorithm sa  = sessionInfo.getSelectedSegmentationAlgorithm();
        AlgorithmSequence algSequence = sessionInfo.getAlgorithmSequence();
        algSequence.enableSegmentation();
        String runConfigPath = null;
        
        if (useRunConfig) {
            this.segRunConfig = new RunConfigFile(sa, algSequence, null);
            runConfigPath = this.segRunConfig.getRunConfigPath();
            File runConfigFile = new File(runConfigPath);
            if (!runConfigFile.exists()){
                throw new AvatolCVException("runConfigFile path does not exist."); 
            }
            logger.info("created runConfigFile " + runConfigPath);
            logger.info("runConfigFile has: " + segRunConfig.getPropertiesAsString());
            this.launcher = new AlgorithmLauncher(sa, runConfigPath, true);
        }
        else {
            this.launcher = new AlgorithmLauncher(sa, runConfigPath, false);
        }
        
        logger.info("launching " + sessionInfo.getSelectedSegmentationAlgorithm().getAlgName());
        this.launcher.launch(controller);
    }
    public void cancelSegmentation(){
        this.launcher.cancel();
        logger.info("cancelled segmentation algorithm run");
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
    @Override
	public boolean shouldRenderIfBackingIntoIt() {
		return false;
	}
    
}
