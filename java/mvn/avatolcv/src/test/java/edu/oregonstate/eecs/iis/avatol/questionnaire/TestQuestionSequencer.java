package edu.oregonstate.eecs.iis.avatol.questionnaire;

import java.util.List;

import edu.oregonstate.eecs.iis.avatolcv.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.SystemDependent;
import edu.oregonstate.eecs.iis.avatolcv.AvatolCVFileSystem;
import edu.oregonstate.eecs.iis.obsolete.avatolcv.questionnaire.AnsweredQuestion;
import edu.oregonstate.eecs.iis.obsolete.avatolcv.questionnaire.QQuestion;
import edu.oregonstate.eecs.iis.obsolete.avatolcv.questionnaire.QQuestions;
import edu.oregonstate.eecs.iis.obsolete.avatolcv.questionnaire.QuestionSequencer;
import edu.oregonstate.eecs.iis.obsolete.avatolcv.questionnaire.QuestionsXMLFile;
import junit.framework.Assert;
import junit.framework.TestCase;

public class TestQuestionSequencer extends TestCase {
    private static final String FILESEP = System.getProperty("file.separator");
    private AnsweredQuestion prevAnswer = null;
    private QQuestion question2 = null;
    private QQuestion question3 = null;
    private QQuestion question4 = null;
    private QQuestion question5 = null;
    public QuestionsXMLFile getQuestionsXMLFile(){
        SystemDependent sd = new SystemDependent();
        String rootDir = sd.getRootDir();
        try {
            AvatolCVFileSystem fs = new AvatolCVFileSystem(rootDir);
        }
        catch(AvatolCVException e){
            Assert.fail("problem instanitation QuestionsXMLFile.");
        }
        String simpleXMLPath = rootDir + FILESEP + "tests" + FILESEP + "simple.xml";
        QuestionsXMLFile xmlFile = null;
        try {
            xmlFile = new QuestionsXMLFile(simpleXMLPath);
        }
        catch(AvatolCVException e){
            Assert.fail("problem instanitation QuestionsXMLFile.");
        }
        return xmlFile;
    }
    public void  testQuestionSequencerCASEForwardSimple(){
        QuestionsXMLFile xmlFile = getQuestionsXMLFile();
        QQuestions qquestions = null;
        QuestionSequencer qs = null;
        try {
            qquestions = new QQuestions(xmlFile.getDomNode());
            qs = new QuestionSequencer(qquestions.getQuestions());
        }
        catch(AvatolCVException e){
            Assert.fail(e.getMessage());
        }
        
        int index = qs.getNextAnswerIndex();
        Assert.assertEquals(index,1);
        QQuestion question1 = qs.getCurrentQuestion();
        Assert.assertEquals(question1.getId(),"BREADBOX");
        // test invalid answer
        try {
            qs.answerQuestion("maybe");
            Assert.fail("invalid answer accepted for question");
        }
        catch (Exception e){
            // nothing to do, just trying to flag the failure to throw
            // an exception
        }

        //test valid answers moving forward
        String existingAnswerToNextQuestion = null;
        try {
            existingAnswerToNextQuestion = qs.answerQuestion("yes");
        }
        catch(AvatolCVException e){
            Assert.fail(e.getMessage());
        }
         
        Assert.assertEquals(existingAnswerToNextQuestion,"NOT_YET_SPECIFIED");
        Assert.assertEquals(qs.getNextAnswerIndex(),2);
        Assert.assertFalse(qs.isAllQuestionsAnswered());
        QQuestion question2 = qs.getCurrentQuestion();
        Assert.assertEquals(question2.getId(),"COLOR");
    
        try {
            existingAnswerToNextQuestion = qs.answerQuestion("red");
        }
        catch(AvatolCVException e){
            Assert.fail(e.getMessage());
        }
        
        Assert.assertEquals(existingAnswerToNextQuestion,"NOT_YET_SPECIFIED");
        Assert.assertEquals(qs.getNextAnswerIndex(),3);
        Assert.assertFalse(qs.isAllQuestionsAnswered());
        QQuestion question3 = qs.getCurrentQuestion();
        Assert.assertEquals(question3.getId(),"COVER_PERCENT");
    
        try {
            existingAnswerToNextQuestion = qs.answerQuestion("40");
        }
        catch(AvatolCVException e){
            Assert.fail(e.getMessage());
        }
        Assert.assertEquals(existingAnswerToNextQuestion,"NOT_YET_SPECIFIED");
        Assert.assertEquals(qs.getNextAnswerIndex(),4);
        Assert.assertTrue(qs.isAllQuestionsAnswered());
    
        Assert.assertTrue(qs.getAnsweredQuestions().size() == 3);
        Assert.assertEquals(qs.getAnsweredQuestions().get(0).getQuestionID(), "BREADBOX");
        Assert.assertEquals(qs.getAnsweredQuestions().get(1).getQuestionID(), "COLOR");
        Assert.assertEquals(qs.getAnsweredQuestions().get(2).getQuestionID(), "COVER_PERCENT");
        Assert.assertEquals(qs.getAnsweredQuestions().get(0).getAnswer(), "yes");
        Assert.assertEquals(qs.getAnsweredQuestions().get(1).getAnswer(), "red");
        Assert.assertEquals(qs.getAnsweredQuestions().get(2).getAnswer(), "40");
    }

    public void testQuestionSequencerCASE_Keep1(){
        // CASE_KEEP1 forward one question, back up, then forward 
        QuestionsXMLFile xmlFile = getQuestionsXMLFile();
        QQuestions qquestions = null;
        QuestionSequencer qs = null;
        try {
            qquestions = new QQuestions(xmlFile.getDomNode());
            qs = new QuestionSequencer(qquestions.getQuestions());
        }
        catch(AvatolCVException e){
            Assert.fail(e.getMessage());
        }
        String existingAnswerToNextQuestion = null;
        try {
            existingAnswerToNextQuestion = qs.answerQuestion("yes");
        }
        catch(AvatolCVException e){
            Assert.fail(e.getMessage());
        }
        
        Assert.assertEquals(existingAnswerToNextQuestion,"NOT_YET_SPECIFIED");
        Assert.assertFalse(qs.isAllQuestionsAnswered());
        
        // step backward 
        Assert.assertTrue(qs.canBackUp());
        AnsweredQuestion prevAnswer = null;
        try {
            prevAnswer = qs.backUp();
        }
        catch(AvatolCVException e){
            Assert.fail(e.getMessage());
        }
        
        Assert.assertEquals(prevAnswer.getQuestionID(),"BREADBOX");
        Assert.assertEquals(prevAnswer.getAnswer(),"yes");
        QQuestion question1 = qs.getCurrentQuestion();
        Assert.assertEquals(question1.getId(),"BREADBOX");
        
        Assert.assertTrue(qs.getAnsweredQuestions().size() == 1);
        Assert.assertEquals(qs.getAnsweredQuestions().get(1).getQuestionID(), "BREADBOX");
        Assert.assertEquals(qs.getAnsweredQuestions().get(1).getAnswer(), "yes");
        Assert.assertEquals(qs.getNextAnswerIndex(),1);  // this should have stepped back one
        
        Assert.assertFalse(qs.canBackUp());
        // move forward the same path, ensure prior set answers are
        // right
        try {
            existingAnswerToNextQuestion = qs.answerQuestion("yes");
        }
        catch(AvatolCVException e){
            Assert.fail(e.getMessage());
        }
        
        Assert.assertEquals(existingAnswerToNextQuestion,"NOT_YET_SPECIFIED");
        Assert.assertEquals(qs.getNextAnswerIndex(),2);
        Assert.assertFalse(qs.isAllQuestionsAnswered());
    }
    
    

    public void testQuestionSequencerCASE_Keep2(){
        // CASE_KEEP2 forward two questions, back up one, then forward
        QuestionsXMLFile xmlFile = getQuestionsXMLFile();
        QQuestions qquestions = null;
        QuestionSequencer qs = null;
        try {
            qquestions = new QQuestions(xmlFile.getDomNode());
            qs = new QuestionSequencer(qquestions.getQuestions());
        }
        catch(AvatolCVException e){
            Assert.fail(e.getMessage());
        }
        String existingAnswerToNextQuestion = null;
        try {
            existingAnswerToNextQuestion = qs.answerQuestion("yes");
            existingAnswerToNextQuestion = qs.answerQuestion("red");
        }
        catch(AvatolCVException e){
            Assert.fail(e.getMessage());
        }
        
        Assert.assertEquals(existingAnswerToNextQuestion,"NOT_YET_SPECIFIED");
        Assert.assertFalse(qs.isAllQuestionsAnswered());
        
        // step backward 
        Assert.assertTrue(qs.canBackUp());
        try {
            prevAnswer = qs.backUp();
        }
        catch(AvatolCVException e){
            Assert.fail(e.getMessage());
        }
        
        Assert.assertEquals(prevAnswer.getQuestionID(),"COLOR");
        Assert.assertEquals(prevAnswer.getAnswer(),"red");
        question2 = qs.getCurrentQuestion();
        Assert.assertEquals(question2.getId(),"COLOR");
        
        Assert.assertTrue(qs.getAnsweredQuestions().size() == 2);
        Assert.assertEquals(qs.getAnsweredQuestions().get(1).getQuestionID(), "BREADBOX");
        Assert.assertEquals(qs.getAnsweredQuestions().get(2).getQuestionID(), "COLOR");
        Assert.assertEquals(qs.getAnsweredQuestions().get(1).getAnswer(), "yes");
        Assert.assertEquals(qs.getAnsweredQuestions().get(2).getAnswer(), "red");
        Assert.assertEquals(qs.getNextAnswerIndex(),2);  // this should have stepped back one
        
        Assert.assertTrue(qs.canBackUp());
        // move forward the same path, ensure prior set answers are
        // right
        try {
            existingAnswerToNextQuestion = qs.answerQuestion("red");
        }
        catch(AvatolCVException e){
            Assert.fail(e.getMessage());
        }
        
        Assert.assertEquals(existingAnswerToNextQuestion,"NOT_YET_SPECIFIED");
        Assert.assertEquals(qs.getNextAnswerIndex(),3);
        Assert.assertFalse(qs.isAllQuestionsAnswered());
        
    }

    public void testQuestionSequencerCASE_Keep3(){
        // CASE_KEEP3 forward through last question, back up one, then forward
        QuestionsXMLFile xmlFile = getQuestionsXMLFile();
        QQuestions qquestions = null;
        QuestionSequencer qs = null;
        try {
            qquestions = new QQuestions(xmlFile.getDomNode());
            qs = new QuestionSequencer(qquestions.getQuestions());
        }
        catch(AvatolCVException e){
            Assert.fail(e.getMessage());
        }
        String existingAnswerToNextQuestion = null;
        try {
            existingAnswerToNextQuestion = qs.answerQuestion("yes");
            existingAnswerToNextQuestion = qs.answerQuestion("red");
            existingAnswerToNextQuestion = qs.answerQuestion("40");
        }
        catch(AvatolCVException e){
            Assert.fail(e.getMessage());
        }
        
        Assert.assertEquals(existingAnswerToNextQuestion,"NOT_YET_SPECIFIED");
        Assert.assertTrue(qs.isAllQuestionsAnswered());
        
        // step backward 
        Assert.assertTrue(qs.canBackUp());
        try {
            prevAnswer = qs.backUp();
        }
        catch(AvatolCVException e){
            Assert.fail(e.getMessage());
        }
        
        Assert.assertEquals(prevAnswer.getQuestionID(),"COVER_PERCENT");
        Assert.assertEquals(prevAnswer.getAnswer(),"40");
        QQuestion question3 = qs.getCurrentQuestion();
        Assert.assertEquals(question3.getId(),"COVER_PERCENT");
        
        Assert.assertTrue(qs.getAnsweredQuestions().size() == 3);
        Assert.assertEquals(qs.getAnsweredQuestions().get(1).getQuestionID(), "BREADBOX");
        Assert.assertEquals(qs.getAnsweredQuestions().get(2).getQuestionID(), "COLOR");
        Assert.assertEquals(qs.getAnsweredQuestions().get(3).getQuestionID(), "COVER_PERCENT");
        Assert.assertEquals(qs.getAnsweredQuestions().get(1).getAnswer(), "yes");
        Assert.assertEquals(qs.getAnsweredQuestions().get(2).getAnswer(), "red");
        Assert.assertEquals(qs.getAnsweredQuestions().get(3).getAnswer(), "40"); // answer remains if we need to reuse going forward
        Assert.assertEquals(qs.getNextAnswerIndex(),3);  // this should have stepped back one
        
        Assert.assertTrue(qs.canBackUp());
        // move forward the same path, ensure prior set answers are
        // right
        try {
        	existingAnswerToNextQuestion = qs.answerQuestion("40");
        }
        catch(AvatolCVException e){
            Assert.fail(e.getMessage());
        }
        
        Assert.assertEquals(existingAnswerToNextQuestion,"NOT_YET_SPECIFIED");
        Assert.assertEquals(qs.getNextAnswerIndex(),4);
        Assert.assertTrue(qs.isAllQuestionsAnswered());
        
        Assert.assertTrue(qs.getAnsweredQuestions().size() == 3);
        Assert.assertEquals(qs.getAnsweredQuestions().get(1).getQuestionID(), "BREADBOX");
        Assert.assertEquals(qs.getAnsweredQuestions().get(2).getQuestionID(), "COLOR");
        Assert.assertEquals(qs.getAnsweredQuestions().get(3).getQuestionID(), "COVER_PERCENT");
        Assert.assertEquals(qs.getAnsweredQuestions().get(1).getAnswer(), "yes");
        Assert.assertEquals(qs.getAnsweredQuestions().get(2).getAnswer(), "red");
        Assert.assertEquals(qs.getAnsweredQuestions().get(3).getAnswer(), "40");
        Assert.assertTrue(qs.isAllQuestionsAnswered());
    }

    public void testQuestionSequencerCASE_Keep4(){
        // CASE_KEEP4 forward through last question, back up two, then forward
        QuestionsXMLFile xmlFile = getQuestionsXMLFile();
        QQuestions qquestions = null;
        QuestionSequencer qs = null;
        try {
            qquestions = new QQuestions(xmlFile.getDomNode());
            qs = new QuestionSequencer(qquestions.getQuestions());
        }
        catch(AvatolCVException e){
            Assert.fail(e.getMessage());
        }
        String existingAnswerToNextQuestion = null;
        try {
            existingAnswerToNextQuestion = qs.answerQuestion("yes");
            existingAnswerToNextQuestion = qs.answerQuestion("red");
            existingAnswerToNextQuestion = qs.answerQuestion("40");
        }
        catch(AvatolCVException e){
            Assert.fail(e.getMessage());
        }
        
        Assert.assertEquals(existingAnswerToNextQuestion,"NOT_YET_SPECIFIED");
        Assert.assertTrue(qs.isAllQuestionsAnswered());
        
        // step backward 
        Assert.assertTrue(qs.canBackUp());
        try {
            prevAnswer = qs.backUp();
        }
        catch(AvatolCVException e){
            Assert.fail(e.getMessage());
        }
        Assert.assertEquals(prevAnswer.getQuestionID(),"COVER_PERCENT");
        Assert.assertEquals(prevAnswer.getAnswer(),"40");
        question3 = qs.getCurrentQuestion();
        Assert.assertEquals(question3.getId(),"COVER_PERCENT");
        
        Assert.assertTrue(qs.getAnsweredQuestions().size() == 3);
        Assert.assertEquals(qs.getAnsweredQuestions().get(1).getQuestionID(), "BREADBOX");
        Assert.assertEquals(qs.getAnsweredQuestions().get(2).getQuestionID(), "COLOR");
        Assert.assertEquals(qs.getAnsweredQuestions().get(3).getQuestionID(), "COVER_PERCENT");
        Assert.assertEquals(qs.getAnsweredQuestions().get(1).getAnswer(), "yes");
        Assert.assertEquals(qs.getAnsweredQuestions().get(2).getAnswer(), "red");
        Assert.assertEquals(qs.getAnsweredQuestions().get(3).getAnswer(), "40"); // answer remains if we need to reuse going forward
        Assert.assertEquals(qs.getNextAnswerIndex(),3);  // this should have stepped back one
        
        // step backward
        Assert.assertTrue(qs.canBackUp());
        try {
            prevAnswer = qs.backUp();
        }
        catch(AvatolCVException e){
            Assert.fail(e.getMessage());
        }
        Assert.assertEquals(prevAnswer.getQuestionID(),"COLOR");
        Assert.assertEquals(prevAnswer.getAnswer(),"red");
        QQuestion question2 = qs.getCurrentQuestion();
        Assert.assertEquals(question2.getId(),"COLOR");
        
        Assert.assertTrue(qs.getAnsweredQuestions().size() == 3);
        Assert.assertEquals(qs.getAnsweredQuestions().get(1).getQuestionID(), "BREADBOX");
        Assert.assertEquals(qs.getAnsweredQuestions().get(2).getQuestionID(), "COLOR");
        Assert.assertEquals(qs.getAnsweredQuestions().get(3).getQuestionID(), "COVER_PERCENT");
        Assert.assertEquals(qs.getAnsweredQuestions().get(1).getAnswer(), "yes");
        Assert.assertEquals(qs.getAnsweredQuestions().get(2).getAnswer(), "red");
        Assert.assertEquals(qs.getAnsweredQuestions().get(3).getAnswer(), "40"); 
        Assert.assertEquals(qs.getNextAnswerIndex(),2);  // this should have stepped back one
        
        Assert.assertTrue(qs.canBackUp());
        // move forward the same path, ensure prior set answers are
        // right
        try {
            existingAnswerToNextQuestion = qs.answerQuestion("red");
        }
        catch(AvatolCVException e){
            Assert.fail(e.getMessage());
        }
        Assert.assertEquals(existingAnswerToNextQuestion,"40");
        Assert.assertEquals(qs.getNextAnswerIndex(),3);
        Assert.assertFalse(qs.isAllQuestionsAnswered());
        QQuestion question3 = qs.getCurrentQuestion();
        Assert.assertEquals(question3.getId(),"COVER_PERCENT");
        
        try {
            existingAnswerToNextQuestion = qs.answerQuestion("40");
        }
        catch(AvatolCVException e){
            Assert.fail(e.getMessage());
        }
        Assert.assertEquals(existingAnswerToNextQuestion,"NOT_YET_SPECIFIED");
        Assert.assertEquals(qs.getNextAnswerIndex(),4);
        Assert.assertTrue(qs.isAllQuestionsAnswered());
        
        Assert.assertTrue(qs.getAnsweredQuestions().size() == 3);
        Assert.assertEquals(qs.getAnsweredQuestions().get(1).getQuestionID(), "BREADBOX");
        Assert.assertEquals(qs.getAnsweredQuestions().get(2).getQuestionID(), "COLOR");
        Assert.assertEquals(qs.getAnsweredQuestions().get(3).getQuestionID(), "COVER_PERCENT");
        Assert.assertEquals(qs.getAnsweredQuestions().get(1).getAnswer(), "yes");
        Assert.assertEquals(qs.getAnsweredQuestions().get(2).getAnswer(), "red");
        Assert.assertEquals(qs.getAnsweredQuestions().get(3).getAnswer(), "40");
        Assert.assertTrue(qs.isAllQuestionsAnswered());
    }

    public void testQuestionSequencerCASE_Keep5(){
     // CASE_KEEP5 forward through last question, back up all the way, then forward
        QuestionsXMLFile xmlFile = getQuestionsXMLFile();
        QQuestions qquestions = null;
        QuestionSequencer qs = null;
        try {
            qquestions = new QQuestions(xmlFile.getDomNode());
            qs = new QuestionSequencer(qquestions.getQuestions()); 
        }
        catch(AvatolCVException e){
            Assert.fail(e.getMessage());
        }
        
        String existingAnswerToNextQuestion = null;
        try {
            existingAnswerToNextQuestion = qs.answerQuestion("yes");
            existingAnswerToNextQuestion = qs.answerQuestion("red");
            existingAnswerToNextQuestion = qs.answerQuestion("40");
        }
        catch(AvatolCVException e){
            Assert.fail(e.getMessage());
        }
        
        Assert.assertEquals(existingAnswerToNextQuestion,"NOT_YET_SPECIFIED");
        Assert.assertTrue(qs.isAllQuestionsAnswered());
        
        // step backward 
        Assert.assertTrue(qs.canBackUp());
        try {
            AnsweredQuestion prevAnswer = qs.backUp();
        }
        catch(AvatolCVException e){
            Assert.fail(e.getMessage());
        }
        Assert.assertEquals(prevAnswer.getQuestionID(),"COVER_PERCENT");
        Assert.assertEquals(prevAnswer.getAnswer(),"40");
        QQuestion question3 = qs.getCurrentQuestion();
        Assert.assertEquals(question3.getId(),"COVER_PERCENT");
        
        Assert.assertTrue(qs.getAnsweredQuestions().size() == 3);
        Assert.assertEquals(qs.getAnsweredQuestions().get(1).getQuestionID(), "BREADBOX");
        Assert.assertEquals(qs.getAnsweredQuestions().get(2).getQuestionID(), "COLOR");
        Assert.assertEquals(qs.getAnsweredQuestions().get(3).getQuestionID(), "COVER_PERCENT");
        Assert.assertEquals(qs.getAnsweredQuestions().get(1).getAnswer(), "yes");
        Assert.assertEquals(qs.getAnsweredQuestions().get(2).getAnswer(), "red");
        Assert.assertEquals(qs.getAnsweredQuestions().get(3).getAnswer(), "40"); // answer remains if we need to reuse going forward
        Assert.assertEquals(qs.getNextAnswerIndex(),3);  // this should have stepped back one
        
        // step backward
        Assert.assertTrue(qs.canBackUp());
        try {
            prevAnswer = qs.backUp();
        }
        catch(AvatolCVException e){
            Assert.fail(e.getMessage());
        }
        Assert.assertEquals(prevAnswer.getQuestionID(),"COLOR");
        Assert.assertEquals(prevAnswer.getAnswer(),"red");
        QQuestion question2 = qs.getCurrentQuestion();
        Assert.assertEquals(question2.getId(),"COLOR");
        
        Assert.assertTrue(qs.getAnsweredQuestions().size() == 3);
        Assert.assertEquals(qs.getAnsweredQuestions().get(1).getQuestionID(), "BREADBOX");
        Assert.assertEquals(qs.getAnsweredQuestions().get(2).getQuestionID(), "COLOR");
        Assert.assertEquals(qs.getAnsweredQuestions().get(3).getQuestionID(), "COVER_PERCENT");
        Assert.assertEquals(qs.getAnsweredQuestions().get(1).getAnswer(), "yes");
        Assert.assertEquals(qs.getAnsweredQuestions().get(2).getAnswer(), "red");
        Assert.assertEquals(qs.getAnsweredQuestions().get(3).getAnswer(), "40"); 
        Assert.assertEquals(qs.getNextAnswerIndex(),2);  // this should have stepped back one
        
        // step backward
        Assert.assertTrue(qs.canBackUp());
        try {
            prevAnswer = qs.backUp();
        }
        catch(AvatolCVException e){
            Assert.fail(e.getMessage());
        }
        Assert.assertEquals(prevAnswer.getQuestionID(),"BREADBOX");
        Assert.assertEquals(prevAnswer.getAnswer(),"yes");
        QQuestion question1 = qs.getCurrentQuestion();
        Assert.assertEquals(question1.getId(),"BREADBOX");
        
        Assert.assertTrue(qs.getAnsweredQuestions().size() == 3);
        Assert.assertEquals(qs.getAnsweredQuestions().get(1).getQuestionID(), "BREADBOX");
        Assert.assertEquals(qs.getAnsweredQuestions().get(2).getQuestionID(), "COLOR");
        Assert.assertEquals(qs.getAnsweredQuestions().get(3).getQuestionID(), "COVER_PERCENT");
        Assert.assertEquals(qs.getAnsweredQuestions().get(1).getAnswer(), "yes");
        Assert.assertEquals(qs.getAnsweredQuestions().get(2).getAnswer(), "red");
        Assert.assertEquals(qs.getAnsweredQuestions().get(3).getAnswer(), "40"); 
        Assert.assertEquals(qs.getNextAnswerIndex(),1);  // this should have stepped back one
        
        Assert.assertFalse(qs.canBackUp());
        // move forward the same path, ensure prior set answers are
        // right
        try {
            existingAnswerToNextQuestion = qs.answerQuestion("yes");
        }
        catch(AvatolCVException e){
            Assert.fail(e.getMessage());
        }
        
        Assert.assertEquals(existingAnswerToNextQuestion,"red");
        Assert.assertEquals(qs.getNextAnswerIndex(),2);
        Assert.assertFalse(qs.isAllQuestionsAnswered());
        question2 = qs.getCurrentQuestion();
        Assert.assertEquals(question2.getId(),"COLOR");
        
        try {
            existingAnswerToNextQuestion = qs.answerQuestion("red");
        }
        catch(AvatolCVException e){
            Assert.fail(e.getMessage());
        }
        
        Assert.assertEquals(existingAnswerToNextQuestion,"40");
        Assert.assertEquals(qs.getNextAnswerIndex(),3);
        Assert.assertFalse(qs.isAllQuestionsAnswered());
        question3 = qs.getCurrentQuestion();
        Assert.assertEquals(question3.getId(),"COVER_PERCENT");
        
        try {
            existingAnswerToNextQuestion = qs.answerQuestion("40");
        }
        catch(AvatolCVException e){
            Assert.fail(e.getMessage());
        }
        
        Assert.assertEquals(existingAnswerToNextQuestion,"NOT_YET_SPECIFIED");
        Assert.assertEquals(qs.getNextAnswerIndex(),4);
        Assert.assertTrue(qs.isAllQuestionsAnswered());
        
        Assert.assertTrue(qs.getAnsweredQuestions().size() == 3);
        Assert.assertEquals(qs.getAnsweredQuestions().get(1).getQuestionID(), "BREADBOX");
        Assert.assertEquals(qs.getAnsweredQuestions().get(2).getQuestionID(), "COLOR");
        Assert.assertEquals(qs.getAnsweredQuestions().get(3).getQuestionID(), "COVER_PERCENT");
        Assert.assertEquals(qs.getAnsweredQuestions().get(1).getAnswer(), "yes");
        Assert.assertEquals(qs.getAnsweredQuestions().get(2).getAnswer(), "red");
        Assert.assertEquals(qs.getAnsweredQuestions().get(3).getAnswer(), "40");
    }



    public void testQuestionSequencerCASE_Change1(){
        // CASE_CHANGE1 forward one question, back up, then forward,
        // change answer
        QuestionsXMLFile xmlFile = getQuestionsXMLFile();
        QQuestions qquestions = null;
        QuestionSequencer qs = null;
        try {
            qquestions = new QQuestions(xmlFile.getDomNode());
            qs = new QuestionSequencer(qquestions.getQuestions());
        }
        catch(AvatolCVException e){
            Assert.fail(e.getMessage());
        }
        String existingAnswerToNextQuestion = null;
        try {
            existingAnswerToNextQuestion = qs.answerQuestion("yes");
        }
        catch(AvatolCVException e){
            Assert.fail(e.getMessage());
        }
        
        Assert.assertEquals(existingAnswerToNextQuestion,"NOT_YET_SPECIFIED");
        Assert.assertFalse(qs.isAllQuestionsAnswered());
        
        // step backward 
        Assert.assertTrue(qs.canBackUp());
        try {
            prevAnswer = qs.backUp();	
        }
        catch(AvatolCVException e){
            Assert.fail(e.getMessage());
        }
        Assert.assertEquals(prevAnswer.getQuestionID(),"BREADBOX");
        Assert.assertEquals(prevAnswer.getAnswer(),"yes");
        QQuestion question2 = qs.getCurrentQuestion();
        Assert.assertEquals(question2.getId(),"BREADBOX");
        
        Assert.assertTrue(qs.getAnsweredQuestions().size() == 1);
        Assert.assertEquals(qs.getAnsweredQuestions().get(1).getQuestionID(), "BREADBOX");
        Assert.assertEquals(qs.getAnsweredQuestions().get(1).getAnswer(), "yes");
        Assert.assertEquals(qs.getNextAnswerIndex(),1);  // this should have stepped back one
        
        Assert.assertFalse(qs.canBackUp());
        // move forward a different path, ensure prior set answers are
        // flushed
        
        try {
            existingAnswerToNextQuestion = qs.answerQuestion("no");
        }
        catch(AvatolCVException e){
            Assert.fail(e.getMessage());
        }
        Assert.assertEquals(existingAnswerToNextQuestion,"NOT_YET_SPECIFIED");
        Assert.assertEquals(qs.getNextAnswerIndex(),2);
        Assert.assertFalse(qs.isAllQuestionsAnswered());
        question2 = qs.getCurrentQuestion();
        Assert.assertEquals(question2.getId(),"WEIGHT");
        Assert.assertTrue(qs.getAnsweredQuestions().size() == 1);
        
        Assert.assertEquals(qs.getAnsweredQuestions().get(1).getQuestionID(), "BREADBOX");
        Assert.assertEquals(qs.getAnsweredQuestions().get(1).getAnswer(), "no");
    }

    public void testQuestionSequencerCASE_Change2(){
     // CASE_CHANGE2 forward two questions, back up one, then forward,
        // change answer
        QuestionsXMLFile xmlFile = getQuestionsXMLFile();
        QQuestions qquestions = null;
        QuestionSequencer qs = null;
        try {
            qquestions = new QQuestions(xmlFile.getDomNode());
            qs = new QuestionSequencer(qquestions.getQuestions());
        }
        catch(AvatolCVException e){
            Assert.fail(e.getMessage());
        }
        String existingAnswerToNextQuestion = null;
        try {
            existingAnswerToNextQuestion = qs.answerQuestion("yes");
            existingAnswerToNextQuestion = qs.answerQuestion("red");
        }
        catch(AvatolCVException e){
            Assert.fail(e.getMessage());
        }
        
        Assert.assertEquals(existingAnswerToNextQuestion,"NOT_YET_SPECIFIED");
        Assert.assertFalse(qs.isAllQuestionsAnswered());
        
        // step backward 
        Assert.assertTrue(qs.canBackUp());
        try {
            prevAnswer = qs.backUp();
        }
        catch(AvatolCVException e){
            Assert.fail(e.getMessage());
        }
        Assert.assertEquals(prevAnswer.getQuestionID(),"COLOR");
        Assert.assertEquals(prevAnswer.getAnswer(),"red");
        QQuestion question2 = qs.getCurrentQuestion();
        Assert.assertEquals(question2.getId(),"COLOR");
        
        Assert.assertTrue(qs.getAnsweredQuestions().size() == 2);
        Assert.assertEquals(qs.getAnsweredQuestions().get(1).getQuestionID(), "BREADBOX");
        Assert.assertEquals(qs.getAnsweredQuestions().get(2).getQuestionID(), "COLOR");
        Assert.assertEquals(qs.getAnsweredQuestions().get(1).getAnswer(), "yes");
        Assert.assertEquals(qs.getAnsweredQuestions().get(2).getAnswer(), "red");
        Assert.assertEquals(qs.getNextAnswerIndex(),2);  // this should have stepped back one
        
        Assert.assertTrue(qs.canBackUp());
        // move forward a different path, ensure prior set answers are
        // flushed

        try {
            existingAnswerToNextQuestion = qs.answerQuestion("blue");
        }
        catch(AvatolCVException e){
            Assert.fail(e.getMessage());
        }
        Assert.assertEquals(existingAnswerToNextQuestion,"NOT_YET_SPECIFIED");
        Assert.assertEquals(qs.getNextAnswerIndex(),3);
        Assert.assertFalse(qs.isAllQuestionsAnswered());
        QQuestion question3 = qs.getCurrentQuestion();
        Assert.assertEquals(question3.getId(),"RESOLUTION");
        Assert.assertTrue(qs.getAnsweredQuestions().size() == 2);
        Assert.assertEquals(qs.getAnsweredQuestions().get(1).getQuestionID(), "BREADBOX");
        Assert.assertEquals(qs.getAnsweredQuestions().get(2).getQuestionID(), "COLOR");
        Assert.assertEquals(qs.getAnsweredQuestions().get(1).getAnswer(), "yes");
        Assert.assertEquals(qs.getAnsweredQuestions().get(2).getAnswer(), "blue");
    }

    public void testQuestionSequencerCASE_Change3(){
     // CASE_CHANGE3 forward through last question, back up one, then forward,
        // change answer
        QuestionsXMLFile xmlFile = getQuestionsXMLFile();
        QQuestions qquestions = null;
        QuestionSequencer qs = null;
        try {
            qquestions = new QQuestions(xmlFile.getDomNode());
            qs = new QuestionSequencer(qquestions.getQuestions());
        }
        catch(AvatolCVException e){
            Assert.fail(e.getMessage());
        }
        String existingAnswerToNextQuestion = null;
        try {
            existingAnswerToNextQuestion = qs.answerQuestion("yes");
            existingAnswerToNextQuestion = qs.answerQuestion("red");
            existingAnswerToNextQuestion = qs.answerQuestion("40");
        }
        catch(AvatolCVException e){
            Assert.fail(e.getMessage());
        }
        
        Assert.assertEquals(existingAnswerToNextQuestion,"NOT_YET_SPECIFIED");
        Assert.assertTrue(qs.isAllQuestionsAnswered());
        
        // step backward 
        Assert.assertTrue(qs.canBackUp());
        try {
            prevAnswer = qs.backUp();
        }
        catch(AvatolCVException e){
            Assert.fail(e.getMessage());
        }
        Assert.assertEquals(prevAnswer.getQuestionID(),"COVER_PERCENT");
        Assert.assertEquals(prevAnswer.getAnswer(),"40");
        question3 = qs.getCurrentQuestion();
        Assert.assertEquals(question3.getId(),"COVER_PERCENT");
        
        Assert.assertTrue(qs.getAnsweredQuestions().size() == 3);
        Assert.assertEquals(qs.getAnsweredQuestions().get(1).getQuestionID(), "BREADBOX");
        Assert.assertEquals(qs.getAnsweredQuestions().get(2).getQuestionID(), "COLOR");
        Assert.assertEquals(qs.getAnsweredQuestions().get(3).getQuestionID(), "COVER_PERCENT");
        Assert.assertEquals(qs.getAnsweredQuestions().get(1).getAnswer(), "yes");
        Assert.assertEquals(qs.getAnsweredQuestions().get(2).getAnswer(), "red");
        Assert.assertEquals(qs.getAnsweredQuestions().get(3).getAnswer(), "40"); // answer remains if we need to reuse going forward
        Assert.assertEquals(qs.getNextAnswerIndex(),3);  // this should have stepped back one
        
        Assert.assertTrue(qs.canBackUp());
        // move forward a different path, ensure prior set answers are
        // flushed

        try {
            existingAnswerToNextQuestion = qs.answerQuestion("50");
        }
        catch(AvatolCVException e){
            Assert.fail(e.getMessage());
        }
        Assert.assertEquals(existingAnswerToNextQuestion,"NOT_YET_SPECIFIED");
        Assert.assertEquals(qs.getNextAnswerIndex(),4);
        Assert.assertTrue(qs.isAllQuestionsAnswered());
        question4 = qs.getCurrentQuestion();
        Assert.assertEquals(question4.getId(),"NO_MORE_QUESTIONS");
        Assert.assertTrue(qs.getAnsweredQuestions().size() == 3);
        
        Assert.assertEquals(qs.getAnsweredQuestions().get(1).getQuestionID(), "BREADBOX");
        Assert.assertEquals(qs.getAnsweredQuestions().get(2).getQuestionID(), "COLOR");
        Assert.assertEquals(qs.getAnsweredQuestions().get(3).getQuestionID(), "COVER_PERCENT");
        Assert.assertEquals(qs.getAnsweredQuestions().get(1).getAnswer(), "yes");
        Assert.assertEquals(qs.getAnsweredQuestions().get(2).getAnswer(), "red");
        Assert.assertEquals(qs.getAnsweredQuestions().get(3).getAnswer(), "50");
    }

    public void testQuestionSequencerCASE_Change4(){
        // CASE_CHANGE4 forward through last question, back up two, then forward,
        // change answer
        QuestionsXMLFile xmlFile = getQuestionsXMLFile();
        QQuestions qquestions = null;
        QuestionSequencer qs = null;
        try {
            qquestions = new QQuestions(xmlFile.getDomNode());
            qs = new QuestionSequencer(qquestions.getQuestions());
        }
        catch(AvatolCVException e){
            Assert.fail(e.getMessage());
        }
        String existingAnswerToNextQuestion = null;
        try {
            existingAnswerToNextQuestion = qs.answerQuestion("yes");
            existingAnswerToNextQuestion = qs.answerQuestion("red");
            existingAnswerToNextQuestion = qs.answerQuestion("40");
        }
        catch(AvatolCVException e){
            Assert.fail(e.getMessage());
        }
        
        Assert.assertEquals(existingAnswerToNextQuestion,"NOT_YET_SPECIFIED");
        Assert.assertTrue(qs.isAllQuestionsAnswered());
        
        // step backward 
        Assert.assertTrue(qs.canBackUp());
        try {
            prevAnswer = qs.backUp();
        }
        catch(AvatolCVException e){
            Assert.fail(e.getMessage());
        }
        Assert.assertEquals(prevAnswer.getQuestionID(),"COVER_PERCENT");
        Assert.assertEquals(prevAnswer.getAnswer(),"40");
        QQuestion question3 = qs.getCurrentQuestion();
        Assert.assertEquals(question3.getId(),"COVER_PERCENT");
        
        Assert.assertTrue(qs.getAnsweredQuestions().size() == 3);
        Assert.assertEquals(qs.getAnsweredQuestions().get(1).getQuestionID(), "BREADBOX");
        Assert.assertEquals(qs.getAnsweredQuestions().get(2).getQuestionID(), "COLOR");
        Assert.assertEquals(qs.getAnsweredQuestions().get(3).getQuestionID(), "COVER_PERCENT");
        Assert.assertEquals(qs.getAnsweredQuestions().get(1).getAnswer(), "yes");
        Assert.assertEquals(qs.getAnsweredQuestions().get(2).getAnswer(), "red");
        Assert.assertEquals(qs.getAnsweredQuestions().get(3).getAnswer(), "40"); // answer remains if we need to reuse going forward
        Assert.assertEquals(qs.getNextAnswerIndex(),3);  // this should have stepped back one
        
        // step backward
        Assert.assertTrue(qs.canBackUp());
        try {
            prevAnswer = qs.backUp();
        }
        catch(AvatolCVException e){
            Assert.fail(e.getMessage());
        }
        Assert.assertEquals(prevAnswer.getQuestionID(),"COLOR");
        Assert.assertEquals(prevAnswer.getAnswer(),"red");
        question2 = qs.getCurrentQuestion();
        Assert.assertEquals(question2.getId(),"COLOR");
        
        Assert.assertTrue(qs.getAnsweredQuestions().size() == 3);
        Assert.assertEquals(qs.getAnsweredQuestions().get(1).getQuestionID(), "BREADBOX");
        Assert.assertEquals(qs.getAnsweredQuestions().get(2).getQuestionID(), "COLOR");
        Assert.assertEquals(qs.getAnsweredQuestions().get(3).getQuestionID(), "COVER_PERCENT");
        Assert.assertEquals(qs.getAnsweredQuestions().get(1).getAnswer(), "yes");
        Assert.assertEquals(qs.getAnsweredQuestions().get(2).getAnswer(), "red");
        Assert.assertEquals(qs.getAnsweredQuestions().get(3).getAnswer(), "40"); 
        Assert.assertEquals(qs.getNextAnswerIndex(),2);  // this should have stepped back one
        
        
        Assert.assertTrue(qs.canBackUp());
        // move forward a different path, ensure prior set answers are
        // flushed
        
        try {
            existingAnswerToNextQuestion = qs.answerQuestion("blue");
        }
        catch(AvatolCVException e){
            Assert.fail(e.getMessage());
        }
        Assert.assertEquals(existingAnswerToNextQuestion,"NOT_YET_SPECIFIED");
        Assert.assertEquals(qs.getNextAnswerIndex(),3);
        Assert.assertFalse(qs.isAllQuestionsAnswered());
        question3 = qs.getCurrentQuestion();
        Assert.assertEquals(question3.getId(),"RESOLUTION");
        Assert.assertTrue(qs.getAnsweredQuestions().size() == 2);
        
        Assert.assertEquals(qs.getAnsweredQuestions().get(1).getQuestionID(), "BREADBOX");
        Assert.assertEquals(qs.getAnsweredQuestions().get(2).getQuestionID(), "COLOR");
        Assert.assertEquals(qs.getAnsweredQuestions().get(1).getAnswer(), "yes");
        Assert.assertEquals(qs.getAnsweredQuestions().get(2).getAnswer(), "blue");
    }

    public void testQuestionSequencerCASE_Change5(){
     // CASE_CHANGE5 forward through last question, back up all the way, then forward,
        // change answer
        QuestionsXMLFile xmlFile = getQuestionsXMLFile();
        QQuestions qquestions = null;
        QuestionSequencer qs = null;
        try {
            qquestions = new QQuestions(xmlFile.getDomNode());
            qs = new QuestionSequencer(qquestions.getQuestions());
        }
        catch(AvatolCVException e){
            Assert.fail(e.getMessage());
        }
        String existingAnswerToNextQuestion = null;
        try {
            existingAnswerToNextQuestion = qs.answerQuestion("yes");
            existingAnswerToNextQuestion = qs.answerQuestion("red");
            existingAnswerToNextQuestion = qs.answerQuestion("40");
        }
        catch(AvatolCVException e){
            Assert.fail(e.getMessage());
        }
        
        Assert.assertEquals(existingAnswerToNextQuestion,"NOT_YET_SPECIFIED");
        Assert.assertTrue(qs.isAllQuestionsAnswered());
        
        // step backward 
        Assert.assertTrue(qs.canBackUp());
        try {
        	prevAnswer = qs.backUp();
        }
        catch(AvatolCVException e){
            Assert.fail(e.getMessage());
        }
        Assert.assertEquals(prevAnswer.getQuestionID(),"COVER_PERCENT");
        Assert.assertEquals(prevAnswer.getAnswer(),"40");
        QQuestion question3 = qs.getCurrentQuestion();
        Assert.assertEquals(question3.getId(),"COVER_PERCENT");
        
        Assert.assertTrue(qs.getAnsweredQuestions().size() == 3);
        Assert.assertEquals(qs.getAnsweredQuestions().get(1).getQuestionID(), "BREADBOX");
        Assert.assertEquals(qs.getAnsweredQuestions().get(2).getQuestionID(), "COLOR");
        Assert.assertEquals(qs.getAnsweredQuestions().get(3).getQuestionID(), "COVER_PERCENT");
        Assert.assertEquals(qs.getAnsweredQuestions().get(1).getAnswer(), "yes");
        Assert.assertEquals(qs.getAnsweredQuestions().get(2).getAnswer(), "red");
        Assert.assertEquals(qs.getAnsweredQuestions().get(3).getAnswer(), "40"); // answer remains if we need to reuse going forward
        Assert.assertEquals(qs.getNextAnswerIndex(),3);  // this should have stepped back one
        
        // step backward
        Assert.assertTrue(qs.canBackUp());
        try {
        	prevAnswer = qs.backUp();
        }
        catch(AvatolCVException e){
            Assert.fail(e.getMessage());
        }
        Assert.assertEquals(prevAnswer.getQuestionID(),"COLOR");
        Assert.assertEquals(prevAnswer.getAnswer(),"red");
        QQuestion question2 = qs.getCurrentQuestion();
        Assert.assertEquals(question2.getId(),"COLOR");
        
        Assert.assertTrue(qs.getAnsweredQuestions().size() == 3);
        Assert.assertEquals(qs.getAnsweredQuestions().get(1).getQuestionID(), "BREADBOX");
        Assert.assertEquals(qs.getAnsweredQuestions().get(2).getQuestionID(), "COLOR");
        Assert.assertEquals(qs.getAnsweredQuestions().get(3).getQuestionID(), "COVER_PERCENT");
        Assert.assertEquals(qs.getAnsweredQuestions().get(1).getAnswer(), "yes");
        Assert.assertEquals(qs.getAnsweredQuestions().get(2).getAnswer(), "red");
        Assert.assertEquals(qs.getAnsweredQuestions().get(3).getAnswer(), "40"); 
        Assert.assertEquals(qs.getNextAnswerIndex(),2);  // this should have stepped back one
        
        // step backward
        Assert.assertTrue(qs.canBackUp());
        try {
        	prevAnswer = qs.backUp();
        }
        catch(AvatolCVException e){
            Assert.fail(e.getMessage());
        }
        Assert.assertEquals(prevAnswer.getQuestionID(),"BREADBOX");
        Assert.assertEquals(prevAnswer.getAnswer(),"yes");
        QQuestion question1 = qs.getCurrentQuestion();
        Assert.assertEquals(question1.getId(),"BREADBOX");
        
        Assert.assertTrue(qs.getAnsweredQuestions().size() == 3);
        Assert.assertEquals(qs.getAnsweredQuestions().get(1).getQuestionID(), "BREADBOX");
        Assert.assertEquals(qs.getAnsweredQuestions().get(2).getQuestionID(), "COLOR");
        Assert.assertEquals(qs.getAnsweredQuestions().get(3).getQuestionID(), "COVER_PERCENT");
        Assert.assertEquals(qs.getAnsweredQuestions().get(1).getAnswer(), "yes");
        Assert.assertEquals(qs.getAnsweredQuestions().get(2).getAnswer(), "red");
        Assert.assertEquals(qs.getAnsweredQuestions().get(3).getAnswer(), "40"); 
        Assert.assertEquals(qs.getNextAnswerIndex(),1);  // this should have stepped back one
        
        Assert.assertFalse(qs.canBackUp());
        // move forward a different path, ensure prior set answers are
        // flushed
        try {
        	existingAnswerToNextQuestion = qs.answerQuestion("no");
        }
        catch(AvatolCVException e){
            Assert.fail(e.getMessage());
        }
        
        Assert.assertEquals(existingAnswerToNextQuestion,"NOT_YET_SPECIFIED");
        Assert.assertEquals(qs.getNextAnswerIndex(),2);
        Assert.assertFalse(qs.isAllQuestionsAnswered());
        question2 = qs.getCurrentQuestion();
        Assert.assertEquals(question2.getId(),"WEIGHT");
        Assert.assertTrue(qs.getAnsweredQuestions().size() == 1);
        
        Assert.assertEquals(qs.getAnsweredQuestions().get(1).getQuestionID(), "BREADBOX");
        Assert.assertEquals(qs.getAnsweredQuestions().get(1).getAnswer(), "no");
    }


    public void testQuestionsXmlFile(){
        QuestionsXMLFile xmlFile = getQuestionsXMLFile();
        QQuestions qquestions = null;
        try {
            qquestions = new QQuestions(xmlFile.getDomNode());
        }
        catch(AvatolCVException e){
            Assert.fail(e.getMessage());
        }
       List<QQuestion> theQuestions = qquestions.getQuestions();
        QQuestion question1 = theQuestions.get(0);
        Assert.assertEquals(question1.getType(),"choice");
        Assert.assertEquals(question1.getId(),"BREADBOX");
        Assert.assertEquals(question1.getText(),"Is it bigger than a breadbox or is it approximately the same size? (Use your own judgement when deciding on the approximate size.  Everything is relative, you know.");
        Assert.assertEquals(question1.getAnswers().get(0).getValue(),"yes");
        Assert.assertEquals(question1.getAnswers().get(0).getNextQuestion(),"COLOR");
        Assert.assertEquals(question1.getAnswers().get(1).getValue(),"no");
        Assert.assertEquals(question1.getAnswers().get(1).getNextQuestion(),"WEIGHT");
        Assert.assertEquals(question1.getImages().get(0).getPath(),"data/questionnaire/images/elephant.jpg");
        Assert.assertEquals(question1.getImages().get(0).getCaption(),"elephants are bigger than a breadbox");
        Assert.assertEquals(question1.getImages().get(1).getPath(),"data/questionnaire/images/mouse.jpg");
        Assert.assertEquals(question1.getImages().get(1).getCaption(),"mice are smaller than a breadbox");
        
        question2 = theQuestions.get(2);
        Assert.assertEquals(question2.getType(),"choice");
        Assert.assertEquals(question2.getId(),"COLOR");
        Assert.assertEquals(question2.getText(),"What color is the larger-than-breadbox item?");
        Assert.assertEquals(question2.getAnswers().get(1).getValue(),"red");
        Assert.assertEquals(question2.getAnswers().get(1).getNextQuestion(),"COVER_PERCENT");
        Assert.assertEquals(question2.getAnswers().get(2).getValue(),"green");
        Assert.assertEquals(question2.getAnswers().get(2).getNextQuestion(),"COVER_PERCENT");
        Assert.assertEquals(question2.getAnswers().get(3).getValue(),"blue");
        Assert.assertEquals(question2.getAnswers().get(3).getNextQuestion(),"RESOLUTION");
         
        question3 = theQuestions.get(3);
        Assert.assertEquals(question3.getType(),"input_integer");
        Assert.assertEquals(question3.getId(),"WEIGHT");
        Assert.assertEquals(question3.getText(),"What is the weight in kilograms of the smaller-than-breadbox item?");
        Assert.assertEquals(question3.getAnswers().get(1).getValue(),"0");
        Assert.assertEquals(question3.getAnswers().get(1).getNextQuestion(),"COVER_PERCENT");
        
        question4 = theQuestions.get(4);
        Assert.assertEquals(question4.getType(),"input_string");
        Assert.assertEquals(question4.getId(),"RESOLUTION");
        Assert.assertEquals(question4.getText(),"What is the resolution of the smaller-than-breadbox item?");
        Assert.assertEquals(question4.getAnswers().get(1).getValue(),"");
        Assert.assertEquals(question4.getAnswers().get(1).getNextQuestion(),"NO_MORE_QUESTIONS");
        
        question5 = theQuestions.get(5);
        Assert.assertEquals(question5.getType(),"input_integer");
        Assert.assertEquals(question5.getId(),"COVER_PERCENT");
        Assert.assertEquals(question5.getText(),"What percent of image is occupied?");
        Assert.assertEquals(question5.getAnswers().get(1).getValue(),"0");
        Assert.assertEquals(question5.getAnswers().get(1).getNextQuestion(),"NO_MORE_QUESTIONS");

    }
}
