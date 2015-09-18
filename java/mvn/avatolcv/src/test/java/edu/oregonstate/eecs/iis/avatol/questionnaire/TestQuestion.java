package edu.oregonstate.eecs.iis.avatol.questionnaire;

import edu.oregonstate.eecs.iis.obsolete.avatolcv.questionnaire.QAnswer;
import edu.oregonstate.eecs.iis.obsolete.avatolcv.questionnaire.QQuestion;
import junit.framework.Assert;
import junit.framework.TestCase;

public class TestQuestion extends TestCase {
	

	public void testIsValidIntegerAnswer(){
		
		try {
			QQuestion q = new QQuestion("input_integer", "AAA", "what is up?");
			Assert.assertTrue(q.getAnswerIntegrity("0").isValid());
			Assert.assertTrue(q.getAnswerIntegrity("1").isValid());
			Assert.assertTrue(q.getAnswerIntegrity("234").isValid());
			Assert.assertFalse(q.getAnswerIntegrity("b").isValid());
			Assert.assertFalse(q.getAnswerIntegrity("023.").isValid());
		}
		catch(Exception e){
			Assert.fail("problem testing isValidAnswer");
		}
	}
    
    
  
	public void testIsValidChoiceAnswer(){
		try {
			QQuestion q = new QQuestion("choice", "BBB", "what is up?");
			QAnswer answer1 = new QAnswer("yes","someNext");
			QAnswer answer2 = new QAnswer("no","someOtherNext");
			q.addAnswer(answer1);
			q.addAnswer(answer2);
			Assert.assertTrue(q.getAnswerIntegrity("yes").isValid());
			Assert.assertTrue(q.getAnswerIntegrity("no").isValid());
			Assert.assertFalse(q.getAnswerIntegrity("maybe").isValid());
		}
		catch(Exception e){
			Assert.fail("problem testing isValidAnswer");
		}
		
    
		try {
			QQuestion q = new QQuestion("bad_type", "AAA", "what is up?");
			Assert.assertTrue(q.getAnswerIntegrity("0").isValid());
			Assert.fail("invalid type accepted for question");
		}
		catch (Exception e){
			
		}
        // nothing to do, just trying to flag the failure to throw
        // an exception
	}
	public void testIsValidStringAnswer(){
		try {
			QQuestion q = new QQuestion("input_string", "AAA", "what is up?");
			Assert.assertTrue(q.getAnswerIntegrity("0").isValid());
			Assert.assertTrue(q.getAnswerIntegrity("1").isValid());
			Assert.assertTrue(q.getAnswerIntegrity("234").isValid());
			Assert.assertFalse(q.getAnswerIntegrity("").isValid());
			Assert.assertFalse(q.getAnswerIntegrity("\n").isValid());
			Assert.assertFalse(q.getAnswerIntegrity("\t").isValid());
		}
		catch(Exception e){
			Assert.fail("problem testing valid string answer");
		}
		
   
	}
    

	public void testGetQAnswerForAnswerValue(){
		try {
			QQuestion q = new QQuestion("choice", "BBB", "what is up?");
			QAnswer answer1 = new QAnswer("yes","someNext");
			QAnswer answer2 = new QAnswer("no","someOtherNext");
			q.addAnswer(answer1);
			q.addAnswer(answer2);
			QAnswer qAnswerYes = q.getQAnswerForAnswerValue("yes");
			Assert.assertTrue(qAnswerYes.getValue().equals("yes"));
			QAnswer qAnswerNo = q.getQAnswerForAnswerValue("no");
			Assert.assertTrue(qAnswerNo.getValue().equals("no"));
			QAnswer qAnswerNULL = q.getQAnswerForAnswerValue("maybe");
			Assert.assertTrue(qAnswerNULL == null);
		}
		catch(Exception e){
			Assert.fail("problem testing GetQAnswer");
		}
	}
}
