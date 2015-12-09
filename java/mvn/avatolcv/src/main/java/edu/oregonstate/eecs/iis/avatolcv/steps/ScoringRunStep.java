package edu.oregonstate.eecs.iis.avatolcv.steps;

import java.io.File;
import java.util.List;

import edu.oregonstate.eecs.iis.avatolcv.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.AvatolCVFileSystem;
import edu.oregonstate.eecs.iis.avatolcv.algorithm.AlgorithmLauncher;
import edu.oregonstate.eecs.iis.avatolcv.algorithm.AlgorithmSequence;
import edu.oregonstate.eecs.iis.avatolcv.algorithm.OutputMonitor;
import edu.oregonstate.eecs.iis.avatolcv.algorithm.RunConfigFile;
import edu.oregonstate.eecs.iis.avatolcv.algorithm.ScoringAlgorithm;
import edu.oregonstate.eecs.iis.avatolcv.core.ImageInfo;
import edu.oregonstate.eecs.iis.avatolcv.core.ModalImageInfo;
import edu.oregonstate.eecs.iis.avatolcv.core.NormalizedImageInfo;
import edu.oregonstate.eecs.iis.avatolcv.core.NormalizedKey;
import edu.oregonstate.eecs.iis.avatolcv.core.NormalizedTypeIDName;
import edu.oregonstate.eecs.iis.avatolcv.core.NormalizedValue;
import edu.oregonstate.eecs.iis.avatolcv.core.ScoringConcernDetails;
import edu.oregonstate.eecs.iis.avatolcv.core.ScoringSet;
import edu.oregonstate.eecs.iis.avatolcv.core.SessionInfo;
import edu.oregonstate.eecs.iis.avatolcv.core.TrainingInfoFile;
import edu.oregonstate.eecs.iis.avatolcv.datasource.ChoiceItem;

public class ScoringRunStep implements Step {
    private SessionInfo sessionInfo = null;
    private AlgorithmLauncher launcher = null;
    public ScoringRunStep(SessionInfo sessionInfo){
        this.sessionInfo = sessionInfo;
    }
    public String getSelectedScoringAlgorithm() throws AvatolCVException {
        return this.sessionInfo.getScoringAlgName();
    }
    public String getImagePathWithIDFromFileList(String id, File[] files, String suffix){
    	if (suffix.equals("*") || suffix.equals("")){
    		for (File f : files){
                if (f.getName().startsWith(id)){
                    return f.getAbsolutePath();
                }
            }
    	}
    	else {
    		for (File f : files){
                if (f.getName().startsWith(id) && f.getName().contains(suffix)){
                    return f.getAbsolutePath();
                }
            }
    	}
        
        return null;
    }
    public void runScoring(OutputMonitor controller, String processName) throws AvatolCVException {
    	boolean hasTrainTestConcern = this.sessionInfo.hasTrainTestConcern();
    	NormalizedKey trainTestConcern = new NormalizedKey(null);
    	NormalizedValue trainTestConcernValue = new NormalizedValue(null);
    	if (hasTrainTestConcern){
    		trainTestConcern = this.sessionInfo.getTrainTestConcern();
    	}
    	 
        ScoringAlgorithm sa  = sessionInfo.getSelectedScoringAlgorithm();
        AlgorithmSequence algSequence = sessionInfo.getAlgorithmSequence();
        algSequence.enableScoring();
        RunConfigFile rcf = new RunConfigFile(sa, algSequence);
        String runConfigPath = rcf.getRunConfigPath();
        File runConfigFile = new File(runConfigPath);
        if (!runConfigFile.exists()){
            throw new AvatolCVException("runConfigFile path does not exist."); 
        }
        List<ChoiceItem> scoringConcerns = this.sessionInfo.getChosenScoringConcerns();
        boolean pointCoordinatesRelevant = this.sessionInfo.arePointCoordinatesRelavent();
        for (ChoiceItem scoringConcern : scoringConcerns){
            Object backingObject = scoringConcern.getBackingObject();
            ScoringConcernDetails scd = (ScoringConcernDetails)backingObject;
            String scoringConcernType = scd.getType();
            String scoringConcernID = scd.getID();
            String scoringConcernName = scd.getName();
            NormalizedKey scoringConcernKey = new NormalizedKey(NormalizedTypeIDName.buildTypeIdName(scoringConcernType, scoringConcernID, scoringConcernName));
            TrainingInfoFile tif = new TrainingInfoFile(scoringConcernType, scoringConcernID, scoringConcernName);
            tif.setImageDir(algSequence.getInputDir());
            ScoringSet scoringSet = this.sessionInfo.getScoringSetForScoringConcern(scoringConcernName);
            List<ModalImageInfo> trainingImages = scoringSet.getImagesToTrainOn();
            for (ModalImageInfo mii : trainingImages){
            	NormalizedImageInfo nii = mii.getNormalizedImageInfo();
            	if (hasTrainTestConcern){
            		trainTestConcernValue = nii.getValueForKey(trainTestConcern);
            	}
            	NormalizedValue value = nii.getValueForKey(scoringConcernKey);
            	//String valueName = new NormalizedTypeIDName(value).getName();
            	String imageID = nii.getImageID();
            	String pathWhereInputImagesForScoringLive = algSequence.getInputDir();
            	File f = new File(pathWhereInputImagesForScoringLive);
            	File[] files = f.listFiles();
            	String imagePathForScoring = getImagePathWithIDFromFileList(imageID, files, sa.getTrainingLabelImageSuffix());
            	if (null == imagePathForScoring){
            		System.out.println("WARNING - no imageFile found for imageID " + imageID);
            		imagePathForScoring = "imageFileNotAvailable";
            	    //throw new AvatolCVException("Cannot find file in scoring input dir " + pathWhereInputImagesForScoringLive + " with id " + imageID);
            	}
            	//String imageName = nii.getImageName();
            	String pointCoordinates = nii.getAnnotationCoordinates();
            	if (!trainTestConcern.getName().equals("")){
            		trainTestConcernValue = nii.getValueForKey(trainTestConcern);
            	}
            	
            	if (pointCoordinatesRelevant){
            		if (null == pointCoordinates){
                		ImageInfo.excludeForReason(ImageInfo.EXCLUSION_REASON_MISSING_ANNOTATION, false, nii.getImageID());
                	}
                	else {
                		tif.addImageInfo(imagePathForScoring, value.toString(),  pointCoordinates, trainTestConcern.toString(), trainTestConcernValue.toString());
                	}
            	}
            	else {
            		tif.addImageInfo(imagePathForScoring, value.toString(),  "", trainTestConcern.toString(), trainTestConcernValue.toString());
            	}
            	
            }
            tif.persist(AvatolCVFileSystem.getTrainingDataDirForScoring());
        }
        this.launcher = new AlgorithmLauncher(sa, runConfigPath);
        this.launcher.launch(controller);
    }
    public void cancelScoring(){
        this.launcher.cancel();
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

    @Override
    public boolean isEnabledByPriorAnswers() {
        return true;
    }

}
