package edu.oregonstate.eecs.iis.avatol.questionnaire;

import edu.oregonstate.eecs.iis.avatolcv.AvatolCVFileSystem;
import edu.oregonstate.eecs.iis.avatolcv.SystemDependent;
import edu.oregonstate.eecs.iis.avatolcv.questionnaire.QQuestion;
import edu.oregonstate.eecs.iis.avatolcv.questionnaire.QQuestions;
import edu.oregonstate.eecs.iis.avatolcv.questionnaire.QuestionsXMLFile;
import junit.framework.Assert;
import junit.framework.TestCase;

public class TestQQuestions extends TestCase {
	private static final String FILESEP = System.getProperty("file.separator");
    public void testFindQuestionById(){
    	SystemDependent sd = new SystemDependent();
    	String rootDir = sd.getRootDir();
    	AvatolCVFileSystem re = new AvatolCVFileSystem(rootDir);
    	try {
    		String simpleXMLPath = rootDir + FILESEP + "tests" + FILESEP + "simple.xml";
        	QuestionsXMLFile xmlFile = new QuestionsXMLFile(simpleXMLPath);
            QQuestions qquestions = new QQuestions(xmlFile.getDomNode());
            QQuestion qquestion1 = qquestions.getQuestions().get(0);
            Assert.assertTrue(qquestion1.getId().equals("BREADBOX"));
            QQuestion qquestion2 = qquestions.getQuestions().get(1);
            Assert.assertTrue(qquestion2.getId().equals("COLOR"));
            QQuestion qquestion3 = qquestions.getQuestions().get(2);
            Assert.assertTrue(qquestion3.getId().equals("WEIGHT"));
            QQuestion qquestion4 = qquestions.getQuestions().get(3);
            Assert.assertTrue(qquestion4.getId().equals("RESOLUTION"));
            QQuestion qquestion5 = qquestions.getQuestions().get(4);
            Assert.assertTrue(qquestion5.getId().equals("COVER_PERCENT"));
    	}
    	catch(Exception ex){
    		ex.printStackTrace();
    		Assert.fail("problem testing QQuestions.");
    	}
    	
    }
}

