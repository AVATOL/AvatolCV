package edu.oregonstate.eecs.iis.avatol.questionnaire;

import junit.framework.TestCase;

public class TestQQuestions extends TestCase {

}

/*
*
classdef TestQQuestions < matlab.unittest.TestCase
    properties 
        OriginalPath
    end
    
    methods (TestMethodSetup)
        function addSrcToPath(testCase)
            testCase.OriginalPath = path;
            addpath(fullfile(pwd, '../questionnaire'));
            addpath(fullfile(pwd, '..'));
        end
    end
    
    methods (TestMethodTeardown)
        function restorePath(testCase)
            path(testCase.OriginalPath);
        end
    end
    
    methods (Test)
        function testFindQuestionById(testCase)
            xmlFile = QuestionsXMLFile('tests/simple.xml');
            qquestions = QQuestions(xmlFile.domNode);
            qquestion1 = qquestions.findQuestionById('BREADBOX');
            testCase.verifyEqual(qquestion1.id, 'BREADBOX');
            qquestion2 = qquestions.findQuestionById('COLOR');
            testCase.verifyEqual(qquestion2.id, 'COLOR');
            qquestion3 = qquestions.findQuestionById('WEIGHT');
            testCase.verifyEqual(qquestion3.id, 'WEIGHT');
            qquestion4= qquestions.findQuestionById('COVER_PERCENT');
            testCase.verifyEqual(qquestion4.id, 'COVER_PERCENT');
            qquestion5= qquestions.findQuestionById('SHOULD_NOT_FIND_THIS');
            testCase.verifyEqual(qquestion5.id, 'NULL');
            qquestion6= qquestions.findQuestionById('RESOLUTION');
            testCase.verifyEqual(qquestion6.id, 'RESOLUTION');
        end
    end
end

*/