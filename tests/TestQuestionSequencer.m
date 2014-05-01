classdef TestQuestionSequencer < matlab.unittest.TestCase
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
        function testQuestionSequencerCASEForwardSimple(testCase)
            xmlFile = QuestionsXMLFile('tests/simple.xml');
            qquestions = QQuestions(xmlFile.domNode);
            qs = QuestionSequencer(qquestions);
            index = qs.nextAnswerIndex;
            testCase.verifyEqual(index,1);
            question1 = qs.getCurrentQuestion();
            testCase.verifyEqual(question1.id,'BREADBOX');
            % test invalid answer
            try 
                qs.answerQuestion('maybe');
                testCase.assertFail('invalid answer accepted for question');
            catch
                % nothing to do, just trying to flag the failure to throw
                % an exception
            end
            
            % test valid answers moving forward
            existingAnswerToNextQuestion = qs.answerQuestion('yes');
            testCase.verifyEqual(existingAnswerToNextQuestion,'NOT_YET_SPECIFIED');
            testCase.verifyEqual(qs.nextAnswerIndex,2);
            testCase.verifyFalse(qs.isAllQuestionsAnswered());
            question2 = qs.getCurrentQuestion();
            testCase.verifyEqual(question2.id,'COLOR');
            
            existingAnswerToNextQuestion = qs.answerQuestion('red');
            testCase.verifyEqual(existingAnswerToNextQuestion,'NOT_YET_SPECIFIED');
            testCase.verifyEqual(qs.nextAnswerIndex,3);
            testCase.verifyFalse(qs.isAllQuestionsAnswered());
            question3 = qs.getCurrentQuestion();
            testCase.verifyEqual(question3.id,'COVER_PERCENT');
            
            existingAnswerToNextQuestion = qs.answerQuestion('40');
            testCase.verifyEqual(existingAnswerToNextQuestion,'NOT_YET_SPECIFIED');
            testCase.verifyEqual(qs.nextAnswerIndex,4);
            testCase.verifyTrue(qs.isAllQuestionsAnswered());
            
            testCase.verifyTrue(length(qs.answeredQuestions) == 3);
            testCase.verifyEqual(qs.answeredQuestions(1).questionID, 'BREADBOX');
            testCase.verifyEqual(qs.answeredQuestions(2).questionID, 'COLOR');
            testCase.verifyEqual(qs.answeredQuestions(3).questionID, 'COVER_PERCENT');
            testCase.verifyEqual(qs.answeredQuestions(1).answer, 'yes');
            testCase.verifyEqual(qs.answeredQuestions(2).answer, 'red');
            testCase.verifyEqual(qs.answeredQuestions(3).answer, '40');
        end
        
        function testQuestionSequencerCASE_Keep1(testCase)
            % CASE_KEEP1 forward one question, back up, then forward 
            xmlFile = QuestionsXMLFile('tests/simple.xml');
            qquestions = QQuestions(xmlFile.domNode);
            qs = QuestionSequencer(qquestions);
            
            existingAnswerToNextQuestion = qs.answerQuestion('yes');
            testCase.verifyEqual(existingAnswerToNextQuestion,'NOT_YET_SPECIFIED');
            testCase.verifyFalse(qs.isAllQuestionsAnswered());
            
            % step backward 
            testCase.verifyTrue(qs.canBackUp());
            prevAnswer = qs.backUp();
            testCase.verifyEqual(prevAnswer.questionID,'BREADBOX');
            testCase.verifyEqual(prevAnswer.answer,'yes');
            question1 = qs.getCurrentQuestion();
            testCase.verifyEqual(question1.id,'BREADBOX');
            
            testCase.verifyTrue(length(qs.answeredQuestions) == 1);
            testCase.verifyEqual(qs.answeredQuestions(1).questionID, 'BREADBOX');
            testCase.verifyEqual(qs.answeredQuestions(1).answer, 'yes');
            testCase.verifyEqual(qs.nextAnswerIndex,1);  % this should have stepped back one
            
            testCase.verifyFalse(qs.canBackUp());
            % move forward the same path, ensure prior set answers are
            % right
            existingAnswerToNextQuestion = qs.answerQuestion('yes');
            testCase.verifyEqual(existingAnswerToNextQuestion,'NOT_YET_SPECIFIED');
            testCase.verifyEqual(qs.nextAnswerIndex,2);
            testCase.verifyFalse(qs.isAllQuestionsAnswered());
            
        end
        
        function testQuestionSequencerCASE_Keep2(testCase)
            % CASE_KEEP2 forward two questions, back up one, then forward
            xmlFile = QuestionsXMLFile('tests/simple.xml');
            qquestions = QQuestions(xmlFile.domNode);
            qs = QuestionSequencer(qquestions);
            
            existingAnswerToNextQuestion = qs.answerQuestion('yes');
            existingAnswerToNextQuestion = qs.answerQuestion('red');
            testCase.verifyEqual(existingAnswerToNextQuestion,'NOT_YET_SPECIFIED');
            testCase.verifyFalse(qs.isAllQuestionsAnswered());
            
            % step backward 
            testCase.verifyTrue(qs.canBackUp());
            prevAnswer = qs.backUp();
            testCase.verifyEqual(prevAnswer.questionID,'COLOR');
            testCase.verifyEqual(prevAnswer.answer,'red');
            question2 = qs.getCurrentQuestion();
            testCase.verifyEqual(question2.id,'COLOR');
            
            testCase.verifyTrue(length(qs.answeredQuestions) == 2);
            testCase.verifyEqual(qs.answeredQuestions(1).questionID, 'BREADBOX');
            testCase.verifyEqual(qs.answeredQuestions(2).questionID, 'COLOR');
            testCase.verifyEqual(qs.answeredQuestions(1).answer, 'yes');
            testCase.verifyEqual(qs.answeredQuestions(2).answer, 'red');
            testCase.verifyEqual(qs.nextAnswerIndex,2);  % this should have stepped back one
            
            testCase.verifyTrue(qs.canBackUp());
            % move forward the same path, ensure prior set answers are
            % right
            existingAnswerToNextQuestion = qs.answerQuestion('red');
            testCase.verifyEqual(existingAnswerToNextQuestion,'NOT_YET_SPECIFIED');
            testCase.verifyEqual(qs.nextAnswerIndex,3);
            testCase.verifyFalse(qs.isAllQuestionsAnswered());
            
        end
        
        function testQuestionSequencerCASE_Keep3(testCase)
            % CASE_KEEP3 forward through last question, back up one, then forward
            xmlFile = QuestionsXMLFile('tests/simple.xml');
            qquestions = QQuestions(xmlFile.domNode);
            qs = QuestionSequencer(qquestions);
            
            existingAnswerToNextQuestion = qs.answerQuestion('yes');
            existingAnswerToNextQuestion = qs.answerQuestion('red');
            existingAnswerToNextQuestion = qs.answerQuestion('40');
            testCase.verifyEqual(existingAnswerToNextQuestion,'NOT_YET_SPECIFIED');
            testCase.verifyTrue(qs.isAllQuestionsAnswered());
            
            % step backward 
            testCase.verifyTrue(qs.canBackUp());
            prevAnswer = qs.backUp();
            testCase.verifyEqual(prevAnswer.questionID,'COVER_PERCENT');
            testCase.verifyEqual(prevAnswer.answer,'40');
            question3 = qs.getCurrentQuestion();
            testCase.verifyEqual(question3.id,'COVER_PERCENT');
            
            testCase.verifyTrue(length(qs.answeredQuestions) == 3);
            testCase.verifyEqual(qs.answeredQuestions(1).questionID, 'BREADBOX');
            testCase.verifyEqual(qs.answeredQuestions(2).questionID, 'COLOR');
            testCase.verifyEqual(qs.answeredQuestions(3).questionID, 'COVER_PERCENT');
            testCase.verifyEqual(qs.answeredQuestions(1).answer, 'yes');
            testCase.verifyEqual(qs.answeredQuestions(2).answer, 'red');
            testCase.verifyEqual(qs.answeredQuestions(3).answer, '40'); % answer remains if we need to reuse going forward
            testCase.verifyEqual(qs.nextAnswerIndex,3);  % this should have stepped back one
            
            testCase.verifyTrue(qs.canBackUp());
            % move forward the same path, ensure prior set answers are
            % right
            existingAnswerToNextQuestion = qs.answerQuestion('40');
            testCase.verifyEqual(existingAnswerToNextQuestion,'NOT_YET_SPECIFIED');
            testCase.verifyEqual(qs.nextAnswerIndex,4);
            testCase.verifyTrue(qs.isAllQuestionsAnswered());
            
            testCase.verifyTrue(length(qs.answeredQuestions) == 3);
            testCase.verifyEqual(qs.answeredQuestions(1).questionID, 'BREADBOX');
            testCase.verifyEqual(qs.answeredQuestions(2).questionID, 'COLOR');
            testCase.verifyEqual(qs.answeredQuestions(3).questionID, 'COVER_PERCENT');
            testCase.verifyEqual(qs.answeredQuestions(1).answer, 'yes');
            testCase.verifyEqual(qs.answeredQuestions(2).answer, 'red');
            testCase.verifyEqual(qs.answeredQuestions(3).answer, '40');
            testCase.verifyTrue(qs.isAllQuestionsAnswered());
        end
        
        function testQuestionSequencerCASE_Keep4(testCase)
            % CASE_KEEP4 forward through last question, back up two, then forward
            xmlFile = QuestionsXMLFile('tests/simple.xml');
            qquestions = QQuestions(xmlFile.domNode);
            qs = QuestionSequencer(qquestions);
            
            existingAnswerToNextQuestion = qs.answerQuestion('yes');
            existingAnswerToNextQuestion = qs.answerQuestion('red');
            existingAnswerToNextQuestion = qs.answerQuestion('40');
            testCase.verifyEqual(existingAnswerToNextQuestion,'NOT_YET_SPECIFIED');
            testCase.verifyTrue(qs.isAllQuestionsAnswered());
            
            % step backward 
            testCase.verifyTrue(qs.canBackUp());
            prevAnswer = qs.backUp();
            testCase.verifyEqual(prevAnswer.questionID,'COVER_PERCENT');
            testCase.verifyEqual(prevAnswer.answer,'40');
            question3 = qs.getCurrentQuestion();
            testCase.verifyEqual(question3.id,'COVER_PERCENT');
            
            testCase.verifyTrue(length(qs.answeredQuestions) == 3);
            testCase.verifyEqual(qs.answeredQuestions(1).questionID, 'BREADBOX');
            testCase.verifyEqual(qs.answeredQuestions(2).questionID, 'COLOR');
            testCase.verifyEqual(qs.answeredQuestions(3).questionID, 'COVER_PERCENT');
            testCase.verifyEqual(qs.answeredQuestions(1).answer, 'yes');
            testCase.verifyEqual(qs.answeredQuestions(2).answer, 'red');
            testCase.verifyEqual(qs.answeredQuestions(3).answer, '40'); % answer remains if we need to reuse going forward
            testCase.verifyEqual(qs.nextAnswerIndex,3);  % this should have stepped back one
            
            % step backward
            testCase.verifyTrue(qs.canBackUp());
            prevAnswer = qs.backUp();
            testCase.verifyEqual(prevAnswer.questionID,'COLOR');
            testCase.verifyEqual(prevAnswer.answer,'red');
            question2 = qs.getCurrentQuestion();
            testCase.verifyEqual(question2.id,'COLOR');
            
            testCase.verifyTrue(length(qs.answeredQuestions) == 3);
            testCase.verifyEqual(qs.answeredQuestions(1).questionID, 'BREADBOX');
            testCase.verifyEqual(qs.answeredQuestions(2).questionID, 'COLOR');
            testCase.verifyEqual(qs.answeredQuestions(3).questionID, 'COVER_PERCENT');
            testCase.verifyEqual(qs.answeredQuestions(1).answer, 'yes');
            testCase.verifyEqual(qs.answeredQuestions(2).answer, 'red');
            testCase.verifyEqual(qs.answeredQuestions(3).answer, '40'); 
            testCase.verifyEqual(qs.nextAnswerIndex,2);  % this should have stepped back one
            
            testCase.verifyTrue(qs.canBackUp());
            % move forward the same path, ensure prior set answers are
            % right
            
            existingAnswerToNextQuestion = qs.answerQuestion('red');
            testCase.verifyEqual(existingAnswerToNextQuestion,'40');
            testCase.verifyEqual(qs.nextAnswerIndex,3);
            testCase.verifyFalse(qs.isAllQuestionsAnswered());
            question3 = qs.getCurrentQuestion();
            testCase.verifyEqual(question3.id,'COVER_PERCENT');
            
            existingAnswerToNextQuestion = qs.answerQuestion('40');
            testCase.verifyEqual(existingAnswerToNextQuestion,'NOT_YET_SPECIFIED');
            testCase.verifyEqual(qs.nextAnswerIndex,4);
            testCase.verifyTrue(qs.isAllQuestionsAnswered());
            
            testCase.verifyTrue(length(qs.answeredQuestions) == 3);
            testCase.verifyEqual(qs.answeredQuestions(1).questionID, 'BREADBOX');
            testCase.verifyEqual(qs.answeredQuestions(2).questionID, 'COLOR');
            testCase.verifyEqual(qs.answeredQuestions(3).questionID, 'COVER_PERCENT');
            testCase.verifyEqual(qs.answeredQuestions(1).answer, 'yes');
            testCase.verifyEqual(qs.answeredQuestions(2).answer, 'red');
            testCase.verifyEqual(qs.answeredQuestions(3).answer, '40');
            testCase.verifyTrue(qs.isAllQuestionsAnswered());
        end
        
        function testQuestionSequencerCASE_Keep5(testCase)
            % CASE_KEEP5 forward through last question, back up all the way, then forward
            xmlFile = QuestionsXMLFile('tests/simple.xml');
            qquestions = QQuestions(xmlFile.domNode);
            qs = QuestionSequencer(qquestions);
            
            existingAnswerToNextQuestion = qs.answerQuestion('yes');
            existingAnswerToNextQuestion = qs.answerQuestion('red');
            existingAnswerToNextQuestion = qs.answerQuestion('40');
            testCase.verifyEqual(existingAnswerToNextQuestion,'NOT_YET_SPECIFIED');
            testCase.verifyTrue(qs.isAllQuestionsAnswered());
            
            % step backward 
            testCase.verifyTrue(qs.canBackUp());
            prevAnswer = qs.backUp();
            testCase.verifyEqual(prevAnswer.questionID,'COVER_PERCENT');
            testCase.verifyEqual(prevAnswer.answer,'40');
            question3 = qs.getCurrentQuestion();
            testCase.verifyEqual(question3.id,'COVER_PERCENT');
            
            testCase.verifyTrue(length(qs.answeredQuestions) == 3);
            testCase.verifyEqual(qs.answeredQuestions(1).questionID, 'BREADBOX');
            testCase.verifyEqual(qs.answeredQuestions(2).questionID, 'COLOR');
            testCase.verifyEqual(qs.answeredQuestions(3).questionID, 'COVER_PERCENT');
            testCase.verifyEqual(qs.answeredQuestions(1).answer, 'yes');
            testCase.verifyEqual(qs.answeredQuestions(2).answer, 'red');
            testCase.verifyEqual(qs.answeredQuestions(3).answer, '40'); % answer remains if we need to reuse going forward
            testCase.verifyEqual(qs.nextAnswerIndex,3);  % this should have stepped back one
            
            % step backward
            testCase.verifyTrue(qs.canBackUp());
            prevAnswer = qs.backUp();
            testCase.verifyEqual(prevAnswer.questionID,'COLOR');
            testCase.verifyEqual(prevAnswer.answer,'red');
            question2 = qs.getCurrentQuestion();
            testCase.verifyEqual(question2.id,'COLOR');
            
            testCase.verifyTrue(length(qs.answeredQuestions) == 3);
            testCase.verifyEqual(qs.answeredQuestions(1).questionID, 'BREADBOX');
            testCase.verifyEqual(qs.answeredQuestions(2).questionID, 'COLOR');
            testCase.verifyEqual(qs.answeredQuestions(3).questionID, 'COVER_PERCENT');
            testCase.verifyEqual(qs.answeredQuestions(1).answer, 'yes');
            testCase.verifyEqual(qs.answeredQuestions(2).answer, 'red');
            testCase.verifyEqual(qs.answeredQuestions(3).answer, '40'); 
            testCase.verifyEqual(qs.nextAnswerIndex,2);  % this should have stepped back one
            
            % step backward
            testCase.verifyTrue(qs.canBackUp());
            prevAnswer = qs.backUp();
            testCase.verifyEqual(prevAnswer.questionID,'BREADBOX');
            testCase.verifyEqual(prevAnswer.answer,'yes');
            question1 = qs.getCurrentQuestion();
            testCase.verifyEqual(question1.id,'BREADBOX');
            
            testCase.verifyTrue(length(qs.answeredQuestions) == 3);
            testCase.verifyEqual(qs.answeredQuestions(1).questionID, 'BREADBOX');
            testCase.verifyEqual(qs.answeredQuestions(2).questionID, 'COLOR');
            testCase.verifyEqual(qs.answeredQuestions(3).questionID, 'COVER_PERCENT');
            testCase.verifyEqual(qs.answeredQuestions(1).answer, 'yes');
            testCase.verifyEqual(qs.answeredQuestions(2).answer, 'red');
            testCase.verifyEqual(qs.answeredQuestions(3).answer, '40'); 
            testCase.verifyEqual(qs.nextAnswerIndex,1);  % this should have stepped back one
            
            testCase.verifyFalse(qs.canBackUp());
            % move forward the same path, ensure prior set answers are
            % right
            
            existingAnswerToNextQuestion = qs.answerQuestion('yes');
            testCase.verifyEqual(existingAnswerToNextQuestion,'red');
            testCase.verifyEqual(qs.nextAnswerIndex,2);
            testCase.verifyFalse(qs.isAllQuestionsAnswered());
            question2 = qs.getCurrentQuestion();
            testCase.verifyEqual(question2.id,'COLOR');
            
            existingAnswerToNextQuestion = qs.answerQuestion('red');
            testCase.verifyEqual(existingAnswerToNextQuestion,'40');
            testCase.verifyEqual(qs.nextAnswerIndex,3);
            testCase.verifyFalse(qs.isAllQuestionsAnswered());
            question3 = qs.getCurrentQuestion();
            testCase.verifyEqual(question3.id,'COVER_PERCENT');
            
            existingAnswerToNextQuestion = qs.answerQuestion('40');
            testCase.verifyEqual(existingAnswerToNextQuestion,'NOT_YET_SPECIFIED');
            testCase.verifyEqual(qs.nextAnswerIndex,4);
            testCase.verifyTrue(qs.isAllQuestionsAnswered());
            
            testCase.verifyTrue(length(qs.answeredQuestions) == 3);
            testCase.verifyEqual(qs.answeredQuestions(1).questionID, 'BREADBOX');
            testCase.verifyEqual(qs.answeredQuestions(2).questionID, 'COLOR');
            testCase.verifyEqual(qs.answeredQuestions(3).questionID, 'COVER_PERCENT');
            testCase.verifyEqual(qs.answeredQuestions(1).answer, 'yes');
            testCase.verifyEqual(qs.answeredQuestions(2).answer, 'red');
            testCase.verifyEqual(qs.answeredQuestions(3).answer, '40');
        end
        
        
        
        function testQuestionSequencerCASE_Change1(testCase)
            % CASE_CHANGE1 forward one question, back up, then forward,
            % change answer
            xmlFile = QuestionsXMLFile('tests/simple.xml');
            qquestions = QQuestions(xmlFile.domNode);
            qs = QuestionSequencer(qquestions);
            
            existingAnswerToNextQuestion = qs.answerQuestion('yes');
            testCase.verifyEqual(existingAnswerToNextQuestion,'NOT_YET_SPECIFIED');
            testCase.verifyFalse(qs.isAllQuestionsAnswered());
            
            % step backward 
            testCase.verifyTrue(qs.canBackUp());
            prevAnswer = qs.backUp();
            testCase.verifyEqual(prevAnswer.questionID,'BREADBOX');
            testCase.verifyEqual(prevAnswer.answer,'yes');
            question2 = qs.getCurrentQuestion();
            testCase.verifyEqual(question2.id,'BREADBOX');
            
            testCase.verifyTrue(length(qs.answeredQuestions) == 1);
            testCase.verifyEqual(qs.answeredQuestions(1).questionID, 'BREADBOX');
            testCase.verifyEqual(qs.answeredQuestions(1).answer, 'yes');
            testCase.verifyEqual(qs.nextAnswerIndex,1);  % this should have stepped back one
            
            testCase.verifyFalse(qs.canBackUp());
            % move forward a different path, ensure prior set answers are
            % flushed
            
            existingAnswerToNextQuestion = qs.answerQuestion('no');
            testCase.verifyEqual(existingAnswerToNextQuestion,'NOT_YET_SPECIFIED');
            testCase.verifyEqual(qs.nextAnswerIndex,2);
            testCase.verifyFalse(qs.isAllQuestionsAnswered());
            question2 = qs.getCurrentQuestion();
            testCase.verifyEqual(question2.id,'WEIGHT');
            testCase.verifyTrue(length(qs.answeredQuestions) == 1);
            
            testCase.verifyEqual(qs.answeredQuestions(1).questionID, 'BREADBOX');
            testCase.verifyEqual(qs.answeredQuestions(1).answer, 'no');
        end
        
        function testQuestionSequencerCASE_Change2(testCase)
            % CASE_CHANGE2 forward two questions, back up one, then forward,
            % change answer
            xmlFile = QuestionsXMLFile('tests/simple.xml');
            qquestions = QQuestions(xmlFile.domNode);
            qs = QuestionSequencer(qquestions);
            
            existingAnswerToNextQuestion = qs.answerQuestion('yes');
            existingAnswerToNextQuestion = qs.answerQuestion('red');
            testCase.verifyEqual(existingAnswerToNextQuestion,'NOT_YET_SPECIFIED');
            testCase.verifyFalse(qs.isAllQuestionsAnswered());
            
            % step backward 
            testCase.verifyTrue(qs.canBackUp());
            prevAnswer = qs.backUp();
            testCase.verifyEqual(prevAnswer.questionID,'COLOR');
            testCase.verifyEqual(prevAnswer.answer,'red');
            question2 = qs.getCurrentQuestion();
            testCase.verifyEqual(question2.id,'COLOR');
            
            testCase.verifyTrue(length(qs.answeredQuestions) == 2);
            testCase.verifyEqual(qs.answeredQuestions(1).questionID, 'BREADBOX');
            testCase.verifyEqual(qs.answeredQuestions(2).questionID, 'COLOR');
            testCase.verifyEqual(qs.answeredQuestions(1).answer, 'yes');
            testCase.verifyEqual(qs.answeredQuestions(2).answer, 'red');
            testCase.verifyEqual(qs.nextAnswerIndex,2);  % this should have stepped back one
            
            testCase.verifyTrue(qs.canBackUp());
            % move forward a different path, ensure prior set answers are
            % flushed
            
            existingAnswerToNextQuestion = qs.answerQuestion('blue');
            testCase.verifyEqual(existingAnswerToNextQuestion,'NOT_YET_SPECIFIED');
            testCase.verifyEqual(qs.nextAnswerIndex,3);
            testCase.verifyFalse(qs.isAllQuestionsAnswered());
            question3 = qs.getCurrentQuestion();
            testCase.verifyEqual(question3.id,'RESOLUTION');
            testCase.verifyTrue(length(qs.answeredQuestions) == 2);
            testCase.verifyEqual(qs.answeredQuestions(1).questionID, 'BREADBOX');
            testCase.verifyEqual(qs.answeredQuestions(2).questionID, 'COLOR');
            testCase.verifyEqual(qs.answeredQuestions(1).answer, 'yes');
            testCase.verifyEqual(qs.answeredQuestions(2).answer, 'blue');
        end
        
        function testQuestionSequencerCASE_Change3(testCase)
            % CASE_CHANGE3 forward through last question, back up one, then forward,
            % change answer
            xmlFile = QuestionsXMLFile('tests/simple.xml');
            qquestions = QQuestions(xmlFile.domNode);
            qs = QuestionSequencer(qquestions);
            
            existingAnswerToNextQuestion = qs.answerQuestion('yes');
            existingAnswerToNextQuestion = qs.answerQuestion('red');
            existingAnswerToNextQuestion = qs.answerQuestion('40');
            testCase.verifyEqual(existingAnswerToNextQuestion,'NOT_YET_SPECIFIED');
            testCase.verifyTrue(qs.isAllQuestionsAnswered());
            
            % step backward 
            testCase.verifyTrue(qs.canBackUp());
            prevAnswer = qs.backUp();
            testCase.verifyEqual(prevAnswer.questionID,'COVER_PERCENT');
            testCase.verifyEqual(prevAnswer.answer,'40');
            question3 = qs.getCurrentQuestion();
            testCase.verifyEqual(question3.id,'COVER_PERCENT');
            
            testCase.verifyTrue(length(qs.answeredQuestions) == 3);
            testCase.verifyEqual(qs.answeredQuestions(1).questionID, 'BREADBOX');
            testCase.verifyEqual(qs.answeredQuestions(2).questionID, 'COLOR');
            testCase.verifyEqual(qs.answeredQuestions(3).questionID, 'COVER_PERCENT');
            testCase.verifyEqual(qs.answeredQuestions(1).answer, 'yes');
            testCase.verifyEqual(qs.answeredQuestions(2).answer, 'red');
            testCase.verifyEqual(qs.answeredQuestions(3).answer, '40'); % answer remains if we need to reuse going forward
            testCase.verifyEqual(qs.nextAnswerIndex,3);  % this should have stepped back one
            
            testCase.verifyTrue(qs.canBackUp());
            % move forward a different path, ensure prior set answers are
            % flushed
            
            existingAnswerToNextQuestion = qs.answerQuestion('50');
            testCase.verifyEqual(existingAnswerToNextQuestion,'NOT_YET_SPECIFIED');
            testCase.verifyEqual(qs.nextAnswerIndex,4);
            testCase.verifyTrue(qs.isAllQuestionsAnswered());
            question4 = qs.getCurrentQuestion();
            testCase.verifyEqual(question4.id,'NO_MORE_QUESTIONS');
            testCase.verifyTrue(length(qs.answeredQuestions) == 3);
            
            testCase.verifyEqual(qs.answeredQuestions(1).questionID, 'BREADBOX');
            testCase.verifyEqual(qs.answeredQuestions(2).questionID, 'COLOR');
            testCase.verifyEqual(qs.answeredQuestions(3).questionID, 'COVER_PERCENT');
            testCase.verifyEqual(qs.answeredQuestions(1).answer, 'yes');
            testCase.verifyEqual(qs.answeredQuestions(2).answer, 'red');
            testCase.verifyEqual(qs.answeredQuestions(3).answer, '50');
        end
        
        function testQuestionSequencerCASE_Change4(testCase)
            % CASE_CHANGE4 forward through last question, back up two, then forward,
            % change answer
            xmlFile = QuestionsXMLFile('tests/simple.xml');
            qquestions = QQuestions(xmlFile.domNode);
            qs = QuestionSequencer(qquestions);
            
            existingAnswerToNextQuestion = qs.answerQuestion('yes');
            existingAnswerToNextQuestion = qs.answerQuestion('red');
            existingAnswerToNextQuestion = qs.answerQuestion('40');
            testCase.verifyEqual(existingAnswerToNextQuestion,'NOT_YET_SPECIFIED');
            testCase.verifyTrue(qs.isAllQuestionsAnswered());
            
            % step backward 
            testCase.verifyTrue(qs.canBackUp());
            prevAnswer = qs.backUp();
            testCase.verifyEqual(prevAnswer.questionID,'COVER_PERCENT');
            testCase.verifyEqual(prevAnswer.answer,'40');
            question3 = qs.getCurrentQuestion();
            testCase.verifyEqual(question3.id,'COVER_PERCENT');
            
            testCase.verifyTrue(length(qs.answeredQuestions) == 3);
            testCase.verifyEqual(qs.answeredQuestions(1).questionID, 'BREADBOX');
            testCase.verifyEqual(qs.answeredQuestions(2).questionID, 'COLOR');
            testCase.verifyEqual(qs.answeredQuestions(3).questionID, 'COVER_PERCENT');
            testCase.verifyEqual(qs.answeredQuestions(1).answer, 'yes');
            testCase.verifyEqual(qs.answeredQuestions(2).answer, 'red');
            testCase.verifyEqual(qs.answeredQuestions(3).answer, '40'); % answer remains if we need to reuse going forward
            testCase.verifyEqual(qs.nextAnswerIndex,3);  % this should have stepped back one
            
            % step backward
            testCase.verifyTrue(qs.canBackUp());
            prevAnswer = qs.backUp();
            testCase.verifyEqual(prevAnswer.questionID,'COLOR');
            testCase.verifyEqual(prevAnswer.answer,'red');
            question2 = qs.getCurrentQuestion();
            testCase.verifyEqual(question2.id,'COLOR');
            
            testCase.verifyTrue(length(qs.answeredQuestions) == 3);
            testCase.verifyEqual(qs.answeredQuestions(1).questionID, 'BREADBOX');
            testCase.verifyEqual(qs.answeredQuestions(2).questionID, 'COLOR');
            testCase.verifyEqual(qs.answeredQuestions(3).questionID, 'COVER_PERCENT');
            testCase.verifyEqual(qs.answeredQuestions(1).answer, 'yes');
            testCase.verifyEqual(qs.answeredQuestions(2).answer, 'red');
            testCase.verifyEqual(qs.answeredQuestions(3).answer, '40'); 
            testCase.verifyEqual(qs.nextAnswerIndex,2);  % this should have stepped back one
            
            
            testCase.verifyTrue(qs.canBackUp());
            % move forward a different path, ensure prior set answers are
            % flushed
            
            existingAnswerToNextQuestion = qs.answerQuestion('blue');
            testCase.verifyEqual(existingAnswerToNextQuestion,'NOT_YET_SPECIFIED');
            testCase.verifyEqual(qs.nextAnswerIndex,3);
            testCase.verifyFalse(qs.isAllQuestionsAnswered());
            question3 = qs.getCurrentQuestion();
            testCase.verifyEqual(question3.id,'RESOLUTION');
            testCase.verifyTrue(length(qs.answeredQuestions) == 2);
            
            testCase.verifyEqual(qs.answeredQuestions(1).questionID, 'BREADBOX');
            testCase.verifyEqual(qs.answeredQuestions(2).questionID, 'COLOR');
            testCase.verifyEqual(qs.answeredQuestions(1).answer, 'yes');
            testCase.verifyEqual(qs.answeredQuestions(2).answer, 'blue');
        end
        
        function testQuestionSequencerCASE_Change5(testCase)
            % CASE_CHANGE5 forward through last question, back up all the way, then forward,
            % change answer
            xmlFile = QuestionsXMLFile('tests/simple.xml');
            qquestions = QQuestions(xmlFile.domNode);
            qs = QuestionSequencer(qquestions);
            
            existingAnswerToNextQuestion = qs.answerQuestion('yes');
            existingAnswerToNextQuestion = qs.answerQuestion('red');
            existingAnswerToNextQuestion = qs.answerQuestion('40');
            testCase.verifyEqual(existingAnswerToNextQuestion,'NOT_YET_SPECIFIED');
            testCase.verifyTrue(qs.isAllQuestionsAnswered());
            
            % step backward 
            testCase.verifyTrue(qs.canBackUp());
            prevAnswer = qs.backUp();
            testCase.verifyEqual(prevAnswer.questionID,'COVER_PERCENT');
            testCase.verifyEqual(prevAnswer.answer,'40');
            question3 = qs.getCurrentQuestion();
            testCase.verifyEqual(question3.id,'COVER_PERCENT');
            
            testCase.verifyTrue(length(qs.answeredQuestions) == 3);
            testCase.verifyEqual(qs.answeredQuestions(1).questionID, 'BREADBOX');
            testCase.verifyEqual(qs.answeredQuestions(2).questionID, 'COLOR');
            testCase.verifyEqual(qs.answeredQuestions(3).questionID, 'COVER_PERCENT');
            testCase.verifyEqual(qs.answeredQuestions(1).answer, 'yes');
            testCase.verifyEqual(qs.answeredQuestions(2).answer, 'red');
            testCase.verifyEqual(qs.answeredQuestions(3).answer, '40'); % answer remains if we need to reuse going forward
            testCase.verifyEqual(qs.nextAnswerIndex,3);  % this should have stepped back one
            
            % step backward
            testCase.verifyTrue(qs.canBackUp());
            prevAnswer = qs.backUp();
            testCase.verifyEqual(prevAnswer.questionID,'COLOR');
            testCase.verifyEqual(prevAnswer.answer,'red');
            question2 = qs.getCurrentQuestion();
            testCase.verifyEqual(question2.id,'COLOR');
            
            testCase.verifyTrue(length(qs.answeredQuestions) == 3);
            testCase.verifyEqual(qs.answeredQuestions(1).questionID, 'BREADBOX');
            testCase.verifyEqual(qs.answeredQuestions(2).questionID, 'COLOR');
            testCase.verifyEqual(qs.answeredQuestions(3).questionID, 'COVER_PERCENT');
            testCase.verifyEqual(qs.answeredQuestions(1).answer, 'yes');
            testCase.verifyEqual(qs.answeredQuestions(2).answer, 'red');
            testCase.verifyEqual(qs.answeredQuestions(3).answer, '40'); 
            testCase.verifyEqual(qs.nextAnswerIndex,2);  % this should have stepped back one
            
            % step backward
            testCase.verifyTrue(qs.canBackUp());
            prevAnswer = qs.backUp();
            testCase.verifyEqual(prevAnswer.questionID,'BREADBOX');
            testCase.verifyEqual(prevAnswer.answer,'yes');
            question1 = qs.getCurrentQuestion();
            testCase.verifyEqual(question1.id,'BREADBOX');
            
            testCase.verifyTrue(length(qs.answeredQuestions) == 3);
            testCase.verifyEqual(qs.answeredQuestions(1).questionID, 'BREADBOX');
            testCase.verifyEqual(qs.answeredQuestions(2).questionID, 'COLOR');
            testCase.verifyEqual(qs.answeredQuestions(3).questionID, 'COVER_PERCENT');
            testCase.verifyEqual(qs.answeredQuestions(1).answer, 'yes');
            testCase.verifyEqual(qs.answeredQuestions(2).answer, 'red');
            testCase.verifyEqual(qs.answeredQuestions(3).answer, '40'); 
            testCase.verifyEqual(qs.nextAnswerIndex,1);  % this should have stepped back one
            
            testCase.verifyFalse(qs.canBackUp());
            % move forward a different path, ensure prior set answers are
            % flushed
            
            existingAnswerToNextQuestion = qs.answerQuestion('no');
            testCase.verifyEqual(existingAnswerToNextQuestion,'NOT_YET_SPECIFIED');
            testCase.verifyEqual(qs.nextAnswerIndex,2);
            testCase.verifyFalse(qs.isAllQuestionsAnswered());
            question2 = qs.getCurrentQuestion();
            testCase.verifyEqual(question2.id,'WEIGHT');
            testCase.verifyTrue(length(qs.answeredQuestions) == 1);
            
            testCase.verifyEqual(qs.answeredQuestions(1).questionID, 'BREADBOX');
            testCase.verifyEqual(qs.answeredQuestions(1).answer, 'no');
        end
        
        
        
        
        function testEmptyQuestionList(testCase)
            xmlFile = QuestionsXMLFile('tests/simple.xml');
            qquestions = QQuestions(xmlFile.domNode);
        end
        
        function testSingleQuestionList(testCase)
            %not yet implemented
        end
 
        function testQuestionsXmlFile(testCase)
            xmlFile = QuestionsXMLFile('tests/simple.xml');
            qquestions = QQuestions(xmlFile.domNode);
            theQuestions = qquestions.questions;
            question1 = theQuestions(1);
            testCase.verifyEqual(question1.type,'choice');
            testCase.verifyEqual(question1.id,'BREADBOX');
            testCase.verifyEqual(question1.text,'Is it bigger than a breadbox or is it approximately the same size? (Use your own judgement when deciding on the approximate size.  Everything is relative, you know.');
            testCase.verifyEqual(question1.answers(1).value,'yes');
            testCase.verifyEqual(question1.answers(1).nextQuestion,'COLOR');
            testCase.verifyEqual(question1.answers(2).value,'no');
            testCase.verifyEqual(question1.answers(2).nextQuestion,'WEIGHT');
            testCase.verifyEqual(question1.images(1).imageFilePath,'data/questionnaire/images/elephant.jpg');
            testCase.verifyEqual(question1.images(1).imageCaption,'elephants are bigger than a breadbox');
            testCase.verifyEqual(question1.images(2).imageFilePath,'data/questionnaire/images/mouse.jpg');
            testCase.verifyEqual(question1.images(2).imageCaption,'mice are smaller than a breadbox');
            
            question2 = theQuestions(2);
            testCase.verifyEqual(question2.type,'choice');
            testCase.verifyEqual(question2.id,'COLOR');
            testCase.verifyEqual(question2.text,'What color is the larger-than-breadbox item?');
            testCase.verifyEqual(question2.answers(1).value,'red');
            testCase.verifyEqual(question2.answers(1).nextQuestion,'COVER_PERCENT');
            testCase.verifyEqual(question2.answers(2).value,'green');
            testCase.verifyEqual(question2.answers(2).nextQuestion,'COVER_PERCENT');
            testCase.verifyEqual(question2.answers(3).value,'blue');
            testCase.verifyEqual(question2.answers(3).nextQuestion,'RESOLUTION');
             
            question3 = theQuestions(3);
            testCase.verifyEqual(question3.type,'input_integer');
            testCase.verifyEqual(question3.id,'WEIGHT');
            testCase.verifyEqual(question3.text,'What is the weight in kilograms of the smaller-than-breadbox item?');
            testCase.verifyEqual(question3.answers(1).value,'0');
            testCase.verifyEqual(question3.answers(1).nextQuestion,'COVER_PERCENT');
            
            question4 = theQuestions(4);
            testCase.verifyEqual(question4.type,'input_string');
            testCase.verifyEqual(question4.id,'RESOLUTION');
            testCase.verifyEqual(question4.text,'What is the resolution of the smaller-than-breadbox item?');
            testCase.verifyEqual(question4.answers(1).value,'');
            testCase.verifyEqual(question4.answers(1).nextQuestion,'NO_MORE_QUESTIONS');
            
            question5 = theQuestions(5);
            testCase.verifyEqual(question5.type,'input_integer');
            testCase.verifyEqual(question5.id,'COVER_PERCENT');
            testCase.verifyEqual(question5.text,'What percent of image is occupied?');
            testCase.verifyEqual(question5.answers(1).value,'0');
            testCase.verifyEqual(question5.answers(1).nextQuestion,'NO_MORE_QUESTIONS');
    
        end
    end
end
