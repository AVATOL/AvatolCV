package edu.oregonstate.eecs.iis.avatolcv.morphobank;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edu.oregonstate.eecs.iis.avatolcv.AvatolCVFileSystem;
import edu.oregonstate.eecs.iis.avatolcv.SystemDependent;
import edu.oregonstate.eecs.iis.avatolcv.TestProgressPresenter;
import edu.oregonstate.eecs.iis.avatolcv.core.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.core.ImageInfo;
import edu.oregonstate.eecs.iis.avatolcv.core.ProgressPresenter;
import edu.oregonstate.eecs.iis.avatolcv.core.Step;
import edu.oregonstate.eecs.iis.avatolcv.core.StepSequence;
import edu.oregonstate.eecs.iis.avatolcv.questionnaire.QQuestion;
import edu.oregonstate.eecs.iis.avatolcv.questionnaire.QuestionSequencer;
import edu.oregonstate.eecs.iis.avatolcv.steps.CharQuestionsStep;
import edu.oregonstate.eecs.iis.avatolcv.ws.MorphobankWSClient;
import edu.oregonstate.eecs.iis.avatolcv.ws.MorphobankWSClientImpl;
import edu.oregonstate.eecs.iis.avatolcv.ws.morphobank.CharacterInfo.MBCharacter;
import edu.oregonstate.eecs.iis.avatolcv.ws.morphobank.ViewInfo.MBView;
import junit.framework.Assert;
import junit.framework.TestCase;

public class MBSessionTester extends TestCase {
    private static final String FILESEP = System.getProperty("file.separator");
    //public BisqueWSClient getBogusWSClient(){
    //    BisqueWSClient client = new BogusBisqueWSClient();
    //    return client;
    //}
    public void testSession(){
        //BisqueWSClient client = getBogusWSClient();
        MorphobankWSClient client = new MorphobankWSClientImpl();
        SystemDependent sd = new SystemDependent();
        String avatolcv_rootDir = sd.getRootDir();
        System.out.println("root dir sensed as " + avatolcv_rootDir);
        try {
            AvatolCVFileSystem afs = new AvatolCVFileSystem(avatolcv_rootDir);
        }
        catch(AvatolCVException e){
            Assert.fail("problem instantiating AvatolCVFileSystem : " + e.getMessage());
        }
        /*
         * create session
         */
        MBSessionData sessionData = null;
        try {
            sessionData = new MBSessionData(avatolcv_rootDir);
        }
        catch (AvatolCVException e){
            Assert.fail("problem instantiating MBSessionData " + e.getMessage());
        }
        StepSequence ss = new StepSequence();
        Step loginStep = new MBLoginStep(null, client);
        ss.appendStep(loginStep);
        Step matrixStep = new MBMatrixChoiceStep(null, client, sessionData); 
        ss.appendStep(matrixStep);
        Step charChoiceStep = new MBCharChoiceStep(null, client, sessionData);
        ss.appendStep(charChoiceStep);
        Step viewChoiceStep = new MBViewChoiceStep(null, sessionData);
        ss.appendStep(viewChoiceStep);
        Step imagePullStep = new MBImagePullStep(null, client, sessionData);
        ss.appendStep(imagePullStep);
        Step exclusionCoachingStep = new MBExclusionQualityStep(null, client, sessionData);
        ss.appendStep(exclusionCoachingStep);
        Step exclusionStep = new MBExclusionPropertyStep(null, sessionData);
        ss.appendStep(exclusionStep);
        Step charQuestionsStep = new CharQuestionsStep(null, sessionData);
        ss.appendStep(charQuestionsStep);
        
        
        MBLoginStep mls = (MBLoginStep)ss.getCurrentStep();
        /*
         * throw exception on failed login 
         */
        try {
            mls.setUsername("irvine@eecs.oregonstate.edu");
            mls.setPassword("badPassword");
            mls.consumeProvidedData();
            Assert.fail("should have thrown exception on bad password");
        }
        catch(AvatolCVException bse){
            Assert.assertTrue(true);
        }
        
        /*
         *  good password should change state
         */
        try {
            mls.setUsername("irvine@eecs.oregonstate.edu");
            mls.setPassword("squonkmb");
            mls.consumeProvidedData();
            Assert.assertTrue(true);
            
        }
        catch(AvatolCVException bse){
            Assert.fail("should not have thrown exception on good password");
        }
        
        //
        // load matrices
        //
        ss.next();
        MBMatrixChoiceStep mcs = (MBMatrixChoiceStep)ss.getCurrentStep();
        try {
            List<String> matrices = mcs.getAvailableMatrices();
            Collections.sort(matrices);
            Assert.assertTrue(matrices.contains("matrixTestWS"));
            mcs.setChosenMatrix("matrixTestWS");
            mcs.consumeProvidedData();
        }
        catch(AvatolCVException e){
            Assert.fail("should not have thrown exception on getDatasets");
        }
        //
        // choose character
        //
        
        ss.next();
        MBCharChoiceStep mccs = (MBCharChoiceStep)ss.getCurrentStep();
        try {
            List<MBCharacter> chars = mccs.getCharacters();
            //Assert.assertTrue(chars.size() == 2); had to comment this out - there are more than two on the live site
            Assert.assertTrue(charsContainName(chars,"charA"));
            Assert.assertTrue(charsContainName(chars,"charB"));
            List<MBCharacter> chosenCharacters = new ArrayList<MBCharacter>();
            chosenCharacters.add(chars.get(0));
            chosenCharacters.add(chars.get(1));
            mccs.setChosenCharacters(chosenCharacters);
            
            
        }
        catch(AvatolCVException e){
            Assert.fail("should not have thrown exception on getCharacters");
        }
        try{
            mccs.consumeProvidedData();
        }
        catch(AvatolCVException e){
            Assert.fail("should not have thrown exception on consumeProvidedData");
        }
        //
        // choose view
        //
       
        ss.next();
        MBViewChoiceStep vcs = (MBViewChoiceStep)ss.getCurrentStep();
        try {
            List<MBView> views = vcs.getViews();
            //Assert.assertTrue(chars.size() == 2); had to comment this out - there are more than two on the live site
            Assert.assertTrue(viewsContainName(views,"side"));
            Assert.assertTrue(viewsContainName(views,"front"));
            vcs.setChosenView(views.get(0).getName());
        }
        catch(AvatolCVException e){
            Assert.fail("should not have thrown exception on getViews");
        }
        try{
            vcs.consumeProvidedData();
        }
        catch(AvatolCVException e){
            Assert.fail("should not have thrown exception on consumeProvidedData");
        }
        
        //
        // load images
        //
        ss.next();
        MBImagePullStep mips = (MBImagePullStep)ss.getCurrentStep();
        ProgressPresenter pp = new TestProgressPresenter();
        try {
            mips.downloadImageInfoForChosenCharactersAndView(pp, "testing");
        }
        catch(AvatolCVException e){
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
        List<ImageInfo> currentImages = sessionData.getImagesLarge();
        Assert.assertTrue(currentImages.size() != 0);
        ImageInfo image = currentImages.get(0);
        String name = image.getFilename();
        String imagesLargeDir = sessionData.getImagesLargeDir();
        String pathOfSupposedlyDownloadedImage = imagesLargeDir + FILESEP + name;
        File downloadedImageFile = new File(pathOfSupposedlyDownloadedImage);
        Assert.assertTrue(downloadedImageFile.exists());
        //
        // exclusion coaching
        //
        /*
        ss.next();
        BisqueExclusionCoachingStep becs = (BisqueExclusionCoachingStep)ss.getCurrentStep();
        becs.userHasViewed();
        //
        // image exclusion
        //
        ss.next();
        BisqueExclusionStep bes = (BisqueExclusionStep)ss.getCurrentStep();
        List<ImageInfo> images = sessionData.getImagesLarge();
        List<ImageInfo> imagesToInclude = new ArrayList<ImageInfo>();
        List<ImageInfo> imagesToExclude = new ArrayList<ImageInfo>();
        for (ImageInfo ii : images){
            if (ii.getNameAsUploadedNormalized().equals("Neph") || ii.getNameAsUploadedNormalized().equals("Pree")){
                imagesToInclude.add(ii);
            }
            else {
                imagesToExclude.add(ii);
            }
        }
        try {
            bes.setImagesToExclude(imagesToExclude);
            bes.setImagesToInclude(imagesToInclude);
            bes.consumeProvidedData();
        }
        catch(AvatolCVException e){
            Assert.fail(e.getMessage());
        }
        Assert.assertTrue(sessionData.getIncludedImages() != null);
        //
        // character questions
        //
        ss.next();
        CharQuestionsStep cqs = (CharQuestionsStep)ss.getCurrentStep();
        try {
            cqs.init();
        }
        catch(AvatolCVException e){
            Assert.fail("problem initializing CharQuestionStep " + e.getMessage());
        }
        QuestionSequencer qs = cqs.getQuestionSequencer();
        QQuestion qquestion = qs.getCurrentQuestion();
        try {
            Assert.assertTrue(qquestion.getAnswerIntegrity("perimeter").isValid());
            Assert.assertTrue(qquestion.getAnswerIntegrity("interior").isValid());
            Assert.assertFalse(qquestion.getAnswerIntegrity("an African swallow").isValid());
        }
        catch(AvatolCVException e){
            Assert.fail("problem getting answer integrity");
        }
        try {
            qs.answerQuestion("perimeter");
        }
        catch(AvatolCVException e){
            Assert.fail("problem answering question");
        }
        try {
            File f = new File(sessionData.getCharQuestionsAnsweredQuestionsPath());
            if (f.exists()){
                f.delete();
            }
            cqs.consumeProvidedData();
            Assert.assertTrue(f.exists());
        }
        catch(AvatolCVException e){
            Assert.fail("problem consuming data");
        }
        */
    }

    private boolean charsContainName(List<MBCharacter> chars, String s){
        for (MBCharacter c : chars){
            if (c.getCharName().equals(s)){
                return true;
            }
        }
        return false;
    }
    private boolean viewsContainName(List<MBView> views, String s){
        for (MBView v : views){
            if (v.getName().equals(s)){
                return true;
            }
        }
        return false;
    }
    /*
     * need to handle timeout situations with connections!
     */
    
}
