package edu.oregonstate.eecs.iis.avatolcv.steps;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.oregonstate.eecs.iis.avatolcv.AvatolCVConstants;
import edu.oregonstate.eecs.iis.avatolcv.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.AvatolCVFileSystem;
import edu.oregonstate.eecs.iis.avatolcv.algorithm.AlgorithmLauncher;
import edu.oregonstate.eecs.iis.avatolcv.algorithm.AlgorithmSequence;
import edu.oregonstate.eecs.iis.avatolcv.algorithm.OutputMonitor;
import edu.oregonstate.eecs.iis.avatolcv.algorithm.RunConfigFile;
import edu.oregonstate.eecs.iis.avatolcv.algorithm.ScoringAlgorithm;
import edu.oregonstate.eecs.iis.avatolcv.core.ImageInfo;
import edu.oregonstate.eecs.iis.avatolcv.datasource.ChoiceItem;
import edu.oregonstate.eecs.iis.avatolcv.datasource.PointAnnotations;
import edu.oregonstate.eecs.iis.avatolcv.normalized.NormalizedImageInfo;
import edu.oregonstate.eecs.iis.avatolcv.normalized.NormalizedKey;
import edu.oregonstate.eecs.iis.avatolcv.normalized.NormalizedTypeIDName;
import edu.oregonstate.eecs.iis.avatolcv.normalized.NormalizedValue;
import edu.oregonstate.eecs.iis.avatolcv.scoring.HoldoutInfoFile;
import edu.oregonstate.eecs.iis.avatolcv.scoring.IgnoreInfoFile;
import edu.oregonstate.eecs.iis.avatolcv.scoring.ModalImageInfo;
import edu.oregonstate.eecs.iis.avatolcv.scoring.ScoringConcernDetails;
import edu.oregonstate.eecs.iis.avatolcv.scoring.ScoringInfoFile;
import edu.oregonstate.eecs.iis.avatolcv.scoring.ScoringProfile;
import edu.oregonstate.eecs.iis.avatolcv.scoring.ScoringSet;
import edu.oregonstate.eecs.iis.avatolcv.scoring.TrainingInfoFile;
import edu.oregonstate.eecs.iis.avatolcv.session.DataIssue;
import edu.oregonstate.eecs.iis.avatolcv.session.SessionInfo;

public class ScoringRunStep implements Step {
	private static final String NL = System.getProperty("file.separator");
    private SessionInfo sessionInfo = null;
    private AlgorithmLauncher launcher = null;
    private RunConfigFile runConfig = null;
    private static final Logger logger = LogManager.getLogger(ScoringRunStep.class);

    public ScoringRunStep(SessionInfo sessionInfo){
        this.sessionInfo = sessionInfo;
    }
    public String getSelectedScoringAlgorithm() throws AvatolCVException {
        return this.sessionInfo.getScoringAlgName();
    }
    public void generateRunSummaries() throws AvatolCVException{
        this.sessionInfo.generateRunSummaries();
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
    public String getPathForNii(NormalizedImageInfo nii, AlgorithmSequence algSequence, String suffix){
        String imageID = nii.getImageID();
        String pathWhereInputImagesForScoringLive = algSequence.getInputDir();
        File f = new File(pathWhereInputImagesForScoringLive);
        File[] files = f.listFiles();
        String imagePath = getImagePathWithIDFromFileList(imageID, files, suffix);
        return imagePath;
    }
   
    public void addImageToTrainingInfoFile(ModalImageInfo mii, NormalizedKey scoringConcernKey,TrainingInfoFile tif, ScoringProfile sp) throws AvatolCVException {
        NormalizedImageInfo nii = mii.getNormalizedImageInfo();
        if (nii.isExcluded()){
            return;
        }
        // omit any marked as NPA from the training file, in case they slipped through prior screening.
        if (nii.isExcludedByValueForKey(scoringConcernKey)){
            logger.info("excluding nii at trainingFile creation due to valueForKey " + nii.getValueForKey(scoringConcernKey).getName());
            return;
        }
        String imagePath = getPathForNii(nii, sessionInfo.getAlgorithmSequence(), sessionInfo.getSelectedScoringAlgorithm().getTrainingLabelImageSuffix());
        NormalizedValue value = nii.getValueForKey(scoringConcernKey);
        
        if (null == imagePath){
            System.out.println("WARNING - no imageFile found for imageID " + nii.getImageID());
            imagePath = "imageFileNotAvailable";
        }
        String pointCoordinates = nii.getAnnotationCoordinates();
        if (this.sessionInfo.arePointCoordinatesRelavent()){
            if (null == pointCoordinates){
                ImageInfo.excludeForSession(ImageInfo.EXCLUSION_REASON_MISSING_ANNOTATION, nii.getImageID());
            }
            else {
                if (!this.sessionInfo.getChosenScoringAlgorithm().canTrainOnMultipleAnnotationsPerImage()){
                    pointCoordinates = PointAnnotations.getFirstAnnotation(pointCoordinates);
                }
                tif.addImageInfo(imagePath, value.toString(),  pointCoordinates, "" + sp.getTrainTestConcern(), "" + sp.getTrainTestConcernValue(mii));
            }
        }
        else {
            tif.addImageInfo(imagePath, value.toString(),  "", "" + sp.getTrainTestConcern(), "" + sp.getTrainTestConcernValue(mii));
        }
    }
    
    public void addImageToScoringInfoFile(ModalImageInfo mii, ScoringInfoFile sif, ScoringProfile sp) throws AvatolCVException {
        NormalizedImageInfo nii = mii.getNormalizedImageInfo();
        if (nii.isExcluded()){
            return;
        }
        String imagePath = getPathForNii(nii, sessionInfo.getAlgorithmSequence(),  "*");
        
        if (null == imagePath){
            System.out.println("WARNING - no imageFile found for imageID " + nii.getImageID());
            imagePath = "imageFileNotAvailable";
        }
       
        if (sp.emitPointAnnotationsInScoringFile()){
            String pointCoordinates = nii.getAnnotationCoordinates();
            sif.addImageInfo(imagePath, "" + sp.getTrainTestConcern(), "" + sp.getTrainTestConcernValue(mii), pointCoordinates);
        }
        else {
            sif.addImageInfo(imagePath, "" + sp.getTrainTestConcern(), "" + sp.getTrainTestConcernValue(mii));
        }
    }
  
    public boolean runScoring(OutputMonitor controller, String processName, boolean useRunConfig) throws AvatolCVException {
        if (!useRunConfig){
        	logger.info("Scoring run skipping runConfigFile");
            String runConfigPath = null;
            ScoringAlgorithm sa  = sessionInfo.getSelectedScoringAlgorithm();
            this.launcher = new AlgorithmLauncher(sa, runConfigPath, false);
            return true;
        }
    	 
        AlgorithmSequence algSequence = sessionInfo.getAlgorithmSequence();
        algSequence.enableScoring();
        List<String> scoringConcernsWithOnlyOneCharacterState = new ArrayList<String>();
        List<ChoiceItem> scoringConcerns = this.sessionInfo.getChosenScoringConcerns();
        for (ChoiceItem scoringConcern : scoringConcerns){
        	logger.info("creating TrainingInfoFile and ScoringInfoFile for " + scoringConcern.getNormalizedKey());
            Object backingObject = scoringConcern.getBackingObject();
            ScoringConcernDetails scd = (ScoringConcernDetails)backingObject;
            String scoringConcernType = scd.getType();
            String scoringConcernID = scd.getID();
            String scoringConcernName = scd.getName();
            NormalizedKey scoringConcernKey = new NormalizedKey(NormalizedTypeIDName.buildTypeIdName(scoringConcernType, scoringConcernID, scoringConcernName));
            ScoringProfile scoringProfile = new ScoringProfile(sessionInfo);
            /*
             * ignore file
             */
            if (this.sessionInfo.isScoringModeEvaluation()){
                logger.info("creating IgnoreInfoFile");
                IgnoreInfoFile iif = new IgnoreInfoFile(scoringConcernType, scoringConcernID, scoringConcernName);
                ScoringSet scoringSet = this.sessionInfo.getScoringSetForScoringConcern(scoringConcernName);
                List<ModalImageInfo> ignoreImages = scoringSet.getImagesToIgnore();
                for (ModalImageInfo mii : ignoreImages){
                    addImageToTrainingInfoFile(mii, scoringConcernKey, iif, scoringProfile);
                }
                iif.persist(AvatolCVFileSystem.getTrainingDataDirForScoring());
            }
            /*
             * training file
             */
            TrainingInfoFile tif = new TrainingInfoFile(scoringConcernType, scoringConcernID, scoringConcernName);
            //tif.setImageDir(algSequence.getInputDir());
            ScoringSet scoringSet = this.sessionInfo.getScoringSetForScoringConcern(scoringConcernName);
            List<ModalImageInfo> trainingImages = scoringSet.getImagesToTrainOn();
            for (ModalImageInfo mii : trainingImages){
                addImageToTrainingInfoFile(mii, scoringConcernKey, tif, scoringProfile);
            }
            tif.persist(AvatolCVFileSystem.getTrainingDataDirForScoring());
            if (tif.isOnlyOneCharacterStateInPlay()){
                scoringConcernsWithOnlyOneCharacterState.add(scoringConcernName);
            }
            /*
             * scoring_ file
             */
            ScoringInfoFile sif = new ScoringInfoFile(scoringConcernType, scoringConcernID, scoringConcernName);
            sif.setImageDir(algSequence.getInputDir());
            List<ModalImageInfo> scoringImages = scoringSet.getImagesToScore();
            for (ModalImageInfo mii : scoringImages){
                addImageToScoringInfoFile(mii,  sif, scoringProfile);
            }
            sif.persist(AvatolCVFileSystem.getTrainingDataDirForScoring());
            
            if (this.sessionInfo.isScoringModeEvaluation()){
            	logger.info("creating HoldoutInfoFile");
                HoldoutInfoFile hif = new HoldoutInfoFile(scoringConcernType, scoringConcernID, scoringConcernName);
                List<ModalImageInfo> toScoreImages = scoringSet.getImagesToScore();
                for (ModalImageInfo mii : toScoreImages){
                    NormalizedImageInfo nii = mii.getNormalizedImageInfo();
                    NormalizedValue value = nii.getValueForKey(scoringConcernKey);
                    String imagePath = getPathForNii(nii, algSequence, sessionInfo.getSelectedScoringAlgorithm().getTrainingLabelImageSuffix());
                    hif.addInfo(imagePath, value.toString(), scoringProfile.getTrainTestConcernValue(mii));
                }
                hif.persist(AvatolCVFileSystem.getTrainingDataDirForScoring());
            }
            logger.info("verifying there are no images that are both in the training and scoring set...");
            List<String> imagesWronglyInBoth = sif.getMatchingImageNames(tif.getImagePaths());
            if (imagesWronglyInBoth.size() != 0){
            	StringBuilder sb = new StringBuilder();
            	sb.append("ERROR - images that appear in both training and scoring lists are : " + NL);
            	for (String image : imagesWronglyInBoth){
            		sb.append(image + NL);
            	}
            	throw new AvatolCVException("" + sb);
            }
            runConfig = new RunConfigFile(sessionInfo.getSelectedScoringAlgorithm(), algSequence, scoringImages);
            String runConfigPath = runConfig.getRunConfigPath();
            File runConfigFile = new File(runConfigPath);
            if (!runConfigFile.exists()){
                throw new AvatolCVException("runConfigFile path does not exist."); 
            }
            logger.info("created runConfigFile " + runConfigPath);
            logger.info("runConfigFile has: " + runConfig.getPropertiesAsString());
        }
        ScoringAlgorithm sa  = sessionInfo.getSelectedScoringAlgorithm();
        boolean watchForSingleCharState = false;
        if (sa.requiresPresentAndAbsentTrainingExamplesForCharacter()){
            watchForSingleCharState = true;
        }
        
        if (watchForSingleCharState && !scoringConcernsWithOnlyOneCharacterState.isEmpty() ){
            controller.acceptOutput("Cannot launch algorithm" + NL + NL);
            controller.acceptOutput(sa.getAlgName() + " requires that there be both present and absent character states in the training data for each character." +NL);
            controller.acceptOutput("The following characters have either all present or all absent" + NL + NL);
            for (String s : scoringConcernsWithOnlyOneCharacterState){
                controller.acceptOutput("  " + s);
            }
            return false;
        }
        else {
            logger.info("launching " + sessionInfo.getSelectedScoringAlgorithm().getAlgName());
            this.launcher = new AlgorithmLauncher(sessionInfo.getSelectedScoringAlgorithm(), runConfig.getRunConfigPath(), true);
            this.launcher.launch(controller);
            return true;
        }
        
    }
    
    public void cancelScoring(){
        this.launcher.cancel();
        logger.info("cancelled scoring algorithm run");
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
    @Override
	public boolean shouldRenderIfBackingIntoIt() {
		return false;
	}
	@Override
	public SessionInfo getSessionInfo() {
		return this.sessionInfo;
	}
}
