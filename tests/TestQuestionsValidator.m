classdef TestQuestionsValidator < matlab.unittest.TestCase
    properties 
        OriginalPath;
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
        
        function testValidateQuestionCount(testCase)
            qv = QuestionsValidator();
            questions = {};
            testCase.verifyFalse(qv.validateQuestionCount(questions));
            
            q = QQuestion('input_integer', 'AAA', 'how many?');
            questions = [ questions, q ];
            testCase.verifyTrue(qv.validateQuestionCount(questions));
            
            q = QQuestion('choice', 'BBB', 'what is down?');
            questions = [ questions, q ];
            testCase.verifyTrue(qv.validateQuestionCount(questions));
        end
        
        function testValidateIdsUnique(testCase)
            qv = QuestionsValidator();
            questions = {};
            q1 = QQuestion('input_integer', 'AAA', 'how many?');
            questions = [ questions, q1 ];
            testCase.verifyTrue(isempty(qv.getDuplicateIds(questions)));
            
            q2 = QQuestion('input_integer', 'BBB', 'how many?');
            questions = [ questions, q2 ];
            testCase.verifyTrue(isempty(qv.getDuplicateIds(questions)));
            
            q3 = QQuestion('input_integer', 'CCC', 'how many?');
            questions = [ questions, q3 ];
            testCase.verifyTrue(isempty(qv.getDuplicateIds(questions)));
            
            q4 = QQuestion('input_integer', 'BBB', 'how many?');
            questions = [ questions, q4 ];
            testCase.verifyFalse(isempty(qv.getDuplicateIds(questions)));
        end
        
        function testRemoveMatchFromList(testCase)
            qv = QuestionsValidator();
            foo = { 'xx', 'y', 'z' };
            foo = qv.removeMatchStringFromList('y', foo);
            testCase.verifyEqual('xx', char(foo(1)));
            testCase.verifyEqual('z', char(foo(2)));
            
            testCase.verifyTrue(length(foo) == 2);
            foo = qv.removeMatchStringFromList('a', foo);
            testCase.verifyEqual('xx', char(foo(1)));
            testCase.verifyEqual('z', char(foo(2)));
            
            testCase.verifyTrue(length(foo) == 2);
            foo = qv.removeMatchStringFromList('xx', foo);
            testCase.verifyEqual('z', char(foo(1)));
            testCase.verifyTrue(length(foo) == 1);
            
            foo = qv.removeMatchStringFromList('z', foo);
            testCase.verifyTrue(length(foo) == 0);
        end
        
        function testValidateAllQuestionsUsed(testCase)
            qv = QuestionsValidator();
            questions = {};
            q1 = QQuestion('input_integer', 'AAA', 'na');
            a1 = QAnswer('0','BBB');
            q1.addAnswer(a1);
            questions = [ questions, q1 ];
            q2 = QQuestion('input_integer', 'BBB', 'na');
            a2 = QAnswer('0','CCC');
            q2.addAnswer(a2);
            questions = [ questions, q2 ];
            q3 = QQuestion('input_integer', 'CCC', 'na');
            a3 = QAnswer('0','NO_MORE_QUESTIONS');
            q3.addAnswer(a3);
            questions = [ questions, q3 ];
            unused = qv.getUnusedQuestions(questions);
            testCase.verifyTrue(isempty(unused));
            
            qv = QuestionsValidator();
            questions = {};
            q1 = QQuestion('input_integer', 'AAA', 'na');
            a1 = QAnswer('0','CCC');
            q1.addAnswer(a1);
            questions = [ questions, q1 ];
            q2 = QQuestion('input_integer', 'UNUSED', 'na');
            a2 = QAnswer('0','CCC');
            q2.addAnswer(a2);
            questions = [ questions, q2 ];
            q3 = QQuestion('input_integer', 'CCC', 'na');
            a3 = QAnswer('0','NO_MORE_QUESTIONS');
            q3.addAnswer(a3);
            questions = [ questions, q3 ];
            unused = qv.getUnusedQuestions(questions);
            testCase.verifyFalse(isempty(unused));
        end
        function testGetChoiceAnswerMalformations(testCase)
            qv = QuestionsValidator();
            a1 = QAnswer('','CCC');
            malformations = qv.getChoiceAnswerMalformations(a1);
            problem = char(malformations(1));
            expectedProblem = 'answer value empty';
            testCase.verifyTrue(strcmp(problem,expectedProblem));
            testCase.verifyTrue(length(malformations) == 1);
            
            a2 = QAnswer('0','');
            malformations = qv.getChoiceAnswerMalformations(a2);
            problem = char(malformations(1));
            expectedProblem = 'answer nextQuestion empty';
            testCase.verifyTrue(strcmp(problem,expectedProblem));
            testCase.verifyTrue(length(malformations) == 1);
            
            a3 = QAnswer('0','CCC');
            malformations = qv.getChoiceAnswerMalformations(a3);
            testCase.verifyTrue(length(malformations) == 0);
        end
        
        function testGetInputIntegerAnswerMalformations(testCase)
            qv = QuestionsValidator();
            a1 = QAnswer('','CCC');
            malformations = qv.getInputIntegerAnswerMalformations(a1);
            problem = char(malformations(1));
            expectedProblem = 'answer value empty';
            testCase.verifyTrue(strcmp(problem,expectedProblem));
            testCase.verifyTrue(length(malformations) == 1);
            
            a2 = QAnswer('0','');
            malformations = qv.getInputIntegerAnswerMalformations(a2);
            problem = char(malformations(1));
            expectedProblem = 'answer nextQuestion empty';
            testCase.verifyTrue(strcmp(problem,expectedProblem));
            testCase.verifyTrue(length(malformations) == 1);
            
            a3 = QAnswer('0','CCC');
            malformations = qv.getInputIntegerAnswerMalformations(a3);
            testCase.verifyTrue(length(malformations) == 0);
        end
        
        function testGetInputStringAnswerMalformations(testCase)
            qv = QuestionsValidator();
            a1 = QAnswer('','CCC');
            malformations = qv.getInputStringAnswerMalformations(a1);
            testCase.verifyTrue(isempty(malformations));
            
            % in case we ever want to have a default value, allow
            % non-empty content for now - it will just be ignored
            a2 = QAnswer('0','');
            malformations = qv.getInputStringAnswerMalformations(a2);
            problem = char(malformations(1));
            expectedProblem = 'answer nextQuestion empty';
            testCase.verifyTrue(strcmp(problem,expectedProblem));
            testCase.verifyTrue(length(malformations) == 1);
            
        end
        
        function testGetImageMalformations(testCase)
            qv = QuestionsValidator();
            i1 = QImage('','someCaption');
            malformations = qv.getImageMalformations(i1);
            problem = char(malformations(1));
            expectedProblem = 'image filename empty';
            testCase.verifyTrue(strcmp(problem,expectedProblem));
            testCase.verifyTrue(length(malformations) == 1);
            
            i2 = QImage('data/questionnaire/images/nonExistent.jpg','someCaption');
            malformations = qv.getImageMalformations(i2);
            problem = char(malformations(1));
            expectedProblem = 'image filename does not exist: data/questionnaire/images/nonExistent.jpg';
            testCase.verifyTrue(strcmp(problem,expectedProblem));
            testCase.verifyTrue(length(malformations) == 1);
            
            i3 = QImage('data/questionnaire/images/bogusJPG.jpg','');
            malformations = qv.getImageMalformations(i3);
            problem = char(malformations(1));
            expectedProblem = 'image caption empty';
            testCase.verifyTrue(strcmp(problem,expectedProblem));
            testCase.verifyTrue(length(malformations) == 1);
            
            i4 = QImage('data/questionnaire/images/bogusJPG.jpg','someCaption');
            malformations = qv.getImageMalformations(i4);
            testCase.verifyTrue(length(malformations) == 0);
        end
        
        function testGetInputIntegerQuestionMalformations(testCase)
            qv = QuestionsValidator();
            a1 = QAnswer('X','CCC');
            a2 = QAnswer('Y','DDD');
            q = QQuestion('integer_input', 'ID3', 'someText');
            q.addAnswer(a1);
            q.addAnswer(a2);
            malformations = qv.getInputIntegerQuestionMalformations(q);
            problem = char(malformations(1));
            expectedProblem = 'input_integer questions should have one answer: ID3';
            testCase.verifyTrue(strcmp(problem,expectedProblem));
            testCase.verifyTrue(length(malformations) == 1);
            
            q = QQuestion('integer_input', 'ID4', 'someText');
            a1 = QAnswer('','CCC');
            q.addAnswer(a1);
            malformations = qv.getInputIntegerQuestionMalformations(q);
            problem = char(malformations(1));
            expectedProblem = 'answer value empty';
            testCase.verifyTrue(strcmp(problem,expectedProblem));
            testCase.verifyTrue(length(malformations) == 1);
            
            q = QQuestion('integer_input', 'ID4', 'someText');
            a1 = QAnswer('','CCC');
            i1 = QImage('data/questionnaire/images/bogusJPG.jpg','');
            q.addAnswer(a1);
            q.addImage(i1);
            malformations = qv.getInputIntegerQuestionMalformations(q);
            problem1 = char(malformations(1));
            problem2 = char(malformations(2));
            expectedProblem = 'answer value empty';
            testCase.verifyTrue(strcmp(problem1,expectedProblem));
            expectedProblem = 'image caption empty';
            testCase.verifyTrue(strcmp(problem2,expectedProblem));
            testCase.verifyTrue(length(malformations) == 2);
        end
        
        
        function testGetInputStringQuestionMalformations(testCase)
            qv = QuestionsValidator();
            a1 = QAnswer('X','CCC');
            a2 = QAnswer('Y','DDD');
            q = QQuestion('integer_input', 'ID3', 'someText');
            q.addAnswer(a1);
            q.addAnswer(a2);
            malformations = qv.getInputStringQuestionMalformations(q);
            problem = char(malformations(1));
            expectedProblem = 'input_string questions should have one answer: ID3';
            testCase.verifyTrue(strcmp(problem,expectedProblem));
            testCase.verifyTrue(length(malformations) == 1);
            
            q = QQuestion('integer_input', 'ID4', 'someText');
            a1 = QAnswer('','CCC');
            q.addAnswer(a1);
            malformations = qv.getInputStringQuestionMalformations(q);
            testCase.verifyTrue(isempty(malformations));
          
        end
        
        
        function testGetChoiceQuestionMalformations(testCase)
            qv = QuestionsValidator();
            a1 = QAnswer('X','CCC');
            q = QQuestion('choice', 'ID3', 'someText');
            q.addAnswer(a1);
            malformations = qv.getChoiceQuestionMalformations(q);
            problem = char(malformations(1));
            expectedProblem = 'at least two answers required of a choice question, not 1';
            testCase.verifyTrue(strcmp(problem,expectedProblem));
            testCase.verifyTrue(length(malformations) == 1);
            
            q = QQuestion('integer_input', 'ID4', 'someText');
            a1 = QAnswer('','CCC');
            a2 = QAnswer('no', 'DDD');
            q.addAnswer(a1);
            q.addAnswer(a2);
            malformations = qv.getChoiceQuestionMalformations(q);
            problem = char(malformations(1));
            expectedProblem = 'answer value empty';
            testCase.verifyTrue(strcmp(problem,expectedProblem));
            testCase.verifyTrue(length(malformations) == 1);
            
            q = QQuestion('input_integer', 'ID4', 'someText');
            a1 = QAnswer('','CCC');
            a2 = QAnswer('no', 'DDD');
            i1 = QImage('images/nonexistent.jpg','someCaption');
            q.addAnswer(a1);
            q.addAnswer(a2);
            q.addImage(i1);
            malformations = qv.getChoiceQuestionMalformations(q);
            problem1 = char(malformations(1));
            problem2 = char(malformations(2));
            expectedProblem = 'answer value empty';
            testCase.verifyTrue(strcmp(problem1,expectedProblem));
            expectedProblem = 'image filename does not exist: images/nonexistent.jpg';
            testCase.verifyTrue(strcmp(problem2,expectedProblem));
            testCase.verifyTrue(length(malformations) == 2);
        end
        
           
        function testGetQuestionMalformations(testCase)
            qv = QuestionsValidator();
            q = QQuestion('badType', 'ID3', 'someText');
            malformations = qv.getQuestionMalformations(q);
            problem = char(malformations(1));
            expectedProblem = 'question type must be either choice, input_integer, or input_string';
            testCase.verifyTrue(strcmp(problem,expectedProblem));
            testCase.verifyTrue(length(malformations) == 1);
            
            q = QQuestion('input_integer', '', 'someText');
            a1 = QAnswer('0','CCC');
            q.addAnswer(a1);
            malformations = qv.getQuestionMalformations(q);
            problem = char(malformations(1));
            expectedProblem = 'question id empty';
            testCase.verifyTrue(strcmp(problem,expectedProblem));
            testCase.verifyTrue(length(malformations) == 1);
            
            q = QQuestion('input_integer', 'ID4', '');
            a1 = QAnswer('0','CCC');
            q.addAnswer(a1);
            malformations = qv.getQuestionMalformations(q);
            problem1 = char(malformations(1));
            expectedProblem = 'question text empty for ID4';
            testCase.verifyTrue(strcmp(problem1,expectedProblem));
            testCase.verifyTrue(length(malformations) == 1);
            
            %need more tests here to cover answers and images to make sure the errors percolate up
        end
    end
end








