package edu.oregonstate.eecs.iis.avatol.questionnaire;

import java.util.List;

import edu.oregonstate.eecs.iis.avatolcv.SystemDependent;
import edu.oregonstate.eecs.iis.avatolcv.SystemDirs;
import edu.oregonstate.eecs.iis.avatolcv.core.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.questionnaire.AnsweredQuestion;
import edu.oregonstate.eecs.iis.avatolcv.questionnaire.QQuestion;
import edu.oregonstate.eecs.iis.avatolcv.questionnaire.QQuestions;
import edu.oregonstate.eecs.iis.avatolcv.questionnaire.QuestionSequencer;
import edu.oregonstate.eecs.iis.avatolcv.questionnaire.QuestionsXMLFile;
import junit.framework.Assert;
import junit.framework.TestCase;

public class TestQuestionSequencer extends TestCase {
    private static final String FILESEP = System.getProperty("file.separator");
    public QuestionsXMLFile getQuestionsXMLFile(){
        SystemDependent sd = new SystemDependent();
        String rootDir = sd.getRootDir();
        SystemDirs re = new SystemDirs(rootDir);
        
        String simpleXMLPath = rootDir + FILESEP + "tests" + FILESEP + "simple.xml";
        QuestionsXMLFile xmlFile = null;
        try {
            xmlFile = QuestionsXMLFile(simpleXMLPath);
        }
        catch(AvatolCVException e){
            Assert.fail("problem instanitation QuestionsXMLFile.");
        }
        return xmlFile;
    }
    public void  testQuestionSequencerCASEForwardSimple(){
        QuestionsXMLFile xmlFile = getQuestionsXMLFile();
        QQuestions qquestions = new QQuestions(xmlFile.getDomNode());
        QuestionSequencer qs = new QuestionSequencer(qquestions);
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
        String existingAnswerToNextQuestion = qs.answerQuestion("yes");
        Assert.assertEquals(existingAnswerToNextQuestion,"NOT_YET_SPECIFIED");
        Assert.assertEquals(qs.getNextAnswerIndex(),2);
        Assert.assertFalse(qs.isAllQuestionsAnswered());
        QQuestion question2 = qs.getCurrentQuestion();
        Assert.assertEquals(question2.getId(),"COLOR");
    
        existingAnswerToNextQuestion = qs.answerQuestion("red");
        Assert.assertEquals(existingAnswerToNextQuestion,"NOT_YET_SPECIFIED");
        Assert.assertEquals(qs.getNextAnswerIndex(),3);
        Assert.assertFalse(qs.isAllQuestionsAnswered());
        QQuestion question3 = qs.getCurrentQuestion();
        Assert.assertEquals(question3.getId(),"COVER_PERCENT");
    
        existingAnswerToNextQuestion = qs.answerQuestion("40");
        Assert.assertEquals(existingAnswerToNextQuestion,"NOT_YET_SPECIFIED");
        Assert.assertEquals(qs.getNextAnswerIndex(),4);
        Assert.assertTrue(qs.isAllQuestionsAnswered());
    
        Assert.assertTrue(length(qs.getAnsweredQuestions().get) == 3);
        Assert.assertEquals(qs.getAnsweredQuestions().get(1).getQuestionID(), "BREADBOX");
        Assert.assertEquals(qs.getAnsweredQuestions().get(2).getQuestionID(), "COLOR");
        Assert.assertEquals(qs.getAnsweredQuestions().get(3).getQuestionID(), "COVER_PERCENT");
        Assert.assertEquals(qs.getAnsweredQuestions().get(1).getAnswer(), "yes");
        Assert.assertEquals(qs.getAnsweredQuestions().get(2).getAnswer(), "red");
        Assert.assertEquals(qs.getAnsweredQuestions().get(3).getAnswer(), "40");
    }

    public void testQuestionSequencerCASE_Keep1(){
        // CASE_KEEP1 forward one question, back up, then forward 
        QuestionsXMLFile xmlFile = getQuestionsXMLFile();
        QQuestions qquestions = new QQuestions(xmlFile.getDomNode());
        QuestionSequencer qs = new QuestionSequencer(qquestions);
        
        String existingAnswerToNextQuestion = qs.answerQuestion("yes");
        Assert.assertEquals(existingAnswerToNextQuestion,"NOT_YET_SPECIFIED");
        Assert.assertFalse(qs.isAllQuestionsAnswered());
        
        // step backward 
        Assert.assertTrue(qs.canBackUp());
        AnsweredQuestion prevAnswer = qs.backUp();
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
        existingAnswerToNextQuestion = qs.answerQuestion("yes");
        Assert.assertEquals(existingAnswerToNextQuestion,"NOT_YET_SPECIFIED");
        Assert.assertEquals(qs.getNextAnswerIndex(),2);
        Assert.assertFalse(qs.isAllQuestionsAnswered());
    }
    
    

    public void testQuestionSequencerCASE_Keep2(){
        // CASE_KEEP2 forward two questions, back up one, then forward
        QuestionsXMLFile xmlFile = getQuestionsXMLFile();
        QQuestions qquestions = new QQuestions(xmlFile.getDomNode());
        QuestionSequencer qs = new QuestionSequencer(qquestions);
        
        existingAnswerToNextQuestion = qs.answerQuestion("yes");
        existingAnswerToNextQuestion = qs.answerQuestion("red");
        Assert.assertEquals(existingAnswerToNextQuestion,"NOT_YET_SPECIFIED");
        Assert.assertFalse(qs.isAllQuestionsAnswered());
        
        // step backward 
        Assert.assertTrue(qs.canBackUp());
        prevAnswer = qs.backUp();
        Assert.assertEquals(prevAnswer.getQuestionID(),"COLOR");
        Assert.assertEquals(prevAnswer.getAnswer(),"red");
        question2 = qs.getCurrentQuestion();
        Assert.assertEquals(question2.getId(),"COLOR");
        
        Assert.assertTrue(length(qs.getAnsweredQuestions().get) == 2);
        Assert.assertEquals(qs.getAnsweredQuestions().get(1).getQuestionID(), "BREADBOX");
        Assert.assertEquals(qs.getAnsweredQuestions().get(2).getQuestionID(), "COLOR");
        Assert.assertEquals(qs.getAnsweredQuestions().get(1).getAnswer(), "yes");
        Assert.assertEquals(qs.getAnsweredQuestions().get(2).getAnswer(), "red");
        Assert.assertEquals(qs.getNextAnswerIndex(),2);  // this should have stepped back one
        
        Assert.assertTrue(qs.canBackUp());
        // move forward the same path, ensure prior set answers are
        // right
        existingAnswerToNextQuestion = qs.answerQuestion("red");
        Assert.assertEquals(existingAnswerToNextQuestion,"NOT_YET_SPECIFIED");
        Assert.assertEquals(qs.getNextAnswerIndex(),3);
        Assert.assertFalse(qs.isAllQuestionsAnswered());
        
    }

    public void testQuestionSequencerCASE_Keep3(){
        // CASE_KEEP3 forward through last question, back up one, then forward
        QuestionsXMLFile xmlFile = getQuestionsXMLFile();
        QQuestions qquestions = new QQuestions(xmlFile.getDomNode());
        QuestionSequencer qs = new QuestionSequencer(qquestions);
        
        existingAnswerToNextQuestion = qs.answerQuestion("yes");
        existingAnswerToNextQuestion = qs.answerQuestion("red");
        existingAnswerToNextQuestion = qs.answerQuestion("40");
        Assert.assertEquals(existingAnswerToNextQuestion,"NOT_YET_SPECIFIED");
        Assert.assertTrue(qs.isAllQuestionsAnswered());
        
        // step backward 
        Assert.assertTrue(qs.canBackUp());
        prevAnswer = qs.backUp();
        Assert.assertEquals(prevAnswer.getQuestionID(),"COVER_PERCENT");
        Assert.assertEquals(prevAnswer.getAnswer(),"40");
        QQuestion question3 = qs.getCurrentQuestion();
        Assert.assertEquals(question3.getId(),"COVER_PERCENT");
        
        Assert.assertTrue(length(qs.getAnsweredQuestions().get) == 3);
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
        existingAnswerToNextQuestion = qs.answerQuestion("40");
        Assert.assertEquals(existingAnswerToNextQuestion,"NOT_YET_SPECIFIED");
        Assert.assertEquals(qs.getNextAnswerIndex(),4);
        Assert.assertTrue(qs.isAllQuestionsAnswered());
        
        Assert.assertTrue(length(qs.getAnsweredQuestions().get) == 3);
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
        QQuestions qquestions = new QQuestions(xmlFile.getDomNode());
        QuestionSequencer qs = new QuestionSequencer(qquestions);
        
        existingAnswerToNextQuestion = qs.answerQuestion("yes");
        existingAnswerToNextQuestion = qs.answerQuestion("red");
        existingAnswerToNextQuestion = qs.answerQuestion("40");
        Assert.assertEquals(existingAnswerToNextQuestion,"NOT_YET_SPECIFIED");
        Assert.assertTrue(qs.isAllQuestionsAnswered());
        
        // step backward 
        Assert.assertTrue(qs.canBackUp());
        prevAnswer = qs.backUp();
        Assert.assertEquals(prevAnswer.getQuestionID(),"COVER_PERCENT");
        Assert.assertEquals(prevAnswer.getAnswer(),"40");
        question3 = qs.getCurrentQuestion();
        Assert.assertEquals(question3.getId(),"COVER_PERCENT");
        
        Assert.assertTrue(length(qs.getAnsweredQuestions().get) == 3);
        Assert.assertEquals(qs.getAnsweredQuestions().get(1).getQuestionID(), "BREADBOX");
        Assert.assertEquals(qs.getAnsweredQuestions().get(2).getQuestionID(), "COLOR");
        Assert.assertEquals(qs.getAnsweredQuestions().get(3).getQuestionID(), "COVER_PERCENT");
        Assert.assertEquals(qs.getAnsweredQuestions().get(1).getAnswer(), "yes");
        Assert.assertEquals(qs.getAnsweredQuestions().get(2).getAnswer(), "red");
        Assert.assertEquals(qs.getAnsweredQuestions().get(3).getAnswer(), "40"); // answer remains if we need to reuse going forward
        Assert.assertEquals(qs.getNextAnswerIndex(),3);  // this should have stepped back one
        
        // step backward
        Assert.assertTrue(qs.canBackUp());
        prevAnswer = qs.backUp();
        Assert.assertEquals(prevAnswer.getQuestionID(),"COLOR");
        Assert.assertEquals(prevAnswer.getAnswer(),"red");
        QQuestion question2 = qs.getCurrentQuestion();
        Assert.assertEquals(question2.getId(),"COLOR");
        
        Assert.assertTrue(length(qs.getAnsweredQuestions().get) == 3);
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
        
        String existingAnswerToNextQuestion = qs.answerQuestion("red");
        Assert.assertEquals(existingAnswerToNextQuestion,"40");
        Assert.assertEquals(qs.getNextAnswerIndex(),3);
        Assert.assertFalse(qs.isAllQuestionsAnswered());
        QQuestion question3 = qs.getCurrentQuestion();
        Assert.assertEquals(question3.getId(),"COVER_PERCENT");
        
        existingAnswerToNextQuestion = qs.answerQuestion("40");
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
        QQuestions qquestions = new QQuestions(xmlFile.getDomNode());
        QuestionSequencer qs = new QuestionSequencer(qquestions);
        
        String existingAnswerToNextQuestion = qs.answerQuestion("yes");
        existingAnswerToNextQuestion = qs.answerQuestion("red");
        existingAnswerToNextQuestion = qs.answerQuestion("40");
        Assert.assertEquals(existingAnswerToNextQuestion,"NOT_YET_SPECIFIED");
        Assert.assertTrue(qs.isAllQuestionsAnswered());
        
        // step backward 
        Assert.assertTrue(qs.canBackUp());
        AnsweredQuestion prevAnswer = qs.backUp();
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
        prevAnswer = qs.backUp();
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
        prevAnswer = qs.backUp();
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
        
        existingAnswerToNextQuestion = qs.answerQuestion("yes");
        Assert.assertEquals(existingAnswerToNextQuestion,"red");
        Assert.assertEquals(qs.getNextAnswerIndex(),2);
        Assert.assertFalse(qs.isAllQuestionsAnswered());
        question2 = qs.getCurrentQuestion();
        Assert.assertEquals(question2.getId(),"COLOR");
        
        existingAnswerToNextQuestion = qs.answerQuestion("red");
        Assert.assertEquals(existingAnswerToNextQuestion,"40");
        Assert.assertEquals(qs.getNextAnswerIndex(),3);
        Assert.assertFalse(qs.isAllQuestionsAnswered());
        question3 = qs.getCurrentQuestion();
        Assert.assertEquals(question3.getId(),"COVER_PERCENT");
        
        existingAnswerToNextQuestion = qs.answerQuestion("40");
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
        QQuestions qquestions = new QQuestions(xmlFile.getDomNode());
        QuestionSequencer qs = new QuestionSequencer(qquestions);
        
        existingAnswerToNextQuestion = qs.answerQuestion("yes");
        Assert.assertEquals(existingAnswerToNextQuestion,"NOT_YET_SPECIFIED");
        Assert.assertFalse(qs.isAllQuestionsAnswered());
        
        // step backward 
        Assert.assertTrue(qs.canBackUp());
        prevAnswer = qs.backUp();
        Assert.assertEquals(prevAnswer.getQuestionID(),"BREADBOX");
        Assert.assertEquals(prevAnswer.getAnswer(),"yes");
        QQuestion question2 = qs.getCurrentQuestion();
        Assert.assertEquals(question2.getId(),"BREADBOX");
        
        Assert.assertTrue(length(qs.getAnsweredQuestions().get) == 1);
        Assert.assertEquals(qs.getAnsweredQuestions().get(1).getQuestionID(), "BREADBOX");
        Assert.assertEquals(qs.getAnsweredQuestions().get(1).getAnswer(), "yes");
        Assert.assertEquals(qs.getNextAnswerIndex(),1);  // this should have stepped back one
        
        Assert.assertFalse(qs.canBackUp());
        // move forward a different path, ensure prior set answers are
        // flushed
        
        existingAnswerToNextQuestion = qs.answerQuestion("no");
        Assert.assertEquals(existingAnswerToNextQuestion,"NOT_YET_SPECIFIED");
        Assert.assertEquals(qs.getNextAnswerIndex(),2);
        Assert.assertFalse(qs.isAllQuestionsAnswered());
        question2 = qs.getCurrentQuestion();
        Assert.assertEquals(question2.getId(),"WEIGHT");
        Assert.assertTrue(length(qs.getAnsweredQuestions().get) == 1);
        
        Assert.assertEquals(qs.getAnsweredQuestions().get(1).getQuestionID(), "BREADBOX");
        Assert.assertEquals(qs.getAnsweredQuestions().get(1).getAnswer(), "no");
    }

    public void testQuestionSequencerCASE_Change2(){
     // CASE_CHANGE2 forward two questions, back up one, then forward,
        // change answer
        QuestionsXMLFile xmlFile = getQuestionsXMLFile();
        QQuestions qquestions = new QQuestions(xmlFile.getDomNode());
        QuestionSequencer qs = new QuestionSequencer(qquestions);
        
        existingAnswerToNextQuestion = qs.answerQuestion("yes");
        existingAnswerToNextQuestion = qs.answerQuestion("red");
        Assert.assertEquals(existingAnswerToNextQuestion,"NOT_YET_SPECIFIED");
        Assert.assertFalse(qs.isAllQuestionsAnswered());
        
        // step backward 
        Assert.assertTrue(qs.canBackUp());
        prevAnswer = qs.backUp();
        Assert.assertEquals(prevAnswer.getQuestionID(),"COLOR");
        Assert.assertEquals(prevAnswer.getAnswer(),"red");
        QQuestion question2 = qs.getCurrentQuestion();
        Assert.assertEquals(question2.getId(),"COLOR");
        
        Assert.assertTrue(length(qs.getAnsweredQuestions().get) == 2);
        Assert.assertEquals(qs.getAnsweredQuestions().get(1).getQuestionID(), "BREADBOX");
        Assert.assertEquals(qs.getAnsweredQuestions().get(2).getQuestionID(), "COLOR");
        Assert.assertEquals(qs.getAnsweredQuestions().get(1).getAnswer(), "yes");
        Assert.assertEquals(qs.getAnsweredQuestions().get(2).getAnswer(), "red");
        Assert.assertEquals(qs.getNextAnswerIndex(),2);  // this should have stepped back one
        
        Assert.assertTrue(qs.canBackUp());
        // move forward a different path, ensure prior set answers are
        // flushed
        
        existingAnswerToNextQuestion = qs.answerQuestion("blue");
        Assert.assertEquals(existingAnswerToNextQuestion,"NOT_YET_SPECIFIED");
        Assert.assertEquals(qs.getNextAnswerIndex(),3);
        Assert.assertFalse(qs.isAllQuestionsAnswered());
        QQuestion question3 = qs.getCurrentQuestion();
        Assert.assertEquals(question3.getId(),"RESOLUTION");
        Assert.assertTrue(length(qs.getAnsweredQuestions().get) == 2);
        Assert.assertEquals(qs.getAnsweredQuestions().get(1).getQuestionID(), "BREADBOX");
        Assert.assertEquals(qs.getAnsweredQuestions().get(2).getQuestionID(), "COLOR");
        Assert.assertEquals(qs.getAnsweredQuestions().get(1).getAnswer(), "yes");
        Assert.assertEquals(qs.getAnsweredQuestions().get(2).getAnswer(), "blue");
    }

    public void testQuestionSequencerCASE_Change3(){
     // CASE_CHANGE3 forward through last question, back up one, then forward,
        // change answer
        QuestionsXMLFile xmlFile = getQuestionsXMLFile();
        QQuestions qquestions = new QQuestions(xmlFile.getDomNode());
        QuestionSequencer qs = new QuestionSequencer(qquestions);
        
        existingAnswerToNextQuestion = qs.answerQuestion("yes");
        existingAnswerToNextQuestion = qs.answerQuestion("red");
        existingAnswerToNextQuestion = qs.answerQuestion("40");
        Assert.assertEquals(existingAnswerToNextQuestion,"NOT_YET_SPECIFIED");
        Assert.assertTrue(qs.isAllQuestionsAnswered());
        
        // step backward 
        Assert.assertTrue(qs.canBackUp());
        prevAnswer = qs.backUp();
        Assert.assertEquals(prevAnswer.getQuestionID(),"COVER_PERCENT");
        Assert.assertEquals(prevAnswer.getAnswer(),"40");
        question3 = qs.getCurrentQuestion();
        Assert.assertEquals(question3.getId(),"COVER_PERCENT");
        
        Assert.assertTrue(length(qs.getAnsweredQuestions().get) == 3);
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
        
        existingAnswerToNextQuestion = qs.answerQuestion("50");
        Assert.assertEquals(existingAnswerToNextQuestion,"NOT_YET_SPECIFIED");
        Assert.assertEquals(qs.getNextAnswerIndex(),4);
        Assert.assertTrue(qs.isAllQuestionsAnswered());
        question4 = qs.getCurrentQuestion();
        Assert.assertEquals(question4.getId(),"NO_MORE_QUESTIONS");
        Assert.assertTrue(length(qs.getAnsweredQuestions().get) == 3);
        
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
        QQuestions qquestions = new QQuestions(xmlFile.getDomNode());
        QuestionSequencer qs = new QuestionSequencer(qquestions);
        
        existingAnswerToNextQuestion = qs.answerQuestion("yes");
        existingAnswerToNextQuestion = qs.answerQuestion("red");
        existingAnswerToNextQuestion = qs.answerQuestion("40");
        Assert.assertEquals(existingAnswerToNextQuestion,"NOT_YET_SPECIFIED");
        Assert.assertTrue(qs.isAllQuestionsAnswered());
        
        // step backward 
        Assert.assertTrue(qs.canBackUp());
        prevAnswer = qs.backUp();
        Assert.assertEquals(prevAnswer.getQuestionID(),"COVER_PERCENT");
        Assert.assertEquals(prevAnswer.getAnswer(),"40");
        QQuestion question3 = qs.getCurrentQuestion();
        Assert.assertEquals(question3.getId(),"COVER_PERCENT");
        
        Assert.assertTrue(length(qs.getAnsweredQuestions().get) == 3);
        Assert.assertEquals(qs.getAnsweredQuestions().get(1).getQuestionID(), "BREADBOX");
        Assert.assertEquals(qs.getAnsweredQuestions().get(2).getQuestionID(), "COLOR");
        Assert.assertEquals(qs.getAnsweredQuestions().get(3).getQuestionID(), "COVER_PERCENT");
        Assert.assertEquals(qs.getAnsweredQuestions().get(1).getAnswer(), "yes");
        Assert.assertEquals(qs.getAnsweredQuestions().get(2).getAnswer(), "red");
        Assert.assertEquals(qs.getAnsweredQuestions().get(3).getAnswer(), "40"); // answer remains if we need to reuse going forward
        Assert.assertEquals(qs.getNextAnswerIndex(),3);  // this should have stepped back one
        
        // step backward
        Assert.assertTrue(qs.canBackUp());
        prevAnswer = qs.backUp();
        Assert.assertEquals(prevAnswer.getQuestionID(),"COLOR");
        Assert.assertEquals(prevAnswer.getAnswer(),"red");
        question2 = qs.getCurrentQuestion();
        Assert.assertEquals(question2.getId(),"COLOR");
        
        Assert.assertTrue(length(qs.getAnsweredQuestions().get) == 3);
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
        
        existingAnswerToNextQuestion = qs.answerQuestion("blue");
        Assert.assertEquals(existingAnswerToNextQuestion,"NOT_YET_SPECIFIED");
        Assert.assertEquals(qs.getNextAnswerIndex(),3);
        Assert.assertFalse(qs.isAllQuestionsAnswered());
        question3 = qs.getCurrentQuestion();
        Assert.assertEquals(question3.getId(),"RESOLUTION");
        Assert.assertTrue(length(qs.getAnsweredQuestions().get) == 2);
        
        Assert.assertEquals(qs.getAnsweredQuestions().get(1).getQuestionID(), "BREADBOX");
        Assert.assertEquals(qs.getAnsweredQuestions().get(2).getQuestionID(), "COLOR");
        Assert.assertEquals(qs.getAnsweredQuestions().get(1).getAnswer(), "yes");
        Assert.assertEquals(qs.getAnsweredQuestions().get(2).getAnswer(), "blue");
    }

    public void testQuestionSequencerCASE_Change5(){
     // CASE_CHANGE5 forward through last question, back up all the way, then forward,
        // change answer
        QuestionsXMLFile xmlFile = getQuestionsXMLFile();
        QQuestions qquestions = new QQuestions(xmlFile.getDomNode());
        QuestionSequencer qs = new QuestionSequencer(qquestions);
        
        existingAnswerToNextQuestion = qs.answerQuestion("yes");
        existingAnswerToNextQuestion = qs.answerQuestion("red");
        existingAnswerToNextQuestion = qs.answerQuestion("40");
        Assert.assertEquals(existingAnswerToNextQuestion,"NOT_YET_SPECIFIED");
        Assert.assertTrue(qs.isAllQuestionsAnswered());
        
        // step backward 
        Assert.assertTrue(qs.canBackUp());
        prevAnswer = qs.backUp();
        Assert.assertEquals(prevAnswer.getQuestionID(),"COVER_PERCENT");
        Assert.assertEquals(prevAnswer.getAnswer(),"40");
        QQuestion question3 = qs.getCurrentQuestion();
        Assert.assertEquals(question3.getId(),"COVER_PERCENT");
        
        Assert.assertTrue(length(qs.getAnsweredQuestions().get) == 3);
        Assert.assertEquals(qs.getAnsweredQuestions().get(1).getQuestionID(), "BREADBOX");
        Assert.assertEquals(qs.getAnsweredQuestions().get(2).getQuestionID(), "COLOR");
        Assert.assertEquals(qs.getAnsweredQuestions().get(3).getQuestionID(), "COVER_PERCENT");
        Assert.assertEquals(qs.getAnsweredQuestions().get(1).getAnswer(), "yes");
        Assert.assertEquals(qs.getAnsweredQuestions().get(2).getAnswer(), "red");
        Assert.assertEquals(qs.getAnsweredQuestions().get(3).getAnswer(), "40"); // answer remains if we need to reuse going forward
        Assert.assertEquals(qs.getNextAnswerIndex(),3);  // this should have stepped back one
        
        // step backward
        Assert.assertTrue(qs.canBackUp());
        prevAnswer = qs.backUp();
        Assert.assertEquals(prevAnswer.getQuestionID(),"COLOR");
        Assert.assertEquals(prevAnswer.getAnswer(),"red");
        QQuestion question2 = qs.getCurrentQuestion();
        Assert.assertEquals(question2.getId(),"COLOR");
        
        Assert.assertTrue(length(qs.getAnsweredQuestions().get) == 3);
        Assert.assertEquals(qs.getAnsweredQuestions().get(1).getQuestionID(), "BREADBOX");
        Assert.assertEquals(qs.getAnsweredQuestions().get(2).getQuestionID(), "COLOR");
        Assert.assertEquals(qs.getAnsweredQuestions().get(3).getQuestionID(), "COVER_PERCENT");
        Assert.assertEquals(qs.getAnsweredQuestions().get(1).getAnswer(), "yes");
        Assert.assertEquals(qs.getAnsweredQuestions().get(2).getAnswer(), "red");
        Assert.assertEquals(qs.getAnsweredQuestions().get(3).getAnswer(), "40"); 
        Assert.assertEquals(qs.getNextAnswerIndex(),2);  // this should have stepped back one
        
        // step backward
        Assert.assertTrue(qs.canBackUp());
        prevAnswer = qs.backUp();
        Assert.assertEquals(prevAnswer.getQuestionID(),"BREADBOX");
        Assert.assertEquals(prevAnswer.getAnswer(),"yes");
        QQuestion question1 = qs.getCurrentQuestion();
        Assert.assertEquals(question1.getId(),"BREADBOX");
        
        Assert.assertTrue(length(qs.getAnsweredQuestions().get) == 3);
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
        
        existingAnswerToNextQuestion = qs.answerQuestion("no");
        Assert.assertEquals(existingAnswerToNextQuestion,"NOT_YET_SPECIFIED");
        Assert.assertEquals(qs.getNextAnswerIndex(),2);
        Assert.assertFalse(qs.isAllQuestionsAnswered());
        question2 = qs.getCurrentQuestion();
        Assert.assertEquals(question2.getId(),"WEIGHT");
        Assert.assertTrue(length(qs.getAnsweredQuestions().get) == 1);
        
        Assert.assertEquals(qs.getAnsweredQuestions().get(1).getQuestionID(), "BREADBOX");
        Assert.assertEquals(qs.getAnsweredQuestions().get(1).getAnswer(), "no");
    }


    public void testQuestionsXmlFile(){
        QuestionsXMLFile xmlFile = getQuestionsXMLFile();
        QQuestions qquestions = new QQuestions(xmlFile.getDomNode());
        List<QQuestion> theQuestions = qquestions.getQuestions();
        QQuestion question1 = theQuestions.get(0);
        Assert.assertEquals(question1.getType(),"choice");
        Assert.assertEquals(question1.getId(),"BREADBOX");
        Assert.assertEquals(question1.getText(),"Is it bigger than a breadbox or is it approximately the same size? (Use your own judgement when deciding on the approximate size.  Everything is relative, you know.");
        Assert.assertEquals(question1.getAnswers().get(0).getValue(),"yes");
        Assert.assertEquals(question1.getAnswers().get(0).getNextQuestion(),"COLOR");
        Assert.assertEquals(question1.getAnswers().get(1).getValue(),"no");
        Assert.assertEquals(question1.getAnswers().get(1).getNextQuestion(),"WEIGHT");
        Assert.assertEquals(question1.images(0).imageFilePath,"data/questionnaire/images/elephant.jpg");
        Assert.assertEquals(question1.images(0).imageCaption,"elephants are bigger than a breadbox");
        Assert.assertEquals(question1.images(1).imageFilePath,"data/questionnaire/images/mouse.jpg");
        Assert.assertEquals(question1.images(1).imageCaption,"mice are smaller than a breadbox");
        
        question2 = theQuestions(2);
        Assert.assertEquals(question2.type,"choice");
        Assert.assertEquals(question2.getId(),"COLOR");
        Assert.assertEquals(question2.getText(),"What color is the larger-than-breadbox item?");
        Assert.assertEquals(question2.getAnswers().get(1).getValue(),"red");
        Assert.assertEquals(question2.getAnswers().get(1).getNextQuestion(),"COVER_PERCENT");
        Assert.assertEquals(question2.getAnswers().get(2).getValue(),"green");
        Assert.assertEquals(question2.getAnswers().get(2).getNextQuestion(),"COVER_PERCENT");
        Assert.assertEquals(question2.getAnswers().get(3).getValue(),"blue");
        Assert.assertEquals(question2.getAnswers().get(3).getNextQuestion(),"RESOLUTION");
         
        question3 = theQuestions(3);
        Assert.assertEquals(question3.type,"input_integer");
        Assert.assertEquals(question3.getId(),"WEIGHT");
        Assert.assertEquals(question3.getText(),"What is the weight in kilograms of the smaller-than-breadbox item?");
        Assert.assertEquals(question3.getAnswers().get(1).getValue(),"0");
        Assert.assertEquals(question3.getAnswers().get(1).getNextQuestion(),"COVER_PERCENT");
        
        question4 = theQuestions(4);
        Assert.assertEquals(question4.type,"input_string");
        Assert.assertEquals(question4.getId(),"RESOLUTION");
        Assert.assertEquals(question4.getText(),"What is the resolution of the smaller-than-breadbox item?");
        Assert.assertEquals(question4.getAnswers().get(1).getValue(),"");
        Assert.assertEquals(question4.getAnswers().get(1).getNextQuestion(),"NO_MORE_QUESTIONS");
        
        question5 = theQuestions(5);
        Assert.assertEquals(question5.type,"input_integer");
        Assert.assertEquals(question5.getId(),"COVER_PERCENT");
        Assert.assertEquals(question5.getText(),"What percent of image is occupied?");
        Assert.assertEquals(question5.getAnswers().get(1).getValue(),"0");
        Assert.assertEquals(question5.getAnswers().get(1).getNextQuestion(),"NO_MORE_QUESTIONS");

    }
}
