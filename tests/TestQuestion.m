classdef TestQuestion < matlab.unittest.TestCase
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
        
        function testIsStringInteger(testCase)
            q = QQuestion('input_integer', 'AAA', 'what is up?');
            testCase.verifyTrue(q.isStringInteger('0'));
            testCase.verifyTrue(q.isStringInteger('1'));
            testCase.verifyTrue(q.isStringInteger('234'));
            testCase.verifyFalse(q.isStringInteger('b'));
            testCase.verifyFalse(q.isStringInteger('023.'));
        end
        
        function testIsStringString(testCase)
            q = QQuestion('input_string', 'AAA', 'what is up?');
            testCase.verifyTrue(q.isStringString('0'));
            testCase.verifyTrue(q.isStringString('o'));
            testCase.verifyTrue(q.isStringString('gsdfg'));
            testCase.verifyFalse(q.isStringString(''));
            testCase.verifyFalse(q.isStringString('\n'));
            testCase.verifyFalse(q.isStringString('\t'));
        end
        function testIsValidIntegerAnswer(testCase)
            q = QQuestion('input_integer', 'AAA', 'what is up?');
            testCase.verifyTrue(q.isValidAnswer('0'));
            testCase.verifyTrue(q.isValidAnswer('1'));
            testCase.verifyTrue(q.isValidAnswer('234'));
            try
                q.isValidAnswer('b');
                testCase.assertFail('invalid integer should have thrown exception');
            catch exception
                
            end
            try 
                testCase.verifyFalse(q.isValidAnswer('023.'));
                testCase.assertFail('invalid integer should have thrown exception');
            catch exception
                
            end
            
            
            
        end
        function testIsValidChoiceAnswer(testCase)
            q = QQuestion('choice', 'BBB', 'what is up?');
            answer1 = QAnswer('yes','someNext');
            answer2 = QAnswer('no','someOtherNext');
            q.addAnswer(answer1);
            q.addAnswer(answer2);
            testCase.verifyTrue(q.isValidAnswer('yes'));
            testCase.verifyTrue(q.isValidAnswer('no'));
            testCase.verifyFalse(q.isValidAnswer('maybe'));
            
            q = QQuestion('bad_type', 'AAA', 'what is up?');
            try 
                testCase.verifyTrue(q.isValidAnswer('0'));
                testCase.assertFail('invalid type accepted for question');
            catch
                % nothing to do, just trying to flag the failure to throw
                % an exception
            end
            
        end
        function testIsValidStringAnswer(testCase)
            q = QQuestion('input_string', 'AAA', 'what is up?');
            testCase.verifyTrue(q.isValidAnswer('0'));
            testCase.verifyTrue(q.isValidAnswer('1'));
            testCase.verifyTrue(q.isValidAnswer('234'));
            try 
                testCase.verifyFalse(q.isValidAnswer(''));
                testCase.assertFail('invalid string input should have thrown exception');
            catch exception
                
            end
            try
                testCase.verifyFalse(q.isValidAnswer('\n'));
                testCase.assertFail('invalid string input "\n" should have thrown exception');
            catch exception
                
            end
            try
                testCase.verifyFalse(q.isValidAnswer('\t'));
                testCase.assertFail('invalid string input "\t" should have thrown exception');
            catch
               
            end
            
        end
        function testGetQAnswerForAnswerValue(testCase)
            q = QQuestion('choice', 'BBB', 'what is up?');
            answer1 = QAnswer('yes','someNext');
            answer2 = QAnswer('no','someOtherNext');
            q.addAnswer(answer1);
            q.addAnswer(answer2);
            qAnswerYes = q.getQAnswerForAnswerValue('yes');
            testCase.verifyTrue(strcmp(qAnswerYes.value, 'yes'));
            qAnswerNo = q.getQAnswerForAnswerValue('no');
            testCase.verifyTrue(strcmp(qAnswerNo.value, 'no'));
            qAnswerNULL = q.getQAnswerForAnswerValue('maybe');
            testCase.verifyTrue(strcmp(qAnswerNULL.value, 'NULL'));
            
        end
 
    end
end
