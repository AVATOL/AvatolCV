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
import edu.oregonstate.eecs.iis.avatolcv.core.TrainingInfoFile;
import edu.oregonstate.eecs.iis.avatolcv.datasource.ChoiceItem;
import edu.oregonstate.eecs.iis.avatolcv.datasource.PointAnnotations;
import edu.oregonstate.eecs.iis.avatolcv.normalized.NormalizedImageInfo;
import edu.oregonstate.eecs.iis.avatolcv.normalized.NormalizedKey;
import edu.oregonstate.eecs.iis.avatolcv.normalized.NormalizedTypeIDName;
import edu.oregonstate.eecs.iis.avatolcv.normalized.NormalizedValue;
import edu.oregonstate.eecs.iis.avatolcv.scoring.HoldoutInfoFile;
import edu.oregonstate.eecs.iis.avatolcv.scoring.ModalImageInfo;
import edu.oregonstate.eecs.iis.avatolcv.scoring.ScoringConcernDetails;
import edu.oregonstate.eecs.iis.avatolcv.scoring.ScoringInfoFile;
import edu.oregonstate.eecs.iis.avatolcv.scoring.ScoringProfile;
import edu.oregonstate.eecs.iis.avatolcv.scoring.ScoringSet;
import edu.oregonstate.eecs.iis.avatolcv.session.SessionInfo;

public class ScoringRunStep implements Step {
	private static final String NL = System.getProperty("file.separator");
    private SessionInfo sessionInfo = null;
    private AlgorithmLauncher launcher = null;
    private RunConfigFile runConfigFile = null;
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
    /*
    public void addImageToTrainingInfoFile(ModalImageInfo mii, NormalizedKey scoringConcernKey,TrainingInfoFile tif) throws AvatolCVException {
        NormalizedKey trainTestConcern = new NormalizedKey(null);
        NormalizedValue trainTestConcernValue = new NormalizedValue(null);
        ScoringAlgorithm sa  = sessionInfo.getSelectedScoringAlgorithm();
        AlgorithmSequence algSequence = sessionInfo.getAlgorithmSequence();
        NormalizedImageInfo nii = mii.getNormalizedImageInfo();
        
        if (this.sessionInfo.hasTrainTestConcern()){
            trainTestConcern = this.sessionInfo.getTrainTestConcern();
            trainTestConcernValue = nii.getValueForKey(trainTestConcern);
        }
        
        String imagePath = getPathForNii(nii, algSequence, sa.getTrainingLabelImageSuffix());
        NormalizedValue value = nii.getValueForKey(scoringConcernKey);
        
        if (null == imagePath){
            System.out.println("WARNING - no imageFile found for imageID " + nii.getImageID());
            imagePath = "imageFileNotAvailable";
        }
        String pointCoordinates = nii.getAnnotationCoordinates();
        if (!trainTestConcern.getName().equals("")){
            trainTestConcernValue = nii.getValueForKey(trainTestConcern);
        }
        
        if (this.sessionInfo.arePointCoordinatesRelavent()){
            if (null == pointCoordinates){
                ImageInfo.excludeForSession(ImageInfo.EXCLUSION_REASON_MISSING_ANNOTATION, nii.getImageID());
            }
            else {
                tif.addImageInfo(imagePath, value.toString(),  pointCoordinates, trainTestConcern.toString(), trainTestConcernValue.toString());
            }
        }
        else {
            tif.addImageInfo(imagePath, value.toString(),  "", trainTestConcern.toString(), trainTestConcernValue.toString());
        }
    }*/
    public void addImageToTrainingInfoFile(ModalImageInfo mii, NormalizedKey scoringConcernKey,TrainingInfoFile tif, ScoringProfile sp) throws AvatolCVException {
        NormalizedImageInfo nii = mii.getNormalizedImageInfo();
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
   /* public void addImageToScoringInfoFile(ModalImageInfo mii, ScoringInfoFile sif) throws AvatolCVException {
        NormalizedKey trainTestConcern = new NormalizedKey(null);
        NormalizedValue trainTestConcernValue = new NormalizedValue(null);
        NormalizedImageInfo nii = mii.getNormalizedImageInfo();
        ScoringAlgorithm sa  = sessionInfo.getSelectedScoringAlgorithm();
        AlgorithmSequence algSequence = sessionInfo.getAlgorithmSequence();
        if (this.sessionInfo.hasTrainTestConcern()){
            trainTestConcern = this.sessionInfo.getTrainTestConcern();
            trainTestConcernValue = nii.getValueForKey(trainTestConcern);
        }
        
        String imagePath = getPathForNii(nii, algSequence,  "*");
        
        if (null == imagePath){
            System.out.println("WARNING - no imageFile found for imageID " + nii.getImageID());
            imagePath = "imageFileNotAvailable";
        }
        
        if (!trainTestConcern.getName().equals("")){
            trainTestConcernValue = nii.getValueForKey(trainTestConcern);
        }
        if (sa.shouldIncludePointAnnotationsInScoringFile()){
            String pointCoordinates = nii.getAnnotationCoordinates();
            sif.addImageInfo(imagePath, trainTestConcern.toString(), trainTestConcernValue.toString(), pointCoordinates);
        }
        else {
            sif.addImageInfo(imagePath, trainTestConcern.toString(), trainTestConcernValue.toString());
        }
    }*/
    public void runScoring(OutputMonitor controller, String processName, boolean useRunConfig) throws AvatolCVException {
        if (!useRunConfig){
            String runConfigPath = null;
            ScoringAlgorithm sa  = sessionInfo.getSelectedScoringAlgorithm();
            this.launcher = new AlgorithmLauncher(sa, runConfigPath, false);
            return;
        }
    	 
        AlgorithmSequence algSequence = sessionInfo.getAlgorithmSequence();
        algSequence.enableScoring();
        
        List<ChoiceItem> scoringConcerns = this.sessionInfo.getChosenScoringConcerns();
        for (ChoiceItem scoringConcern : scoringConcerns){
            Object backingObject = scoringConcern.getBackingObject();
            ScoringConcernDetails scd = (ScoringConcernDetails)backingObject;
            String scoringConcernType = scd.getType();
            String scoringConcernID = scd.getID();
            String scoringConcernName = scd.getName();
            NormalizedKey scoringConcernKey = new NormalizedKey(NormalizedTypeIDName.buildTypeIdName(scoringConcernType, scoringConcernID, scoringConcernName));
            ScoringProfile scoringProfile = new ScoringProfile(sessionInfo);
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
                HoldoutInfoFile hif = new HoldoutInfoFile(scoringConcernType, scoringConcernID, scoringConcernName);
                for (ModalImageInfo mii : scoringImages){
                    NormalizedImageInfo nii = mii.getNormalizedImageInfo();
                    NormalizedValue value = nii.getValueForKey(scoringConcernKey);
                    String imagePath = getPathForNii(nii, algSequence, sessionInfo.getSelectedScoringAlgorithm().getTrainingLabelImageSuffix());
                    hif.addInfo(imagePath, value.toString());
                }
                hif.persist(AvatolCVFileSystem.getTrainingDataDirForScoring());
            }
            
            List<String> imagesWronglyInBoth = sif.getMatchingImageNames(tif.getImagePaths());
            if (imagesWronglyInBoth.size() != 0){
            	StringBuilder sb = new StringBuilder();
            	sb.append("ERROR - images that appear in both training and scoring lists are : " + NL);
            	for (String image : imagesWronglyInBoth){
            		sb.append(image + NL);
            	}
            	throw new AvatolCVException("" + sb);
            }
            runConfigFile = new RunConfigFile(sessionInfo.getSelectedScoringAlgorithm(), algSequence, scoringImages);
            String runConfigPath = runConfigFile.getRunConfigPath();
            File runConfigFile = new File(runConfigPath);
            if (!runConfigFile.exists()){
                throw new AvatolCVException("runConfigFile path does not exist."); 
            }
        }
        
       
       
        this.launcher = new AlgorithmLauncher(sessionInfo.getSelectedScoringAlgorithm(), runConfigFile.getRunConfigPath(), true);
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
    @Override
	public boolean shouldRenderIfBackingIntoIt() {
		return false;
	}
    
}
